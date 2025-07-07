package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.model.utilities.components.Component;
import it.polimi.ingsw.model.playerset.PlayerColor;

public class HandDelta extends Delta {
    private final PlayerColor playerColor;
    private final Component hand;

    /**
     * HandDelta() is a GameState containing the updated Hand
     */
    public HandDelta(PlayerColor playerColor, Component h) {
        super(DeltaType.HAND_DELTA);
        this.playerColor = playerColor;
        this.hand = h;
    }

    /**
     * getPlayerColor() returns the color of the player whose hand has been changed
     */
    public PlayerColor getPlayerColor(){
        return playerColor;
    }

    /**
     * getHand() returns the updated hand component
     */
    public Component getHand(){
        return hand;
    }
}
