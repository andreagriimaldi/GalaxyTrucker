package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;

public class PopulateShipMove extends Move{
    String choice;

    public PopulateShipMove(GamePhase phase, MoveType type, String choice) {
        super(phase, type);
        this.choice = choice;
    }

    public String getChoice(){
        return choice;
    }
}
