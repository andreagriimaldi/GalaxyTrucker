package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;
import it.polimi.ingsw.enums.CrewType;

import java.util.*;


public class SimpleCabinComponent extends HousingUnit {
    private List<CrewType> supportedAtmospheres;


    public SimpleCabinComponent(ComponentType componentType, ConnectorType[] connectors, String id) {
        super(componentType, connectors, id);
        supportedAtmospheres = new LinkedList<>();
    }

    @Override
    public void addOneCrewMember(CrewMember crewMember) {
        if (getNumberOfResidents() == 0) {
            if(crewMember.getCrewType().equals(CrewType.HUMAN)) {
                this.residents.add(crewMember);
            }
            else if(this.supportedAtmospheres.contains(crewMember.getCrewType())){
                this.residents.add(crewMember);
            }
        }
        else if (getNumberOfResidents() == 1) {
            if(residents.getFirst().getCrewType().equals(CrewType.HUMAN) && crewMember.getCrewType().equals(CrewType.HUMAN)) {
                this.residents.add(crewMember);
            }
        }
    }

    /**
     * updateSupportedAtmospheres() receives a list of supported atmospheres and proceeds to update the internal
     * list. Then it uses the removeUnsupportedAlien() to scan all the crew members and remove aliens which
     * atmosphere is no longer supported
     */
    public void updateSupportedAtmospheres(List<CrewType> supportedAtmospheres) {
        this.supportedAtmospheres = supportedAtmospheres;
        removeUnsupportedAlien();
    }

    public List<CrewType> getSupportedAtmospheres() {
        return new ArrayList<>(supportedAtmospheres);
    }

    /**
     * removeUnsupportedAlien() scans all the crew in order to remove alien which atmosphere is no longer supported
     */
    public void removeUnsupportedAlien(){
        for(CrewMember cm: residents){
            if(!cm.getCrewType().equals(CrewType.HUMAN) && !supportedAtmospheres.contains(cm.getCrewType())){
                try{
                    removeCrewMember(cm);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
