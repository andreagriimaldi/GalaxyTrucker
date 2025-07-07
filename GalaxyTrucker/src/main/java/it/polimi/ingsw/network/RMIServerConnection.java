package it.polimi.ingsw.network;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import it.polimi.ingsw.global.GlobalManager;
import it.polimi.ingsw.global.ClientSession;

import it.polimi.ingsw.network.RMIRemoteServerConnection;

import it.polimi.ingsw.network.client.RMIRemoteServer;
import it.polimi.ingsw.network.server.RMIRemoteClient;
import it.polimi.ingsw.network.server.RMIClientHandler;




public class RMIServerConnection extends UnicastRemoteObject implements RMIRemoteServerConnection {

    private GlobalManager globalManager;

    public RMIServerConnection(GlobalManager globalManager) throws RemoteException {
        this.globalManager = globalManager;
    }


    /** NOTE: RMI magically, behind the scenes, returns the implementation (RMIClientHandler) into RMIRemoteServer, which plays the role of the remote server **/
    @Override
    public RMIRemoteServer connect(RMIRemoteClient clientStub) throws RemoteException {
        ClientSession newClientSession = globalManager.initializeClientSession(clientStub);

        System.out.println("Callback stub implements: "
                + Arrays.toString(clientStub.getClass().getInterfaces()));

        RMIRemoteServer rmiServerStub = (RMIRemoteServer) newClientSession.exportRMIServerStub();
        System.out.println("RMI client accepted");
        return rmiServerStub;
    }
}