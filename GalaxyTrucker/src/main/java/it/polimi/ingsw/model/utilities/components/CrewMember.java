package it.polimi.ingsw.model.utilities.components;


import it.polimi.ingsw.enums.CrewType;

import java.io.Serializable;

public class CrewMember implements Serializable {
    private final CrewType crewType;

    public CrewMember(CrewType crewType) {
        this.crewType = crewType;
    }

    public CrewType getCrewType() {
        return this.crewType;
    }

}