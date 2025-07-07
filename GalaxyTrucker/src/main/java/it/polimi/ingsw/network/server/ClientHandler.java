package it.polimi.ingsw.network.server;
import it.polimi.ingsw.commands.serverResponse.ServerResponse;
import it.polimi.ingsw.commands.userRequest.UserRequest;
import it.polimi.ingsw.global.ClientSession;

public abstract class ClientHandler {


    /*protected GlobalManager globalManager;*/
    protected ClientSession clientSession;


    public ClientHandler(ClientSession clientSession) {
        this.clientSession = clientSession;
    }

    public abstract void notifyUser(ServerResponse response);


    public abstract void disconnect();

    public void onDisconnect() {

        //globalManager.onDisconnect(clientSessionID);

        clientSession.onDisconnect();
    }


    public abstract void addRequestToQueue(UserRequest receivedRequest); // ADDS REQUEST TO REQUEST QUEUE IN ClientSession




}