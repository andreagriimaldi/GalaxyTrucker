package it.polimi.ingsw.controller;

import it.polimi.ingsw.commands.serverResponse.ServerResponse;
import it.polimi.ingsw.commands.serverResponse.deltas.AdventureDelta;
import it.polimi.ingsw.commands.serverResponse.deltas.FlightBoardDelta;
import it.polimi.ingsw.commands.serverResponse.deltas.PhaseDelta;
import it.polimi.ingsw.commands.serverResponse.deltas.RankingDelta;
import it.polimi.ingsw.commands.userRequest.moves.FlightChoiceMove;
import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.enums.CreditsType;
import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;
import it.polimi.ingsw.model.game.AdventureStack;
import it.polimi.ingsw.model.game.FlightBoardFacade;
import it.polimi.ingsw.model.game.GameSession;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.playerset.RocketshipBoard;
import it.polimi.ingsw.model.utilities.adventure.AdventureCard;
import it.polimi.ingsw.model.utilities.components.*;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class AdventureController {

    private GameController gameController;
    private GameLobby lobby;
    private GameSession gameSession;

    /** FROM MODEL: ADVENTURE CARDS AND FLIGHTBOARD **/
    private AdventureStack deck;
    private AdventureCard drawn;
    private FlightBoardFacade flightBoard;

    /** PLAYERS IN ORDER OF FINISHING ASSEMBLY **/
    private List<PlayerColor> orderOfAssemblyFinishers;

    /** FLIGHT MOVE QUEUE AND MOVE PROCESSING THREAD **/
    private final LinkedBlockingQueue<Move> moveQueue;
    private Thread moveProcessorThread;

    private List<PlayerColor> forfeittingColors;

    private boolean finished;


    private final Set<PlayerColor> needsToFixRocketship =
            java.util.Collections.newSetFromMap(
                    new java.util.concurrent.ConcurrentHashMap<>());


    public AdventureController(GameController gameController) {

        this.gameController = gameController;
        this.lobby = gameController.getLobby();
        this.gameSession = gameController.getGameSession();

        /** FROM MODEL: ADVENTURE CARDS AND FLIGHTBOARD **/
        this.deck = gameSession.getCards();
        this.drawn = null; //the active card will be drawn in due time
        this.flightBoard = gameSession.getFlightBoard();


        /** PLAYERS IN ORDER OF FINISHING ASSEMBLY **/
        this.orderOfAssemblyFinishers = new LinkedList<>();

        /** FLIGHT MOVE QUEUE AND MOVE PROCESSING THREAD **/
        this.moveQueue = new LinkedBlockingQueue<>();
        this.moveProcessorThread = null;

        this.forfeittingColors = new LinkedList<>();

        finished = false;

    }



    @TestOnly
    public AdventureController(AdventureStack customAdventureStack, GameSession gameSession, GameLobby lobby) { // method exclusively for testing
        this.deck = customAdventureStack;
        this.moveQueue = new LinkedBlockingQueue<>();
        this.gameSession = gameSession;
        this.lobby = lobby;

        this.forfeittingColors = new LinkedList<>();
    }


    public GameSession getGameSession() {
        return gameSession;
    }

    public GameLobby getLobby() {
        return lobby;
    }


    public void initializeFlight(List<Player> orderOfAssemblyFinishers) {

        /* INITIALIZE FLIGHTBOARD */
        try {
            this.gameSession.createFlightBoard(orderOfAssemblyFinishers);
            this.flightBoard = this.gameSession.getFlightBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * addMoveToQueue() is used to add a move coming from the client to a list containing all the moves that need
     * to be realized on the rocketship
     * @param m is the move added to the queue
     */
    public void addMoveToQueue(Move m) {
        moveQueue.add(m);
    }

    public void startProcessingMoves() {
        this.moveProcessorThread = new Thread (() -> {
            try {
                System.out.println("Started move processing on a dedicated thread");
                while (!Thread.currentThread().isInterrupted()) {

                    Move move = moveQueue.take(); /** BLOCKING INSTRUCTION **/
                    System.out.println("move taken. now activating it");

                    activateMove(move);

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "FlightMoveProcessor");
        this.moveProcessorThread.start();
    }


    public void terminateMoveProcessorThread() {
        if (moveProcessorThread != null && moveProcessorThread.isAlive()) {
            this.moveProcessorThread.interrupt();
            this.moveProcessorThread = null;
        }
    }


    /**
     * the method draws a card, set the adventure controller as listener for possible requests and then activates the card
     * @throws Exception if there are no more cards in the deck but a draw request is sent anyway
     */
    public void beginFlight() {

        /* INITIALIZING ROCKETSHIP WITH CREW AND BATTERIES, AND FLAGGING SIMPLE CABINS WITH THEIR SUPPORTED ATMOSPHERES */
        for(Player player : flightBoard.returnPlayersInOrder()) {
            player.getBoard().resetSimpleCabinSupportedAtmospheres();
            player.getBoard().putBatteriesAndCrew();
        }

        ServerResponse flightPhaseUpdate = new ServerResponse();
        flightPhaseUpdate.addDelta(new PhaseDelta(GamePhase.FLIGHT_PHASE));
        lobby.notifyAllPlayers(flightPhaseUpdate);

        try {
            Thread.currentThread().sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lobby.logAllPlayers("\uD83C\uDFC1 \uD83C\uDFC1 ALL ROCKETSHIPS TOOK OFF. THE GAME ENTERED FLIGHT PHASE \uD83C\uDFC1 \uD83C\uDFC1");


        try {
            Thread.currentThread().sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        drawAndPlayNextCard();

    }



    public void drawAndPlayNextCard() {

        this.drawn = deck.draw();

        List<Player> playersLeft = gameSession.getFlightBoard().returnPlayersInOrder();

        if(playersLeft.size() > 0) {
            if(this.drawn != null) {

                flightBoard.printFlightBoard();

                ServerResponse flightBoardUpdate = new ServerResponse();
                List<PlayerColor> flightBoardRepresentation = this.flightBoard.getFlightBoardRepresentation();
                flightBoardUpdate.addDelta(new FlightBoardDelta(flightBoardRepresentation));
                lobby.notifyAllPlayers(flightBoardUpdate);

                StringBuilder nextCardSign = new StringBuilder();
                nextCardSign.append(System.lineSeparator() + System.lineSeparator());
                nextCardSign.append("#######################################" + System.lineSeparator());
                nextCardSign.append("#          DRAWING NEXT CARD...       #" + System.lineSeparator());
                nextCardSign.append("#######################################" + System.lineSeparator());
                lobby.logAllPlayers(nextCardSign.toString());

                try {
                    Thread.currentThread().sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ServerResponse cardToPushResponse = new ServerResponse("DRAWN " + drawn.getType() + " ADVENTURE CARD (ID: " + drawn.getID() + ")");
                cardToPushResponse.addDelta(new AdventureDelta(drawn));
                lobby.notifyAllPlayers(cardToPushResponse);

                try {
                    Thread.currentThread().sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                this.drawn.attachAdventureController(this);
                this.drawn.handle();

            } else {
                lobby.logAllPlayers("ALL CARDS WERE DRAWN");
                endFlight();
            }
        } else {
            lobby.logAllPlayers("THERE ARE NO PLAYERS LEFT ON THE FLIGHTBOARD");

            endFlight();
        }

    }


    public void activateMove(Move move) {
        MoveType moveType = move.getType();
        PlayerColor movesColor = move.getColor();

        if(moveType == MoveType.FORFEIT) {
            if(!forfeittingColors.contains(movesColor)) {
                lobby.logAllPlayers(movesColor + " player decided to forfeit. they will end their flight at the end of the current card");
                forfeittingColors.add(movesColor);
            }
        } else if (moveType == MoveType.FLIGHT_CHOICE) {
            if(this.drawn != null) {
                if(this.drawn.isAwaitingPlayerChoice()) {
                    new Thread(() -> {
                        this.drawn.handle(move);
                    }).start();
                }
            } else {
                // drawn is null in between one card and the next,
                // so this allows to direct the moves of ADVENTURE_CHOICE
                // type to saving a piece of a fragmented ship, if asked by the server
                saveRocketship(move);
            }
        }
    }


    public void removePlayer(Player player) {
        try {
            flightBoard.removePlayerFromBoard(player);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    public void saveRocketship(Move move) {

        PlayerColor movesColor = move.getColor();
        Player movesPlayer = lobby.getPlayerFromColor(movesColor);

        FlightChoiceMove adventureChoiceMove = (FlightChoiceMove) move;
        int choice = adventureChoiceMove.getChoice();

        if(needsToFixRocketship.contains(movesColor)) {

            boolean isInPartsRange = choice < movesPlayer.getBoard().getSizeOfSetOfConnectedComponents();

            if (isInPartsRange) { // checking if the player choice is within range

                synchronized (this) {

                    // making the code block atomic to avoid two players removing their rocketship "almost simultaneously",
                    // and then both threads checking the if condition as true

                    movesPlayer.getBoard().chooseBrokenShip(choice);
                    needsToFixRocketship.remove(movesColor);
                    lobby.logPlayer(movesColor, "choosing to save group " + choice);

                    int i = 0;
                    for (Set<Component> set : movesPlayer.getBoard().checkIntegrity()) {
                        System.out.println("Group " + i++ + ":");
                        for (Component c : set) {
                            int[] coordinates = movesPlayer.getBoard().getCoordinates(c);
                            int row = coordinates[0] + 5;
                            int col = coordinates[1] + 4;
                            System.out.println("  - (" + row + ", " + col + ") " + System.identityHashCode(c) + " " + c);
                        }
                    }

                    if (needsToFixRocketship.isEmpty()) {
                        checkConditionsForRemoval();
                        drawAndPlayNextCard(); // [] dopo la chiamata la metodo, mantiene la sincronizzazzione?
                    }
                }
            } else {
                lobby.logPlayer(movesColor, "you need to choose a group in the proposed range");
            }
        } else {
            lobby.logPlayer(movesColor, "you don't need to fix your rocketship");
        }

    }



    public void prepareForNextCard() {
        drawn = null;

        lobby.logAllPlayers(System.lineSeparator() + "--############ CARD IS DONE ############--");


        List<Player> playersLeft = flightBoard.returnPlayersInOrder();
        for(Player player : playersLeft) {

            RocketshipBoard rs = player.getBoard();

            rs.resetSimpleCabinSupportedAtmospheres();

            lobby.logPlayer(player.getColor(), System.lineSeparator() + "------------- YOUR SITUATION -------------");

            int crewMatesLeft = rs.checkTotalCrew();
            lobby.logPlayer(player.getColor(), "# OF CREWMATES LEFT: " + crewMatesLeft);

            if(rs.hasPurpleAlien()) {
                lobby.logPlayer(player.getColor(), "PURPLE ALIEN: YES");
            }

            if(rs.hasBrownAlien()) {
                lobby.logPlayer(player.getColor(), "BROWN ALIEN: YES");
            }

            int destroyedComponents = rs.countComponentsDestroyedDuringFlight();
            lobby.logPlayer(player.getColor(), "# OF DESTROYED COMPONENTS: " + destroyedComponents);

            int batteriesLeft = rs.checkTotalBatteries();
            lobby.logPlayer(player.getColor(), "# OF BATTERIES LEFT: " + batteriesLeft);

            int credits = player.getCredits();
            lobby.logPlayer(player.getColor(), "TOTAL CREDITS: " + credits + "$");

            int resourceValue = rs.getValueOfCubes();
            lobby.logPlayer(player.getColor(), "TOTAL RESOURCE VALUE: " + resourceValue + "$");


            lobby.logPlayer(player.getColor(), "------------------------------------------" + System.lineSeparator());



            checkBrokenShip(player);

        }

        if(needsToFixRocketship.isEmpty()) {
            checkConditionsForRemoval();
            drawAndPlayNextCard();
        }
    }


    public void checkBrokenShip(Player currentPlayer) {
        // [x done] manage the case where there's no component left
        // solved by not asking player's intervention, as there's no subgraph left to choose to save in the first place

        RocketshipBoard rs = currentPlayer.getBoard();
        List<HashSet<Component>> integritySubGraphs = rs.checkIntegrity();

        if(!integritySubGraphs.isEmpty()) {
            if(lobby.isColorConnected(currentPlayer.getColor())) {
                if(!rs.checkShipEverytime()) {

                    lobby.logPlayer(currentPlayer.getColor(),
                            "Your ship broke into multiple parts – type 'CHOOSE <index>' to choose which part to keep, among ");

                    int i = 0;
                    for (Set<Component> set : rs.checkIntegrity()) {
                        System.out.println("Group " + i++ + ":");
                        for (Component c : set) {
                            int[] coordinates = rs.getCoordinates(c);
                            int row = coordinates[0] + 5;
                            int col = coordinates[1] + 4;
                            System.out.println("  - (" + row + ", " + col + ") " + System.identityHashCode(c) + " " + c);
                        }
                    }


                    needsToFixRocketship.add(currentPlayer.getColor());
                }
            } else {
                currentPlayer.getBoard().chooseBrokenShip(0); // DISCONNECTED PLAYER WILL ALWAYS SAVE PART #0
            }
        }
    }

    public boolean isPlayersShipBroken(PlayerColor playerColor) {
        return needsToFixRocketship.contains(playerColor);
    }

    public void removeFromNeedsToFixRocketship(PlayerColor playerColor) {
        needsToFixRocketship.remove(playerColor);
    }


    public void checkConditionsForRemoval() {
        List<Player> playersLeft = flightBoard.returnPlayersInOrder();

        /* CREWMATES REMOVAL CONDITION */
        for(Player player : playersLeft) {

            boolean hasAnyCrewmatesLeft = (player.getBoard().checkTotalCrew() > 0);

            if(!hasAnyCrewmatesLeft) {
                lobby.logAllPlayers(player.getColor() + " was has no crew member left and therefore will be removed from the rocketship");
                removePlayer(player);
            }

        }


        /* BEING SURPASSED REMOVAL CONDITION */
        try {
            List<Player> surpassedPlayers = flightBoard.checkLapsForPlayersToRemove();

            for(Player surpassedPlayer : surpassedPlayers) {
                lobby.logAllPlayers(surpassedPlayer.getColor() + " was surpassed and will be removed from the rocketship board");
                removePlayer(surpassedPlayer);
            }
        } catch (Exception e) { // from check laps
            e.printStackTrace();
        }


        /* FORFEITTING REMOVAL CONDITION */

        // get an updated version of players left
        playersLeft = flightBoard.returnPlayersInOrder();

        for(int i = 0; i < forfeittingColors.size(); i++) {
            for(int j = 0; j < playersLeft.size(); j++) {
                if(forfeittingColors.get(i) == playersLeft.get(j).getColor()) {
                    removePlayer(playersLeft.get(j));
                    lobby.logAllPlayers(forfeittingColors.get(i) +  " player forfeitted, and is being removed from the flightboard");
                    break;
                }
            }
        }

        while(forfeittingColors.size() > 0) {
            forfeittingColors.remove(0);
        }

    }



    public void endFlight() {
        if(!finished) { // additional check
            finished = true;

            lobby.logAllPlayers("FLIGHT IS OVER");

            if(gameController != null) { // because in testing it can be null
                try {
                    Thread.currentThread().sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                gameController.setFlightAsOver();
                showRankings();
            }
        }

    }




    public void showRankings() {
        Map<PlayerColor, Map<CreditsType, Float>> credits = new HashMap<>();
        Set<PlayerColor> allPlayerColors = lobby.getAllPlayerColors();

        for(PlayerColor currentPlayerColor : allPlayerColors) {
            credits.put(currentPlayerColor, new HashMap<>());
        }

        List<Player> players = gameSession.getPlayers();


        /* CREDITS ACQUIRED THROUGHOUT THE GAME */
        for(Player player : players) {
            credits.get(player.getColor()).put(CreditsType.FLIGHT_CREDITS, Float.valueOf(player.getCredits()));
        }

        List<Player> finishingPlayers = flightBoard.returnPlayersInOrder();
        List<PlayerColor> finishingPlayersColors = new LinkedList<>();

        for(Player player : finishingPlayers) {
            finishingPlayersColors.add(player.getColor());
        }

        /* PLAYERS WHOSE ROCKETS ARE NOT ON THE FLIGHTBOARD BY THE END OF THE FLIGHT */
        for(PlayerColor playerColor : allPlayerColors) {
            if(!finishingPlayersColors.contains(playerColor)) {
                credits.get(playerColor).put(CreditsType.FINISHING_ORDER_BONUS, Float.valueOf(0));
            } else {
                System.out.println(playerColor + " has won the game and so will be assigned some credits, depending on their positioning");
            }
        }

        /* ASSIGNING BONUS POINTS TO PLAYERS WHOSE ROCKETS ARE STILL ON THE FLIGHTBOARD BY THE END OF THE FLIGHT */
        for(int i = 0; i < finishingPlayersColors.size(); i++) {
            PlayerColor playerColor = finishingPlayersColors.get(i);

            if(i == 0) {
                credits.get(playerColor).put(CreditsType.FINISHING_ORDER_BONUS, Float.valueOf(4));
            } else if (i == 1) {
                credits.get(playerColor).put(CreditsType.FINISHING_ORDER_BONUS, Float.valueOf(3));
            } else if (i == 2) {
                credits.get(playerColor).put(CreditsType.FINISHING_ORDER_BONUS, Float.valueOf(2));
            } else if (i == 3) {
                credits.get(playerColor).put(CreditsType.FINISHING_ORDER_BONUS, Float.valueOf(1));
            }
        }

        Map<PlayerColor, Integer> playersExposedConnectors = new HashMap<>();
        int leastNumberOfExposedConnectors = 10000;
        for(Player player : players) {
            if(player.checkIfHasRocketship()) {
                RocketshipBoard rb = player.getBoard();
                int numberOfExposedConnectors = rb.countExposedConnectors();
                playersExposedConnectors.put(player.getColor(), Integer.valueOf(numberOfExposedConnectors));

                if(numberOfExposedConnectors < leastNumberOfExposedConnectors) {
                    leastNumberOfExposedConnectors = numberOfExposedConnectors;
                }
            } else {
                System.out.println(player.getColor() + " doesn't have a rocketship");
            }
        }

        System.out.println(leastNumberOfExposedConnectors + " is the min number of exposed connectors across players");

        List<PlayerColor> connectorsPrizeWinners = new LinkedList<>();
        for(PlayerColor playerColor : playersExposedConnectors.keySet()) {
            if(playersExposedConnectors.get(playerColor) == leastNumberOfExposedConnectors) {
                connectorsPrizeWinners.add(playerColor);
            }
        }

        for(PlayerColor playerColor : allPlayerColors) {
            if(connectorsPrizeWinners.contains(playerColor)) {
                System.out.println(playerColor + " player has collected a 2 point prize for their beautiful ship");
                credits.get(playerColor).put(CreditsType.CONNECTORS_PRIZE, Float.valueOf(2));
            } else {
                credits.get(playerColor).put(CreditsType.CONNECTORS_PRIZE, Float.valueOf(0));
            }
        }

        /* PENALIZATION FOR LOST COMPONENTS */
        for(Player player : players) {
            System.out.println("penalizing " +player.getColor()+ " player for components lost during flight");
            player.penalizeForComponentsLostDuringFlight();
        }

        for(Player player : players) {
            int currentPlayerComponentsPenalty = player.getTotalComponentsPenalty();
            System.out.println(player.getColor() + " has a total of " + currentPlayerComponentsPenalty + " lost components");
            credits.get(player.getColor()).put(CreditsType.LOST_COMPONENTS_PENALTY, Float.valueOf(-currentPlayerComponentsPenalty));
        }

        /* SELL RESOURCES */
        for(Player player : players) {

            float finalValueOfResources = 0;

            if(player.checkIfHasRocketship()) {
                int totalValueOfResources = player.getBoard().getValueOfCubes(); // compute total value of stocks
                if (player.checkIfHasRocketship()) {
                    finalValueOfResources = totalValueOfResources;
                } else {
                    finalValueOfResources = (int) Math.ceil(totalValueOfResources / 2);
                }
            }
            credits.get(player.getColor()).put(CreditsType.PROFIT_FROM_RESOURCES, Float.valueOf(finalValueOfResources));

        }




        /* COMPUTE TOTAL CREDITS */
        // 1. compute TOTAL for every player
        for (PlayerColor playerColor : allPlayerColors) {
            float total = credits.get(playerColor)
                    .values()
                    .stream()
                    .reduce(0f, Float::sum);
            credits.get(playerColor).put(CreditsType.TOTAL, total);
        }

        // 2. build a ranking list (most -> least credits)
        List<Map.Entry<PlayerColor, Float>> ranking =
                allPlayerColors.stream()
                        .map(pc -> new AbstractMap.SimpleImmutableEntry<>(
                                pc, credits.get(pc).get(CreditsType.TOTAL)))
                        .sorted(Map.Entry.<PlayerColor,Float>comparingByValue()
                                .reversed())
                        .collect(Collectors.toList());

        // sending to client
        RankingDelta rankingDelta = new RankingDelta(credits, ranking);
        ServerResponse rankingResponse = new ServerResponse();
        rankingResponse.addDelta(rankingDelta);
        lobby.notifyAllPlayers(rankingResponse);


        // 3. pretty-print the results in ranked order

        //offloaded to the TUI
        /*StringBuilder results = new StringBuilder();
        for (Map.Entry<PlayerColor, Float> entry : ranking) {
            PlayerColor playerColor = entry.getKey();
            float total   = entry.getValue();

            results.append(System.lineSeparator());
            results.append(System.lineSeparator() + "computing total credits for " + playerColor + " player");
            credits.get(playerColor).forEach((type, value) -> {if(type != CreditsType.TOTAL) results.append(System.lineSeparator() + "+ " + value + " (" + type.toString() + ")");});
            results.append(System.lineSeparator() + "= " + total + " (TOTAL)");
            results.append(System.lineSeparator() + playerColor + " collected a total of " + total + " credits");
        }

        lobby.logAllPlayers(results.toString());

         */


    }





}
