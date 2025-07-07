package it.polimi.ingsw.exceptions;

public class ClientNotYetAuthenticatedException extends RuntimeException {
    public ClientNotYetAuthenticatedException() {
        super("You haven't logged in yet.");
    }
}