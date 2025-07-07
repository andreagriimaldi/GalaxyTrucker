package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.commands.userRequest.moves.FlightChoiceMove;
import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.controller.AdventureController;
import it.polimi.ingsw.controller.GameLobby;
import it.polimi.ingsw.enums.AdventureLevel;
import it.polimi.ingsw.enums.AdventureType;
import it.polimi.ingsw.enums.MoveType;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;

public class ShipCard extends AdventureCard {
    private final int crewLost;
    private final int reward;


    public ShipCard(AdventureType type, AdventureLevel level, String ID, int days, int crewLost, int reward) {
        super(type, level, ID, days);
        this.skippable = true;
        this.crewLost = crewLost; // [] TEMPORANEAMENTE = 0
        this.reward = reward;

        this.awaitingPlayerChoice = false;
    }



    public void attachAdventureController(AdventureController ac) {
        this.ac = ac;
    }



    public int getCrewLost() {
        return crewLost;
    }

    public int getReward() {
        return reward;
    }


    public String toString(){
        return "A ship card";
    }


    @Override
    public void handle() {
        ac.getLobby().logAllPlayers("called method to handle ship card");
        if (playersInOrder == null) { // se la carta non è ancora iniziata
            ac.getLobby().logAllPlayers("getting players in order");
            playersInOrder = ac.getGameSession().getFlightBoard().returnPlayersInOrder();
            if(playersInOrder == null) {
                System.err.println("players in order is null");
            }
            routine();
        }
    }


    public void routine() {
        if (currentPlayerIndex < playersInOrder.size()) { // posso ancora passare a un prossimo player e l'effetto della carta non è ancora stato esaurito
            ac.getLobby().logAllPlayers("asking active player");
            Player activePlayer = playersInOrder.get(currentPlayerIndex);
            GameLobby lobby = ac.getLobby();

            if(activePlayer.getBoard().checkTotalCrew() >= this.crewLost) { // DA CONTROLLARE QUESTO IF
                if(lobby.isColorConnected(activePlayer.getColor())) {
                    awaitingPlayerChoice = true;
                    lobby.logPlayer(activePlayer.getColor(), "choose if you want to take advantage of this card [1] or skip [0]");
                } else {
                    lobby.logPlayer(activePlayer.getColor(), "you were disconnected, and so the default choice of skipping was made for you");

                    nextPlayer();
                    routine();
                }
            } else {
                lobby.logPlayer(activePlayer.getColor(), "you don't have enough crew members to take advantage of this card");

                nextPlayer();
                routine();
            }
        } else {
            drawNextCard();
        }
    }



    @Override
    public void handle(Move move) {
        PlayerColor movesPlayer = move.getColor();
        MoveType moveType = move.getType();

        Player currentPlayer = playersInOrder.get(currentPlayerIndex);

        if(awaitingPlayerChoice) {
            if(movesPlayer.equals(currentPlayer.getColor())) {
                FlightChoiceMove adventureChoiceMove = (FlightChoiceMove) move;
                int choice = adventureChoiceMove.getChoice();


                 if (choice == 0) { // decided to skip
                     awaitingPlayerChoice = false;

                     nextPlayer();
                     routine();

                 } else if (choice == 1) { // decided to land
                    awaitingPlayerChoice = false;

                    currentPlayer.addCredits(reward);
                    currentPlayer.getBoard().removeCrewmates(crewLost);
                    ac.getGameSession().getFlightBoard().movePlayerByN(activePlayer, days);

                    drawNextCard();

                } else {
                    ac.getLobby().logPlayer(activePlayer.getColor(), "incorrect move format. either choose 1 [take advantage] or 0 [skip] as parameters");
                }
            }
        } else {
            ac.getLobby().logPlayer(movesPlayer, "not time to make a move");
        }
    }

    public void nextPlayer() {
        this.currentPlayerIndex++;
    }

}
