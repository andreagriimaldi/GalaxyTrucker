package it.polimi.ingsw.network.client;

import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.commands.serverResponse.ServerResponse;
import it.polimi.ingsw.commands.userRequest.UserGameManagementRequest;
import it.polimi.ingsw.commands.userRequest.UserGameMoveRequest;
import it.polimi.ingsw.commands.userRequest.UserSessionManagementRequest;
import it.polimi.ingsw.commands.serverResponse.deltas.Delta;
import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.model.playerset.PlayerColor;


import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class ServerHandler {

    // [ ] IN TEORIA NON SERVE NEMMENO LA RESPONSE QUEUE PERCHE', A DIFFERENZA DEL SERVER CHE NON DEVE GESTIRE PIU CLIENT, IL CLIENT RICEVE SOLO UNA COSA DAL SERVER
    // PERO' POTREBBE ESSERE COMUNQUE UTILE PER RENDERE IL CLIENT PIU RESPONSIVO, IDK

    private LinkedBlockingQueue<ServerResponse> responseQueue;
    private boolean connected;
    private boolean logged;
    private boolean inGame;
    private boolean isWaiting;

    private List<String[]> availableGames = new LinkedList<>();

    private final ViewModel viewModel;


    public ServerHandler(ViewModel viewModel) {
        this.responseQueue = new LinkedBlockingQueue<ServerResponse>();
        this.connected = false;
        this.logged = false;
        this.inGame = false;
        this.isWaiting = false;

        this.viewModel = viewModel;
    }



    public void notifyUser(ServerResponse receivedResponse) {
        addResponseToQueue(receivedResponse);
    }

    public void addResponseToQueue(ServerResponse response) {
        //System.out.println("received response and added it to queue");
        responseQueue.add(response);
    }

    public void startProcessingResponses() {
        Thread responseProcessor = new Thread(() -> {
            //System.out.println("started response processing loop");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ServerResponse responseToProcess = responseQueue.take(); /** BLOCKING READ **/
                    //System.out.println("processing response");

                    String log = responseToProcess.getLog();
                    if (log != null) {

                        System.out.println(log);
                        viewModel.addLogEntry(log);
                        viewModel.setLastLog(log);

                        if(log.contains("Login Successful.")){
                            onLogin();
                        }

                        if(log.contains("dissociated from game")){
                            backToLogin();
                        }

                        if(log.contains("Initializing game")){
                            String[] gamesToParse = log.split("\\r?\\n");

                            List<String[]> games = new LinkedList<>();
                            for(String gameToParse : gamesToParse){
                                String[] game = parse(gameToParse);
                                games.add(game);
                            }
                            addAvailableGames(games);
                        }

                        if(log.contains("please join running game")){
                            String regex = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(log);

                            if (matcher.find()) {
                                String game = matcher.group();
                                viewModel.reconnectToGame(game);
                            }
                        }

                        if(log.contains("associated with game")){
                            onWaiting();
                        }

                        if(log.contains("GAME INITIALIZED") || log.contains("SUCCESSFULLY RE-JOINED GAME")){
                            onJoiningGame();
                        }

                        if(log.contains("Now it's time for you to decide who gets to participate in this cosmic trip!")){
                            viewModel.setToPopulate();
                        }

                        if(log.contains("The ship has now its crew, it's time to fly")){
                            viewModel.setPopulated();
                        }

                        if(log.contains("Your rocketship is ready for takeoff")){
                            viewModel.setFixedShip();
                            viewModel.setFinalNumberOfPlayers();
                        }

                        if(log.contains("Your rocketship is not compliant with the rules, you must remove some components")){
                            viewModel.setNotCompliant();
                        }

                        if(log.contains("left... starting timer")){
                            viewModel.setPaused();
                        }

                        if(log.contains("ha terminato l'assemblaggio") && log.contains(viewModel.getMyColor().toString())){
                            viewModel.assemblyOver();
                        }

                        if(log.contains("Countdown interrupted")){
                            viewModel.setNotPaused();
                        }

                    }

                    Map<DeltaType, Delta> deltas = responseToProcess.getDeltas();

                    if (deltas != null && !deltas.isEmpty()) {
                        viewModel.updateView(deltas);
                    }

                } catch (InterruptedException e) {
                    System.err.println("Response processing thread interrupted, stopping.");
                    Thread.currentThread().interrupt(); // Re-set interrupt flag
                } catch (Exception e) {
                    e.printStackTrace(); // Optional: better error handling/logging
                }
            }
        }, "ResponseProcessor");
        responseProcessor.start();
    }

    public abstract void connect(String ipToConnectTo);

    public void setToConnected() {
        connected = true;
    }

    public void onConnect() {
        setToConnected();
    }

    public abstract void disconnect();

    public void setToDisconnected() {
        connected = false;
    }

    public void onDisconnect() {

        setToDisconnected();

        // [ ] cosa faccio con ViewModel e con ClientController e con il main / con la TUI?

        /**
         *
         *
         *
         *
         */

    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isLoggedIn(){
        return logged;
    }

    private void onLogin(){
        logged = true;
    }

    public boolean isWaitingForStart(){
        return isWaiting;
    }

    private void onWaiting(){
        isWaiting = true;
    }

    public boolean isInGame(){
        return inGame;
    }

    private void backToLogin(){
        isWaiting = false;
        inGame = false;
    }

    private void onJoiningGame(){
        inGame = true;
    }

    /**
     * getAvailableGames() returns a list containing all the ongoing games with their relative information
     */
    public List<String[]> getAvailableGames(){
        return availableGames;
    }

    /**
     * addAvailableGame() is useful to add new games to the list of available ones
     */
    private void addAvailableGames(List<String[]> games){
        availableGames = games;
    }

    /**
     * parse() extracts the info about a game
     * @return an array of Strings, containing the UUID, the number of connected and maximum players and finally
     * the level of the game
     *
     */
    public String[] parse(String input){
        Pattern pattern = Pattern.compile(
                ".*ID:\\s*([^ ]+)\\s*\\[(\\d+)/(\\d+)\\s*connected\\]\\s*level:\\s*(\\w+).*"
        );
        Matcher matcher = pattern.matcher(input);

        if(!matcher.matches()){
            return new String[4];
        }

        String extractedId = matcher.group(1);
        int connected = Integer.parseInt(matcher.group(2));
        String connectedNum = new String();
        switch(connected){
            case 0 -> {
                connectedNum = "0";
            }
            case 1 -> {
                connectedNum = "1";
            }
            case 2 -> {
                connectedNum = "2";
            }
            case 3 -> {
                connectedNum = "3";
            }
            case 4 -> {
                connectedNum = "4";
            }
        }
        int totalSlots = Integer.parseInt(matcher.group(3));
        String slots = new String();
        switch(totalSlots){
            case 0 -> {
                slots = "0";
            }
            case 1 -> {
                slots = "1";
            }
            case 2 -> {
                slots = "2";
            }
            case 3 -> {
                slots = "3";
            }
            case 4 -> {
                slots = "4";
            }
        }
        String level = matcher.group(4);

        String[] game = new String[4];
        game[0] = extractedId;
        game[1] = connectedNum;
        game[2] = slots;
        game[3] = level;
        return game;
    }




    /** SESSION MANAGEMENT **/

    public abstract void register(UserSessionManagementRequest registrationRequest);
    public abstract void login(UserSessionManagementRequest loginRequest);
    public abstract void logout(UserSessionManagementRequest logoutRequest);

    public abstract void ping(UserSessionManagementRequest pingRequest);


    /** GAME MANAGEMENT **/

    public abstract void createNewGame(UserGameManagementRequest createNewGameRequest);
    public abstract void joinGame(UserGameManagementRequest joinGameRequest);
    public abstract void fetchAvailableGames(UserGameManagementRequest fetchAvailableGamesRequest);
    public abstract void quitGame(UserGameManagementRequest quitGameRequest);


    /** IN-GAME MOVES **/

    public abstract void makeGameMove(UserGameMoveRequest gameMoveRequest);

}