package it.polimi.ingsw.commands.userRequest;
import it.polimi.ingsw.commands.Command;
import it.polimi.ingsw.enums.UserRequestType;


public abstract class UserRequest extends Command {
    private UserRequestType type;
    private String clientSessionID; // nullo fino a quando raggiunge il server

    public UserRequest(UserRequestType type) {
        this.type = type;
        this.clientSessionID = null;
    }

    public UserRequestType getUserRequestType() {
        return this.type;
    }

    public void setClientSessionID(String clientSessionID) {
        this.clientSessionID = clientSessionID;
    }

    public String getClientSessionID() {
        return this.clientSessionID;
    }

}