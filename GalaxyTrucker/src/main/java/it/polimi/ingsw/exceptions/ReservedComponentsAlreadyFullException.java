package it.polimi.ingsw.exceptions;

public class ReservedComponentsAlreadyFullException extends RuntimeException {
    public ReservedComponentsAlreadyFullException() {
        super("You can't reserve more than two components");
    }
}
