package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;
import it.polimi.ingsw.enums.CrewType;
import it.polimi.ingsw.model.playerset.PlayerColor;

public class StarterCabinComponent extends HousingUnit {
    private PlayerColor color;

    public StarterCabinComponent(ComponentType componentType, ConnectorType[] connectors, String id, PlayerColor c) {
        super(componentType, connectors, id);
        color = c;
    }

    @Override
    public void addOneCrewMember(CrewMember crewMember) {
        if(residents.size() < maxCapacity) {
            if (crewMember.getCrewType().equals(CrewType.HUMAN)) {
                this.residents.add(crewMember);
            }
        }
    }

    public PlayerColor getColor() {
        return color;
    }

}
