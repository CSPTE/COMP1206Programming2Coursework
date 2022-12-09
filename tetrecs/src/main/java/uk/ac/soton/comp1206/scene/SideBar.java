package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Sidebar on the right in the challenge scene, where the highScore, level, and following pieces are displayed
 */
public class SideBar extends VBox {
    private int width = 150;
    private PieceBoard pb1;
    private PieceBoard pb2;

    public SideBar(Game game, GameWindow window){
        setPrefWidth(width);
        this.setPadding(new Insets(0, 10, 0, 10));
        this.setSpacing(6);
        this.setAlignment(Pos.BASELINE_RIGHT);
        this.getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());

        //Multiplier
        /*Label multiplierText = new Label("Multiplier");
        multiplierText.getStyleClass().add("heading");
        this.getChildren().add(multiplierText);
        Label multiplier = new Label();
        multiplier.textProperty().bind(game.multiplierProperty().asString());
        multiplier.getStyleClass().add("hiscore");
        this.getChildren().add(multiplier);*/

        //HighScore
        String high = null;
        try {
            high = this.getHighScore();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Label highScoreText = new Label("High Score");
        highScoreText.getStyleClass().add("heading");
        this.getChildren().add(highScoreText);
        Label highScore = new Label(high);
        highScore.getStyleClass().add("hiscore");
        this.getChildren().add(highScore);
        if(game.getScore() > Integer.valueOf(high)){
            highScore.textProperty().bind(game.scoreProperty().asString());
        }

        //Level
        Label levelText = new Label("Level");
        levelText.getStyleClass().add("heading");
        this.getChildren().add(levelText);
        Label level = new Label();
        level.textProperty().bind(game.levelProperty().asString());
        level.getStyleClass().add("level");
        this.getChildren().add(level);

        //Incoming Pieces
        Label incomingText = new Label("Incoming");
        incomingText.getStyleClass().add("heading");
        this.getChildren().add(incomingText);
        pb1 = new PieceBoard(3,3,window.getWidth() / 6, window.getWidth() / 6);
        pb1.setPiece(game);
        this.getChildren().add(pb1);
        Region blank2 = new Region();
        blank2.setMinHeight(40);
        this.getChildren().add(blank2);
        pb2 = new PieceBoard(3,3,window.getWidth() / 10, window.getWidth() / 10);
        pb2.setFollowingPiece(game);
        this.getChildren().add(pb2);
    }

    /**
     * Getter method for the PieceBoard that stores the current Piece
     * @return pb1
     */
    public PieceBoard getPieceBoard1(){
        return pb1;
    }
    /**
     * Getter method for the PieceBoard that stores the following Piece
     * @return pb2
     */
    public PieceBoard getPieceBoard2(){
        return pb2;
    }

    /**
     * Gets the current highScore
     * @return current highScore
     * @throws Exception
     */
    public String getHighScore() throws Exception{
        File file = new File("scores.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        String[] splitScore = line.split(":");
        int score = Integer.parseInt(splitScore[1]);
        br.close();
        String scoreString = String.valueOf(score);
        return scoreString;
    }

}
