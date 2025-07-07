package it.polimi.ingsw.exceptions;

public class HandAlreadyFullException extends RuntimeException {
    public HandAlreadyFullException() {
        super("You can't pick up a component because your hand is already full");
    }
}
