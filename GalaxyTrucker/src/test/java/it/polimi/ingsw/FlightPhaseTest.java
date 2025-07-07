package it.polimi.ingsw;


import it.polimi.ingsw.Flight.CustomStackFactory;
import it.polimi.ingsw.commands.userRequest.moves.*;
import it.polimi.ingsw.commands.userRequest.moves.FlightChoiceMove;
import it.polimi.ingsw.controller.AdventureController;
import it.polimi.ingsw.controller.GameLobby;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.global.ClientSession;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.adventure.AdventureCard;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static it.polimi.ingsw.PlayerInitialization.initializePlayersInOrder;





public class FlightPhaseTest {


    public static void main(String[] args) {
        FlightPhaseTest test = new FlightPhaseTest();
        test.testFlightPhase();
    }


    private LinkedBlockingQueue<String> readerQueue;
    private AdventureController adventureController;



    public void initializeUserInputManagement() {
        // INPUT READER THREAD -- OF PRODUCER KIND
        Thread userInputReader = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {

                while (!Thread.currentThread().isInterrupted()) {

                    if (scanner.hasNextLine()) {
                        String input = scanner.nextLine();
                        readerQueue.add(input);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "UserInputReader");
        userInputReader.start();


        // INPUT PROCESSOR THREAD -- OF CONSUMER KIND
        Thread inputProcessor = new Thread(() -> {
            while (true) {
                try {
                    String input = readerQueue.take();  // Blocks until input is available
                    System.out.println("Received: " + input);
                    processUserInput(input);

                } catch (InterruptedException e) {
                    System.out.println("Polling was interrupted.");
                    break;
                }
            }
        });
        inputProcessor.start();

    }


    public void processUserInput(String inputToProcess) {
        String[] inputArgs = inputToProcess.split(" ");
        String playerColorString = inputArgs[0];

        PlayerColor playerColor = null;

        switch(playerColorString) {
            case "RED" -> {
                playerColor = PlayerColor.RED;
            }
            case "BLUE" -> {
                playerColor = PlayerColor.BLUE;
            }
            case "YELLOW" -> {
                playerColor = PlayerColor.YELLOW;
            }
            case "GREEN" -> {
                playerColor = PlayerColor.GREEN;
            }
            default -> {
                System.out.println("invalid color");
                return;
            }
        }

        if(inputArgs.length == 2) {
            String stringChoice = inputArgs[1];

            if (stringChoice.equals("FORFEIT")) {
                ForfeitMove forfeitMove = MoveFactory.createForfeitMove();
                forfeitMove.attachColor(playerColor);
                adventureController.addMoveToQueue(forfeitMove);
            } else if(Constants.isNumeric(stringChoice)) {
                int choice = Integer.parseInt(stringChoice);
                FlightChoiceMove adventureChoiceMove = MoveFactory.createFlightChoiceMove(choice);
                adventureChoiceMove.attachColor(playerColor);
                adventureController.addMoveToQueue(adventureChoiceMove);
            } else {
                System.out.println("Parameter must be either a number or FORFEIT");
            }

        }
    }



    public void testFlightPhase() {

        this.readerQueue = new LinkedBlockingQueue<>();
        initializeUserInputManagement();


        FlightType flightType = FlightType.TWO;
        GameSessionTrial gameSessionTrial = new GameSessionTrial();
        GameSessionTwo gameSessionTwo = new GameSessionTwo();

        GameSession gameSession = gameSessionTwo;



        /** INITIALIZING PLAYERS **/

        List<PlayerColor> orderOfAssemblyFinishersColor = new ArrayList<>();
        orderOfAssemblyFinishersColor.add(PlayerColor.RED);
        orderOfAssemblyFinishersColor.add(PlayerColor.BLUE);
        orderOfAssemblyFinishersColor.add(PlayerColor.YELLOW);
        orderOfAssemblyFinishersColor.add(PlayerColor.GREEN);


        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(flightType, orderOfAssemblyFinishersColor);



        /** INITIALIZING GAME LOBBY **/

        GameLobby lobby = new GameLobby(orderOfAssemblyFinishersColor.size(), true);


        Map<String, Player> usernameToPlayer = new HashMap<>();
        Map<PlayerColor, ClientSession> colorToClient = new HashMap<>();


        for (Player player : orderOfAssemblyFinishers) {
            usernameToPlayer.put(player.getUsername(), player);
            colorToClient.put(player.getColor(), null);
        }

        lobby.addColorToClientMap(colorToClient);
        lobby.addUsernameToPlayerMap(usernameToPlayer);


        for(PlayerColor playerColor : orderOfAssemblyFinishersColor) {
            lobby.createLogBufferForDisconnectedColor(playerColor);
        }




        /** INITIALIZING GAME **/

        for (String username : lobby.getAllUsernames()) {
            Player player = lobby.getPlayerFromUsername(username);
            gameSession.addPlayer(player);

            PlayerColor color = player.getColor();
        }




        /* costruisci deck fatto solo di ship card */
        List<AdventureCard> adventureCardList = CustomStackFactory.build_station_card_stack();
        AdventureStack adventureStack = new AdventureStack(adventureCardList);

        this.adventureController = new AdventureController(adventureStack, gameSession, lobby);
        adventureController.initializeFlight(orderOfAssemblyFinishers);
        adventureController.startProcessingMoves();
        adventureController.beginFlight();


        while(true) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }





}