package it.polimi.ingsw.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("A user with " + username + " as a username is already registered.");
    }
}