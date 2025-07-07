package it.polimi.ingsw.exceptions;

public class EmptyHandException extends RuntimeException {
    public EmptyHandException() {
        super("Your hand is now empty");
    }
}
