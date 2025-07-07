package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;

public class FlightChoiceMove extends Move {
    int choice;

    public FlightChoiceMove(GamePhase phase, MoveType type, int choice) {
        super(phase, type);
        this.choice = choice;
    }

    /**
     * getPlanet() returns an int indicating on which planet the user wants to land
     */
    public int getChoice(){
        return choice;
    }
}