package it.polimi.ingsw.model.utilities.components;


import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;
import it.polimi.ingsw.enums.CrewType;

public class AlienAddOnComponent extends PipesComponent {
    private CrewType crewType;

    public AlienAddOnComponent(ComponentType componentType, ConnectorType[] connectors, String id, CrewType alienColor){
        super(componentType, connectors, id);

        if (alienColor == CrewType.HUMAN) {
            throw new RuntimeException("the enum passed as a parameter is not an alien color");
        }
        else {
            this.crewType = alienColor;
        }
    }

    public CrewType getAlienColor() {
        return crewType;
    }
}