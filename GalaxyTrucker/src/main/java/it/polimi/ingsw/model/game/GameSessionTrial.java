package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.utilities.adventure.*;

import java.util.List;

import static it.polimi.ingsw.enums.FlightType.TRIAL;

public class GameSessionTrial extends GameSession{
    List<AdventureCard> cards;

    public GameSessionTrial(){
        super(TRIAL);
        CardFactory factory = new CardFactory(TRIAL);
        cards = factory.getCards();
    }

    @Override
    public AdventureStack getCards() {
        AdventureStack trialStack = new AdventureStack(false, cards);
        return trialStack;
    }
}
