package it.polimi.ingsw.model.game;

import java.util.*;

import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;

// [] suggerimento: la rimozione dei player doppiati potrebbe essere semplificata se avviene in getNextFreePosition()/getPreviousFreePosition()


public class FlightBoardImplementation {
    private final FlightBoardCell[] flightBoardArray;
    private final List<Player> players;


    /**
     * the following method is the constructor of the FlightBoardImplementation class;
     * it takes care of initializing an array of flightboardcells, such that its size
     * and the initial position of the players determined by the flightType, as specified
     * by the rules of the game
     * @param flightType is an enum whose value determine the type of game that is being player
     *                   (eg: TWO -> indicates a level two flight, whereas
     *                        TRIAL -> indicates a trial flight)
     * @param initialOrderOfPlayers specifies the order by which each finished building their rocketship
     *                              the first player in the first player that finished building its ship, and so on
     * the third and forth player are added to the flight board only if a 3 or 4 player match si being played, respectively
     */
    public FlightBoardImplementation(FlightType flightType, List<Player> initialOrderOfPlayers) throws Exception {

        players = initialOrderOfPlayers;

        int presetFlightBoardLength = 0;
        int firstPlayerStartingPosition = 0;
        int secondPlayerStartingPosition = 0;
        int thirdPlayerStartingPosition = 0;
        int fourthPlayerStartingPosition = 0;

        switch (flightType) {
            case TRIAL -> {
                presetFlightBoardLength = 18;

                firstPlayerStartingPosition = 4;
                secondPlayerStartingPosition = 2;
                thirdPlayerStartingPosition = 1;
            }
            case TWO -> {
                presetFlightBoardLength = 24;

                firstPlayerStartingPosition = 6;
                secondPlayerStartingPosition = 3;
                thirdPlayerStartingPosition = 1;
            }
        }

        this.flightBoardArray = new FlightBoardCell[presetFlightBoardLength];

        for (int i = 0; i < presetFlightBoardLength; i++) {
            this.flightBoardArray[i] = new FlightBoardCell();
        }

        getCellAt(firstPlayerStartingPosition).addPlayerToCell(initialOrderOfPlayers.get(0));
        getCellAt(secondPlayerStartingPosition).addPlayerToCell(initialOrderOfPlayers.get(1));


        if (initialOrderOfPlayers.size() >= 3) {
            getCellAt(thirdPlayerStartingPosition).addPlayerToCell(initialOrderOfPlayers.get(2));
        }
        if (initialOrderOfPlayers.size() == 4) {
            getCellAt(fourthPlayerStartingPosition).addPlayerToCell(initialOrderOfPlayers.get(3));
        }
    }


    /**
     * getCellAt() returns the cell that corresponds to a specific position on the flightboard
     * @param flightBoardPosition is an integer specifying the position on the flightboard,
     *                            where 0 corresponds to the first position on the flightboard,
     *                            and getFlightBoardSize()-1 corresponds to the last position on the flightboard
     * @return the requested cell
     */
    public FlightBoardCell getCellAt(int flightBoardPosition) {
        return flightBoardArray[flightBoardPosition];
    }


    /**
     * getFLightBoardSize() returns the size of the flightboard in a way that is independent of the data structure
     * that is used to implement it
     * @return an int that corresponds to the size of the flightboard, set during
     * the construction, and dependent on the flight type
     */
    public int getFlightBoardSize() {
        return flightBoardArray.length;
    }


    /**
     * isCellFree() simply checks whether the cell specified in the parameter is free or not
     * @param flightBoardPosition is an integer specifying the desired position on the board
     * @return true if the cell is free
     *         false otherwise
     */
    public boolean isCellFree(int flightBoardPosition) {
        return getCellAt(flightBoardPosition).isFree();
    }


    /**
     * getPlayerPosition() checks each cell from the first to the last, until it finds the requested player
     * @param requestedPlayer is the very player whose position on the board that the calling method wants
     * @return the int corresponding to the requested player's position
     * @throws Exception if the player is not on the board
     */
    public int getPlayerPosition(Player requestedPlayer) throws Exception {
        FlightBoardCell currentCell;
        for (int i = 0; i < getFlightBoardSize(); i++) {
            currentCell = getCellAt(i);
            if(!currentCell.isFree()) {
                if (currentCell.getPlayerInCell().equals(requestedPlayer)) {
                    return i;
                }
            }
        }
        throw new Exception("Player is not on the board");
    }


    /**
     * getNextPosition() seeks the next position on the board, starting from the one that it receives as input.
     * It's used to model the board as a circular path, although the data structure under it's not
     * @param currentPosition is the position whose next the calling method wants to get
     * @return the next position, which is zero if currentPosition was the last on the board
     */
    public int getNextPosition(int currentPosition) {
        int nextPosition;

        if(currentPosition == getFlightBoardSize() - 1) {
            nextPosition = 0;
        }
        else {
            nextPosition = currentPosition + 1;
        }
        return nextPosition;
    }


    /**
     * getPreviousPosition() is the dual method to getNextPosition(), but to get the previous position
     * @param currentPosition is the position whose previous the calling method wants to get
     * @return the previous position, which is the last on the board if currentPosition was the first
     */
    public int getPreviousPosition(int currentPosition) {
        int previousPosition;

        if(currentPosition == 0) {
            previousPosition = getFlightBoardSize() - 1;
        }
        else {
            previousPosition = currentPosition - 1;
        }
        return previousPosition;
    }


    /**
     * getNextFreePosition() increases the current position until there a cell containing no player is found
     * this is because the rules of the game prohibit a cell to contain more than one player
     * and so a player can't be moved to a cell that already contains a player
     * @param startingPosition is the position that the method starts checking forward from
     * @return the position of the next free cell, which may be lower than the starting position
     * if the end of the board is reached
     */
    public int getNextFreePosition(int startingPosition) {

        int currentPosition = getNextPosition(startingPosition);

        while(!isCellFree(currentPosition)) {
            currentPosition = getNextPosition(currentPosition);
        }
        return currentPosition;
    }


    /**
     * getPreviousFreePosition is the dual method to getNextFreePosition()
     * @param startingPosition is the position that the method starts checking backward from
     * @return the position of the first cell before the specified one that is found to be free
     * such position may be higher than the starting position if the first position is crossed
     */
    public int getPreviousFreePosition(int startingPosition) {

        int currentPosition = getPreviousPosition(startingPosition);

        while(!isCellFree(currentPosition)) {
            currentPosition = getPreviousPosition(currentPosition);
        }
        return currentPosition;
    }


    /**
     * movePlayerForwardByOne() removes the player from their current cell, and moves them to the next free cell
     * @param player is the player that is to be moved
     */
    public void movePlayerForwardByOne(Player player) throws Exception {

        int playerInitialPosition = getPlayerPosition(player);
        int nextFreePosition = getNextFreePosition(playerInitialPosition);

        //If the next position is smaller than the previous it means that the player's done a lap
        if(nextFreePosition < playerInitialPosition) {
            player.addOneLap();
        }

        getCellAt(playerInitialPosition).removePlayerFromCell();
        getCellAt(nextFreePosition).addPlayerToCell(player);

    }


    /**
     * movePlayerBackwardByOne() removes the player from their current cell, and moves them to the first cell before
     * the one they're currently in that is found to be free
     * @param player is the player that is to be moved
     */
    public void movePlayerBackwardByOne(Player player) throws Exception {

        int playerInitialPosition = getPlayerPosition(player);
        int previousFreePosition = getPreviousFreePosition(playerInitialPosition);

        //if the next position is greater than the previous it means that the player's lost a lap
        if(previousFreePosition > playerInitialPosition) {
            player.subtractOneLap();
        }

        getCellAt(playerInitialPosition).removePlayerFromCell();
        getCellAt(previousFreePosition).addPlayerToCell(player);

    }


    /**
     * getAllPlayersProgression() is used to know where each player is on the map
     * @return a map that contains the minimal sufficient information on the players'
     * relative positions, in order to later construct data structures containing their
     * order, in a way that is independent of the flight board implementation, or to
     * check if a player has been lapped
     */
    public Map<Player, Integer> getAllPlayersProgression(){

        Map<Player, Integer> progressionMap = new HashMap<Player, Integer>();

        for (Player p : players) {
            try{
                progressionMap.put(p, p.getLaps()*getFlightBoardSize() + getPlayerPosition(p));
            } catch (Exception e) {
                System.out.println("You're not supposed to see this");
            }
        }

        return progressionMap;
    }

    /**
     * removePlayerFromBoard simply removes the player from the board
     * @param playerToRemove is the player that is to be removed from the board
     */
    public void removePlayerFromBoard(Player playerToRemove) throws Exception {
        int position = getPlayerPosition(playerToRemove);
        getCellAt(position).removePlayerFromCell();
        players.remove(playerToRemove);
    }

    public List<PlayerColor> getFlightBoardRepresentation() {

        List<PlayerColor> flightBoardRepresentation = new LinkedList<>();

        for(int i = 0; i < flightBoardArray.length; i++) {
            FlightBoardCell currentCell = getCellAt(i);
            if(currentCell.isFree()) {
                flightBoardRepresentation.add(null);
            } else {
                try {
                    Player playerInCell = currentCell.getPlayerInCell();
                    flightBoardRepresentation.add(playerInCell.getColor());
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return flightBoardRepresentation;
    }

}







