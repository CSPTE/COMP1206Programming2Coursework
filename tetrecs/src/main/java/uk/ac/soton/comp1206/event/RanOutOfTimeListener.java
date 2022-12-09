package uk.ac.soton.comp1206.event;

/**
 * The Ran Out Of Time listener is used to handle the event when the user ran out of time to place a gamePiece.
 */
public interface RanOutOfTimeListener {
    /**
     * Handle a ran out of time event
     */
    public void ranOutOfTime();
}
