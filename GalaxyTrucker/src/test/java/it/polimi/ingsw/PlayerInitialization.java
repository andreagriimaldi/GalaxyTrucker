package it.polimi.ingsw;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.playerset.RocketshipBoard;
import it.polimi.ingsw.model.utilities.components.*;

import java.util.LinkedList;
import java.util.List;

public class PlayerInitialization {

    public static List<Player> initializePlayersInOrder(FlightType flightType, List<PlayerColor> orderOfPlayers) {



        List<Player> orderedPlayers = new LinkedList<>();
        Player newPlayer = null;



        for (PlayerColor playerColor : orderOfPlayers) {

            try {
                switch (playerColor) {
                    case RED -> {
                        String redUsername = "red";
                        RocketshipBoard rsRed = initializePresetRocketship(flightType, PlayerColor.RED);
                        Player redPlayer = new Player(PlayerColor.RED, redUsername, rsRed);
                        newPlayer = redPlayer;
                    }
                    case BLUE -> {
                        String blueUsername = "blue";
                        RocketshipBoard rsBlue = initializePresetRocketshipNoDouble(flightType, PlayerColor.BLUE);
                        Player bluePlayer = new Player(PlayerColor.BLUE, blueUsername, rsBlue);
                        newPlayer = bluePlayer;
                    }
                    case YELLOW -> {
                        String yellowUsername = "yellow";
                        RocketshipBoard rsYellow = initializePresetRocketship(flightType, PlayerColor.YELLOW);
                        Player yellowPlayer = new Player(PlayerColor.YELLOW, yellowUsername, rsYellow);
                        newPlayer = yellowPlayer;
                    }
                    case GREEN -> {
                        String greenUsername = "green";
                        RocketshipBoard rsGreen = initializePresetRocketship(flightType, PlayerColor.GREEN);
                        Player greenPlayer = new Player(PlayerColor.GREEN, greenUsername, rsGreen);
                        newPlayer = greenPlayer;
                    }
                }

                if (newPlayer != null) {
                    orderedPlayers.add(newPlayer);
                } else throw new NullPointerException();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return orderedPlayers;
    }


    public static RocketshipBoard initializePresetRocketship(FlightType flightType, PlayerColor playerColor) {
        RocketshipBoard defaultRocketship = new RocketshipBoard(flightType, playerColor);

        // [] ideally, the cells in common are all added anyway, and then cells that are only present in a level two flight are added in a separate if
        if(flightType == FlightType.TWO) {


            /*
            Component cell_4_7 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web115"); // (4, 7) Rx0
            defaultRocketship.addComponent(4, 7, cell_4_7);
             */

            Component cell_6_7 = new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web62", 1, true); // (6, 7) Rx0
            placeTranslatedComponent(6, 7, cell_6_7, defaultRocketship);

            Component cell_6_6 = new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR}, "GT-new_tiles_16_for web12", 3); // (6, 6) Rx1
            cell_6_6.rotateClockwise();
            placeTranslatedComponent(6, 6, cell_6_6, defaultRocketship);

            Component cell_6_5 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web124"); // (6, 5) Rx3
            cell_6_5.rotateClockwise();
            cell_6_5.rotateClockwise();
            cell_6_5.rotateClockwise();
            placeTranslatedComponent(6, 5, cell_6_5, defaultRocketship);

            Component cell_6_8 = new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR}, "GT-new_tiles_16_for web7", 2); // (6, 8) Rx1
            cell_6_8.rotateClockwise();
            placeTranslatedComponent(6, 8, cell_6_8, defaultRocketship);

            Component cell_6_9 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web114"); // (6, 9) Rx0
            placeTranslatedComponent(6, 9, cell_6_9, defaultRocketship);




            Component cell_5_6 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web121"); // (5, 6) Rx0
            placeTranslatedComponent(5, 6, cell_5_6, defaultRocketship);

            /*
            Component cell_5_7 = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web53", ComponentSide.NORTH, ComponentSide.EAST); // (5, 7) Rx0
            cell_5_7.rotateClockwise();
            defaultRocketship.addComponent(5, 7, cell_5_7);
             */

            Component cell_5_8 = new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE}, "GT-new_tiles_16_for web2", 2); // (5, 8) Rx2
            cell_5_8.rotateClockwise();
            cell_5_8.rotateClockwise();
            placeTranslatedComponent(5, 8, cell_5_8, defaultRocketship);

            Component cell_7_6 = new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_for web143", CrewType.ALIEN_PURPLE); // (7, 6) Rx2
            cell_7_6.rotateClockwise();
            cell_7_6.rotateClockwise();
            placeTranslatedComponent(7, 6, cell_7_6, defaultRocketship);

            Component cell_7_5 = new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web131"); // (7, 5) Rx3
            cell_7_5.rotateClockwise();
            cell_7_5.rotateClockwise();
            cell_7_5.rotateClockwise();
            placeTranslatedComponent(7, 5, cell_7_5, defaultRocketship);

            Component cell_7_8 = new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web66", 1, true); // (7, 8) Rx3
            cell_7_8.rotateClockwise();
            cell_7_8.rotateClockwise();
            cell_7_8.rotateClockwise();
            placeTranslatedComponent(7, 8, cell_7_8, defaultRocketship);

            Component cell_7_9 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_for web122"); // (7, 9) Rx1
            cell_7_9.rotateClockwise();
            placeTranslatedComponent(7, 9, cell_7_9, defaultRocketship);



            Component cell_8_7 = new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web96"); // (8, 7) Rx0
            placeTranslatedComponent(8, 7, cell_8_7, defaultRocketship);

            Component cell_8_6 = new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web37"); // (8, 6) Rx1
            cell_8_6.rotateClockwise();
            placeTranslatedComponent(8, 6, cell_8_6, defaultRocketship);

            Component cell_8_5 = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web56", ComponentSide.NORTH, ComponentSide.EAST); // (8, 5) Rx3
            cell_8_5.rotateClockwise();
            cell_8_5.rotateClockwise();
            cell_8_5.rotateClockwise();
            placeTranslatedComponent(8, 5, cell_8_5, defaultRocketship);

            Component cell_8_4 = new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_for web128"); // (8, 4) Rx0
            placeTranslatedComponent(8, 4, cell_8_4, defaultRocketship);

            Component cell_8_8 = new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web40"); // (8, 8) Rx0
            placeTranslatedComponent(8, 8, cell_8_8, defaultRocketship);

            Component cell_8_9 = new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR}, "GT-new_tiles_16_for web6", 2); // (8, 9) Rx3
            cell_8_9.rotateClockwise();
            cell_8_9.rotateClockwise();
            cell_8_9.rotateClockwise();
            placeTranslatedComponent(8, 9, cell_8_9, defaultRocketship);

            Component cell_8_10 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web112"); // (8, 10) Rx0
            placeTranslatedComponent(8, 10, cell_8_10, defaultRocketship);



            Component cell_9_6 = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_for web86"); // (9, 6) Rx0
            placeTranslatedComponent(9, 6, cell_9_6, defaultRocketship);

            Component cell_9_5 = new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_for web93"); // (9, 5) Rx0
            placeTranslatedComponent(9, 5, cell_9_5, defaultRocketship);

            Component cell_9_4 = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_for web76"); // (9, 4) Rx0
            placeTranslatedComponent(9, 4, cell_9_4, defaultRocketship);


            // cell_9_7 is unbuildable

            Component cell_9_8 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_for web110"); // (9, 8) Rx3
            cell_9_8.rotateClockwise();
            cell_9_8.rotateClockwise();
            cell_9_8.rotateClockwise();
            placeTranslatedComponent(9, 8, cell_9_8, defaultRocketship);

            Component cell_9_9 = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_for web85"); // (9, 9) Rx0
            placeTranslatedComponent(9, 9, cell_9_9, defaultRocketship);

            Component cell_9_10 = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_for web84"); // (9, 10) Rx0
            placeTranslatedComponent(9, 10, cell_9_10, defaultRocketship);


        } else {

        }

        // stampa le coordinate reali interne (senza offset)
        //for (int i = 0; i < 5; i++)
            //for (int j = 0; j < 7; j++)
                //if (!defaultRocketship.isCellFree(i,j))
                    //System.out.printf("(%d,%d)  %s%n", i, j,
                            //defaultRocketship.getComponentAt(i,j).getComponentType());


        return defaultRocketship;
    }

    public static RocketshipBoard initializePresetRocketshipNoDouble(FlightType flightType, PlayerColor playerColor) {
        RocketshipBoard defaultRocketship = new RocketshipBoard(flightType, playerColor);

        // [] ideally, the cells in common are all added anyway, and then cells that are only present in a level two flight are added in a separate if
        if(flightType == FlightType.TWO) {


            /*
            Component cell_4_7 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web115"); // (4, 7) Rx0
            defaultRocketship.addComponent(4, 7, cell_4_7);
             */

            Component cell_6_7 = new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web62", 1, true); // (6, 7) Rx0
            placeTranslatedComponent(6, 7, cell_6_7, defaultRocketship);

            Component cell_6_6 = new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR}, "GT-new_tiles_16_for web12", 3); // (6, 6) Rx1
            cell_6_6.rotateClockwise();
            placeTranslatedComponent(6, 6, cell_6_6, defaultRocketship);

            Component cell_6_5 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web124"); // (6, 5) Rx3
            cell_6_5.rotateClockwise();
            cell_6_5.rotateClockwise();
            cell_6_5.rotateClockwise();
            placeTranslatedComponent(6, 5, cell_6_5, defaultRocketship);

            Component cell_6_8 = new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR}, "GT-new_tiles_16_for web7", 2); // (6, 8) Rx1
            cell_6_8.rotateClockwise();
            placeTranslatedComponent(6, 8, cell_6_8, defaultRocketship);

            Component cell_6_9 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web114"); // (6, 9) Rx0
            placeTranslatedComponent(6, 9, cell_6_9, defaultRocketship);




            Component cell_5_6 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web121"); // (5, 6) Rx0
            placeTranslatedComponent(5, 6, cell_5_6, defaultRocketship);

            /*
            Component cell_5_7 = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web53", ComponentSide.NORTH, ComponentSide.EAST); // (5, 7) Rx0
            cell_5_7.rotateClockwise();
            defaultRocketship.addComponent(5, 7, cell_5_7);
             */

            Component cell_5_8 = new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE}, "GT-new_tiles_16_for web2", 2); // (5, 8) Rx2
            cell_5_8.rotateClockwise();
            cell_5_8.rotateClockwise();
            placeTranslatedComponent(5, 8, cell_5_8, defaultRocketship);

            Component cell_7_6 = new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_for web143", CrewType.ALIEN_PURPLE); // (7, 6) Rx2
            cell_7_6.rotateClockwise();
            cell_7_6.rotateClockwise();
            placeTranslatedComponent(7, 6, cell_7_6, defaultRocketship);

            Component cell_7_5 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web131"); // (7, 5) Rx3
            cell_7_5.rotateClockwise();
            cell_7_5.rotateClockwise();
            cell_7_5.rotateClockwise();
            placeTranslatedComponent(7, 5, cell_7_5, defaultRocketship);

            Component cell_7_8 = new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web66", 1, true); // (7, 8) Rx3
            cell_7_8.rotateClockwise();
            cell_7_8.rotateClockwise();
            cell_7_8.rotateClockwise();
            placeTranslatedComponent(7, 8, cell_7_8, defaultRocketship);

            Component cell_7_9 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_for web122"); // (7, 9) Rx1
            cell_7_9.rotateClockwise();
            placeTranslatedComponent(7, 9, cell_7_9, defaultRocketship);



            Component cell_8_7 = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web96"); // (8, 7) Rx0
            placeTranslatedComponent(8, 7, cell_8_7, defaultRocketship);

            Component cell_8_6 = new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web37"); // (8, 6) Rx1
            cell_8_6.rotateClockwise();
            placeTranslatedComponent(8, 6, cell_8_6, defaultRocketship);

            Component cell_8_5 = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_for web56", ComponentSide.NORTH, ComponentSide.EAST); // (8, 5) Rx3
            cell_8_5.rotateClockwise();
            cell_8_5.rotateClockwise();
            cell_8_5.rotateClockwise();
            placeTranslatedComponent(8, 5, cell_8_5, defaultRocketship);

            Component cell_8_4 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_for web128"); // (8, 4) Rx0
            placeTranslatedComponent(8, 4, cell_8_4, defaultRocketship);

            Component cell_8_8 = new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web40"); // (8, 8) Rx0
            placeTranslatedComponent(8, 8, cell_8_8, defaultRocketship);

            Component cell_8_9 = new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR}, "GT-new_tiles_16_for web6", 2); // (8, 9) Rx3
            cell_8_9.rotateClockwise();
            cell_8_9.rotateClockwise();
            cell_8_9.rotateClockwise();
            placeTranslatedComponent(8, 9, cell_8_9, defaultRocketship);

            Component cell_8_10 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_for web112"); // (8, 10) Rx0
            placeTranslatedComponent(8, 10, cell_8_10, defaultRocketship);



            Component cell_9_6 = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_for web86"); // (9, 6) Rx0
            placeTranslatedComponent(9, 6, cell_9_6, defaultRocketship);

            Component cell_9_5 = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_for web93"); // (9, 5) Rx0
            placeTranslatedComponent(9, 5, cell_9_5, defaultRocketship);

            Component cell_9_4 = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_for web76"); // (9, 4) Rx0
            placeTranslatedComponent(9, 4, cell_9_4, defaultRocketship);


            // cell_9_7 is unbuildable

            Component cell_9_8 = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_for web110"); // (9, 8) Rx3
            cell_9_8.rotateClockwise();
            cell_9_8.rotateClockwise();
            cell_9_8.rotateClockwise();
            placeTranslatedComponent(9, 8, cell_9_8, defaultRocketship);

            Component cell_9_9 = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_for web85"); // (9, 9) Rx0
            placeTranslatedComponent(9, 9, cell_9_9, defaultRocketship);

            Component cell_9_10 = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_for web84"); // (9, 10) Rx0
            placeTranslatedComponent(9, 10, cell_9_10, defaultRocketship);


        } else {

        }

        // stampa le coordinate reali interne (senza offset)
        //for (int i = 0; i < 5; i++)
        //for (int j = 0; j < 7; j++)
        //if (!defaultRocketship.isCellFree(i,j))
        //System.out.printf("(%d,%d)  %s%n", i, j,
        //defaultRocketship.getComponentAt(i,j).getComponentType());


        return defaultRocketship;
    }


    public static void placeTranslatedComponent(int row, int col, Component c, RocketshipBoard rs) {
        rs.addComponent(row-5, col-4, c);
    }



}