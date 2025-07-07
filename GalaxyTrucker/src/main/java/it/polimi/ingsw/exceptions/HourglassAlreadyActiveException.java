package it.polimi.ingsw.exceptions;

public class HourglassAlreadyActiveException extends RuntimeException {
    public HourglassAlreadyActiveException() {
        super("Hourglass is already active");
    }
}