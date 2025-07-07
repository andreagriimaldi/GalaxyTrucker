package it.polimi.ingsw.enums;


public enum CrewType {
    HUMAN,
    ALIEN_PURPLE,
    ALIEN_BROWN;

    @Override
    public String toString() {
        switch(this){
            case HUMAN -> {
                return "human";
            }
            case ALIEN_PURPLE -> {
                return "purple alien";
            }
            case ALIEN_BROWN -> {
                return "brown alien";
            }
        }
        return "";
    }
}