package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.enums.AdventureLevel;
import it.polimi.ingsw.enums.AdventureType;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.utilities.components.Component;
import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.model.utilities.components.HousingUnit;

import java.util.List;

public class EpidemyCard extends AdventureCard {

    public EpidemyCard(AdventureType type, AdventureLevel level, String ID, int days) {
        super(type, level, ID, days);
        this.skippable = false;
    }


    /**
     * method that checks if there is at least a pair of adjacent cabins with non-0 lifeforms in it
     * @param currentPlayer the player we are currently checking
     */
    public void activateEpidemy(Player currentPlayer) {
        List<Component> startingCabinComponent = currentPlayer.getBoard().getComponentByType(ComponentType.STARTING_CABIN);
        List<Component> allCabinsComponents = currentPlayer.getBoard().getComponentByType(ComponentType.SIMPLE_CABIN);
        allCabinsComponents.addAll(startingCabinComponent); // gestisce il caso in cui la starter cabin viene rimossa, in quanto la lista di StartingCabin ritornata potrebbe essere vuota

        for(Component cabinComponent : allCabinsComponents) { // infect
            HousingUnit housingUnit = (HousingUnit) cabinComponent;
            if (housingUnit.getNumberOfResidents() == 0) {
                // do nothing
            } else {

                // [] filtra tutte le housing unit con residents maggiore di zero

                for (Component adjacentCabinComponent : currentPlayer.getBoard().getAdjacentComponents(cabinComponent)) {
                    boolean isAdjacentHousingUnit = (adjacentCabinComponent.getComponentType() == ComponentType.SIMPLE_CABIN
                            || adjacentCabinComponent.getComponentType() == ComponentType.STARTING_CABIN);

                    if (isAdjacentHousingUnit) {
                        HousingUnit adjacentHousingUnit = (HousingUnit) adjacentCabinComponent;
                        if (adjacentHousingUnit.getNumberOfResidents() > 0) {
                            housingUnit.setPlagued();
                            adjacentHousingUnit.setPlagued();
                        }
                    }
                }
            }
        }


        for(Component cabinComponent : allCabinsComponents) { // kill
            HousingUnit housingUnit = (HousingUnit) cabinComponent;
            if(housingUnit.isPlagued()) {
                housingUnit.removeOneCrewMember();
                housingUnit.resetPlagued();
            }
        }

    }



    public void handle() {
        playersInOrder = ac.getGameSession().getFlightBoard().returnPlayersInOrder();

        try {
            for(Player currentPlayer : playersInOrder) {
                activateEpidemy(currentPlayer);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        drawNextCard();
    }




    public String toString(){
        return "An epidemy card";
    }
}
