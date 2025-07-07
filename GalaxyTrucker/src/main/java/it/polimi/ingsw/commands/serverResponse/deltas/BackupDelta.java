package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.GameProgression;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.game.GameSession;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.components.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * is the delta whose purpose is to contains all information
 * to send to a player that just reconnected to a game
 */
public class BackupDelta extends Delta{
    private FlightType type;
    private GameProgression gameProgression;
    private GamePhase gamePhase;
    private PlayerColor myColor;
    private Component[][] myBoard;
    private Component[] myReserved;
    private Component myHand;
    private Map<PlayerColor, Component[][]> othersBoards = new HashMap<>();
    private Map<PlayerColor, Component[]> othersReserved = new HashMap<>();
    private Map<PlayerColor, Component> othersHands = new HashMap<>();
    private List<Component> turned;
    private List<PlayerColor> flightBoard;

    public BackupDelta(FlightType t, GameProgression gp, GamePhase gph, GameSession gs, PlayerColor color) {
        super(DeltaType.BACKUP_DELTA);
        myColor = color;
        type = t;
        gameProgression = gp;
        gamePhase = gph;
        List<Player> players = gs.getPlayers();
        for(Player p: players){
            if(!p.getColor().equals(myColor)){
                othersBoards.put(p.getColor(), p.getBoard().getWholeShip());
                othersReserved.put(p.getColor(), p.getBoard().returnReservedComponents());
                othersHands.put(p.getColor(), p.getBoard().getHandComponent());
            }
            else{
                myBoard = p.getBoard().getWholeShip();
                myReserved = p.getBoard().returnReservedComponents();
                myHand = p.getBoard().getHandComponent();
            }
        }
        synchronized (gs.getTurnedComponents()){
            turned = gs.getTurnedComponents().getTurnedList();
        }

        if(gs.getFlightBoard() != null) {
            flightBoard = gs.getFlightBoard().getFlightBoardRepresentation();
        }


    }

    public FlightType getFlightType(){
        return type;
    }

    public GameProgression getGameProgression(){
        return gameProgression;
    }

    public GamePhase getGamePhase(){
        return gamePhase;
    }

    public PlayerColor getMyColor(){
        return myColor;
    }

    public Component[][] getMyBoard(){
        return myBoard;
    }

    public Component[] getMyReserved(){
        return myReserved;
    }

    public Component getMyHand(){
        return myHand;
    }

    public Map<PlayerColor, Component[][]> getOthersBoards(){
        return othersBoards;
    }

    public Map<PlayerColor, Component[]> getOthersReserved(){
        return othersReserved;
    }

    public Map<PlayerColor, Component> getOthersHands(){
        return othersHands;
    }

    public List<Component> getTurnedComponents(){
        return turned;
    }

    public List<PlayerColor> getFlightBoard(){
        return flightBoard;
    }




}
