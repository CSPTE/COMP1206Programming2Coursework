package uk.ac.soton.comp1206.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;

    private final double width;
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Used to check if keyboard is currently hovering over a block
     */
    private boolean hovering = false;
    /**
     * Used to save the value of a block before we hovered over it
     */
    private SimpleIntegerProperty unHoveredValue = new SimpleIntegerProperty();
    /**
     * Used to check if mouse is currently hovering over a block
     */
    private boolean mouseHovering = false;

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);

    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintTransparent();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
    }

    /**
     * Paint this canvas with the given colour and shaders
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, this.width, this.height);
        gc.setFill(colour);
        gc.fillRect(0, 0, this.width, this.height);
        gc.setFill(Color.color(1, 1, 1, 0.12));
        gc.fillPolygon(new double[]{0, 0, this.width}, new double[]{0, this.height, this.height}, 3);
        gc.setFill(Color.color(1, 1, 1, 0.3));
        gc.fillRect(0, 0, this.width, 3);
        gc.setFill(Color.color(1, 1, 1, 0.3));
        gc.fillRect(0, 0, 3, this.height);
        gc.setFill(Color.color(0, 0, 0, 0.3));
        gc.fillRect(this.width - 3, 0, this.width, this.height);
        gc.setFill(Color.color(0, 0, 0, 0.3));
        gc.fillRect(0, this.height - 3, this.width, this.height);
        gc.setStroke(Color.color(0, 0, 0, 0.5));
        gc.strokeRect(0, 0, this.width, this.height);
    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

    /**
     * Check if we are currently hovering over block with keyboard, if yes, paint to hover value, if not, paint back to original value
     */
    public void paintHover() {
        if(hovering == false){
            this.setUnHoveredValue(this.getValue());
            GraphicsContext gc = this.getGraphicsContext2D();
            gc.setFill(Color.color(1, 1, 1, 0.5));
            gc.fillRect(0, 0, this.width, this.height);
            hovering = true;
        } else {
            if(this.getValue() == 0){
                paintHovering(COLOURS[unHoveredProperty().get()]);
                hovering = false;
            } else {
                paintColor(COLOURS[unHoveredProperty().get()]);
                hovering = false;
            }
        }
    }

    /**
     * Paint this canvas transparent (with shadows and other nice things)
     */
    public void paintTransparent(){
         GraphicsContext gc = this.getGraphicsContext2D();
         gc.clearRect(0, 0, this.width, this.height);
         Color start = Color.color(0, 0, 0, 0.1);
         Color end = Color.color(0, 0, 0, 0.2);
         gc.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.REFLECT, new Stop[]{new Stop(0, start), new Stop(1, end)}));
         gc.fillRect(0, 0, this.width, this.height);
         gc.setStroke(Color.color(1, 1, 1, 1));
         //gc.setStroke(Color.BLACK);
         gc.strokeRect(0, 0, this.width, this.height);
    }

    /**
     * Paint a transparent (white) circle on the block
     */
    public void paintCentre() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setFill(Color.color(1, 1, 1, 0.5));
        gc.fillOval(this.width / 4, this.height / 4, this.width / 2, this.height / 2);
    }

    public SimpleIntegerProperty unHoveredProperty() {
        return unHoveredValue;
    }
    /**
     * Setter method for the unHoveredValue
     * @param x value to set to
     */
    public void setUnHoveredValue(int x){
        unHoveredProperty().set(x);
    }
    /**
     * Used to update the unHoveredValue
     */
    public void updateUnHoveredValue(){
        this.setUnHoveredValue(this.getValue());
    }

    /**
     *
     * @param colour
     */
    public void paintHovering(Paint colour){
        var gc = getGraphicsContext2D();
        //Clear
        gc.clearRect(0,0,width,height);
        //Colour fill
        gc.setFill(colour);
        gc.fillRect(0,0, width, height);
        //Border
        gc.setStroke(Color.color(1, 1, 1, 1));
        gc.strokeRect(0,0,width,height);

    }

    /**
     * Check if we are currently hovering over block with mouse, if yes, paint to hover value, if not, paint back to original value
     */
    public void paintMouseHover() {
        if(mouseHovering == false){
            this.setUnHoveredValue(this.getValue());
            GraphicsContext gc = this.getGraphicsContext2D();
            gc.setFill(Color.color(1, 1, 1, 0.5));
            gc.fillRect(0, 0, this.width, this.height);
            mouseHovering = true;
        } else {
            if(this.getValue() == 0){
                paintHovering(COLOURS[unHoveredProperty().get()]);
                mouseHovering = false;
            } else {
                paintColor(COLOURS[unHoveredProperty().get()]);
                mouseHovering = false;
            }
        }
    }

    /**
     * FadeOut animation when line cleared
     */
    public void fadeOut(){
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, this.width, this.height);
        Color start = Color.color(0, 1, 0, 0);
        Color end = Color.color(0, 0, 0, 1);
        gc.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.REFLECT, new Stop[]{new Stop(0, start), new Stop(1, end)}));
        gc.fillRect(0, 0, this.width, this.height);
        gc.setStroke(Color.color(1, 1, 1, 0.5));
        gc.strokeRect(0, 0, this.width, this.height);
    }
}
