package it.polimi.ingsw.model.utilities.components;


import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;

public class PipesComponent extends Component {
    public PipesComponent(ComponentType componentType, ConnectorType[] connectors, String id) {
        super(componentType, connectors, id);
    }
}