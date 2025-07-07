package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.components.Component;

public class ReservedDelta extends Delta {
    private PlayerColor playerColor;
    private Component[] reserved;

    /**
     * Reserved() is a GameState containing the updated reserved components
     */
    public ReservedDelta(PlayerColor playerColor, Component[] r) {
        super(DeltaType.RESERVED_DELTA);
        this.playerColor = playerColor;
        this.reserved = r;
    }


    /**
     * getPlayerColor() returns the color of the player whose reserved components have been changed
     */
    public PlayerColor getPlayerColor(){
        return playerColor;
    }

    /**
     * getReserved() returns the updated reserved components
     */
    public Component[] getReserved(){
        return reserved;
    }
}
