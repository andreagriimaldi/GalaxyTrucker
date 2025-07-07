package it.polimi.ingsw.controller;

import it.polimi.ingsw.commands.serverResponse.deltas.HandDelta;
import it.polimi.ingsw.commands.serverResponse.deltas.ReservedDelta;
import it.polimi.ingsw.commands.serverResponse.deltas.RocketshipDelta;
import it.polimi.ingsw.commands.serverResponse.ServerResponse;
import it.polimi.ingsw.commands.serverResponse.deltas.TurnedDelta;
import it.polimi.ingsw.commands.userRequest.moves.*;
import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.CrewType;
import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.game.GameSession;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.playerset.RocketshipBoard;


import it.polimi.ingsw.model.utilities.components.*;


import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public class RocketshipBuildingController {
    private final TurnedComponents turned;
    private final UnturnedComponents unturned;
    private final RocketshipBoard board;


    private final GameController gc;
    private final TransitionController tc;
    private final GameSession gs;
    private final Player player;


    private final GameLobby lobby;

    private final LinkedBlockingQueue<Move> moveQueue;
    private Thread moveProcessorThread;

    private volatile AtomicBoolean populated = new AtomicBoolean();


    /**
     * this is the constructor of Rocketship's controller. Every player will have his controller.
     * It takes a player p as a parameter and then proceeds
     * to get his board. The controller is used to manipulate the board during the construction phase and also
     * during the flight. Since each player's got his controller there is no need for synchronization
     */
    public RocketshipBuildingController(Player p, GameController gc, GameLobby lobby) {

        player = p;
        board = p.getBoard();
        this.gc = gc;
        this.tc = gc.getTransitionController();
        this.gs = gc.getGameSession();
        turned = gs.getTurnedComponents();
        unturned = gs.getUnturnedComponents();

        this.lobby = lobby;

        this.moveQueue = new LinkedBlockingQueue<>();
        this.moveProcessorThread = null;

        populated.set(false);


    }


    public void startProcessingMoves() {
        this.moveProcessorThread = new Thread (() -> {
            try {
                System.out.println("Started move processing on a dedicated thread");
                while (!Thread.currentThread().isInterrupted()) {
                    Move move = moveQueue.take(); /** BLOCKING INSTRUCTION **/
                    System.out.println("move taken in assembly controller. now activating it");

                    activateMove(move);

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        this.moveProcessorThread.start();
    }

    public void terminateMoveProcessorThread() {
        if (moveProcessorThread != null && moveProcessorThread.isAlive()) {
            this.moveProcessorThread.interrupt();
            this.moveProcessorThread = null;
        }
    }


    /**
     * addMoveToQueue() is used to add a move coming from the client to a list containing all the moves that need
     * to be realized on the rocketship
     * @param m is the move added to the queue
     */
    public void addMoveToQueue(Move m) {
        moveQueue.add(m);
    }

    /**
     * activateMove() takes the first Move in the queue and proceeds to apply it to the Model. Then it proceeds to add
     * a ServerResponse to current player and to others (if required)
     */
    public void activateMove(Move m) {
        MoveType type = m.getType();

        if(gc.getGamePhase() == GamePhase.ASSEMBLY_PHASE && tc.getPlayersInAssemblyFinishingOrder().contains(m.getColor())) {

            switch (type) {
                case REJECT_COMPONENT -> {
                    rejectComponent();
                }
                case FETCH_FROM_RESERVED -> {
                    fetchComponentFromReserved(m);
                }
                case ROTATE_COMPONENT -> {
                    rotateComponent();
                }
            }
            // [] PERMETTERE AL LIMITE: "FETCH_RESERVED", E "REJECT"; LA HOURGLASS E' INVECE GIA' GESTITA DAL TRANSITION CONTROLLER

            lobby.logPlayer(m.getColor(), "you need to wait for the other players to finish their assembly");
        }
        else if(gc.getGamePhase() == GamePhase.TAKEOFF_PHASE) { //SE SONO IN FASE DI TAKEOFF
            Set<PlayerColor> playersReadyForFlight = tc.getPlayersReadyForFlight();

            if(m.getType().equals(MoveType.POPULATE_SHIP)){

            }

            if(!playersReadyForFlight.contains(m.getColor())) {
                switch (type) {
                    case DETACH_COMPONENT -> {
                        detachComponent(m);
                        checkRocketshipAtTakeOff();
                    }
                    case POPULATE_SHIP -> {
                        populateShip(m);
                        if (!populated.get()) {
                            lobby.logPlayer(player.getColor(), "Crew assignment not valid... please choose crew for every cabin.");
                        }
                    }
                    default -> {
                        lobby.logPlayer(player.getColor(), "Your rocketship is not compliant with the rules, the only possible move is to detach a component");
                    }
                }
            } else {
                lobby.logPlayer(player.getColor(), "Your rocketship is ready for the flight, you can't remove any component");
            }
        } else if(tc.checkIsTotalTimeOver()) { // if timeout is over
            switch (type) {
                case PLACE_COMPONENT -> {
                    placeComponent(m);
                }
                case REJECT_COMPONENT -> {
                    rejectComponent();
                } default -> {
                    lobby.logAllPlayers("since all hourglass time is over, you can only place the component you drew");
                }
            }
        } else { // durning normal assembly
            switch (type) {
                case FETCH_TURNED -> {
                    fetchTurnedComponent(m);
                }
                case FETCH_UNTURNED -> {
                    fetchUnturnedComponent();
                }
                case PLACE_COMPONENT -> {
                    placeComponent(m);
                }
                case PUT_IN_RESERVED -> {
                    putComponentInReserved();
                }
                case FETCH_FROM_RESERVED -> {
                    fetchComponentFromReserved(m);
                }
                case ROTATE_COMPONENT -> {
                    rotateComponent();
                }
                case REJECT_COMPONENT -> {
                    rejectComponent();
                }
                case READY_FOR_TAKEOFF -> { // [] shouldnt be required here any longer, as the transition is managed by the TransitionController
                    gs.playerIsReady(player);
                    lobby.logPlayer(player.getColor(), "Assembly phase is over for you! After a short check on the ships you will be ready to start your journey");
                }
            }
        }
    }

    private void fetchTurnedComponent(Move m) {
        try{
            drawTurnedComponents(((FetchTurnedComponentMove) m).getID());

            ServerResponse sr = new ServerResponse();
            sr.addDelta(new HandDelta(player.getColor(), board.getHandComponent()));
            sr.addDelta(new TurnedDelta(gs.showTurnedComponents()));

            lobby.notifyAllPlayers(sr);

            lobby.logPlayer(player.getColor(), "Added component to hand");

        } catch (RuntimeException e) {
            lobby.logPlayer(player.getColor(), e.getMessage());
        }
    }

    private void fetchUnturnedComponent() {
        try{
            drawUnturnedComponent();

            ServerResponse sr = new ServerResponse();
            sr.addDelta(new HandDelta(player.getColor(), board.getHandComponent()));
            lobby.notifyAllPlayers(sr);

            lobby.logPlayer(player.getColor(), "Component added to hand");

        } catch (RuntimeException e) {
            lobby.logPlayer(player.getColor(), e.getMessage());
        }
    }

    private void placeComponent(Move m) {
        int[] coord = ((PlaceComponentMove) m).getCoordinates();

        try{
            board.addComponent(coord[0], coord[1], board.getHandComponent());
            board.removeHand();

            ServerResponse sr = new ServerResponse();
            sr.addDelta(new RocketshipDelta(player.getColor(), board.getWholeShip()));
            sr.addDelta(new HandDelta(player.getColor(), board.getHandComponent()));
            lobby.notifyAllPlayers(sr);

            lobby.logPlayer(player.getColor(), "Component placed on the board");

        } catch (RuntimeException e) {
            lobby.logPlayer(player.getColor(), e.getMessage());
        }
    }

    private void putComponentInReserved() {
        try{
            if(!board.getHandComponent().getComponentType().equals(ComponentType.EMPTY_COMPONENT)){
                board.putHandInReserved();

                ServerResponse sr = new ServerResponse();
                sr.addDelta(new HandDelta(player.getColor(), board.getHandComponent()));
                sr.addDelta(new ReservedDelta(player.getColor(), board.returnReservedComponents()));

                lobby.notifyAllPlayers(sr);

                lobby.logPlayer(player.getColor(), "Component added to the reserved");
            }
            else{
                lobby.logPlayer(player.getColor(), "Your hand is now empty");
            }

        } catch (RuntimeException e) {
            lobby.logPlayer(player.getColor(), e.getMessage());
        }
    }


    private void saveBrokenShip(Move m) {
        try{
            board.chooseBrokenShip(((SaveBrokenShipMove) m).getChoice());

            ServerResponse sr = new ServerResponse();
            sr.addDelta(new RocketshipDelta(player.getColor(), board.getWholeShip()));
            lobby.notifyAllPlayers(sr);

            lobby.logPlayer(player.getColor(), "Ship correctly fixed");

        } catch (RuntimeException e) {
            lobby.logPlayer(player.getColor(), e.getMessage());
        }
    }

    private void fetchComponentFromReserved(Move m) {
        try{
            int choice = ((FetchFromReservedMove) m).getChoice();
            if(!board.getReservedComponentType(choice).equals(ComponentType.EMPTY_COMPONENT)){
                board.getReservedComponent(((FetchFromReservedMove) m).getChoice());

                ServerResponse sr = new ServerResponse();
                sr.addDelta(new HandDelta(player.getColor(), board.getHandComponent()));
                sr.addDelta(new ReservedDelta(player.getColor(), board.returnReservedComponents()));
                lobby.notifyAllPlayers(sr);

                lobby.logPlayer(player.getColor(), "Component correctly drawn from reserve");
            }
            else{
                lobby.logPlayer(player.getColor(), "This place is empty, you can't draw a component from there");
            }

        } catch (RuntimeException e) {
            lobby.logPlayer(player.getColor(), e.getMessage());
        }
    }

    private void rotateComponent() {
        try{
            board.rotateHand();

            ServerResponse sr = new ServerResponse();
            sr.addDelta(new HandDelta(player.getColor(), board.getHandComponent()));
            lobby.notifyAllPlayers(sr);

            lobby.logPlayer(player.getColor(), "Component correctly rotated");

        } catch (EmptyHandException e) {
            lobby.logPlayer(player.getColor(), e.getMessage());
        }
    }

    private void rejectComponent() {
        try{
            gs.addDiscardedComponent(board.getHandComponent());
            board.removeHand();

            ServerResponse sr = new ServerResponse();
            sr.addDelta(new HandDelta(player.getColor(), board.getHandComponent()));
            sr.addDelta(new TurnedDelta(gs.showTurnedComponents()));
            lobby.notifyAllPlayers(sr);

            lobby.logPlayer(player.getColor(), "Component correctly put in the turned pile");

        } catch (RuntimeException e) {
            lobby.logPlayer(player.getColor(), e.getMessage());
        }
    }

    private void detachComponent(Move m) {
        int[] coord = ((DetachComponentMove) m).getCoordinates();

        try{
            board.removeComponent(coord[0], coord[1]);

            ServerResponse sr = new ServerResponse();
            sr.addDelta(new RocketshipDelta(player.getColor(), board.getWholeShip()));
            lobby.notifyAllPlayers(sr);

            lobby.logPlayer(player.getColor(), "Component removed from the board");
        } catch (RuntimeException e) {
            lobby.logPlayer(player.getColor(), e.getMessage());
        }
    }

    private void populateShip(Move m){
        PopulateShipMove psm = (PopulateShipMove) m;
        String choice = psm.getChoice();
        String[] choices = choice.split("\\s+");

        List<Map<Component, Integer>> comps = board.getPossibleAlienOptions();
        for(int i = 0; i < comps.size(); i++){
            Component c = comps.get(i).keySet().iterator().next();
            int possibility = comps.get(i).get(c);

            String s = choices[i];

            boolean allowed =
                    (possibility == 0 && s.equals("HUMAN")) ||
                            (possibility == 1 && (s.equals("HUMAN") || s.equals("PURPLE"))) ||
                            (possibility == 2 && (s.equals("HUMAN") || s.equals("BROWN"))) ||
                            (possibility == 3);

            int row = board.getCoordinates(c)[0];
            int col = board.getCoordinates(c)[1];

            if(!allowed) {
                lobby.logPlayer(player.getColor(), "Cabin " + row + " " + col + " doesn't accept " + s + ".");
                return;
            }

            int outcome = -1;
            switch (s) {
                case "HUMAN" -> {
                    outcome = board.putAlienCrew(row, col, CrewType.HUMAN);
                }
                case "PURPLE" -> {
                    outcome = board.putAlienCrew(row, col, CrewType.ALIEN_PURPLE);
                }
                case "BROWN" -> {
                    outcome = board.putAlienCrew(row, col, CrewType.ALIEN_BROWN);
                }
            }
            if(outcome == 0){
                lobby.logPlayer(this.player.getColor(), "Put two humans at " + (row + 5) + " " + (col + 4));
            }
            else if(outcome == 1){
                lobby.logPlayer(this.player.getColor(), "Put a purple alien at " + (row + 5) + " " + (col + 4));
            }
            else if(outcome == 2){
                lobby.logPlayer(this.player.getColor(), "Put a brown alien at " + (row + 5) + " " + (col + 4));
            }
        }
        lobby.logPlayer(this.player.getColor(),"The ship has now its crew, it's time to fly");

        populated.set(true);
        tc.setAsReadyForFlight(player.getColor());
    }



    public void crewOnboarding(){
        if(!board.getComponentByType(ComponentType.SIMPLE_CABIN).isEmpty()){
            lobby.logPlayer(this.player.getColor(), "Now it's time for you to decide who gets to participate in this cosmic trip!");
            board.resetSimpleCabinSupportedAtmospheres();
            List<Map<Component, Integer>> comps = board.getPossibleAlienOptions();
            for(int i = 0; i < comps.size(); i++){
                Component c = comps.get(i).keySet().iterator().next();
                int possibility = comps.get(i).get(c);

                int row = board.getCoordinates(c)[0] + 5;
                int col = board.getCoordinates(c)[1] + 4;
                switch(possibility){
                    case 0 -> {
                        lobby.logPlayer(this.player.getColor(), "For the cabin in position " + row + " " + col + " you can only put two humans (command: POPULATE HUMAN)");
                    }
                    case 1 -> {
                        lobby.logPlayer(this.player.getColor(), "For the cabin in position " + row + " " + col + " you can also put a purple alien (command: POPULATE HUMAN/PURPLE)");
                    }
                    case 2 -> {
                        lobby.logPlayer(this.player.getColor(), "For the cabin in position " + row + " " + col + " you can also put a brown alien (command: POPULATE HUMAN/BROWN)");
                    }
                    case 3 -> {
                        lobby.logPlayer(this.player.getColor(), "For the cabin in position " + row + " " + col + " you can also put a purple or brown alien (command: POPULATE HUMAN/PURPLE/BROWN)");
                    }
                }
            }
        }

    }


    public void checkRocketshipAtTakeOff() { /* AGGIUNGE DIRETTAMENTE A TRANSITION CONTROLLER IL PLAYER COLOR DELLE NAVI CHE SONO GIA PRONTE */
        boolean isReadyForFlight = board.checkShipAtStart();

        if(board.getComponentByType(ComponentType.SIMPLE_CABIN).isEmpty()){
            populated.set(true);
        }

        if (isReadyForFlight && populated.get()) {
            tc.setAsReadyForFlight(player.getColor());
            return;
        }

        if(isReadyForFlight) {
            lobby.logPlayer(this.player.getColor(), "Your rocketship is ready for takeoff");
            crewOnboarding();
        } else{
            lobby.logPlayer(this.player.getColor(), "Your rocketship is not compliant with the rules, you must remove some components");
        }
    }



    /**
     * getPlayer() simply returns the player that "owns" this controller
     */
    public Player getPlayer(){
        return player;
    }

    /**
     * returnStartBoard() is useful to get the board at the start of the game
     */
    public Component[][] returnStartBoard(){
        return board.getWholeShip();
    }

    /**
     * drawUnturnedComponent() allows the user to draw a component among the unturned ones and puts it in hand
     */
    public void drawUnturnedComponent(){
        synchronized (unturned){
            board.addHand(unturned.draw());
        }
    }

    /**
     * drawTurnedComponents() allows the user to draw a component among the turned ones and puts it in hand
     */
    public void drawTurnedComponents(String ID){
        synchronized (turned){
            Component c = turned.takeTurnedComponentByID(ID);
            board.addHand(c);
        }
    }

    public void penalizeForReserved() {

        System.out.println("penalizing " + player.getColor() + " player");

        Component[] reserved = player.getBoard().returnReservedComponents();

        int numberOfReserved = 0;
        for(Component component : reserved) {
            if(component.getComponentType() != ComponentType.EMPTY_COMPONENT) {
                numberOfReserved++;
            }
        }
        System.out.println(player.getColor() + " has " + numberOfReserved + " component(s) in reserved, and will be penalized for it accordingly");
        player.penalizeForLostComponents(numberOfReserved);
    }


}
