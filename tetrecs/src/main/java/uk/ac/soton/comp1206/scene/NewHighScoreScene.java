package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import uk.ac.soton.comp1206.audio.Multimedia;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.ArrayList;

public class NewHighScoreScene extends BaseScene {
    /**
     * Game being played
     */
    private Game games;
    /**
     * VBox holding the components of the scene
     */
    private VBox scoreBox;
    /**
     * Media to handle audio
     */
    private Multimedia media = new Multimedia();
    /**
     * Makes sure the users score is introduced once in the array of scores
     */
    private boolean scoreIntroduced = false;
    /**
     * Stores local scores
     */
    private ArrayList<String> array = new ArrayList<String>();
    /**
     * Stores online scores
     */
    private ArrayList<String> remoteScores;
    /**
     * Boolean that checks if current score beat any online scores
     */
    private boolean beatOnlineScore = false;
    /**
     * Checks if music is on
     */
    private boolean isMusicOn = false;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public NewHighScoreScene(GameWindow gameWindow, Game game, ArrayList<String> array) {
        super(gameWindow);
        this.games = game;
        remoteScores = array;
    }

    @Override
    public void initialise() {
        getScene().setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
                if(isMusicOn == true){
                    this.media.getMusicPlayer().pause();
                }
                media.playAudio("transition.wav");
            }
        });
    }

    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //BackgroundMusic
        if(SettingsScene.audioEnabled.get() ==  true){
            media.playMusic("menu.mp3");
            isMusicOn = true;
        }

        //Makes scene pretty
        scoreBox = new VBox();
        scoreBox.setAlignment(Pos.TOP_CENTER);
        scoreBox.setPadding(new Insets(5, 5, 5, 5));
        scoreBox.setSpacing(20);
        mainPane.setCenter(this.scoreBox);
        ImageView image = new ImageView(this.getClass().getResource("/images/TetrECS.png").toExternalForm());
        image.setFitWidth((double)this.gameWindow.getWidth() * 0.6666666666666666);
        image.setPreserveRatio(true);
        this.scoreBox.getChildren().add(image);
        Text gameOverText = new Text("Game Over");
        gameOverText.setTextAlignment(TextAlignment.CENTER);
        VBox.setVgrow(gameOverText, Priority.ALWAYS);
        gameOverText.getStyleClass().add("bigTitleAlternative");
        this.scoreBox.getChildren().add(gameOverText);

        //Name entering methods: label, button, and sendOnButtonPressed
        var text = new TextField();
        text.setPromptText("Enter your name");
        text.setPrefWidth(gameWindow.getWidth());

        //On Enter pressed
        text.setOnKeyPressed((e) -> {
            if (e.getCode() != KeyCode.ENTER) return;
            try {
                saveScore(text.getText());
                updateScoreList();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            checkOnlineScore();
            if(beatOnlineScore == true){
                String stri = "HISCORE " + text.getText() + ":" + games.getScore();
                System.out.println(stri);
                gameWindow.getCommunicator().send(stri);
            }
            gameWindow.getCommunicator().addListener(this::loadOnlineScores);
            gameWindow.getCommunicator().send("HISCORES DEFAULT");
            text.clear();
            text.requestFocus();
            if(isMusicOn == true){
                this.media.getMusicPlayer().pause();
            }
            gameWindow.startScores(remoteScores);
        });
        scoreBox.getChildren().add(text);

        //On Button Pressed
        var button = new Button("Submit");
        button.setOnAction((e) -> {
            try {
                saveScore(text.getText());
                updateScoreList();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            checkOnlineScore();
            if(beatOnlineScore == true){
                String stri = "HISCORE " + text.getText() + ":" + games.getScore();
                System.out.println(stri);
                gameWindow.getCommunicator().send(stri);
            }
            gameWindow.getCommunicator().addListener(this::loadOnlineScores);
            gameWindow.getCommunicator().send("HISCORES DEFAULT");
            text.clear();
            text.requestFocus();
            if(isMusicOn == true){
                this.media.getMusicPlayer().pause();
            }
            gameWindow.startScores(remoteScores);
        });
        scoreBox.getChildren().add(button);

        var hiscoreText = new Text("You got a High Score");
        hiscoreText.setTextAlignment(TextAlignment.CENTER);
        VBox.setVgrow(hiscoreText, Priority.ALWAYS);
        hiscoreText.getStyleClass().add("headingAlternative");
        scoreBox.getChildren().add(hiscoreText);
    }

    /**
     * Save current score into array at the right location
     * @param name name under which the score will be saved
     * @throws Exception
     */
    public void saveScore(String name) throws Exception{
        File file = new File("scores.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        for (int i=0; i<10; i++) {
            line = br.readLine();
            String[] splitScore = line.split(":");
            int score = Integer.parseInt(splitScore[1]);
            if ((score < games.getScore()) && (scoreIntroduced == false)) {
                scoreIntroduced = true;
                String currentScore;
                currentScore = name + ":" + games.getScore();
                array.add(currentScore);
            }
            array.add(line);
        }
        br.close();
    }

    /**
     * Writes updated scoreList into file
     * @throws Exception
     */
    public void updateScoreList() throws Exception{
        File f = new File("scores.txt");
        Writer fw = new FileWriter(f);
        for (int i=0; i<10; i++){
            fw.write(array.get(i));
            fw.write("\r\n");
        }
        fw.close();
    }

    /**
     * Checks if online score is beat
     */
    public void checkOnlineScore(){
        for (int i=0; i<10; i++){
            String[] score = remoteScores.get(i).split(":");
            if(games.getScore() > Integer.valueOf(score[1])){
                beatOnlineScore = true;
            }
        }
    }

    /**
     * Loads online scores, and splits them into readable format
     * @param s string that will be split into correct format
     */
    public void loadOnlineScores(String s) {
        remoteScores.clear();
        String[] online = s.split(" ");
        String scores = online[1];
        String[] split = scores.split("\n");
        for (int i = 0; i < 10; i++) {
            String line = split[i];
            remoteScores.add(line);
        }
    }
}
