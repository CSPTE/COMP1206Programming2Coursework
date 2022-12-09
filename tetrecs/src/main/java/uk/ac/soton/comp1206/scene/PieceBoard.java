package uk.ac.soton.comp1206.scene;

import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;

public class PieceBoard extends GameBoard {
    double width2;
    double height2;

    public PieceBoard(int cols, int rows, double width, double height) {
        super(3, 3, width, height);
        this.width2 = width;
        this.height2 = height;
    }

    /**
     * Sets the currentPiece on the PieceBoard
     * @param game the game being played
     */
    public void setPiece(Game game){
        for(int y = 0; y < this.getGrid().getRows(); ++y) {
            for (int x = 0; x < this.getGrid().getCols(); ++x) {
                this.getGrid().set(x,y,0);
            }
        }
        this.getGrid().playPiece(game.getCurrentPiece(), 1,1);
        this.getBlock(1,1).paintCentre();
    }

    /**
     * Sets the followingPiece on the PieceBoard
     * @param game the game being played
     */
    public void setFollowingPiece(Game game){
        for(int y = 0; y < this.getGrid().getRows(); ++y) {
            for (int x = 0; x < this.getGrid().getCols(); ++x) {
                this.getGrid().set(x,y,0);
            }
        }
        this.getGrid().playPiece(game.getFollowingPiece(), 1,1);
    }
}
