package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBoard;

/**
 * The Right Click listener is used to handle the event when a PieceBoard is right clicked.
 */
public interface RightClicked {
    /**
     * Handle a board right clicked event
     * @param board the board that was clicked
     */
    public void setOnRightClicked(GameBoard board);
}
