package it.polimi.ingsw.client;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.commands.userRequest.*;
import it.polimi.ingsw.commands.userRequest.moves.*;
import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.network.client.ServerHandler;
import it.polimi.ingsw.network.client.RMIServerHandler;
import it.polimi.ingsw.network.client.SocketServerHandler;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import static it.polimi.ingsw.enums.FlightType.TWO;


public class ClientController {

    private UIType uiType;
    private ServerHandler serverHandler;
    private ViewModel vm;
    private View view;

    private LinkedBlockingQueue<String> readerQueue;

    private PipedOutputStream pos;
    private PipedInputStream pis;
    private GUIScanner scannerGUI;

    private boolean logged = false;

    private Thread heartbeatThread;

    public ClientController(ConnectionType connectionType, UIType uiType){
        vm = new ViewModel(uiType);
        this.readerQueue = new LinkedBlockingQueue<>();

        this.uiType = uiType;

        initializeServerHandler(connectionType);
        try{
            initializeUI(uiType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initializeUserInputManagement();

        heartbeatThread = null;
    }

    public void initializeServerHandler(ConnectionType connectionType) {
        if(connectionType == ConnectionType.RMI) {
            this.serverHandler = new RMIServerHandler(vm);
        } else if (connectionType == ConnectionType.SOCKET) {
            this.serverHandler = new SocketServerHandler(vm);
        }
        (this.serverHandler).startProcessingResponses();
    }

    public void initializeUI(UIType uiType) throws IOException {
        if (uiType == UIType.TUI) {
            this.view = new TUI(vm);
            vm.addView(view);
        } else{
            pos = new PipedOutputStream();
            pis = new PipedInputStream(pos);

            scannerGUI = new GUIScanner(pis);

            GUIApp.setController(this);
            GUIApp.setWriter(new PrintWriter(pos, true));
            new Thread(() -> {
                GUIApp.launchGUI();
            }).start();
        }
    }

    public void setGUI(View G){
        this.view = G;
        vm.addView(view);
    }

    public ViewModel getViewModel(){
        return vm;
    }

    public View getView(){
        return view;
    }

    /**
     * isLoggedIn() is called to know if the user is actually registered and logged in the lobby. It is useful to render a new
     * screen on the GUI
     */
    public boolean isLoggedIn(){
        return serverHandler.isLoggedIn();
    }

    /**
     * isInGame() is called to know if the user is actually in a game.
     */
    public boolean isInGame(){
        return serverHandler.isInGame();
    }

    /**
     * isWaiting() is called to know if a user is already in a game and is waiting for it to start soon
     */
    public boolean isWaiting(){
        return serverHandler.isWaitingForStart();
    }

    /**
     * getAvailableGames() returns a list containing all the ongoing games with their relative information
     */
    public List<String[]> getAvailableGames(){
        return serverHandler.getAvailableGames();
    }


    /** [] LA PORZIONE CHE SEGUE POTREBBE ESSERE PARTE DELLA TUI E NON DEL CONTROLLER
     * - INOLTRE IL CONTENUTO SPECIFICO CHE VIENE STAMPATO A SCHERMO E', PER ORA, SOLO VOLTO A TESTARE IL MUTLITHREADING E COME QUESTO INTERAGISCE CON LA COMMAND LINE. NON HA ANCORA A CHE FARE CON LE FUNZIONALITA' DI GIOCO
     */
        // -- tuttavia non sarebbe male trovare un modo per fare convergere TUI e GUI
    public void initializeUserInputManagement() {
        // INPUT READER THREAD -- OF PRODUCER KIND

        // INPUT PROCESSOR prende il lock su oggetto in comune con INPUT PROCESSOR THREAD,
        // poi input processor thread lo prende, e INPUT READER THREAD può andare avanti solo
        // quando INPUT PROCESSOR THREAD lo rilascia

        if(uiType.equals(UIType.TUI)){
            Thread userInputReader = new Thread(() -> {
                try (Scanner scanner = new Scanner(System.in)) {

                    int inputCounter = 1;
                    //System.out.println(inputCounter + ") Please enter a string: ");
                    System.out.println("USER INPUT: ");

                    while (!Thread.currentThread().isInterrupted()) {



                        if (scanner.hasNextLine()) {
                            String input = scanner.nextLine();
                            readerQueue.add(input);
                        }

                        inputCounter++;
                        //System.out.println(inputCounter + ") Please enter a string: ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "UserInputReader");

            userInputReader.start();
        }
        else{
            Thread userInputReader = new Thread(() -> {

                while (!Thread.currentThread().isInterrupted()) {

                    if (!scannerGUI.hasNextLine()) {
                        return;
                    }

                    String input = scannerGUI.nextLine();
                    readerQueue.add(input);

                }

            }, "UserInputReader");

            userInputReader.start();
        }



        // INPUT PROCESSOR THREAD -- OF CONSUMER KIND
        Thread inputProcessor = new Thread(() -> {
            while (true) {
                try {
                    String input = readerQueue.take();  // Blocks until input is available
                    //System.out.println("Received: " + input);
                    processUserInput(input);

                } catch (InterruptedException e) {
                    System.err.println("Polling was interrupted.");
                    break;
                }
            }
        });

        inputProcessor.start();
    }


    public void processUserInput(String inputToProcess) {
        String[] inputArgs = inputToProcess.split(" ");
        String commandType = inputArgs[0];
        // System.out.println("Command of type " + commandType + " received");

        if(commandType.equals("CONNECT")) {
            // [] actually, here will be a call to a method that checks whether the client is already connected to a server
            // [] also, it will TRY to connect, and handle cases of connection that are not successuful, or potentially of automatic reconnection
            String serverIP = inputArgs[1];
            if (serverIP.equals("LOCAL")) {
                String localMachineServerIP = Constants.localMachineServerIP;
                connect(localMachineServerIP);
            } else if (Constants.isValidIPv4(serverIP)) {
                connect(serverIP);
            } else {
                System.out.println("Invalid IP");
            }
        } else if (isConnected()) {
            switch (commandType) {
                case "REGISTER" -> {
                    if (inputArgs.length == 3) {
                        String username = inputArgs[1];
                        String password = inputArgs[2];

                        register(username, password);
                    }
                }
                case "LOGIN" -> {
                    if (inputArgs.length == 3) {
                        String username = inputArgs[1];
                        String password = inputArgs[2];

                        login(username, password);
                    }
                }
                case "LOGOUT" -> {
                    logout();
                }
                case "CREATE_NEW_GAME" -> { //[] spostare i controlli sul valore lato server
                    if(inputArgs.length == 3) {
                        String stringFlightType = inputArgs[1];
                        String stringNumberOfPlayers = inputArgs[2];

                        if(stringFlightType.equals("TRIAL") || stringFlightType.equals("TWO")) {
                            FlightType flightType = null;

                            if(stringFlightType.equals("TRIAL")) {
                                //System.out.println("selected trial");
                                flightType = FlightType.TRIAL;
                            } else if (stringFlightType.equals("TWO")) {
                                //System.out.println("selected level two");
                                flightType = TWO;
                            }

                            if(Constants.isNumeric(stringNumberOfPlayers)) {
                                int numberOfPlayers = Integer.parseInt(stringNumberOfPlayers);
                                if ((numberOfPlayers == 2) || (numberOfPlayers == 3) || (numberOfPlayers == 4)) {
                                    createNewGame(flightType, numberOfPlayers);
                                } else {
                                    System.out.println("Invalid number of players. Choose 2, 3 or 4");
                                }
                            } else {
                                System.out.println("Expecting *number* of players as second parameter");
                            }
                        } else {
                            System.out.println("Invalid flight type");
                        }
                    }
                }
                case "FETCH_AVAILABLE_GAMES" -> {
                    fetchAvailableGames();
                }
                case "JOIN_GAME" -> {
                    if(inputArgs.length == 2) {
                        String gameID = inputArgs[1];
                        joinGame(gameID);
                    } else {
                        System.out.println("Expecting Game ID as parameter");
                    }
                }
                case "FETCH_UNTURNED" -> {
                    fetchUnturnedComponent();
                }
                case "FETCH_TURNED" -> {
                    if(inputArgs.length == 2) {
                        String indexChoice = inputArgs[1];
                        int index = Integer.parseInt(indexChoice) - 1;
                        if(index + 1 > 0){
                            try{
                                String ID = vm.getTurnedID(index);
                                fetchTurnedComponent(ID);
                            } catch (RuntimeException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        else{
                            System.out.println("Index should be greater than zero");
                        }
                    } else {
                        System.out.println("Invalid choice. One parameter required");
                    }
                }
                case "FETCH_RESERVED" -> {
                    if(inputArgs.length == 2) {
                        String stringChoice = inputArgs[1];

                        if(Constants.isNumeric(stringChoice)) {
                            int choice = Integer.parseInt(stringChoice);
                            if(choice == 1 || choice == 2) {
                                fetchFromReserved(choice);
                            } else {
                                System.out.println("Choice of component from reserved must either be 1 or 2");
                            }
                        } else {
                            System.out.println("Parameter must be a number");
                        }
                    } else {
                        System.out.println("This command requires one numeric parameter");
                    }
                }
                case "ROTATE" -> {
                    rotateComponent();
                }
                case "RESERVE" -> {
                    putInReserved();
                }
                case "REJECT" -> {
                    rejectComponent();
                }
                case "PLACE" -> {
                    if(inputArgs.length == 3) {
                        String stringRow = inputArgs[1];
                        String stringCol = inputArgs[2];

                        if(Constants.isNumeric(stringRow)) {
                            if(Constants.isNumeric(stringCol)) {
                                int row = Integer.parseInt(stringRow);
                                int col = Integer.parseInt(stringCol);

                                placeComponent(row - 5, col - 4);
                            } else {
                                System.out.println("Expecting column *number* as second parameter");
                            }
                        } else {
                            System.out.println("Expecting row *number* as first parameter");
                        }
                    } else {
                        System.out.println("You need one argument for the row, and another for the column");
                    }
                }
                case "DETACH" -> {
                    if(inputArgs.length == 3) {
                        String stringRow = inputArgs[1];
                        String stringCol = inputArgs[2];

                        if(Constants.isNumeric(stringRow)) {
                            if(Constants.isNumeric(stringCol)) {
                                int row = Integer.parseInt(stringRow);
                                int col = Integer.parseInt(stringCol);

                                if(row == 7 && col == 7){
                                    System.out.println("You can't remove the start cabin");
                                }
                                else{
                                    detachComponent(row - 5, col - 4);
                                }
                            } else {
                                System.out.println("Expecting column *number* as second parameter");
                            }
                        } else {
                            System.out.println("Expecting row *number* as first parameter");
                        }
                    } else {
                        System.out.println("You need one argument for the row, and another for the column");
                    }
                }
                case "FORFEIT" -> {
                    forfeit();
                }
                case "FLIP_HOURGLASS" -> {
                    flipHourglass();
                }
                case "READY_FOR_TAKEOFF" -> {
                    readyForTakeOff();
                }
                case "QUIT_GAME" -> {
                    quitGame();
                }
                case "SWITCH_VIEW" -> {
                    vm.switchView();
                }
                case "VIEW" -> {
                    GamePhase phase = vm.getGamePhase();
                    if(phase == GamePhase.ASSEMBLY_PHASE) {
                        if(inputArgs.length == 1) {
                            vm.viewMyRocketship();
                        } else if (inputArgs.length == 2) {
                            vm.viewRocketship(inputArgs[1]);
                        }
                    } else {
                        if(inputArgs.length == 1) {
                            requestUpdatedRocketship(vm.getMyColor());
                        } else if (inputArgs.length == 2) {
                            PlayerColor requestedColor = null;
                            switch(inputArgs[1]) {
                                case "RED" -> {
                                    requestedColor = PlayerColor.RED;
                                }
                                case "BLUE" -> {
                                    requestedColor = PlayerColor.BLUE;
                                }
                                case "YELLOW" -> {
                                    requestedColor = PlayerColor.YELLOW;
                                }
                                case "GREEN" -> {
                                    requestedColor = PlayerColor.GREEN;
                                }
                            }
                            
                            if(requestedColor != null && vm.checkColorInGame(requestedColor)) {
                                requestUpdatedRocketship(requestedColor);
                            } else {
                                System.out.println("There's no such color.");
                            }

                        }
                    }

                }
                case "CHOOSE" -> {
                    if(inputArgs.length == 2) {
                        String stringChoice = inputArgs[1];

                        if(Constants.isNumeric(stringChoice)) {
                            int choice = Integer.parseInt(stringChoice);
                            makeFlightChoice(choice);
                        } else {
                            System.out.println("Parameter must be a number");
                        }
                    } else {
                        System.out.println("This command requires one numeric parameter");
                    }
                }
                case "PEEK" -> {
                    if(vm.getFlightType().equals(TWO)){
                        if(inputArgs.length == 2) {
                            String stringChoice = inputArgs[1];

                            if(Constants.isNumeric(stringChoice)) {
                                int choice = Integer.parseInt(stringChoice);
                                if(choice > 0 && choice < 4){
                                    vm.displayCardStack(choice);
                                }
                                else System.out.println("Number must be between one and three");
                            } else {
                                System.out.println("Parameter must be a number");
                            }
                        } else {
                            System.out.println("This command requires one numeric parameter");
                        }
                    }
                    else System.out.println("This is a trial flight, you can't peek cards");
                }
                case "STOP_PEEKING" -> {
                    if(vm.getFlightType().equals(TWO)){
                        vm.stopPeeking();
                    }
                    else System.out.println("This is a trial flight, you can't peek cards");
                }
                case "POPULATE" -> {
                    boolean correctInput = true;

                    if(inputArgs.length == 1) {
                        System.out.println("please select at least one parameter");

                    } else {
                        for(int i = 1; i<inputArgs.length; i++) {
                            String s = inputArgs[i];
                            if(s.equals("HUMAN") || s.equals("PURPLE") || s.equals("BROWN")) {

                            } else {
                                correctInput = false;
                            }
                        }

                        if(correctInput) {
                            int firstSpace = inputToProcess.indexOf(" ");
                            String result = (firstSpace != -1) ? inputToProcess.substring(firstSpace + 1) : "";
                            populateShip(result);
                        } else {
                            System.out.println("Please select correct input");
                        }
                    }



                }
                default -> {
                    System.out.println("Invalid command");
                }
            }
        } else {
            System.out.println("connect to a server before sending commands");
        }

    }


    public boolean isConnected() {
        return serverHandler.isConnected();
    }


    public void onDisconnect() { // [] non sono sicuro vada bene
        stopHeartbeatGeneration();
    }


    public void startHeartbeatGeneration() {
        heartbeatThread = new Thread (() -> {generateHeartbeat();});
        heartbeatThread.start();
    }

    public void stopHeartbeatGeneration() {
        heartbeatThread.interrupt();
        heartbeatThread = null;
    }

    public void generateHeartbeat() {
        while (true) {
            try {
                Thread.currentThread().sleep(Constants.heartbeatGenerationIntervalInMs);
                ping();
            } catch (InterruptedException e) {
                // do nothing
            }
        }

    }





    /** CONNECTION COMMAND **/

    public void connect(String ipToConnectTo) {
        serverHandler.connect(ipToConnectTo);

        if(isConnected()) {
            startHeartbeatGeneration();
        }

    }


    /** SESSION MANAGEMENT COMMANDS **/

    public void register(String username, String password) {
        //System.out.println("sent request to register the username " + username);
        UserSessionManagementRequest registrationRequest = UserRequestFactory.createRegistrationCommand(username, password);
        serverHandler.register(registrationRequest);
    }

    public void login(String username, String password) {
        UserSessionManagementRequest loginRequest = UserRequestFactory.createLoginCommand(username, password);
        serverHandler.login(loginRequest);
    }

    public void logout() {
        UserSessionManagementRequest logoutRequest = UserRequestFactory.createLogoutCommand();
        serverHandler.logout(logoutRequest);
    }

    public void ping() {
        UserSessionManagementRequest pingRequest = UserRequestFactory.createPingCommand();
        serverHandler.ping(pingRequest);
    }


    /** GAME MANAGEMENT COMMANDS **/

    public void createNewGame(FlightType flightType, int numberOfPlayers) {
        System.out.println("creating new game request of type " + flightType + " with " + numberOfPlayers + " number of players");
        UserGameManagementRequest createNewGameRequest = UserRequestFactory.createNewGameRequest(flightType, numberOfPlayers);
        serverHandler.createNewGame(createNewGameRequest);
    }

    public void fetchAvailableGames() {
        UserGameManagementRequest fetchAvailableGamesRequest = UserRequestFactory.createFetchAvailableGamesRequest();
        serverHandler.fetchAvailableGames(fetchAvailableGamesRequest);
    }

    public void joinGame(String gameID) {
        UserGameManagementRequest joinGameRequest = UserRequestFactory.createJoinGameRequest(gameID);
        serverHandler.joinGame(joinGameRequest);
    }






    /**
     * FetchFromReserved() creates a UserRequest indicating the move that the players wants to do
     * @param choice is the reserved component to draw
     */
    public void fetchFromReserved(int choice){
        FetchFromReservedMove m = MoveFactory.createFetchFromReservedMove(choice);
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    /**
     * FetchTurnedComponent() creates a UserRequest indicating which turned components the player wants to pick up
     * @param ID is the ID of the selected component
     */
    public void fetchTurnedComponent(String ID){
        FetchTurnedComponentMove m = MoveFactory.createFetchTurnedComponentMove(ID);
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    /**
     * FetchUnturnedComponent() creates a UserRequest indicating that the user wants to pick a casual component
     */
    public void fetchUnturnedComponent(){
        FetchUnturnedComponentMove m = MoveFactory.createFetchUnturnedComponentMove();
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    /**
     * placeComponent() creates a UserRequest indicating that the user wants to place the component he's holding
     * @param row is the row number of the board on which the component is going to be placed
     * @param col is the column number of the board on which the component is going to be placed
     */
    public void placeComponent(int row, int col){
        PlaceComponentMove m = MoveFactory.createPlaceComponentMove(row, col);
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    /**
     * detachComponent() creates a UserRequest indicating that the user wants to remove a component from the ship
     * @param row is the row number of the place that contains the component
     * @param col is the column number of the place that contains the component
     */
    public void detachComponent(int row, int col){
        DetachComponentMove m = MoveFactory.createDetachComponentMove(row, col);
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    /**
     * PutInReserved() creates a UserRequest indicating that the user wants to put in his reserve the component he's holding
     */
    public void putInReserved(){
        PutInReservedMove m = MoveFactory.createPutInReservedMove();
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    /**
     * ReadyForTakeOff() creates a UserRequest indicating that the user is ready for takeoff
     */
    public void readyForTakeOff(){
        ReadyForTakeOffMove m = MoveFactory.createReadyForTakeOffMove();
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    /**
     * RotateComponent() creates a UserRequest indicating that the user wants to rotate clockwise the component he's holding
     * in his hand
     */
    public void rotateComponent(){
        RotateComponentMove m = MoveFactory.createRotateComponentMove();
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    /**
     * RejectComponent() creates a UserRequest indicating that the user wants to give back his hand component
     */
    public void rejectComponent(){
        ComponentRejectedMove m = MoveFactory.createComponentRejectedMove();
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    public void flipHourglass() {
        FlipHourglassMove m = MoveFactory.createFlipHourglassMove();
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    public void quitGame() {
        QuitGameMove m = MoveFactory.createQuitGameMove();
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    public void populateShip(String choice){
        PopulateShipMove m = MoveFactory.createPopulateShipMove(choice);
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }





    public void makeFlightChoice(int choice) {
        FlightChoiceMove m = MoveFactory.createFlightChoiceMove(choice);
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    public void forfeit() {
        ForfeitMove m = MoveFactory.createForfeitMove();
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }

    public void requestUpdatedRocketship(PlayerColor color) {
        RequestUpdatedRocketshipMove m = MoveFactory.createRequestUpdatedRocketshipMove(color, vm.getGamePhase());
        serverHandler.makeGameMove(UserRequestFactory.createMoveRequest(m));
    }


}