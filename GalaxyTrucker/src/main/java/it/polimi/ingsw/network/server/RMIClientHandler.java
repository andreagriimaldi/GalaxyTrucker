package it.polimi.ingsw.network.server;

import java.rmi.ConnectException;
import java.rmi.server.UnicastRemoteObject;

import it.polimi.ingsw.commands.serverResponse.ServerResponse;
import it.polimi.ingsw.commands.userRequest.*;
import it.polimi.ingsw.global.ClientSession;
import it.polimi.ingsw.network.client.RMIRemoteServer;

import java.rmi.RemoteException;



public class RMIClientHandler extends ClientHandler implements RMIRemoteServer {


    //FROM RMI VIRTUAL CLIENT

    private RMIRemoteClient clientStubDelegate;


    public RMIClientHandler(ClientSession clientSession, RMIRemoteClient clientStubDelegate) {
        super(clientSession);
        this.clientStubDelegate = clientStubDelegate;
    }

    public RMIRemoteServer exportServerStub() throws RemoteException {
        //RMIClientHandler serverStub = new RMIClientHandler(clientSession, RMIRemoteClient);
        return (RMIRemoteServer) UnicastRemoteObject.exportObject(this, 0);
    }





    // [ ] DA CAPIR COME FARE





    /** PARTE COMMENTATA ANALOGA A SocketVirtualClient
     *
     *
     *
     *
     *
     */


    /** ### VIRTUAL CLIENT IMPLEMENTATION ### **/
    @Override
    public void notifyUser(ServerResponse response) {
        try {
            clientStubDelegate.notifyUser(response);
        } catch (ConnectException e) {
            System.err.println("Connection refused while notifying client: client is unreachable.");
            disconnect(); // method in superclass
        } catch (RemoteException e) {
            System.err.println("Remote error while notifying client.");
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void disconnect() {
        try {
            UnicastRemoteObject.unexportObject(this, true);
            System.err.println("RMIClientHandler successfully unexported.");
            super.onDisconnect();
        } catch (Exception e) {
            System.err.println("Warning: Failed to unexport RMIClientHandler.");
            e.printStackTrace();
        }
    }





    @Override
    public void addRequestToQueue(UserRequest receivedRequest) {
        clientSession.addRequestToQueue(receivedRequest);
    }




    /** SESSION MANAGEMENT **/

    public void register(UserSessionManagementRequest registrationRequest) throws RemoteException {
        addRequestToQueue(registrationRequest);
    }

    public void login(UserSessionManagementRequest loginRequest) throws RemoteException {
        addRequestToQueue(loginRequest);
    }

    public void logout(UserSessionManagementRequest logoutRequest) throws RemoteException {
        addRequestToQueue(logoutRequest);
    }

    /** in base a se ho inserito nome utente e/o password, le cambia di conseguenza **/
    public void changeCredentials(UserSessionManagementRequest changeCredentialsRequest) throws RemoteException {
        addRequestToQueue(changeCredentialsRequest);
    }

    public void ping(UserSessionManagementRequest pingRequest) throws RemoteException {
        addRequestToQueue(pingRequest);
    }



    /** GAME MANAGEMENT **/

    public void createNewGame(UserGameManagementRequest createNewGameRequest) throws RemoteException {
        addRequestToQueue(createNewGameRequest);
    }

    public void joinGame(UserGameManagementRequest joinGameRequest) throws RemoteException {
        addRequestToQueue(joinGameRequest);
    }

    @Override
    public void fetchAvailableGames(UserGameManagementRequest fetchAvailableGamesRequest) throws RemoteException {
        addRequestToQueue(fetchAvailableGamesRequest);
    }

    /** only when game hasn't begun yet, or else exception "can't quit a game that's already started" **/
    public void quitGame(UserGameManagementRequest quitGameRequest) throws RemoteException {
        addRequestToQueue(quitGameRequest);
    }



    /** IN-GAME MOVES **/

    public void makeGameMove(UserGameMoveRequest gameMoveRequest) throws RemoteException {
        addRequestToQueue(gameMoveRequest);
    }

}