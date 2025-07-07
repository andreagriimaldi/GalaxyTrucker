package it.polimi.ingsw.model.utilities.adventure;

import it.polimi.ingsw.enums.AdventureLevel;
import it.polimi.ingsw.enums.AdventureType;
import it.polimi.ingsw.model.game.AdventureStack;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.utilities.components.CubeToken;
import it.polimi.ingsw.enums.ResourceTypes;

import java.util.*;

public class CardFactory {
    private final List<AdventureCard> cards;
    private List<String> taken;

    public CardFactory(FlightType t){
        if(t == FlightType.TRIAL){
            cards = buildAdventureStackTrial();
        }
        else {
            cards = buildAllCards();
        }
        taken = new LinkedList<>();
    }

    public List<AdventureCard> getCards() {
        return cards;
    }

    /**
     * buildAdventureStackTrial() creates a deck of all the cards used in the trial adventure
     * @return the deck of 8 cards used in the trial adventure
     */
    public List<AdventureCard> buildAdventureStackTrial() {
        List<AdventureCard> adventureStackTrial = new LinkedList<>();


        /** ALL TRIAL CARDS **/

        adventureStackTrial.add(new EnemiesCard
                (AdventureType.ENEMY, AdventureLevel.LEVEL1, "GT-cards_I_IT_012", -1, 4, 2,
                new CubeToken[]{
                    new CubeToken(ResourceTypes.YELLOWCUBE),
                    new CubeToken(ResourceTypes.GREENCUBE),
                    new CubeToken(ResourceTypes.BLUECUBE)}));

        adventureStackTrial.add(new StardustCard
                (AdventureType.STARDUST, AdventureLevel.LEVEL1, "GT-cards_I_IT_014", 0));

        adventureStackTrial.add(new OpenSpaceCard
                (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL1, "GT-cards_I_IT_015", 0));

        adventureStackTrial.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL1, "GT-cards_I_IT_019", 0, new int[]{5,4,2}));

        adventureStackTrial.add(new PlanetCard
                (AdventureType.PLANET, AdventureLevel.LEVEL1, "GT-cards_I_IT_0113", -2,
                new CubeToken[][]{
                    {new CubeToken(ResourceTypes.REDCUBE), new CubeToken(ResourceTypes.REDCUBE)},
                    {new CubeToken(ResourceTypes.REDCUBE), new CubeToken(ResourceTypes.BLUECUBE),
                    new CubeToken(ResourceTypes.BLUECUBE)},
                    {new CubeToken(ResourceTypes.YELLOWCUBE)}}));

        adventureStackTrial.add(new WarzoneCard
                (AdventureType.WARZONE, AdventureLevel.LEVEL1, "GT-cards_I_IT_0116", -3, 2,
                new int[]{3,7}, 0, new int[]{1,2,3}, new int[]{4,3,1}));

        adventureStackTrial.add(new ShipCard
                (AdventureType.SHIP, AdventureLevel.LEVEL1, "GT-cards_I_IT_0118", -1, 3, 4));

        adventureStackTrial.add(new StationCard
                (AdventureType.STATION, AdventureLevel.LEVEL1, "GT-cards_I_IT_0119", -1, 5,
                new CubeToken[]{new CubeToken(ResourceTypes.YELLOWCUBE), new CubeToken(ResourceTypes.GREENCUBE)}));

        Collections.shuffle(adventureStackTrial);
        return adventureStackTrial;
    }


    /**
     * buildAdventureStackLevelTwo() creates stacks of 2 level 2 cards and 1 level 1 card
     * @param peekable indicates if a stack is peekable or not
     * @return one of the four stacks used in the Level 2 adventures
     */
    public AdventureStack buildAdventureStackLevelTwo(boolean peekable) {
        AdventureStack finalStack = new AdventureStack(peekable);
        List<AdventureCard> shuffled = cards;
        Collections.shuffle(shuffled);
        int counterLevel2 = 0;
        int counterLevel1 = 0;
        for(AdventureCard card : shuffled){
            if(counterLevel1 < 1 && card.getLevel().equals(AdventureLevel.LEVEL1) && !taken.contains(card.getID())){
                finalStack.addCard(card);
                taken.add(card.getID());
                counterLevel1++;
            }
            else if(counterLevel2 < 2 && card.getLevel().equals(AdventureLevel.LEVEL2) && !taken.contains(card.getID())){
                finalStack.addCard(card);
                taken.add(card.getID());
                counterLevel2++;
            }
            if(counterLevel1 == 1 && counterLevel2 == 2){
                break;
            }
        }
        return finalStack;
    }

    /**
     * buildAllCards() creates all the cards of Level 1 and 2
     */
    private List<AdventureCard> buildAllCards() {
        List<AdventureCard> cards = new LinkedList<>();



        /** ### ALL LEVEL 1 CARDS ### **/

        /* LV1 ENEMIES CARDS */

        cards.add(new EnemiesCard
                (AdventureType.ENEMY, AdventureLevel.LEVEL1, "GT-cards_I_IT_01", -1, 6, 3, 5));

        cards.add(new EnemiesCard
                (AdventureType.ENEMY, AdventureLevel.LEVEL1, "GT-cards_I_IT_013", -1, 5, new int[]{1,5,1}, 4));


        /* LV1 OPEN SPACE CARDS */

        cards.add(new OpenSpaceCard
                (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL1, "GT-cards_I_IT_016", 0));

        cards.add(new OpenSpaceCard
                (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL1, "GT-cards_I_IT_017", 0));

        cards.add(new OpenSpaceCard
                (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL1, "GT-cards_I_IT_018", 0));


        /* LV1 METEORITE CARDS */

        cards.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL1, "GT-cards_I_IT_0110", 0, new int[]{1,1,4,2,3}));

        cards.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL1, "GT-cards_I_IT_0111", 0, new int[]{5,1,5}));


        /* LV1 PLANET CARDS */

        cards.add(new PlanetCard
                (AdventureType.PLANET, AdventureLevel.LEVEL1, "GT-cards_I_IT_0112", -3,
                new CubeToken[][]{
                    {
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE)
                    }, {
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.YELLOWCUBE),
                        new CubeToken(ResourceTypes.BLUECUBE)
                    }, {
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE)
                    }, {
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE)
                    }
                }));

        cards.add(new PlanetCard
                (AdventureType.PLANET, AdventureLevel.LEVEL1, "GT-cards_I_IT_0114", -3,
                new CubeToken[][]{
                    {
                        new CubeToken(ResourceTypes.YELLOWCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE)
                    }, {
                        new CubeToken(ResourceTypes.YELLOWCUBE),
                        new CubeToken(ResourceTypes.YELLOWCUBE)
                    }
                }));

        cards.add(new PlanetCard
                (AdventureType.PLANET, AdventureLevel.LEVEL1, "GT-cards_I_IT_0115", -1,
                new CubeToken[][]{
                    {
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE)
                    }, {
                        new CubeToken(ResourceTypes.YELLOWCUBE)
                    }, {
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE)}
                }));


        /* LV1 SHIP CARDS */

        cards.add(new ShipCard
                (AdventureType.SHIP, AdventureLevel.LEVEL1, "GT-cards_I_IT_0117", -1, 2, 3));


        /* LV1 STATION CARDS */

        cards.add(new StationCard
                (AdventureType.STATION, AdventureLevel.LEVEL1, "GT-cards_I_IT_0120", -1, 6,
                new CubeToken[]{
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.REDCUBE)
                }));




        /* ALL LEVEL 2 CARDS */

        /* LV2 ENEMIES CARDS */

        cards.add(new EnemiesCard
                (AdventureType.ENEMY, AdventureLevel.LEVEL2, "GT-cards_II_IT_01", -2, 7, 4, 8));

        cards.add(new EnemiesCard // pirates
                (AdventureType.ENEMY,AdventureLevel.LEVEL2, "GT-cards_II_IT_012", -1, 8, 3,
                new CubeToken[] {
                    new CubeToken(ResourceTypes.REDCUBE),
                    new CubeToken(ResourceTypes.YELLOWCUBE),
                    new CubeToken(ResourceTypes.YELLOWCUBE)
                }));

        cards.add(new EnemiesCard
                (AdventureType.ENEMY, AdventureLevel.LEVEL2, "GT-cards_II_IT_013", -2, 6, new int[]{5,1,5}, 7));


        /* LV2 STARDUST CARDS */

        cards.add(new StardustCard
                (AdventureType.STARDUST, AdventureLevel.LEVEL2, "GT-cards_II_IT_014", 0));


        /* LV2 EPIDEMY CARDS */

        cards.add(new EpidemyCard
                (AdventureType.EPIDEMY, AdventureLevel.LEVEL2, "GT-cards_II_IT_015", 0));


        /* LV2 OPEN SPACE CARDS */

        cards.add(new OpenSpaceCard
                (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL2, "GT-cards_II_IT_016", 0));

        cards.add(new OpenSpaceCard
                (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL2, "GT-cards_II_IT_017", 0));

        cards.add(new OpenSpaceCard
                (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL2, "GT-cards_II_IT_018", 0));


        /* LV2 METEORITE CARDS */

        cards.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL2, "GT-cards_II_IT_019", 0, new int[]{1,1,8,4,4}));

        cards.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL2, "GT-cards_II_IT_0110", 0, new int[]{5,5,3,3}));

        cards.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL2, "GT-cards_II_IT_0111", 0, new int[]{1,1,6,2,2}));


        /* LV2 PLANET CARDS */

        cards.add(new PlanetCard
                (AdventureType.PLANET, AdventureLevel.LEVEL2, "GT-cards_II_IT_0112", -4,
                new CubeToken[][]{
                    {
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.YELLOWCUBE)
                    }, {
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE)
                }, {
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE)
                    }
                }));

        cards.add(new PlanetCard
                (AdventureType.PLANET, AdventureLevel.LEVEL2, "GT-cards_II_IT_0113", -3,
                new CubeToken[][]{
                    {
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.REDCUBE)
                    }, {
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE)
                    }
                }));

        cards.add(new PlanetCard
                (AdventureType.PLANET, AdventureLevel.LEVEL2, "GT-cards_II_IT_0114", -2,
                new CubeToken[][]{
                    {
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.YELLOWCUBE)
                    }, {
                        new CubeToken(ResourceTypes.YELLOWCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.BLUECUBE)
                    }, {
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE)
                    }, {
                        new CubeToken(ResourceTypes.YELLOWCUBE)
                    }
                }));

        cards.add(new PlanetCard
                (AdventureType.PLANET, AdventureLevel.LEVEL2, "GT-cards_II_IT_0115", -3,
                new CubeToken[][]{
                    {
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE),
                        new CubeToken(ResourceTypes.GREENCUBE)
                    }, {
                        new CubeToken(ResourceTypes.YELLOWCUBE),
                        new CubeToken(ResourceTypes.YELLOWCUBE)
                    }, {
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE),
                        new CubeToken(ResourceTypes.BLUECUBE)
                    }
                }));


        /* LV2 WARZONE CARDS */

        cards.add(new WarzoneCard
                (AdventureType.WARZONE, AdventureLevel.LEVEL2, "GT-cards_II_IT_0116", -4, 0,
                new int[]{1,4,2,7}, 3, new int[]{3,2,1}, new int[]{4,2,1}));


        /* LV2 SHIP CARDS */

        cards.add(new ShipCard
                (AdventureType.SHIP, AdventureLevel.LEVEL2, "GT-cards_II_IT_0117", -1, 4, 6));

        cards.add(new ShipCard
                (AdventureType.SHIP, AdventureLevel.LEVEL2, "GT-cards_II_IT_0118", -2, 5, 8));


        /* LV2 STATION CARDS */

        cards.add(new StationCard
                (AdventureType.STATION, AdventureLevel.LEVEL2, "GT-cards_II_IT_0119", -1, 7,
                new CubeToken[]{
                        new CubeToken(ResourceTypes.REDCUBE),
                        new CubeToken(ResourceTypes.YELLOWCUBE)
                    }));

        cards.add(new StationCard
                (AdventureType.STATION, AdventureLevel.LEVEL2, "GT-cards_II_IT_0120", -2, 8,
                new CubeToken[]{
                    new CubeToken(ResourceTypes.YELLOWCUBE),
                    new CubeToken(ResourceTypes.YELLOWCUBE),
                    new CubeToken(ResourceTypes.GREENCUBE)
                }));

        return cards;
    }


}
