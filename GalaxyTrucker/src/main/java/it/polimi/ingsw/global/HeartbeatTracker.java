package it.polimi.ingsw.global;

import it.polimi.ingsw.Constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.valueOf;

public class HeartbeatTracker {

    private GlobalManager globalManager;

    private int timeoutInMs;
    private int checkIntervalInMs;
    private Map<String, Long> clientsLastPingInMs;

    private ScheduledExecutorService heartbeatCheckRoutine;


    public HeartbeatTracker(GlobalManager globalManager) {
        this.globalManager = globalManager;

        this.timeoutInMs = Constants.heartbeatTimeoutInMs;
        this.checkIntervalInMs = Constants.heartbeatCheckIntervalInMs;
        this.clientsLastPingInMs = new ConcurrentHashMap<>();

        this.heartbeatCheckRoutine = Executors.newSingleThreadScheduledExecutor();
        heartbeatCheckRoutine.scheduleWithFixedDelay(this::checkLastPingInMs, checkIntervalInMs, checkIntervalInMs, TimeUnit.MILLISECONDS);
    }

    /**
     * updates the registry of clients and their ping with the timestamp of the last ping received by a specific client
     * @param clientSessionID is the client that sent the ping that is being processed
     */
    public void updateLastPingFromClient(String clientSessionID) {
        clientsLastPingInMs.put(clientSessionID, valueOf(System.currentTimeMillis()));
    }

    /**
     * checks the timestamp of all players. if it exceeds the preset threshold,
     * meaning the player hasn't been pinging the server for a while,
     * then the player is disconnected from the server
     */
    public void checkLastPingInMs() {
        long nowInMs = System.currentTimeMillis();
        for(String clientSessionID : clientsLastPingInMs.keySet()) {
            long lastPingInMs = clientsLastPingInMs.get(clientSessionID);
            if(nowInMs - lastPingInMs > timeoutInMs) {
                disconnectClient(clientSessionID);
            }
        }
    }

    /**
     * adds a client to the registry tracking the timestamps of the last ping received by each connected player
     * @param clientSessionID is the client to add
     */
    public void registerClient(String clientSessionID) {
        clientsLastPingInMs.put(clientSessionID, valueOf(System.currentTimeMillis()));
    }

    /**
     * unregisters the client from the registry trackin pings
     * @param clientSessionID
     */
    public void unregisterClient(String clientSessionID) { // will be called by global manager
        clientsLastPingInMs.remove(clientSessionID);
    }

    /**
     * starts a separate thread for executing the routine for disconnecting a player. called when the pings are
     * @param clientSessionID
     */
    public void disconnectClient(String clientSessionID) {
        new Thread(() -> {
            globalManager.getClientSession(clientSessionID).onDisconnect();
        }).start();
    }

}
