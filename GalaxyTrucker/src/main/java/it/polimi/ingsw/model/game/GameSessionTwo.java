package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.utilities.adventure.AdventureCard;
import it.polimi.ingsw.model.utilities.adventure.CardFactory;

import java.util.List;

import static it.polimi.ingsw.enums.FlightType.TWO;

public class GameSessionTwo extends GameSession{
    AdventureStack cardsOne;
    AdventureStack cardsTwo;
    AdventureStack cardsThree;
    AdventureStack cardsHidden;

    public GameSessionTwo(){
        super(TWO);
        CardFactory factory = new CardFactory(TWO);
        cardsOne = factory.buildAdventureStackLevelTwo(true);
        cardsTwo = factory.buildAdventureStackLevelTwo(true);
        cardsThree = factory.buildAdventureStackLevelTwo(true);
        cardsHidden = factory.buildAdventureStackLevelTwo(false);
    }

    @Override
    public AdventureStack getCards() {
        return AdventureStack.createShuffled(this.cardsOne, this.cardsTwo, this.cardsThree, this.cardsHidden);
    }

    /**
     * getCardsToPeek() returns a specific peekable AdventureStack
     * @return a list containing all the cards in the selected stack
     */
    public List<AdventureCard> getCardsToPeek(int stack){
        switch (stack){
            case 1 -> {
                return cardsOne.peek();
            }
            case 2 -> {
                return cardsTwo.peek();
            }
            case 3 -> {
                return cardsThree.peek();
            }
            default -> throw new IllegalArgumentException();
        }
    }
}
