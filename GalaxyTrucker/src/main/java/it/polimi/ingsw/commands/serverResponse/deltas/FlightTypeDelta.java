package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.enums.FlightType;

public class FlightTypeDelta extends Delta{
    private final FlightType type;

    public FlightTypeDelta(FlightType type) {
        super(DeltaType.FLIGHT_TYPE_DELTA);
        this.type = type;
    }

    /**
     * getType() returns the flight type
     */
    public FlightType getType(){
        return type;
    }
}
