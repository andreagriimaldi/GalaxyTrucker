package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;

public class PeekAStackDelta extends Delta{
    int choice;

    public PeekAStackDelta(int c) {
        super(DeltaType.PEEK_A_STACK_DELTA);
        choice = c;
    }

    /**
     * returns the index of the stack of adventure card that the player requested to peek
     * @return
     */
    public int getChoice(){
        return choice;
    }
}
