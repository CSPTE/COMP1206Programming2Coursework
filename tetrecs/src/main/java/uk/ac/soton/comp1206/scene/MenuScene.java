package uk.ac.soton.comp1206.scene;

import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.audio.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Media that handles audio
     */
    private Multimedia media = new Multimedia();
    /**
     * Checks if music is on
     */
    private boolean isMusicOn = false;
    /**
     * Contains the number of games played
     */
    public static int gamesPlayed;

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        var titleImage = new ImageView(new Image(getClass().getResource("/images/TetrECS.png").toExternalForm()));
        titleImage.setFitWidth(gameWindow.getHeight());
        titleImage.setPreserveRatio(true);
        mainPane.setCenter(titleImage);
        RotateTransition rotater = new RotateTransition(new Duration(3000), titleImage);
        rotater.setCycleCount(-1);
        rotater.setFromAngle(-5);
        rotater.setToAngle(5);
        rotater.setAutoReverse(true);
        rotater.play();

        //Gets local score from file
        try {
            fileHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Gets number of games played
        try {
            gamesPlayedFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Labels
        var menuBox = new VBox();
        menuBox.setAlignment(Pos.CENTER);
        Label singlePlayerText = new Label("Single Player");
        singlePlayerText.getStyleClass().add("menuItem");
        menuBox.getChildren().add(singlePlayerText);
        singlePlayerText.setOnMouseClicked((e) -> {
            this.startGame();
        });
        Label multiPlayerText = new Label("Multi Player");
        multiPlayerText.getStyleClass().add("menuItem");
        menuBox.getChildren().add(multiPlayerText);
        multiPlayerText.setOnMouseClicked((e) -> {
            //TODO: Load Multi Player
        });
        Label howToPlayText = new Label("How to Play");
        howToPlayText.getStyleClass().add("menuItem");
        menuBox.getChildren().add(howToPlayText);
        howToPlayText.setOnMouseClicked((e) -> {
            this.startInstructionScene();
        });
        Label settingsText = new Label("Settings and Statistics");
        settingsText.getStyleClass().add("menuItem");
        menuBox.getChildren().add(settingsText);
        settingsText.setOnMouseClicked((e) -> {
            this.startSettingsScene();
        });
        Label exitText = new Label("Exit");
        exitText.getStyleClass().add("menuItem");
        menuBox.getChildren().add(exitText);
        exitText.setOnMouseClicked((e) -> {
            App.getInstance().shutdown();
        });
        mainPane.setBottom(menuBox);

        //BackgroundMusic
        if(SettingsScene.audioEnabled.get() ==  true){
            media.playMusic("menu.mp3");
            isMusicOn = true;
        }
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        getScene().setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                App.getInstance().shutdown();
            }
        });
    }

    /**
     * Handle when the Start Game label is pressed
     */
    private void startGame() {
        gameWindow.startChallenge();
        if(isMusicOn == true){
            this.media.getMusicPlayer().pause();
        }
        if (SettingsScene.soundEffectsProperty().get() == true){
            media.playAudio("transition.wav");
        }
        //Updates number of games played
        gamesPlayed = gamesPlayed + 1;
        try {
            updateGamesPlayed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Handle when the Instructions label is pressed
     */
    private void startInstructionScene(){
        gameWindow.startInstructions();
        if(isMusicOn == true){
            this.media.getMusicPlayer().pause();
        }
        if (SettingsScene.soundEffectsProperty().get() == true){
            media.playAudio("transition.wav");
        }
    }
    private void startSettingsScene(){
        gameWindow.startSettings();
        if(isMusicOn == true){
            this.media.getMusicPlayer().pause();
        }
        if (SettingsScene.soundEffectsProperty().get() == true){
            media.playAudio("transition.wav");
        }
    }
    /**
     * Creates file with default sores if non existent
     */
    public void fileHandler() throws Exception {
        File f = new File("scores.txt");
        if (f.exists()){
        } else {
            f.createNewFile();
            Writer fw = new FileWriter(f);
            for (int i=10; i>0; i--){
                String nr = Integer.toString(i*1000);
                fw.write("Captain:" + nr);
                fw.write("\r\n");
            }
            fw.close();
        }
    }

    /**
     * Creates file with number of times played
     */
    public void gamesPlayedFile() throws Exception {
        File f = new File("gamesPlayed.txt");
        if (f.exists()){
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            String[] splitScore = line.split(":");
            int score = Integer.parseInt(splitScore[1]);
            gamesPlayed = score;
            br.close();
        } else {
            f.createNewFile();
            Writer fw = new FileWriter(f);
            fw.write("GamesPlayed:0");
            fw.close();
        }
    }

    /**
     * Writes the number of games played into the file
     */
    public void updateGamesPlayed() throws Exception {
        File f = new File("gamesPlayed.txt");
        Writer fw = new FileWriter(f);
        fw.write("GamesPlayed:" + gamesPlayed);
        fw.close();
    }
}
