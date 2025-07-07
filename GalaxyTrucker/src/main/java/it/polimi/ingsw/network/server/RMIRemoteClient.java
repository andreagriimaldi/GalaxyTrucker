package it.polimi.ingsw.network.server;
import java.rmi.RemoteException;
import it.polimi.ingsw.commands.serverResponse.ServerResponse;

import java.rmi.Remote;


public interface RMIRemoteClient extends Remote {
    public void notifyUser(ServerResponse response) throws RemoteException;
}