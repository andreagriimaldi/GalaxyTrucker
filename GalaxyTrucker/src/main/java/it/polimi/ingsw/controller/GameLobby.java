package it.polimi.ingsw.controller;

import it.polimi.ingsw.commands.serverResponse.ServerResponse;
import it.polimi.ingsw.exceptions.InvalidNumberOfPlayers;
import it.polimi.ingsw.exceptions.RequestedClientNotFoundException;
import it.polimi.ingsw.global.ClientSession;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.exceptions.ClientNotYetAuthenticatedException;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameLobby {

    public boolean testing = true;

    private int finalNumberOfPlayers;
    private int maxPlayerSet = 0;

    List<ClientSession> clientsInWaitingRoom;

    // da identificativo a classe contente dati e metodi per svolgere operazioni
    private Map<String, Player> usernameToPlayer;
    private Map<PlayerColor, ClientSession> colorToClient;

    private final Map<PlayerColor, String> logBufferForDisconnectedColors;




    private final Object logBufferLock = new Object();



    public GameLobby(int finalNumberOfPlayers){
        this.finalNumberOfPlayers = finalNumberOfPlayers;

        this.clientsInWaitingRoom = new ArrayList<>(); /** [ ] come aveva inizialmente fatto andrea, all'inizio effettivo della partita, i client si possono rimuovere da qua. per il resto della partita, si usa solo colorToClient **/
        this.usernameToPlayer = new ConcurrentHashMap<>();
        this.colorToClient = new ConcurrentHashMap<>();

        this.logBufferForDisconnectedColors = new Hashtable<>();

        this.testing = false;
    }

    @TestOnly
    public GameLobby(int finalNumberOfPlayers, boolean testing) {
        this.finalNumberOfPlayers = finalNumberOfPlayers;

        this.clientsInWaitingRoom = new ArrayList<>(); /** [ ] come aveva inizialmente fatto andrea, all'inizio effettivo della partita, i client si possono rimuovere da qua. per il resto della partita, si usa solo colorToClient **/
        this.usernameToPlayer = new ConcurrentHashMap<>();
        this.colorToClient = new ConcurrentHashMap<>();

        this.logBufferForDisconnectedColors = new Hashtable<>();

        this.testing = testing;
    }

    /**
     * getFinalNumberOfPlayers() simply returns the maximum number of players in the game
     */
    public int getFinalNumberOfPlayers() {
        return finalNumberOfPlayers;
    }


    /**
     * setFinalNumberOfPlayers() allows the host to change the maximum number of players allowed in the game. Throws an
     * exception if the specified number is not in the range [2;4]. It can be called only one time
    public void setFinalNumberOfPlayers(int choice){
        if(maxPlayerSet == 0){
            if(choice > 1 && choice < 5){
                finalNumberOfPlayers = choice;
            }
            else throw new InvalidNumberOfPlayers();
        }
        else throw new RuntimeException("The number has already been set");
    }
     */

    public Player getPlayerFromUsername(String username) {
        return usernameToPlayer.get(username);
    }

    public Player getPlayerFromColor(PlayerColor playerColor) {
        for(Player player : usernameToPlayer.values()) {
            if(player.getColor().equals(playerColor)) {
                return player;
            }
        }
        return null;
    }

    public List<Player> getOrderedPlayersByColor(List<PlayerColor> orderedColors) {
        return orderedColors.stream()
                .flatMap(color -> usernameToPlayer.values().stream()
                        .filter(player -> player.getColor() == color))
                .collect(Collectors.toList());
    }


    public ClientSession getClientFromUsername(String username) throws RequestedClientNotFoundException { // POTREBBE NON ESSERCI => devo lanciare un eccezione
        ClientSession client = colorToClient.get(getPlayerFromUsername(username).getColor()); // only instruction requiring synchronization. dealt with by the use of ConcurrentHashMap
        if(client != null) {
            return client;
        }
        else throw new RequestedClientNotFoundException();
    }



    public ClientSession getClientFromWaitingRoom(String username) {
        for (ClientSession clientSession : clientsInWaitingRoom) {
            if (clientSession.getUsername().equals(username)) {
                return clientSession;
            }
        }
        return null;
    }

    public ClientSession getClientFromColor(PlayerColor color) { // POTREBBE NON ESSERCI
        return colorToClient.get(color);
    }

    public PlayerColor getColorFromUsername(String username) {
        Player player = getPlayerFromUsername(username);
        PlayerColor color = player.getColor();
        return color;
    }

    public Set<PlayerColor> getAllPlayerColors() {
        Set<PlayerColor> allPlayerColors = new HashSet<>();
        for(Player player : this.usernameToPlayer.values()) {
            allPlayerColors.add(player.getColor());
        }
        return allPlayerColors;
    }


    public synchronized List<ClientSession> getAllClientsInWaitingRoom() {
        return clientsInWaitingRoom;
    }

    /** IL METODO SARA' CHIAMATO DA UN METODO OMONIMO NEL CONTROLLER, A SUA VOLTA CHIAMATO DAI METODI CreateGame() (per il primo player) E JoinGame() PER I GIOCATORI SUCCESSIVI AL PRIMO
     *  IN SEGUITO IL CONTROLLER CHIAMERA' IN AUTONOMIA IL PROPRIO METODO StartGame()
     */

    /**
     * addUserToWaitingRoom() is a synchronized method that allows the server to add a user to the waiting room
     * @param client is the ClientSession that represents the client that wants to play the game
     */
    public synchronized void addClientToWaitingRoom(ClientSession client) {
        clientsInWaitingRoom.add(client);
    }

    public synchronized void removeClientFromWaitingRoom(String username) {


        clientsInWaitingRoom.removeIf(client -> {
            try {
                return client.getUsername().equals(username);
            } catch (ClientNotYetAuthenticatedException e) {
                return false;
            }
        });

    }

    public synchronized void removeWaitingRoom() {
        clientsInWaitingRoom.clear();
    }











    public void addColorToClient(PlayerColor playerColor, ClientSession clientSession) {
        this.colorToClient.put(playerColor, clientSession);
    }

    public void removeClient(PlayerColor color) {
        colorToClient.put(color, null);
    }




    public void addUsernameToPlayerMap(Map<String, Player> usernameToPlayer) {
        this.usernameToPlayer = usernameToPlayer;
    }

    public void addColorToClientMap(Map<PlayerColor, ClientSession> colorToClient) {
        this.colorToClient = colorToClient;
    }




    public boolean isUsernameInGame(String username) {
        return usernameToPlayer.containsKey(username);
    }



    public boolean isColorConnected(PlayerColor colorToCheck) {
        if(testing) {

            /*if(colorToCheck == PlayerColor.YELLOW) {
                return false;
              else
             */

            return true;
        } else {
            ClientSession client = getClientFromColor(colorToCheck);

            if(client == null) {
                return false;
            } else {
                return true;
            }
        }
    }




    public synchronized List<String> getAllUsernames() {
        return new LinkedList<>(usernameToPlayer.keySet());
    }

    public synchronized List<Player> getAllPlayers() {
        return new LinkedList<>(usernameToPlayer.values());
    }

    /**
     * getUsernames() returns a list containing all the usernames of lobby's users
     */
    public synchronized List<String> getAllConnectedUsernames(){
        List<ClientSession> allConnectedClients = new LinkedList<>(colorToClient.values());

        return new LinkedList<>(allConnectedClients.stream().map(u -> {
            try {
                return u.getUsername();
            } catch (Exception e) {
                System.out.println("You're not supposed to see this");
                return null;
            }
        }).toList());
    }




    public List<ClientSession> getAllConnectedClients() {
        return colorToClient.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }




    public void removeClientFromGame(String username) {

        PlayerColor color = getColorFromUsername(username);
        removeClient(color);
        createLogBufferForDisconnectedColor(color); /** CREATES A LOG BUFFER FOR THE DISCONNECTED PLAYER **/
    }




    public void createLogBufferForDisconnectedColor(PlayerColor disconnectedColor) {
        synchronized (logBufferLock) {
            logBufferForDisconnectedColors.put(disconnectedColor, "UPDATES YOU MISSED OUT ON:");
        }
    }

    public void appendToLogBufferForDisconnectedColor(PlayerColor color, String newLog) { /** called by the controllers **/
        synchronized (logBufferLock) {
            String oldLogBuffer = logBufferForDisconnectedColors.get(color);
            if (oldLogBuffer != null) { /** might be null in case the player rejoins in the meanwhile and its cached log gets removed **/
                String newLogBuffer = oldLogBuffer + newLog + System.lineSeparator();
                logBufferForDisconnectedColors.put(color, newLogBuffer);
            } else {
                System.err.println("null pointer exception for oldLogBuffer object");
            }
        }
    }
    public void sendLogBufferToReconnectedColor(PlayerColor reconnectedColor) {
        String logBuffer = logBufferForDisconnectedColors.remove(reconnectedColor);
        logPlayer(reconnectedColor, logBuffer);
    }






    // SE SONO CONNESSI, CHIAMA NOTIFY; SE NON LO SONO, SALVA SOLO I LOG
    public void notifyPlayer(PlayerColor playerColor, ServerResponse response) {
        ClientSession client = colorToClient.get(playerColor);
        if(client != null ) { /** null when client is disconnected **/
            client.notify(response); // if the client disconnects in the meantime, that's not a problem that is to be dealt by the GameLobby. rather, it will create IO exception in the transport part of game session, which is properly dealt with there
        }
        else {
            if(testing) {
                System.out.println("to " + playerColor + " player: " + response.getLog());
            } else {
                System.out.println("saving the response that the "+ playerColor +" disconnected client was supposed to see: " + response.getLog());
                appendToLogBufferForDisconnectedColor(playerColor, response.getLog());
            }
        }

    }

    public void notifyAllPlayers(ServerResponse response) { // [ ] forse a livello di implementazione dovrebbe essere un "notify all CONNECTED players()"
        for (PlayerColor colorToNotify : colorToClient.keySet()) { // [ ] si può implementare usando java functional
            notifyPlayer(colorToNotify, response);
        }
    } /** method was changed in order to get iterate on colors instead of clients directly, because if more than one client were disconnected, there wouldn't have been a way to disambiguate what color to send the log buffer to upon reconnection **/


    public void logPlayer(PlayerColor playerColor, String log) {
        ServerResponse response = new ServerResponse(log);
        notifyPlayer(playerColor, response);
    }

    public void logAllPlayers(String log) {
        if(testing) {
            System.out.println("to all players: " + log);
        } else {
            ServerResponse response = new ServerResponse(log);
            for (PlayerColor colorToNotify : colorToClient.keySet()) { // [ ] si può implementare usando java functional
                notifyPlayer(colorToNotify, response);
            }
        }
    }

    public void logAllPlayersExceptOne(PlayerColor exemptedPlayerColor, String log) {
        if(testing) {
            System.out.println("to all players except "+ exemptedPlayerColor+": " + log);
        } else {
            ServerResponse response = new ServerResponse(log);
            for (PlayerColor colorToNotify : colorToClient.keySet()) { // [ ] si può implementare usando java functional
                if(colorToNotify != exemptedPlayerColor) {
                    notifyPlayer(colorToNotify, response);
                }
            }
        }
    }


    public void logAllPlayersInWaitingRoom(String log) {

        List<ClientSession> clients = getAllClientsInWaitingRoom();
        ServerResponse response = new ServerResponse(log);

        for(ClientSession client : clients) {
            client.notify(response);
        }
    }

    public void logPlayerFromUsername(String username, ServerResponse response) {

    }



}
