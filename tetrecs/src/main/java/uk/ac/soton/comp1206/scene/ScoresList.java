package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;

public class ScoresList extends VBox {
    /**
     * Stores array of scores that will be displayed
     */
    private ArrayList<String> scoreArray;
    /**
     * Stores label of text (is it online or local)
     */
    private String scoreText;

    public ScoresList(ArrayList<String> array, String scoreType) {
        this.getStyleClass().add("scorelist");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(2);
        this.setPadding(new Insets(0, 100, 0, 100));
        scoreArray = array;
        scoreText = scoreType;


        Label label0 = new Label(scoreType);
        label0.getStyleClass().add("heading");
        this.getChildren().add(label0);

        Region blank2 = new Region();
        this.setVgrow(blank2, Priority.ALWAYS);
        this.getChildren().add(blank2);

        Label label1 = new Label(scoreArray.get(0));
        label1.setTextFill(Color.DEEPPINK);
        this.getChildren().add(label1);

        Label label2 = new Label(scoreArray.get(1));
        label2.setTextFill(Color.RED);
        this.getChildren().add(label2);

        Label label3 = new Label(scoreArray.get(2));
        label3.setTextFill(Color.ORANGE);
        this.getChildren().add(label3);

        Label label4 = new Label(scoreArray.get(3));
        label4.setTextFill(Color.YELLOW);
        this.getChildren().add(label4);

        Label label5 = new Label(scoreArray.get(4));
        label5.setTextFill(Color.YELLOWGREEN);
        this.getChildren().add(label5);

        Label label6 = new Label(scoreArray.get(5));
        label6.setTextFill(Color.LIME);
        this.getChildren().add(label6);

        Label label7 = new Label(scoreArray.get(6));
        label7.setTextFill(Color.GREEN);
        this.getChildren().add(label7);

        Label label8 = new Label(scoreArray.get(7));
        label8.setTextFill(Color.DARKGREEN);
        this.getChildren().add(label8);

        Label label9 = new Label(scoreArray.get(8));
        label9.setTextFill(Color.AQUA);
        this.getChildren().add(label9);

        Label label10 = new Label(scoreArray.get(9));
        label10.setTextFill(Color.BLUE);
        this.getChildren().add(label10);

    }

    /**
     * Reveal animation for labels
     * @param node node to animate
     */
    public void reveal(Node node){
        FadeTransition fader = new FadeTransition(new Duration(3000), node);
        fader.setFromValue(0);
        fader.setToValue(1);
    }
}
