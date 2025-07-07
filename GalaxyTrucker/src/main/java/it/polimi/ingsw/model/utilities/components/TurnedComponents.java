package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.exceptions.ComponentAlreadyPickedException;

import java.util.*;


public class TurnedComponents {
    private List<Component> turned;


    public TurnedComponents()  {
        turned = new LinkedList<>();
    }


    public void addTurnedComponent(Component component) {
        turned.add(component);
    }

    public List<Component> getTurnedList(){
        return new ArrayList<>(turned);
    }

    public Component takeTurnedComponent(Component requestedComponent){
        Component component = turned.stream()
                .filter(c -> c.equals(requestedComponent))
                .findFirst()
                .orElseThrow(() -> new ComponentAlreadyPickedException());

        turned.remove(component);
        return component;
    }

    /**
     * takeTurnedComponentByID() returns and removes the component with the specified ID
     */
    public Component takeTurnedComponentByID(String ID){
        Component component = turned.stream().filter(c -> c.getID().equals(ID)).findFirst().orElseThrow(() -> new ComponentAlreadyPickedException());
        turned.remove(component);
        return component;
    }

}
