package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;

public class FetchTurnedComponentMove extends Move{
    String ID;

    public FetchTurnedComponentMove(GamePhase phase, MoveType type, String ID) {
        super(phase, type);
        this.ID = ID;
    }

    /**
     * getChoice() returns a String indicating which turned components needs to be picked by the user
     */
    public String getID(){
        return ID;
    }
}
