package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;

public class StopPeekingDelta extends Delta{

    public StopPeekingDelta() {
        super(DeltaType.STOP_PEEKING_DELTA);
    }
}
