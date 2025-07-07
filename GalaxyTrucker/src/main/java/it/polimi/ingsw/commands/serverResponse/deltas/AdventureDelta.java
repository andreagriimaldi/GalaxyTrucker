package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.DeltaType;
import it.polimi.ingsw.model.utilities.adventure.AdventureCard;

public class AdventureDelta extends Delta {
    AdventureCard card;

    public AdventureDelta(AdventureCard card) {
        super(DeltaType.ADVENTURE_DELTA);
        this.card = card;
    }
    
    public AdventureCard getDrawnCard() {
        return card;
    }
}