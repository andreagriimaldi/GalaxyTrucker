package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.commands.userRequest.moves.FlightChoiceMove;
import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.enums.AdventureLevel;
import it.polimi.ingsw.enums.AdventureType;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.components.Component;
import it.polimi.ingsw.enums.ComponentSide;
import it.polimi.ingsw.enums.ComponentType;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.min;

public class WarzoneCard extends AdventureCard {


    private Player[] sufferersOrder;


    private int currentPlayerIndex;

    // conditions
    private Player lowestPowerPlayer;
    private float lowestPower;

    // penalties
    private int[] damage;
    private int cargoLost;
    private int crewLost;

    // 1 = lowestCrew, 2 = lowestThrust, 3 = lowestPower
    private int[] conditions;
    private int conditionIndex;

    private boolean sufferAttack;
    private boolean awaitingShieldChoice;
    private int attacksInflicted;
    private boolean defend;

    private int[] diceRolls;
    private Map<PlayerColor, Integer[]> tempCoordinates;

    // 1 = damage, 2 = cargoLost, 3 = crewLost, 4 = daysLost
    private int[] penalties;

    public WarzoneCard(AdventureType type, AdventureLevel level, String ID, int days, int crewLost, int[] damage, int cargoLost,
                       int[] conditions, int[] penalties) {
        super(type, level, ID, days);
        this.skippable = false;
        this.crewLost = crewLost;
        this.damage = damage;
        this.cargoLost = cargoLost;
        this.sufferersOrder = new Player[3];
        this.currentPlayerIndex = 0;
        this.lowestPowerPlayer = null;
        this.lowestPower = 0;
        this.conditions = conditions;
        this.conditionIndex = 0;
        this.sufferAttack = false;
        this.attacksInflicted = 0;
        this.defend = false;
        this.penalties = penalties;
        this.diceRolls = new int[damage.length];
        this.tempCoordinates = new ConcurrentHashMap<>();
    }

    /** getter methods*/

    public String getConditionPenalty(int i){
        return String.format("Condition: %s, Penalty %s", translateCondition(conditions[i]), translatePenalty(penalties[i]));
    }

    public String translateCondition(int i){
        return switch (i) {
            case 1 -> "lowest crew";
            case 2 -> "lowest thrust";
            case 3 -> "lowest power";
            default -> "";
        };
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

    public String translatePenalty(int i){
        switch (i) {
            case 1 -> {
                StringBuilder sb = new StringBuilder();
                for (int j : damage) {
                    sb.append(getUnicodeFromInt(j));
                }
                return sb.toString();
            }
            case 2 -> {
                return String.format("lose %d cubes", cargoLost);
            }
            case 3 -> {
                return String.format("lose %d crewmembers", crewLost);
            }
            case 4 -> {
                return String.format("lose %d days", days);
            }
            default -> {
                return "";
            }
        }
    }


    public Player findPlayerWithLowestCrew(){
        int lowestCrewNum = 0;
        for(Player p : playersInOrder){
            if((p.getBoard().checkTotalCrew() < lowestCrewNum) || (lowestPowerPlayer == null)){
                lowestPowerPlayer = p;
                lowestCrewNum = p.getBoard().checkTotalCrew();
            }
        }
        return lowestPowerPlayer;
    }



    public void nextPlayer() {
        currentPlayerIndex++;
    }


    public void handle() {
        if(playersInOrder == null) {
            playersInOrder = ac.getGameSession().getFlightBoard().returnPlayersInOrder();
        }

        if(playersInOrder.size() == 1) {
            ac.getLobby().logAllPlayers("warzone card has to be skipped if there's only one player left on the flightboard");
            drawNextCard();
        } else {
            rollDice();
            routine();
        }
    }

    public void rollDice() {
        Random random = new Random();
        for(int i = 0; i < diceRolls.length; i++) {
            diceRolls[i] = (random.nextInt(6) + 1) + (random.nextInt(6) + 1);
        }
    }

    public void routine() {
        /** SUFFER ATTACK OR ASK PLAYER TO ACTIVATE THRUSTER/CANNON **/

        switch (conditions[conditionIndex]) {
            case 1 -> { // NUMBER OF CREW MEMBERS CHALLENGE
                findPlayerWithLowestCrew();
                ac.getLobby().logAllPlayers("inflicting penalties to first player " + lowestPowerPlayer.getColor() +" which among those having the lowest crew, is the one that's ahead in the race");

                inflictPenalty();
            }
            case 2 -> { // THRUST POWER CHALLENGE


                Player currentPlayer = playersInOrder.get(currentPlayerIndex);
                boolean hasDoubleThrusters = currentPlayer.getBoard().hasDoubleThrusters();
                boolean hasBatteries = currentPlayer.getBoard().hasBatteries();


                // asking players how many double thrusters to activate
                if(hasDoubleThrusters) {
                    if(hasBatteries) {
                        if(ac.getLobby().isColorConnected(currentPlayer.getColor())) {
                            ac.getLobby().logPlayer(currentPlayer.getColor(), "choose number of double thrusters to activate (total batteries: " + currentPlayer.getBoard().checkTotalBatteries() + " (double thrusters available to use: " + currentPlayer.getBoard().checkAvailableDoubleThrusters() + "), thrustpower: " + currentPlayer.getBoard().checkDefaultThrustPower() + "-" + currentPlayer.getBoard().checkMaxThrustPower() + ")");
                            awaitingPlayerChoice = true;
                        } else {
                            ac.getLobby().logPlayer(playersInOrder.get(currentPlayerIndex).getColor(), "you were disconnected and so the choice to not activate any double thrusters was made for you");

                            float currentPlayerTotalFirePower = currentPlayer.getBoard().checkDefaultThrustPower();
                            checkAndUpdateLowestPower(currentPlayerTotalFirePower);

                            askNextPlayerOrInflictPenalty();
                        }

                    } else {
                        ac.getLobby().logPlayer(playersInOrder.get(currentPlayerIndex).getColor(), "you don't have any batteries to activate your double thrusters");

                        float currentPlayerTotalFirePower = currentPlayer.getBoard().checkDefaultThrustPower();
                        checkAndUpdateLowestPower(currentPlayerTotalFirePower);

                        askNextPlayerOrInflictPenalty();
                    }
                } else {
                    ac.getLobby().logPlayer(playersInOrder.get(currentPlayerIndex).getColor(), "you don't have double thrusters to activate");

                    float currentPlayerTotalFirePower = currentPlayer.getBoard().checkDefaultThrustPower();
                    checkAndUpdateLowestPower(currentPlayerTotalFirePower);

                    askNextPlayerOrInflictPenalty();
                }
            }
            case 3 -> { // FIRE POWER CHALLENGE

                // asking players how many double cannons to activate
                Player currentPlayer = playersInOrder.get(currentPlayerIndex);
                boolean hasDoubleCannons = currentPlayer.getBoard().hasDoubleCannons();
                boolean hasBatteries = currentPlayer.getBoard().hasBatteries();

                if(hasDoubleCannons) {

                    if(hasBatteries) {
                        if(ac.getLobby().isColorConnected(currentPlayer.getColor())) {
                            ac.getLobby().logPlayer(currentPlayer.getColor(), "choose number of double cannons to activate (total batteries: " + currentPlayer.getBoard().checkTotalBatteries() + " (double cannons available to use: " + currentPlayer.getBoard().checkAvailableDoubleCannons() + "), firepower: " + currentPlayer.getBoard().checkDefaultFirePower() + "-" + currentPlayer.getBoard().checkMaxFirePower() + ")");
                            awaitingPlayerChoice = true;
                        } else {
                            ac.getLobby().logPlayer(playersInOrder.get(currentPlayerIndex).getColor(), "you were disconnected and so the choice to not activate any double cannons was made for you");

                            float currentPlayerTotalFirePower = currentPlayer.getBoard().checkDefaultFirePower();
                            checkAndUpdateLowestPower(currentPlayerTotalFirePower);

                            askNextPlayerOrInflictPenalty();
                        }
                    } else {
                        ac.getLobby().logPlayer(playersInOrder.get(currentPlayerIndex).getColor(), "you don't have any batteries to activate your double cannons");

                        float currentPlayerTotalFirePower = currentPlayer.getBoard().checkDefaultFirePower();
                        checkAndUpdateLowestPower(currentPlayerTotalFirePower);

                        askNextPlayerOrInflictPenalty();
                    }
                } else {
                    ac.getLobby().logPlayer(playersInOrder.get(currentPlayerIndex).getColor(), "you don't have any double cannons to activate");

                    float currentPlayerTotalFirePower = currentPlayer.getBoard().checkDefaultFirePower();
                    checkAndUpdateLowestPower(currentPlayerTotalFirePower);

                    askNextPlayerOrInflictPenalty();

                }
            }
        }
    }


    public void inflictPenalty() {
        switch (penalties[conditionIndex]) {
            case 1 -> {  // damage
                preparingToNextAttackAgainstPlayer();
                // transition to next condition is not immediate. will need to wait choices made by player
            }
            case 2 -> {  // cargoLost
                for (int j = 0; j < cargoLost; j++) {
                    lowestPowerPlayer.getBoard().removeCubeByPrice();
                }
                transitionToNextConditionOrEndCard();
            }
            case 3 -> {  // crewLost
                ac.getLobby().logAllPlayers("removing " + crewLost + " crew mates from " + lowestPowerPlayer.getColor() + " player");
                lowestPowerPlayer.getBoard().removeCrewmates(crewLost);
                transitionToNextConditionOrEndCard();
            }
            case 4 -> {  // daysLost
                ac.getLobby().logAllPlayers("moving " + lowestPowerPlayer.getColor() + " backward by " + days + " days");
                ac.getGameSession().getFlightBoard().movePlayerByN(lowestPowerPlayer, days);
                playersInOrder = ac.getGameSession().getFlightBoard().returnPlayersInOrder(); // update playersInOrder in case the positions on the map were modified
                transitionToNextConditionOrEndCard();
            }
        }
    }



    public void transitionToNextConditionOrEndCard() {
        lowestPowerPlayer = null;
        currentPlayerIndex = 0;

        if (conditionIndex < conditions.length - 1) {
            conditionIndex++;
            routine();
        } else {
            drawNextCard();
        }
    }


    public void checkAndUpdateLowestPower(float currentPlayerTotalPower) {
        if (lowestPowerPlayer == null || currentPlayerTotalPower < lowestPower) {
            Player currentPlayer = playersInOrder.get(currentPlayerIndex);
            lowestPowerPlayer = currentPlayer;
            lowestPower = currentPlayerTotalPower;
        }
    }


    public void askNextPlayerOrInflictPenalty() {
        nextPlayer();

        if(currentPlayerIndex == playersInOrder.size()) {
            inflictPenalty();
        } else {
            routine();
        }

    }



    @Override
    public void handle(Move move) {

        // [] attenzione a filtro sulle mosse

        FlightChoiceMove adventureChoiceMove = (FlightChoiceMove) move;
        PlayerColor movesColor = adventureChoiceMove.getColor();

        Player movesPlayer = ac.getLobby().getPlayerFromColor(movesColor);

        int choice = adventureChoiceMove.getChoice();

        System.out.println("received move by " + movesColor);


        // [] bisogna mettere awaitingPlayerChoice = false;

        //if(awaitingPlayerChoice) {
            if(ac.isPlayersShipBroken(movesColor)) {
                if (movesColor == lowestPowerPlayer.getColor()) {
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

                        preparingToNextAttackAgainstPlayer();
                    } else {
                        ac.getLobby().logPlayer(movesColor, "you need to choose a group in the proposed range");
                    }

                } else {
                    ac.getLobby().logPlayer(movesColor, "not your turn to make a move");
                }
            } else if(awaitingShieldChoice) { // alla luce del subire simulazione attacco
                if (movesColor == lowestPowerPlayer.getColor()) {
                    try {


                        if (choice == 1) {
                            dontInflictAttack(movesPlayer);

                            try {
                                movesPlayer.getBoard().removeBatteries(1);
                                ac.getLobby().logPlayer(movesColor, "using " + 1 + " battery");
                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                            awaitingShieldChoice = false;
                            preparingToNextAttackAgainstPlayer();
                        }
                        else if (choice == 0) {
                            Integer[] attackCoordinates = tempCoordinates.remove(movesPlayer.getColor());

                            inflictAttack(movesPlayer, attackCoordinates);
                            awaitingShieldChoice = false;
                            preparingToNextAttackAgainstPlayer();
                        } else {
                            ac.getLobby().logPlayer(movesColor, "you either need to choose 0 or 1");
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ac.getLobby().logPlayer(movesColor, "not your turn to make a move");
                }
            } else if (!sufferAttack) {
                Player currentPlayer = playersInOrder.get(currentPlayerIndex);

                if (movesColor == playersInOrder.get(currentPlayerIndex).getColor()) {


                    switch (conditions[conditionIndex]) {
                        case 1 -> {
                            // do nothing
                        }
                        case 2 -> {
                            int choiceOfBatteriesToUse = adventureChoiceMove.getChoice();
                            int availableDoubleThrusters = currentPlayer.getBoard().getComponentByType(ComponentType.DOUBLE_THRUSTER).size();

                            int thrustersToActivate = min(choiceOfBatteriesToUse, availableDoubleThrusters);

                            float totalThrustPower = currentPlayer.getBoard().checkActivatableThrustPower(thrustersToActivate);

                            checkAndUpdateLowestPower(totalThrustPower);

                            try {
                                currentPlayer.getBoard().removeBatteries(thrustersToActivate);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                            askNextPlayerOrInflictPenalty();
                        }
                        case 3 -> {
                            int choiceOfBatteriesToUse = adventureChoiceMove.getChoice();
                            int availableDoubleCannons = currentPlayer.getBoard().getComponentByType(ComponentType.DOUBLE_CANNON).size();

                            int cannonsToActivate = min(choiceOfBatteriesToUse, availableDoubleCannons);

                            float totalFirePower = currentPlayer.getBoard().checkActivatableFirePower(cannonsToActivate);

                            checkAndUpdateLowestPower(totalFirePower);

                            try {
                                currentPlayer.getBoard().removeBatteries(cannonsToActivate);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                            askNextPlayerOrInflictPenalty();

                        }
                    }

                } else {
                    ac.getLobby().logPlayer(movesColor, "not your turn to make a move");
                }
            }
        /*
        } else {
            ac.getLobby().logPlayer(movesColor, "not time to make a move");
        }
         */
    }



    public static ComponentSide getSideFromInt(int penalty){
        return switch (penalty){
            case 1,5 -> ComponentSide.NORTH;
            case 2,6 -> ComponentSide.EAST;
            case 3,7 -> ComponentSide.SOUTH;
            case 4,8 -> ComponentSide.WEST;
            default -> null;
        };
    }


    public void preparingToNextAttackAgainstPlayer() {

        Player currentPlayer = lowestPowerPlayer;

        if(!ac.isPlayersShipBroken(currentPlayer.getColor())) {
            if (attacksInflicted < damage.length) {

                System.out.println("attacking " + currentPlayer.getColor() + " now");

                int shotToInflict = damage[attacksInflicted];
                ComponentSide shotSide = getSideFromInt(shotToInflict);
                int currentRoll = diceRolls[attacksInflicted];


                boolean shotInRange = (currentRoll > 4 && currentRoll < 10) || ((currentRoll == 4 || currentRoll == 10)
                        && (shotSide == ComponentSide.NORTH || shotSide == ComponentSide.SOUTH));


                if (shotInRange) {


                    Component limitComponent;

                    if (shotSide == ComponentSide.NORTH || shotSide == ComponentSide.SOUTH) {
                        limitComponent = currentPlayer.getBoard().getLimitComponent(currentRoll - 4, shotSide);
                    } else {
                        limitComponent = currentPlayer.getBoard().getLimitComponent(currentRoll - 5, shotSide);
                    }

                    boolean anyComponentInTheWay = (limitComponent != null && limitComponent.getComponentType() != ComponentType.EMPTY_COMPONENT);

                    if (anyComponentInTheWay) {

                        int[] limitComponentCoordinatesArray = currentPlayer.getBoard().getCoordinates(limitComponent);
                        Integer[] limitComponentCoordinates = new Integer[]{limitComponentCoordinatesArray[0], limitComponentCoordinatesArray[1]};

                        int row = limitComponentCoordinates[0] + 5;
                        int col = limitComponentCoordinates[1] + 4;

                        ac.getLobby().logPlayer(currentPlayer.getColor(),
                                limitComponent.getComponentType()
                                        +" ("+ row
                                        +", "+ col
                                        +") is in the way of the incoming shot");


                        boolean smallShot = (shotToInflict <= 4);

                        if (smallShot) {

                            boolean hasShieldCoveringSide = currentPlayer.getBoard().hasShield(shotSide);
                            boolean hasBatteries = (currentPlayer.getBoard().checkTotalBatteries() > 0);

                            if (hasShieldCoveringSide && hasBatteries) {
                                if (ac.getLobby().isColorConnected(currentPlayer.getColor())) {
                                    awaitingShieldChoice = true; // - alzo flag che sono in attesa di risposta
                                    tempCoordinates.put(currentPlayer.getColor(), limitComponentCoordinates);
                                    ac.getLobby().logPlayer(currentPlayer.getColor(), "you can decide to activate a shield and defend from the incoming attack");
                                } else {
                                    ac.getLobby().logPlayer(currentPlayer.getColor(), "you were disconnected and so the choice to not activate a shield to protect you was made for you");
                                    inflictAttack(currentPlayer, limitComponentCoordinates);
                                    preparingToNextAttackAgainstPlayer();
                                }
                            } else {

                                if (hasShieldCoveringSide) {
                                    ac.getLobby().logPlayer(currentPlayer.getColor(), "you have a shield, but you don't have any batteries to activate it, and so you'll be hit");
                                } else {
                                    ac.getLobby().logPlayer(currentPlayer.getColor(), "you don't have a shield to protect yourself, and so you'll be hit");
                                }

                                inflictAttack(currentPlayer, limitComponentCoordinates);
                                preparingToNextAttackAgainstPlayer();
                            }
                        } else {
                            ac.getLobby().logPlayer(currentPlayer.getColor(), "you can't protect from a big attack, and so you'll be hit regardless");
                            inflictAttack(currentPlayer, limitComponentCoordinates);
                            preparingToNextAttackAgainstPlayer();
                        }

                    } else {
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "shot missed, no component was in the way");
                        dontInflictAttack(currentPlayer);
                        preparingToNextAttackAgainstPlayer();
                    }


                } else {
                    ac.getLobby().logPlayer(currentPlayer.getColor(), "shot missed by a lot");
                    dontInflictAttack(currentPlayer);
                    preparingToNextAttackAgainstPlayer();
                }


            } else { // DONE INFLICTING ATTACKS TO PLAYER
                ac.getLobby().logPlayer(currentPlayer.getColor(), "you are done being inflicted attacks");
                transitionToNextConditionOrEndCard();
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
        attacksInflicted++;
        ac.getLobby().logPlayer(currentColor, attacksInflicted + " out of " + damage.length + " attacks inflicted");
    }



    public String toString() {
        return "A warzone card";
    }
}
