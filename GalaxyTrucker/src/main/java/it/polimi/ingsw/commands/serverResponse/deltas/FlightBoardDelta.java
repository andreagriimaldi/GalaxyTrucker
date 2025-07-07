package it.polimi.ingsw.commands.serverResponse.deltas;


import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.model.playerset.PlayerColor;

import java.util.List;

public class FlightBoardDelta extends Delta {
    private List<PlayerColor> flightBoardRepresentation;

    public FlightBoardDelta(List<PlayerColor> flightBoardRepresentation) {
        super(DeltaType.FLIGHT_BOARD_DELTA);
        this.flightBoardRepresentation = flightBoardRepresentation;
    }

    /**
     * returns the updated flightboard
     * @return is the updated flightboard
     */
    public List<PlayerColor> getFlightBoard() {
        return flightBoardRepresentation;
    }

}