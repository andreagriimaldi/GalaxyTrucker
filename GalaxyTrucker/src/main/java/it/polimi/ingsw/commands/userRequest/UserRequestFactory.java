package it.polimi.ingsw.commands.userRequest;

import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.enums.UserRequestType;

public class UserRequestFactory {


    /** ### FROM CLIENT TO SERVER ### **/



    /** SESSION MANAGEMENT **/

    public static UserSessionManagementRequest createRegistrationCommand(String username, String passwordHash) {
        return new UserSessionManagementRequest(UserRequestType.REGISTER, username, passwordHash);
    }

    public static UserSessionManagementRequest createLoginCommand(String username, String passwordHash) {
        return new UserSessionManagementRequest(UserRequestType.LOGIN, username, passwordHash);
    }

    public static UserSessionManagementRequest createLogoutCommand() {
        return new UserSessionManagementRequest(UserRequestType.LOGOUT);
    }

    public static UserSessionManagementRequest createPingCommand() {
        return new UserSessionManagementRequest(UserRequestType.PING);
    }


    /** GAME MANAGEMENT **/

    public static UserGameManagementRequest createNewGameRequest(FlightType flightType, int numberOfPlayers) {
        return new UserGameManagementRequest(UserRequestType.CREATE_NEW_GAME, flightType, numberOfPlayers);
    }
    public static UserGameManagementRequest createFetchAvailableGamesRequest() {
        return new UserGameManagementRequest(UserRequestType.FETCH_AVAILABLE_GAMES);
    }
    public static UserGameManagementRequest createJoinGameRequest(String gameID) {
        return new UserGameManagementRequest(UserRequestType.JOIN_GAME, gameID);
    }


    /** GAME MOVES **/

    public static UserGameMoveRequest createMoveRequest(Move move) {
        return new UserGameMoveRequest(UserRequestType.MAKE_MOVE, move);
    }

}