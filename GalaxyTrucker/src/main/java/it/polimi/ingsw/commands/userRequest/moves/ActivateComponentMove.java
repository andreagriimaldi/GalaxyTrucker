package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;

public class ActivateComponentMove extends Move{
    int row, col;

    public ActivateComponentMove(GamePhase phase, MoveType type, int row, int col) {
        super(phase, type);
        this.row = row;
        this.col = col;
    }

    /**
     * getComponent() returns the coordinates of the component that the user wants to activate
     */
    public int[] getComponent(){
        int[] component = new int[2];
        component[0] = row;
        component[1] = col;

        return component;
    }
}
