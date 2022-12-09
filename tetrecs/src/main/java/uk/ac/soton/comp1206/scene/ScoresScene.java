package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import uk.ac.soton.comp1206.audio.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.ArrayList;

public class ScoresScene extends BaseScene{
    /**
     * Handles audio
     */
    private Multimedia media = new Multimedia();
    /**
     * VB ox that stores the components in the scene
     */
    private VBox scoreBox;
    /**
     * Stores the High Scores text
     */
    private Text hiscoreText;
    /**
     * Stores the local scores
     */
    private ArrayList<String> array = new ArrayList<String>();
    /**
     * Stores the online Scores
     */
    private ArrayList<String> remoteScores;
    /**
     * Checks if music is on
     */
    private boolean isMusicOn = false;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoresScene(GameWindow gameWindow, ArrayList<String> array) {
        super(gameWindow);
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

        //Makes the ScoreScene pretty
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
        this.hiscoreText = new Text("High Scores");
        this.hiscoreText.setTextAlignment(TextAlignment.CENTER);
        VBox.setVgrow(this.hiscoreText, Priority.ALWAYS);
        this.hiscoreText.getStyleClass().add("title");
        this.hiscoreText.setFill(Color.YELLOW);
        this.scoreBox.getChildren().add(this.hiscoreText);

        try {
            this.saveScores();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //ScoreBoxes with local and online scores
        HBox hBox = new HBox();
        String str1 = "Local Scores:";
        ScoresList sc1 = new ScoresList(array, str1);
        hBox.getChildren().add(sc1);
        Region blank1 = new Region();
        hBox.setHgrow(blank1, Priority.ALWAYS);
        hBox.getChildren().add(blank1);
        String str2 = "Onlince Scores:";
        ScoresList sc2 = new ScoresList(remoteScores, str2);
        hBox.getChildren().add(sc2);
        scoreBox.getChildren().add(hBox);
    }

    /**
     * Gets scores from file into array
     * @throws Exception
     */
    public void saveScores() throws Exception{
        File file = new File("scores.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        for (int i=0; i<10; i++) {
            line = br.readLine();
            array.add(line);
        }
        br.close();
    }
}
