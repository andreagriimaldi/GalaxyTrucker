package it.polimi.ingsw.commands.serverResponse.deltas;


import it.polimi.ingsw.enums.DeltaType;

import java.io.Serializable;

public class Delta implements Serializable {
    private final DeltaType deltaType;

    public Delta(DeltaType t) {
        this.deltaType = t;
    }

    /**
     * returns the type of the delta
     * @return is the delta type
     */
    public DeltaType getDeltaType() {
        return deltaType;
    }
}