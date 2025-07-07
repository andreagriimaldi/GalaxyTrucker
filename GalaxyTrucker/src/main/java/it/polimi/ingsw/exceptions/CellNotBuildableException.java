package it.polimi.ingsw.exceptions;

public class CellNotBuildableException extends RuntimeException {
    public CellNotBuildableException() {
        super("This cell is currently not buildable");
    }
}
