package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import uk.ac.soton.comp1206.audio.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class SettingsScene extends BaseScene {
    /**
     * Handles audio
     */
    private Multimedia media = new Multimedia();
    /**
     * Stores whether music is enabled or not
     */
    public static SimpleBooleanProperty audioEnabled =  new SimpleBooleanProperty(true);
    /**
     * Stores whether sound effects is enabled or not
     */
    public static SimpleBooleanProperty soundEffectsEnabled =  new SimpleBooleanProperty(true);
    /**
     * Stores the number of games played
     */
    public static SimpleIntegerProperty gamesPlayedNumber =  new SimpleIntegerProperty();

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public SettingsScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {
        getScene().setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
                this.media.getMusicPlayer().pause();
                if (soundEffectsProperty().get() == true){
                    media.playAudio("transition.wav");
                }
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

        //BackGroundMusic
        media.playMusic("menu.mp3");

        //Make it pretty
        var settingsBox = new VBox();
        settingsBox.setAlignment(Pos.TOP_CENTER);
        settingsBox.setPadding(new Insets(5, 5, 5, 5));
        settingsBox.setSpacing(20);
        mainPane.setCenter(settingsBox);
        Text gameOverText = new Text("Settings & Statistics");
        gameOverText.setTextAlignment(TextAlignment.CENTER);
        VBox.setVgrow(gameOverText, Priority.ALWAYS);
        gameOverText.getStyleClass().add("bigTitleAlternative");
        settingsBox.getChildren().add(gameOverText);

        //Settings
        //Music
        HBox audioBox = new HBox();
        var audioEnabledText = new Text("Music Disabled");
        audioEnabledText.getStyleClass().add("headingAlternative");
        audioBox.getChildren().add(audioEnabledText);
        Region blank2 = new Region();
        audioBox.setHgrow(blank2, Priority.ALWAYS);
        audioBox.getChildren().add(blank2);
        CheckBox audioCB = new CheckBox();
        audioCB.setOnAction(e -> {
            if (audioCB.isSelected()) {
                setAudio(false);
                this.media.getMusicPlayer().pause();
            } else {
                setAudio(true);
                media.playMusic("menu.mp3");
            };
        });
        audioBox.getChildren().add(audioCB);
        settingsBox.getChildren().add(audioBox);
        //SoundEffects
        HBox effectsBox = new HBox();
        var soundEffectsEnabledText = new Text("Sound Effects Disabled");
        soundEffectsEnabledText.getStyleClass().add("headingAlternative");
        effectsBox.getChildren().add(soundEffectsEnabledText);
        Region blank3 = new Region();
        effectsBox.setHgrow(blank3, Priority.ALWAYS);
        effectsBox.getChildren().add(blank3);
        CheckBox effectsCB = new CheckBox();
        effectsCB.setOnAction(e -> {
            if (effectsCB.isSelected()) {
                setSoundEffects(false);
            } else {
                setSoundEffects(true);
            };
        });
        effectsBox.getChildren().add(effectsCB);
        settingsBox.getChildren().add(effectsBox);

        //Statistics
        //Games Played
        setGamesPlayedNumber(MenuScene.gamesPlayed);
        HBox gamesPlayedBox = new HBox();
        var gamesPlayedText = new Text("Games Played");
        gamesPlayedText.getStyleClass().add("headingAlternative");
        gamesPlayedBox.getChildren().add(gamesPlayedText);
        Region blank4 = new Region();
        gamesPlayedBox.setHgrow(blank4, Priority.ALWAYS);
        gamesPlayedBox.getChildren().add(blank4);
        var gamesPlayed = new Text();
        gamesPlayed.textProperty().bind(gamesPlayedNumber.asString());
        gamesPlayed.getStyleClass().add("headingAlternative");
        gamesPlayedBox.getChildren().add(gamesPlayed);
        settingsBox.getChildren().add(gamesPlayedBox);
    }

    /**
     * Method used to get the simple audio property
     * @return audioEnabled
     */
    public static SimpleBooleanProperty audioProperty() { return audioEnabled; }
    /**
     * Method used to set the audio property
     */
    public void setAudio(boolean x){ audioProperty().set(x); }

    /**
     * Method used to get the simple sound effects property
     * @return soundEffectsEnabled
     */
    public static SimpleBooleanProperty soundEffectsProperty() { return soundEffectsEnabled; }
    /**
     * Method used to set the sound effects property
     */
    public void setSoundEffects(boolean x){ soundEffectsProperty().set(x); }

    /**
     * Method used to get the simple number of games played property
     * @return soundEffectsEnabled
     */
    public static SimpleIntegerProperty gamesPlayedNumberProperty() { return gamesPlayedNumber; }
    /**
     * Method used to set the number of games played property
     */
    public void setGamesPlayedNumber(int x){ gamesPlayedNumberProperty().set(x); }
}
