package it.polimi.ingsw.model.game;

import it.polimi.ingsw.enums.AdventureType;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.adventure.*;
import it.polimi.ingsw.model.utilities.components.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * GameSession is a class used to gather around the main information about the game. It includes of course references to all the players and also to the FlightBoard
 */

public abstract class GameSession{
    private int maxplayers = 4;
    private final List<Player> players;
    private final List<Player> readyForFlight;
    private int numplayers = 0;
    private AdventureCard current;
    private FlightType type;
    private TurnedComponents turned;
    private UnturnedComponents unturned;
    private FlightBoardFacade flightBoard;
    private String ID;


    public GameSession(FlightType t){
        players = new LinkedList<>();
        readyForFlight = new LinkedList<>();
        current = new EmptyCard(AdventureType.EMPTY, null, null, 0);
        type = t;
        ComponentFactory factory = new ComponentFactory();
        unturned = new UnturnedComponents();
        turned = new TurnedComponents();
        ID = UUID.randomUUID().toString();
    }

    /**
     * getID() returns a String representing the session's ID
     */
    public String getID(){
        return ID;
    }

    /**
     * setMaxPlayers() is a method that allows to modify maxplayers within the range 2 to 4
     * The method does not allow to set maxplayers lower than the actual number of players
     * @param max represents the maximum number of players that the user wants to set
     */
    public void setMaxPlayers(int max){
        if(max < 5 && max > 1 && max >= numplayers)
            maxplayers = max;
        else throw new IllegalArgumentException("Number should be in the 2-4 range and bigger than the current number of players");
    }

    /**
     * getPlayers() simply returns a list containing a reference to all the players in the game
     * @return a list that contains every element of players
     */
    public List<Player> getPlayers(){
        return new ArrayList<>(players);
    }

    /**
     * getTurnedComponents returns all the turned components
     */
    public TurnedComponents getTurnedComponents(){
        return turned;
    }

    /**
     * getUnturnedComponents() returns all the unturned components
     */
    public UnturnedComponents getUnturnedComponents(){
        return unturned;
    }

    /**
     * isPlayerPresent() checks if the player we want to add is already in the session, in order to avoid duplicates
     * @param p is the player that we want ot insert in the game
     * @return true if p is already in the game, false otherwise
     */
    private boolean isPlayerPresent(Player p){
        return players.stream().map(player -> player.getUsername()).anyMatch(username -> p.getUsername().equals(username));
    }

    /**
     * addPlayer() check if the player is not already in the sessions, then if there's place for him. In that case the player is added to the session,
     * otherwise am exception is thrown
     * @param p is the player that we want to insert in the game
     */
    public void addPlayer(Player p){
        if(isPlayerPresent(p))
            throw new IllegalArgumentException("Player already in session");
        else{
            if(numplayers < maxplayers){
                players.add(p);
                numplayers++;
            }
            else throw new IllegalArgumentException("Access limited to " + maxplayers + " players" );
        }
    }

    /**
     * showTurnedComponents() return a list with all the turned components without removing them from the actual list
     * @return a new list containing all the elements of TurnedComponents
     */
    public List<Component> showTurnedComponents(){
        return turned.getTurnedList();
    }

    /**
     * addDiscardedComponent() takes a component discarded by a player and adds it to the pile of turned components
     * @param c is the component that we want to add to turned ones
     */
    public void addDiscardedComponent(Component c){
        turned.addTurnedComponent(c);
    }

    /**
     * createFlightBoard() is the method in charge of the board's creation.
     * @param playerInOrder is a list containing all the players in starting order
     */
    public void createFlightBoard(List<Player> playerInOrder) throws Exception {
        if (this instanceof GameSessionTrial) {
            flightBoard = new FlightBoardFacade(FlightType.TRIAL, playerInOrder);
        } else if (this instanceof GameSessionTwo) {
            flightBoard = new FlightBoardFacade(FlightType.TWO, playerInOrder);
        }
    }

    /**
     * getFlightBoard() lets other classes access the game state via the game session
     * @return the current state of the flightboard used in the game
     */
    public FlightBoardFacade getFlightBoard(){
        return flightBoard;
    }


    public FlightType getType() {
        return type;
    }

    public abstract AdventureStack getCards();

    /**
     * playerIsReady() adds the player p to the list of ready players
     */
    public void playerIsReady(Player p){
        readyForFlight.add(p);
    }
}
