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

import java.util.LinkedList;
import java.util.List;

public class PlanetCard extends AdventureCard {
    private CubeToken[][] planets;
    private boolean[] planetsTaken;
    private List<PlayerColor> landedPlayers;

    public PlanetCard(AdventureType type, AdventureLevel level, String ID, int days, CubeToken[][] planets) {
        super(type, level, ID, days);
        this.planets = planets;
        this.skippable = true;
        this.planetsTaken = new boolean[planets.length];
        this.landedPlayers = new LinkedList<>();
    }

    /** getter methods */

    public String getLandablePlanets() {
        StringBuilder string = new StringBuilder();
        for (boolean b : planetsTaken) {
            if (b) {
                string.append("no");
            } else {
                string.append("yes");
            }
            string.append(" ");
        }
        return string.toString();
    }


    public String getPlanetReward(int i) {
        StringBuilder string = new StringBuilder();
        for (CubeToken cubeToken : planets[i]) {
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

    public int getNumberPlanets() {
        return planets.length;
    }

    /**
     * if the planet is landable, give to the player the reward assigned to it.
     * afterward the planet is assigned as non-landable, and if all the possible planets have been
     * looted effectEnded is assigned true
     */

    public CubeToken[] giveReward(int planetIndex) {
        planetsTaken[planetIndex] = true;
        checkEffectEnded();
        return planets[planetIndex];
    }

    public void checkEffectEnded(){
        for(boolean taken : planetsTaken){
            if(!taken){
                return;
            }
        }

        setEffectAsEnded();
    }

    public void setEffectAsEnded() {
        effectEnded = true;
        awaitingPlayerChoice = false;
        drawNextCard();
    }

    public void setPlanetAsTaken(int i) {
        planetsTaken[i] = true;
    }


    @Override
    public void handle() {

        if (playersInOrder == null) { // se la carta non è ancora iniziata
            playersInOrder = ac.getGameSession().getFlightBoard().returnPlayersInOrder();
            if(playersInOrder == null) {
                System.err.println("players in order is null");
            }
            String landablePlanets = getLandablePlanets();
            ac.getLobby().logAllPlayers(landablePlanets);
        }

        routine();
    }


    public void routine() {
         if (currentPlayerIndex < playersInOrder.size() && !effectEnded) { // posso ancora passare a un prossimo player e l'effetto della carta non è ancora stato esaurito

            Player currentPlayer = playersInOrder.get(currentPlayerIndex);
            GameLobby lobby = ac.getLobby();

            lobby.logAllPlayersExceptOne(currentPlayer.getColor(), "asking "+currentPlayer.getColor()+" player");

            if(lobby.isColorConnected(currentPlayer.getColor())) { // [] idealmente però si riesce anche a gestire la disconnessione asincrona dopo questo if. come? idealmente mettendo a false awaitingPlayerChoice, e chiamando handle() in autonomia

                String log = "Your turn: choose if you want to skip [0] or land on planet ";

                for(int i = 0; i < planetsTaken.length; i++) {
                    if(!planetsTaken[i]) {
                        int currentPlanet = i+1;
                        log += "[" + currentPlanet + "] ";
                    }
                }

                lobby.logPlayer(currentPlayer.getColor(), log);

                awaitingPlayerChoice = true;
                // [] bisognerà attivare handle(move) mediante l'invio di mosse dall'adventure controller
            } else {
                lobby.logPlayer(currentPlayer.getColor(), "you were disconnected, and so the default choice of skipping was made for you");
                lobby.logAllPlayersExceptOne(currentPlayer.getColor(), currentPlayer.getColor()+" skipped, as they were disconnected from the game");

                nextPlayer();
                routine();
            }

        } else { // in case all players made their move => end card

            List<Player> playerInReverseOrder = ac.getGameSession().getFlightBoard().returnPlayersInReverseOrder();
            for(Player player : playerInReverseOrder) {
                if(landedPlayers.contains(player.getColor())) {
                    ac.getGameSession().getFlightBoard().movePlayerByN(player, days);
                }

            }

            setEffectAsEnded();

        }
    }





    // [] se i pianeti non sono finiti, e il player sceglie di atterrare su un pianeta già scelto, non prendere la sua scelta come buona (aka non avanzare ancora al prossimo player)
    @Override
    public void handle(Move move) {
        PlayerColor movesPlayer = move.getColor();
        MoveType moveType = move.getType();

        Player currentPlayer = playersInOrder.get(currentPlayerIndex);

        // ... logica in cui spacchetto la mossa e controllo se ha scelto di fermarsi
        if(movesPlayer.equals(currentPlayer.getColor())) {
            if(moveType == MoveType.FLIGHT_CHOICE) {
                FlightChoiceMove adventureChoiceMove = (FlightChoiceMove) move;

                int choice = adventureChoiceMove.getChoice();
                int chosenPlanet = choice - 1; // translate by one

                if(choice == 0) {
                    nextPlayer();
                }
                else if (chosenPlanet < planetsTaken.length) { // decided to land

                    if(!planetsTaken[chosenPlanet]) {
                        try {
                            currentPlayer.getBoard().addCubes(giveReward(chosenPlanet)); // [] BISOGNA GESTIRE IL DARE I CUBI AI PLAYER
                            landedPlayers.add(currentPlayer.getColor());
                            setPlanetAsTaken(chosenPlanet);
                            CubeToken[] reward = giveReward(chosenPlanet);
                            currentPlayer.getBoard().addCubes(reward);
                            nextPlayer();
                            ac.getLobby().logAllPlayersExceptOne(currentPlayer.getColor(), currentPlayer.getColor() + " player decided to land on planet " + (chosenPlanet+1));
                            ac.getLobby().logPlayer(currentPlayer.getColor(), "you decided to land on planet " + (chosenPlanet+1));
                            awaitingPlayerChoice = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        checkEffectEnded();


                    } else {
                        ac.getLobby().logPlayer(currentPlayer.getColor(), "choose a valid planet");
                    }

                } else {
                    ac.getLobby().logPlayer(currentPlayer.getColor(), "incorrect move format");
                }
            }
        }

        routine();
    }

    public void nextPlayer() {
        this.currentPlayerIndex++;
    }



    public String toString() {
        return "A planet card";
    }
}
