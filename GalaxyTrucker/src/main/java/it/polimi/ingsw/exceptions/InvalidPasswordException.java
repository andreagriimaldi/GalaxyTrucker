package it.polimi.ingsw.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Password is incorrect.");
    }
}