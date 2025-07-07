package it.polimi.ingsw;

import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.game.GameSession;
import it.polimi.ingsw.model.game.GameSessionTrial;
import it.polimi.ingsw.model.game.GameSessionTwo;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.components.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.PlayerInitialization.initializePlayersInOrder;
import static it.polimi.ingsw.enums.ComponentSide.EAST;
import static it.polimi.ingsw.enums.ComponentSide.NORTH;
import static it.polimi.ingsw.enums.ConnectorType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GameSessionTest {
    @Test
    public void testPlayerInGame() {
        List<PlayerColor> listOfPlayers = new ArrayList<>();
        listOfPlayers.add(PlayerColor.RED);
        listOfPlayers.add(PlayerColor.BLUE);
        listOfPlayers.add(PlayerColor.YELLOW);
        listOfPlayers.add(PlayerColor.GREEN);

        assertEquals(4, listOfPlayers.size());
        GameSession gameTwo = new GameSessionTwo();
        for (int index = 0; index < listOfPlayers.size(); index++) {
            gameTwo.addPlayer(initializePlayersInOrder(FlightType.TWO, listOfPlayers).get(index));
            System.out.println("Username: " + gameTwo.getPlayers().get(index).getUsername() + ", Player Color: " + gameTwo.getPlayers().get(index).getColor() + ", Player ID: " + gameTwo.getID());
        }
    }

    @Test
    public void testTurnedInGame(){
        GameSession gameTwo = new GameSessionTwo();
        TurnedComponents turnedComponents = gameTwo.getTurnedComponents();

        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{SMOOTH_SURFACE, SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE}, "cannon1");
        ThrusterComponent thruster = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE}, "thruster1");
        ShieldComponent shield = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE, DOUBLE_CONNECTOR}, "shield1", NORTH, EAST);

        turnedComponents.addTurnedComponent(thruster);
        turnedComponents.addTurnedComponent(cannon);
        turnedComponents.addTurnedComponent(shield);

        assertEquals(3, turnedComponents.getTurnedList().size());
        assertEquals("single thruster", turnedComponents.takeTurnedComponentByID("thruster1").getComponentType().toString());
    }

    @Test
    public void testRocketshipInGame() {
        List<PlayerColor> listOfPlayers = new ArrayList<>();
        listOfPlayers.add(PlayerColor.RED);
        listOfPlayers.add(PlayerColor.BLUE);
        listOfPlayers.add(PlayerColor.YELLOW);
        listOfPlayers.add(PlayerColor.GREEN);


        GameSession gameTwo = new GameSessionTwo();
        for (int index = 0; index < listOfPlayers.size(); index++) {
            gameTwo.addPlayer(initializePlayersInOrder(FlightType.TWO, listOfPlayers).get(index));
            if(gameTwo.getPlayers().get(index).checkIfHasRocketship()){
                System.out.println("Username: " + gameTwo.getPlayers().get(index).getUsername() + ", Player Board: " + gameTwo.getPlayers().get(index).getBoard());
            }
            assertNotNull(gameTwo.getPlayers().get(index).getBoard());
        }
    }

    @Test
    public void testCardInGameTrial() {
        GameSession gameTrial = new GameSessionTrial();
        assertEquals(8, gameTrial.getCards().getSize());
        for (int index = 0; index < gameTrial.getCards().getSize(); index++) {
            System.out.println(gameTrial.getCards().draw());
        }
        assertEquals(4, gameTrial.getCards().getSize());
    }

    @Test
    public void testCardInGameTwo() {
        GameSession gameTwo = new GameSessionTwo();
        assertEquals(12, gameTwo.getCards().getSize());
        for (int index = 0; index < gameTwo.getCards().getSize(); index++) {
            System.out.println(gameTwo.getCards().draw());
        }
        assertEquals(12, gameTwo.getCards().getSize());
    }

    @Test
    public void testFlightBoardInGameTrial() {
        List<PlayerColor> listOfPlayers = new ArrayList<>();
        listOfPlayers.add(PlayerColor.RED);
        listOfPlayers.add(PlayerColor.BLUE);
        listOfPlayers.add(PlayerColor.YELLOW);
        listOfPlayers.add(PlayerColor.GREEN);

        GameSession gameTrial = new GameSessionTrial();
        for (int index = 0; index < listOfPlayers.size(); index++) {
            gameTrial.addPlayer(initializePlayersInOrder(FlightType.TRIAL, listOfPlayers).get(index));
        }
        try {
            gameTrial.createFlightBoard(gameTrial.getPlayers());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("FlightBoard: " + gameTrial.getFlightBoard().getFlightBoardRepresentation());
        assertNotNull(gameTrial.getFlightBoard().getFlightBoardRepresentation());
        assertEquals(18, gameTrial.getFlightBoard().getFlightBoardRepresentation().size());
        assertEquals(PlayerColor.GREEN, gameTrial.getFlightBoard().returnPlayersInOrder().get(3).getColor());
    }


    @Test
    public void testFlightBoardInGameTwo() {
        List<PlayerColor> listOfPlayers = new ArrayList<>();
        listOfPlayers.add(PlayerColor.RED);
        listOfPlayers.add(PlayerColor.BLUE);
        listOfPlayers.add(PlayerColor.YELLOW);
        listOfPlayers.add(PlayerColor.GREEN);

        GameSession gameTwo = new GameSessionTwo();
        for (int index = 0; index < listOfPlayers.size(); index++) {
            gameTwo.addPlayer(initializePlayersInOrder(FlightType.TWO, listOfPlayers).get(index));
        }
            try {
                gameTwo.createFlightBoard(gameTwo.getPlayers());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("FlightBoard: " + gameTwo.getFlightBoard().getFlightBoardRepresentation());
            assertNotNull(gameTwo.getFlightBoard().getFlightBoardRepresentation());
            assertEquals(24, gameTwo.getFlightBoard().getFlightBoardRepresentation().size());
            assertEquals(PlayerColor.RED, gameTwo.getFlightBoard().returnPlayersInOrder().getFirst().getColor());
    }



}
