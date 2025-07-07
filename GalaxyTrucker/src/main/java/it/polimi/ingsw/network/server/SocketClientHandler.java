package it.polimi.ingsw.network.server;
import java.io.*;

import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

import it.polimi.ingsw.commands.Command;
import it.polimi.ingsw.commands.serverResponse.ServerResponse;
import it.polimi.ingsw.commands.userRequest.UserRequest;
import it.polimi.ingsw.global.ClientSession;
import it.polimi.ingsw.network.CommandMessage;
import it.polimi.ingsw.network.server.ClientHandler;

public class SocketClientHandler extends ClientHandler {



    //FROM SOCKET VIRTUAL CLIENT
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private Socket clientSocket;

    private Thread listenerThread;



    public SocketClientHandler(ClientSession clientSession, Socket clientSocket) {
        super(clientSession);


        try {
            this.clientSocket = clientSocket;

            this.toClient = new ObjectOutputStream(clientSocket.getOutputStream());
            this.toClient.flush();

            this.fromClient = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
            // [] devo fare altro qua dentro?
        }

    }



    /** SE ALLA DISCONNESSIONE DEL CLIENT LA SESSIONE VIENE RIMOSSA E NE DEVO CREARE UNA NUOVA, ALLORA QUESTA PARTE NON SERVE

    public void SocketRemoteClientService() {
        this.toClient = null;
    }


    public void onConnect(Socket clientSocket) {
        this.toClient = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    public void onDisconnect() {
        this.toClient = null;
    }**/




    /** ### VIRTUAL CLIENT IMPLEMENTATION **/

    @Override
    public void notifyUser(ServerResponse response) {
        //System.out.println("sending response through socket");
        sendResponse(response);
    }


    public boolean isConnected() {
        return clientSocket != null && !clientSocket.isClosed();
    }


    public void sendResponse(ServerResponse responseToSend) {
        try {
            if(isConnected()) {
                toClient.reset();
                toClient.writeObject(new CommandMessage(responseToSend));
                toClient.flush();
            }
        }
        catch (IOException e) {
            disconnect(); // METHOD OF THE SUPERCLASS VirtualClient (executes code that is shared among RMI and Socket)
        }
    }



    @Override
    public void disconnect() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client socket.");
            e.printStackTrace();
        }
        try {
            if (fromClient != null) {
                fromClient.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing input stream.");
            e.printStackTrace();
        }
        try {
            if (toClient != null) {
                toClient.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing output stream.");
            e.printStackTrace();
        }

        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();
        }

        // Clear references
        fromClient = null;
        toClient = null;
        clientSocket = null;
        listenerThread = null;

        super.onDisconnect(); // clean up session and remove from global manager
    }


    //DA SOCKET SERVER SERVICE


    @Override
    public void addRequestToQueue(UserRequest receivedRequest) {
        clientSession.addRequestToQueue(receivedRequest);
    }



    public void startListening() {

        this.listenerThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && !clientSocket.isClosed())
                {

                    Object obj = fromClient.readObject(); /** BLOCKING INSTRUCTION **/
                    if (obj instanceof CommandMessage msg) {
                        Command cmd = msg.getCommand();
                        if (cmd instanceof UserRequest req) {
                            addRequestToQueue(req);
                        }
                    }
                }
            } catch (EOFException | SocketException e) {
                System.out.println("closed the socket: normal termination path");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }, "SocketClientHandler-" + clientSession.getClientSessionID());

        this.listenerThread.start();
    }


}