package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;

public class DetachComponentMove extends Move{
    int row, col;

    public DetachComponentMove(GamePhase phase, MoveType type, int r, int c) {
        super(phase, type);
        row = r;
        col = c;
    }

    /**
     * getCoordinates() returns the board's coordinates, specified by the user to place a component
     */
    public int[] getCoordinates(){
        int[] coord = new int[2];
        coord[0] = row;
        coord[1] = col;

        return coord;
    }
}
