package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;
import it.polimi.ingsw.model.utilities.components.Component;

public class ComponentRejectedMove extends Move{

    public ComponentRejectedMove(GamePhase phase, MoveType type) {
        super(phase, type);
    }
}
