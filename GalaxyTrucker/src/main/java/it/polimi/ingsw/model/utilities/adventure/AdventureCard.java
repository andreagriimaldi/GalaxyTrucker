package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.controller.AdventureController;
import it.polimi.ingsw.enums.AdventureLevel;
import it.polimi.ingsw.enums.AdventureType;
import it.polimi.ingsw.model.playerset.Player;

import java.io.Serializable;
import java.util.*;

public abstract class AdventureCard implements Serializable {
    private AdventureLevel level;
    private String ID;
    protected int days;
    protected boolean effectEnded;
    protected boolean skippable;
    protected int currentPlayerIndex;
    protected transient Player activePlayer;
    protected transient List<Player> playersInOrder;
    protected transient AdventureController ac;
    private AdventureType type;


    protected boolean awaitingPlayerChoice;



    public AdventureCard(AdventureType type, AdventureLevel level, String ID, int days) {
        this.level = level;
        this.ID = ID;
        this.days = days;
        this.effectEnded = false;
        this.skippable = false;
        this.currentPlayerIndex = 0;
        this.playersInOrder = null;
        this.type = type;


        this.awaitingPlayerChoice = false;
    }

    /**
     * @return the ID specific to the card
     * each card has a unique ID to not have multiple copies of the same card in a game
     */
    public synchronized String getID() {
        return ID;
    }

    public synchronized AdventureLevel getLevel() {
        return level;
    }

    public synchronized AdventureType getType() {
        return type;
    }

    public synchronized int getDays() {
        return days;
    }

    public synchronized void attachAdventureController(AdventureController ac) {
        this.ac = ac;
    }

    public synchronized boolean isAwaitingPlayerChoice() {
        return this.awaitingPlayerChoice;
    }



    public synchronized void handle() {
        drawNextCard();
    }


    public synchronized void handle(Move move) {
    }

    public synchronized void drawNextCard() {
        ac.prepareForNextCard();
    }

}
