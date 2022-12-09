package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBoard;

/**
 * The LeftClickRotatelistener is used to handle the event when a GameBoard is left clicked.
 */
public interface LeftClickRotateListener {
    /**
     * Handle a board left clicked event
     * @param board the board that was clicked
     */
    public void setOnClick(GameBoard board);
}
