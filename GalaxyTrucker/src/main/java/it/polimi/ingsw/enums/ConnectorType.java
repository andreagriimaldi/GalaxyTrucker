package it.polimi.ingsw.enums;


public enum ConnectorType {
    EMPTY_SPACE,
    SMOOTH_SURFACE,
    SINGLE_CONNECTOR,
    DOUBLE_CONNECTOR,
    UNIVERSAL_CONNECTOR;

    /**
     * toString() simply returns a String indicating the type of connector
     */
    @Override
    public String toString() {
        switch(this){
            case SMOOTH_SURFACE -> {
                return "smooth surface";
            }
            case SINGLE_CONNECTOR -> {
                return "single connector";
            }
            case DOUBLE_CONNECTOR -> {
                return "double connector";
            }
            case UNIVERSAL_CONNECTOR -> {
                return "universal connector";
            }
            case EMPTY_SPACE -> {
                return "an empty space";
            }
        }
        return "error!";
    }

    /**
     * matches() returns a boolean indicating whether the two connectors can be put together. A single connector can be linked with
     * a single and a universal. A double can be linked with a double and a universal and a universal can be linked with all.
     * A smooth surface matches with empty space or another smooth surface. Empty space matches with anything
     * @return true if the two connectors are compatible, false otherwise
     */
    public boolean matches(ConnectorType other){
        if(this.equals(other))
            return true;
        else{
            if( (this.equals(SINGLE_CONNECTOR) && other.equals(DOUBLE_CONNECTOR)) || (this.equals(DOUBLE_CONNECTOR) && other.equals(SINGLE_CONNECTOR)) ){
                return false;
            }
            if ( (this.equals(SMOOTH_SURFACE) && other.equals(SINGLE_CONNECTOR)) || (this.equals(SINGLE_CONNECTOR) && other.equals(SMOOTH_SURFACE))){
                return false;
            }
            if ( (this.equals(SMOOTH_SURFACE) && other.equals(DOUBLE_CONNECTOR)) || (this.equals(DOUBLE_CONNECTOR) && other.equals(SMOOTH_SURFACE))){
                return false;
            }
            if ( (this.equals(SMOOTH_SURFACE) && other.equals(UNIVERSAL_CONNECTOR)) || (this.equals(UNIVERSAL_CONNECTOR) && other.equals(SMOOTH_SURFACE))){
                return false;
            }
            return true;
        }
    }

    /**
     * connects() is very similar to matches() but does not allow two smooth sides to connect
     * @return true if the two connectors are compatible, false otherwise
     */
    public boolean connects(ConnectorType other) {

        // superfici lisce non si collegano mai
        if (this == SMOOTH_SURFACE || other == SMOOTH_SURFACE)
            return false;

        // SINGLE non si collega a DOUBLE (né vice-versa)
        if ((this == SINGLE_CONNECTOR && other == DOUBLE_CONNECTOR) || (this == DOUBLE_CONNECTOR && other == SINGLE_CONNECTOR))
            return false;

        // UNIVERSAL si collega a qualsiasi cosa eccetto SMOOTH (già gestito)
        if (this == UNIVERSAL_CONNECTOR || other == UNIVERSAL_CONNECTOR)
            return true;

        // altrimenti devono essere uguali
        return this == other;
    }

}