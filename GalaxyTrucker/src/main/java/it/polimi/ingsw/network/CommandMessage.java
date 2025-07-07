package it.polimi.ingsw.network;

import it.polimi.ingsw.commands.Command;

import java.io.Serializable;

public class CommandMessage implements Serializable {
    private final Command command;

    public CommandMessage(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return this.command;
    }
}