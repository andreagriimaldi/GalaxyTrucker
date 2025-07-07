package it.polimi.ingsw.model.utilities.components ;

import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;
import it.polimi.ingsw.enums.ResourceTypes;

import java.util.ArrayList ;
import java.util.List ;

public class PowerCenterComponent extends StorageComponent{
    private final int maxCapacity;
    private final List<ResourceType> objects;

    public PowerCenterComponent(ComponentType componentType, ConnectorType[] connectors, String id, int capacity){
        super(componentType, connectors, id);
        this.maxCapacity = capacity;
        objects = new ArrayList<ResourceType>();
    }

    /**
     * getMaxCapacity() returns an int representing how many batteries this component can hold
     */
    public int getMaxCapacity(){
        return maxCapacity;
    }

    /**
     * addResource() is used to add a new resource to the component, only if the current number of resources is below
     * the maximum, otherwise an exception is thrown
     * @param resource is the resource wa want to add in the stock component
     * @throws Exception if the stock component is already full
     */
    public void addResource(ResourceType resource){
        if(objects.size() < maxCapacity && resource.getType().equals(ResourceTypes.BATTERY)){
            objects.add(resource);
        }
        else throw new RuntimeException("Power center component is already full");
    }

    /**
     * removeResource() simply removes a resource when called. If the component is empty throws an exception.
     */
    public void removeResource() throws Exception{
        if(!objects.isEmpty()) {
            objects.removeLast();
        }
        else throw new Exception("Power center component is empty");
    }

    /**
     * getTotalResource() simply returns the total number of batteries
     */
    public int getTotalResource(){
        return objects.size();
    }
}

