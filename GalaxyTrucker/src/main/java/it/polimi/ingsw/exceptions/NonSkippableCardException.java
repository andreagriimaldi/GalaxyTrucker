package it.polimi.ingsw.exceptions;

public class NonSkippableCardException extends RuntimeException {
    public NonSkippableCardException(String ID) {
        super("Card with ID:" + ID + " requested skip, but isn't skippable.");
    }
}
