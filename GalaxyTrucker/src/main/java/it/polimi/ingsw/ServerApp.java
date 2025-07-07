package it.polimi.ingsw;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.global.GlobalManager;
import it.polimi.ingsw.network.RMIRemoteServerConnection;
import it.polimi.ingsw.network.RMIServerConnection;
import it.polimi.ingsw.network.SocketServerConnection;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static it.polimi.ingsw.Constants.printIP;


/** class that:
 * 1. creates the server socket responsible for accepting new clients
 * 2. and "publishes" the registry
 */
public class ServerApp {
    public static void main(String[] args) {


        GlobalManager globalManager = new GlobalManager();
        globalManager.startProcessingRequests();

        printIP();

        // START SERVER SOCKET AS AN ENTRY POINT TO SOCKET CONNECTION
        SocketServerConnection socketServerAcceptor = new SocketServerConnection(globalManager);
        Thread acceptorThread = new Thread(socketServerAcceptor, "SocketAcceptor");
        acceptorThread.start();
        System.out.println("Socket acceptor thread started");


        // START RMI SOMETHING SOMETHING AS AN ENTRY POINT TO RMI CONNECTION
        try {
            Registry registry =
                    LocateRegistry.createRegistry(Constants.RMI_REGISTRY_PORT);

            // 1. real object that implements the Remote interface
            RMIServerConnection impl =
                    new RMIServerConnection(globalManager);

            // 3. bind ONLY the stub (simple name, not URL)
            registry.rebind(Constants.RMI_ENTRY_POINT_NAME, impl);

            System.out.println("RMI registry bound to name: " + Constants.RMI_ENTRY_POINT_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}