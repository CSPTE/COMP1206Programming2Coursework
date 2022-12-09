package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.audio.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.*;
import uk.ac.soton.comp1206.scene.SettingsScene;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Variable used to store the current GamePiece
     */
    private GamePiece currentPiece;
    /**
     * Used to check if after a click a piece was played or not
     */
    private boolean piecePlayed;
    /**
     * Variable used to store the following piece
     */
    private GamePiece followingPiece;
    /**
     * Multimedia used to handle audio
     */
    private Multimedia media = new Multimedia();
    /**
     * Used to store the time delay given to the timer
     */
    private long timerDelay;
    /**
     * Listener used when the user ran out of time to play a piece
     */
    private RanOutOfTimeListener ranOutOfTime;
    /**
     * Timer used to pressure the user to play a block :)))
     */
    private Timer timer = new Timer();
    /**
     * TimerTask executed by timer
     */
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Platform.runLater(() -> {
                if(getLives() > 0){
                    gameLoop();
                } else {
                    startScoresScene();
                }
            });
        }
    };
    /**
     * Listener used when the user ran out of lives, to load the scoreScene
     */
    private LoadScoresListener load;
    /**
     * Variable used to check if the user is dead
     */
    private boolean dead = false;
    /**
     * Integet property that stores the current score
     */
    private SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    /**
     * Integet property that stores the current level
     */
    private SimpleIntegerProperty level = new SimpleIntegerProperty(0);
    /**
     * Integet property that stores the current number of lives
     */
    private SimpleIntegerProperty lives = new SimpleIntegerProperty(3);
    /**
     * Integet property that stores the current multiplier
     */
    private SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);

        currentPiece = this.spawnPiece();
        followingPiece = this.spawnPiece();
    }
    /**
     * Set the listener to handle an event when the lives ran out
     * @param listener listener to add
     */
    public void setOnLivesRanOut(LoadScoresListener listener){
        this.load = listener;
    }
    /**
     * Triggered when the number of lives ran out, and loads the next Scene
     */
    private void livesRanOut() {
        if(load != null) {
            if(dead == false){
                load.loadScores();
                dead = true;
            }
        }
    }

    /**
     * Method used to call livesRanOut()
     */
    private void startScoresScene(){
        livesRanOut();
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        timer.scheduleAtFixedRate(timerTask, getTimerDelay(), getTimerDelay());
        //timer2.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        //Get the new value for this block
        int previousValue = grid.get(x,y);
        int newValue = previousValue + 1;
        if (newValue  > GamePiece.PIECES) {
            newValue = 0;
        }

        //Update the grid with the new value
        if(grid.canPlayPiece(currentPiece,x,y)){
            grid.playPiece(currentPiece,x,y);
            this.nextPiece();
            this.afterPiece();
            piecePlayed = true;
            createNewTimerTask();
        } else {
            logger.info("Cant play the piece");
            piecePlayed = false;
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Used to spawn a random GamePiece
     * @return the random piece
     */
    public GamePiece spawnPiece(){
        Random random = new Random();
        int value = random.nextInt(14);
        return GamePiece.createPiece(value);
        //return GamePiece.createPiece(3);
    }

    /**
     * Sets the newly spawned piece as the following piece and the previously spawned piece as the currentPiece
     */
    public void nextPiece(){
        currentPiece = followingPiece;
        followingPiece = this.spawnPiece();
    }

    /**
     * Used to check status of game once a piece has been played. It also removes blocks, calculates blocks and lines cleared, etc.
     */
    public void afterPiece(){
        int clearedBlocks = 0;
        int clearedLines = 0;
        int[][] a = new int[5][5];
        for(int x = 0; x < grid.getCols(); ++x) {
            int blocks = 0;
            for(int y = 0; y < grid.getRows(); ++y) {
                if (grid.get(x,y) != 0){
                    blocks++;
                }
            }
            if (blocks == grid.getCols()){
                clearedLines = clearedLines + 1;
                for(int y = 0; y < grid.getRows(); ++y) {
                    a[x][y] = 1;
                }
            }
        }
        for(int y = 0; y < grid.getRows(); ++y) {
            int blocks = 0;
            for(int x = 0; x < grid.getCols(); ++x) {
                if (grid.get(x,y) != 0){
                    blocks++;
                }
            }
            if (blocks == grid.getRows()){
                clearedLines = clearedLines + 1;
                for(int x = 0; x < grid.getCols(); ++x) {
                    a[x][y] = 1;
                }
            }
        }
        for(int y = 0; y < grid.getRows(); ++y) {
            for (int x = 0; x < grid.getCols(); ++x) {
                if(a[x][y] == 1){
                    grid.set(x,y,0);
                    clearedBlocks = clearedBlocks + 1;
                }
            }
        }
        if(clearedLines > 0){
            if (SettingsScene.soundEffectsProperty().get() == true){
                media.playAudio("clear.wav");
            }
        }
        this.score(clearedLines, clearedBlocks);
        this.multiplier(clearedLines);
        this.level();
    }

    /**
     * Method used to get the simple level property
     * @return level
     */
    public SimpleIntegerProperty levelProperty() { return level; }
    /**
     * Method used to get the level property
     * @return levelProperty
     */
    public int getLevel(){ return levelProperty().get(); }
    /**
     * Method used to set the level property
     */
    public void setLevel(int x){ levelProperty().set(x); }
    /**
     * Method used to get the simple score property
     * @return score
     */
    public SimpleIntegerProperty scoreProperty() { return score; }
    /**
     * Method used to get the score property
     * @return scoreProperty
     */
    public int getScore(){ return scoreProperty().get(); }
    /**
     * Method used to set the score property
     */
    public void setScore(int x){ scoreProperty().set(x); }
    /**
     * Method used to get the simple multiplier property
     * @return multiplier
     */
    public SimpleIntegerProperty multiplierProperty() { return multiplier; }
    /**
     * Method used to get the multiplier property
     * @return multiplierProperty
     */
    public int getMultiplier(){ return multiplierProperty().get(); }
    /**
     * Method used to set the multiplier property
     */
    public void setMultiplier(int x){  multiplierProperty().set(x); }
    /**
     * Method used to get the simple lives property
     * @return lives
     */
    public SimpleIntegerProperty livesProperty() { return lives; }
    /**
     * Method used to get the lives property
     * @return livesProperty
     */
    public int getLives(){ return livesProperty().get(); }
    /**
     * Method used to set the lives property
     */
    public void setLives(int x){ livesProperty().set(x); }

    /**
     * Method used to calculate current score
     * @param numberOfLines number of lines cleared
     * @param numberOfBlocks numver of blocks cleared
     */
    public void score(int numberOfLines, int numberOfBlocks){ if(numberOfLines > 0){
            this.setScore( this.getScore() + (numberOfLines * numberOfBlocks * 10 * this.getMultiplier()));
        }
    }
    /**
     * Method used to calculate current multiplier
     * @param numberOfLines number of lines cleared on last click
     */
    public void multiplier(int numberOfLines){
        if(numberOfLines > 0){
            this.setMultiplier(this.getMultiplier()+1);
        } else {
            this.setMultiplier(1);
        }
    }
    /**
     * Method used to calculate current Level
     */
    public void level(){
        int increaseLevel = 0;
        int scoreToNextLevel = this.getScore() - (1000*this.getLevel());
        scoreToNextLevel = scoreToNextLevel - 1000;
        while(scoreToNextLevel >= 0){
            increaseLevel = increaseLevel + 1;
            scoreToNextLevel = scoreToNextLevel - 1000;
        }
        int currentLevel = increaseLevel + this.getLevel();
        this.setLevel(currentLevel);
    }

    /**
     * Getter method for the currentPiece
     * @return currentPiece
     */
    public GamePiece getCurrentPiece(){
        return currentPiece;
    }
    /**
     * Getter method for the followingPiece
     * @return followingPiece
     */
    public GamePiece getFollowingPiece() {
        return followingPiece;
    }
    /**
     * Getter method for the piecePlayed boolean
     * @return piecePlayed
     */
    public boolean getIfPlayed(){
        return piecePlayed;
    }

    /**
     * Rotate current GamePiece
     * @param nrOfRotations number of rotations
     */
    public void rotateCurrentPiece(int nrOfRotations){
        currentPiece.rotate(nrOfRotations);
    }
    /**
     * Swap current GamePiece with following one
     */
    public void swapCurrentPiece(){
        GamePiece tempPiece = currentPiece;
        currentPiece = followingPiece;
        followingPiece = tempPiece;
    }

    /**
     * Calculate the time until life lost
     * @return time until time lost
     */
    public long getTimerDelay(){
        if(12000-(500 * getLevel()) > 2500){
            timerDelay = 12000-(500 * getLevel());
        } else {
            timerDelay = 2500;
        }
        return timerDelay;
    }
    /**
     * Actions taken when time runs out
     */
    public void gameLoop(){
        this.setLives(getLives()-1);
        setMultiplier(1);
        this.nextPiece();
        this.afterPiece();
        ranOutOfTime();
    }
    /**
     * Set the listener to handle an event when the player ran out of time to play a piece
     * @param listener listener to add
     */
    public void setOnRanOutOfTime(RanOutOfTimeListener listener){ this.ranOutOfTime = listener; }
    /**
     * Triggered when the the player runs out of time to play a GamePiece. Call the attached listener.
     */
    private void ranOutOfTime() {
        if(ranOutOfTime != null) {
            ranOutOfTime.ranOutOfTime();
        }
    }
    /**
     * When piece played cancel timer and timerTask, create new ones and schedule them
     */
    private void createNewTimerTask() {
        timerTask.cancel();
        timer.cancel();
        timer.purge();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (getLives() > 0) {
                        gameLoop();
                        System.out.println("korte");
                    } else {
                        System.out.println("alma");
                        startScoresScene();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, getTimerDelay(), getTimerDelay());
    }

    /**
     * Getter method for the timer
     * @return timer
     */
    public Timer getTimer(){
        return timer;
    }

    /**
     * Getter method for the TimerTask
     * @return timerTask
     */
    public TimerTask getTimerTask(){
        return timerTask;
    }
}
