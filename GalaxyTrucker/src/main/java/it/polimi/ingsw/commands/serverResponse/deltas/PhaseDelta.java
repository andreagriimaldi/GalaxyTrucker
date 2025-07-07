package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.enums.GamePhase;

public class PhaseDelta extends Delta{
    private GamePhase phase;

    public PhaseDelta(GamePhase newPhase) {
        super(DeltaType.PHASE_DELTA);
        phase = newPhase;
    }

    /**
     * getPhase() returns the new GamePhase
     */
    public GamePhase getPhase(){
        return phase;
    }
}
