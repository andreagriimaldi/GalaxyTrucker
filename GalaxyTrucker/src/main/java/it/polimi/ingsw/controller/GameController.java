package it.polimi.ingsw.controller;

import it.polimi.ingsw.commands.serverResponse.ServerResponse;
import it.polimi.ingsw.commands.serverResponse.deltas.*;
import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.commands.userRequest.moves.MoveFactory;
import it.polimi.ingsw.commands.userRequest.moves.QuitGameMove;
import it.polimi.ingsw.commands.userRequest.moves.RequestUpdatedRocketshipMove;
import it.polimi.ingsw.enums.MoveType;
import it.polimi.ingsw.exceptions.ClientNotYetAuthenticatedException;
import it.polimi.ingsw.exceptions.RequestedClientNotFoundException;
import it.polimi.ingsw.global.ClientSession;
import it.polimi.ingsw.global.GlobalManager;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.game.GameSession;
import it.polimi.ingsw.model.game.GameSessionTrial;
import it.polimi.ingsw.model.game.GameSessionTwo;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;

import it.polimi.ingsw.enums.GameProgression;
import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.model.playerset.RocketshipBoard;
import it.polimi.ingsw.model.utilities.components.Component;
import it.polimi.ingsw.model.utilities.components.EmptyComponent;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static it.polimi.ingsw.model.playerset.PlayerColor.*;

public class GameController implements Serializable {
    private GlobalManager globalManager;
    private String gameID;

    private FlightType flightType;

    private GameSession gameSession;
    private GameProgression gameProgression;
    private GamePhase gamePhase;
    
    private final int finalNumberOfPlayers;
    private GameLobby lobby;
    public static final PlayerColor[] DEFAULT_COLOR_ORDER = {RED, BLUE, YELLOW, GREEN};

    private Thread gameExecutionThread;
    private Map<PlayerColor, RocketshipBuildingController> assemblyControllers;
    private AdventureController adventureController;
    private TransitionController transitionController;

    private LinkedBlockingQueue<Move> moveQueue;
    private Thread moveDispatcherThread;

    private Thread lastPlayerTimerThread;
    private boolean runCountdown;


    public GameController(GlobalManager globalManager, String gameID, FlightType flightType, int finalNumberOfPlayers) {
        this.globalManager = globalManager;
        this.gameID = gameID;

        this.flightType = flightType;

        this.gameProgression = GameProgression.INITIALIZING_GAME;
        
        this.finalNumberOfPlayers = finalNumberOfPlayers;
        this.lobby = new GameLobby(finalNumberOfPlayers);

        this.gameExecutionThread = null;
        this.transitionController = null;
        this.assemblyControllers = null;
        this.adventureController = null;

        this.moveQueue = new LinkedBlockingQueue<>();
        this.moveDispatcherThread = null;

        this.lastPlayerTimerThread = null;
        this.runCountdown = false;

        startDispatchingMoves();

    }

    public String getGameID() {
        return gameID;
    }


    public void notifyCurrentWaitingRoomState() {

        List<ClientSession> clientsInWaitingRoom = lobby.getAllClientsInWaitingRoom();
        /* LOGGING ALL PLAYERS IN WAITING ROOM WITH INFORMATION ON PLAYERS IN WAITING ROOM */
        StringBuilder playersInWaitingRoomInfo = new StringBuilder();
        playersInWaitingRoomInfo.append(System.lineSeparator() + System.lineSeparator() + "[WAITING ROOM]:");
        for(int i = 0; i <finalNumberOfPlayers; i++) {
            playersInWaitingRoomInfo.append(System.lineSeparator() + DEFAULT_COLOR_ORDER[i] + ") ");
            if(i<clientsInWaitingRoom.size()) {
                playersInWaitingRoomInfo.append(clientsInWaitingRoom.get(i).getUsername());
            }
        }
        System.out.println(playersInWaitingRoomInfo);
        lobby.logAllPlayersInWaitingRoom(playersInWaitingRoomInfo.toString());
    }


    public void addClientToLobby(ClientSession client){ /* CALLED BY GlobalManager */

        if(gameProgression == GameProgression.INITIALIZING_GAME) {

            lobby.addClientToWaitingRoom(client);
            System.out.println("Added " + client.getUsername() + " to waiting room");

            notifyCurrentWaitingRoomState();

            if (finalNumberOfPlayers == lobby.getAllClientsInWaitingRoom().size()) {
                gameProgression = GameProgression.RUNNING_GAME;
                startGameExecutionThread();
            }

            globalManager.updatePlayersInGlobalLobbyOnAvailableGames();

        }
        else if(gameProgression == GameProgression.RUNNING_GAME) { /** reconnection is done by global manager thread **/
            onReconnect(client);
        }
    }


    public void startGameExecutionThread() {
        this.gameExecutionThread = new Thread(()-> {
            System.out.println("Game controller started GAME EXECUTION on a dedicated thread");
            startGame();
        }, "GameExecutor");

        this.gameExecutionThread.start();
    }


    public void startGame(){
        System.out.println("Starting game");

        gamePhase = GamePhase.ASSEMBLY_PHASE;

        initializePlayers();
        initializeGame();


        lobby.logAllPlayers("GAME INITIALIZED. STARTING ...");

        try {
            Thread.currentThread().sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ServerResponse sr = new ServerResponse();
        sr.addDelta(new FlightTypeDelta(flightType));
        sr.addDelta(new ProgressionDelta(gameProgression));
        sr.addDelta(new PhaseDelta(gamePhase));

        lobby.notifyAllPlayers(sr);

        pushColorIdentitiesToPlayers();

        if(flightType.equals(FlightType.TWO))
            pushPeekableCards();

        waitForEndOfAssembly();
    }


    public void initializePlayers(){

        Map<String, Player> usernameToPlayer = new HashMap<>();
        Map<PlayerColor, ClientSession> colorToClient = new HashMap<>();

        for (int i = 0; i < lobby.getFinalNumberOfPlayers(); i++) {
            String currentUsername = null;
            try {
                currentUsername = lobby.getAllClientsInWaitingRoom().get(i).getUsername();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            PlayerColor currentColor = DEFAULT_COLOR_ORDER[i];
            Player newPlayer = new Player(currentColor, currentUsername, new RocketshipBoard(flightType, currentColor));

            usernameToPlayer.put(currentUsername, newPlayer);
            colorToClient.put(currentColor, lobby.getAllClientsInWaitingRoom().get(i));
        }

        lobby.addColorToClientMap(colorToClient);
        lobby.addUsernameToPlayerMap(usernameToPlayer);
        lobby.removeWaitingRoom();

    }


    public void initializeGame(){

        if(flightType == FlightType.TRIAL) {
            this.gameSession = new GameSessionTrial();
            gameSession.setMaxPlayers(finalNumberOfPlayers);
        } else if (flightType == FlightType.TWO) {
            this.gameSession = new GameSessionTwo();
            gameSession.setMaxPlayers(finalNumberOfPlayers);
        }

        this.transitionController = new TransitionController(this);
        this.transitionController.flipFirstHourglass();
        this.transitionController.startProcessingMoves();

        this.assemblyControllers = new HashMap<>();

        for (String username : lobby.getAllUsernames()) {
            Player player = lobby.getPlayerFromUsername(username);
            gameSession.addPlayer(player);

            PlayerColor color = player.getColor();

            RocketshipBuildingController rocketshipBuildingController = new RocketshipBuildingController(player, this, lobby);
            rocketshipBuildingController.startProcessingMoves(); /* START THREAD */

            assemblyControllers.put(color, rocketshipBuildingController);

            System.out.println("adding rocketship building controller to map for player " + player.getUsername() + " " + color);
        }

    }


    public void addMoveToQueue(Move move) { // the calling user's username will already have been added in ClientSession
        String userMakingTheMovesUsername = move.getUsername();
        if(gameProgression != GameProgression.INITIALIZING_GAME) {
            move.attachColor(lobby.getColorFromUsername(userMakingTheMovesUsername));
        }
        moveQueue.add(move);
    }


    public void startDispatchingMoves() {

        this.moveDispatcherThread = new Thread(()-> {
            try {
                System.out.println("Game controller started MOVE DISPATCHING LOOP on a dedicated thread");
                while (!Thread.currentThread().isInterrupted()) {

                    Move move = moveQueue.take(); /* BLOCKING INSTRUCTION */
                    System.out.println("move taken in game controller. now dispatching and processing");

                    dispatchMove(move);

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "MoveDispatcher");

        this.moveDispatcherThread.start();
    }


    private void dispatchMove(Move move) {

        GamePhase movePhase = move.getPhase();
        MoveType moveType = move.getType();
        if(gameProgression == GameProgression.INITIALIZING_GAME) {
            if(moveType == MoveType.QUIT_GAME) {
                onQuittingGame(move.getUsername());
            }
        } else if(movePhase == this.gamePhase) {
            switch (movePhase) {
                case GamePhase.ASSEMBLY_PHASE -> {

                    /* DISPATCHING MOVE TOWARDS TRANSITION CONTROLLER */
                    if (moveType == MoveType.READY_FOR_TAKEOFF || moveType == MoveType.FLIP_HOURGLASS) {
                        this.transitionController.addMoveToQueue(move); /* DISPATCHING MOVE TOWARDS ASSEMBLY CONTROLLERS */

                    } else {
                        RocketshipBuildingController rbc = this.assemblyControllers.get(move.getColor());
                        rbc.addMoveToQueue(move);
                    }
                }
                case GamePhase.TAKEOFF_PHASE -> {
                    RocketshipBuildingController rbc = this.assemblyControllers.get(move.getColor());
                    rbc.addMoveToQueue(move);
                }
                case GamePhase.FLIGHT_PHASE -> {
                    System.out.println("adding move to adventure controller queue");
                    if(moveType == MoveType.REQUEST_UPDATED_ROCKETSHIP) {
                        RequestUpdatedRocketshipMove rurm = (RequestUpdatedRocketshipMove) move;
                        Component[][] rs = lobby.getPlayerFromColor(rurm.getTargetColor()).getBoard().getWholeShip();
                        RocketshipDelta rsd = new RocketshipDelta(rurm.getTargetColor(), rs);
                        ServerResponse sr = new ServerResponse();
                        sr.addDelta(rsd);
                        System.out.println("received request by " + move.getColor() + " player to view " + rurm.getTargetColor() + " player's rocketship");
                        lobby.notifyPlayer(move.getColor(), sr);
                    } else {
                        adventureController.addMoveToQueue(move);
                    }

                }
                case GamePhase.END_FLIGHT_PHASE -> { /* FOR NOW, ONLY QUIT_GAME MOVES ARE ALLOWED */
                    if(moveType == MoveType.QUIT_GAME) {
                        QuitGameMove quitMove = (QuitGameMove) move;
                        String quittingUsername = quitMove.getUsername();

                        onQuittingGame(quittingUsername);

                    } else if (moveType == MoveType.REQUEST_UPDATED_ROCKETSHIP) {
                        RequestUpdatedRocketshipMove rurm = (RequestUpdatedRocketshipMove) move;
                        Component[][] rs = lobby.getPlayerFromColor(rurm.getTargetColor()).getBoard().getWholeShip();
                        RocketshipDelta rsd = new RocketshipDelta(rurm.getTargetColor(), rs);
                        ServerResponse sr = new ServerResponse();
                        sr.addDelta(rsd);
                        System.out.println("received request by " + move.getColor() + " player to view " + rurm.getTargetColor() + " player's rocketship");
                        lobby.notifyPlayer(move.getColor(), sr);
                    } else {
                        PlayerColor playerColor = move.getColor();
                        lobby.logPlayer(playerColor, "you can only quit after the game is over");
                    }
                }
            }
        } else {
            lobby.logPlayer(move.getColor(), "move not appropriate for current game phase. received: " + movePhase + ". actual: " + this.gamePhase);
        }
    }


    public void waitForEndOfAssembly() {

        try {
            while(!transitionController.checkIsAssemblyOver()) {
                Thread.sleep(1000); // ricontrollo ogni secondo; il thread mandato in sleep è GameExecutorThread
                transitionController.checkIsAssemblyOver();
            }
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
            Thread.currentThread().interrupt();
        }


        transitionController.forceStopHourglass();
        lobby.logAllPlayers("TRANSITIONING TO TAKEOFF PHASE");
        transitionToTakeOffPhase();
    }


    public void transitionToTakeOffPhase() {

        ServerResponse sr = new ServerResponse();
        sr.addDelta(new PhaseDelta(GamePhase.TAKEOFF_PHASE));
        lobby.notifyAllPlayers(sr);

        this.gamePhase = GamePhase.TAKEOFF_PHASE;

        for(PlayerColor playerColor : lobby.getAllPlayerColors()) { /* first: check which rocketships require no input from the user as they require no fixing -- already in conformity with the rules */
            RocketshipBuildingController rocketshipBuildingController = assemblyControllers.get(playerColor);
            rocketshipBuildingController.checkRocketshipAtTakeOff(); /* also notifies clients if their rocketship is ready for takeoff */


            rocketshipBuildingController.penalizeForReserved(); /* for every player, assign a penalty based on the number of reserved components */
        }


        /* SI E' MESSI IN PRONTI IN AUTOMATICO IN BASE A SE NESSUNA ROCKETSHIP HA PIU ERRORI; IL CONTROLLO AVVIENE DOPO OGNI
         * RIMOZIONE DI COMPONENTI DA PARTE DELL'UTENTE. L'AGGIORNAMENTO AVVIENE MEDIANTE ROCKETSHIP BUILDING CONTROLLER, CHE
         * MODIFICA LA PARTE DI TRANSITION CONTROLLER CHE SI OCCUPA DI TENER TRACCIA DI CHI HA FINITO, E DI SE HANNO FINITO TUTTI */
        try {
            while(!transitionController.checkAreAllRocketshipsReadyForFlight()) {
                Thread.sleep(1000); // ricontrollo ogni secondo; il thread mandato in sleep è GameExecutorThread
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        lobby.logAllPlayers("All players' rocketships are ready for takeoff");
        transitionToFlightPhase();
    }

    public void transitionToFlightPhase() {

        gamePhase = GamePhase.FLIGHT_PHASE;

        System.out.println("instantiating adventure controller");
        this.adventureController = new AdventureController(this);

        System.out.println("getting players in assembly finishing order");
        List<PlayerColor> orderOfAssemblyFinishersColor = transitionController.getPlayersInAssemblyFinishingOrder();
        System.out.println("initializing flight");
        List<Player> orderOfAssemblyFinishers = lobby.getOrderedPlayersByColor(orderOfAssemblyFinishersColor);
        this.adventureController.initializeFlight(orderOfAssemblyFinishers);

        System.out.println("starting procesing moves");
        this.adventureController.startProcessingMoves();

        System.out.println("drawing first card");
        this.adventureController.beginFlight();

    }


    public void setFlightAsOver() {
        terminateLastPlayerTimerThread();

        if(gamePhase.equals(GamePhase.FLIGHT_PHASE)){
            gamePhase = GamePhase.END_FLIGHT_PHASE;

            PhaseDelta phaseDelta = new PhaseDelta(gamePhase);
            ServerResponse sr = new ServerResponse();
            sr.addDelta(phaseDelta);
            lobby.notifyAllPlayers(sr);
        }
    }


    public void killGame() {

        System.out.println("KILLING GAME...");

        if(gameProgression != GameProgression.INITIALIZING_GAME) {
            transitionController.forceStopHourglass();
            transitionController.terminateMoveProcessorThread();

            for(RocketshipBuildingController rbc : assemblyControllers.values()) {
                rbc.terminateMoveProcessorThread();
            }

            if(adventureController != null) {
                adventureController.terminateMoveProcessorThread();
            }

            terminateLastPlayerTimerThread();
        }

        this.gameProgression = GameProgression.CLOSED_GAME;
        globalManager.removeGame(this.gameID);

    }


    public GameProgression getGameProgression() { return gameProgression; }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public FlightType getFlightType() {
        return this.flightType;
    }

    public int getFinalNumberOfPlayers() {
        return finalNumberOfPlayers;
    }

    public int getNumberOfClientsInWaitingRoom() {
        List<ClientSession> clients = lobby.getAllClientsInWaitingRoom();
        return clients.size();
    }

    public int getNumberOfConnectedPlayingClients() {
        List<ClientSession> clients = lobby.getAllConnectedClients();
        return clients.size();
    }

    public List<String> getUsernamesInRunningGame() {
        if (gameProgression == GameProgression.RUNNING_GAME) {
            return lobby.getAllUsernames();
        } else {
            return new LinkedList<>();
        }
    }

    public GameLobby getLobby() {
        return lobby;
    }




    /**
     * getGameSession() simply returns the GameSession that the controller is handling
     */
    public GameSession getGameSession(){
        return gameSession;
    }

    public TransitionController getTransitionController() {
        return transitionController;
    }


    public void removeClientFromWaitingRoom(String username) {

        lobby.removeClientFromWaitingRoom(username);

        notifyCurrentWaitingRoomState();

        lobby.getAllClientsInWaitingRoom();
        if(getNumberOfClientsInWaitingRoom() == 0){
            System.out.println("NO PLAYERS LEFT IN WAITING ROOM");
            killGame();
        }

        globalManager.updatePlayersInGlobalLobbyOnAvailableGames();
    }

    public void removeClientFromGame(String username) {
        lobby.removeClientFromGame(username);

        int connectedClientsLeft = lobby.getAllConnectedClients().size();

        if(gamePhase != GamePhase.END_FLIGHT_PHASE) {

            if (connectedClientsLeft > 1) {
                lobby.logAllPlayers(connectedClientsLeft + " connected clients remaining");
            } else if (connectedClientsLeft == 1) { /* IF ONLY ONE PLAYER IS LEFT, A TIMEOUT IS STARTED UNTIL THE PLAYER WINS THE GAME BY DEFAULT, OR UNTIL A SECOND PLAYER JOINS BACK */
                // starting a 60sec timer, at the end of which the remaining player is declared as winner

                lobby.logAllPlayers("1 player left... starting timer");
                startLastPlayerTimerThread();
            }
        }

        if(connectedClientsLeft == 0) {
            System.out.println("NO PLAYERS LEFT IN GAME");
            killGame();
        }
    }

    public void onQuittingGame(String quittingUsername) { /* ONLY CALLED IN GamePhase.END_FLIGHT_PHASE */

        try {
            if (gameProgression == GameProgression.INITIALIZING_GAME) {
                ClientSession quittingClient = lobby.getClientFromWaitingRoom(quittingUsername);
                quittingClient.detachGame();
                removeClientFromWaitingRoom(quittingUsername);
            } else if (gameProgression == GameProgression.RUNNING_GAME && gamePhase == GamePhase.END_FLIGHT_PHASE) {
                ClientSession quittingClient = lobby.getClientFromUsername(quittingUsername);
                quittingClient.detachGame();
                removeClientFromGame(quittingUsername);
                globalManager.updatePlayerOnAvailableGames(quittingUsername);
            }
        } catch(RequestedClientNotFoundException e) {
            System.err.println("UPON REQUESTING TO REMOVE QUITTING CLIENT: " + e.getMessage() + " possibly because it disconnected already");
        }


        lobby.logAllPlayers(quittingUsername + " left the game");

    }

    public void onDisconnect(String username) {
        lobby.logAllPlayers(username + " disconnected from game");

        if (gameProgression == GameProgression.INITIALIZING_GAME) {
            lobby.logAllPlayers("player disconnected from game not yet started");
            removeClientFromWaitingRoom(username);

        } else if (gameProgression == GameProgression.RUNNING_GAME) {
            removeClientFromGame(username);

            if(gamePhase == GamePhase.TAKEOFF_PHASE) {
                Player player = lobby.getPlayerFromUsername(username);
                player.getBoard().fixDisconnectedPlayersRocketship();
            }
            if(gamePhase == GamePhase.FLIGHT_PHASE) {
                Move adventureChoiceMove = MoveFactory.createFlightChoiceMove(0);
                adventureChoiceMove.attachColor(lobby.getColorFromUsername(username));
                adventureChoiceMove.attachUsername(username);
                addMoveToQueue(adventureChoiceMove);
            }
        }
    }

    public boolean isUsernameInGame(String username) {
        return lobby.isUsernameInGame(username);
    }

    public void onReconnect(ClientSession reconnectedClient) { /* will be executed by the main GlobalManager thread */
        // ogni join della partita a partita iniziata, chiama questo metodo

        try {
            String reconnectedUsername = reconnectedClient.getUsername();

            lobby.logAllPlayers(reconnectedClient.getUsername() + " (" + lobby.getColorFromUsername(reconnectedClient.getUsername()) + " player) re-entered the game");

            PlayerColor reconnectedColor = lobby.getColorFromUsername(reconnectedUsername);
            lobby.addColorToClient(reconnectedColor, reconnectedClient);

            if(lobby.getAllConnectedClients().size() > 1) {
                runCountdown = false;
            }


            reconnectedClient.log("SUCCESSFULLY RE-JOINED GAME");

            // if the game is still running, send all logs in buffer
            lobby.sendLogBufferToReconnectedColor(reconnectedColor);

            sendBackupGameState(lobby.getColorFromUsername(reconnectedClient.getUsername()));


        } catch (ClientNotYetAuthenticatedException e) {
            //...
        }

        /*after the game has started, only plyers that have their username in game, but no client session in game (due to disconnection) can join back*/

    }


    public void startLastPlayerTimerThread() {
        this.lastPlayerTimerThread = new Thread(() -> {
            System.out.println("starting countdown thread");
            this.runCountdown = true; // sets itself as true because, if it starts, that means there's only one player left

            try {
                /* COUNTDOWN */
                for (int i = 60; i > 0; i--) {
                    if (!runCountdown) { /* THE BOOLEAN runCountdown IS USED TO STOP THE OUTSIDE IF A PLAYER RECONNECTS */
                        lobby.logAllPlayers("Countdown interrupted since a player re-joined the game");
                        return;
                    }

                    if(i%10 == 0 && i!= 0) {
                        lobby.logAllPlayers(i + " seconds left");
                    } // actually only the player left should be logged, but this way those that join can see what happened in the meanwhile with regards to logs
                    Thread.sleep(1000);
                }
                lobby.logAllPlayers("time is up. the remaining player won the game");
                killGame();
            } catch (InterruptedException e) {
                System.out.println("Countdown thread was interrupted");
                Thread.currentThread().interrupt();
            }
        }, "LastPlayerTimer");

        this.lastPlayerTimerThread.start();
    }

    public void terminateLastPlayerTimerThread() {
        if(lastPlayerTimerThread != null && lastPlayerTimerThread.isAlive()) {
            lastPlayerTimerThread.interrupt();
            lastPlayerTimerThread = null;
        }
    }


    private void pushColorIdentitiesToPlayers() {
        for(int i = 0; i < lobby.getFinalNumberOfPlayers(); i++){
            switch (i){
                case 0 -> {
                    ServerResponse resp = new ServerResponse();
                    resp.addDelta(new ColorDelta(RED));
                    List<PlayerColor> otherColors = new ArrayList<>();
                    if(lobby.getFinalNumberOfPlayers() == 2){
                        otherColors.add(BLUE);
                    }
                    else if(lobby.getFinalNumberOfPlayers() == 3){
                        otherColors.add(BLUE);
                        otherColors.add(YELLOW);
                    }
                    else{
                        otherColors.add(BLUE);
                        otherColors.add(YELLOW);
                        otherColors.add(GREEN);
                    }
                    resp.addDelta(new OtherColorDelta(otherColors));
                    lobby.notifyPlayer(RED, resp);
                }
                case 1 -> {
                    ServerResponse resp = new ServerResponse();
                    resp.addDelta(new ColorDelta(BLUE));
                    List<PlayerColor> otherColors = new ArrayList<>();
                    otherColors.add(RED);
                    if(lobby.getFinalNumberOfPlayers() == 3){
                        otherColors.add(YELLOW);
                    }
                    else if(lobby.getFinalNumberOfPlayers() == 4){
                        otherColors.add(YELLOW);
                        otherColors.add(GREEN);
                    }
                    resp.addDelta(new OtherColorDelta(otherColors));
                    lobby.notifyPlayer(BLUE, resp);
                }
                case 2 -> {
                    ServerResponse resp = new ServerResponse();
                    resp.addDelta(new ColorDelta(YELLOW));
                    List<PlayerColor> otherColors = new ArrayList<>();
                    otherColors.add(RED);
                    otherColors.add(BLUE);
                    if(lobby.getFinalNumberOfPlayers() == 4){
                        otherColors.add(GREEN);
                    }
                    resp.addDelta(new OtherColorDelta(otherColors));
                    lobby.notifyPlayer(YELLOW, resp);
                }
                case 3 -> {
                    ServerResponse resp = new ServerResponse();
                    resp.addDelta(new ColorDelta(GREEN));
                    List<PlayerColor> otherColors = new ArrayList<>();
                    otherColors.add(RED);
                    otherColors.add(BLUE);
                    otherColors.add(YELLOW);
                    resp.addDelta(new OtherColorDelta(otherColors));
                    lobby.notifyPlayer(GREEN, resp);
                }
            }
        }

        for(int i = 0; i < lobby.getFinalNumberOfPlayers(); i++){
            switch (i){
                case 0 -> {
                    ServerResponse rsp = new ServerResponse();
                    RocketshipBuildingController rbc = assemblyControllers.get(RED);
                    rsp.addDelta(new RocketshipDelta(RED, rbc.returnStartBoard()));
                    rsp.addDelta(new HandDelta(RED, new EmptyComponent()));
                    Component[] res = new Component[2];
                    res[0] = new EmptyComponent();
                    res[1] = new EmptyComponent();
                    rsp.addDelta(new ReservedDelta(RED, res));
                    lobby.notifyAllPlayers(rsp);
                }
                case 1 -> {
                    ServerResponse rsp = new ServerResponse();
                    RocketshipBuildingController rbc = assemblyControllers.get(BLUE);
                    rsp.addDelta(new RocketshipDelta(BLUE, rbc.returnStartBoard()));
                    rsp.addDelta(new HandDelta(BLUE, new EmptyComponent()));
                    Component[] res = new Component[2];
                    res[0] = new EmptyComponent();
                    res[1] = new EmptyComponent();
                    rsp.addDelta(new ReservedDelta(BLUE, res));
                    lobby.notifyAllPlayers(rsp);
                }
                case 2 -> {
                    ServerResponse rsp = new ServerResponse();
                    RocketshipBuildingController rbc = assemblyControllers.get(YELLOW);
                    rsp.addDelta(new RocketshipDelta(YELLOW, rbc.returnStartBoard()));
                    rsp.addDelta(new HandDelta(YELLOW, new EmptyComponent()));
                    Component[] res = new Component[2];
                    res[0] = new EmptyComponent();
                    res[1] = new EmptyComponent();
                    rsp.addDelta(new ReservedDelta(YELLOW, res));
                    lobby.notifyAllPlayers(rsp);
                }
                case 3 -> {
                    ServerResponse rsp = new ServerResponse();
                    RocketshipBuildingController rbc = assemblyControllers.get(GREEN);
                    rsp.addDelta(new RocketshipDelta(GREEN, rbc.returnStartBoard()));
                    rsp.addDelta(new HandDelta(GREEN, new EmptyComponent()));
                    Component[] res = new Component[2];
                    res[0] = new EmptyComponent();
                    res[1] = new EmptyComponent();
                    rsp.addDelta(new ReservedDelta(GREEN, res));
                    lobby.notifyAllPlayers(rsp);
                }
            }
        }
    }

    private void pushPeekableCards(){
        ServerResponse rsp = new ServerResponse();
        GameSessionTwo gs = (GameSessionTwo) gameSession;
        rsp.addDelta(new PeekableCardsDelta(gs.getCardsToPeek(1), gs.getCardsToPeek(2), gs.getCardsToPeek(3)));
        lobby.notifyAllPlayers(rsp);
    }

    /**
     * sendBackupGameState() sends to a player the whole game state
     * @param color is the color of the player that needs to receive a backup
     */
    private void sendBackupGameState(PlayerColor color){
        ServerResponse rsp = new ServerResponse();
        rsp.addDelta(new BackupDelta(flightType, gameProgression, gamePhase, gameSession, color));
        GameSessionTwo gs = (GameSessionTwo) gameSession;
        rsp.addDelta(new PeekableCardsDelta(gs.getCardsToPeek(1), gs.getCardsToPeek(2), gs.getCardsToPeek(3)));
        lobby.notifyPlayer(color, rsp);
    }
}