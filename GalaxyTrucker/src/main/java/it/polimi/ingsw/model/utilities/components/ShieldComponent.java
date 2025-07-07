package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ComponentSide;
import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;

import java.util.*;

public class ShieldComponent extends Component {
    private ComponentSide[] protect = new ComponentSide[2];

    public ShieldComponent(ComponentType componentType, ConnectorType[] connectors, String id, ComponentSide one, ComponentSide two)  {
        super(componentType, connectors, id) ;
        protect[0] = one ;
        protect[1] = two ;

    }

    /**
     * isSideProtected() checks if a certain side is protected by the shield
     * @param side is the side that we want to make sure is protected
     * @return true if the side is actually protected, false otherwise
     */
    public boolean isSideProtected(ComponentSide side) {
        if(side.equals(protect[0]) || side.equals(protect[1]))
            return true;
        else return false;
    }

    /**
     * protectedSides() returns a list containing the two protected sides
     */
    public List<ComponentSide> protectedSides(){
        return new ArrayList<>(List.of(protect));
    }

    /**
     * rotateClockwise() handles the rotation by calling the superclass method and then handles the switching of
     * the protected sides
     */
    @Override
    public void rotateClockwise(){
        super.rotateClockwise();
        protect[0] = protect[0].getNextClockwise();
        protect[1] = protect[1].getNextClockwise();
    }
}
