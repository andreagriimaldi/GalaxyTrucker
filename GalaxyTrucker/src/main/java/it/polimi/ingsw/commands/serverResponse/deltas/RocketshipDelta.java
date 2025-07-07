package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.model.utilities.components.Component;

import it.polimi.ingsw.model.playerset.PlayerColor;

public class RocketshipDelta extends Delta {
    private PlayerColor playerColor;
    private Component[][] board;

    /**
     * MyRocketshipUpdate() is a GameState containing the updated Rocketship Board
     */
    public RocketshipDelta(PlayerColor playerColor, Component[][] b) {
        super(DeltaType.ROCKETSHIP_DELTA);
        this.playerColor = playerColor;
        this.board = b;
    }

    /**
     * getPlayerColor() returns the username of the player who owns the board
     */
    public PlayerColor getPlayerColor(){
        return playerColor;
    }

    /**
     * getBoard() returns the updated board
     */
    public Component[][] getBoard(){
        return board;
    }


}
