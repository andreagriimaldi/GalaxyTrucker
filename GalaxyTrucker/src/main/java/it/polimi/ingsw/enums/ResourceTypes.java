package it.polimi.ingsw.enums;

public enum ResourceTypes {
    REDCUBE,
    GREENCUBE,
    YELLOWCUBE,
    BLUECUBE,

    BATTERY;


    @Override
    public String toString() {
        switch(this) {
            case REDCUBE -> {
                return "a red cube";
            }
            case GREENCUBE -> {
                return "a green cube";
            }
            case YELLOWCUBE -> {
                return "a yellow cube";
            }
            case BLUECUBE -> {
                return "a blue cube";
            }
            case BATTERY -> {
                return "a battery";
            }
        }
        return "";
    }
}
