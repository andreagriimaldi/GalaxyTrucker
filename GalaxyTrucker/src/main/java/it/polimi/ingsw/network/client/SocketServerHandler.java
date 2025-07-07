package it.polimi.ingsw.network.client;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.commands.Command;
import it.polimi.ingsw.commands.serverResponse.ServerResponse;
import it.polimi.ingsw.network.CommandMessage;

import it.polimi.ingsw.commands.userRequest.UserSessionManagementRequest;
import it.polimi.ingsw.commands.userRequest.UserGameManagementRequest;
import it.polimi.ingsw.commands.userRequest.UserGameMoveRequest;
import it.polimi.ingsw.commands.userRequest.UserRequest;


import java.io.BufferedInputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;


public class SocketServerHandler extends ServerHandler {


    private Socket serverSocket;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private Thread listenerThread;

    public SocketServerHandler(ViewModel viewModel) {
        super(viewModel);
        this.serverSocket = null;
        this.fromServer = null;
        this.toServer = null;
        this.listenerThread = null;
    }

    @Override
    public void connect(String ipToConnectTo) {
        try {
            this.serverSocket = new Socket(ipToConnectTo, Constants.SOCKET_PORT);
            this.toServer = new ObjectOutputStream(serverSocket.getOutputStream());
            this.toServer.flush();
            this.fromServer = new ObjectInputStream(new BufferedInputStream(serverSocket.getInputStream()));
            super.onConnect();
            startListening();
            System.out.println("connected to " + ipToConnectTo);
        } catch (IOException e) {
            System.err.println("Could not connect to server at " + ipToConnectTo + ":" + Constants.SOCKET_PORT);
        }
    }

    public void startListening() {
        this.listenerThread = new Thread(() -> {
            try {
                // System.out.println("initiating socket listening loop");
                while (!Thread.currentThread().isInterrupted() && !serverSocket.isClosed()) {
                    Object obj = fromServer.readObject(); /** BLOCKING INSTRUCTION **/
                    if (obj instanceof CommandMessage msg) {
                        Command cmd = msg.getCommand();
                        if (cmd instanceof ServerResponse rsp) {
                            addResponseToQueue(rsp);
                        }
                    }
                }
            } catch (SocketException e) {
                System.err.println("Connection reset by server (possibly shutdown).");
                disconnect();
            } catch (IOException e) {
                System.err.println("I/O error in socket listener: " + e.getMessage());
                e.printStackTrace();
                disconnect();
            } catch (ClassNotFoundException e) {
                System.err.println("Received unknown object type from server.");
                e.printStackTrace();
                disconnect();
            }
        }, "ResponseListener");
        listenerThread.start();
    }


    @Override
    public void disconnect() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Disconnected from server.");
            }
        } catch (IOException e) {
            System.err.println("Error closing socket.");
            e.printStackTrace();
        }

        try {
            if (fromServer != null) {
                fromServer.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing input stream.");
            e.printStackTrace();
        }

        try {
            if (toServer != null) {
                toServer.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing output stream.");
            e.printStackTrace();
        }

        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();

        }

        // Clear all references
        serverSocket = null;
        fromServer = null;
        toServer = null;
        listenerThread = null;

        super.onDisconnect();
    }



    /*
    /** avrò da qualche parte un thread che è in ascolto, prende il comando e lo spacchetta
    public * upon receiving () {
        // [ ] estraggo comando da messaggio

        // [ ] chiamo updateUser
    }
    */

    public boolean isConnected() {
        return serverSocket != null && !serverSocket.isClosed();
    }


    public void sendRequest(UserRequest requestToSend) {
        try {
            synchronized (this) {
                if(isConnected() && serverSocket != null) {
                    toServer.writeObject(new CommandMessage(requestToSend));
                    toServer.flush();
                }
            }
        }
        catch (IOException e) {
            disconnect();
        }
    }



    /** ### VIRTUAL SERVER IMPLEMENTATION ### **/



    /** SESSION MANAGEMENT **/

    @Override
    public void register(UserSessionManagementRequest registrationRequest) {
        sendRequest(registrationRequest);
    }

    @Override
    public void login(UserSessionManagementRequest loginRequest) {
        sendRequest(loginRequest);
    }

    @Override
    public void logout(UserSessionManagementRequest logoutRequest) {
        sendRequest(logoutRequest);
    }

    @Override
    public void ping(UserSessionManagementRequest pingRequest) {
        sendRequest(pingRequest);
    }



    /** GAME MANAGEMENT **/

    @Override
    public void createNewGame(UserGameManagementRequest createNewGameRequest) {
        sendRequest(createNewGameRequest);
    }

    @Override
    public void joinGame(UserGameManagementRequest joinGameRequest) {
        sendRequest(joinGameRequest);
    }

    @Override
    public void fetchAvailableGames(UserGameManagementRequest fetchAvailableGamesRequest) {
        sendRequest(fetchAvailableGamesRequest);
    }

    /** only when game hasn't begun yet, or else exception "can't quit a game that's already started" **/
    @Override
    public void quitGame(UserGameManagementRequest quitGameRequest) {
        sendRequest(quitGameRequest);
    }



    /** IN-GAME MOVES **/

    @Override
    public void makeGameMove(UserGameMoveRequest gameMoveRequest) {
        sendRequest(gameMoveRequest);
    }


}