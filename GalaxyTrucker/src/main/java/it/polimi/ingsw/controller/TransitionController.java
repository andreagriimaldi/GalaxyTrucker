package it.polimi.ingsw.controller;
import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.exceptions.HourglassAlreadyActiveException;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.playerset.PlayerColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;


// IN CASE TAKEOFF AND FLIGHT DON'T NEED SPECIAL LOGIC FOR CHECKING WHETHER THE
// TRANSITION SHOULD TAKE PLACE, THEY SHOULD BE REMOVED FROM THIS CLASS
public class TransitionController {

    private GameController gameController;

    private FromAssemblyToTakeOffController fromAssemblyToTakeOffController;
    private FromTakeOffToFlightController fromTakeOffToFlightController;

    private LinkedBlockingQueue<Move> moveQueue;
    private Thread moveProcessorThread;

    public TransitionController(GameController gameController) {
        this.gameController = gameController;
        this.fromAssemblyToTakeOffController = new FromAssemblyToTakeOffController(gameController);
        this.fromTakeOffToFlightController = new FromTakeOffToFlightController(gameController);

        this.moveQueue = new LinkedBlockingQueue<>();
        this.moveProcessorThread = null;
    }

    public void flipFirstHourglass() {
        FlightType flightType = gameController.getFlightType();
        if (flightType == FlightType.TRIAL) {

        } else if (flightType == FlightType.TWO) {
            fromAssemblyToTakeOffController.flipFirstHourglass();
        }
    }


    public void addMoveToQueue(Move m) {
        moveQueue.add(m);
    }

    /* USEFUL TO HAVE A SEPARATE THREAD THAN THOSE MANAGING THE BUILDING OF THE ROCKETSHIP */
    public void startProcessingMoves() {
        this.moveProcessorThread = new Thread (() -> {
            try {
                System.out.println("Started processing transition-related moves on a dedicated thread");
                while (!Thread.currentThread().isInterrupted()) {

                    Move move = moveQueue.take(); /** BLOCKING INSTRUCTION **/
                    System.out.println("");
                    System.out.println("move taken. now activating it");

                    activateMove(move);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "TransitionMoveProcessor");
        this.moveProcessorThread.start();
    }

    public void terminateMoveProcessorThread() {
        if (moveProcessorThread != null && moveProcessorThread.isAlive()) {
            this.moveProcessorThread.interrupt();
            this.moveProcessorThread = null;
        }
    }


    public void activateMove(Move move) { //faccio un secondo check, che tuttavia era già stato fatto nel move dispatcher, giusto per sicurezza nel caso in cui le cose per qualche motivo fossero cambiate
        GamePhase currentGamePhase = gameController.getGamePhase();


        if(currentGamePhase == move.getPhase()) {
            switch(move.getPhase()) {
                case ASSEMBLY_PHASE -> {
                    switch(move.getType()) {
                        case FLIP_HOURGLASS -> {
                            System.out.println("dispatching FLIP_HOURGLASS move");
                            fromAssemblyToTakeOffController.flipHourglass(move);
                        } case READY_FOR_TAKEOFF -> {
                            System.out.println("dispatching READY_FOR_TAKEOFF move");
                            fromAssemblyToTakeOffController.terminateAssemblyForPlayer(move);
                        }
                    }
                } case TAKEOFF_PHASE -> {

                } case FLIGHT_PHASE -> {

                } case END_FLIGHT_PHASE -> {

                }
            }
        } else {
            GameLobby lobby = gameController.getLobby();
            lobby.logPlayer(move.getColor(), "too late. phase not appropriate for the move you sent");
        }
    }


    /* FOR ASSEMBLY PHASE */
    public boolean checkIsAssemblyOver() {
        return fromAssemblyToTakeOffController.checkIfAllPlayersTerminatedAssembly();
    }

    public boolean checkIsTotalTimeOver() { /** METHOD CALLED BY ROCKETSHIP BUILDING CONTROLLER TO KNOW WHENT TO RESTRICT PLAYERS' MOVES TO ONLY PLACING THE ONE COMPONENT THEY'RE HOLDING IN THEIR HAND, IF PRESENT AT ALL **/
        return fromAssemblyToTakeOffController.checkIsTotalTimeIsOver();
    }

    public List<PlayerColor> getPlayersInAssemblyFinishingOrder() {
        return fromAssemblyToTakeOffController.getPlayersThatFinishedAssembly();
    }

    public void forceStopHourglass() {
        if(fromAssemblyToTakeOffController.checkIfHasHourglass()) {
            System.out.println("FORCE-STOPPING HOURGLASS");
            fromAssemblyToTakeOffController.forceStopHourglass();
        }
    }


    /* FOR TAEKOFF PHASE */
    public boolean checkAreAllRocketshipsReadyForFlight() {
        return this.fromTakeOffToFlightController.checkAreRocketshipsReadyForFlight();
    }

    public Set<PlayerColor> getPlayersReadyForFlight() {
        return this.fromTakeOffToFlightController.getPlayersReadyForFlight();
    }

    public void setAsReadyForFlight(PlayerColor color) { /* CALLED BY CHECK INTEGRITY IN RocketshipBuildingController */
        this.fromTakeOffToFlightController.setAsReadyForFlight(color);
    }

}




class FromAssemblyToTakeOffController { /* PROCESSES THE FLIP_HOURGLASS OR READY_FOR_FLIGHT MOVES */

    private GameLobby lobby;


    public static final int MAX_NUM_OF_HOURGLASS_FLIPS_LV_TWO = 3;

    private boolean hasHourglass;
    private Hourglass hourglass; /* INNERCLASS SPECIFIED BELOW */

    private boolean isTotalTimeOver;
    private boolean haveAllPlayersFinishedAssembly;
    private List<PlayerColor> playersThatFinishedAssembly; // dipende da cosa succede con la hourglass


    /* CONSTRUCTOR */
    public FromAssemblyToTakeOffController(GameController gameController) {

        this.lobby = gameController.getLobby();

        FlightType flightType = gameController.getFlightType();
        this.isTotalTimeOver = false;
        switch (flightType) {
            case TRIAL -> { /* TRIAL GAMES ARE NOT MEANT TO WORK WITH HOURGLASSES/TIMERS */
                this.hourglass = null;
                this.hasHourglass = false;
            }
            case TWO -> {
                this.hourglass = new Hourglass(MAX_NUM_OF_HOURGLASS_FLIPS_LV_TWO);
                this.hasHourglass = true;
            }
        }

        this.haveAllPlayersFinishedAssembly = false;
        this.playersThatFinishedAssembly = new ArrayList<PlayerColor>();
    }


    public void flipHourglass(Move move) { // IL CONTROLLO LO FACCIAMO IN QUESTA CLASSE PERCHE' HA ACCESSO A QUALI SONO I PLAYER CHE HANNO TERMINATO, E CHE QUINDI POSSONO CAPOVOLGERE LA CLESSIDRA PER L'ULTIMA VOLTA

        PlayerColor playerThatRequestedToFlipHourglass = move.getColor();

        try {
            if (hasHourglass) {
                System.out.println("flipping hourglass. n of times flipped: " + hourglass.numberOfTimesHourglassFlipped + "/" + hourglass.maxNumberOfHourglassFlips);

                if (hourglass.numberOfTimesHourglassFlipped < hourglass.maxNumberOfHourglassFlips - 1) {
                    hourglass.flipHourglass();
                } else if (hourglass.numberOfTimesHourglassFlipped == hourglass.maxNumberOfHourglassFlips - 1) { // check su se è l'ultimo spot, solo un player che ha terminato può flippare la hourglass
                    if (hasPlayerTerminatedAssembly(playerThatRequestedToFlipHourglass)) {
                        hourglass.flipHourglass();
                    } else {
                        lobby.logPlayer(playerThatRequestedToFlipHourglass, "before flipping the hourglass for the last time, you need to declare READY_FOR_TAKEOFF");
                    }
                } else {
                    lobby.logPlayer(playerThatRequestedToFlipHourglass, "the rocketship was already flipped a maximum number of times ("+hourglass.numberOfTimesHourglassFlipped+"/" +hourglass.maxNumberOfHourglassFlips+")");
                }
            } else {
                lobby.logPlayer(playerThatRequestedToFlipHourglass, "you can't flip the hourglass in a TRIAL level game");
            }
        } catch (HourglassAlreadyActiveException e) {
            lobby.logPlayer(playerThatRequestedToFlipHourglass, e.getMessage());
        }
    }

    public void flipFirstHourglass() {
        try {
            lobby.logAllPlayers("flipping first hourglass");
            hourglass.flipHourglass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void terminateAssemblyForPlayer(Move move) {

        PlayerColor terminatingPlayer = move.getColor();
        System.out.println("terminating assembly for player" + terminatingPlayer);

        if(!playersThatFinishedAssembly.contains(terminatingPlayer)) {
            playersThatFinishedAssembly.add(terminatingPlayer);
            lobby.logAllPlayers("il player color " + terminatingPlayer + " ha terminato l'assemblaggio");
            if(lobby.getFinalNumberOfPlayers() == playersThatFinishedAssembly.size()) {
                haveAllPlayersFinishedAssembly = true;
            }


            StringBuilder orderOfAssemblyFinishersLog = new StringBuilder();
            orderOfAssemblyFinishersLog.append("current order: ");
            for(PlayerColor color : playersThatFinishedAssembly) {
                orderOfAssemblyFinishersLog.append(" -> " + color);
            }
            String orderOfAssemblyFinishersLogToSend = orderOfAssemblyFinishersLog.toString();
            lobby.logAllPlayers(orderOfAssemblyFinishersLogToSend);

        } else {
            lobby.logPlayer(terminatingPlayer, "you already declare READY_FOR_TAKEOFF");
        }
    }

    public boolean hasPlayerTerminatedAssembly(PlayerColor playerColor) {
        return playersThatFinishedAssembly.contains(playerColor);
    }

    public boolean checkIfAllPlayersTerminatedAssembly() {
        return haveAllPlayersFinishedAssembly;
    }

    public List<PlayerColor> getPlayersThatFinishedAssembly() {
        return this.playersThatFinishedAssembly;
    }

    public boolean checkIfHasHourglass() {
        return this.hasHourglass;
    }

    public void forceStopHourglass() {
        hourglass.shouldStop = true;
        if (hourglass.hourglassTimerThread != null && hourglass.hourglassTimerThread.isAlive()) {
            hourglass.hourglassTimerThread.interrupt();
        }
    }

    public boolean checkIsTotalTimeIsOver() {
        return this.isTotalTimeOver;
    }


    /** HOURGLASS INNER CLASS **/
    class Hourglass {
        private int maxNumberOfHourglassFlips;
        private int numberOfFlips;

        private int numberOfTimesHourglassFlipped;
        private boolean isHourglassRunning;

        private Thread hourglassTimerThread;
        private boolean shouldStop = false;

        public Hourglass(int maxNumberOfHourglassFlips) {
            this.maxNumberOfHourglassFlips = maxNumberOfHourglassFlips;
        }

        public void flipHourglass() throws HourglassAlreadyActiveException {
            try {
                if (!this.isHourglassRunning) {
                    this.shouldStop = false;

                    startHourglassThread();
                    this.hourglassTimerThread.start();
                } else {
                    throw new HourglassAlreadyActiveException();
                }
            } catch (HourglassAlreadyActiveException e) {
                throw new HourglassAlreadyActiveException();
            }
        }

        public void startHourglassThread() {
            this.hourglassTimerThread = new Thread(() -> {
                try {
                    turnOnHourglass();

                    /* COUNTDOWN */
                    for (int i = 0; i < 60; i++) {
                        if (shouldStop || Thread.currentThread().isInterrupted()) { /* THE BOOLEAN shouldStop IS USED TO FORCE STOP THE HOURGLASS FROM THE OUTSIDE IF ASSEMBLY PHASE IS TERMINATED BEFORE THE HOURGLASS TIME IS OVER */
                            System.out.println("Hourglass interrupted early");
                            return;
                        }
                        if(i%10 == 0 && i!= 0) {lobby.logAllPlayers(i + " seconds passed");}
                        Thread.sleep(1000);
                    }
                    turnOffHourglass();
                } catch (InterruptedException e) {
                    System.out.println("Hourglass thread was interrupted");
                    Thread.currentThread().interrupt();
                }
            }, "HourglassTimer");
        }

        public void turnOnHourglass() {
            this.isHourglassRunning = true;
            lobby.logAllPlayers("Hourglass started");
        }

        public void turnOffHourglass() {
            this.isHourglassRunning = false;
            this.numberOfTimesHourglassFlipped++;
            this.shouldStop = true;

            lobby.logAllPlayers("60 seconds passed");
            lobby.logAllPlayers("("+hourglass.numberOfTimesHourglassFlipped+"/" +hourglass.maxNumberOfHourglassFlips+") current hourglass' time's up");
            if(this.numberOfTimesHourglassFlipped == this.maxNumberOfHourglassFlips) {
                FromAssemblyToTakeOffController.this.isTotalTimeOver = true;

                lobby.logAllPlayers("Total time is up. you can only declare yourself as READY_FOR_TAKEOFF, or PLACE the component you're holding");
            }

        }

    }
    /* END OF HOURGLASS INNER CLASS */

}




// Non è gestita da un thread separato come fromAssemblyToTakeOffController, ma vi intervengono:
        // - da GameExecutorThread, per
            // 1. settare se rocketship pronta all'inizio della takeoff per poi
            // 2. controllare periodicamente se sono tutte le rs sono pronte
        // - e dai MoveProcessorThread dei rocketshipbuilding controllers (per aggiornare se rs pronta),
            // che chiamano il suo metodo setAsReadyForFlight() mediante setAsReadyForFlight() in TransitionController,
            // a sua volta chiamato da checkIntegrity in RS building controller in caso la nave risulti integra
class FromTakeOffToFlightController {

    private boolean areAllRocketshipsReadyForFlight; // [] si può rinominare "is takeoff over"

    private GameLobby lobby;


    private Set<PlayerColor> playersReadyForFlight;

    public FromTakeOffToFlightController(GameController gameController) {
        this.lobby = gameController.getLobby();
        this.playersReadyForFlight = new HashSet<>();
        this.areAllRocketshipsReadyForFlight = false;
    }

    public Set<PlayerColor> getPlayersReadyForFlight() {
        return this.playersReadyForFlight;
    }

    public void setAsReadyForFlight(PlayerColor playerColor) {
        playersReadyForFlight.add(playerColor);

        if(playersReadyForFlight.containsAll(lobby.getAllPlayerColors())) { // IF ALL ROCKETSHIPS ARE IN CONFORMITY WITH THE RULES, THIS UNLOCKS THE BOOLEAN THAT KEEPS THE GameExecutorThread IN LOOP in GameController
            areAllRocketshipsReadyForFlight = true;
        }
    }

    public boolean checkAreRocketshipsReadyForFlight() {
        return this.areAllRocketshipsReadyForFlight;
    }

}



class FromFlightToEndFlightController {

}