package it.polimi.ingsw.client;

import it.polimi.ingsw.commands.serverResponse.deltas.BackupDelta;
import it.polimi.ingsw.commands.serverResponse.deltas.RankingDelta;
import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.adventure.*;
import it.polimi.ingsw.model.utilities.components.*;

import java.util.*;

import static it.polimi.ingsw.enums.GamePhase.ASSEMBLY_PHASE;
import static it.polimi.ingsw.enums.GameProgression.INITIALIZING_GAME;
import static it.polimi.ingsw.enums.FlightType.TRIAL;

public class TUI extends View{

    private FlightType type;

    private List<String> myBoard;
    private String myHandComponent;
    private String myReservedComponents;

    private PlayerColor color;
    private PlayerColor currentShip;
    private  List<PlayerColor> otherColors;

    private Map<PlayerColor, List<String>> otherBoards;
    private Map<PlayerColor, String> otherHand;
    private Map<PlayerColor, String> otherReserved;

    private String turnedComponents = "There are no turned components right now\n";

    private List<AdventureCard> cardsOne;
    private List<AdventureCard> cardsTwo;
    private List<AdventureCard> cardsThree;

    private AdventureCard drawnCard;

    private boolean peeking = false;
    private int peekchoice;

    private List<String> flightBoard;

    private GamePhase phase;
    private String phaseString;
    private GameProgression progression;
    private String progressionString;
    private String finalRanking;

    private String cardString;

    private boolean fixedShip = false;
    private boolean notCompliant = false;

    public TUI(ViewModel vm){
        vm.addObserver(this);

        otherColors = new LinkedList<>();
        otherBoards = new HashMap<>();
        otherHand = new HashMap<>();
        otherReserved = new HashMap<>();

        phaseString = ASSEMBLY_PHASE.toString();
        progressionString = INITIALIZING_GAME.toString();

        cardsOne = new LinkedList<>();
        cardsTwo = new LinkedList<>();
        cardsThree = new LinkedList<>();

        cardString = "";
    }

    @Override
    public void update(Observable o, Object arg) {
        if(!(arg instanceof UpdateMessage)) return;

        switch(((UpdateMessage) arg).getType()){
            case TURNED_DELTA -> {
                List<Component> newturned = (List<Component>) ((UpdateMessage) arg).getData();
                renderTurnedComponents(newturned);
                updateTUI();
            }
            case HAND_DELTA -> {
                Component newhand = (Component) ((UpdateMessage) arg).getData();

                if(((UpdateMessage) arg).toOtherPlayer()){
                    renderOtherHandComponent(newhand, ((UpdateMessage) arg).getPlayerColor());
                }
                else{
                    renderMyHandComponent(newhand);
                }
                if(currentShip != null){
                    if(currentShip.equals(color)){
                        if(getMyRocketshipBoard() != null)
                            updateTUI();
                    }
                    else{
                        if(getOtherRocketshipBoard(currentShip) != null)
                            updateTUI();
                    }
                }

            }
            case RESERVED_DELTA -> {
                Component[] newreserved = (Component[]) ((UpdateMessage) arg).getData();

                if(((UpdateMessage) arg).toOtherPlayer()){
                    renderOtherReservedComponents(newreserved, ((UpdateMessage) arg).getPlayerColor());
                }
                else{
                    renderMyReservedComponents(newreserved);
                }
                if(currentShip != null){
                    if(currentShip.equals(color)){
                        if(getMyRocketshipBoard() != null)
                            updateTUI();
                    }
                    else{
                        if(getOtherRocketshipBoard(currentShip) != null)
                            updateTUI();
                    }
                }

            }
            case ROCKETSHIP_DELTA -> {
                Component[][] newboard = (Component[][]) ((UpdateMessage) arg).getData();
                System.out.println("processing delta in TUI");

                if(phaseString == "Assembly phase" || phaseString == "Takeoff phase") {
                    if(((UpdateMessage) arg).toOtherPlayer()){
                        renderOtherRocketshipBoard(newboard, ((UpdateMessage) arg).getPlayerColor());
                    }
                    else {
                        renderMyRocketshipBoard(newboard);
                    }
                    if(currentShip != null){
                        if(currentShip.equals(color)){
                            if(getMyRocketshipBoard() != null)
                                updateTUI();
                        }
                        else {
                            if(getOtherRocketshipBoard(currentShip) != null)
                                updateTUI();
                        }
                    }
                } else {
                    System.out.println("processing delta in TUI in either flight or end of flight phase");

                    if(((UpdateMessage) arg).toOtherPlayer()) {
                        System.out.println("other player");
                        PlayerColor rsColor = ((UpdateMessage) arg).getPlayerColor();
                        if(otherColors.contains(rsColor)) {
                            currentShip = rsColor;
                            renderRocketshipAdventure();
                        }
                    } else {
                        System.out.println("yours");
                        currentShip = color;
                        renderRocketshipAdventure();
                    }


                }


            }
            case PHASE_DELTA -> {
                GamePhase newPhase = (GamePhase) ((UpdateMessage) arg).getData();
                renderPhase(newPhase);
                if(!newPhase.equals(ASSEMBLY_PHASE)) {
                    updateTUI();
                }
            }
            case PROGRESSION_DELTA -> {
                GameProgression newProg = (GameProgression) ((UpdateMessage) arg).getData();
                renderProgression(newProg);
                if(currentShip != null) {
                    if(currentShip.equals(color)){
                        if(getMyRocketshipBoard() != null)
                            updateTUI();
                    }
                    else{
                        if(getOtherRocketshipBoard(currentShip) != null)
                            updateTUI();
                    }
                }
            }
            case FLIGHT_TYPE_DELTA -> {
                FlightType t = (FlightType) ((UpdateMessage) arg).getData();
                this.type = t;
            }
            case FLIGHT_BOARD_DELTA -> {
                List<PlayerColor> fbd = (List<PlayerColor>) ((UpdateMessage) arg).getData();

                renderFlightBoard(fbd);
                if(getFlightBoard() != null){
                    updateTUI();
                }

            }
            case VIEW_DELTA -> {
                PlayerColor rsColor = (PlayerColor) ((UpdateMessage) arg).getData();

                if(rsColor == null) { // for SWITCH_VIEW command (assembly phase)
                    nextShip();
                    updateTUI();
                } else {
                    currentShip = rsColor;

                    if(phaseString == "Assembly phase") {
                        updateTUI();
                    }

                }

            }
            case COLOR_DELTA -> {
                PlayerColor color = (PlayerColor) ((UpdateMessage) arg).getData();
                setColor(color);
                currentShip = color;
            }
            case OTHER_COLOR_DELTA -> {
                PlayerColor otherColor = (PlayerColor) ((UpdateMessage) arg).getData();
                addOtherColor(otherColor);
                otherBoards.put(otherColor, null);
                otherHand.put(otherColor, null);
                otherReserved.put(otherColor, new String());
            }
            case ADVENTURE_DELTA -> {
                drawnCard = (AdventureCard) ((UpdateMessage) arg).getData();
                renderAdventureCard(drawnCard);
                System.out.println(cardString);
            }
            case PEEKABLE_CARDS_DELTA -> {
                List<List<AdventureCard>> cards = (List<List<AdventureCard>>) ((UpdateMessage) arg).getData();
                cardsOne = cards.get(0);
                cardsTwo = cards.get(1);
                cardsThree = cards.get(2);
            }
            case PEEK_A_STACK_DELTA -> {
                peeking = true;
                peekchoice = (int) ((UpdateMessage) arg).getData();
                updateTUI();
            }
            case STOP_PEEKING_DELTA -> {
                peeking = false;
                updateTUI();
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

                Set<PlayerColor> colorsBK = bd.getOthersBoards().keySet();
                for (PlayerColor c: colorsBK){
                    addOtherColor(c);
                    otherBoards.put(c, null);
                    otherHand.put(c, null);
                    otherReserved.put(c, null);
                    renderOtherRocketshipBoard(bd.getOthersBoards().get(c), c);
                    renderOtherReservedComponents(bd.getOthersReserved().get(c), c);
                    renderOtherHandComponent(bd.getOthersHands().get(c), c);
                }

                renderFlightBoard(bd.getFlightBoard());

                renderTurnedComponents(bd.getTurnedComponents());

                updateTUI();
            }
            case RANKING_DELTA -> {
                RankingDelta rankingDelta = (RankingDelta) ((UpdateMessage) arg).getData();
                renderRanking(rankingDelta);
                updateTUI();

            }
        }
    }

    public void updateTUI() {

        switch(progressionString){
            case "Initializing game" -> {
                System.out.println("Game is going to start very soon!");
            }
            case "Running game" -> {
                switch(phaseString){
                    case "Assembly phase" -> {
                        clearScreen();

                        if(peeking){
                            switch(peekchoice){
                                case 1 -> {
                                    System.out.println(cardsOne);
                                }
                                case 2 -> {
                                    System.out.println(cardsTwo);
                                }
                                case 3 -> {
                                    System.out.println(cardsThree);
                                }
                            }
                        }
                        else{
                            if(currentShip.equals(color)){
                                renderMyRocketship();
                            } else{
                                renderOthersRocketship();
                            }
                            System.out.println();
                            System.out.println("*".repeat(150));
                            System.out.println("*".repeat(150));
                            System.out.println();
                            System.out.println("Turned components: ");
                            System.out.print(getTurnedComponents());

                            System.out.println(possibleCommands(phaseString));
                        }
                    }
                    case "Takeoff phase" -> {
                        if(isNotCompliant()){
                            if(!isFixedShip()){
                                List<String> board = getMyRocketshipBoard();
                                for (int i = 0; i < board.size(); i++) {
                                    System.out.println(board.get(i));
                                }

                                System.out.println(possibleCommands(phaseString));
                            }
                        }
                    }
                    case "Flight phase" -> {
                        List<String> board = getFlightBoard();
                        if(board != null) {
                            for (String s : board) {
                                System.out.println(s);
                            }
                        }


                        System.out.println(possibleCommands(phaseString) + System.lineSeparator());
                    }
                    case "End flight phase" -> {
                        System.out.println(getRanking());
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + phase);
                }

            }
            case "Paused game" -> {
                System.out.println("The game is currently paused");
            }
            case "Ended game" -> {

            }
            default -> throw new IllegalStateException("Unexpected value: " + progression);
        }
    }

    /**
     * clearScreen() prints a lot of empty lines to clean the textual interface
     */
    public void clearScreen(){
        for(int i = 0; i < 100; i++){
            System.out.println();
        }
    }

    public void renderMyRocketship() {
        System.out.println("Your RocketshipBoard:");

        String[] reservedCell;
        if(getMyReservedComponents() != null) {
            reservedCell = getMyReservedComponents().split("\n");
        }
        else {
            reservedCell = renderEmptyCell(13, 7).split("\n");
        }

        String[] handCell;
        if(getMyHandComponent() != null) {
            handCell = getMyHandComponent().split("\n");
        }
        else {
            handCell = renderEmptyCell(13, 7).split("\n");
        }

        List<String> board = getMyRocketshipBoard();
        int handLinesCount = 0;
        for (int i = 0; i < board.size(); i++) {
            if (i < 7) {
                System.out.println(board.get(i) + " ".repeat(13) + reservedCell[i]);
            }
            else if (i >= 14 && i < 21) {
                System.out.println(board.get(i) + " ".repeat(13) + handCell[handLinesCount]);
                handLinesCount++;
            }
            else {
                System.out.println(board.get(i));
            }
        }
    }

    public void renderOthersRocketship() {
        String username = currentShip.toString();
        System.out.println(username + " player Rocketship:");

        String[] reservedCell;
        if(getOtherReservedComponents(currentShip) != null) {
            reservedCell = getOtherReservedComponents(currentShip).split("\n");
        }
        else {
            reservedCell = renderEmptyCell(13, 7).split("\n");
        }

        String[] handCell;
        if(getOtherHandComponent(currentShip) != null) {
            handCell = getOtherHandComponent(currentShip).split("\n");
        }
        else {
            handCell = renderEmptyCell(13, 7).split("\n");
        }

        List<String> board = getOtherRocketshipBoard(currentShip);
        int handLinesCount = 0;
        for (int i = 0; i < board.size(); i++) {
            if (i < 7) {
                System.out.println(board.get(i) + " ".repeat(13) + reservedCell[i]);
            }
            else if (i >= 14 && i < 21) {
                System.out.println(board.get(i) + " ".repeat(13) + handCell[handLinesCount]);
                handLinesCount++;
            }
            else {
                System.out.println(board.get(i));
            }
        }
    }

    public void renderRocketshipAdventure() {

        List<String> board;
        if(currentShip == color) {
            System.out.println("Your RocketshipBoard:");
            board = getMyRocketshipBoard();
        } else {
            String username = currentShip.toString();
            System.out.println(username + " player Rocketship:");
            board = getOtherRocketshipBoard(currentShip);
        }

        for (int i = 0; i < board.size(); i++) {
            if (i < 7) {
                System.out.println(board.get(i) + " ".repeat(13));
            }
            else if (i >= 14 && i < 21) {
                System.out.println(board.get(i) + " ".repeat(13));
            }
            else {
                System.out.println(board.get(i));
            }
        }
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
     * possibleCommands() returns a String containing the possible commands in every gamePhase
     */
    public String possibleCommands(String gamePhase){
        switch(gamePhase){
            case "Assembly phase" -> {
                if(peeking){
                    return "[Commands during PEEKING]: STOP_PEEKING, PEEK num";
                }
                else{
                    if(type.equals(TRIAL)){
                        return "[Commands for ASSEMBLY phase]: FETCH_UNTURNED, FETCH_TURNED num, FETCH_RESERVED num, ROTATE, RESERVE, REJECT, PLACE row col, SWITCH_VIEW, READY_FOR_TAKEOFF";
                    }
                    else{
                        return "[Commands for ASSEMBLY phase]: FETCH_UNTURNED, FETCH_TURNED num, FETCH_RESERVED num, ROTATE, RESERVE, REJECT, PLACE row col, SWITCH_VIEW, PEEK num, FLIP_HOURGLASS, READY_FOR_TAKEOFF";
                    }
                }
            }
            case "Takeoff phase" -> {
                return "[Commands for TAKEOFF phase]: DETACH row col, POPULATE crewType";
            }
            case "Flight phase" -> {
                return "[Commands for FLIGHT phase]: CHOOSE choice, VIEW, VIEW color, FORFEIT";
            }
            case "End flight phase" -> {
                return "[Commands for END FLIGHT phase]: QUIT_GAME";
            }
            default -> {
                return "";
            }
        }
    }

    //GETTER METHODS
    public List<String> getMyRocketshipBoard(){
        return myBoard;
    }

    public List<String> getOtherRocketshipBoard(PlayerColor color){
        return otherBoards.get(color);
    }

    public String getMyHandComponent(){
        return myHandComponent;
    }

    public String getOtherHandComponent(PlayerColor color){
        return otherHand.get(color);
    }

    public String getMyReservedComponents(){
        return myReservedComponents;
    }

    public String getOtherReservedComponents(PlayerColor color){
        return otherReserved.get(color);
    }

    public String getTurnedComponents(){
        return turnedComponents;
    }

    public String getPhase(){
        return phaseString;
    }

    public String getProgression(){
        return progressionString;
    }

    public List<String> getFlightBoard() {
        return flightBoard;
    }

    public String getRanking(){
        return finalRanking;
    }

    /**
     * getOtherColors() returns all the other player's colors
     */
    public List<PlayerColor> getOtherColors(){
        return otherBoards.keySet().stream().toList();
    }


    //RENDERING METHODS:
    @Override
    public void renderMyRocketshipBoard(Component[][] newboard){
        List<String> myRocketship = new ArrayList<>();
        if(type.equals(TRIAL)){
            List<String> rocketship = renderRocketshipTrial(newboard);
            myRocketship.addAll(rocketship);
            myBoard = myRocketship;
        }
        else{
            List<String> rocketship = renderRocketshipTwo(newboard);
            myRocketship.addAll(rocketship);
            myBoard = myRocketship;
        }
    }

    @Override
    public void renderOtherRocketshipBoard(Component[][] newboard, PlayerColor color){
        List<String> render = new ArrayList<>();
        if(type.equals(TRIAL)){
            List<String> rocketship = renderRocketshipTrial(newboard);
            render.addAll(rocketship);
            otherBoards.put(color, render);
        }
        else{
            List<String> rocketship = renderRocketshipTwo(newboard);
            render.addAll(rocketship);
            otherBoards.put(color, render);
        }
    }

    public List<String> renderRocketshipTrial(Component[][] newboard){
        List<String> myCompleteRocketship = new ArrayList<>();

        int maxLength = 13;
        int numRows = 5; // valore della prima riga
        int numCols = 4;

        boolean[][] shape = {
                {false, false, false, true, false, false, false},
                {false, false, true, true, true, false, false},
                {false, true, true, true, true, true, false},
                {false, true, true, true, true, true, false},
                {false, true, true, false, true, true, false},
        };

        for (int i = 0; i < shape.length; i++) {
            StringBuilder[] rowLines = new StringBuilder[7];
            for (int k = 0; k < rowLines.length; k++) {
                rowLines[k] = new StringBuilder();
            }

            for (int j = 0; j < shape[i].length; j++) {
                String cell;
                if(shape[i][j]){
                    cell = renderCell(newboard[i][j], maxLength);
                }
                else{
                    cell = renderEmptyCell(maxLength, 7);
                }
                String[] lines = cell.split("\n");

                for (int k = 0; k < lines.length; k++) {
                        rowLines[k].append(lines[k]);
                }
            }

            for (int k = 0; k < rowLines.length; k++) {
                if (k % 7 == 3) {
                    myCompleteRocketship.add(numRows + rowLines[k].toString());
                    numRows++;
                }
                else {
                    myCompleteRocketship.add(" " + rowLines[k].toString());
                }
            }
        }

        StringBuilder LastLine = new StringBuilder();
        LastLine.append("  "); //due spazi, sono la spaziatura dal margine, serve per l'aggiunga dei numeri per le righe
        for(int i = 0; i <= shape.length + 1; i++){
            LastLine.append(" ".repeat((maxLength + 1) / 2)).append(numCols + i).append(" ".repeat(maxLength - maxLength / 2)); //spazi prima del numero + numero + spazi dopo
        }
        myCompleteRocketship.add(LastLine.toString());

        return myCompleteRocketship;
    }

    public List<String> renderRocketshipTwo(Component[][] newboard){
        List<String> myCompleteRocketship = new ArrayList<>();

        int maxLength = 13;
        int numRows = 5; //valore della prima colonna
        int numCols = 4;

        boolean[][] shape = {
                {false, false, true, false, true, false, false},
                {false, true, true, true, true, true, false},
                {true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true},
                {true, true, true, false, true, true, true},
        };

        for (int i = 0; i < shape.length; i++) {
            StringBuilder[] rowLines = new StringBuilder[7];
            for (int k = 0; k < rowLines.length; k++) {
                rowLines[k] = new StringBuilder();
            }

            for (int j = 0; j < shape[i].length; j++) {
                String cell;
                if(shape[i][j]){
                    cell = renderCell(newboard[i][j], maxLength);
                }
                else{
                    cell = renderEmptyCell(maxLength, 7);
                }
                String[] lines = cell.split("\n");

                for (int k = 0; k < lines.length; k++) {
                    rowLines[k].append(lines[k]);
                }
            }


            for (int k = 0; k < rowLines.length; k++) {
                if (k % 7 == 3) {
                    myCompleteRocketship.add(numRows + " " + rowLines[k].toString());
                    numRows++;
                }
                else {
                    myCompleteRocketship.add("  " + rowLines[k].toString());
                }
            }
        }

        StringBuilder LastLine = new StringBuilder();
        LastLine.append("  "); //due spazi, sono la spaziatura dal margine, serve per l'aggiunga dei numeri per le righe
        for(int i = 0; i <= shape.length + 1; i++){
            LastLine.append(" ".repeat((maxLength + 1) / 2)).append(numCols + i).append(" ".repeat(maxLength - maxLength / 2)); //spazi prima del numero + numero + spazi dopo
        }
        myCompleteRocketship.add(LastLine.toString());

        return myCompleteRocketship;
    }

    public String renderCell(Component newPiece, int length){
        ComponentType referenceType = newPiece.getComponentType();

        String north = renameConnectors(newPiece.getConnectorByDirection(ComponentSide.NORTH));
        String south = renameConnectors(newPiece.getConnectorByDirection(ComponentSide.SOUTH));
        String west = renameConnectors(newPiece.getConnectorByDirection(ComponentSide.WEST));
        String east = renameConnectors(newPiece.getConnectorByDirection(ComponentSide.EAST));
        String type = renameType(newPiece);
        String specialLine = renderSpecialLine(newPiece);

        if (referenceType.equals(ComponentType.SINGLE_CANNON) || referenceType.equals(ComponentType.DOUBLE_CANNON)) { //lato dove punta il cannone
            switch (newPiece.getReferenceSide()) {
                case NORTH -> north = " X ";
                case SOUTH -> south = " X ";
                case WEST -> west = " X ";
                case EAST -> east = " X ";
            }
        }
        else if (referenceType.equals(ComponentType.SINGLE_THRUSTER) || referenceType.equals(ComponentType.DOUBLE_THRUSTER)) { //lato dove punta il motore
            south = " X ";
        }

        String firstLine = "╔" + "═".repeat(length) + "╗";
        String secondLine = "║" + center(north, length) + "║";
        String thirdLine = "║" + " ".repeat(length) + "║";
        String fourthLine = "║" + west + " " + center(type, length - west.length() - east.length() - 2) + " " + east + "║";
        String fifthLine = "║" + center(specialLine, length) + "║";
        String sixthLine = "║" + center(south, length) + "║";
        String lastLine = "╚" + "═".repeat(length) + "╝";

        return firstLine + "\n" + secondLine + "\n" + thirdLine + "\n" + fourthLine + "\n" + fifthLine + "\n" + sixthLine + "\n" + lastLine;
    }

    public static String renderEmptyCell(int length, int height) {
        String emptyLine = " ".repeat(length + 2); // +2 is it to "║"
        return (emptyLine + "\n").repeat(height);
    }

    static String center(String content, int width) {
        String removeColorSpaces = content.replaceAll("\u001B\\[[\\d;]+m", "");

        int left = (width - removeColorSpaces.length()) / 2;
        int right = width - removeColorSpaces.length() - left;
        return " ".repeat(left) + content + " ".repeat(right);
    }

    public String renameConnectors(ConnectorType type) {
        return switch (type) {
            case SINGLE_CONNECTOR -> " ↑ ";
            case DOUBLE_CONNECTOR -> " ⇑ ";
            case UNIVERSAL_CONNECTOR -> " U ";
            case SMOOTH_SURFACE -> "   ";
            case EMPTY_SPACE -> " . ";
        };
    }

    public String renameType(Component newPiece) {
        String type = " . ";
        switch (newPiece.getComponentType()) {
            case EMPTY_COMPONENT:
                type = " . ";
                break;
            case PIPES:
                type = "PIPES";
                break;
            case STOCK:
                if(newPiece instanceof StockComponent stock) {
                    if(stock.isSpecial()) {
                       type = "\u001B[31mSTOCK\u001B[0m";
                    }
                    else {
                        type = "STOCK";
                    }
                };
                break;
            case SHIELD:
                type = "SHELD";
                break;
            case ALIEN_ADD_ON:
                if(newPiece instanceof AlienAddOnComponent addOn) {
                    if(addOn.getAlienColor().equals(CrewType.ALIEN_PURPLE)) {
                        type = "\u001B[38;5;135mADDON\u001B[0m";
                    }
                    else if(addOn.getAlienColor().equals(CrewType.ALIEN_BROWN)) {
                        type = "\u001B[38;5;94mADDON\u001B[0m";
                    }
                }
                break;
            case POWER_CENTER:
                type = "POWER";
                break;
            case SIMPLE_CABIN:
                type = "CABIN";
                break;
            case DOUBLE_CANNON:
                type = "2CANN";
                break;
            case SINGLE_CANNON:
                type = "1CANN";
                break;
            case STARTING_CABIN :
                if(newPiece instanceof StarterCabinComponent starter) {
                    switch(starter.getColor()) {
                        case PlayerColor.RED -> type = "\u001B[31mCABIN\u001B[0m";
                        case PlayerColor.GREEN -> type = "\u001B[32mCABIN\u001B[0m";
                        case PlayerColor.BLUE -> type = "\u001B[34mCABIN\u001B[0m";
                        case PlayerColor.YELLOW -> type = "\u001B[33mCABIN\u001B[0m";
                    }
                }
                break;
            case DOUBLE_THRUSTER:
                type = "2THRU";
                break;
            case SINGLE_THRUSTER:
                type = "1THRU";
                break;
        };
        return type;
    }

    public String renderSpecialLine(Component newPiece) {
        String renderInformation = "  ";
        ComponentType referenceType = newPiece.getComponentType();

        if (referenceType.equals(ComponentType.SHIELD) && newPiece instanceof ShieldComponent shield) {
            if(shield.protectedSides().getFirst().equals(ComponentSide.NORTH)){
                renderInformation = "N - E";
            }
            else if(shield.protectedSides().getFirst().equals(ComponentSide.EAST)){
                renderInformation = "E - S";
            }
            else if(shield.protectedSides().getFirst().equals(ComponentSide.SOUTH)){
                renderInformation = "S - W";
            }
            else if(shield.protectedSides().getFirst().equals(ComponentSide.WEST)){
                renderInformation = "W - N";
            }

        } else if (referenceType.equals(ComponentType.STOCK) && newPiece instanceof StockComponent stock) {
            int maxCapacity = stock.getMaxCapacity();
            int rendered = 0;
            StringBuilder renderCapacity = new StringBuilder();

            for (ResourceType type : stock.getStock()) {
                if (rendered < maxCapacity) {
                    if(maxCapacity == 2) {
                        renderCapacity.append(" ").append(type.toString());
                    }
                    else {
                        renderCapacity.append(type.toString());
                    }
                    rendered++;
                }
            }
            while(rendered < maxCapacity){
                    if(maxCapacity == 2) {
                        renderCapacity.append(" ").append("□");
                    }
                    else {
                        renderCapacity.append("□");
                    }
                    rendered++;
                }
            renderInformation = renderCapacity.toString();
        }

        else if (referenceType.equals(ComponentType.POWER_CENTER) && newPiece instanceof PowerCenterComponent powerCenter) {
            int rendered = 0;
            int maxCapacity = powerCenter.getMaxCapacity();
            StringBuilder renderCapacity = new StringBuilder();

            for (int i = 0; i < powerCenter.getTotalResource(); i++) {
                if (rendered < maxCapacity) {
                    if(maxCapacity == 2) {
                        renderCapacity.append(" ").append("■");
                    }
                    else {
                        renderCapacity.append("■");
                    }
                    rendered++;
                }
            }
            while(rendered < maxCapacity){
                if(maxCapacity == 2) {
                    renderCapacity.append(" ").append("□");
                }
                else {
                    renderCapacity.append("□");
                }
                rendered++;
            }
            renderInformation = renderCapacity.toString();
        }
        return renderInformation;
    }

    @Override
    public void renderMyHandComponent(Component hand){
        myHandComponent = renderCell(hand, 13);
    }

    @Override
    public void renderOtherHandComponent(Component hand, PlayerColor color){
        otherHand.put(color, renderCell(hand, 13));
    }

    @Override
    public void renderMyReservedComponents(Component[] reserved){
        String[] left = renderCell(reserved[0], 13).split("\n");
        String[] right = renderCell(reserved[1], 13).split("\n");

        StringBuilder rendered = new StringBuilder();
        for (int i = 0; i < left.length; i++) {
            rendered.append(left[i]).append("  ").append(right[i]).append("\n");
        }
        myReservedComponents = rendered.toString();
    }

    @Override
    public void renderOtherReservedComponents(Component[] reserved, PlayerColor color){
        String[] left = renderCell(reserved[0], 13).split("\n");
        String[] right = renderCell(reserved[1], 13).split("\n");

        StringBuilder rendered = new StringBuilder();
        for (int i = 0; i < left.length; i++) {
            rendered.append(left[i]).append("  ").append(right[i]).append("\n");
        }
        otherReserved.put(color, rendered.toString());
    }

    @Override
    public void renderTurnedComponents(List<Component> t){
        if(!t.isEmpty()){
            List<String[]> turned = t.stream().map(c -> renderCell(c, 13).split("\n")).toList();

            StringBuilder rendered = new StringBuilder();

            for(int i = 0; i < turned.size(); i+=10){ //creo righe da 10
                int endLines = i + 10;
                if(endLines > turned.size()){ //se dovessi arrivare oltre al massimo degli oggetti
                    endLines = turned.size();
                }
                for (int j = 0; j < turned.getFirst().length; j++) { //stringhe del quadrato
                    for (int k = i; k < endLines; k++) { //oggetti per ogni riga
                        rendered.append(turned.get(k)[j]);
                    }
                    rendered.append("\n");
                }
                rendered.append("\n");
            }
            turnedComponents = rendered.toString();
        }
        else{
            turnedComponents = "There are no turned components right now\n";
        }
    }

    @Override
    public void renderAdventureCard(AdventureCard card) {
        List<String> lines = new ArrayList<>();
        lines.add(card.getType().toString());
        lines.addAll(getCardSpecifics(card));

        int maxLength = lines.stream()
                            .mapToInt(String::length)
                            .max()
                            .orElse(0);

        String border = "═".repeat(maxLength + 2);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("╔" + border + "╗\n"));
        for (String line : lines) {
            sb.append("║ ")
                    .append(String.format("%-" + maxLength + "s", line))
                    .append(" ║\n");
        }
        sb.append("╚").append(border).append("╝\n");
        cardString = sb.toString();
    }

    public List<String> getCardSpecifics(AdventureCard card){
        List<String> lines = new ArrayList<>();
        switch (card.getType()) {
            case ENEMY -> {
                EnemiesCard enemyCard = (EnemiesCard) card;
                lines.add(String.format("days lost: %d", card.getDays()));
                lines.add(String.format("enemies power: %.2f", enemyCard.getPower()));
                switch (enemyCard.getEnemyType()) {
                    case SMUGGLER:
                        lines.add("type of enemy: SMUGGLER");
                        lines.add(String.format("cubes reward: %s", enemyCard.getSmugglerReward()));
                        lines.add(String.format("cubes lost: %d", enemyCard.getPenalty()));
                        break;
                    case SLAVER:
                        lines.add("type of enemy: SLAVER");
                        lines.add(String.format("credits reward: %d", enemyCard.getReward()));
                        lines.add(String.format("crewmembers lost: %d", enemyCard.getPenalty()));
                        break;
                    case PIRATES:
                        lines.add("type of enemy: PIRATES");
                        lines.add(String.format("credits reward: %d", enemyCard.getReward()));
                        lines.add(String.format("attack: %s", enemyCard.getPiratePenalty()));
                        break;
                }
            }
            case METEORITE -> {
                MeteoriteCard meteoriteCard = (MeteoriteCard) card;
                lines.add(("meteorites:"));

                List<String> meteoritesString = meteoriteCard.getMeteorites();
                for(String string : meteoritesString) {
                    lines.add(string);
                }
            }
            case PLANET -> {
                PlanetCard planetCard = (PlanetCard) card;
                int numPlanets = planetCard.getNumberPlanets();
                lines.add(String.format("days lost: %d", card.getDays()));
                //lines.add(String.format("landable planets: %s", planetCard.getLandablePlanets()));
                String[] rewardOrdinals = {"first", "second", "third", "fourth"};
                for (int i = 0; i < Math.min(numPlanets, 4); i++) {
                    lines.add(String.format("%s planet reward: %s", rewardOrdinals[i], planetCard.getPlanetReward(i)));
                }
            }
            case SHIP -> {
                ShipCard shipCard = (ShipCard) card;
                lines.add(String.format("days lost: %d", card.getDays()));
                lines.add(String.format("crewmembers lost: %d", shipCard.getCrewLost()));
                lines.add(String.format("reward: %d", shipCard.getReward()));
            }
            case STATION -> {
                StationCard stationCard = (StationCard) card;
                lines.add(String.format("days lost: %d", card.getDays()));
                lines.add(String.format("crewmembers needed: %d", stationCard.getRequiredCrew()));
                lines.add(String.format("reward: %s", stationCard.getReward()));
            }
            case WARZONE -> {
                WarzoneCard warzoneCard = (WarzoneCard) card;
                for(int i = 0; i < 3; i++) {
                    lines.add(String.format("%s", warzoneCard.getConditionPenalty(i)));
                }
            }
            default -> { // reached by EMPTY,EPIDEMY,OPEN_SPACE,STARDUST,SABOTAGE
            }
        }
        return lines;
    }

    @Override
    public void renderPhase(GamePhase p){
        phaseString = p.toString();
    }

    @Override
    public void renderProgression(GameProgression p){
        progressionString = p.toString();
    }

    public void renderFlightBoard(List<PlayerColor> fbd){
        if(fbd != null) {
            List<String> actualFlightBoard = new ArrayList<>();
            if(type.equals(TRIAL)){
                List<String> board = renderFlightBoardRepresentationTrial(fbd);
                actualFlightBoard.addAll(board);
                flightBoard = actualFlightBoard;
            }
            else{
                List<String> board = renderFlightBoardRepresentationTwo(fbd);
                actualFlightBoard.addAll(board);
                flightBoard = actualFlightBoard;
            }
        }
    }
    public List<String> renderFlightBoardRepresentationTrial(List<PlayerColor> fbd) {
        List<String> completeFlightBoard = new ArrayList<>();
        int position = 0;

        int row = 5;
        int col = 6;
        int widthCell = 5;
        String[][] flightMap = new String[row][col];

        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                flightMap[i][j] = renderEmptyCell(widthCell, 3);
            }
        }

        for (int j = 0; j < col; j++) {
            PlayerColor currentCell = fbd.get(position);
            flightMap[0][j] = renderFlightBoardCell(currentCell, position, widthCell);
            position++;
        }
        for(int k = 1; k < row - 1; k++) {
            PlayerColor currentCell = fbd.get(position);
            flightMap[k][col - 1] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }
        for (int j = col - 1; j >= 0; j--) {
            PlayerColor currentCell = fbd.get(position);
            flightMap[row -1][j] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }
        for(int k = row-2; k > 0; k--) {
            PlayerColor currentCell = fbd.get(position);
            flightMap[k][0] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }

        for (int i = 0; i < row; i++) {
            StringBuilder[] lines = new StringBuilder[3];
            for (int k = 0; k < 3; k++) {
                lines[k] = new StringBuilder();
            }

            for (int j = 0; j < col; j++) {
                String[] cellLines = flightMap[i][j].split("\n");
                for (int k = 0; k < 3; k++) {
                    lines[k].append(cellLines[k]);
                }
            }
            for (int k = 0; k < 3; k++) {
                completeFlightBoard.add(lines[k].toString());
            }
        }
        return completeFlightBoard;
    }


    public List<String> renderFlightBoardRepresentationTwo(List<PlayerColor> fbd) {
        List<String> completeFlightBoard = new ArrayList<>();
        int position = 0;

        int sizeMap = 7;
        int widthCell = 5;
        String[][] flightMap = new String[sizeMap][sizeMap];


        for(int i = 0; i < sizeMap; i++) {
            for(int j = 0; j < sizeMap; j++) {
                flightMap[i][j] = renderEmptyCell(widthCell, 3);
            }
        }
        for (int j = 0; j < sizeMap; j++) {
            PlayerColor currentCell = fbd.get(position);
            flightMap[0][j] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }
        for(int k = 1; k < sizeMap -1; k++) {
            PlayerColor currentCell = fbd.get(position);
            flightMap[k][sizeMap - 1] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }
        for (int j = sizeMap -1; j >= 0; j--) {
            PlayerColor currentCell = fbd.get(position);
            flightMap[sizeMap -1][j] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }
        for(int k = sizeMap -2; k > 0; k--) {
            PlayerColor currentCell = fbd.get(position);
            flightMap[k][0] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }

        for (int i = 0; i < sizeMap; i++) {
            StringBuilder[] lines = new StringBuilder[3];
            for (int k = 0; k < 3; k++) {
                lines[k] = new StringBuilder();
            }

            for (int j = 0; j < sizeMap; j++) {
                String[] cellLines = flightMap[i][j].split("\n");
                for (int k = 0; k < 3; k++) {
                    lines[k].append(cellLines[k]);
                }
            }
            for (int k = 0; k < 3; k++) {
                completeFlightBoard.add(lines[k].toString());
            }
        }
        return completeFlightBoard;
    }

    public String renderFlightBoardCell(PlayerColor currentCell, int position, int length) {
        if(position == 0) {
            if(currentCell == null) {
                String firstLine = "╔" + "░" + "═".repeat(length-1) + "╗";
                String midLine = "║" + center(" ", length) + "║";
                String lastLine = "╚" + "═".repeat(length) + "╝";

                return firstLine + "\n" + midLine + "\n" + lastLine;
            }
            else {
                String player = renamePlayer(currentCell);
                String firstLine = "╔" + "░" + "═".repeat(length-1) + "╗";
                String midLine = "║" + center(player, length) + "║";
                String lastLine = "╚" + "═".repeat(length) + "╝";

                return firstLine + "\n" + midLine + "\n" + lastLine;
            }
        }
        else {
            if(currentCell == null) {
                String firstLine = "╔" + "═".repeat(length) + "╗";
                String midLine = "║" + center(" ", length) + "║";
                String lastLine = "╚" + "═".repeat(length) + "╝";

                return firstLine + "\n" + midLine + "\n" + lastLine;
            }
            else {
                String player = renamePlayer(currentCell);
                String firstLine = "╔" + "═".repeat(length) + "╗";
                String midLine = "║" + center(player, length) + "║";
                String lastLine = "╚" + "═".repeat(length) + "╝";

                return firstLine + "\n" + midLine + "\n" + lastLine;
            }
        }

    }

    public String renamePlayer(PlayerColor player) {
        return switch (player) {
            case RED -> "\u001B[31m●\u001B[0m";
            case GREEN -> "\u001B[32m●\u001B[0m";
            case YELLOW -> "\u001B[33m●\u001B[0m";
            case BLUE -> "\u001B[34m●\u001B[0m";
        };
    }

    public void renderRanking(RankingDelta rankingDelta) {
        Map<PlayerColor, Map<CreditsType, Float>> credits = rankingDelta.getCredits();
        List<Map.Entry<PlayerColor, Float>> ranking = rankingDelta.getRanking();

        StringBuilder results = new StringBuilder();
        for (Map.Entry<PlayerColor, Float> entry : ranking) {
            PlayerColor playerColor = entry.getKey();
            float total   = entry.getValue();

            results.append(System.lineSeparator());
            results.append(System.lineSeparator() + "computing total credits for " + playerColor + " player");
            credits.get(playerColor).forEach((type, value) -> {if(type != CreditsType.TOTAL) results.append(System.lineSeparator() + "+ " + value + " (" + type.toString() + ")");});
            results.append(System.lineSeparator() + "= " + total + " (TOTAL)");
            results.append(System.lineSeparator() + playerColor + " collected a total of " + total + " credits");
        }

        finalRanking = results.toString();
    }

    public void setFixedShip(){
        fixedShip = true;
    }

    public boolean isFixedShip(){
        return fixedShip;
    }

    public boolean isNotCompliant(){
        return notCompliant;
    }

    public void setNotCompliant(){
        notCompliant = true;
    }
}