package it.polimi.ingsw.network;

import it.polimi.ingsw.Constants;

import it.polimi.ingsw.global.GlobalManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/** handles connection of clients using socket **/
public class SocketServerConnection implements Runnable {

    private GlobalManager globalManager;
    private volatile boolean running = true;
    private ServerSocket serverSocket;



    public SocketServerConnection(GlobalManager globalManager) {
        this.globalManager = globalManager;
    }

    public GlobalManager getGlobalManager() {
        return globalManager;
    }


    public void run() {
        try {
            listenForConnectionRequestsLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void listenForConnectionRequestsLoop() throws IOException {
        this.serverSocket = new ServerSocket(Constants.SOCKET_PORT, Constants.BACKLOG);

        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Socket clientSocket = serverSocket.accept();  /** BLOCKING INSTRUCTION **/
                System.out.println("Socket client accepted");
                globalManager.initializeClientSession(clientSocket);
            }
            catch (SocketException e) {
                if (!running) {
                    System.out.println("Server socket shut down.");
                    break;
                }
                throw e; // real I/O problem
            }
            catch (IOException e) {
                // Connection crashed before session init: log and keep going
                e.printStackTrace();
            }
        }


    }
}