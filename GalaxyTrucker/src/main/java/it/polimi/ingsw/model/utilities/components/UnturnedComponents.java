package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.exceptions.NoComponentsLeftException;

import java.util.*;


public class UnturnedComponents {
    private List<Component> unturned;

    public UnturnedComponents()  {
        ComponentFactory factory = new ComponentFactory();
        unturned = factory.buildUnturnedComponents();
    }


    public int countUnturnedComponents() {
        return unturned.size();
    }

    public Component draw(){
        if (countUnturnedComponents() != 0) {
            Random random = new Random();
            return unturned.remove(random.nextInt(countUnturnedComponents())); // nextInt already returns integer in range [0, countUnturnedComponents()-1]
        }
        else throw new NoComponentsLeftException();
    }

}
