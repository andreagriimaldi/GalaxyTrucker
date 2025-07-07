package it.polimi.ingsw.network;

import java.rmi.RemoteException;
import java.rmi.Remote;

import it.polimi.ingsw.network.RMIRemoteServerConnection;
import it.polimi.ingsw.network.client.RMIRemoteServer;
import it.polimi.ingsw.network.server.RMIRemoteClient;

public interface RMIRemoteServerConnection extends Remote {
    public RMIRemoteServer connect(RMIRemoteClient clientStub) throws RemoteException;
}