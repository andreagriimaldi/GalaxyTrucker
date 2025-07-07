package it.polimi.ingsw.client;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.model.playerset.PlayerColor;

public class UpdateMessage {
    private final DeltaType type;
    private final Object data;
    private final boolean other;
    private PlayerColor color = null;

    public UpdateMessage(DeltaType type, Object data, boolean other, PlayerColor color){
        this.type = type;
        this.data = data;
        this.other = other;

        if(other){
            this.color = color;
        }
    }

    /**
     * getType() returns the type of update
     */
    public DeltaType getType(){
        return type;
    }

    /**
     * getData() returns the data carried by the update message
     */
    public Object getData(){
        return data;
    }

    /**
     * toOtherPlayer() returns true if the update is about another player's set
     */
    public boolean toOtherPlayer(){
        return other;
    }

    /**
     * getPlayerColor() returns the color of the player if the update is about another player's set, otherwise,
     * if called, throws a RuntimeException
     */
    public PlayerColor getPlayerColor(){
        if(toOtherPlayer()){
            return color;
        }
        else throw new RuntimeException();
    }
}
