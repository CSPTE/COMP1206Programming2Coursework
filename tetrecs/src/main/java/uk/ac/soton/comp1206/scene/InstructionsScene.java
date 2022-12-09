package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.audio.Multimedia;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class InstructionsScene extends BaseScene {
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * Handles audio
     */
    private Multimedia media = new Multimedia();
    /**
     * Checks if music is on
     */
    private boolean isMusicOn = false;

    @Override
    public void initialise() {
        getScene().setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
                if(isMusicOn == true){
                    this.media.getMusicPlayer().pause();
                }
                if (SettingsScene.soundEffectsProperty().get() == true){
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

        //Makes scene pretty
        VBox vBox = new VBox();
        BorderPane.setAlignment(vBox, Pos.CENTER);
        vBox.setAlignment(Pos.TOP_CENTER);
        mainPane.setCenter(vBox);
        Text instructions = new Text("Instructions");
        instructions.getStyleClass().add("headingAlternative");
        vBox.getChildren().add(instructions);
        Text instructionText = new Text("TetrECS is a fast-paced gravity-free block placement game, where you must survive by clearing rows through careful placement of the upcoming blocks before the time runs out. Lose all 3 lives and you're destroyed!");
        TextFlow instructionFlow = new TextFlow(new Node[]{instructionText});
        instructionText.getStyleClass().add("instructions");
        instructionText.setTextAlignment(TextAlignment.CENTER);
        instructionFlow.setTextAlignment(TextAlignment.CENTER);
        vBox.getChildren().add(instructionFlow);
        ImageView instructionImage = new ImageView(this.getClass().getResource("/images/Instructions.png").toExternalForm());
        instructionImage.setFitWidth((double)this.gameWindow.getWidth() / 1.5D);
        instructionImage.setPreserveRatio(true);
        vBox.getChildren().add(instructionImage);
        Text pieces = new Text("Game Pieces");
        pieces.getStyleClass().add("headingAlternative");
        vBox.getChildren().add(pieces);
        GridPane gridPane = new GridPane();
        vBox.getChildren().add(gridPane);
        double padding = (double)(this.gameWindow.getWidth() - this.gameWindow.getWidth() / 14 * 5 - 50) / 2;
        gridPane.setPadding(new Insets(0, padding, 0, padding));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        int x = 0;
        int y = 0;

        //Place PieceBoards with all blocks
        for(int i = 0; i < 15; ++i) {
            GamePiece piece = GamePiece.createPiece(i);
            GameBoard gameBoard = new GameBoard(3, 3, gameWindow.getWidth() / 14, gameWindow.getWidth() / 14);
            gameBoard.getGrid().playPiece(piece,1,1);
            gridPane.add(gameBoard, x, y);
            ++x;
            if (x == 5) {
                x = 0;
                ++y;
            }
        }

        //BackgroundMusic
        if(SettingsScene.audioEnabled.get() ==  true){
            media.playMusic("menu.mp3");
            isMusicOn = true;
        }
    }
}
