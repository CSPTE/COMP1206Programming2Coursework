package uk.ac.soton.comp1206.scene;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * TimeBar at the bottom of Challenge Scene
 */
public class TimeBar extends Rectangle {
    public TimeBar(GameWindow window) {
        this.setHeight(20);
        this.setWidth(window.getWidth());
        this.setFill(Color.GREEN);
    }
}
