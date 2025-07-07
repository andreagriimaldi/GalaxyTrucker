package it.polimi.ingsw.exceptions;

public class CellAlreadyTakenException extends RuntimeException {
    public CellAlreadyTakenException() {
        super("This cell already has a component");
    }
}
