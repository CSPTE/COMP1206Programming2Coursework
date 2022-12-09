package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The HoverListener is used to handle the event when a block in a GameBoard is being hovered over or unhovered. It passes the
 * GameBlock that was hovered or unhovered in the message
 */
public interface HoverListener {
    /**
     * Handle a block hovered event
     * @param block the block that was hovered or unhovered
     */
    public void blockHoveredOver(GameBlock block);
}
