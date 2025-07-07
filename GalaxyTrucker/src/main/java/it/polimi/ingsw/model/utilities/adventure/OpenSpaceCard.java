package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.commands.userRequest.moves.FlightChoiceMove;
import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.enums.AdventureLevel;
import it.polimi.ingsw.enums.AdventureType;
import it.polimi.ingsw.model.game.FlightBoardFacade;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.enums.ComponentType;

import static java.lang.Math.min;

public class OpenSpaceCard extends AdventureCard {


    private int currentPlayerIndex;


    public OpenSpaceCard(AdventureType type, AdventureLevel level, String ID, int days) {
        super(type, level, ID, days);
        this.skippable = false;

        currentPlayerIndex = 0;
    }


    public void handle() {
        playersInOrder = ac.getGameSession().getFlightBoard().returnPlayersInOrder();
        routine();
    }

    public void routine() {

        if(currentPlayerIndex < playersInOrder.size()) {
            Player currentPlayer = playersInOrder.get(currentPlayerIndex);

            FlightBoardFacade flightBoard = ac.getGameSession().getFlightBoard();

            boolean hasSingleThrusters = currentPlayer.getBoard().hasSingleThrusters();
            boolean hasDoubleThrusters = currentPlayer.getBoard().hasDoubleThrusters();
            boolean hasBatteries = currentPlayer.getBoard().hasBatteries();

            if(!hasSingleThrusters) {

                if(!hasDoubleThrusters) {
                    try {
                        flightBoard.removePlayerFromBoard(currentPlayer);
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "you're being removed from the flightboard since you have no thrusters to activate");
                        ac.getLobby().logAllPlayersExceptOne(currentPlayer.getColor(), currentPlayer.getColor()+" player couldn't make it");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    nextPlayer();
                    routine();
                } else if (!hasBatteries) {
                    try {
                        flightBoard.removePlayerFromBoard(currentPlayer);
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "you have no single thrusters, and although you have double thrusters, you currently possess no batteries to activate them, so you are being removed from the flightboard");
                        ac.getLobby().logAllPlayersExceptOne(currentPlayer.getColor(), currentPlayer.getColor()+" player couldn't make it");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    nextPlayer();
                    routine();
                } else {

                    if(ac.getLobby().isColorConnected(currentPlayer.getColor())) {
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "choose number of double thrusters to activate (total batteries: " + currentPlayer.getBoard().checkTotalBatteries() + " (double thrusters available to use: " + currentPlayer.getBoard().checkAvailableDoubleThrusters() + "), thrustpower: " + currentPlayer.getBoard().checkDefaultThrustPower() + "-" + currentPlayer.getBoard().checkMaxThrustPower() + ")");
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "if you don't activate any double thruster, you'll be removed from the flightboard");
                    } else {
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "the choice to not activate any double thrusters was made for you, as you were disconnected. since your default thrust power is zero, you will be removed from the flightboard");

                        try {
                            ac.getGameSession().getFlightBoard().removePlayerFromBoard(currentPlayer);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                        nextPlayer();
                        routine();
                    }



                }

            } else {

                if(!hasDoubleThrusters) {
                    int totalThrustPower = (int) currentPlayer.getBoard().checkDefaultThrustPower();
                    flightBoard.movePlayerByN(currentPlayer, totalThrustPower);
                    ac.getLobby().logPlayer(currentPlayer.getColor(), "you are being moved forward by " + totalThrustPower);
                    ac.getLobby().logAllPlayersExceptOne(currentPlayer.getColor(), currentPlayer.getColor() + " player was moved forward by " + totalThrustPower);

                    nextPlayer();
                    routine();
                } else if (!hasBatteries) {
                    int totalThrustPower = (int) currentPlayer.getBoard().checkDefaultThrustPower();
                    flightBoard.movePlayerByN(currentPlayer, totalThrustPower);
                    ac.getLobby().logPlayer(currentPlayer.getColor(), "you were moved forward by " + totalThrustPower);
                    ac.getLobby().logAllPlayersExceptOne(currentPlayer.getColor(), currentPlayer.getColor() + " player was forward by " + totalThrustPower);

                    nextPlayer();
                    routine();
                } else {
                    if(ac.getLobby().isColorConnected(currentPlayer.getColor())) {
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "choose number of double thrusters to activate (total batteries: " + currentPlayer.getBoard().checkTotalBatteries() + " (double thrusters available to use: " + currentPlayer.getBoard().checkAvailableDoubleThrusters() + "), thrustpower: " + currentPlayer.getBoard().checkDefaultThrustPower() + "-" + currentPlayer.getBoard().checkMaxThrustPower() + ")");
                        ac.getLobby().logAllPlayersExceptOne(currentPlayer.getColor(), "asking "+currentPlayer.getColor()+" player");
                        awaitingPlayerChoice = true;
                    } else {
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "the choice to not activate additional double thrusters was made for you, as you were disconnected");

                        int totalThrustPower = (int) currentPlayer.getBoard().checkDefaultThrustPower();
                        flightBoard.movePlayerByN(currentPlayer, totalThrustPower);
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "you were moved forward by " + totalThrustPower);
                        ac.getLobby().logAllPlayersExceptOne(currentPlayer.getColor(), "disconnected " + currentPlayer.getColor() + " player was moved forward by " + totalThrustPower);


                        nextPlayer();
                        routine();
                    }
                }
            }
        } else {
            drawNextCard();
        }
    }

    public void handle(Move move) {

        FlightChoiceMove adventureChoiceMove = (FlightChoiceMove) move;
        PlayerColor movesColor = adventureChoiceMove.getColor();

        Player currentPlayer = playersInOrder.get(currentPlayerIndex);

        if(movesColor == currentPlayer.getColor()) {

            int choiceOfBatteriesToUse = adventureChoiceMove.getChoice();
            int availableDoubleThrusters = currentPlayer.getBoard().getComponentByType(ComponentType.DOUBLE_THRUSTER).size();
            int thrustersToActivate = min(choiceOfBatteriesToUse, availableDoubleThrusters);
            int totalThrustPower = (int) currentPlayer.getBoard().checkActivatableThrustPower(thrustersToActivate);

            FlightBoardFacade flightBoard = ac.getGameSession().getFlightBoard();


            try {
                if(totalThrustPower == 0) {
                    flightBoard.removePlayerFromBoard(currentPlayer);
                    ac.getLobby().logPlayer(currentPlayer.getColor(), "your choice got you removed from the flightboard, as your total thrust power was zero");
                    ac.getLobby().logAllPlayersExceptOne(currentPlayer.getColor(), currentPlayer.getColor() + " player's choice got them removed from the flightboard, as their total thrust power was zero");
                } else {
                    ac.getLobby().logPlayer(currentPlayer.getColor(), "your choice got you moving forward by " + totalThrustPower);
                    ac.getLobby().logAllPlayersExceptOne(currentPlayer.getColor(), currentPlayer.getColor() + " player's choice got them moving forward by " + totalThrustPower);
                    flightBoard.movePlayerByN(currentPlayer, totalThrustPower);
                    currentPlayer.getBoard().removeBatteries(thrustersToActivate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            awaitingPlayerChoice = false;
            nextPlayer();
            routine();
        }
    }

    public void nextPlayer() {
        currentPlayerIndex++;
    }

    public String toString(){
        return "An open space card";
    }
}
