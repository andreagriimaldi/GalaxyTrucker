package it.polimi.ingsw.enums;

public enum CreditsType {
    FLIGHT_CREDITS,
    FINISHING_ORDER_BONUS,
    CONNECTORS_PRIZE,
    LOST_COMPONENTS_PENALTY,
    PROFIT_FROM_RESOURCES,
    TOTAL;

    @Override
    public String toString() {
        switch(this) {
            case FLIGHT_CREDITS -> {
                return "FLIGHT CREDITS";
            }
            case FINISHING_ORDER_BONUS -> {
                return "FINISHING ORDER BONUS";
            }
            case CONNECTORS_PRIZE -> {
                return "CONNECTORS PRIZE";
            }
            case LOST_COMPONENTS_PENALTY -> {
                return "LOST COMPONENTS PENALTY";
            }
            case PROFIT_FROM_RESOURCES -> {
                return "PROFIT FROM RESOURCES";
            }
            case TOTAL -> {
                return "TOTAL";
            }
            default -> {
                return "UNKNOWN";
            }
        }
    }

}
