package it.polimi.ingsw.exceptions;

public class LogoutWhileInGameException extends RuntimeException {
    public LogoutWhileInGameException() {
        super("Can't log out while in game.");
    }
}