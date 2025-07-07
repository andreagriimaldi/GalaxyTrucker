package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.commands.userRequest.moves.FlightChoiceMove;
import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.playerset.RocketshipBoard;
import it.polimi.ingsw.model.utilities.components.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MeteoriteCard extends AdventureCard {
    // 1 to 4 = small north, east, south, west; 5 to 8 = big north, east, south, west
    private int[] meteorites;
    private int[] diceRolls;
    private Map<PlayerColor, Integer> attacksInflicted;
    private Map<PlayerColor, Boolean> awaitingShieldChoice;
    private Map<PlayerColor, Boolean> awaitingCannonChoice;
    private Map<PlayerColor, Integer[]> tempCoordinates;




    public MeteoriteCard(AdventureType type, AdventureLevel level, String ID, int days, int[] meteorites) {
        super(type, level, ID, days);
        this.skippable = false;
        this.meteorites = meteorites;
        this.diceRolls = new int[meteorites.length];

        this.attacksInflicted = new ConcurrentHashMap<>();
        this.awaitingShieldChoice = new ConcurrentHashMap<>();
        this.awaitingCannonChoice = new ConcurrentHashMap<>();
        this.tempCoordinates = new ConcurrentHashMap<>();

        this.awaitingPlayerChoice = true;
    }


    public List<String> getMeteorites() {
        List<String> meteoritesStrings = new LinkedList<>();
        for (int j : meteorites) {
            meteoritesStrings.add(getStringFromInt(j));
        }
        return meteoritesStrings;
    }


    /**
     * returns if cannons are present in the specified row or column
     * @param currentPlayer the player whose board we are checking
     * @param roll the row/column that is being attacked
     * @param side from which side the meteor is arriving
     * @param cannonType the type of cannon we're checking for
     * @param meteor from which direction the meteor is arriving: for front facing meteor we only check the direct column,
     *               for side and back facing meteor we check the direct and adjacent columns/rows
     * @return true if there are cannons that can stop the meteor, false if there aren't
     */
    public boolean checkCannonsForMeteorCoverage(Player currentPlayer, int roll, ComponentSide side, ComponentType cannonType, int meteor) {

        boolean result = false;

        if (isValidLineIndex(roll, side)) {
            result = currentPlayer.getBoard().getRowOrColumn(roll, side).contains(cannonType);
        }

        if (meteor > 5) {
            if (isValidLineIndex(roll-1, side)) {
                result = result || currentPlayer.getBoard().getRowOrColumn(roll-1, side).contains(cannonType);
            }
            if (isValidLineIndex(roll+1, side)) {
                result = result || currentPlayer.getBoard().getRowOrColumn(roll+1, side).contains(cannonType);
            }
        }
        return result;
    }


    private boolean isValidLineIndex(int index, ComponentSide side) {
        if (side == ComponentSide.NORTH || side == ComponentSide.SOUTH) {
            return (index >= 0 && index < 7);
        } else {
            return (index >= 0 && index < 5);
        }
    }


    public void rollDice() {
        Random random = new Random();
        for(int i = 0; i < diceRolls.length; i++) {
            diceRolls[i] = (random.nextInt(6) + 1) + (random.nextInt(6) + 1);
        }
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


    public String getStringFromInt(int penalty){
        return switch (penalty){
            case 1 -> "NORTH | SMALL";
            case 2 -> "EAST  | SMALL";
            case 3 -> "SOUTH | SMALL";
            case 4 -> "WEST  | SMALL";
            case 5 -> "NORTH | BIG";
            case 6 -> "EAST  | BIG";
            case 7 -> "SOUTH | BIG";
            case 8 -> "WEST  | BIG";
            default -> null;
        };
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

    @Override
    public void handle() {

        initializePlayersInOrder();
        for(Player player : playersInOrder) {
            attacksInflicted.put(player.getColor(), 0);
            awaitingShieldChoice.put(player.getColor(), false);
            awaitingCannonChoice.put(player.getColor(), false);
        }

        rollDice();
        informPlayersOnAttacks();

        for(PlayerColor playerColor : attacksInflicted.keySet()) {
            try {
                preparingToNextAttackAgainstPlayer(ac.getLobby().getPlayerFromColor(playerColor)); // [] shouldn't take place here, but after a player won, or all players have been beaten
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initializePlayersInOrder() {
        if(playersInOrder == null) {
            playersInOrder = ac.getGameSession().getFlightBoard().returnPlayersInOrder();
        }
    }



    public void informPlayersOnAttacks() {
        String shotsInformation = "";
        for(int i = 0; i < meteorites.length; i++) {
            String attackSize = "";

            if(meteorites[i] <= 4) {
                attackSize = "small";
            } else {
                attackSize = "big";
            }

            shotsInformation += "shot #" + (i+1) + " : " + attackSize + " attack (" + getUnicodeFromInt(meteorites[i])+ " line "+ diceRolls[i] +") " + System.lineSeparator();
        }

        ac.getLobby().logAllPlayers(shotsInformation);
    }





    public void preparingToNextAttackAgainstPlayer(Player currentPlayer) {

        int attacksInflictedForCurrentPlayer = attacksInflicted.get(currentPlayer.getColor());

        ac.getLobby().logPlayer(currentPlayer.getColor(), ""); // new line

        if(!ac.isPlayersShipBroken(currentPlayer.getColor())) {
            if(attacksInflictedForCurrentPlayer < meteorites.length) {

                int meteorite = meteorites[attacksInflictedForCurrentPlayer];
                ComponentSide meteoriteSide = getSideFromInt(meteorite);
                int currentRoll = diceRolls[attacksInflictedForCurrentPlayer];

                boolean shotInRange = (currentRoll > 4 && currentRoll < 10) || ((currentRoll == 4 || currentRoll == 10) && (meteoriteSide == ComponentSide.NORTH || meteoriteSide == ComponentSide.SOUTH));

                if(shotInRange) {
                    Component limitComponent;

                    if(meteoriteSide == ComponentSide.NORTH || meteoriteSide == ComponentSide.SOUTH) {
                        limitComponent = currentPlayer.getBoard().getLimitComponent(currentRoll-4, meteoriteSide);
                    }
                    else{
                        limitComponent = currentPlayer.getBoard().getLimitComponent(currentRoll-5, meteoriteSide);
                    }

                    boolean anyComponentInTheWay = (limitComponent != null && limitComponent.getComponentType() != ComponentType.EMPTY_COMPONENT);


                    // if there are components in that row/column
                    if (anyComponentInTheWay) {



                        int[] limitComponentCoordinatesArray = currentPlayer.getBoard().getCoordinates(limitComponent);


                        Integer[] limitComponentCoordinates = new Integer[]{limitComponentCoordinatesArray[0], limitComponentCoordinatesArray[1]};

                        int row = limitComponentCoordinates[0] + 5;
                        int col = limitComponentCoordinates[1] + 4;

                        ac.getLobby().logPlayer(currentPlayer.getColor(),
                                limitComponent.getComponentType()
                                        +" ("+ row
                                        +", "+ col
                                        +") is in the way of the meteorite");


                        boolean smallMeteorite = (meteorite <= 4);

                        if(smallMeteorite) {

                            boolean hasShieldCoveringSide = currentPlayer.getBoard().hasShield(meteoriteSide);
                            boolean hasBatteries = (currentPlayer.getBoard().checkTotalBatteries() > 0 );
                            boolean limitComponentHasSmoothSurface = (limitComponent.getConnectorByDirection(meteoriteSide) == ConnectorType.SMOOTH_SURFACE);

                            if(limitComponentHasSmoothSurface) {

                                ac.getLobby().logPlayer(currentPlayer.getColor(), "the small meteorite bounced off");
                                dontInflictAttack(currentPlayer);
                                preparingToNextAttackAgainstPlayer(currentPlayer);

                            } else if (hasShieldCoveringSide && hasBatteries) {
                                if(ac.getLobby().isColorConnected(currentPlayer.getColor())) {
                                    awaitingShieldChoice.put(currentPlayer.getColor(), true);
                                    tempCoordinates.put(currentPlayer.getColor(), limitComponentCoordinates);
                                    ac.getLobby().logPlayer(currentPlayer.getColor(), "do you want to activate a shield to protect you from this attack? (available batteries : " + currentPlayer.getBoard().checkTotalBatteries() + ")");
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

                        } else { // LARGE METEORITE
                            boolean hasSingleCannon = false;
                            boolean hasDoubleCannon = false;

                            // check if the incoming meteorite is covered by front facing single or double cannons
                            if(meteorite == 5) {
                                hasSingleCannon = checkCannonsForMeteorCoverage(currentPlayer, currentRoll-4, meteoriteSide, ComponentType.SINGLE_CANNON, meteorite);
                                hasDoubleCannon = checkCannonsForMeteorCoverage(currentPlayer, currentRoll-4, meteoriteSide, ComponentType.DOUBLE_CANNON, meteorite);
                            } else { // check if the incoming meteorite is covered by front facing single or double cannons
                                if(meteoriteSide == ComponentSide.SOUTH){
                                    hasSingleCannon = checkCannonsForMeteorCoverage(currentPlayer, currentRoll-4, meteoriteSide, ComponentType.SINGLE_CANNON, meteorite);
                                    hasDoubleCannon = checkCannonsForMeteorCoverage(currentPlayer, currentRoll-4, meteoriteSide, ComponentType.DOUBLE_CANNON, meteorite);
                                } else { // FROM EAST OR WEST
                                    hasSingleCannon = checkCannonsForMeteorCoverage(currentPlayer, currentRoll-5, meteoriteSide, ComponentType.SINGLE_CANNON, meteorite);
                                    hasDoubleCannon = checkCannonsForMeteorCoverage(currentPlayer, currentRoll-5, meteoriteSide, ComponentType.DOUBLE_CANNON, meteorite);
                                }
                            }


                            if(hasSingleCannon){ // if the players has a single cannon that can handle the meteor, the attack is automatically prevented

                                ac.getLobby().logPlayer(currentPlayer.getColor(), "you have a single cannon to protect you from this attack");

                                dontInflictAttack(currentPlayer);
                                preparingToNextAttackAgainstPlayer(currentPlayer);
                            } else if (!hasDoubleCannon){ // if they don't have single nor double cannons the limit component gets eliminated

                                ac.getLobby().logPlayer(currentPlayer.getColor(), "you don't have single or double cannons to protect you from this attack");

                                inflictAttack(currentPlayer, limitComponentCoordinates);
                                preparingToNextAttackAgainstPlayer(currentPlayer);
                            } else { // if they do have double cannons, the player is asked whether he wants to activate them

                                boolean hasBatteries = (currentPlayer.getBoard().checkTotalBatteries() > 0 );

                                if (hasBatteries) {
                                    if(ac.getLobby().isColorConnected(currentPlayer.getColor())) {
                                        awaitingCannonChoice.put(currentPlayer.getColor(), true);
                                        tempCoordinates.put(currentPlayer.getColor(), limitComponentCoordinates);
                                        ac.getLobby().logPlayer(currentPlayer.getColor(), "you have double cannons and batteries to protect you from this big meteorite. do you want to activate them?");
                                    }
                                    else {
                                        ac.getLobby().logAllPlayers(currentPlayer.getColor() + " player is disconnected and so the automatic move to not activate any double cannons was made for them");
                                        inflictAttack(currentPlayer, limitComponentCoordinates);
                                        preparingToNextAttackAgainstPlayer(currentPlayer);
                                    }
                                } else {
                                    ac.getLobby().logPlayer(currentPlayer.getColor(), "you have double cannons but not enough batteries to protect you from this attack");

                                    inflictAttack(currentPlayer, limitComponentCoordinates);
                                    preparingToNextAttackAgainstPlayer(currentPlayer);
                                }
                            }
                        }
                    } else { // shot missed
                        // loggo che il meteorite ha mancato
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "meteorite missed, as no component was in its way");
                        dontInflictAttack(currentPlayer);
                        preparingToNextAttackAgainstPlayer(currentPlayer);
                    }
                } else {
                    ac.getLobby().logPlayer(currentPlayer.getColor(), "meteorite missed by a lot");
                    dontInflictAttack(currentPlayer);
                    preparingToNextAttackAgainstPlayer(currentPlayer);
                }

            } else { // DONE INFLICTING ATTACKS TO PLAYER
                ac.getLobby().logPlayer(currentPlayer.getColor(), "you are done being inflicted attacks");
                ifEveryPlayerFinishedDrawNewCard(); // controllo che tutti i player abbiano finito di subire attacchi
            }
        }

    }


    public void ifEveryPlayerFinishedDrawNewCard() {

        boolean everyPlayerFinished = attacksInflicted.values()
                .stream()
                .noneMatch(attacks -> attacks < meteorites.length);

        if(everyPlayerFinished) {
            drawNextCard();
        }
    }

    public void inflictAttack(Player currentPlayer, Integer[] attackCoordinates) {

        currentPlayer.getBoard().removeComponent(
                attackCoordinates[0],
                attackCoordinates[1]);

        increaseNumberOfInflictedAttacks(currentPlayer.getColor());


        // ### CHECKING INTEGRITY FOR FIXING ROCKETSHIP ### //

        // [] check integrity della nave
        // [] faccio come nell'adventure controller e uso una mappa per tenere traccia dei player
        // [] una volta che arriva la scelta (nel caso sia corretta), tolgo il player dalla mappa e
        // 1. se ha finito con gli attacchi, lo considero pronto
        // 2. altrimenti avvio il prossimo attacco

        ac.checkBrokenShip(currentPlayer);

    }




    public void dontInflictAttack(Player currentPlayer) {
        increaseNumberOfInflictedAttacks(currentPlayer.getColor());
    }

    public void increaseNumberOfInflictedAttacks(PlayerColor currentColor) {
        attacksInflicted.put(currentColor, attacksInflicted.get(currentColor) + 1);
        ac.getLobby().logPlayer(currentColor, attacksInflicted.get(currentColor) + " out of " + meteorites.length + " attacks inflicted");
    }


    public void handle(Move move) {
        FlightChoiceMove adventureChoiceMove = (FlightChoiceMove) move;
        PlayerColor movesColor = adventureChoiceMove.getColor();

        int choice = adventureChoiceMove.getChoice();
        
        System.out.println("received move from " + movesColor + " player");

        try {

            Player movesPlayer = ac.getLobby().getPlayerFromColor(movesColor);

            if(ac.isPlayersShipBroken(movesColor)) {
                boolean isInPartsRange = choice < movesPlayer.getBoard().getSizeOfSetOfConnectedComponents();

                if(isInPartsRange) {
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

            } else {
                if (attacksInflicted.get(movesColor) < meteorites.length) {
                    if (awaitingShieldChoice.get(movesColor)) {
                        if (choice == 1) { // player decided to prevent attack
                            dontInflictAttack(movesPlayer);

                            try {
                                movesPlayer.getBoard().removeBatteries(1);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                            ac.getLobby().logPlayer(movesColor, "you made a decision to activate the available shield, and so you prevented the attack. a battery has been used up in the process");
                            awaitingShieldChoice.put(movesColor, false);
                            preparingToNextAttackAgainstPlayer(movesPlayer);

                        } else if (choice == 0) { // player decided not to prevent attack

                            Integer[] attackCoordinates = tempCoordinates.remove(movesPlayer.getColor());

                            ac.getLobby().logPlayer(movesColor, "you made a decision not to activate the available shield, and so you will be inflicted an attack");
                            inflictAttack(movesPlayer, attackCoordinates);
                            awaitingShieldChoice.put(movesColor, false);
                            preparingToNextAttackAgainstPlayer(movesPlayer);
                        } else {
                            ac.getLobby().logPlayer(movesColor, "you either need to choose 0 or 1");
                        }

                    } else if (awaitingCannonChoice.get(movesColor)) {
                        if (choice == 1) { // player decided to prevent attack
                            dontInflictAttack(movesPlayer);

                            try {
                                movesPlayer.getBoard().removeBatteries(1);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                            ac.getLobby().logPlayer(movesColor, "you made a decision to activate the available cannon, and so you prevented the attack. a battery has been used up in the process");
                            awaitingCannonChoice.put(movesColor, false);
                            preparingToNextAttackAgainstPlayer(movesPlayer);

                        } else if (choice == 0) { // player decided not to prevent attack

                            Integer[] attackCoordinates = tempCoordinates.remove(movesPlayer.getColor());

                            ac.getLobby().logPlayer(movesColor, "you made a decision not to activate the available cannon, and so you will be inflicted an attack");
                            inflictAttack(movesPlayer, attackCoordinates);
                            awaitingCannonChoice.put(movesColor, false);
                            preparingToNextAttackAgainstPlayer(movesPlayer);
                        } else {
                            ac.getLobby().logPlayer(movesColor, "you either need to choose 0 or 1");
                        }
                    } else {
                        ac.getLobby().logPlayer(movesColor, "the game is not waiting for you to make a choice");
                    }
                } else { // vuol dire che il player ha gia subito tutti gli attacchi
                    ac.getLobby().logPlayer(movesColor, "you finished attacks");
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public String toString(){
        return "A meteorite card";
    }

}
