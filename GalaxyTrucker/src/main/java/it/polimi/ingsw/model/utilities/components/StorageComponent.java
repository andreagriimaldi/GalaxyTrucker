package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;

public abstract class StorageComponent extends Component {

    public StorageComponent(ComponentType componentType, ConnectorType[] connectors, String id) {
        super(componentType, connectors, id);
    }

    public abstract void addResource(ResourceType resource) throws Exception;

    public abstract int getTotalResource();

    public abstract int getMaxCapacity();
}

