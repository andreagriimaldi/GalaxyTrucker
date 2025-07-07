package it.polimi.ingsw.network.client;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.commands.userRequest.UserSessionManagementRequest;
import it.polimi.ingsw.commands.userRequest.UserGameManagementRequest;
import it.polimi.ingsw.commands.userRequest.UserGameMoveRequest;
import it.polimi.ingsw.network.RMIRemoteServerConnection;
import it.polimi.ingsw.network.server.RMIRemoteClient;

import java.rmi.ConnectException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class RMIServerHandler extends ServerHandler implements RMIRemoteClient {


    // FROM RMI VIRTUAL SERVER
    private RMIRemoteServer serverStub;


    // FROM RMI CLIENT SERVICE


    public RMIServerHandler(ViewModel viewModel) {
        super(viewModel);
        this.serverStub = null;
    }


    public RMIServerHandler export() throws RemoteException {
        return (RMIServerHandler) UnicastRemoteObject.exportObject(this, 0);
    }



    /*  METHOD FOR RECEIVING "CALLS"
    public /* upon receiving *() {
        // [ ] estraggo comando da messaggio

        // [ ] chiamo updateUser
    }
    */



    // FROM RMI VIRTUAL SERVER


    /** METHODS TO MANAGE "CONNECTION" **/

    @Override
    public void connect(String ipToConnectTo) {
        try {
            Registry registry = LocateRegistry.getRegistry(ipToConnectTo, Constants.RMI_REGISTRY_PORT);
            RMIRemoteServerConnection entryPoint = (RMIRemoteServerConnection) registry.lookup(Constants.RMI_ENTRY_POINT_NAME);
            RMIRemoteClient clientStub = (RMIRemoteClient) UnicastRemoteObject.exportObject(this, 0); // [] TO FIX
            RMIRemoteServer serverStub = (RMIRemoteServer) entryPoint.connect(clientStub);

            //System.out.println("Server stub implements: "
                    //+ Arrays.toString(serverStub.getClass().getInterfaces()));

            this.serverStub = serverStub;
            super.onConnect();
            System.out.println("connected to " + ipToConnectTo);
        } catch (ConnectException ce) {
            System.err.println("Connection refused: RMI server is not available at " + ipToConnectTo + ":" + Constants.RMI_REGISTRY_PORT);
        } catch (NotBoundException nbe) {
            System.err.println("RMI entry point not bound on server. Are you sure the server is running?");
        } catch (ExportException e) {
            System.err.println("RMI object already exported");
        } catch (RemoteException re) {
            System.err.println("RemoteException during RMI connection setup.");
        } catch (Exception e) {
            System.err.println("Unexpected error during RMI connection.");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (this.serverStub != null) {
                UnicastRemoteObject.unexportObject(this, true);
                //System.out.println("Client stub unexported successfully.");
                System.out.println("Disconnected from server.");
            }
        } catch (NoSuchObjectException e) {
            // Already unexported or never exported - safe to ignore
        } catch (Exception e) {
            System.err.println("Warning: failed to unexport RMI client object cleanly.");
            e.printStackTrace();
        } finally {
            this.serverStub = null;
            super.onDisconnect();
        }
    }



    /** ### VIRTUAL SERVER IMPLEMENTATION ### **/



    @Override
    public void register(UserSessionManagementRequest registrationRequest) {
        try {
            serverStub.register(registrationRequest);
        } catch (ConnectException e) {
            System.err.println("Connection refused during register: server is unreachable.");
            disconnect();
        } catch (RemoteException e) {
            System.err.println("Remote error during register.");
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void login(UserSessionManagementRequest loginRequest) {
        try {
            serverStub.login(loginRequest);
        } catch (ConnectException e) {
            System.err.println("Connection refused during login: server is unreachable.");
            disconnect();
        } catch (RemoteException e) {
            System.err.println("Remote error during login.");
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void logout(UserSessionManagementRequest logoutRequest) {
        try {
            serverStub.logout(logoutRequest);
        } catch (ConnectException e) {
            System.err.println("Connection refused during logout: server is unreachable.");
            disconnect();
        } catch (RemoteException e) {
            System.err.println("Remote error during logout.");
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void ping(UserSessionManagementRequest pingRequest) {
        try {
            synchronized (this) {
                if(serverStub != null) {
                    serverStub.ping(pingRequest);
                }
            }
        } catch (ConnectException e) {
            System.err.println("Connection refused during ping: server is unreachable.");
            disconnect();
        } catch (RemoteException e) {
            System.err.println("Remote error during ping.");
            e.printStackTrace();
            disconnect();
        }
    }




    /** GAME MANAGEMENT **/

    @Override
    public void createNewGame(UserGameManagementRequest createNewGameRequest) {
        try {
            serverStub.createNewGame(createNewGameRequest);
        } catch (ConnectException e) {
            System.err.println("Connection refused during createNewGame: server is unreachable.");
            disconnect();
        } catch (RemoteException e) {
            System.err.println("Remote error during createNewGame.");
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void joinGame(UserGameManagementRequest joinGameRequest) {
        try {
            serverStub.joinGame(joinGameRequest);
        } catch (ConnectException e) {
            System.err.println("Connection refused during joinGame: server is unreachable.");
            disconnect();
        } catch (RemoteException e) {
            System.err.println("Remote error during joinGame.");
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void fetchAvailableGames(UserGameManagementRequest fetchAvailableGamesRequest) {
        try {
            serverStub.fetchAvailableGames(fetchAvailableGamesRequest);
        } catch (ConnectException e) {
            System.err.println("Connection refused during fetchAvailableGames: server is unreachable.");
            disconnect();
        } catch (RemoteException e) {
            System.err.println("Remote error during fetchAvailableGames.");
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void quitGame(UserGameManagementRequest quitGameRequest) {
        try {
            serverStub.quitGame(quitGameRequest);
        } catch (ConnectException e) {
            System.err.println("Connection refused during quitGame: server is unreachable.");
            disconnect();
        } catch (RemoteException e) {
            System.err.println("Remote error during quitGame.");
            e.printStackTrace();
            disconnect();
        }
    }



    /** IN-GAME MOVES **/

    @Override
    public void makeGameMove(UserGameMoveRequest gameMoveRequest) {
        try {
            serverStub.makeGameMove(gameMoveRequest);
        } catch (ConnectException e) {
            System.err.println("Connection refused during makeGameMove: server is unreachable.");
            disconnect();
        } catch (RemoteException e) {
            System.err.println("Remote error during makeGameMove.");
            e.printStackTrace();
            disconnect();
        }
    }


}