package it.polimi.ingsw.network.client;

import it.polimi.ingsw.commands.userRequest.*;
import java.rmi.RemoteException;
import java.rmi.Remote;



public interface RMIRemoteServer extends Remote {

    /** SESSION MANAGEMENT **/


    public void register(UserSessionManagementRequest registrationRequest) throws RemoteException;
    public void login(UserSessionManagementRequest loginRequest) throws RemoteException;
    public void logout(UserSessionManagementRequest logoutRequest) throws RemoteException;


    public void ping(UserSessionManagementRequest pingRequest) throws RemoteException;


    /** GAME MANAGEMENT **/


    public void createNewGame(UserGameManagementRequest createNewGameRequest) throws RemoteException;
    public void joinGame(UserGameManagementRequest joinGameRequest) throws RemoteException;
    public void fetchAvailableGames(UserGameManagementRequest fetchAvailableGamesRequest) throws RemoteException;
    public void quitGame(UserGameManagementRequest quitGameRequest) throws RemoteException;


    /** IN-GAME MOVES **/

    public void makeGameMove(UserGameMoveRequest gameMoveRequest) throws RemoteException;

}