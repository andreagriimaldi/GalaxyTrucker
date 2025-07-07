package it.polimi.ingsw.model.playerset;

/**
 * Player's class is a bridge between the User class and the game itself
 */

public class Player {
    private final PlayerColor color;
    private final String username;
    private RocketshipBoard board;
    private int credits;
    private int laps;
    private int componentsPenalty;
    private boolean hasRocketship;


    /**
     * Constructor for Player's class
     * Color is assigned when the game actually starts
     * */
    public Player(PlayerColor color, String username, RocketshipBoard board) {
        this.color = color;
        this.username = username;
        this.board = board;
        credits = 0;
        laps = 0;
        componentsPenalty = 0;
        hasRocketship = true;


    }




    /**
     * getUsername() return player's username
     * @return a String representing the username
     */
    public String getUsername(){
        return username;
    }

    /**
     * getColor() returns player's color
     * @return a PlayerColor representing the color
     */
    public PlayerColor getColor(){
        return color;
    }

    /**
     * getCredits() returns player's credits
     * @return an int representing the credits
     */
    public int getCredits(){
        return credits;
    }

    /**
     * addCredits() is used to add a specified quantity of credits to the player
     * @param cred is an int representing the quantity of credits we want to add
     */
    public void addCredits(int cred){
        credits = credits + cred;
    }

    /**
     * @return player's personal board
     */
    public RocketshipBoard getBoard() {
        return board;
    }




    /**
     * addOneLap() allows to increase the player's number of completed laps by one
     */
    public void addOneLap(){
        laps = laps+1;
    }

    /**
     * subtractOneLap() allows to decrease the player's number of completed laps by one
     */
    public void subtractOneLap(){
        laps = laps - 1;
    }

    /**
     * getLaps() returns the number of completed laps by the player;
     */
    public int getLaps(){
        return laps;
    }



    public boolean checkIfHasRocketship() {
        return hasRocketship;
    }


    public void penalizeForLostComponents(int i) {
        System.out.println("enacting penalization of " + color + " player by increasing the number that keeps track of lost components");
        componentsPenalty = componentsPenalty + i;
    }

    public void penalizeForComponentsLostDuringFlight() {
        int destroyedComponents = board.countComponentsDestroyedDuringFlight();
        penalizeForLostComponents(destroyedComponents);
    }

    public void removeRocketship() { // [] called by cards that can destroy the rocketship (the rocketship needs to be checked first)
        penalizeForComponentsLostDuringFlight();
        hasRocketship = false;
    }

    public int getTotalComponentsPenalty() {
        return componentsPenalty;
    }




}
