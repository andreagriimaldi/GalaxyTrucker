package it.polimi.ingsw.exceptions;

public class InvalidNumberOfPlayers extends RuntimeException {
    public InvalidNumberOfPlayers() {
        super("Maximum number of players in the lobby must be between 2 and 4");
    }
}
