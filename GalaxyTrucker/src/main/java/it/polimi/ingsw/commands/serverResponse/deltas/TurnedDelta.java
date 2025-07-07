package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.model.utilities.components.Component;

import java.util.List;

public class TurnedDelta extends Delta{
    private List<Component> turned;

    public TurnedDelta(List<Component> t) {
        super(DeltaType.TURNED_DELTA);
        turned = t;
    }

    /**
     * getTurned() returns the list containing the turned components
     */
    public List<Component> getTurned(){
        return turned;
    }
}
