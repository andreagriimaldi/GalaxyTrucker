package it.polimi.ingsw.exceptions;

public class NoBatteriesLeftToRemoveException extends RuntimeException {
    public NoBatteriesLeftToRemoveException() {
        super("There are no batteries left to remove.");
    }
}
