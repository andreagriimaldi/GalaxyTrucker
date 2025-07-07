package it.polimi.ingsw;


import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.game.FlightBoardFacade;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.PlayerInitialization.initializePlayersInOrder;
import static org.junit.Assert.*;

public class FlightBoardTest {

    @Test
    public void flightBoardSize() {
        List<PlayerColor> listOfPlayers = new ArrayList<>();
        listOfPlayers.add(PlayerColor.RED);
        listOfPlayers.add(PlayerColor.BLUE);
        listOfPlayers.add(PlayerColor.YELLOW);
        listOfPlayers.add(PlayerColor.GREEN);

        FlightBoardFacade flightBoardTrial = null;
        FlightBoardFacade flightBoardTwo = null;
        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(FlightType.TRIAL, listOfPlayers);
        try {
            flightBoardTrial = new FlightBoardFacade(FlightType.TRIAL, orderOfAssemblyFinishers);
            flightBoardTwo = new FlightBoardFacade(FlightType.TWO, orderOfAssemblyFinishers);

        } catch (Exception e) {
            e.printStackTrace();
        }

        assertNotNull(flightBoardTrial);
        assertNotNull(flightBoardTwo);
        assertEquals(18, flightBoardTrial.getSize());
        assertEquals(24, flightBoardTwo.getSize());

    }




    @Test
    public void testFlightBoard() {
        List<Player> players;

        List<PlayerColor> listOfPlayers1 = new ArrayList<>();
        listOfPlayers1.add(PlayerColor.RED);
        listOfPlayers1.add(PlayerColor.BLUE);
        listOfPlayers1.add(PlayerColor.YELLOW);
        listOfPlayers1.add(PlayerColor.GREEN);


        FlightBoardFacade fb = createAndPopulateFLightBoard(FlightType.TWO, listOfPlayers1);
        players = fb.returnPlayersInOrder();


        fb.printFlightBoard();
        fb.movePlayerByN(players.get(0), -7);
        int posRed = players.get(0).getLaps();

        fb.printFlightBoard();
        fb.movePlayerByN(players.get(1), 29);
        int posBlue = players.get(1).getLaps();

        System.out.println();
        printFormattedFlightBoard(fb, FlightType.TWO);

        assertEquals(-1, posRed);
        assertEquals(1, posBlue);


    }


    public static FlightBoardFacade createAndPopulateFLightBoard(FlightType flightType, List<PlayerColor> orderOfPlayers) {
        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(flightType, orderOfPlayers);

        FlightBoardFacade flightBoard = null;

        try {
            flightBoard = new FlightBoardFacade(flightType, orderOfAssemblyFinishers);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flightBoard;
    }

    @Test
    public void testCyclicalTurnsTrial() {
        cyclicalTurns(FlightType.TRIAL);
    }

    @Test
    public void testCyclicalTurnsTwo() {
        cyclicalTurns(FlightType.TWO);

    }

    public void cyclicalTurns(FlightType flightTypes) {
        List<PlayerColor> listOfPlayers = new ArrayList<>();
        List<Player> players;
        listOfPlayers.add(PlayerColor.RED);
        listOfPlayers.add(PlayerColor.BLUE);
        listOfPlayers.add(PlayerColor.YELLOW);
        listOfPlayers.add(PlayerColor.GREEN);

        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(flightTypes, listOfPlayers);

        FlightBoardFacade flightBoard = null;
        try {
            flightBoard = new FlightBoardFacade(flightTypes, orderOfAssemblyFinishers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FlightBoardFacade fb = flightBoard;
        players = fb.returnPlayersInOrder();

        int max = flightTypes == FlightType.TRIAL ? 18 : 24;
        printFormattedFlightBoard(fb, flightTypes);
        System.out.println();
        for(int i = 0; i <= max; i++){
            fb.movePlayerByN(players.get(0), 1);
            fb.movePlayerByN(players.get(1), -1);
            fb.movePlayerByN(players.get(2), -5);
            printFormattedFlightBoard(fb, flightTypes);
            System.out.println();
        }
        assertEquals("red", fb.returnPlayersInOrder().get(0).getUsername());
        assertEquals("green", fb.returnPlayersInOrder().get(1).getUsername());
        assertEquals("blue", fb.returnPlayersInOrder().get(2).getUsername());
        assertEquals("yellow", fb.returnPlayersInOrder().get(3).getUsername());



    }

    public void printFormattedFlightBoard(FlightBoardFacade fb,  FlightType flight) {
        List<PlayerColor> fbr = fb.getFlightBoardRepresentation();

        if(flight == FlightType.TRIAL) {
            List<String> boardToPrint = renderFlightBoardRepresentationTrial(fbr);
            for(int i = 0; i < boardToPrint.size(); i++){
                System.out.println(boardToPrint.get(i));
            }

        }
        else if(flight == FlightType.TWO) {
            List<String> boardToPrint = renderFlightBoardRepresentationTwo(fbr);
            for(int i = 0; i < boardToPrint.size(); i++){
                System.out.println(boardToPrint.get(i));
            }
        }
    }

    public List<String> renderFlightBoardRepresentationTrial(List<PlayerColor> fbd) {
        List<String> completeFlightBoard = new ArrayList<>();
        int position = 0;

        int row = 5;
        int col = 6;
        int widthCell = 5;
        String[][] griglia = new String[row][col];

        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                griglia[i][j] = (renderEmptyCell(widthCell));
            }
        }

        for (int j = 0; j < col; j++) {
            PlayerColor currentCell = fbd.get(position);
            griglia[0][j] = renderFlightBoardCell(currentCell, position, widthCell);
            position++;
        }
        for(int k = 1; k < row - 1; k++) {
            PlayerColor currentCell = fbd.get(position);
            griglia[k][col - 1] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }
        for (int j = col - 1; j >= 0; j--) {
            PlayerColor currentCell = fbd.get(position);
            griglia[row -1][j] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }
        for(int k = row-2; k > 0; k--) {
            PlayerColor currentCell = fbd.get(position);
            griglia[k][0] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }

        for (int i = 0; i < row; i++) {
            StringBuilder[] lines = new StringBuilder[3];
            for (int k = 0; k < 3; k++) {
                lines[k] = new StringBuilder();
            }

            for (int j = 0; j < col; j++) {
                String[] cellLines = griglia[i][j].split("\n");
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
        String[][] griglia = new String[sizeMap][sizeMap];


        for(int i = 0; i < sizeMap; i++) {
            for(int j = 0; j < sizeMap; j++) {
                griglia[i][j] = (renderEmptyCell(widthCell));
            }
        }
        for (int j = 0; j < sizeMap; j++) {
            PlayerColor currentCell = fbd.get(position);
            griglia[0][j] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }
        for(int k = 1; k < sizeMap -1; k++) {
            PlayerColor currentCell = fbd.get(position);
            griglia[k][sizeMap - 1] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }
        for (int j = sizeMap -1; j >= 0; j--) {
            PlayerColor currentCell = fbd.get(position);
            griglia[sizeMap -1][j] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }
        for(int k = sizeMap -2; k > 0; k--) {
            PlayerColor currentCell = fbd.get(position);
            griglia[k][0] = renderFlightBoardCell(currentCell, position, widthCell);
            position ++;
        }

        for (int i = 0; i < sizeMap; i++) {
            StringBuilder[] lines = new StringBuilder[3];
            for (int k = 0; k < 3; k++) {
                lines[k] = new StringBuilder();
            }

            for (int j = 0; j < sizeMap; j++) {
                String[] cellLines = griglia[i][j].split("\n");
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

    public static String renderEmptyCell(int length) {
        String emptyLine = " ".repeat(length + 2); // +2 is it to "║"
        return (emptyLine + "\n").repeat(3);
    }

    static String center(String content, int width) {
        String removeColorSpaces = content.replaceAll("\u001B\\[[\\d;]+m", "");

        int left = (width - removeColorSpaces.length()) / 2;
        int right = width - removeColorSpaces.length() - left;
        return " ".repeat(left) + content + " ".repeat(right);
    }

    public String renamePlayer(PlayerColor player) {
        return switch (player) {
            case RED -> "\u001B[31m●\u001B[0m";
            case GREEN -> "\u001B[32m●\u001B[0m";
            case YELLOW -> "\u001B[33m●\u001B[0m";
            case BLUE -> "\u001B[34m●\u001B[0m";
        };
    }

    @Test
    public void removePlayer() {
        FlightType flightTypes = FlightType.TWO;
        List<PlayerColor> listOfPlayers = new ArrayList<>();
        List<Player> players;
        listOfPlayers.add(PlayerColor.RED);
        listOfPlayers.add(PlayerColor.BLUE);
        listOfPlayers.add(PlayerColor.YELLOW);
        listOfPlayers.add(PlayerColor.GREEN);

        List<Player> orderOfAssemblyFinishers = initializePlayersInOrder(flightTypes, listOfPlayers);

        FlightBoardFacade flightBoard = null;
        try {
            flightBoard = new FlightBoardFacade(flightTypes, orderOfAssemblyFinishers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FlightBoardFacade fb = flightBoard;
        players = fb != null ? fb.returnPlayersInOrder() : null;

        int steps = 24;

        printFormattedFlightBoard(fb, flightTypes);
        System.out.println();
        for(int i = 0; i <= steps; i++){
            fb.movePlayerByN(players.get(0), 1);
            try {
                List<Player> playersToRemove = fb.checkLapsForPlayersToRemove();
                for (Player playerRemoved : playersToRemove) {
                    fb.removePlayerFromBoard(playerRemoved);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            printFormattedFlightBoard(fb, flightTypes);
        }
        assertEquals(1, fb.returnPlayersInOrder().size());
    }

}