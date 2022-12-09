package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.*;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.scene.PieceBoard;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 *
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;

    /**
     * Listener to be called when right clicked
     */
    private RightClicked rightClickedListener;
    /**
     * Listener used to rotate block when left clicked
     */
    private LeftClickRotateListener lcrl;
    /**
     * Listener used to see if block is being hovered over
     */
    private HoverListener hoverListener;
    /**
     * GameBlock being hovered over
     */
    private GameBlock hover;


    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        //Build the GameBoard
        build();
    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols number of columns for internal grid
     * @param rows number of rows for internal grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols,rows);

        //Build the GameBoard
        build();
    }

    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     * Also checks if block/board is right or leftClicked
     */
    protected void build() {
        logger.info("Building grid: {} x {}",cols,rows);

        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        blocks = new GameBlock[cols][rows];

        for(var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x,y);
            }
        }
        //Handle right clicking on board
        rightClick(this);
        //Handle left clicking on pieceBoard
        if (this instanceof PieceBoard){
            leftClick(this);
        }
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     * Also checks if a block if entered with mouse or exited, or if block clicked
     * @param x column
     * @param y row
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;
        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);
        //Add to the GridPane
        add(block,x,y);
        //Add to our block directory
        blocks[x][y] = block;
        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x,y));
        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> blockClicked(e, block));
        //Do action when mouse enters block
        block.setOnMouseEntered((e) -> {
            this.hover(block);
        });
        //Do action when mouse exits block
        block.setOnMouseExited((e) -> {
            this.unhover(block);
        });
        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }
    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param event mouse event
     * @param block block clicked on
     */
    private void blockClicked(MouseEvent event, GameBlock block) {
        logger.info("Block clicked: {}", block);
        if(event.getButton() == MouseButton.PRIMARY){
            if(blockClickedListener != null) {
                blockClickedListener.blockClicked(block);
            }
        }
    }

    /**
     * Getter method for the grid
     * @return grid
     */
    public Grid getGrid(){
        return grid;
    }

    /**
     * Set the listener to handle an event when a board is right clicked
     * @param listener listener to add
     */
    public void setOnRightClick(RightClicked listener){ this.rightClickedListener = listener; }
    /**
     * Triggered when the board is right clicked. Call the attached listener.
     * @param event mouse event
     * @param board board clicked on
     */
    private void rightClicked(MouseEvent event, GameBoard board) {
        if(event.getButton() == MouseButton.SECONDARY){
            if(rightClickedListener != null) {
                rightClickedListener.setOnRightClicked(board);
            }
        }
    }
    /**
     * Action to do when pieceBoard is rightClicked
     * @param board board clicked
     */
    public void rightClick(GameBoard board){
        board.setOnMouseClicked((e) -> rightClicked(e, board));
    }

    /**
     * Set the listener to handle an event when a board is left clicked
     * @param listener listener to add
     */
    public void setOnLeftClick(LeftClickRotateListener listener){ this.lcrl = listener; }
    /**
     * Triggered when the pieceBoard is left clicked. Call the attached listener.
     * @param event mouse event
     * @param board block clicked on
     */
    private void leftRotateClicked(MouseEvent event, GameBoard board) {
        if(event.getButton() == MouseButton.PRIMARY){
            if(lcrl != null) {
                lcrl.setOnClick(board);
            }
        }
    }
    /**
     * Action to do when pieceBoard is leftClicked
     * @param board board clicked
     */
    public void leftClick(GameBoard board){
        board.setOnMouseClicked((e) -> leftRotateClicked(e, board));
    }

    /**
     * Set the listener to handle an event when a block is being hovered over with mouse
     * @param listener listener to add
     */
    public void setOnBlockHoveredOver(HoverListener listener){ this.hoverListener = listener; }
    /**
     * Action to do when block is hovered over
     * @param block which block to do action on
     */
    public void hover(GameBlock block) {
        this.hover = block;
        block.paintMouseHover();
    }
    /**
     * Action to do when block is unhovered
     * @param block which block to do action on
     */
    public void unhover(GameBlock block) {
        block.paintMouseHover();
    }

    /**
     * FadeOut animation when line cleared
     */
    public void fadeOut(int[][] a){
        for(int y = 0; y < grid.getRows(); ++y) {
            for (int x = 0; x < grid.getCols(); ++x) {
                if(a[x][y] == 1){
                    this.getBlock(x,y).fadeOut();
                }
            }
        }
    }
}
