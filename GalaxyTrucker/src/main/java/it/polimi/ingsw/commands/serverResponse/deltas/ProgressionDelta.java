package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.enums.GameProgression;

public class ProgressionDelta extends Delta{
    private GameProgression progression;

    public ProgressionDelta(GameProgression newProgression) {
        super(DeltaType.PROGRESSION_DELTA);
        progression = newProgression;
    }

    /**
     * getProgression() returns the new GameProgression
     */
    public GameProgression getProgression(){
        return progression;
    }
}
