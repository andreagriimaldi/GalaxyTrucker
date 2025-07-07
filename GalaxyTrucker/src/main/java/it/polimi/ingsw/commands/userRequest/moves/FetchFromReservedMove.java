package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;

public class FetchFromReservedMove extends Move{
    int choice;

    public FetchFromReservedMove(GamePhase phase, MoveType type, int choice) {
        super(phase, type);
        this.choice = choice;
    }

    /**
     * getChoice() simply returns the user's choice whether to draw the first or the second reserved component
     */
    public int getChoice(){
        return choice;
    }
}
