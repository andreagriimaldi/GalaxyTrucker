package it.polimi.ingsw.exceptions;

public class ActiveSessionAlreadyExistsException extends RuntimeException {
    public ActiveSessionAlreadyExistsException(String username) {
        super("User '" + username + "' already has an active session.");
    }

    public ActiveSessionAlreadyExistsException() {
        super("You already have an active session.");
    }
}