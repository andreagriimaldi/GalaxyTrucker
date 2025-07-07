package it.polimi.ingsw.enums;


public enum ComponentType {
    EMPTY_COMPONENT,

    STARTING_CABIN,
    SIMPLE_CABIN,

    SINGLE_CANNON,
    DOUBLE_CANNON,

    SINGLE_THRUSTER,
    DOUBLE_THRUSTER,

    PIPES,
    ALIEN_ADD_ON,

    SHIELD,

    STOCK,
    POWER_CENTER;

    /**
     * toString() simply returns a string indicating the type of the component
     */
    @Override
    public String toString(){
        switch(this){
            case EMPTY_COMPONENT -> {
                return "empty component";
            }
            case STARTING_CABIN -> {
                return "starting cabin";
            }
            case SIMPLE_CABIN -> {
                return "simple cabin";
            }
            case SINGLE_CANNON -> {
                return "single cannon";
            }
            case DOUBLE_CANNON -> {
                return "double cannon";
            }
            case SINGLE_THRUSTER -> {
                return "single thruster";
            }
            case DOUBLE_THRUSTER -> {
                return "double thruster";
            }
            case PIPES -> {
                return "pipes";
            }
            case ALIEN_ADD_ON -> {
                return "alien add on";
            }
            case SHIELD -> {
                return "shield";
            }
            case STOCK -> {
                return "stock";
            }
            case POWER_CENTER -> {
                return "power center";
            }
        }
        return "";
    }

}