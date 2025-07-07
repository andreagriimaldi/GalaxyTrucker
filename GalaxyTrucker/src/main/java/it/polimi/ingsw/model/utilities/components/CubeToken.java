package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ResourceTypes;

import java.io.Serializable;

public class CubeToken extends ResourceType implements Serializable {
    private ResourceTypes type;

    public CubeToken(ResourceTypes t){
        type = t;
    }

    public ResourceTypes getType(){
        return type;
    }

    /**
     * getValue() returns the value of the cube
     */
    public int getValue(){
        switch(type){
            case REDCUBE -> {
                return 4;
            }
            case YELLOWCUBE -> {
                return 3;
            }
            case GREENCUBE -> {
                return 2;
            }
            case BLUECUBE -> {
                return 1;
            }
            default -> throw new RuntimeException();
        }
    }

}
