package it.polimi.ingsw.commands.serverResponse;
import it.polimi.ingsw.commands.serverResponse.deltas.Delta;
import it.polimi.ingsw.enums.DeltaType;

import java.util.HashMap;
import java.util.Map;


import it.polimi.ingsw.commands.Command;


/**
 * contains the information to be sent from server to client
 * that is constituted by a log, and set of game state deltas
 *
 *
 * the flow to construct a server response object and send it is the following:
 *
 * 1. construct the server response object, either with or without a log.
 * a server response that doesn't contain a log serves the purpose of silently updating the game state
 * 2. then add the specific game deltas. more deltas can be added to a single server response
 * 3. then call notify(response) on the client's session to send it via the network
 *
 * alternatively, calling clientSession.log((String) msg) automatically
 * constructs and sends server response that only contains a log
 */
public class ServerResponse extends Command {

    private Map<DeltaType, Delta> deltas;
    private String log;

    public ServerResponse(String log) {
        this.deltas = new HashMap<DeltaType, Delta>();
        this.log = log;
    }

    public ServerResponse() {
        this.deltas = new HashMap<DeltaType, Delta>();
    }

    /**
     * adds a delta to the server response
     * @param delta delta to add
     */
    public void addDelta(Delta delta) {
        deltas.put(delta.getDeltaType(), delta);
    }

    /**
     * returns all the deltas contained in the response
     * @return a map containing the deltas
     */
    public Map<DeltaType, Delta> getDeltas() {
        return deltas;
    }

    /**
     * returns the message that the clients needs to be logged
     * @return the log
     */
    public String getLog() {
        return log;
    }
}