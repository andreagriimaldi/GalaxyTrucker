package it.polimi.ingsw.model.playerset;

public enum PlayerColor {
    RED,
    BLUE,
    YELLOW,
    GREEN;

    @Override
    public String toString() {
        switch(this) {
            case RED -> {
                return "\u001B[31mRED\u001B[0m";
            }
            case BLUE -> {
                return "\u001B[34mBLUE\u001B[0m";
            }
            case YELLOW -> {
                return "\u001B[33mYELLOW\u001B[0m";
            }
            case GREEN -> {
                return "\u001B[32mGREEN\u001B[0m";
            }
        }
        return "";
    }
}


