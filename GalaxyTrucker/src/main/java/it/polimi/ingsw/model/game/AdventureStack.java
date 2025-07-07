package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.utilities.adventure.AdventureCard;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AdventureStack {
    List<AdventureCard> cards;
    boolean peekable;
    int size;



    public AdventureStack(boolean p) {
        this.cards = new LinkedList<>();
        peekable = p;
        size = 0;
    }

    @TestOnly
    public AdventureStack(List<AdventureCard> cards) {
        this.cards = cards;
        size = cards.size();
    }

    // for trial stack
    public AdventureStack(boolean p, List<AdventureCard> cards) {
        this.cards = cards;
        peekable = p;
        size = cards.size();
    }


    /**
     * Constructor method used to create a new AdventureStack in the shuffle() method
     * @param p tells whether the stack is peekable or not
     * @param one is the first AdventureStack
     * @param two is the second AdventureStack
     * @param three is the third AdventureStack
     * @param hidden is the non peekable AdventureStack
     */
    public AdventureStack(boolean p, AdventureStack one, AdventureStack two, AdventureStack three, AdventureStack hidden){
        peekable = p;
        size = one.getSize() + two.getSize() + three.getSize() + hidden.getSize();
        cards = new LinkedList<>(one.drawCardsToShuffle());
        cards.addAll(two.drawCardsToShuffle());
        cards.addAll(three.drawCardsToShuffle());
        cards.addAll(hidden.drawCardsToShuffle());
    }


    /**
     * createShuffled() is a method that creates the shuffled stack
     * @param one is the first AdventureStack
     * @param two is the second AdventureStack
     * @param three is the third AdventureStack
     * @param hidden is the non-peekable AdventureStack
     * @return a new AdventureStack containing all the cards of the three previous
     */
    public static AdventureStack createShuffled(AdventureStack one, AdventureStack two, AdventureStack three, AdventureStack hidden){
        return new AdventureStack(false, one, two, three, hidden);
    }


    /**
     * addCard() is used in the creation of the stack to add cards to it
     * @param adv is the card that we want to add
     */
    public void addCard(AdventureCard adv){
        cards.add(adv);
        size++;
    }

    /**
     * getSize() returns the size of the stack
     */
    public int getSize(){
        return size;
    }


    /**
     * peek() returns a list containing all the cards of the stack if it's peekable, otherwise throws an exception
     * @return a list of AdventureCard
     * @throws Exception if the stack is not peekable
     */
    public List<AdventureCard> peek(){
        if(peekable)
            return new ArrayList<>(cards);
        else throw new RuntimeException("Stack is not peekable");
    }


    /**
     * drawCardsToShuffle() is a private method used for the construction of the shuffled stack
     * @return all the cards in the stack
     */
    private List<AdventureCard> drawCardsToShuffle(){
        return cards;
    }


    /**
     * draw() is used to take a random card in the stack if the size is positive, otherwise an exception is thrown
     * @return a random card
     * @throws Exception if the stack is empty
     */
    public AdventureCard draw() {
        if(size > 0) {
            Random random = new Random();
            AdventureCard cardDrawn = cards.remove(random.nextInt(size));
            size--;
            return cardDrawn;
        } else {
            return null;
        }
    }

}
