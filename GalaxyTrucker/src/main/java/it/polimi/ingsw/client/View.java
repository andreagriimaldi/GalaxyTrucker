package it.polimi.ingsw.client;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.GameProgression;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.adventure.AdventureCard;
import it.polimi.ingsw.model.utilities.components.Component;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public abstract class View implements Observer {
    @Override
    public abstract void update(Observable o, Object arg);

    public abstract void renderMyRocketshipBoard(Component[][] b);

    public abstract void renderOtherRocketshipBoard(Component[][] newboard, PlayerColor color);

    public abstract void renderMyHandComponent(Component hand);

    public abstract void renderOtherHandComponent(Component hand, PlayerColor color);

    public abstract void renderMyReservedComponents(Component[] reserved);

    public abstract void renderOtherReservedComponents(Component[] reserved, PlayerColor color);

    public abstract void renderTurnedComponents(List<Component> t);

    public abstract void renderAdventureCard(AdventureCard card);

    public abstract void setColor(PlayerColor color);

    public abstract void addOtherColor(PlayerColor color);

    public abstract void nextShip();

    public abstract void renderPhase(GamePhase p);

    public abstract void renderProgression(GameProgression p);
}
