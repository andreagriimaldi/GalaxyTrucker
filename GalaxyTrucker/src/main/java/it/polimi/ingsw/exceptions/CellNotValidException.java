package it.polimi.ingsw.exceptions;

public class CellNotValidException extends RuntimeException {
    public CellNotValidException() {
        super("This cell does not exists");
    }
}
