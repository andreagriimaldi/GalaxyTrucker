package it.polimi.ingsw.enums;


public enum ComponentSide {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    /**
     * getNumComponentSide returns an int indicating the side (0 for north and increasing clockwise)
     */
    public int getNumComponentSide(){
        switch(this){
            case NORTH: return 0;
            case EAST: return 1;
            case SOUTH: return 2;
            case WEST: return 3;
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.valueOf(getNumComponentSide());
    }

    /**
     * getNextClockwise() returns the next side in a clockwise order
     */
    public ComponentSide getNextClockwise(){
        switch(this){
            case NORTH -> {
                return EAST;
            }
            case EAST -> {
                return SOUTH;
            }
            case SOUTH -> {
                return WEST;
            }
            case WEST -> {
                return NORTH;
            }
        }
        return null;
    }
}