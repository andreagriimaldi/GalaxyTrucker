package it.polimi.ingsw.commands.userRequest;

import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.enums.UserRequestType;

public class UserGameManagementRequest extends UserRequest {
    String gameID;
    FlightType flightType;
    int numberOfPlayers;

    //PER FETCH
    public UserGameManagementRequest(UserRequestType type) {
        super(type);

        this.gameID = null;
        this.flightType = null;
        this.numberOfPlayers = 0;
    }

    //PER JOIN
    public UserGameManagementRequest(UserRequestType type, String gameID) {
        super(type);

        this.gameID = gameID;
        this.flightType = null;
        this.numberOfPlayers = 0;
    }

    //PER CREATE
    public UserGameManagementRequest(UserRequestType type, FlightType flightType, int numberOfPlayers) {
        super(type);

        this.flightType = flightType;
        this.numberOfPlayers = numberOfPlayers;
    }

    public FlightType getFlightType() {
        return flightType;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public String getGameID() {
        return gameID;
    }


}