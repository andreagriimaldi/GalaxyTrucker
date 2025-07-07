package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.MoveType;
import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.model.playerset.PlayerColor;

import java.io.Serializable;

public abstract class Move implements Serializable {
    private final GamePhase phase;
    private final MoveType type;
    private String username;
    private PlayerColor color;


    public Move(GamePhase phase, MoveType type) {
        this.phase = phase;
        this.type = type;
        this.username = null;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public MoveType getType() {
        return type;
    }





    // called on the server by ClientSession to assign the player's identity to the move
    public void attachUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


    public void attachColor(PlayerColor color) {
        this.color = color;
    }

    public PlayerColor getColor() {
        return this.color;
    }


}