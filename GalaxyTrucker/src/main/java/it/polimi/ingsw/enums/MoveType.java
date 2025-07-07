package it.polimi.ingsw.enums;

public enum MoveType {

    /** ASSEMBLY PHASE **/
    FETCH_UNTURNED,
    FETCH_TURNED,
    PUT_IN_RESERVED,
    FETCH_FROM_RESERVED,
    PLACE_COMPONENT,
    ROTATE_COMPONENT,
    REJECT_COMPONENT,

    /** TO TRANSITION TO TAKEOFF PHASE **/
    FLIP_HOURGLASS,
    READY_FOR_TAKEOFF,

    /** TAKEOFF PHASE **/
    DETACH_COMPONENT, //for removing wrongly placed component
    POPULATE_SHIP,

    /** FLIGHT PHASE **/
    FLIGHT_CHOICE,
    FORFEIT,
    REQUEST_UPDATED_ROCKETSHIP,


    /** END OF FLIGHT PHASE **/
    QUIT_GAME;

}