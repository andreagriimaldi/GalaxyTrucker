package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;

import static it.polimi.ingsw.enums.ConnectorType.EMPTY_SPACE;

/**
 * This class is used to avoid assignment of null references when constructing the RocketshipBoard
 */

public class EmptyComponent extends Component {

    public EmptyComponent() {
        super(ComponentType.EMPTY_COMPONENT, new ConnectorType[]{EMPTY_SPACE, EMPTY_SPACE, EMPTY_SPACE, EMPTY_SPACE}, "GT-new_tiles_16_forweb157");
    }
}
