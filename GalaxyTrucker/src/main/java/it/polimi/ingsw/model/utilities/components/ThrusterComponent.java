package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ComponentSide;
import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;

import static it.polimi.ingsw.enums.ComponentSide.*;

public class ThrusterComponent extends Component {
    private ComponentSide thrusterSide;
    private final int thrusterPower;

    public ThrusterComponent(ComponentType componentType, ConnectorType[] connectors, String id){
        super(componentType, connectors, id);
        this.thrusterSide = SOUTH;
        this.thrusterPower = 1;
    }

    /**
     * this constructor is used to create an instance of DoubleThrusterComponent class
     */
    public ThrusterComponent(ComponentType componentType, ConnectorType[] connectors, String id, int power) {
        super(componentType, connectors, id);
        this.thrusterSide = SOUTH;
        this.thrusterPower = power;
    }

    public int getThrustPower() {
        return thrusterPower;
    }

    /**
     * this override prevents the thrusterComponent from being rotated
     */
    @Override
    public void rotateClockwise(){;}
}
