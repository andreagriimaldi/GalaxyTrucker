package it.polimi.ingsw.model.game;

import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;

import java.util.*;
import java.util.stream.*;

public class FlightBoardFacade {
    private final FlightBoardImplementation flightBoard;


    /**
     * the following is the constructor of the FlightBoardFacade class;
     * to work it simply requires the same parameters required by FlightBoardImplementation.
     * in fact, the constructor only constructs an object of type FlightBoardImplementation
     * and saves it as an attribute
     * @param flightType is used by flightboard to decide its initial size
     * @param initialOrderOfPlayers is used to specify the initial order of players,
     * and that depends on the order by which each finished building their rocketship
     */
    public FlightBoardFacade(FlightType flightType, List<Player> initialOrderOfPlayers) throws Exception {
        this.flightBoard = new FlightBoardImplementation(flightType, initialOrderOfPlayers);
    }


    /**
     * movePlayerByN is a facade method that encapsulate the complexity of moving
     * a specific player by making use of methods of FlightBoardImplementation
     * @param playerToMove specifies the player that will be moved on the flightboard
     * @param days specified by how many free cells the player will be moved
     *             if days is > 0, then the player will be moved forward by |days|
     *             otherwise the player will be moved backward by |days|
     */
    public void movePlayerByN(Player playerToMove, int days) {

        if(days>0) {
            for (int i = 0; i < days; i++) {
                try {
                    flightBoard.movePlayerForwardByOne(playerToMove);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            for (int i = 0; i > days; i--) {
                try {
                    flightBoard.movePlayerBackwardByOne(playerToMove);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * returnPlayersInOrder() utilizes the method returnPlayerInReverseOrder(), which returns a list of player from
     * the last in line to the leader, and reverses it, thereby effectively returning a list
     * of player that contains the leader in the first cell, then the second, and then the
     * third and last player if they are present
     * @return a list of ordered players, from first/leader to last
     */
    public List<Player> returnPlayersInOrder(){
        List<Player> playersInOrder = returnPlayersInReverseOrder();
        Collections.reverse(playersInOrder);
        return playersInOrder;
    }


    /**
     * returnPlayersInReverseOrder() utilizes a method provided by the implementation that links every player
     * with an "absolute" value of their progression. The higher the number the further the player has gone. This
     * method then orders the player from the last to the first
     * @return an ordered list of players, from last to first/leader
     */
    public List<Player> returnPlayersInReverseOrder(){
        Map<Player, Integer> allPlayersProgression = flightBoard.getAllPlayersProgression();

        List<Player> playersInReverseOrder = allPlayersProgression.entrySet().stream()
                                                .sorted(Map.Entry.comparingByValue())
                                                .map(Map.Entry::getKey)
                                                .collect(Collectors.toList());
        return playersInReverseOrder;
    }


    /**
     * checkLapsForPlayersToRemove() does a check to determine if there are lapped players on the board and, in that
     * case, returns them
     */
    public List<Player> checkLapsForPlayersToRemove() throws Exception {
        List<Player> playersToRemove = new LinkedList<>();

        Map<Player, Integer> allPlayersProgression = flightBoard.getAllPlayersProgression();

        int maxProgression = allPlayersProgression.values()
                                .stream()
                                .max(Integer::compareTo)
                                .orElse(0);

        for (Player currentPlayer : allPlayersProgression.keySet()) {
            if(maxProgression - allPlayersProgression.get(currentPlayer) > flightBoard.getFlightBoardSize()) {
                playersToRemove.add(currentPlayer);
            }
        }
        return playersToRemove;
    }


    /**
     * removePlayerFromBoard() removes the player from the actual board and also removes him from the list of players.
     * It is useful because we can represent what happens when a player abandons the flight but stays in the game (GameSession)
     * @param p is the player we want to remove
     */
    public void removePlayerFromBoard(Player p) throws Exception {
        flightBoard.removePlayerFromBoard(p);
    }


    /**
     * getAllPlayerProgression() returns a map that contains each player and his absolute position on the board
     */
    public Map<Player, Integer> getAllPlayerProgression(){
        return flightBoard.getAllPlayersProgression();
    }

    public int getSize(){
        return flightBoard.getFlightBoardSize();
    }

    public List<PlayerColor> getFlightBoardRepresentation() {
        return flightBoard.getFlightBoardRepresentation();
    }

    public void printFlightBoard() {
        System.out.println("");
        for(int i = 0; i < flightBoard.getFlightBoardSize(); i++) {
            System.out.print("[");
            PlayerColor pc = flightBoard.getFlightBoardRepresentation().get(i);
            System.out.print((i+1));
            if(pc != null) {
                System.out.print(" "+pc);
            }
            System.out.print("]");
        }
    }


}



