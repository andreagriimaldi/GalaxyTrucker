package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.commands.userRequest.moves.FlightChoiceMove;
import it.polimi.ingsw.commands.userRequest.moves.Move;
import it.polimi.ingsw.controller.GameLobby;
import it.polimi.ingsw.enums.AdventureLevel;
import it.polimi.ingsw.enums.AdventureType;
import it.polimi.ingsw.enums.MoveType;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.components.CubeToken;

public class StationCard extends AdventureCard {
    private int requiredCrew;
    private CubeToken[] reward;

    public StationCard(AdventureType type, AdventureLevel level, String ID, int days, int requiredCrew, CubeToken[] reward) {
        super(type, level, ID, days);
        this.skippable = true;
        this.reward = reward;
        this.requiredCrew = requiredCrew;
    }

    public int getRequiredCrew() {
        return requiredCrew;
    }

    public String getReward() {
        StringBuilder string = new StringBuilder();
        for (CubeToken cubeToken : reward) {
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


    public String toString(){
        return "A station card";
    }




    @Override
    public void handle() {
        ac.getLobby().logAllPlayers("called method to handle station card");

        if (playersInOrder == null) {
            playersInOrder = ac.getGameSession().getFlightBoard().returnPlayersInOrder();
        }

        routine();
    }


    public void routine() {
        if (currentPlayerIndex < playersInOrder.size()) { // posso ancora passare a un prossimo player e l'effetto della carta non è ancora stato esaurito
            ac.getLobby().logAllPlayers("asking active player");

            Player currentPlayer = playersInOrder.get(currentPlayerIndex);
            GameLobby lobby = ac.getLobby();

            if(currentPlayer.getBoard().checkTotalCrew() >= this.requiredCrew) { // DA CONTROLLARE QUESTO IF
                if(lobby.isColorConnected(currentPlayer.getColor())) {
                    lobby.logPlayer(currentPlayer.getColor(), "choose if you want to take advantage of this card [1] or skip [0]");

                    awaitingPlayerChoice = true;

                    // bisognerà attivare handle(move) mediante l'invio di mosse dall'adventure controller
                } else {
                    lobby.logPlayer(currentPlayer.getColor(), "you were disconnected, and so the default choice of skipping was made for you");
                    nextPlayer();
                    handle();
                }
            } else {
                lobby.logPlayer(currentPlayer.getColor(), "you don't have enough crew members to take advantage of this card");
                nextPlayer();
                handle();
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

        if(movesPlayer.equals(currentPlayer.getColor())) {

            FlightChoiceMove adventureChoiceMove = (FlightChoiceMove) move;
            int choice = adventureChoiceMove.getChoice();

            if (choice == 1) { // decided to land

                try {
                    currentPlayer.getBoard().addCubes(reward);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ac.getGameSession().getFlightBoard().movePlayerByN(currentPlayer, days);

                drawNextCard();
            } else if (choice == 0) { // decided to skip
                awaitingPlayerChoice = false;
                nextPlayer();
                routine();
            } else {
                ac.getLobby().logPlayer(currentPlayer.getColor(), "incorrect move format. either choose 1 [take advantage] or 0 [skip] as parameters");
            }

        }

    }


    public void nextPlayer() {
        this.currentPlayerIndex++;
    }

}
