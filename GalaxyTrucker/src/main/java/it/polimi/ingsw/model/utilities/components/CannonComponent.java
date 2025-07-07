package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ComponentSide;
import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;

public class CannonComponent extends Component {
    private ComponentSide cannonSide;
    private float firePower;

    public CannonComponent(ComponentType componentType, ConnectorType[] connectors, String id) {
        super(componentType, connectors, id);
        this.cannonSide = ComponentSide.NORTH;
        this.firePower = 1;
    }

    /**
     * this constructor is used to create an instance of DoubleCannonComponent class
     */
    public CannonComponent(ComponentType componentType, ConnectorType[] connectors, String id, int power){
        super(componentType, connectors, id);
        this.cannonSide = ComponentSide.NORTH;
        this.firePower = power;
    }

    /**
     * rotateClockwise() rotates all the connectors calling the superclass method and then handles the switch of the
     * cannon side and its power. The power is doubled when in the end the component faces north and it is divided
     * by 2 when it's rotated east (and it will remain that as long as the component's not facing north)
     */
    @Override
    public void rotateClockwise() {
        super.rotateClockwise() ;
        cannonSide = cannonSide.getNextClockwise();
        if(cannonSide.equals(ComponentSide.NORTH)){
            firePower = 2*firePower;
        }
        else if(cannonSide.equals(ComponentSide.EAST)){
            firePower = firePower/2;
        }
    }

    /**
     * getCannonSide() simply returns the pointing direction of the cannon
     */
    public ComponentSide getCannonSide(){
        return cannonSide;
    }

    /**
     * getFirePower() returns a float indicating the power of the cannon
     */
    public float getFirePower(){
        return firePower ;
    }
}
