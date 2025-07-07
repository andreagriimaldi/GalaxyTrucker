package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.commands.userRequest.moves.FlightChoiceMove;
import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.components.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.min;

public class EnemiesCard extends AdventureCard {
    private EnemyType type;
    private int reward;
    private CubeToken[] rewardSmuggler;
    private float power;
    private int penalty;
    public int[] penaltyPirate;

    private int[] diceRolls;

    private boolean stillCheckingPower;

    private float playerPower;

    private Map<PlayerColor, Integer> attacksInflicted;
    private Map<PlayerColor, Boolean> awaitingShieldChoice;
    private Map<PlayerColor, Integer[]> tempCoordinates;


    private boolean acceptOrRejectRewardPhase;


    /**
     * constructor for the Smuggler cards
     * @param level in which level deck this card is used
     * @param ID it's identification code
     * @param days amounts of days to move forwards/backwards when the reward is collected
     * @param power the amount of power the enemies have
     * @param penalty how many boxes will be lost if defeated
     * @param rewardSmuggler which kind of boxes can be gained if successful
     */
    public EnemiesCard(AdventureType adventureType, AdventureLevel level, String ID, int days, int power, int penalty, CubeToken[] rewardSmuggler){
        super(adventureType, level, ID, days);
        this.type = EnemyType.SMUGGLER;
        this.power = power;
        this.penalty = penalty;
        this.skippable = false;
        this.rewardSmuggler = rewardSmuggler;


        this.stillCheckingPower = true;

        this.playerPower = 0;

        this.acceptOrRejectRewardPhase = false;

    }

    /**
     * constructor for the Slaver cards
     * @param level in which level deck this card is used
     * @param ID it's identification code
     * @param days amounts of days to move forwards/backwards when the reward is collected
     * @param power the amount of power the enemies have
     * @param penalty how many crewmates will be lost if defeated
     * @param reward how many credits can be gained if successful
     */
    public EnemiesCard(AdventureType adventureType, AdventureLevel level, String ID, int days, int power, int penalty, int reward){
        super(adventureType, level, ID, days);
        this.type = EnemyType.SLAVER;
        this.power = power;
        this.penalty = penalty;
        this.reward = reward;

        this.stillCheckingPower = true;

        this.acceptOrRejectRewardPhase = false;
    }

    /**
     * constructor for the Pirate cards
     * @param level in which level deck this card is used
     * @param ID it's identification code
     * @param days amounts of days to move forwards/backwards when the reward is collected
     * @param power the amount of power the enemies have
     * @param penaltyPirate which kind of shots will be sent towards the defeated player
     * @param reward how many credits can be gained if successful
     */
    public EnemiesCard(AdventureType adventureType, AdventureLevel level, String ID, int days, int power, int[] penaltyPirate, int reward) {
        super(adventureType, level, ID, days);
        this.type = EnemyType.PIRATES;
        this.power = power;
        this.penaltyPirate = penaltyPirate;
        this.reward = reward;

        this.stillCheckingPower = true;

        this.diceRolls = new int[penaltyPirate.length];

        this.attacksInflicted = new ConcurrentHashMap<>();
        this.awaitingShieldChoice = new ConcurrentHashMap<>();
        this.tempCoordinates = new ConcurrentHashMap<>();

        this.acceptOrRejectRewardPhase = false;
    }

    /** Getter Methods */

    public EnemyType getEnemyType() {
        return type;
    }

    public float getPower(){
        return power;
    }

    public int getReward(){
        return reward;
    }

    public String getSmugglerReward() {
        StringBuilder string = new StringBuilder();
        for (CubeToken cubeToken : rewardSmuggler) {
            switch (cubeToken.getType()) {
                case REDCUBE -> string.append("\uD83D\uDD34");
                case YELLOWCUBE -> string.append("\uD83D\uDFE1");
                case GREENCUBE -> string.append("\uD83D\uDFE2");
                case BLUECUBE -> string.append("\uD83D\uDD35");
            }
            string.append(" ");
        }

        return string.toString();
    }

    public int getPenalty(){
        return penalty;
    }

    public String getPiratePenalty(){
        StringBuilder penalty = new StringBuilder();
        for (int j : penaltyPirate) {
            penalty.append(getUnicodeFromInt(j));
        }
        return penalty.toString();
    }

    public String getUnicodeFromInt(int penalty){
        return switch (penalty){
            case 1 -> "↓";
            case 2 -> "←";
            case 3 -> "↑";
            case 4 -> "→";
            case 5 -> "↡";
            case 6 -> "↞";
            case 7 -> "↟";
            case 8 -> "↠";
            default -> null;
        };
    }

    /**
     * if the player wins against the enemies this method will give them the correct reward
     */
    public void giveReward() {
        try {
            Player currentPlayer = playersInOrder.get(currentPlayerIndex);

            // [] occhio alla questione che il player deve anche decidere se skippare i giorni in base a se accetta il reward o meno

            switch (type) {
                case SMUGGLER -> {
                    System.out.println("adding cubes to " + currentPlayer.getColor() + " player, and moving them backwards by " + days + " days");
                    ac.getGameSession().getFlightBoard().movePlayerByN(currentPlayer, days);
                    currentPlayer.getBoard().addCubes(rewardSmuggler);
                }
                case SLAVER, PIRATES -> {
                    System.out.println("adding " + reward + " credits to " + currentPlayer.getColor() + " player, and moving them backwards by " + days + " days");
                    ac.getGameSession().getFlightBoard().movePlayerByN(currentPlayer, days);
                    currentPlayer.addCredits(reward);
                }
                default -> {

                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    public ComponentSide getSideFromInt(int penalty){
        return switch (penalty){
            case 1,5 -> ComponentSide.NORTH;
            case 2,6 -> ComponentSide.EAST;
            case 3,7 -> ComponentSide.SOUTH;
            case 4,8 -> ComponentSide.WEST;
            default -> null;
        };
    }



    public void nextPlayer() {
        currentPlayerIndex++;
        ac.getLobby().logAllPlayers("moving onto the next player");
        playerPower = 0; // reset current player's power
    }


    @Override
    public void handle() {
        initializePlayersInOrder();
        ac.getLobby().logAllPlayers("extracted a "+ getEnemyType()+ " enemy card, with a FIRE POWER OF " + power);

        routine();
    }

    public void rollDice() {
        Random random = new Random();
        for(int i = 0; i < diceRolls.length; i++) {
            diceRolls[i] = (random.nextInt(6) + 1) + (random.nextInt(6) + 1);
        }
    }



    public void informPlayersOnAttacks() {
        if(getEnemyType() == EnemyType.PIRATES) {
            rollDice();

            String shotsInformation = "";
            for(int i = 0; i < penaltyPirate.length; i++) {
                String attackSize = "";

                if(penaltyPirate[i] <= 4) {
                    attackSize = "small";
                } else {
                    attackSize = "big";
                }

                shotsInformation += "shot #" + (i+1) + " : " + attackSize + " attack (" + getUnicodeFromInt(penaltyPirate[i])+ " line "+ diceRolls[i] +") " + System.lineSeparator();
            }

            ac.getLobby().logAllPlayers(shotsInformation);
        }
    }


    public void routine() {

        // asking players how many double thrusters to activate

        System.out.println("");

        System.out.println("stato di stillCheckingPower: " + stillCheckingPower);

        if (stillCheckingPower) {
            simulatePowerConfrontation();
        } else if (getEnemyType() == EnemyType.PIRATES) {
            if(!attacksInflicted.isEmpty()) {
                informPlayersOnAttacks();

                System.out.println("now awaiting for players to finish defending");

                for(PlayerColor playerColor : attacksInflicted.keySet()) {
                    try {
                        preparingToNextAttackAgainstPlayer(ac.getLobby().getPlayerFromColor(playerColor)); // [] shouldn't take place here, but after a player won, or all players have been beaten
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void initializePlayersInOrder() {
        if(playersInOrder == null) {
            playersInOrder = ac.getGameSession().getFlightBoard().returnPlayersInOrder();
        }
    }


    public void simulatePowerConfrontation() {

        Player currentPlayer = playersInOrder.get(currentPlayerIndex);

        float defaultPlayerPower = currentPlayer.getBoard().checkDefaultFirePower();
        boolean hasDoubleCannons = currentPlayer.getBoard().hasDoubleCannons();
        boolean hasBatteries = currentPlayer.getBoard().hasBatteries();

        PlayerColor currentColor = currentPlayer.getColor();


        if (defaultPlayerPower > power) { // wins without needing to activate double cannons
            playerPower = defaultPlayerPower;

            evaluateCurrentPlayerPower();


        } else if (hasDoubleCannons) { // otherwise, if
            if (hasBatteries) {
                if(ac.getLobby().isColorConnected(currentPlayer.getColor())) {
                    ac.getLobby().logPlayer(currentPlayer.getColor(), "choose number of double cannons to activate (total batteries: " + currentPlayer.getBoard().checkTotalBatteries() + " (double cannons available to use: " + currentPlayer.getBoard().checkAvailableDoubleCannons() + "), firepower: " + currentPlayer.getBoard().checkDefaultFirePower() + "-" + currentPlayer.getBoard().checkMaxFirePower() + ")");
                    awaitingPlayerChoice = true;
                } else {
                    ac.getLobby().logAllPlayers(currentPlayer.getColor() + " player is disconnected and so the automatic move to not activate any double cannons was made for them");
                    playerPower = currentPlayer.getBoard().checkDefaultFirePower();
                    evaluateCurrentPlayerPower();
                }
            } else {
                ac.getLobby().logPlayer(currentPlayer.getColor(), "you don't have any batteries to activate your double cannons");
                playerPower = currentPlayer.getBoard().checkDefaultFirePower();
                evaluateCurrentPlayerPower();
            }
        } else {
            ac.getLobby().logPlayer(currentPlayer.getColor(), "you don't have double cannons to activate");
            playerPower = currentPlayer.getBoard().checkDefaultFirePower();
            evaluateCurrentPlayerPower();

        }
    }


    public void evaluateCurrentPlayerPower() {


        Player currentPlayer = playersInOrder.get(currentPlayerIndex);


        ac.getLobby().logAllPlayers("confronting " + currentPlayer.getColor() + " player's power");

        if (playerPower < power) { // il player perde

            ac.getLobby().logPlayer(currentPlayer.getColor(), "you lost power confrontation");
            sufferPenalty();
            nextPlayerAndCheckIfEvaluatedAllPlayersPower();

        } else if (playerPower == power) {
            ac.getLobby().logPlayer(currentPlayer.getColor(), "you neither lost nor won power confrontation");
            nextPlayerAndCheckIfEvaluatedAllPlayersPower();
        } else { /* SE VINCE */
            if(ac.getLobby().isColorConnected(currentPlayer.getColor())) {
                ac.getLobby().logPlayer(currentPlayer.getColor(), "you won the confrontation with a power of " + playerPower + ". choose to accept [1] or reject [0] the reward");
                acceptOrRejectRewardPhase = true;
                stillCheckingPower = false;
                checkIfCardDone();
            } else {
                ac.getLobby().logPlayer(currentPlayer.getColor(), "you won the confrontation but you were disconnected and so the choice to reject the reward was automatically made for you");
                stillCheckingPower = false;
                checkIfCardDone();
            }
        }

        routine();
    }

    public void initiatePirateAttack(Player currentPlayer) {
        PlayerColor currentColor = currentPlayer.getColor();

        ac.getLobby().logPlayer(currentColor, "initiatePirateAttack() method called for " + currentColor + " player");

        attacksInflicted.put(currentColor, 0);
        awaitingShieldChoice.put(currentColor, false);
    }



    public void preparingToNextAttackAgainstPlayer(Player currentPlayer) {

        int attacksInflictedForCurrentPlayer = attacksInflicted.get(currentPlayer.getColor());

        if(!ac.isPlayersShipBroken(currentPlayer.getColor())) {
            if(attacksInflictedForCurrentPlayer < penaltyPirate.length) {
                System.out.println("attacking " + currentPlayer.getColor() + " now");

                int shotToInflict = penaltyPirate[attacksInflicted.get(currentPlayer.getColor())];
                ComponentSide shotSide = getSideFromInt(shotToInflict);
                int currentRoll = diceRolls[attacksInflicted.get(currentPlayer.getColor())];


                boolean shotInRange = (currentRoll > 4 && currentRoll < 10) || ((currentRoll == 4 || currentRoll == 10)
                        && (shotSide == ComponentSide.NORTH || shotSide == ComponentSide.SOUTH));


                if(shotInRange) {

                    Component limitComponent;

                    if(shotSide == ComponentSide.NORTH || shotSide == ComponentSide.SOUTH) {
                        limitComponent = currentPlayer.getBoard().getLimitComponent(currentRoll-4, shotSide);
                    }
                    else {
                        limitComponent = currentPlayer.getBoard().getLimitComponent(currentRoll-5, shotSide);
                    }


                    boolean anyComponentInTheWay = (limitComponent != null && limitComponent.getComponentType() != ComponentType.EMPTY_COMPONENT);

                    if(anyComponentInTheWay) {

                        int[] limitComponentCoordinatesArray = currentPlayer.getBoard().getCoordinates(limitComponent);
                        Integer[] limitComponentCoordinates = new Integer[]{limitComponentCoordinatesArray[0], limitComponentCoordinatesArray[1]};

                        int row = limitComponentCoordinates[0] + 5;
                        int col = limitComponentCoordinates[1] + 4;


                        ac.getLobby().logPlayer(currentPlayer.getColor(),
                                limitComponent.getComponentType()
                                        +" ("+ row
                                        +", "+ col
                                        +") is in the way of the pirate shot");


                        boolean smallShot = (shotToInflict <= 4);

                        if(smallShot) {

                            boolean hasShieldCoveringSide = currentPlayer.getBoard().hasShield(shotSide);
                            boolean hasBatteries = (currentPlayer.getBoard().checkTotalBatteries() > 0 );

                            if(hasShieldCoveringSide && hasBatteries) {
                                if(ac.getLobby().isColorConnected(currentPlayer.getColor())) {
                                    awaitingShieldChoice.put(currentPlayer.getColor(), true); // - alzo flag che sono in attesa di risposta
                                    tempCoordinates.put(currentPlayer.getColor(), limitComponentCoordinates);
                                    ac.getLobby().logPlayer(currentPlayer.getColor(), "you can decide to activate a shield and defend from the incoming attack");
                                } else {
                                    ac.getLobby().logAllPlayers(currentPlayer.getColor() + " player is disconnected and so the automatic move to not activate any shields was made for them");
                                    inflictAttack(currentPlayer, limitComponentCoordinates);
                                    preparingToNextAttackAgainstPlayer(currentPlayer);
                                }
                            } else {

                                if(hasShieldCoveringSide) {
                                    ac.getLobby().logPlayer(currentPlayer.getColor(), "you have a shield, but you don't have any batteries to activate it, and so you'll be hit");
                                } else {
                                    ac.getLobby().logPlayer(currentPlayer.getColor(), "you don't have a shield to protect yourself, and so you'll be hit");
                                }

                                inflictAttack(currentPlayer, limitComponentCoordinates);
                                preparingToNextAttackAgainstPlayer(currentPlayer);
                            }
                        } else {
                            ac.getLobby().logPlayer(currentPlayer.getColor(), "you can't protect from a big attack, and so you'll be hit regardless");
                            inflictAttack(currentPlayer, limitComponentCoordinates);
                            preparingToNextAttackAgainstPlayer(currentPlayer);
                        }

                    } else {
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "shot missed, no component was in the way");
                        dontInflictAttack(currentPlayer);
                        preparingToNextAttackAgainstPlayer(currentPlayer);
                    }


                } else {
                    ac.getLobby().logPlayer(currentPlayer.getColor(), "shot missed by a lot");
                    dontInflictAttack(currentPlayer);
                    preparingToNextAttackAgainstPlayer(currentPlayer);
                }


            } else {
                ac.getLobby().logPlayer(currentPlayer.getColor(), "you are done being inflicted attacks");
                checkIfCardDone();
            }
        }

    }



    public void inflictAttack(Player currentPlayer, Integer[] attackCoordinates) {

        currentPlayer.getBoard().removeComponent(
                attackCoordinates[0],
                attackCoordinates[1]);

        increaseNumberOfInflictedAttacks(currentPlayer.getColor());

        ac.checkBrokenShip(currentPlayer);
    }

    public void dontInflictAttack(Player currentPlayer) {
        increaseNumberOfInflictedAttacks(currentPlayer.getColor());
    }

    public void increaseNumberOfInflictedAttacks(PlayerColor currentColor) {
        attacksInflicted.put(currentColor, attacksInflicted.get(currentColor) + 1);
        ac.getLobby().logPlayer(currentColor, attacksInflicted.get(currentColor) + " out of " + penaltyPirate.length + " attacks inflicted");
    }


    public void sufferPenalty() {
        Player currentPlayer = playersInOrder.get(currentPlayerIndex);

        switch(getEnemyType()) {

            case EnemyType.PIRATES -> {
                ac.getLobby().logPlayer(currentPlayer.getColor(), "you'll suffer piretes penalty accordingly. prepare to defend");
                initiatePirateAttack(currentPlayer);
            }

            case EnemyType.SLAVER -> {
                ac.getLobby().logPlayer(currentPlayer.getColor(), "you'll suffer slaver penalty accordingly");
                enslave(currentPlayer);
            }

            case EnemyType.SMUGGLER -> {
                ac.getLobby().logPlayer(currentPlayer.getColor(), "you'll suffer smuggler penalty accordingly");
                smuggle(currentPlayer);
            }
        }
    }

    public void nextPlayerAndCheckIfEvaluatedAllPlayersPower() {
        nextPlayer();
        System.out.println("checking if evaluated all players' power");
        if(currentPlayerIndex == playersInOrder.size()) {
            stillCheckingPower = false;
            checkIfCardDone();
        }
    }


    public void smuggle(Player currentPlayer) {
        currentPlayer.getBoard().smuggle(penalty);
    }

    public void enslave(Player currentPlayer) {
        ac.getLobby().logPlayer(currentPlayer.getColor(), "removing " + penalty + " crewmates from your rocketship");
        currentPlayer.getBoard().removeCrewmates(penalty);
    }



    @Override
    public void handle(Move move) {

        // [] attenzione a filtro sulle mosse

        FlightChoiceMove adventureChoiceMove = (FlightChoiceMove) move;
        PlayerColor movesColor = adventureChoiceMove.getColor();
        Player movesPlayer = ac.getLobby().getPlayerFromColor(movesColor);

        int choice = adventureChoiceMove.getChoice();


        // [] bisogna mettere awaitingPlayerChoice = false;

        if(acceptOrRejectRewardPhase) {
            if(movesColor == playersInOrder.get(currentPlayerIndex).getColor()) {
                if (choice == 1) {
                    giveReward();
                    ac.getGameSession().getFlightBoard().movePlayerByN(playersInOrder.get(currentPlayerIndex), days);
                    acceptOrRejectRewardPhase = false;
                    System.out.println("accepted reward");
                }
                else if (choice == 0) {
                    days = 0;
                    acceptOrRejectRewardPhase = false;
                } else {
                    ac.getLobby().logPlayer(movesColor, "you either need to choose 0 or 1");
                }
            }

            checkIfCardDone();
            return;
        }

        if(stillCheckingPower) { /** MEANS THAT THE PLAYER HAS ALREADY LOST CONFRONTATION AND IS BEING INFLICTED ATTACKS **/
            if(movesColor == playersInOrder.get(currentPlayerIndex).getColor()) { /** MEANS THAT IT'S STILL CONFRONTING FIRE POWER **/

                int choiceOfBatteriesToUse = adventureChoiceMove.getChoice();
                int availableDoubleCannons = playersInOrder.get(currentPlayerIndex).getBoard().getComponentByType(ComponentType.DOUBLE_CANNON).size();

                int cannonsToActivate = min(choiceOfBatteriesToUse, availableDoubleCannons);



                playerPower = playersInOrder.get(currentPlayerIndex).getBoard().checkActivatableFirePower(cannonsToActivate);
                // [] IL METODO CHECK ACTUAL CANNONS E' DA MODIFICARE, IN QUANTO NON RITORNA LA POTENZA DI FUOCO DEI CANNONI LATERALI/POSTERIORI DIMEZZATA
                // [] UN SUGGERIMENTO E' DI FARE getComponentsByType(ComponentType.DOUBLE_CANNON) E CONSIDERARE PRIMA TUTTI QUELLI CHE PUNTANO FRONTALMENTE

                ac.getLobby().logPlayer(movesColor, "you activated a total of " + playerPower);


                try {
                    movesPlayer.getBoard().removeBatteries(cannonsToActivate);
                    ac.getLobby().logPlayer(movesColor, "using " + cannonsToActivate + " batteries");
                } catch(Exception e) {
                    e.printStackTrace();
                }



                evaluateCurrentPlayerPower();


            } else {
                ac.getLobby().logPlayer(movesColor, "not your turn to make a move");
            }
        } else if (getEnemyType() == EnemyType.PIRATES) { // defending from attacks
            if(ac.isPlayersShipBroken(movesColor)) {
                    boolean isInPartsRange = choice < movesPlayer.getBoard().getSizeOfSetOfConnectedComponents();

                    if (isInPartsRange) {
                        movesPlayer.getBoard().chooseBrokenShip(choice);
                        ac.removeFromNeedsToFixRocketship(movesColor);
                        ac.getLobby().logPlayer(movesColor, "choosing to save group " + choice);

                        int i = 0;
                        for (Set<Component> set : movesPlayer.getBoard().checkIntegrity()) {
                            System.out.println("Group " + i++ + ":");
                            for (Component c : set) {
                                int[] coordinates = movesPlayer.getBoard().getCoordinates(c);
                                int row = coordinates[0] + 5;
                                int col = coordinates[1] + 4;
                                System.out.println("  - (" + row + ", " + col + ") " + System.identityHashCode(c) + " " + c);
                            }
                        }

                        preparingToNextAttackAgainstPlayer(movesPlayer);
                    } else {
                        ac.getLobby().logPlayer(movesColor, "you need to choose a group in the proposed range");
                    }


            } else if (attacksInflicted.get(movesColor) < penaltyPirate.length) {
                if(awaitingShieldChoice.get(movesColor)) {
                    try {
                        if (choice == 1) {
                            dontInflictAttack(movesPlayer);

                            try {
                                movesPlayer.getBoard().removeBatteries(1);
                                ac.getLobby().logPlayer(movesColor, "using " + 1 + " battery");
                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                            awaitingShieldChoice.put(movesColor, false);
                            preparingToNextAttackAgainstPlayer(movesPlayer);
                        }
                        else if (choice == 0) {
                            Integer[] attackCoordinates = tempCoordinates.remove(movesPlayer.getColor());

                            inflictAttack(movesPlayer, attackCoordinates);
                            awaitingShieldChoice.put(movesColor, false);
                            preparingToNextAttackAgainstPlayer(movesPlayer);
                        } else {
                            ac.getLobby().logPlayer(movesColor, "you either need to choose 0 or 1");
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ac.getLobby().logPlayer(movesColor, "the game is not waiting for you to make a choice");
                }
            } else { // vuol dire che il player ha gia subito tutti gli attacchi
                ac.getLobby().logPlayer(movesColor, "you finished attacks");
            }
        }
    }


    @Override
    public boolean isAwaitingPlayerChoice() {
        // include anche la fase di accetta/rifiuta ricompensa
        return awaitingPlayerChoice || acceptOrRejectRewardPhase;
    }



    public void checkIfCardDone() {

        System.out.println("checking if card is done");

        boolean notOverYet = false;

        if (getEnemyType() == EnemyType.PIRATES) {

            // 1. se sto ancora calcolando la potenza -> non è finita
            if (stillCheckingPower) {
                notOverYet = true;
            }

            // 2. se devo ancora chiedere accetta/rifiuta -> non è finita
            if (acceptOrRejectRewardPhase) {
                notOverYet = true;
            }

            // 3. se ci sono attacchi e non sono stati tutti sparati -> non è finita
            if (!attacksInflicted.isEmpty()) {
                for (PlayerColor pc : attacksInflicted.keySet()) {
                    if (attacksInflicted.get(pc) < penaltyPirate.length) {
                        notOverYet = true;
                        break;
                    }
                }
            }


            // [] ? mettere qua anche controllo su integrità navi ?



        } else {  // SMUGGLER o SLAVER
            notOverYet = stillCheckingPower || acceptOrRejectRewardPhase;
        }

        if (notOverYet) {
            System.out.println("-- card still not over --");
        } else {
            effectEnded = true;
            System.out.println("-- card is over --");
            drawNextCard();
        }
    }

    public String toString(){
        return "An enemies card";
    }

}
