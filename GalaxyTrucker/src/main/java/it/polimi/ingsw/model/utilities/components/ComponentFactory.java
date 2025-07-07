package it.polimi.ingsw.model.utilities.components;

import it.polimi.ingsw.enums.ComponentSide;
import it.polimi.ingsw.enums.ComponentType;
import it.polimi.ingsw.enums.ConnectorType;
import it.polimi.ingsw.enums.CrewType;

import java.util.ArrayList;
import java.util.List;

public class ComponentFactory {

    public ComponentFactory(){
    }

    /**
     * buildUnturnedComponents() returns a list containing all the components included in the game's box, excluding
     * the starting cabins
     */
    public List<Component> buildUnturnedComponents(){
        List<Component> components = new ArrayList<>();

        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE}, "GT-new_tiles_16_forweb", 2));
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE}, "GT-new_tiles_16_forweb2", 2)); // (5, 8) Rx2
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE}, "GT-new_tiles_16_forweb3", 2));
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR}, "GT-new_tiles_16_forweb4", 2));
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR}, "GT-new_tiles_16_forweb5", 2));
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR}, "GT-new_tiles_16_forweb6", 2)); // (8, 9) Rx3
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR}, "GT-new_tiles_16_forweb7", 2)); // (6, 8) Rx1
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR}, "GT-new_tiles_16_forweb8", 2));
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR}, "GT-new_tiles_16_forweb9", 2));
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR}, "GT-new_tiles_16_forweb10", 2));
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR}, "GT-new_tiles_16_forweb11", 2));

        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE}, "GT-new_tiles_16_forweb12", 3)); // (6, 6) Rx1
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE}, "GT-new_tiles_16_forweb13", 3));
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE}, "GT-new_tiles_16_forweb14", 3));
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE}, "GT-new_tiles_16_forweb15", 3));
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE}, "GT-new_tiles_16_forweb16", 3));
        components.add(new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR}, "GT-new_tiles_16_forweb17", 3));


        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb18", 2, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb19", 2, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb20", 2, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR}, "GT-new_tiles_16_forweb21", 2, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb22", 2, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb23", 2, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb24", 2, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb25", 2, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb26", 2, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb27", 3, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb28", 3, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb29", 3, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb30", 3, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb31", 3, false));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb32", 3, false));

        // 33 IS BLUE CABIN
        // 34 IS GREEN CABIN
        
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb35"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb36"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb37")); // (8, 6) Rx1
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb38"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb39"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb40")); // (8, 9) Rx0
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb41"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb42"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb43"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb44"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb45"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb46"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb47"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb48"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb49"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb50"));
        components.add(new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb51"));

        // 52 IS RED CABIN

        components.add(new PipesComponent(ComponentType.PIPES, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb53")); // (5, 7) Rx0
        components.add(new PipesComponent(ComponentType.PIPES, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb54"));
        components.add(new PipesComponent(ComponentType.PIPES, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb55"));
        components.add(new PipesComponent(ComponentType.PIPES, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb56")); // (8, 5) Rx3
        components.add(new PipesComponent(ComponentType.PIPES, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb57"));
        components.add(new PipesComponent(ComponentType.PIPES, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb58"));
        components.add(new PipesComponent(ComponentType.PIPES, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb59"));
        components.add(new PipesComponent(ComponentType.PIPES, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb60"));

        // 61 IS YELLOW CABIN

        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb62", 1, true)); // (6, 7) Rx0
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb63", 1, true));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb64", 1, true));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb65", 1, true));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb66", 1, true)); // (7, 8) Rx3
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb67",1, true));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb68",2, true));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb69",2, true));
        components.add(new StockComponent(ComponentType.STOCK, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb70",2, true));

        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb71"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb72"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb73"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb74"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb75"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb76")); // (9, 4) Rx0
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb77"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb78"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb79"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb80"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb81"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb82"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb83"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb84")); // (9, 10) Rx0
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb85")); // (9, 9) Rx0
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb86")); // (9, 6) Rx0
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb87"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb88"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb89"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb90"));
        components.add(new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb91"));

        components.add(new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb92"));
        components.add(new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb93")); // (9, 5) Rx0
        components.add(new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb94"));
        components.add(new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb95"));
        components.add(new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb96")); // (8, 7) Rx0
        components.add(new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb97"));
        components.add(new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{ ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb98"));
        components.add(new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb99"));
        components.add(new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb100"));


        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb101"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb102"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb103"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb104"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb105"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb106"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb107"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb108"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb109"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb110")); // (9, 8) Rx3
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb111"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb112"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb113"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb114")); // (6, 5) Rx0
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb115")); // (4, 7) Rx0
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb116"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb117"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb118"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb119"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb120"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb121"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb122")); // (7, 9) Rx1
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb123"));
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb124")); // (6, 5) Rx3
        components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb125"));

        // components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_AAA")); // (5, 6) Rx0
        // components.add(new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_BBB")); // (8, 10) Rx0


        components.add(new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb126"));
        components.add(new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb127"));
        components.add(new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb128")); // (8, 4) Rx0
        components.add(new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb129"));
        components.add(new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE,}, "GT-new_tiles_16_forweb130"));
        components.add(new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb131")); // (7, 5) Rx3
        components.add(new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb132"));
        components.add(new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb133"));
        components.add(new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb134"));
        components.add(new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb135"));
        components.add(new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb136"));


        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb137", CrewType.ALIEN_BROWN));
        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb138", CrewType.ALIEN_BROWN));
        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb139", CrewType.ALIEN_BROWN));
        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb140", CrewType.ALIEN_BROWN));
        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb141", CrewType.ALIEN_BROWN));
        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb142", CrewType.ALIEN_BROWN));



        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb143", CrewType.ALIEN_PURPLE)); // (7, 6) Rx2
        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb144", CrewType.ALIEN_PURPLE));
        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb145", CrewType.ALIEN_PURPLE));
        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb146", CrewType.ALIEN_PURPLE));
        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb147", CrewType.ALIEN_PURPLE));
        components.add(new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb148", CrewType.ALIEN_PURPLE));



        components.add(new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb149", ComponentSide.NORTH, ComponentSide.EAST));
        components.add(new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb150", ComponentSide.NORTH, ComponentSide.EAST));
        components.add(new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR,}, "GT-new_tiles_16_forweb151", ComponentSide.NORTH, ComponentSide.EAST));
        components.add(new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb152", ComponentSide.NORTH, ComponentSide.EAST));
        components.add(new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb153", ComponentSide.NORTH, ComponentSide.EAST));
        components.add(new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.SINGLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR,}, "GT-new_tiles_16_forweb154", ComponentSide.NORTH, ComponentSide.EAST));
        components.add(new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.SMOOTH_SURFACE, ConnectorType.SINGLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb155", ComponentSide.NORTH, ComponentSide.EAST));
        components.add(new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{ ConnectorType.SMOOTH_SURFACE, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.DOUBLE_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR,}, "GT-new_tiles_16_forweb156", ComponentSide.NORTH, ComponentSide.EAST));

        return components;
    }
}






