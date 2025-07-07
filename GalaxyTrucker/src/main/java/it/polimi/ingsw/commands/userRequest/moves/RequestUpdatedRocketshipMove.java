package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;
import it.polimi.ingsw.model.playerset.PlayerColor;

public class RequestUpdatedRocketshipMove extends Move {
    private PlayerColor targetColor;

    public RequestUpdatedRocketshipMove(GamePhase gamePhase, MoveType moveType, PlayerColor targetColor) {
        super(gamePhase, moveType);
        this.targetColor = targetColor;
    }

    public PlayerColor getTargetColor() {
        return targetColor;
    }
}
