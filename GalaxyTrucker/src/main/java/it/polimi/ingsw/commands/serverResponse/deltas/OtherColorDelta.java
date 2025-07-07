package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.model.playerset.PlayerColor;

import java.util.List;

/**
 * informs the player with the other player's colors
 */
public class OtherColorDelta extends Delta{
    List<PlayerColor> otherColors;

    public OtherColorDelta(List<PlayerColor> colors) {
        super(DeltaType.OTHER_COLOR_DELTA);
        otherColors = colors;
    }

    /**
     * returns a list of the other player in game's colors
     * @return the other player's color
     */
    public List<PlayerColor> getOtherColors(){
        return otherColors;
    }
}
