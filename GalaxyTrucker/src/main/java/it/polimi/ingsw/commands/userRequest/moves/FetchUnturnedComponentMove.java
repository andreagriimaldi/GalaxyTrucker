package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;

public class FetchUnturnedComponentMove extends Move{

    public FetchUnturnedComponentMove(GamePhase phase, MoveType type) {
        super(phase, type);
    }
}
