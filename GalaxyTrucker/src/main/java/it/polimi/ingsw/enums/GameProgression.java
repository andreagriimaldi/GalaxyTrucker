package it.polimi.ingsw.enums;

public enum GameProgression {
    INITIALIZING_GAME,
    RUNNING_GAME,
    CLOSED_GAME;

    public String toString(){
        switch(this){
            case INITIALIZING_GAME -> {
                return "Initializing game";
            }
            case RUNNING_GAME -> {
                return "Running game";
            }
            case CLOSED_GAME -> {
                return "Closed game";
            }
        }
        throw new RuntimeException();
    }
}
