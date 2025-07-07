package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ResourceTypes;

public class BatteryToken extends ResourceType{
    private ResourceTypes type;

    public BatteryToken(){
        type = ResourceTypes.BATTERY;
    }

    public ResourceTypes getType(){
        return this.type;
    }

}
