package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.model.utilities.adventure.AdventureCard;

import java.util.List;

public class PeekableCardsDelta extends Delta{
    List<AdventureCard> cardsOne;
    List<AdventureCard> cardsTwo;
    List<AdventureCard> cardsThree;

    public PeekableCardsDelta(List<AdventureCard> one, List<AdventureCard> two, List<AdventureCard> three) {
        super(DeltaType.PEEKABLE_CARDS_DELTA);
        cardsOne = one;
        cardsTwo = two;
        cardsThree = three;
    }

    /**
     * returns a stack out of the three peekable stacks
     * @param choice specifies the stack to get
     * @return is the requested stack
     */
    public List<AdventureCard> getCardStack(int choice){
        switch (choice){
            case 1 -> {
                return cardsOne;
            }
            case 2 -> {
                return cardsTwo;
            }
            case 3 -> {
                return cardsThree;
            }
            default -> throw new IllegalArgumentException();
        }
    }
}
