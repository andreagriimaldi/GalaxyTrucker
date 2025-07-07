package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;
import it.polimi.ingsw.enums.ResourceTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockComponent extends StorageComponent {
    private final int maxCapacity;
    private final List<ResourceType> objects;
    private boolean isSpecial;

    public StockComponent(ComponentType componentType, ConnectorType[] connectors, String id, int capacity, boolean special) {
        super(componentType, connectors, id);
        this.maxCapacity = capacity;
        objects = new ArrayList<ResourceType>();
        isSpecial = special;
    }

    /**
     * isSpecial() returns true if the stock component is special, false otherwise
     */
    public boolean isSpecial() {
        return isSpecial;
    }

    /**
     * addResource() is used to add a new resource to the component, only if the current number of resources is below
     * the maximum, otherwise an exception is thrown. If the stock component is special then all kinds of cubes are allowed,
     * if not it will not be possible to add red cubes.
     *
     * @param resource is the resource wa want to add in the stock component
     * @throws Exception if the stock component is already full or if a red cube is added to a non-special stock component
     */
    public void addResource(ResourceType resource) throws Exception {
        if (objects.size() < maxCapacity && !resource.getType().equals(ResourceTypes.BATTERY)) {
            if (isSpecial) {
                objects.add(resource);
            } else {
                if (resource.getType().equals(ResourceTypes.REDCUBE)) {
                    throw new Exception("You can't put a red chest in this stock component");
                } else objects.add(resource);
            }
        } else throw new Exception("Stock component is already full");
    }

    /**
     * getTotalResource() returns the number of chests on the component
     */
    @Override
    public int getTotalResource() {
        return objects.size();
    }



    private Optional<ResourceType> anyTokenOfType(ResourceTypes type) {
        return objects.stream()
                .filter(r -> r.getType().equals(type))
                .findFirst();
    }

    public boolean isThereTypeObject(ResourceTypes type) {
        return anyTokenOfType(type).isPresent();
    }

    public void removeResource(ResourceTypes type) throws Exception {
        if (objects.isEmpty())
            throw new Exception("Stock component is empty");

        ResourceType token = anyTokenOfType(type)
                .orElseThrow(() -> new RuntimeException(
                        "There are no resources of the specified type"));

        objects.remove(token);
    }

    /**
     * returns true if all the slots in the Stock are occupied by a cube
     */
    public boolean isFull(){
        return objects.size() == maxCapacity;
    }

    public int getMaxCapacity(){
        return maxCapacity;
    }

    /**
     * getStock() returns a list containing all the elements in a stock component
     */
    public List<ResourceType> getStock(){
        return new ArrayList<>(objects);
    }

    /**
     * getValueOfStock() returns the values' sum of all the cubes on the stock component
     */
    public int getValueOfStock(){
        return getStock().stream().map(r -> ((CubeToken) r).getValue()).reduce(0, (a, b) -> a + b);
    }
}

