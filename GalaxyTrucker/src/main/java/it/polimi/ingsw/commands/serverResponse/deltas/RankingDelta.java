package it.polimi.ingsw.commands.serverResponse.deltas;

import it.polimi.ingsw.enums.CreditsType;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.enums.DeltaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingDelta extends Delta {

    List<Map.Entry<PlayerColor, Float>> ranking;

    Map<PlayerColor, Map<CreditsType, Float>> credits;

    public RankingDelta(Map<PlayerColor, Map<CreditsType, Float>> credits, List<Map.Entry<PlayerColor, Float>> rankings) {
        super(DeltaType.RANKING_DELTA);
        this.credits = credits;
        this.ranking = rankings;
    }

    public List<Map.Entry<PlayerColor, Float>> getRanking() {
        return ranking;
    }

    public Map<PlayerColor, Map<CreditsType, Float>> getCredits() {
        return credits;
    }

}
