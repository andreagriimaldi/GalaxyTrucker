package it.polimi.ingsw.exceptions;

public class NoComponentsLeftException extends RuntimeException {
    public NoComponentsLeftException() {
        super("There are no components left on the table");
    }
}
