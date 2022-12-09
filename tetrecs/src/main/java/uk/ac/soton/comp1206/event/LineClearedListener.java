package uk.ac.soton.comp1206.event;

/**
 * The Line Cleared listener is used to handle the event when a Line is cleared to pass on the blocks that were cleared
 */
public interface LineClearedListener {
    /**
     * Handle a line cleared event
     * @param a the blocks that were cleared
     */
    public void blocksCleared(int[][] a);
}
