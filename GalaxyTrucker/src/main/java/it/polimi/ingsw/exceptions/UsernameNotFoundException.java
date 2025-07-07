package it.polimi.ingsw.exceptions;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String username) {
        super("No user registered with " + username + " as a username was found.");
    }
}