package it.polimi.ingsw.client;

import it.polimi.ingsw.client.Screens.*;
import it.polimi.ingsw.commands.serverResponse.deltas.BackupDelta;
import it.polimi.ingsw.commands.serverResponse.deltas.RankingDelta;
import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.adventure.AdventureCard;
import it.polimi.ingsw.model.utilities.components.Component;
import it.polimi.ingsw.model.utilities.components.EmptyComponent;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.net.URL;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static it.polimi.ingsw.enums.FlightType.TWO;

public class GUI extends View{

    private int Width;
    private int Height;

    private ClientController controller;

    private PrintWriter writer;
    private Stage stage;
    private LoginScreen login;
    private AssemblyScreen assembly;
    private FlightScreen flight;
    private TakeoffScreen takeoff;
    private PauseScreen pause;
    private EndScreen end;

    private boolean displayable = false;

    private boolean fixedShip = false;

    private boolean paused = false;

    private boolean toPopulate = false;
    private boolean populated = false;

    private boolean aOver = false;

    private FlightType type;

    private Component[][] myBoard;
    private Component myHandComponent;
    private Component[] myReservedComponents;

    private final Map<PlayerColor, Component[][]> otherBoards;
    private final Map<PlayerColor, Component> otherHand;
    private final Map<PlayerColor, Component[]> otherReserved;

    private PlayerColor color;
    private PlayerColor currentShip;
    private final List<PlayerColor> otherColors;

    private List<Component> turnedComponents;

    private List<AdventureCard> cardsOne;
    private List<AdventureCard> cardsTwo;
    private List<AdventureCard> cardsThree;

    private boolean peeking = false;
    private int peekchoice;

    private List<String> aliensChoices = new LinkedList<>();

    private List<PlayerColor> flightBoard;

    private GamePhase phase;
    private GameProgression progression;

    private String lastLog;

    private final int maxrow = 5;
    private final int maxcol = 7;

    private URL currentCardURL;

    private boolean renderRS;

    private int finalNumberOfPlayers;


    private final BlockingQueue<String> pendingLogs = new LinkedBlockingQueue<>();


    private boolean flightBoardCreated;

    private Map<PlayerColor, double[]> finalScore = new HashMap<>();

    public GUI(ViewModel vm, Stage stage, PrintWriter writer, ClientController c, int W, int H){
        vm.addObserver(this);
        this.writer = writer;
        this.stage = stage;
        this.controller = c;
        stage.setTitle("GalaxyTrucker");

        myBoard = new Component[maxrow][maxcol];
        myReservedComponents = new Component[2];
        myHandComponent = null;

        otherColors = new LinkedList<>();
        otherBoards = new HashMap<>();
        otherHand = new HashMap<>();
        otherReserved = new HashMap<>();

        turnedComponents = new LinkedList<>();

        cardsOne = new LinkedList<>();
        cardsTwo = new LinkedList<>();
        cardsThree = new LinkedList<>();

        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                myBoard[i][j] = new EmptyComponent();
            }
        }

        this.Width = W;
        this.Height = H;
        this.flightBoardCreated = false;
    }

    public void initializeView(){
        setupScreens();
        showLogin();
    }

    /**
     * setupScreens() proceeds to initializes all the different kind of screens
     */
    private void setupScreens(){
        login = new LoginScreen(this, writer, controller, Width, Height);
        assembly = new AssemblyScreen(stage,this, writer, controller, Width, Height);
        flight = new FlightScreen(stage, this, writer, controller, Width, Height);
        takeoff = new TakeoffScreen(stage,this, writer, controller, Width, Height);
        pause = new PauseScreen(stage,this,writer,controller,Width,Height);
        end = new EndScreen(stage,this,writer,controller,Width,Height);
    }

    /**
     * showLogin() is used to show on screen the login view
     */
    public void showLogin(){
        stage.setScene(login.getScene());
        stage.setFullScreen(true);
        stage.show();
    }

    public void showGames(){
        login.setupGames();
        stage.setScene(login.getScene());
        stage.setFullScreen(true);
        stage.show();
    }

    public void showAssembly(){
        stage.setScene(assembly.getScene());
        stage.setFullScreen(true);
        stage.show();
    }

    public void showTakeoff(){
        stage.setScene(takeoff.getScene());
        stage.setFullScreen(true);
        stage.show();
    }

    public void showFlight(){
        stage.setScene(flight.getScene());
        stage.setFullScreen(true);
        stage.show();
    }

    public void startPaused(){
        stage.setScene(pause.getScene());
        stage.setFullScreen(true);
        stage.show();
    }

    public void showEnd(){
        stage.setScene(end.getScene());
        stage.setFullScreen(true);
        stage.show();
    }

    /**
     * getAvailableGames() returns a List of Strings' array containing infos about the ongoing games
     */
    public List<String[]> getAvailableGames(){
        return controller.getAvailableGames();
    }

    public Stage getStage(){
        return stage;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(!(arg instanceof UpdateMessage)) return;

        switch(((UpdateMessage) arg).getType()){
            case TURNED_DELTA -> {
                List<Component> newturned = (List<Component>) ((UpdateMessage) arg).getData();
                renderTurnedComponents(newturned);
                updateGUI();
            }
            case HAND_DELTA -> {
                Component newhand = (Component) ((UpdateMessage) arg).getData();

                if(((UpdateMessage) arg).toOtherPlayer()){
                    renderOtherHandComponent(newhand, ((UpdateMessage) arg).getPlayerColor());
                }
                else{
                    renderMyHandComponent(newhand);
                }
                updateGUI();

            }
            case RESERVED_DELTA -> {
                Component[] newreserved = (Component[]) ((UpdateMessage) arg).getData();

                if(((UpdateMessage) arg).toOtherPlayer()){
                    renderOtherReservedComponents(newreserved, ((UpdateMessage) arg).getPlayerColor());
                }
                else{
                    renderMyReservedComponents(newreserved);
                }
                updateGUI();

            }
            case ROCKETSHIP_DELTA -> {
                Component[][] newboard = (Component[][]) ((UpdateMessage) arg).getData();

                if(!getPhase().equals(GamePhase.FLIGHT_PHASE)){
                    if(((UpdateMessage) arg).toOtherPlayer()){
                        System.out.println(((UpdateMessage) arg).getPlayerColor());
                        renderOtherRocketshipBoard(newboard, ((UpdateMessage) arg).getPlayerColor());
                    }
                    else{
                        renderMyRocketshipBoard(newboard);
                    }
                }
                else{
                    if(((UpdateMessage) arg).toOtherPlayer()) {
                        System.out.println("other player");
                        PlayerColor rsColor = ((UpdateMessage) arg).getPlayerColor();
                        if(otherColors.contains(rsColor)) {
                            currentShip = rsColor;
                        }
                    } else {
                        System.out.println("yours");
                        currentShip = color;
                    }
                    renderRS = true;
                }
                updateGUI();
            }
            case PHASE_DELTA -> {
                GamePhase newPhase = (GamePhase) ((UpdateMessage) arg).getData();
                renderPhase(newPhase);
                updateGUI();
            }
            case PROGRESSION_DELTA -> {
                GameProgression newProg = (GameProgression) ((UpdateMessage) arg).getData();
                renderProgression(newProg);
                updateGUI();
            }
            case FLIGHT_TYPE_DELTA -> {
                FlightType t = (FlightType) ((UpdateMessage) arg).getData();
                this.type = t;
                updateGUI();
            }
            case FLIGHT_BOARD_DELTA -> {
                List<PlayerColor> fbd = (List<PlayerColor>) ((UpdateMessage) arg).getData();
                this.flightBoard = fbd;
                flightBoardCreated = true;
                updateGUI();
            }
            case VIEW_DELTA -> {
                nextShip();
                updateGUI();
            }
            case COLOR_DELTA -> {
                PlayerColor color = (PlayerColor) ((UpdateMessage) arg).getData();
                setColor(color);
                currentShip = color;
                updateGUI();
            }
            case OTHER_COLOR_DELTA -> {
                PlayerColor otherColor = (PlayerColor) ((UpdateMessage) arg).getData();
                addOtherColor(otherColor);
                otherBoards.put(otherColor, null);
                otherHand.put(otherColor, null);
                otherReserved.put(otherColor, null);
                updateGUI();
            }
            case ADVENTURE_DELTA -> {
                AdventureCard drawnCard = (AdventureCard) ((UpdateMessage) arg).getData();
                renderAdventureCard(drawnCard);
                updateGUI();
            }
            case PEEKABLE_CARDS_DELTA -> {
                List<List<AdventureCard>> cards = (List<List<AdventureCard>>) ((UpdateMessage) arg).getData();
                cardsOne = cards.get(0);
                cardsTwo = cards.get(1);
                cardsThree = cards.get(2);
                updateGUI();
            }
            case PEEK_A_STACK_DELTA -> {
                peeking = true;
                peekchoice = (int) ((UpdateMessage) arg).getData();
                updateGUI();
            }
            case STOP_PEEKING_DELTA -> {
                peeking = false;
                updateGUI();
            }
            case BACKUP_DELTA -> {
                BackupDelta bd = (BackupDelta) ((UpdateMessage) arg).getData();
                this.type = bd.getFlightType();
                renderProgression(bd.getGameProgression());
                renderPhase(bd.getGamePhase());
                setColor(bd.getMyColor());
                currentShip = bd.getMyColor();
                renderMyRocketshipBoard(bd.getMyBoard());
                renderMyReservedComponents(bd.getMyReserved());
                renderMyHandComponent(bd.getMyHand());
                this.flightBoard = bd.getFlightBoard();
                Set<PlayerColor> colorsBK = bd.getOthersBoards().keySet();
                for (PlayerColor c: colorsBK){
                    addOtherColor(c);
                    renderOtherRocketshipBoard(bd.getOthersBoards().get(c), c);
                    renderOtherReservedComponents(bd.getOthersReserved().get(c), c);
                    renderOtherHandComponent(bd.getOthersHands().get(c), c);
                }

                renderTurnedComponents(bd.getTurnedComponents());

                updateGUI();
            }
        }
    }

    public void updateGUI(){
        if(isPaused()){
            Platform.runLater(() -> pause.updateGUI());
        }
        else{
            if(isDisplayable()){
                switch(getProgression()){
                    case RUNNING_GAME -> {
                        switch(getPhase()){
                            case ASSEMBLY_PHASE -> {
                                if(isAssemblyOver()){
                                    Platform.runLater(() -> showTakeoff());
                                }
                                else{
                                    Platform.runLater(() -> showAssembly());
                                    Platform.runLater(() -> assembly.updateGUI());
                                }
                            }
                            case TAKEOFF_PHASE -> {
                                Platform.runLater(() -> showTakeoff());
                                Platform.runLater(() -> takeoff.updateGUI());
                            }
                            case FLIGHT_PHASE -> {
                                Platform.runLater(() -> showFlight());
                                Platform.runLater(() -> flight.updateGUI());
                            }
                            case END_FLIGHT_PHASE -> {
                                Platform.runLater(() -> showEnd());
                                Platform.runLater(() -> end.updateGUI());
                            }
                        }
                    }
                }
            }
            else{
                if(color != null && !otherColors.isEmpty() && !(myHandComponent == null) && type != null){
                    if(type.equals(TWO)){
                        if(!cardsOne.isEmpty()){
                            setDisplayable();
                            updateGUI();
                        }
                    }
                    else{
                        setDisplayable();
                        updateGUI();
                    }
                }
            }
        }
    }

    //GETTER METHODS
    public Component[][] getMyRocketshipBoard(){
        return myBoard;
    }

    public Component[][] getOtherRocketshipBoard(PlayerColor color){
        return otherBoards.get(color);
    }

    public Component getMyHandComponent(){
        return myHandComponent;
    }

    public Component getOtherHandComponent(PlayerColor color){
        return otherHand.get(color);
    }

    public Component[] getMyReservedComponents(){
        return myReservedComponents;
    }

    public Component[] getOtherReservedComponents(PlayerColor color){
        return otherReserved.get(color);
    }

    public List<Component> getTurnedComponents(){
        return turnedComponents;
    }

    public GamePhase getPhase(){
        return phase;
    }

    public GameProgression getProgression(){
        return progression;
    }

    public PlayerColor getCurrentShip(){
        return currentShip;
    }

    public PlayerColor getColor(){
        return color;
    }

    public List<PlayerColor> getOtherColors(){
        return otherColors;
    }

    public FlightType getType(){
        return type;
    }

    public java.net.URL getCurrentCardURL() {return currentCardURL;}

    public List<PlayerColor> getFlightBoard(){return flightBoard;}

    public boolean getRenderRS(){return renderRS;}

    public void setRenderRs(){renderRS = false;}

    public boolean getFlightBoardCreated() {
        return flightBoardCreated;
    }

    public void pushLog(String msg) {
        pendingLogs.offer(msg);
        if (getPhase() != null && getPhase().equals(GamePhase.FLIGHT_PHASE)) {
            updateGUI();
        }
    }

    public String pollLog() {
        return pendingLogs.poll();
    }

    @Override
    public void renderMyRocketshipBoard(Component[][] b) {
        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j ++){
                myBoard[i][j] = b[i][j];
            }
        }
    }

    @Override
    public void renderOtherRocketshipBoard(Component[][] newboard, PlayerColor color) {
        otherBoards.put(color, newboard);
    }

    @Override
    public void renderMyHandComponent(Component hand) {
        myHandComponent = hand;
    }

    @Override
    public void renderOtherHandComponent(Component hand, PlayerColor color) {
        otherHand.put(color, hand);
    }

    @Override
    public void renderMyReservedComponents(Component[] reserved) {
        myReservedComponents[0] = reserved[0];
        myReservedComponents[1] = reserved[1];
    }

    @Override
    public void renderOtherReservedComponents(Component[] reserved, PlayerColor color) {
        otherReserved.put(color, reserved);
    }

    @Override
    public void renderTurnedComponents(List<Component> t) {
        turnedComponents = t;
    }

    @Override
    public void renderAdventureCard(AdventureCard card) {
        String filename = card.getID() + ".jpg";
        java.net.URL url = getClass().getResource("/it/polimi/ingsw/GUI/cards/" + filename);
        if (url == null) {
            throw new RuntimeException(filename + " not present in /cards/");
        }
        currentCardURL = url;
    }

    @Override
    public void renderPhase(GamePhase p){
        phase = p;
    }

    @Override
    public void renderProgression(GameProgression p){
        progression = p;
    }


    /**
     * setColor() is useful to set the color of the player
     */
    @Override
    public void setColor(PlayerColor color){
        this.color = color;
    }

    /**
     * addOtherColor() is useful to add another color to the list of current players
     */
    @Override
    public void addOtherColor(PlayerColor color){
        if(!otherColors.contains(color)){
            otherColors.add(color);
        }
        else throw new RuntimeException("Color already present");
    }

    /**
     * nextShip() is able to set the new Ship based on the list of other colors and the color of the player himself
     */
    @Override
    public void nextShip(){
        if(otherColors.contains(currentShip)){
            int index = otherColors.indexOf(currentShip);
            if(index == otherColors.size() - 1){
                currentShip = color;
            }
            else{
                currentShip = otherColors.get(index + 1);
            }
        }
        else{
            currentShip = otherColors.getFirst();
        }
    }

    public void backToMyShip(){
        currentShip = color;
        updateGUI();
    }

    public List<AdventureCard> getPeekableCards(int choice){
        switch (choice){
            case 1 -> {
                return cardsOne;
            }
            case 2 -> {
                return cardsTwo;
            }
            case 3 -> {
                return cardsThree;
            }
            default -> throw new RuntimeException("Invalid choice of peekable deck");
        }
    }

    public boolean isDisplayable(){
        return displayable;
    }

    private void setDisplayable(){
        displayable = true;
    }

    public void switchResolution(){
        if(Width == 1280){
            Width = 1920;
            Height = 1080;
        }
        else{
            Width = 1280;
            Height = 720;
        }
        updateGUI();
    }

    public int getWidth(){
        return Width;
    }

    public int getHeight(){
        return Height;
    }

    public void onReconnect(String game){
        write("JOIN_GAME " + game);
    }

    private void write(String s){
        writer.println(s);
        writer.flush();
    }

    public void setLastLog(String log){
        if(log.contains("For the cabin in position")){
            aliensChoices.add(log);
        }
        lastLog = log;
        updateGUI();
    }

    public String getLastLog(){
        return lastLog;
    }

    public boolean isFixedShip(){
        return fixedShip;
    }

    public void setFixedShip(){
        fixedShip = true;
    }

    public boolean isPaused(){
        return paused;
    }

    public void setPaused(){
        paused = true;
    }

    public void setNotPaused(){
        paused = false;
    }

    public void setToPopulate(){
        toPopulate = true;
    }

    public boolean isToPopulate(){
        return toPopulate;
    }

    public void setPopulated(){
        populated = true;
    }

    public boolean isPopulated(){
        return populated;
    }

    public int getNumberOfCabins(){
        int num = 0;
        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                if(myBoard[i][j].getComponentType().equals(ComponentType.SIMPLE_CABIN))
                    num++;
            }
        }
        return num;
    }

    public List<String> getAliensChoices(){
        return new ArrayList<>(aliensChoices);
    }

    public void assemblyOver(){
        aOver = true;
    }

    public boolean isAssemblyOver(){
        return aOver;
    }

    public int getFinalNumberOfPlayers(){
        return finalNumberOfPlayers;
    }

    public void setFinalNumberOfPlayers(int num){
        finalNumberOfPlayers = num;
    }

    public String renderRanking(RankingDelta rankingDelta){
        Map<PlayerColor, Map<CreditsType, Float>> credits = rankingDelta.getCredits();
        List<Map.Entry<PlayerColor, Float>> ranking = rankingDelta.getRanking();

        StringBuilder results = new StringBuilder();
        for (Map.Entry<PlayerColor, Float> entry : ranking) {
            PlayerColor playerColor = entry.getKey();
            float total   = entry.getValue();

            List<CreditsType> orderedTypes = List.of(
                    CreditsType.FLIGHT_CREDITS,
                    CreditsType.PROFIT_FROM_RESOURCES,
                    CreditsType.CONNECTORS_PRIZE,
                    CreditsType.LOST_COMPONENTS_PENALTY,
                    CreditsType.FINISHING_ORDER_BONUS
            );


            results.append(System.lineSeparator());
            results.append(System.lineSeparator() + "computing total credits for " + playerColor + " player");

            Map<CreditsType, Float> playerCredits = credits.get(playerColor);

            for (CreditsType type : orderedTypes) {
                Float value = playerCredits.getOrDefault(type, 0f);
                results.append(System.lineSeparator())
                        .append("+ ")
                        .append(value)
                        .append(" (")
                        .append(type)
                        .append(")");
            }

            results.append(System.lineSeparator())
                    .append("= ").append(total).append(" (TOTAL)");

            results.append(System.lineSeparator() + playerColor + " collected a total of " + total + " credits");
        }
        return results.toString();
    }

    public Map<PlayerColor,double[]> getFinalScore(){
        return finalScore;
    }

    public void parseScore(String log) {
        String cleanLog = log.replaceAll("\\u001B\\[[;\\d]*m", "");

        String[] lines = cleanLog.split("\\r?\\n");

        String colorLine = null;
        for (String line : lines) {
            if (line.startsWith("computing total credits for ")) {
                colorLine = line;
                break;
            }
        }
        if (colorLine == null) {
            throw new RuntimeException("Cannot find color line in log:\n" + cleanLog);
        }

        int start = colorLine.indexOf("for ") + 4;
        int end   = colorLine.indexOf(" player", start);
        if (start < 4 || end < 0) {
            throw new RuntimeException("Unexpected format in line:\n" + colorLine);
        }
        String colorName = colorLine.substring(start, end).trim().toUpperCase();

        PlayerColor color;
        try {
            color = PlayerColor.valueOf(colorName);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(
                    "Unknown PlayerColor \"" + colorName + "\" in line:\n" + colorLine
            );
        }

        List<Double> values = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("+") || line.startsWith("=")) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length > 1) {
                    try {
                        values.add(Double.parseDouble(parts[1]));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        double[] score = values.stream().mapToDouble(Double::doubleValue).toArray();

        addFinalScore(color, score);
    }




    public void addFinalScore(PlayerColor color, double[] score){
        finalScore.put(color,score);
    }

    public void processAllScores(RankingDelta rankingDelta) {
        String fullLog = renderRanking(rankingDelta);

        String[] perPlayerLogs = fullLog.split("(?=computing total credits for)");

        for (String playerLog : perPlayerLogs) {
            if (playerLog.isBlank()) continue;
            parseScore(playerLog);
        }
    }

}
