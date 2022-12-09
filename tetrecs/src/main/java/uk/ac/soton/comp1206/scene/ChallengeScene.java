package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.audio.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.ArrayList;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    /**
     * Handles audio
     */
    private Multimedia media = new Multimedia();
    /**
     * Sidebar of game
     */
    private SideBar sb;
    /**
     * The current coordinates of the piece being hovered over with keyboard
     */
    int x=0, y=0;
    private GameBoard board;
    /**
     * Checks if it is the first move made with keyboard, needed for hovering
     */
    private boolean firstMove = true;
    /**
     * Holds the Timer bar at the bottom of screen
     */
    protected StackPane timerStack;
    /**
     * The timer rectangle at the bottom of screen
     */
    protected TimeBar timer;
    /**
     * Checks if there is a new current highScore
     */
    private boolean isThereNewHighScore = false;
    /**
     * Stores online scores
     */
    private ArrayList<String> remoteScores = new ArrayList<String>();
    /**
     * Checks if music is on
     */
    private boolean isMusicOn = false;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);
        sb = new SideBar(game, this.gameWindow);
        mainPane.setRight(sb);
        TopBar tb = new TopBar(game);
        mainPane.setTop(tb);

        //Timer
        timerStack = new StackPane();
        timer = new TimeBar(gameWindow);
        timerStack.getChildren().add(timer);
        mainPane.setBottom(timerStack);

        //BackgroundMusic
        if(SettingsScene.audioEnabled.get() ==  true){
            media.playMusic("game_start.wav");
            isMusicOn = true;
        }

        //Requests scores
        gameWindow.getCommunicator().addListener(this::loadOnlineScores);
        gameWindow.getCommunicator().send("HISCORES");

        //Handle block on gameboard, or gameblock being clicked, or hovered over
        board.setOnBlockClick(this::blockClicked);
        board.setOnRightClick(this::rightClicked);
        sb.getPieceBoard1().setOnLeftClick(this::leftRotateClicked);
        board.setOnBlockHoveredOver(this::hover);

        //Handles time or livesRanOut
        game.setOnRanOutOfTime(this::ranOutOfTime);
        game.setOnLivesRanOut(this::loadScores);
    }

    /**
     * Once dead it loads the appropriate score scene
     */
    private void loadScores() {
        try {
            scoreSceneDecider();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isThereNewHighScore == true){
            gameWindow.startNewHighScores(game, remoteScores);
            game.getTimerTask().cancel();
            game.getTimer().cancel();
            game.getTimer().purge();
        } else {
            gameWindow.startScores(remoteScores);
            game.getTimerTask().cancel();
            game.getTimer().cancel();
            game.getTimer().purge();
        }
        if(isMusicOn == true){
            this.media.getMusicPlayer().pause();
        }
    }

    /**
     * Refreshes PieceBoards and timerBar
     */
    private void ranOutOfTime() {
        sb.getPieceBoard1().setPiece(game);
        sb.getPieceBoard2().setFollowingPiece(game);
        Timeline timeline = new Timeline(new KeyFrame[]{new KeyFrame(Duration.ZERO,
                new KeyValue[]{new KeyValue(this.timer.fillProperty(), Color.GREEN)}),
                new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(this.timer.widthProperty(), this.timerStack.getWidth())}),
                new KeyFrame(new Duration(game.getTimerDelay()), new KeyValue[]{new KeyValue(this.timer.fillProperty(), Color.RED)}),
                new KeyFrame(new Duration(game.getTimerDelay()), new KeyValue[]{new KeyValue(this.timer.fillProperty(), Color.RED)}),
                new KeyFrame(new Duration(game.getTimerDelay()), new KeyValue[]{new KeyValue(this.timer.widthProperty(), 0)})});
        timeline.play();
    }

    /**
     * Paints gameBlock desired with hover method in GameBlock class
     * @param gameBlock which gameBlock to be painted
     */
    private void hover(GameBlock gameBlock) {
        gameBlock.paintMouseHover();
    }
    /**
     * Action to be taken, when left clicked on board
     * @param gameBoard
     */
    private void leftRotateClicked(GameBoard gameBoard) {
        game.rotateCurrentPiece(1);
        this.blockRotated();
    }
    /**
     * Action to be taken when rightClicked on pieceBoard
     */
    private void rightClicked(GameBoard gameBoard) {
        game.rotateCurrentPiece(1);
        this.blockRotated();
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
        if(game.getIfPlayed() == true){
            gameBlock.updateUnHoveredValue();
            sb.getPieceBoard1().setPiece(game);
            sb.getPieceBoard2().setFollowingPiece(game);
            if (SettingsScene.soundEffectsProperty().get() == true){
                media.playAudio("place.wav");
            }
            Timeline timeline = new Timeline(new KeyFrame[]{new KeyFrame(Duration.ZERO,
                    new KeyValue[]{new KeyValue(this.timer.fillProperty(), Color.GREEN)}),
                    new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(this.timer.widthProperty(), this.timerStack.getWidth())}),
                    new KeyFrame(new Duration(game.getTimerDelay()), new KeyValue[]{new KeyValue(this.timer.fillProperty(), Color.RED)}),
                    new KeyFrame(new Duration(game.getTimerDelay()), new KeyValue[]{new KeyValue(this.timer.fillProperty(), Color.RED)}),
                    new KeyFrame(new Duration(game.getTimerDelay()), new KeyValue[]{new KeyValue(this.timer.widthProperty(), 0)})});
            timeline.play();
        }

    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();

        //KeyBoard Codes
        getScene().setOnKeyPressed((e) -> {
            //Escape
            if (e.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
                if(isMusicOn == true){
                    this.media.getMusicPlayer().pause();
                }
                if (SettingsScene.soundEffectsProperty().get() == true){
                    media.playAudio("transition.wav");
                }
                game.getTimerTask().cancel();
                game.getTimer().cancel();
                game.getTimer().purge();
            }
            //Rotation
            if (e.getCode() == KeyCode.Q) {
                game.rotateCurrentPiece(3);
                this.blockRotated();
            }
            if (e.getCode() == KeyCode.Z) {
                game.rotateCurrentPiece(3);
                this.blockRotated();
            }
            if (e.getCode() == KeyCode.OPEN_BRACKET) {
                game.rotateCurrentPiece(3);
                this.blockRotated();
            }
            if (e.getCode() == KeyCode.E) {
                game.rotateCurrentPiece(1);
                this.blockRotated();
            }
            if (e.getCode() == KeyCode.C) {
                game.rotateCurrentPiece(1);
                this.blockRotated();
            }
            if (e.getCode() == KeyCode.CLOSE_BRACKET) {
                game.rotateCurrentPiece(1);
                this.blockRotated();
            }
            //Swapping
            if (e.getCode() == KeyCode.R) {
                game.swapCurrentPiece();
                this.piecesSwapped();
            }
            if (e.getCode() == KeyCode.SPACE) {
                game.swapCurrentPiece();
                this.piecesSwapped();
            }
            //Moving
            if (e.getCode() == KeyCode.D) {
                board.getBlock(x,y).paintHover();
                if(firstMove == true){
                    board.getBlock(x,y).paintHover();
                    firstMove = false;
                }
                if(x < 4){
                    x=x+1;
                }
                board.getBlock(x,y).paintHover();
            }
            if (e.getCode() == KeyCode.S) {
                board.getBlock(x,y).paintHover();
                if(firstMove == true){
                    board.getBlock(x,y).paintHover();
                    firstMove = false;
                }
                if(y<4){
                    y=y+1;
                }
                board.getBlock(x,y).paintHover();
            }
            if (e.getCode() == KeyCode.A) {
                board.getBlock(x,y).paintHover();
                if(firstMove == true){
                    board.getBlock(x,y).paintHover();
                    firstMove = false;
                }
                if(x>0){
                    x=x-1;
                }
                board.getBlock(x,y).paintHover();
            }
            if (e.getCode() == KeyCode.W) {
                board.getBlock(x,y).paintHover();
                if(firstMove == true){
                    board.getBlock(x,y).paintHover();
                    firstMove = false;
                }
                if(y>0){
                    y=y-1;
                }
                board.getBlock(x,y).paintHover();
            }
            if (e.getCode() == KeyCode.RIGHT) {
                board.getBlock(x,y).paintHover();
                if(firstMove == true){
                    board.getBlock(x,y).paintHover();
                    firstMove = false;
                }
                if(x < 4){
                    x=x+1;
                }
                board.getBlock(x,y).paintHover();
            }
            if (e.getCode() == KeyCode.DOWN) {
                board.getBlock(x,y).paintHover();
                if(firstMove == true){
                    board.getBlock(x,y).paintHover();
                    firstMove = false;
                }
                if(y<4){
                    y=y+1;
                }
                board.getBlock(x,y).paintHover();
            }
            if (e.getCode() == KeyCode.LEFT) {
                board.getBlock(x,y).paintHover();
                if(firstMove == true){
                    board.getBlock(x,y).paintHover();
                    firstMove = false;
                }
                if(x>0){
                    x=x-1;
                }
                board.getBlock(x,y).paintHover();
            }
            if (e.getCode() == KeyCode.UP) {
                board.getBlock(x,y).paintHover();
                if(firstMove == true){
                    board.getBlock(x,y).paintHover();
                    firstMove = false;
                }
                if(y>0){
                    y=y-1;
                }
                board.getBlock(x,y).paintHover();
            }
            //Placing
            if (e.getCode() == KeyCode.ENTER) {
                blockClicked(board.getBlock(x,y));
                board.getBlock(x,y).updateUnHoveredValue();
            }
            if (e.getCode() == KeyCode.X) {
                blockClicked(board.getBlock(x,y));
                board.getBlock(x,y).updateUnHoveredValue();
            }
        });

        //Get TimeBar to play
        Timeline timeline = new Timeline(new KeyFrame[]{new KeyFrame(Duration.ZERO,
                new KeyValue[]{new KeyValue(this.timer.fillProperty(), Color.GREEN)}),
                new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(this.timer.widthProperty(), this.timerStack.getWidth())}),
                new KeyFrame(new Duration(game.getTimerDelay()), new KeyValue[]{new KeyValue(this.timer.fillProperty(), Color.RED)}),
                new KeyFrame(new Duration(game.getTimerDelay()), new KeyValue[]{new KeyValue(this.timer.fillProperty(), Color.RED)}),
                new KeyFrame(new Duration(game.getTimerDelay()), new KeyValue[]{new KeyValue(this.timer.widthProperty(), 0)})});
        timeline.play();
    }

    /**
     * Update PieceBoard when block rotated
     */
    public void blockRotated(){
        sb.getPieceBoard1().setPiece(game);
        if (SettingsScene.soundEffectsProperty().get() == true){
            media.playAudio("rotate.wav");
        }
    }
    /**
     * Update PieceBoards when GamePieces swapped
     */
    public void piecesSwapped(){
        sb.getPieceBoard1().setPiece(game);
        sb.getPieceBoard2().setFollowingPiece(game);
        if (SettingsScene.soundEffectsProperty().get() == true){
            media.playAudio("transition.wav");
        }
    }

    /**
     * Checks whether we have a new highScore or not
     * @throws Exception
     */
    public void scoreSceneDecider() throws Exception{
        File file = new File("scores.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        while (line != null) {
            String[] splitScore = line.split(":");
            int score = Integer.parseInt(splitScore[1]);
            if (score < game.getScore()){
                isThereNewHighScore = true;
            }
            line = br.readLine();
        }
        br.close();
    }

    /**
     * Splits string containing online scores into readable bits, and places them in array
     * @param s string to be split
     */
    public void loadOnlineScores(String s) {
        String[] online = s.split(" ");
        String scores = online[1];
        String[] split = scores.split("\n");
        for (int i = 0; i < 10; i++) {
            String line = split[i];
            remoteScores.add(line);
        }
    }
}
