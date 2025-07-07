package it.polimi.ingsw.exceptions;

public class RequestedClientNotFoundException extends RuntimeException {
    public RequestedClientNotFoundException() {
        super("Requested client not found");
    }
}