package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.game.Game;

/**
 * HBox at the top of the ChallengeScene where the current score and number of lives can be found
 */
public class TopBar extends HBox {
    private int height = 25;
    public TopBar(Game game){
        setPrefHeight(height);
        this.setPadding(new Insets(0, 100, 0, 100));
        this.setSpacing(6);

        //Score
        VBox vb1 = new VBox();
        vb1.setPadding(new Insets(0, 10, 0, 10));
        vb1.setSpacing(6);
        vb1.setAlignment(Pos.BASELINE_LEFT);
        Label scoreText = new Label("Score");
        scoreText.getStyleClass().add("heading");
        vb1.getChildren().add(scoreText);
        Label score = new Label();
        score.textProperty().bind(game.scoreProperty().asString());
        score.getStyleClass().add("score");
        vb1.getChildren().add(score);
        this.getChildren().add(vb1);

        Region blank1 = new Region();
        this.setHgrow(blank1, Priority.ALWAYS);
        this.getChildren().add(blank1);

        //Title
        Label challengeModeText = new Label("Challenge Mode");
        challengeModeText.getStyleClass().add("title");
        this.getChildren().add(challengeModeText);

        Region blank2 = new Region();
        this.setHgrow(blank2, Priority.ALWAYS);
        this.getChildren().add(blank2);

        //Lives
        VBox vb2 = new VBox();
        vb2.setPadding(new Insets(0, 10, 0, 10));
        vb2.setSpacing(6);
        vb2.setAlignment(Pos.BASELINE_RIGHT);
        Label livesText = new Label("Lives");
        livesText.getStyleClass().add("heading");
        vb2.getChildren().add(livesText);
        Label lives = new Label();
        lives.textProperty().bind(game.livesProperty().asString());
        lives.getStyleClass().add("lives");
        vb2.getChildren().add(lives);
        this.getChildren().add(vb2);
    }
}
