package it.polimi.ingsw.exceptions;

public class ComponentAlreadyPickedException extends RuntimeException {
    public ComponentAlreadyPickedException() {
        super("Too bad you're late! This component has already been picked up by another player in the meantime");
    }
}
