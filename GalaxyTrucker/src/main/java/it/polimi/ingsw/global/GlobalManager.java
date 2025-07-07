package it.polimi.ingsw.global;

import it.polimi.ingsw.commands.userRequest.UserGameManagementRequest;
import it.polimi.ingsw.commands.userRequest.UserRequest;
import it.polimi.ingsw.enums.UserRequestType;
import it.polimi.ingsw.commands.userRequest.UserSessionManagementRequest;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.enums.ClientSessionState;
import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.GameProgression;
import it.polimi.ingsw.exceptions.*;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;


import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.network.server.RMIRemoteClient;

public class GlobalManager {

    RegistrationManager registrationManager;

    /** FROM USERNAME TO CLIENT SESSION ID **/
    Map<String, String> authenticatedUsers;

    /** FROM SESSION ID TO USER SESSION **/
    Map<String, ClientSession> clientSessions;

    /** FROM GAME ID TO GAME CONTROLLER **/
    Map<String, GameController> games;

    HeartbeatTracker heartbeatTracker;

    LinkedBlockingQueue<UserRequest> globalRequests;
    Thread requestProcessorThread;


    public GlobalManager() {
        this.registrationManager = new RegistrationManager(this);
        this.authenticatedUsers = new HashMap<>();
        this.clientSessions = new HashMap<>();
        this.games = new HashMap<>();

        heartbeatTracker = new HeartbeatTracker(this);
        globalRequests = new LinkedBlockingQueue<>();
        requestProcessorThread = null;
    }


    /**
     * adds a request to the global manager's linked blocking list,
     * so that it will be processed as soon as the processing thread is free
     * @param newRequest is the request to be added
     */
    public void addRequestToGlobalQueue(UserRequest newRequest) {
        this.globalRequests.add(newRequest);
        //System.out.println("request effectively added to global manager");
    }

    /**
     * starts a separate thread for processing and dispatching requests in global manager
     */
    public void startProcessingRequests() {
        this.requestProcessorThread = new Thread(()-> {
            try {
                System.out.println("Server started GLOBAL REQUESTS processing loop on a dedicated thread");
                while (!Thread.currentThread().isInterrupted()) {

                    UserRequest req = globalRequests.take(); /** BLOCKING INSTRUCTION **/
                    //System.out.println("global requests taken. now dispatching and processing");
                    dispatchGlobalRequest(req);

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "GlobalRequestsProcessor");

        this.requestProcessorThread.start();
    }

    /**
     * effectively dispatches the next global request in the queue to the appropriate routine for that type of request
     * @param requestToProcess
     */
    public void dispatchGlobalRequest(UserRequest requestToProcess) {
        //System.out.println("dispatcher method called");
        switch(requestToProcess.getUserRequestType()) {
            case UserRequestType.PING -> {
                UserSessionManagementRequest pingRequest = (UserSessionManagementRequest) requestToProcess;
                String clientSessionID = pingRequest.getClientSessionID();
                heartbeatTracker.updateLastPingFromClient(clientSessionID);
            }
            case UserRequestType.REGISTER -> {

                UserSessionManagementRequest registrationRequest = (UserSessionManagementRequest) requestToProcess;

                String clientSessionID = registrationRequest.getClientSessionID();
                System.out.println("processing registration request from client " + clientSessionID);

                String username = registrationRequest.getUsername();

                String passwordHash = registrationRequest.getPasswordHash();

                System.out.println("received username " + username + " and password hash " + passwordHash);

                register(clientSessionID, username, passwordHash);
            } case UserRequestType.LOGIN -> {
                UserSessionManagementRequest loginRequest = (UserSessionManagementRequest) requestToProcess;

                String clientSessionID = loginRequest.getClientSessionID();

                String username = loginRequest.getUsername();
                String passwordHash = loginRequest.getPasswordHash();

                login(clientSessionID, username, passwordHash);
            } case UserRequestType.LOGOUT -> {
                UserSessionManagementRequest logoutRequest = (UserSessionManagementRequest) requestToProcess;

                String clientSessionID = logoutRequest.getClientSessionID();

                logout(clientSessionID);
            } case UserRequestType.CREATE_NEW_GAME -> { /** [] NB: bisogna rifare i controlli che sono stati fatti sul client **/
                UserGameManagementRequest createNewGameRequest = (UserGameManagementRequest) requestToProcess;

                String clientSessionID = createNewGameRequest.getClientSessionID();
                ClientSession clientSession = getClientSession(clientSessionID);

                if (checkIfAuthenticated(clientSessionID)) {
                    String runningGameID = getRunningGameForPlayer(clientSession.getUsername());

                    if(runningGameID == null) {
                        FlightType flightType = createNewGameRequest.getFlightType();
                        int numberOfPlayers = createNewGameRequest.getNumberOfPlayers();

                        String gameID = createNewGame(flightType, numberOfPlayers);
                        addPlayerToGame(gameID, clientSession);
                    } else {
                        clientSession.log("you're already in a game. join game " + runningGameID + " instead.");
                    }

                } else {
                    clientSession.log("You need to log in before creating a game");
                }
            } case UserRequestType.JOIN_GAME -> {
                UserGameManagementRequest joinGameRequest = (UserGameManagementRequest) requestToProcess;

                String clientSessionID = joinGameRequest.getClientSessionID();
                ClientSession clientSession = getClientSession(clientSessionID);

                if (checkIfAuthenticated(clientSessionID)) {
                    String gameID = joinGameRequest.getGameID();
                    addPlayerToGame(gameID, clientSession);
                } else {
                    clientSession.log("You need to log in before joining a game");
                }

            } case UserRequestType.FETCH_AVAILABLE_GAMES -> {
                UserGameManagementRequest fetchAvailableGamesRequest = (UserGameManagementRequest) requestToProcess;

                String clientSessionID = fetchAvailableGamesRequest.getClientSessionID();
                ClientSession clientSession = getClientSession(clientSessionID);

                if(checkIfAuthenticated(clientSessionID)) {

                    String availableGamesInformation = fetchAvailableGamesInformation(); /** GESTISCE GIA' ANCHE IL CASO IN CUI NON CI SONO PARTITE **/
                    clientSession.log(availableGamesInformation);

                } else {
                    clientSession.log("You need to log in before viewing available games");
                }
            }
        }
    }



    /**
     * the default routine for initializing a session for a client that is implementing socket for communication
     * note: there's no need to return the client session
     * @param clientSocket is the client's socket
     */
    public void initializeClientSession(Socket clientSocket) {
        ClientSession newClientSession = new ClientSession(this, clientSocket);
        System.out.println("new ClientSession created");
        addClientSessionToGlobalManager(newClientSession);
        System.out.println("new ClientSession added to active sessions");

        newClientSession.startDispatchingRequests();
        newClientSession.startSendingResponses();
        System.out.println("started thread that dispatches requests for new client");
    }

    /**
     * the default routine for initializing a session for a client that is implementing RMI for communication
     * @param clientStub is the client's stub
     * @return the client session
     * @throws RemoteException
     */
    public ClientSession initializeClientSession(RMIRemoteClient clientStub) throws RemoteException {
        ClientSession newClientSession = new ClientSession(this, clientStub);
        addClientSessionToGlobalManager(newClientSession);
        newClientSession.startDispatchingRequests();
        newClientSession.startSendingResponses();

        /*
        RMIVirtualClient toClient = new RMIVirtualClient(this, clientStub);

        ServerService newServerService = new ServerService(this);
        RMIServerService fromClient = RMIServerService.createAndExport(newServerService); // da inviare al client, che lo userà come stub

        ClientSession newClientSession = new ClientSession(ConnectionType.RMI, fromClient, toClient);
        toClient.addClientSessionID(newClientSession.getClientSessionID());

        return fromClient;
        */

        return newClientSession;
    }

    /**
     * adds a newly created client session to those that are tracked, realizing a pool of active user sessions
     * @param clientSession is the client session to add
     */
    public void addClientSessionToGlobalManager(ClientSession clientSession) {
        clientSessions.put(clientSession.getClientSessionID(), clientSession);
        heartbeatTracker.registerClient(clientSession.getClientSessionID());
    }

    /**
     * returns the ClientSession object (which represents an active user session) that corresponds to the requested client session ID
     * @param clientSessionID is the requested client session ID that the ClientSession object returned must match
     * @return the requested ClientSession object
     */
    public ClientSession getClientSession(String clientSessionID) {
        return clientSessions.get(clientSessionID);
    }

    /**
     * removes the client session from the active user sessions
     * @param clientSessionID is the ID that the client session to remove has to match
     */
    public void removeClientSession(String clientSessionID) {
        clientSessions.remove(clientSessionID);
    }

    /**
     * checks if the requested client session has already logged in using username and password
     * @param sessionID is the ID of the requested client session
     * @return true if already authenticated, false otherwise
     */
    public boolean checkIfAuthenticated(String sessionID) {
        return authenticatedUsers.containsValue(sessionID);
    }

    /**
     * checks if the client session corresponding to the requested username already logged in using username and password
     * @param username is the username whose authentication status is to be checked
     * @return true if already authenticated, false otherwise
     */
    public boolean isUserAlreadyAuthenticated(String username) {
        return authenticatedUsers.containsKey(username);
    }

    /**
     * removes the entry in the map tracking authenticated users
     * @param username is the username to be removed
     */
    public void removeAuthenticatedUser(String username) {
        authenticatedUsers.remove(username);
    }

    /**
     * gets the client session ID associated to a username that already logged in
     * @param username is the requested username
     * @return the client session ID
     */
    public ClientSession getClientSessionFromUsername(String username) {
        String clientSessionID = authenticatedUsers.get(username);
        return clientSessions.get(clientSessionID);
    }

    /**
     * realizes all the steps required to register a player, including checks
     * @param clientSessionID is the client session ID of the client that is attempting registration
     * @param username is the username provided by the client
     * @param passwordHash is the hash of the password provided by the client
     */
    public void register(String clientSessionID, String username, String passwordHash) {
        ClientSession clientSession = getClientSession(clientSessionID);

        if(checkIfAuthenticated(clientSessionID)) {
            clientSession.log((new ActiveSessionAlreadyExistsException()).getMessage());
            return;
        }

        try {
            registrationManager.registerNewUser(username, passwordHash);
            clientSession.log("Registration succeeded. Proceeding to automatic log in...");
        } catch(UsernameAlreadyExistsException e) {
            clientSession.log(e.getMessage());
        }


        /** SUCCESSFUL REGISTRATION AUTOMATICALLY LOGS IN THE USER RIGHT AFTER **/
        System.out.println("proceeding with automatic login of the newly registered client");
        login(clientSessionID, username, passwordHash);

    }

    /**
     * realizes all the steps required to log a player into the server, including checks
     * @param clientSessionID is the client session ID of the client that is attempting to log in
     * @param username is the username provided by the client
     * @param passwordHash is the hash of the password provided by the client
     */
    public void login(String clientSessionID, String username, String passwordHash) {
        System.out.println("login method called");

        ClientSession clientSession = getClientSession(clientSessionID);
        System.out.println("got client session from clientSessionID");

        if(checkIfAuthenticated(clientSessionID)) {
            clientSession.log((new ActiveSessionAlreadyExistsException()).getMessage());
            return;
        }

        // [ ] FARE IN MODO CHE FUNZIONI SE E SOLO SE LE CREDENZIALI CORRISPONDANO (USANDO TRY-CATCH, CON UN SOLO CATCH SE NON SI VUOLE DIFFERENZIARE IL TIPO DI ECCEZIONE)
        // ovvero, se sono verificate le seguenti condizioni:
            // 1) esiste lo username
            // 2) l'hash della password matcha
            // 3) lo username non ha già una sessione attiva (autenticata o in partita -- la quale preclude previa autenticazione)
        // faccio una chiamata a LOGINREGISTEREDUSER() IN REGISTRATIONMANAGER
            // [ ] IN BASE AL TIPO DI ECCEZIONE, INVIO IL LOG ALLO USER
            // [ ] POI FACCIO RETURN
        try {
            registrationManager.checkUserCredentials(clientSessionID, username, passwordHash);
            // if the three conditions are all satisfied. those conditions are checked in loginRegisteredUser()

            System.out.println("conditions properly checked");

            clientSession.onLogin(username);
            System.out.println("client session updated according as authenticated, and has been assigned their username");

            authenticatedUsers.put(username, clientSessionID);
            System.out.println("username to client session ID map instance created");

            System.out.println("LOGIN SUCCESSFUL");

            clientSession.log("Login Successful.");



            String runningGameID = getRunningGameForPlayer(username);
            if(runningGameID == null) {
                clientSession.log(fetchAvailableGamesInformation());
            } else {
                clientSession.log("please join running game " + runningGameID + " that you disconnected from");
            }


        } catch (UsernameNotFoundException e) {
            clientSession.log(e.getMessage());
        } catch (ActiveSessionAlreadyExistsException e) {
            clientSession.log(e.getMessage());
        } catch (InvalidPasswordException e) {
            clientSession.log(e.getMessage());
        }
    }

    /**
     * logs out the client. that is, their username is removed from the authenticated users and changes
     * the status of the client to prior to authentication
     * @param clientSessionID
     */
    public void logout(String clientSessionID) {

        ClientSession clientSession = getClientSession(clientSessionID);

        try {

            /** L'ORDINE E' NELLO SPECIFICO QUESTO, DATO CHE PRIMA DI RIMUOVERE DA THIS.AUTHENTICATEDUSERS VOGLIO CONTROLLARE
              * SE L'UTENTE NON E' IN PARTITA, O E' ALMENO LOGGATO. LE RELATIVE ECCEZIONI SONO LANCIATE DA CLIENTSESSION.ONLOGOUT() **/

            String username = clientSession.getUsername(); /** THROWS ClientNotYetAuthenticatedException **/

            clientSession.onLogout(); /** THROWS LogoutWhileInGameException, ClientNotYetAuthenticatedException **/

            authenticatedUsers.remove(username);
            clientSession.log("Logout Successful.");
        } catch(Exception e) {
            clientSession.log(e.getMessage());
        }
    }

    /**
     * creates a new game by creating an instance of a game controller, and adds it to those contained in game controller
     * @param flightType specifies the type of flight (trial, or level two)
     * @param numberOfPlayers specifies the number of players for the game to start
     * @return returns the ID of the newly created game
     */
    public String createNewGame(FlightType flightType, int numberOfPlayers) {
        String gameID = UUID.randomUUID().toString();
        GameController newGame = new GameController(this, gameID, flightType, numberOfPlayers);
        addNewGame(gameID, newGame);

        return gameID;
    }

    /**
     * gets a game from the game ID provided
     * @param gameID is the requested game's ID
     * @return the game controller of the requested game
     */
    public GameController getGame(String gameID) {
        return games.get(gameID);
    }

    /**
     * adds a new game to the map that contains all game, within global manager
     * @param gameID is the game's ID
     * @param gameController is the game's controller
     */
    public void addNewGame(String gameID, GameController gameController) {
        games.put(gameID, gameController);
    }

    /**
     * removes the game controller from those contained in global manager
     * @param gameID is the ID of the game to be removed
     */
    public void removeGame(String gameID) { /** per partite che non partono perche tutti i player escono prima che essa effettivamente inizi **/
        games.remove(gameID);
    }


    /**
     * adds a player (more specifically, their client session) to a game
     * @param gameID is the ID of the requested game
     * @param clientSession is the client session of the player to add
     */
    public void addPlayerToGame(String gameID, ClientSession clientSession) {
        try {
            String runningGameID = getRunningGameForPlayer(clientSession.getUsername());

            if(runningGameID != null) {
                if(runningGameID.equals(gameID)) { /** PLAYER IS RECONNECTING TO A GAME IT DISCONNECTED FROM **/
                    GameController gameToJoin = games.get(runningGameID);

                    clientSession.attachGame(gameToJoin);

                    new Thread(() -> { /** to avoid losing the main GlobalManager thread **/
                        try {
                            gameToJoin.addClientToLobby(clientSession);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).start();



                } else {
                    clientSession.log("you're already in a game. join game " + runningGameID + " instead.");
                }
            } else {
                if (games.containsKey(gameID)) {
                    GameController gameToJoin = games.get(gameID);
                    GameProgression gameProgression = gameToJoin.getGameProgression();
                    if (gameProgression == GameProgression.INITIALIZING_GAME) {

                        clientSession.attachGame(gameToJoin);
                        gameToJoin.addClientToLobby(clientSession);


                    } else {
                        clientSession.log("you can't join a game in state " + gameProgression + " which you didn't join during the initialization phase");
                    }
                } else {
                    clientSession.log("there is no such game");
                }
            }



            /* NON CREDO SERVA NEMMENO PIÙ CONTROLLARE LA QUANTITA' DI PLAYER ATTUALMENTE IN PARTITA

            GameController game = games.get(gameID);
            if(game.getNumberOfConnectedPlayingClients() < game.getFinalNumberOfPlayers()) { // [] occhio a questo if che non ne sono sicuro
                if(game.getGameProgression() == GameProgression.INITIALIZING_GAME) { // SE IL GIOCO NON E' ANCORA INIZIATO

                } else
                    if (game.isUsernameInGame(clientSession.getUsername())) { // SE IL GIOCO E' GIA' INIZIATO, CONTROLLO SE IL PLAYER FACESSE PARTE DEL GIOCO
                    game.addClientToLobby(clientSession);
                    clientSession.associateWithGame(game);
                } else {
                    clientSession.log("this username wasn't originally part of the game");
                }
            } else {
                clientSession.log("the game is full");
            }
            */
        } catch(Exception e) { /** [] QUA CI STA DIFFERENZIARE I TIPI DI ECCEZIONE: 1) NON ESISTE GIOCO CORRISPONDENTE AL GAME ID 2a) LA PARTITA E' GIA' IN CORSO / 2b) ?? E' GIA' TERMINATA (in base a se le partite terminate rimangono salvate o meno) 3) MA ANCHE SE LA PARTITA E' PIENA**/
            e.printStackTrace();
        }
    }


    /**
     * if a given player is in-game and the game is in running state, the method returns the ID of said game
     * @param username is the username of the player
     * @return the ID of the game
     */
    public String getRunningGameForPlayer(String username) {
        return games.entrySet()
                .stream()
                .filter(g -> g.getValue().getGameProgression() == GameProgression.RUNNING_GAME)
                .filter(g -> g.getValue().getGamePhase() != GamePhase.END_FLIGHT_PHASE)
                .filter(g -> g.getValue().getUsernamesInRunningGame().contains(username))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }


    /**
     * fetches the main information of all games in the server that haven't been closed yet.
     * it's a visualization of the lobby of all games
     * this includes both games that haven't started yet, but also games that are already running
     * @return a string containing information gathered, which will be sent to players
     */
    public String fetchAvailableGamesInformation() {
        StringBuilder allGamesInformation = new StringBuilder();

        Map<String, GameController> availableGames = games.entrySet().stream() /** GAMES WHOSE Progression IS EITHER Closed, OR Running WITH Phase SET AS END_OF_FLIGHT ARE NOT DISPLAYER IN THE GAMES AVAILABLE TO JOIN **/
                .filter(entry -> {
                    GameController game = entry.getValue();
                    GameProgression progression = game.getGameProgression();
                    return progression == GameProgression.INITIALIZING_GAME ||
                            (progression == GameProgression.RUNNING_GAME && game.getGamePhase() != GamePhase.END_FLIGHT_PHASE);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (availableGames.isEmpty()) {
            allGamesInformation.append("No games available. You can be the first to create a new game");
        } else {
            availableGames.keySet().forEach(gameID -> allGamesInformation.append(formatGameInformation(games.get(gameID))));
        }

        return allGamesInformation.toString();
    }

    /**
     * formats information of a given game for display
     * @param game is the game whose information to format
     * @return game information (as a string)
     */
    public String formatGameInformation(GameController game) {

        GameProgression gameProgression = game.getGameProgression();
        int numberOfConnectedPlayers = 0;

        if(gameProgression == GameProgression.INITIALIZING_GAME) {
            numberOfConnectedPlayers = game.getNumberOfClientsInWaitingRoom();
        } else if (gameProgression == GameProgression.RUNNING_GAME) {
            numberOfConnectedPlayers = game.getNumberOfConnectedPlayingClients();
        }

        int finalNumberOfPlayers = game.getFinalNumberOfPlayers();

        FlightType flightType = game.getFlightType();

        String gameInformation = "("+gameProgression+")" + " ID: " + game.getGameID() + " [" + numberOfConnectedPlayers + "/" + finalNumberOfPlayers + " connected] level: " + flightType + System.lineSeparator();
        return gameInformation;
    }

    /**
     * sends available games information to authenticated players that are not in game
     */
    public void updatePlayersInGlobalLobbyOnAvailableGames() {
        for(String clientSessionID : clientSessions.keySet()) {
            ClientSession clientSession = clientSessions.get(clientSessionID);
            if(clientSession.getClientSessionState() == ClientSessionState.AUTHENTICATED) {
                String availableGamesInformation = fetchAvailableGamesInformation(); /** GESTISCE GIA' ANCHE IL CASO IN CUI NON CI SONO PARTITE **/
                clientSession.log(availableGamesInformation);
            }
        }
    }

    /**
     * sends available games information to a specific (authenticated) player
     * @param username is the username of the player
     */
    public void updatePlayerOnAvailableGames(String username) {
        ClientSession clientSession = getClientSessionFromUsername(username);
        String availableGamesInformation = fetchAvailableGamesInformation(); /** GESTISCE GIA' ANCHE IL CASO IN CUI NON CI SONO PARTITE **/
        clientSession.log(availableGamesInformation);
    }


    /**
     * executes complete routine for global manager to account for the disconnection of a client,
     * regardless of whether they were authenticated or not
     * @param clientSessionID is the session ID of the client that has disconnected
     */
    public void onDisconnect(String clientSessionID) {
        //tra l'altro credo che dovrebbe avere comportamenti diversi in base al tipo di connessione

        heartbeatTracker.unregisterClient(clientSessionID);

        ClientSession clientToDisconnect = getClientSession(clientSessionID);

        if(clientToDisconnect != null) {
            if(checkIfAuthenticated(clientSessionID)) {
                System.out.println(clientToDisconnect.getConnectionType() + " client " + clientToDisconnect.getUsername() + " disconnected: session " + clientSessionID);
            } else {
                System.out.println(clientToDisconnect.getConnectionType() + " client disconnected: session " + clientSessionID);
            }


        /*
            [x] IMPLEMENTATO RIMOZIONE AUTENTICAZIONE:
                - cerco se l'utente era autenticato, e quindi aveva un username. se lo aveva, me lo salvo, e lo uso per rimuovere l'utente da authenticatedUsers
                - in ogni caso, uso clientSessionID per rimuovere il client disconnesso da clientSessions
        */

            if(checkIfAuthenticated(clientSessionID)) {
                try {
                    String username = clientToDisconnect.getUsername();
                    removeAuthenticatedUser(username); //anche questo qua dovrebbe lanciare un'eccezione
                } catch (ClientNotYetAuthenticatedException e) {
                    e.printStackTrace();
                }
            }

            removeClientSession(clientSessionID);
        }

    }

    /**
     * saveGamesOnDisk() is useful to save all the ongoing games on the server's local memory
     */
    public void saveGamesOnDisk(){
        Set<String> id = games.keySet();

        for(String s: id){
            GameController c = games.get(s);

            File saveDir = new File("data");
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            File saveFile = new File(saveDir, "gamestate" + c.getGameID() + ".dat");

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(saveFile))) {
                synchronized (c) {
                    oos.writeObject(c);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * loadGamesFromDisk() is useful to load ongoing games from server's local memory
     */
    public void loadGamesFromDisk(){
        File saveDir = new File("data");
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            return;
        }

        File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) return;

        for (File f : files) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(f))) {

                GameController c = (GameController) ois.readObject();

                games.put(c.getGameID(), c);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}