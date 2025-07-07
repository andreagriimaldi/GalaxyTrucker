package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.enums.AdventureLevel;
import it.polimi.ingsw.enums.AdventureType;
import it.polimi.ingsw.model.playerset.Player;

import java.util.List;

public class StardustCard extends AdventureCard {

    public StardustCard(AdventureType type, AdventureLevel level, String ID, int days) {
        super(type, level, ID, days);
        this.skippable = false;
    }

    public void handle() {
        List<Player> playersInReverseOrder = ac.getGameSession().getFlightBoard().returnPlayersInReverseOrder();
        for(int i = 0; i < playersInReverseOrder.size(); i++) {
            Player player = playersInReverseOrder.get(i);
            days = player.getBoard().countExposedConnectors();
            ac.getGameSession().getFlightBoard().movePlayerByN(player, -days);

            ac.getLobby().logAllPlayers(player.getColor() + " player took " + days + " steps backwards");
        }
        drawNextCard();
    }

    public String toString(){
        return "A stardust card";
    }
}