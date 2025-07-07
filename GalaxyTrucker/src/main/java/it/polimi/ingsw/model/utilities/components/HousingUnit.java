package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;

import java.util.*;


public abstract class HousingUnit extends Component {
    protected List<CrewMember> residents;
    protected final int maxCapacity = 2;
    protected boolean isPlagued;

    public HousingUnit(ComponentType componentType, ConnectorType[] connectors, String id) {

        super(componentType, connectors, id);
        this.residents = new LinkedList<CrewMember>();
        this.isPlagued = false;
    }


    public abstract void addOneCrewMember(CrewMember crewMember);


    /**
     * removeOneCrewMember() simply removes a crew member if there's more than zero, otherwise throws an exception
     */
    public void removeOneCrewMember() {
        try {
            if (!residents.isEmpty()) {
                residents.removeLast();
            } else throw new Exception("no more crewmates to remove");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setPlagued() {
        isPlagued = true;
    }

    public void resetPlagued() {
        isPlagued = false;
    }

    public boolean isPlagued() {
        return isPlagued;
    }

    public List<CrewMember> getResidents() {
        return new ArrayList<>(residents);
    }

    /**
     * removeCrewMember() simply removes the specified crew member
     * @throws Exception if that crew member is not on the cabin
     */
    public void removeCrewMember(CrewMember c) throws Exception {
        if(!residents.isEmpty() && residents.contains(c)){
            residents.remove(c);
        }
        else throw new Exception("This crew member is not here");
    }

    /**
     * getNumberOfResidents() simply returns the number of residents of the cabin
     */
    public int getNumberOfResidents() {
        return residents.size();
    }

}