package it.polimi.ingsw.model.utilities.components;


import it.polimi.ingsw.enums.ComponentSide;
import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;

import java.io.Serializable;

public abstract class Component implements Serializable {
    protected ComponentType componentType;
    private ConnectorType[] connectors;
    private ComponentSide referenceSide;
    private String id;

    public Component(ComponentType componentType, ConnectorType[] connectors, String id) {
        this.componentType = componentType;
        this.connectors = connectors;
        this.referenceSide = ComponentSide.NORTH;
        this.id = id;
    }

    public String getID(){
        return this.id;
    }


    public void rotateClockwise() {
        ConnectorType[] rotatedConnectors = new ConnectorType[4];
        rotatedConnectors[0] = connectors[3];
        rotatedConnectors[1] = connectors[0];
        rotatedConnectors[2] = connectors[1];
        rotatedConnectors[3] = connectors[2];
        this.connectors = rotatedConnectors;

        referenceSide = referenceSide.getNextClockwise();
    }

    /**
     * listConnectors() returns an array (dimension 4) containing all the connectors of the component (0 for north,
     * and then in clockwise order)
     */
    public ConnectorType[] listConnectors(){
        return connectors;
    }

    /**
     * getConnectorByDirection() returns the connector facing the specified side
     * @param side represents the direction of which we want to get the connector
     * @return the connector facing in the indicated direction
     */
    public ConnectorType getConnectorByDirection(ComponentSide side){
        switch(side){
            case NORTH -> {
                return this.connectors[0];
            }
            case EAST -> {
                return this.connectors[1];
            }
            case SOUTH -> {
                return this.connectors[2];
            }
            case WEST -> {
                return this.connectors[3];
            }
        }
        return null;
    }

    public String toStringListConnectors() {
        String connectorsString = ". Connectors:";
        connectorsString += " N: "+connectors[0].toString();
        connectorsString += " E: "+connectors[1].toString();
        connectorsString += " S: "+connectors[2].toString();
        connectorsString += " W: "+connectors[3].toString();
        return connectorsString;
    }

    public ComponentType getComponentType() {
        return this.componentType;
    }

    /**
     * equals() is used to check if two components are actually the same
     * @param c is the second component
     * @return true if the two components share the same id, false otherwise
     */
    public boolean equals(Component c){
        if(this.getID().equals(c.getID()))
            return true;
        else return false;
    }

    /**
     * toString() returns information about component type and his connectors
     * @return a String containing all the information
     */
    public String toString(){
        return "Component type: " + this.getComponentType().toString() + toStringListConnectors();
    }

    public ComponentSide getReferenceSide() {
        return this.referenceSide;
    }
}