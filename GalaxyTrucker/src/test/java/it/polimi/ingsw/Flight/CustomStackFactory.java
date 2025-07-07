package it.polimi.ingsw.Flight;

import it.polimi.ingsw.enums.AdventureLevel;
import it.polimi.ingsw.enums.AdventureType;
import it.polimi.ingsw.model.utilities.adventure.*;
import it.polimi.ingsw.model.utilities.components.CubeToken;
import it.polimi.ingsw.enums.ResourceTypes;

import java.util.LinkedList;
import java.util.List;


public class CustomStackFactory {



    public static List<AdventureCard> build_ship_card_stack() {
        List<AdventureCard> test_stack = new LinkedList<>();

        /* LV1 SHIP CARD */

        test_stack.add(new ShipCard
                (AdventureType.SHIP, AdventureLevel.LEVEL1, "GT-cards_I_IT_0118", -1, 3, 4));


        /* LV2 SHIP CARD */

        test_stack.add(new ShipCard
                (AdventureType.SHIP, AdventureLevel.LEVEL2, "GT-cards_II_IT_0117", -1, 4, 6));

        test_stack.add(new ShipCard
                (AdventureType.SHIP, AdventureLevel.LEVEL2, "GT-cards_II_IT_0118", -2, 5, 8));


        return test_stack;
    }


    public static List<AdventureCard> build_station_card_stack() {
        List<AdventureCard> test_stack = new LinkedList<>();


        /* LV1 STATION CARDS */

        test_stack.add(new StationCard
                (AdventureType.STATION, AdventureLevel.LEVEL1, "GT-cards_I_IT_0120", -1, 6,
                        new CubeToken[]{
                                new CubeToken(ResourceTypes.REDCUBE),
                                new CubeToken(ResourceTypes.REDCUBE)
                        }));

        /* LV2 STATION CARDS */

        test_stack.add(new StationCard
                (AdventureType.STATION, AdventureLevel.LEVEL2, "GT-cards_II_IT_0119", -1, 7,
                        new CubeToken[]{
                                new CubeToken(ResourceTypes.REDCUBE),
                                new CubeToken(ResourceTypes.YELLOWCUBE)
                        }));

        test_stack.add(new StationCard
                (AdventureType.STATION, AdventureLevel.LEVEL2, "GT-cards_II_IT_0120", -2, 8,
                        new CubeToken[]{
                                new CubeToken(ResourceTypes.YELLOWCUBE),
                                new CubeToken(ResourceTypes.YELLOWCUBE),
                                new CubeToken(ResourceTypes.GREENCUBE)
                        }));

        return test_stack;
    }






    public static List<AdventureCard> build_stardust_card_stack() {
        List<AdventureCard> test_stack = new LinkedList<>();


        /* LV2 STARDUST CARDS */

        test_stack.add(new StardustCard
                (AdventureType.STARDUST, AdventureLevel.LEVEL2, "GT-cards_II_IT_014", 0));


        return test_stack;
    }

    public static List<AdventureCard> build_open_space_card_stack() {
        List<AdventureCard> test_stack = new LinkedList<>();

        test_stack.add(new OpenSpaceCard
                (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL1, "GT-cards_I_IT_015", 0));

        test_stack.add(new OpenSpaceCard
                (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL1, "GT-cards_I_IT_016", 0));

        test_stack.add(new OpenSpaceCard
                (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL1, "GT-cards_I_IT_017", 0));

        test_stack.add(new OpenSpaceCard
                (AdventureType.OPEN_SPACE, AdventureLevel.LEVEL1, "GT-cards_I_IT_018", 0));

        return test_stack;
    }



    public static List<AdventureCard> build_enemies_card_stack() {
        List<AdventureCard> test_stack = new LinkedList<>();


        /* TRIAL ENEMIES CARD */
        test_stack.add(new EnemiesCard
                (AdventureType.ENEMY,AdventureLevel.LEVEL1, "GT-cards_I_IT_012", -1, 4, 2,
                        new CubeToken[]{
                                new CubeToken(ResourceTypes.YELLOWCUBE),
                                new CubeToken(ResourceTypes.GREENCUBE),
                                new CubeToken(ResourceTypes.BLUECUBE)}));



        /* LV1 ENEMIES CARDS */

        test_stack.add(new EnemiesCard
                (AdventureType.ENEMY, AdventureLevel.LEVEL1, "GT-cards_I_IT_01", -1, 6, 3, 5));



        test_stack.add(new EnemiesCard // pirates
                (AdventureType.ENEMY, AdventureLevel.LEVEL1, "GT-cards_I_IT_013", -1, 7, new int[]{1,5,1}, 4)); // originally firepower was 5 instead of 7


        /* LV2 ENEMIES CARDS */

        test_stack.add(new EnemiesCard
                (AdventureType.ENEMY, AdventureLevel.LEVEL2, "GT-cards_II_IT_01", -2, 7, 4, 8));

        test_stack.add(new EnemiesCard
                (AdventureType.ENEMY,AdventureLevel.LEVEL2, "GT-cards_II_IT_012", -1, 8, 3,
                        new CubeToken[]{
                                new CubeToken(ResourceTypes.REDCUBE),
                                new CubeToken(ResourceTypes.YELLOWCUBE),
                                new CubeToken(ResourceTypes.YELLOWCUBE)
                        }));

        test_stack.add(new EnemiesCard
                (AdventureType.ENEMY, AdventureLevel.LEVEL2, "GT-cards_II_IT_013", -2, 6, new int[]{5,1,5}, 7));



        return test_stack;
    }


    public static List<AdventureCard> build_meteorite_card_stack() {
        List<AdventureCard> test_stack = new LinkedList<>();

        /* TRIAL METEORITE CARD */

        test_stack.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL1, "GT-cards_I_IT_019", 0, new int[]{5,4,2}));


        /* LV1 METEORITE CARDS */

        test_stack.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL1, "GT-cards_I_IT_0110", 0, new int[]{1,1,4,2,3}));

        test_stack.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL1, "GT-cards_I_IT_0111", 0, new int[]{5,1,5}));

        /* LV2 METEORITE CARDS */

        test_stack.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL2, "GT-cards_II_IT_019", 0, new int[]{1,1,8,4,4}));

        test_stack.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL2, "GT-cards_II_IT_0110", 0, new int[]{5,5,3,3}));

        test_stack.add(new MeteoriteCard
                (AdventureType.METEORITE, AdventureLevel.LEVEL2, "GT-cards_II_IT_0111", 0, new int[]{1,1,6,2,2}));

        return test_stack;
    }


    public static List<AdventureCard> build_warzone_card_stack() {
        List<AdventureCard> test_stack = new LinkedList<>();


        /* TRIAL WARZONE CARD */

        test_stack.add(new WarzoneCard
                (AdventureType.WARZONE, AdventureLevel.LEVEL1, "GT-cards_I_IT_0116", -3, 2,
                        new int[]{3,7}, 0, new int[]{1,2,3}, new int[]{4,3,1}));


        /* LV2 WARZONE CARDS */

        test_stack.add(new WarzoneCard
                (AdventureType.WARZONE, AdventureLevel.LEVEL2, "GT-cards_II_IT_0116", -4, 0,
                        new int[]{1,4,2,7}, 3, new int[]{3,2,1}, new int[]{4,2,1}));

        return test_stack;
    }




    public static List<AdventureCard> build_planets_card_stack() {
        List<AdventureCard> test_stack = new LinkedList<>();


        test_stack.add(new PlanetCard
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

        test_stack.add(new PlanetCard
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

        test_stack.add(new PlanetCard
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

        /* LV2 PLANET CARDS */

        test_stack.add(new PlanetCard
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

        test_stack.add(new PlanetCard
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

        test_stack.add(new PlanetCard
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

        test_stack.add(new PlanetCard
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


        return test_stack;
    }


}