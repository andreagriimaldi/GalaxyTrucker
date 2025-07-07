package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ResourceTypes;

import java.io.Serializable;

public abstract class ResourceType implements Serializable {

    public abstract ResourceTypes getType();

    public String toString(){
        return this.getType().toString();
    }
}
