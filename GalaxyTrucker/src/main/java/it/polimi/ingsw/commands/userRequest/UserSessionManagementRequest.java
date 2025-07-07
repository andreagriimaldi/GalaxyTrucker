package it.polimi.ingsw.commands.userRequest;


import it.polimi.ingsw.enums.UserRequestType;

public class UserSessionManagementRequest extends UserRequest {
    private String username;
    private String passwordHash;

    // FOR REGISTRATION AND LOGIN
    public UserSessionManagementRequest(UserRequestType type, String username, String passwordHash) {
        super(type);

        this.username = username;
        this.passwordHash = passwordHash;
    }

    //FOR LOGOUT
    public UserSessionManagementRequest(UserRequestType type) {
        super(type);

        this.username = null;
        this.passwordHash = null;
    }


    public String getUsername() {
        return this.username;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }
}
