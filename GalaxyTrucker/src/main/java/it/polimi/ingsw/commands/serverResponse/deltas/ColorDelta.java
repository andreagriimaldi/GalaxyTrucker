package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.model.playerset.PlayerColor;

/**
 * informs the player with their color
 */
public class ColorDelta extends Delta{
    private final PlayerColor color;

    public ColorDelta(PlayerColor color) {
        super(DeltaType.COLOR_DELTA);
        this.color = color;
    }

    /**
     * returns the player's color
     * @return the player's color
     */
    public PlayerColor getColor(){
        return color;
    }
}
