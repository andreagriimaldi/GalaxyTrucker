package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.enums.AdventureLevel;
import it.polimi.ingsw.enums.AdventureType;

public class EmptyCard extends AdventureCard {
    public EmptyCard(AdventureType type, AdventureLevel level, String ID, int days) {
        super(type, level, ID, days);
    }


    public String toString() {
        return "An empty card";
    }
}
