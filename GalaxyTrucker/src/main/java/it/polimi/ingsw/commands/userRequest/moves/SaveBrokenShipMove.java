package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;

public class SaveBrokenShipMove extends Move{
    int choice;

    public SaveBrokenShipMove(GamePhase phase, MoveType type, int choice) {
        super(phase, type);
        this.choice = choice;
    }

    /**
     * getChoice() returns the choice made by the user
     */
    public int getChoice(){
        return choice;
    }
}
