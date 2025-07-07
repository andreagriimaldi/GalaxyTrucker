package it.polimi.ingsw.enums;

public enum GamePhase {

    ASSEMBLY_PHASE,
    TAKEOFF_PHASE,
    FLIGHT_PHASE,
    END_FLIGHT_PHASE;

    @Override
    public String toString() {
        switch(this){
            case ASSEMBLY_PHASE -> {
                return "Assembly phase";
            }
            case TAKEOFF_PHASE -> {
                return "Takeoff phase";
            }
            case FLIGHT_PHASE -> {
                return "Flight phase";
            }
            case END_FLIGHT_PHASE -> {
                return "End flight phase";
            }
        }
        throw new RuntimeException();
    }
}
