package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;

public class PutInReservedMove extends Move{

    public PutInReservedMove(GamePhase phase, MoveType type) {
        super(phase, type);
    }
}
