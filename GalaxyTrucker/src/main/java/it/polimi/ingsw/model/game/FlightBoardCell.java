package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.playerset.Player;

/**
 * the purpose of this class is to provide a data structure and its methods
 * to "hold" a player within any specific position of the flight board
 * it also allows to return the contained player, or check if a specific player
 * is present
 *
 * the reasoning behind this is to avoid having FlightBoardImplementation
 * be a list mostly containing null objects
 *
 * NOTE: to check if a cell is free before checking if a specific player is present,
 * or before getting the player contained by the cell, IS A RESPONSIBILITY OF THE CALLING METHOD!
 */
public class FlightBoardCell {

    private Player playerInCell; // A CELL CONTAINS ONE AND ONLY ONE PLAYER



    /**
     * initializes a cell, initially containing no player, with a null object
     */
    public FlightBoardCell() {
        playerInCell = null;
    }



    /**
     * isFree() checks if the cell contains a player or not
     * @return true if the cell doesn't contain a player
     *         false if the cell does indeed contain a player
     */
    public boolean isFree() {
        if(playerInCell == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * getPlayerInCell() returns the player who's on the cell, only if the cell is not empty
     * @throws Exception if the cell is empty
     */
    public Player getPlayerInCell() throws Exception {
        if(isFree()){
            throw new Exception("No player in this cell");
        }
        else return playerInCell;
    }


    /**
     * removePlayerFromCell() simply sets player to null
     */
    public void removePlayerFromCell() {
        playerInCell = null;
    }


    /**
     * addPlayerToCell() adds the given player to the cell if it's empty, otherwise throws an exception
     */
    public void addPlayerToCell(Player playerToAdd) throws Exception {
        if(isFree()){
            this.playerInCell = playerToAdd;
        }
        else throw new Exception("There's already a player here");
    }


}
