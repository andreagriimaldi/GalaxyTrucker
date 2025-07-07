package it.polimi.ingsw.global;

import java.net.Socket;
import java.util.UUID;
import java.rmi.RemoteException;
import java.util.concurrent.LinkedBlockingQueue;

import it.polimi.ingsw.commands.userRequest.UserGameMoveRequest;
import it.polimi.ingsw.enums.UserRequestType;
import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.enums.ClientSessionState;
import it.polimi.ingsw.network.client.RMIRemoteServer;
import it.polimi.ingsw.network.server.RMIRemoteClient;
import it.polimi.ingsw.network.server.RMIClientHandler;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.commands.userRequest.UserRequest;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.enums.ConnectionType;
import it.polimi.ingsw.exceptions.ClientNotYetAuthenticatedException;
import it.polimi.ingsw.exceptions.LogoutWhileInGameException;
import it.polimi.ingsw.commands.serverResponse.ServerResponse;
import it.polimi.ingsw.network.server.SocketClientHandler;


public class ClientSession {

    /** TO HANDLE THE CONNECTION **/
    private ConnectionType connectionType;
    private ClientHandler clientHandler;

    /** TO HANDLE THE SESSION **/
    private ClientSessionState clientSessionState;
    private String clientSessionID;

    /** TO HANDLE IDENTITY **/
    private String username;

    private GlobalManager globalManager;
    private GameController gameController;

    private LinkedBlockingQueue<UserRequest> userRequestsQueue;
    private LinkedBlockingQueue<ServerResponse> serverResponseQueue;


    private Thread requestDispatcherThread;
    private Thread responseSenderThread;


    public void commonConstructor(GlobalManager globalManager, ClientHandler clientHandler, ConnectionType c) {

        this.connectionType = c;

        this.clientHandler = clientHandler;


        this.clientSessionState = ClientSessionState.PRE_AUTHENTICATED;

        this.clientSessionID = UUID.randomUUID().toString();
        System.out.println(this.clientSessionID + " is the new ClientSession sessionID");

        this.username = null;

        this.userRequestsQueue = new LinkedBlockingQueue<>();
        this.serverResponseQueue = new LinkedBlockingQueue<>();

        this.globalManager = globalManager;
        this.gameController = null;

        this.requestDispatcherThread = null;
        this.responseSenderThread = null;

    }


    public ClientSession(GlobalManager globalManager, Socket clientSocket) {
        ClientHandler socketClientHandler = new SocketClientHandler(this, clientSocket);

        ((SocketClientHandler) socketClientHandler).startListening();
        //System.out.println("Started a socket client handler listener thread");

        commonConstructor(globalManager, socketClientHandler, ConnectionType.SOCKET);
    }


    /*
    public static RMIClientHandler initializeRMIClientHandler(GlobalManager globalManager, RMIRemoteClient clientStubDelegate) {
        ClientSession clientSession = new ClientSession(GlobalManager globalManager, RMIRemoteClient clientStubDelegate);
        return ClientSession.createAndExportServerStub(clientSession); // [] EH MA COSI NON RITORNO MAI LA CLIENT SESSION IN MODO DA SALVARLA IN GLOBAL MANAGER
    }
    */


    public ClientSession(GlobalManager globalManager, RMIRemoteClient clientStubDelegate) {

        ClientHandler rmiClientHandler = new RMIClientHandler(this, clientStubDelegate);
        commonConstructor(globalManager, rmiClientHandler, ConnectionType.RMI);
    }


    /**
     * for exporting the RMI server stub
     * @return the RMI remote server stub
     */
    public RMIRemoteServer exportRMIServerStub() {
        try {
            RMIRemoteServer rmiRemoteServer = ((RMIClientHandler) clientHandler).exportServerStub();
            return rmiRemoteServer;
        } catch (RemoteException e) {
            throw new RuntimeException("Failed to export RMI server stub", e);
        }
    }


    /**
     * adds the client's request to its individual queue
     * the request will then be dispatched to global manager or to the game controller
     * @param receivedRequest is the player request received by the player
     */
    public void addRequestToQueue(UserRequest receivedRequest) {
        receivedRequest.setClientSessionID(clientSessionID);
        //System.out.println("request to " + receivedRequest.getUserRequestType() + " added to client session queue");
        userRequestsQueue.add(receivedRequest);
    }

    /**
     * starts a separate thread that picks the first player request in the queue and dispatches it
     */
    public void startDispatchingRequests() {
        //System.out.println("method called");

        requestDispatcherThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    //System.out.println("dispatching request in client session queue");
                    UserRequest request = userRequestsQueue.take(); /** BLOCKING INSTRUCTION **/
                    dispatchRequest(request);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Dispatcher-" + clientSessionID);

        requestDispatcherThread.start();
    }

    /**
     * executes a routine appropriate to the user request to be dispatched, depending on its type
     * @param request
     */
    private void dispatchRequest(UserRequest request) {

        /*
        System.out.println("dispatchRequest() method called");
        if(gameController == null) {
            System.out.println("gameController is null");
        }
        if(clientSessionState != ClientSessionState.IN_GAME) {
            System.out.println("ClientSessionState is not IN_GAME");
        }
        if(request.getUserRequestType() != UserRequestType.MAKE_MOVE) {
            System.out.println("user request is not a MAKE_MOVE_REQUEST");
        }
         */

        if (request.getUserRequestType() == UserRequestType.PING) {
            globalManager.addRequestToGlobalQueue(request);
        } else if (gameController != null
                && clientSessionState == ClientSessionState.IN_GAME) // [] fai anche un controllo sul tipo di mossa
        {
            if(request.getUserRequestType() == UserRequestType.MAKE_MOVE) {
                UserGameMoveRequest moveRequest = (UserGameMoveRequest) request;
                Move move = moveRequest.getMove();
                move.attachUsername(this.username);

                //System.out.println("adding move to game controller");
                gameController.addMoveToQueue(move);
            } else {
                log("you can't send global requests when in game");
            }
        }
        else {
            globalManager.addRequestToGlobalQueue(request);
            //System.out.println("request in queue forwarded to global manager");
        }
    }

    /**
     * sends to send a generic server response to the player. the response will vary depending
     * on what the server needs to communicate to the client. it may contain logs, and/or game deltas.
     * however, the server response is independent of its content
     * @param responseToSend
     */
    public void notify(ServerResponse responseToSend) {
        serverResponseQueue.add(responseToSend);
    }

    /**
     * starts a separate thread for sending responses to the client. it will pick responses in queue
     * and send them through the network. this is done to avoid having the game threads or global threads
     * interact with the network
     */
    public void startSendingResponses() {

        responseSenderThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    //System.out.println("sending response from client session queue");
                    ServerResponse response = serverResponseQueue.take(); /** BLOCKING INSTRUCTION **/
                    sendResponse(response);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Sender-" + clientSessionID);

        responseSenderThread.start();
    }





    /** ### TO GET USER SESSION INFORMATION ### **/


    /**
     * getState() returns the state of the session. The user can be authenticated, in game or still to authenticate
     * @return the current state of the client session
     */
    public ClientSessionState getClientSessionState(){
        return clientSessionState;
    }

    /**
     * getClientSessionID() returns the clientSessionID, that has been generated when the client when they first joined the lobby
     */
    public String getClientSessionID(){
        return clientSessionID;
    }

    /**
     * getUsername() returns the username if the user's already authenticated, throws an exception otherwise
     */
    public String getUsername(){
        if(!clientSessionState.equals(ClientSessionState.PRE_AUTHENTICATED)){
            return username;
        }
        else throw new ClientNotYetAuthenticatedException();
    }

    /**
     * checks what connection type the player implements
     * @return the connection type (either RMI or SOCKET)
     */
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    /** ### TO NOTIFY THE USER ### **/


    /**
     * to actually send the response to the player, by interacting with the client handler
     * the client handler can either be RMI or SOCKET. this is irrelevant to the sendResponse() method
     * @param response
     */
    public void sendResponse(ServerResponse response) {
        clientHandler.notifyUser(response);
    }

    /**
     * takes a log and encapsulates it in a ServerResponse object, and passes it to methods that take care of+
     * sending it to the player
     * @param log
     */
    public void log(String log) {
        //System.out.println("logging the client with the message: " + log);
        ServerResponse responseLog = new ServerResponse(log);
        notify(responseLog);
    }



    /** ### IN CASE OF SUCCESSFUL LOGIN ### **/

    /**
     * executes a routine in case of effective login. that means setting the client session as authenticated,
     * and storing the player's name directly inside it so as to make the username accessible to any part of the server
     * that has access to client session
     * @param username is the username of the player that logged in with that username (and its respective password)
     */
    public void onLogin(String username) {
        assignUsername(username);
        System.out.println("username " + this.username + " assigned to user");
        setAsAuthenticated();
        System.out.println("user state set to " + this.clientSessionState);
    }

    /**
     * sets the username attribute equal to the username passed as a parameter
     * @param username is the username to assign to the client session
     */
    public void assignUsername(String username) {
        this.username = username;
    }

    /**
     * sets the client as authenticated
     */
    public void setAsAuthenticated() {
        this.clientSessionState = ClientSessionState.AUTHENTICATED;
        //log("Authenticated successfully");
    }


    /**
     * saves a reference to the game the player joined inside client session,
     * allowing the client's request to bypass global request and be passed
     * straight to the game
     * @param game
     */
    public void attachGame(GameController game) {
        this.gameController = game;
        setAsInGame();
        log("associated with game " + game.getGameID() + " and set as " + this.clientSessionState);
    }

    /**
     * removes reference to the game the player was in
     */
    public void detachGame() {
        this.gameController = null;
        setAsAuthenticated();
        log("dissociated from game and set as " + this.clientSessionState);
    }

    /**
     * changeStateToGame() is used by the lobby to change the status of its users when the game starts
     */
    public void setAsInGame(){
        if(clientSessionState.equals(ClientSessionState.AUTHENTICATED)) {
            clientSessionState = ClientSessionState.IN_GAME;
        }
    }




    /** ### IN CASE OF (SUCCESFUL) LOGOUT **/


    /**
     * executes a full logout routine
     * @throws LogoutWhileInGameException in case the player attempts to log out while in game
     * @throws ClientNotYetAuthenticatedException in case attempts to log out having logged in
     */
    public void onLogout() throws LogoutWhileInGameException, ClientNotYetAuthenticatedException {
        if (clientSessionState == ClientSessionState.AUTHENTICATED) {
            removeUsername();
            setAsPreAuthenticated();
        }
        else if (clientSessionState == ClientSessionState.IN_GAME) {
            throw new LogoutWhileInGameException();
        }
        else if (clientSessionState == ClientSessionState.PRE_AUTHENTICATED) {
            throw new ClientNotYetAuthenticatedException();
        }
        /** NON LANCIO ECCEZIONE IN CASO IN CUI IL PLAYER NON E' AUTENTICATO PERCHE' SE NE OCCUPA GIA' getUsername() &/OR getUserID() **/
    }


    /**
     * executes the disconnection routine: the client session is removed from those in global manager,
     * and in case the player was in a game, from the game as well. also, the sender and received thread are interrupted
     */
    public void onDisconnect() {
        System.out.println("disconnecting client " + clientSessionID);
        if (getClientSessionState() == ClientSessionState.IN_GAME && gameController != null) {

            try {
                gameController.onDisconnect(getUsername());

            } catch (ClientNotYetAuthenticatedException e) { // i don't know if this exception is of any use here
                //...
            }
        }

        /* [] da implementare
             passando per il controller (se vedo che user ha stato IN_GAME) => o mi creo una mappa da username(/altro) a Controller, oppure passo per il Controller salvato nel ServerService associato al client (e contenuto nel relativo RMIServerService o SocketServerService)
                - bisogna rimuovere client (da lista "clients") in partite che non sono ancora state avviate (forse il comportamento è analogo se la partita è stata avviata, dato che alla fine, se rimuovo il client a partita avviata, i restanti dati associati rimangono. se lo rimuovo a partita non avviata, e subentrano altri, affinché la partita si avvii arrivando a numero, saranno essi ad essere inizializzati)
                    - dovrò tuttavia rimuovere il client dalla mappa colorToClient
                - per la riconnessione di un client in partita avviata, dovrebbe bastare lo username
         */

        if (requestDispatcherThread != null && requestDispatcherThread.isAlive()) {
            requestDispatcherThread.interrupt();
            //System.out.println("Request dispatcher thread interrupted for client " + username);
        }

        if (responseSenderThread != null && responseSenderThread.isAlive()) {
            responseSenderThread.interrupt();
            //System.out.println("Response sender thread interrupted for client " + username);
        }

        globalManager.onDisconnect(getClientSessionID());


    }


    /**
     * removes the username assigned to the player
     */
    public void removeUsername() {
        this.username = null;
    }

    /**
     * initializes the client session as pre-authenticated, which signifies that the client
     * connected to the server and has initialized a session, but hasn't yet logged in using
     * its credentials
     */
    public void setAsPreAuthenticated() {
        this.clientSessionState = ClientSessionState.PRE_AUTHENTICATED;
    }


    /** ### WHEN A GAME IS JOINED ### **/
    //[ ] DA DECIDERE SE AVVIENE GIA' QUANDO SI E' IN LOBBY, OPPURE SOLO QUANDO LA PARTITA E' AVVIATA



    /** L'HO RESO COMMENTATO PERCHE VIRTUAL CLIENT E' USATO SOLO INDIRETTAMENTE, CHIAMANDO NOTIFYCLIENT() ~Daniele
     *
     * getClient() simply returns the VirtualClient object, used by server for the connection to the client
     *
     * public VirtualClient getClient(){
     *        return toClientHandler;
     *    }
     */



}
