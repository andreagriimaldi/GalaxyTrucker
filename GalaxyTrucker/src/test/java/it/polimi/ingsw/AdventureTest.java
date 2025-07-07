package it.polimi.ingsw;

import it.polimi.ingsw.Flight.CustomStackFactory;
import it.polimi.ingsw.commands.userRequest.moves.FlightChoiceMove;
import it.polimi.ingsw.commands.userRequest.moves.ForfeitMove;
import it.polimi.ingsw.commands.userRequest.moves.MoveFactory;
import it.polimi.ingsw.controller.AdventureController;
import it.polimi.ingsw.controller.GameLobby;
import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.global.ClientSession;
import it.polimi.ingsw.model.game.AdventureStack;
import it.polimi.ingsw.model.game.GameSession;
import it.polimi.ingsw.model.game.GameSessionTwo;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.adventure.*;
import it.polimi.ingsw.model.utilities.components.CubeToken;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static it.polimi.ingsw.PlayerInitialization.initializePlayersInOrder;
import static org.junit.Assert.*;

public class AdventureTest {
    @Test
    public void createAndPopulateAdventureStack() {
        AdventureStack adventureStack = new AdventureStack(true);
        EnemiesCard enemies = new EnemiesCard(AdventureType.ENEMY, AdventureLevel.LEVEL1, UUID.randomUUID().toString(), -1, 4, 2, new CubeToken[]{ new CubeToken(ResourceTypes.YELLOWCUBE), new CubeToken(ResourceTypes.GREENCUBE), new CubeToken(ResourceTypes.BLUECUBE)});
        StardustCard stardust = new StardustCard(AdventureType.STARDUST, AdventureLevel.LEVEL1, UUID.randomUUID().toString(), 0);
        OpenSpaceCard openSpace = new OpenSpaceCard (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL1, UUID.randomUUID().toString(), 0);
        MeteoriteCard meteorite = new MeteoriteCard (AdventureType.METEORITE, AdventureLevel.LEVEL1, UUID.randomUUID().toString(), 0, new int[]{5,4,2});
        PlanetCard planet = new PlanetCard (AdventureType.PLANET, AdventureLevel.LEVEL1, UUID.randomUUID().toString(), -2, new CubeToken[][]{{new CubeToken(ResourceTypes.REDCUBE), new CubeToken(ResourceTypes.REDCUBE)}, {new CubeToken(ResourceTypes.REDCUBE), new CubeToken(ResourceTypes.BLUECUBE), new CubeToken(ResourceTypes.BLUECUBE)}, {new CubeToken(ResourceTypes.YELLOWCUBE)}});

        adventureStack.addCard(enemies);
        adventureStack.addCard(stardust);
        adventureStack.addCard(openSpace);
        adventureStack.addCard(meteorite);
        adventureStack.addCard(planet);

        assertEquals(5, adventureStack.getSize());

        System.out.println("the adventure deck contains " + adventureStack.getSize() + " cards:");
        for (int i = 0; i < adventureStack.getSize(); i++) {
            System.out.println(adventureStack.peek().get(i));
        }
        System.out.println(renderAdventureCard(adventureStack.draw()));
    }

    @Test
    public void TrialCards(){
        CardFactory cards = new CardFactory(FlightType.TRIAL);
        List<AdventureCard> trialStack = cards.getCards();
        for(int i = 0; i < trialStack.size(); i++){
            System.out.println(renderAdventureCard(trialStack.get(i)));
        }
        assertEquals(8, cards.buildAdventureStackTrial().size());

    }

    @Test
    public void TwoCards(){
        CardFactory cards = new CardFactory(FlightType.TWO);
        List<AdventureCard> twoStack = cards.getCards();
        for(int i = 0; i < twoStack.size(); i++){
            System.out.println(renderAdventureCard(twoStack.get(i)));
        }
        assertEquals(3, cards.buildAdventureStackLevelTwo(true).getSize());

    }

    @Test
    public void EnemiesCardsTest(){
        GameSessionTwo gameSessionTwo = new GameSessionTwo();
        AdventureStack adventureStack = new AdventureStack(true);

        List<PlayerColor> listOfPlayers = Arrays.asList(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.YELLOW, PlayerColor.GREEN);
        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(FlightType.TRIAL, listOfPlayers);
        GameLobby lobby = new GameLobby(orderOfAssemblyFinishers.size(), true);
        Map<String, Player> usernameToPlayer = new HashMap<>();
        Map<PlayerColor, ClientSession> colorToClient = new HashMap<>();

        for (Player player : orderOfAssemblyFinishers) {
            usernameToPlayer.put(player.getUsername(), player);
            colorToClient.put(player.getColor(), null);
        }

        lobby.addColorToClientMap(colorToClient);
        lobby.addUsernameToPlayerMap(usernameToPlayer);

        for(Player player : orderOfAssemblyFinishers) {
            lobby.createLogBufferForDisconnectedColor(player.getColor());
        }

        for (String username : lobby.getAllUsernames()) {
            Player player = lobby.getPlayerFromUsername(username);
            gameSessionTwo.addPlayer(player);
        }

        EnemiesCard enemies = new EnemiesCard(AdventureType.ENEMY, AdventureLevel.LEVEL2, "GT-cards_II_IT_013", -2, 6, new int[]{5,1,5}, 7);
        adventureStack.addCard(enemies);

        assertEquals(1, adventureStack.getSize());
        assertEquals(AdventureType.ENEMY, adventureStack.peek().get(0).getType());

        AdventureController adventureController = new AdventureController(adventureStack, gameSessionTwo, lobby);
        assertNotNull(adventureController);

        adventureController.initializeFlight(orderOfAssemblyFinishers);
        adventureController.startProcessingMoves();
        adventureController.beginFlight();

        assertEquals(0, adventureStack.getSize());

    }

    @Test
    public void EpidemyCardTest(){
        GameSessionTwo gameSessionTwo = new GameSessionTwo();
        AdventureStack adventureStack = new AdventureStack(true);

        List<PlayerColor> listOfPlayers = Arrays.asList(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.YELLOW, PlayerColor.GREEN);
        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(FlightType.TRIAL, listOfPlayers);
        GameLobby lobby = new GameLobby(orderOfAssemblyFinishers.size(), true);
        Map<String, Player> usernameToPlayer = new HashMap<>();
        Map<PlayerColor, ClientSession> colorToClient = new HashMap<>();

        for (Player player : orderOfAssemblyFinishers) {
            usernameToPlayer.put(player.getUsername(), player);
            colorToClient.put(player.getColor(), null);
        }

        lobby.addColorToClientMap(colorToClient);
        lobby.addUsernameToPlayerMap(usernameToPlayer);

        for(Player player : orderOfAssemblyFinishers) {
            lobby.createLogBufferForDisconnectedColor(player.getColor());
        }

        for (String username : lobby.getAllUsernames()) {
            Player player = lobby.getPlayerFromUsername(username);
            gameSessionTwo.addPlayer(player);
        }

        EpidemyCard epidemy = new EpidemyCard(AdventureType.EPIDEMY, AdventureLevel.LEVEL2, "GT-cards_II_IT_015", 0);
        adventureStack.addCard(epidemy);

        assertEquals(1, adventureStack.getSize());
        assertEquals(AdventureType.EPIDEMY, adventureStack.peek().get(0).getType());

        AdventureController adventureController = new AdventureController(adventureStack, gameSessionTwo, lobby);

        adventureController.initializeFlight(orderOfAssemblyFinishers);
        adventureController.startProcessingMoves();
        adventureController.beginFlight();
        assertEquals(0, adventureStack.getSize());

    }

    @Test
    public void MeteoriteCardTest(){
        GameSessionTwo gameSessionTwo = new GameSessionTwo();
        AdventureStack adventureStack = new AdventureStack(true);
        List<PlayerColor> listOfPlayers = Arrays.asList(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.YELLOW, PlayerColor.GREEN);
        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(FlightType.TRIAL, listOfPlayers);
        GameLobby lobby = new GameLobby(orderOfAssemblyFinishers.size(), true);

        Map<String, Player> usernameToPlayer = new HashMap<>();
        Map<PlayerColor, ClientSession> colorToClient = new HashMap<>();

        for (Player player : orderOfAssemblyFinishers) {
            usernameToPlayer.put(player.getUsername(), player);
            colorToClient.put(player.getColor(), null);
        }

        lobby.addColorToClientMap(colorToClient);
        lobby.addUsernameToPlayerMap(usernameToPlayer);

        for(Player player : orderOfAssemblyFinishers) {
            lobby.createLogBufferForDisconnectedColor(player.getColor());
        }

        for (String username : lobby.getAllUsernames()) {
            Player player = lobby.getPlayerFromUsername(username);
            gameSessionTwo.addPlayer(player);
        }

        MeteoriteCard meteorite = new MeteoriteCard(AdventureType.METEORITE, AdventureLevel.LEVEL2, "GT-cards_II_IT_0111", 0, new int[]{1,1,6,2,2});
        adventureStack.addCard(meteorite);

        assertEquals(1, adventureStack.getSize());
        assertEquals(AdventureType.METEORITE, adventureStack.peek().get(0).getType());

        AdventureController adventureController = new AdventureController(adventureStack, gameSessionTwo, lobby);
        adventureController.initializeFlight(orderOfAssemblyFinishers);
        adventureController.startProcessingMoves();
        adventureController.beginFlight();

        assertEquals(0, adventureStack.getSize());

    }

    @Test
    public void OpenSpaceCardTest(){
        GameSessionTwo gameSessionTwo = new GameSessionTwo();
        AdventureStack adventureStack = new AdventureStack(true);
        List<PlayerColor> listOfPlayers = Arrays.asList(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.YELLOW, PlayerColor.GREEN);
        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(FlightType.TRIAL, listOfPlayers);
        GameLobby lobby = new GameLobby(orderOfAssemblyFinishers.size(), true);

        Map<String, Player> usernameToPlayer = new HashMap<>();
        Map<PlayerColor, ClientSession> colorToClient = new HashMap<>();

        for (Player player : orderOfAssemblyFinishers) {
            usernameToPlayer.put(player.getUsername(), player);
            colorToClient.put(player.getColor(), null);
        }

        lobby.addColorToClientMap(colorToClient);
        lobby.addUsernameToPlayerMap(usernameToPlayer);

        for(Player player : orderOfAssemblyFinishers) {
            lobby.createLogBufferForDisconnectedColor(player.getColor());
        }

        for (String username : lobby.getAllUsernames()) {
            Player player = lobby.getPlayerFromUsername(username);
            gameSessionTwo.addPlayer(player);
        }

        OpenSpaceCard openSpace = new OpenSpaceCard(AdventureType.OPEN_SPACE, AdventureLevel.LEVEL2, "GT-cards_II_IT_018", 0);
        adventureStack.addCard(openSpace);

        assertEquals(1, adventureStack.getSize());
        assertEquals(AdventureType.OPEN_SPACE, adventureStack.peek().get(0).getType());

        AdventureController adventureController = new AdventureController(adventureStack, gameSessionTwo, lobby);
        adventureController.initializeFlight(orderOfAssemblyFinishers);
        adventureController.startProcessingMoves();
        adventureController.beginFlight();

        assertEquals(0, adventureStack.getSize());

    }

    @Test
    public void ShipCardTest(){
        GameSessionTwo gameSessionTwo = new GameSessionTwo();
        AdventureStack adventureStack = new AdventureStack(true);
        List<PlayerColor> listOfPlayers = Arrays.asList(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.YELLOW, PlayerColor.GREEN);
        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(FlightType.TRIAL, listOfPlayers);
        GameLobby lobby = new GameLobby(orderOfAssemblyFinishers.size(), true);

        Map<String, Player> usernameToPlayer = new HashMap<>();
        Map<PlayerColor, ClientSession> colorToClient = new HashMap<>();

        for (Player player : orderOfAssemblyFinishers) {
            usernameToPlayer.put(player.getUsername(), player);
            colorToClient.put(player.getColor(), null);
        }

        lobby.addColorToClientMap(colorToClient);
        lobby.addUsernameToPlayerMap(usernameToPlayer);

        for(Player player : orderOfAssemblyFinishers) {
            lobby.createLogBufferForDisconnectedColor(player.getColor());
        }

        for (String username : lobby.getAllUsernames()) {
            Player player = lobby.getPlayerFromUsername(username);
            gameSessionTwo.addPlayer(player);
        }

        ShipCard ship = new ShipCard(AdventureType.SHIP, AdventureLevel.LEVEL2, "GT-cards_II_IT_0118", -2, 5, 8);
        adventureStack.addCard(ship);

        assertEquals(1, adventureStack.getSize());
        assertEquals(AdventureType.SHIP, adventureStack.peek().get(0).getType());

        AdventureController adventureController = new AdventureController(adventureStack, gameSessionTwo, lobby);
        adventureController.initializeFlight(orderOfAssemblyFinishers);
        adventureController.startProcessingMoves();
        adventureController.beginFlight();

        assertEquals(0, adventureStack.getSize());

    }

    @Test
    public void WarzoneCardTest(){
        GameSessionTwo gameSessionTwo = new GameSessionTwo();
        AdventureStack adventureStack = new AdventureStack(true);
        List<PlayerColor> listOfPlayers = Arrays.asList(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.YELLOW, PlayerColor.GREEN);
        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(FlightType.TRIAL, listOfPlayers);
        GameLobby lobby = new GameLobby(orderOfAssemblyFinishers.size(), true);

        Map<String, Player> usernameToPlayer = new HashMap<>();
        Map<PlayerColor, ClientSession> colorToClient = new HashMap<>();

        for (Player player : orderOfAssemblyFinishers) {
            usernameToPlayer.put(player.getUsername(), player);
            colorToClient.put(player.getColor(), null);
        }

        lobby.addColorToClientMap(colorToClient);
        lobby.addUsernameToPlayerMap(usernameToPlayer);

        for(Player player : orderOfAssemblyFinishers) {
            lobby.createLogBufferForDisconnectedColor(player.getColor());
        }

        for (String username : lobby.getAllUsernames()) {
            Player player = lobby.getPlayerFromUsername(username);
            gameSessionTwo.addPlayer(player);
        }

        WarzoneCard warzone = new WarzoneCard(AdventureType.WARZONE, AdventureLevel.LEVEL2, "GT-cards_II_IT_0116", -4, 0,
                new int[]{1,4,2,7}, 3, new int[]{3,2,1}, new int[]{4,2,1});

        adventureStack.addCard(warzone);

        assertEquals(1, adventureStack.getSize());
        assertEquals(AdventureType.WARZONE, adventureStack.peek().get(0).getType());

        AdventureController adventureController = new AdventureController(adventureStack, gameSessionTwo, lobby);
        adventureController.initializeFlight(orderOfAssemblyFinishers);
        adventureController.startProcessingMoves();
        adventureController.beginFlight();

        assertEquals(0, adventureStack.getSize());

    }

    @Test
    public void StationCardTest(){
        GameSessionTwo gameSessionTwo = new GameSessionTwo();
        AdventureStack adventureStack = new AdventureStack(true);
        List<PlayerColor> listOfPlayers = Arrays.asList(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.YELLOW, PlayerColor.GREEN);
        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(FlightType.TRIAL, listOfPlayers);
        GameLobby lobby = new GameLobby(orderOfAssemblyFinishers.size(), true);

        Map<String, Player> usernameToPlayer = new HashMap<>();
        Map<PlayerColor, ClientSession> colorToClient = new HashMap<>();

        for (Player player : orderOfAssemblyFinishers) {
            usernameToPlayer.put(player.getUsername(), player);
            colorToClient.put(player.getColor(), null);
        }

        lobby.addColorToClientMap(colorToClient);
        lobby.addUsernameToPlayerMap(usernameToPlayer);

        for(Player player : orderOfAssemblyFinishers) {
            lobby.createLogBufferForDisconnectedColor(player.getColor());
        }

        for (String username : lobby.getAllUsernames()) {
            Player player = lobby.getPlayerFromUsername(username);
            gameSessionTwo.addPlayer(player);
        }

        StationCard stationCard = new StationCard(AdventureType.STATION, AdventureLevel.LEVEL1, "GT-cards_I_IT_0119", -1, 5,
                new CubeToken[]{new CubeToken(ResourceTypes.YELLOWCUBE), new CubeToken(ResourceTypes.GREENCUBE)});

        adventureStack.addCard(stationCard);

        assertEquals(1, adventureStack.getSize());
        assertEquals(AdventureType.STATION, adventureStack.peek().get(0).getType());

        AdventureController adventureController = new AdventureController(adventureStack, gameSessionTwo, lobby);
        adventureController.initializeFlight(orderOfAssemblyFinishers);
        adventureController.startProcessingMoves();
        adventureController.beginFlight();

        assertEquals(0, adventureStack.getSize());

    }

    @Test
    public void completeEnemyTest(){
        EnemiesCard enemies = new EnemiesCard
                (AdventureType.ENEMY, AdventureLevel.LEVEL2, "GT-cards_II_IT_013", -2, 6, new int[]{5,1,5}, 7);
        flightToTest("ENEMY", enemies);
    }

    @Test
    public void completePlanetTest(){
       PlanetCard planetCard = new PlanetCard
                (AdventureType.PLANET, AdventureLevel.LEVEL1, "GT-cards_I_IT_0112", -3,
                        new CubeToken[][]{
                                {
                                        new CubeToken(ResourceTypes.REDCUBE),
                                        new CubeToken(ResourceTypes.GREENCUBE),
                                        new CubeToken(ResourceTypes.BLUECUBE),
                                        new CubeToken(ResourceTypes.BLUECUBE),
                                        new CubeToken(ResourceTypes.BLUECUBE)
                                }, {
                                new CubeToken(ResourceTypes.REDCUBE),
                                new CubeToken(ResourceTypes.YELLOWCUBE),
                                new CubeToken(ResourceTypes.BLUECUBE)
                        }, {
                                new CubeToken(ResourceTypes.REDCUBE),
                                new CubeToken(ResourceTypes.BLUECUBE),
                                new CubeToken(ResourceTypes.BLUECUBE),
                                new CubeToken(ResourceTypes.BLUECUBE)
                        }, {
                                new CubeToken(ResourceTypes.REDCUBE),
                                new CubeToken(ResourceTypes.GREENCUBE)
                        }
                        });
        flightToTest("PLANET", planetCard);
    }

    public void initializeUserInputManagementEnemy(LinkedBlockingQueue<String> readerQueue, AdventureController adventureController) {
        // INPUT READER THREAD -- OF PRODUCER KIND
        Thread simulatedInputProducer = new Thread(() -> {
            try {
                Thread.sleep(2000); // 2 secondi prima del primo input
                readerQueue.add("RED 2");

                Thread.sleep(3000); // 3 secondi dopo il primo input
                readerQueue.add("RED 1");

                Thread.sleep(3000); // 3 secondi dopo il primo input
                readerQueue.add("RED 0");
                readerQueue.add("BLUE 0");
                readerQueue.add("YELLOW 0");
                readerQueue.add("GREEN 0");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "SimulatedInputProducer");
        simulatedInputProducer.start();



        // INPUT PROCESSOR THREAD -- OF CONSUMER KIND
        Thread inputProcessor = new Thread(() -> {
            while (true) {
                try {
                    String input = readerQueue.take();  // Blocks until input is available
                    System.out.println("Received: " + input);
                    processUserInput(input, adventureController);

                } catch (InterruptedException e) {
                    System.out.println("Polling was interrupted.");
                    break;
                }
            }
        });
        inputProcessor.start();

    }

    public void initializeUserInputManagementPlanet(LinkedBlockingQueue<String> readerQueue, AdventureController adventureController) {
        // INPUT READER THREAD -- OF PRODUCER KIND
        Thread simulatedInputProducer = new Thread(() -> {
            try {
                Thread.sleep(2000); // 2 secondi prima del primo input
                readerQueue.add("RED 0");

                Thread.sleep(3000); // 3 secondi dopo il primo input
                readerQueue.add("BLUE 1");

                Thread.sleep(3000); // 3 secondi dopo il primo input
                readerQueue.add("YELLOW 2");

                Thread.sleep(3000); // 3 secondi dopo il primo input
                readerQueue.add("GREEN 3");

                Thread.sleep(3000);
                readerQueue.add("RED 0");
                readerQueue.add("BLUE 0");
                readerQueue.add("YELLOW 0");
                readerQueue.add("GREEN 0");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "SimulatedInputProducer");
        simulatedInputProducer.start();



        // INPUT PROCESSOR THREAD -- OF CONSUMER KIND
        Thread inputProcessor = new Thread(() -> {
            while (true) {
                try {
                    String input = readerQueue.take();  // Blocks until input is available
                    System.out.println("Received: " + input);
                    processUserInput(input, adventureController);

                } catch (InterruptedException e) {
                    System.out.println("Polling was interrupted.");
                    break;
                }
            }
        });
        inputProcessor.start();

    }

    public void processUserInput(String inputToProcess, AdventureController adventureController) {
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

    public void flightToTest(String typeCard, AdventureCard adventureCard) {
        LinkedBlockingQueue<String> readerQueue = new LinkedBlockingQueue<>();

        FlightType flightType = FlightType.TWO;
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
        }

        AdventureStack adventureStack = new AdventureStack(true);
        adventureStack.addCard(adventureCard);

        AdventureController adventureController = new AdventureController(adventureStack, gameSession, lobby);
        adventureController.initializeFlight(orderOfAssemblyFinishers);
        adventureController.startProcessingMoves();
        adventureController.beginFlight();

        if(typeCard.equals("ENEMY")){
            initializeUserInputManagementEnemy(readerQueue, adventureController);
        }
        else {
            initializeUserInputManagementPlanet(readerQueue, adventureController);
        }

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(typeCard.equals("ENEMY")){
            assertEquals(7, orderOfAssemblyFinishers.get(0).getCredits());
            assertEquals(7, orderOfAssemblyFinishers.get(0).getBoard().checkTotalBatteries());
        }
        else {
            assertEquals(0, orderOfAssemblyFinishers.get(0).getBoard().getValueOfCubes());
            assertEquals(5, orderOfAssemblyFinishers.get(1).getBoard().getValueOfCubes());
            assertEquals(5, orderOfAssemblyFinishers.get(2).getBoard().getValueOfCubes());
            assertEquals(5, orderOfAssemblyFinishers.get(3).getBoard().getValueOfCubes());
        }

    }


    public String renderAdventureCard(AdventureCard card) {
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
        return sb.toString();
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
}
