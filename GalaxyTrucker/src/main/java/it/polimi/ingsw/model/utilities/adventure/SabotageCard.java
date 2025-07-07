package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.enums.AdventureLevel;
import it.polimi.ingsw.enums.AdventureType;
import it.polimi.ingsw.model.playerset.Player;

import java.util.List;
import java.util.Random;

public class SabotageCard extends AdventureCard {

    public SabotageCard(AdventureType type, AdventureLevel level, String ID, int days) {
        super(type, level, ID, days);
        this.skippable = false;
    }

    public Player getPlayerWithLowestCrewNumber() {

        List<Player> playersInReverseOrder = ac.getGameSession().getFlightBoard().returnPlayersInReverseOrder(); // uses reverse order so that, in situations where there's more than one player possessing the least number of crew memebers, the one that's more ahead in the race will be chosen

        int lowestCrewNumber = 0;
        Player playerWithLowestCrewNumber = null;

        for(Player p : playersInReverseOrder) {
            if (playerWithLowestCrewNumber == null) {
                playerWithLowestCrewNumber = p;
                lowestCrewNumber = p.getBoard().checkTotalCrew();
            } if(p.getBoard().checkTotalCrew() < lowestCrewNumber)  {
                playerWithLowestCrewNumber = p;
                lowestCrewNumber = p.getBoard().checkTotalCrew();
            }
        }

        return playerWithLowestCrewNumber;
    }



    public boolean attemptSabotage(Player activePlayer) {


        Random random = new Random();
        int row = (random.nextInt(6) + 1) + (random.nextInt(6) + 1) - 5;
        int col = (random.nextInt(6) + 1) + (random.nextInt(6) + 1) - 4;

        ac.getLobby().logAllPlayers("extracted row: " + row + ", col: " + col);
        if(activePlayer.getBoard().isCellValid(row, col) && !activePlayer.getBoard().isCellFree(row, col)){
            activePlayer.getBoard().removeComponent(row, col);
            ac.getLobby().logAllPlayers("sabotage attempt successful");
            return true;
        } else {
            ac.getLobby().logAllPlayers("sabotage attempt failed");
            return false;
        }

    }



    public void handle() {
        playersInOrder = ac.getGameSession().getFlightBoard().returnPlayersInOrder();

        if(playersInOrder.size() == 1) {
            ac.getLobby().logAllPlayers("sabotage card has to be skipped if there's only one player left on the flightboard");
        } else {
            boolean isTileEliminated = false;
            Player playerWithLowestCrewNumber = getPlayerWithLowestCrewNumber();
            ac.getLobby().logAllPlayers(playerWithLowestCrewNumber.getColor() + " player was found to be the player with the lowest number of crew members that is also further ahead in the race");

            int sabotageTries = 0;

            do {
                ac.getLobby().logAllPlayers("attempting #" + (sabotageTries+1) + " out of 3 sabotage tries");
                isTileEliminated = attemptSabotage(playerWithLowestCrewNumber);
            } while(sabotageTries++ < 3 && !isTileEliminated);
        }

        drawNextCard();
    }


    public String toString(){
        return "A sabotage card";
    }
}
