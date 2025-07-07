package it.polimi.ingsw.commands.userRequest;

import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.enums.UserRequestType;

public class UserGameMoveRequest extends UserRequest {
    final Move move;

    public UserGameMoveRequest(UserRequestType type, Move move) {
        super(type);
        this.move = move;
    }

    public Move getMove() {
        return move;
    }
}