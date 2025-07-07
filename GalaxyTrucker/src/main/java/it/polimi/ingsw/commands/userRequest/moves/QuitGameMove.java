package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;

public class QuitGameMove extends Move {
    public QuitGameMove(GamePhase gamePhase, MoveType moveType) {
        super(gamePhase, moveType);
    }
}