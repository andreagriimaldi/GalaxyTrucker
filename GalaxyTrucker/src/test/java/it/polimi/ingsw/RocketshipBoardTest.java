package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.TUI;
import it.polimi.ingsw.commands.serverResponse.deltas.Delta;
import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.commands.serverResponse.deltas.FlightTypeDelta;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.playerset.RocketshipBoard;
import it.polimi.ingsw.model.utilities.components.*;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static it.polimi.ingsw.enums.FlightType.TRIAL;
import static it.polimi.ingsw.enums.FlightType.TWO;
import static it.polimi.ingsw.enums.ComponentSide.*;
import static it.polimi.ingsw.enums.ConnectorType.*;
import static org.junit.Assert.*;


public class RocketshipBoardTest {
    @Test
    public void createAndPopulateRocketship(){
        RocketshipBoard board = new RocketshipBoard(TWO, PlayerColor.YELLOW);
        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{SMOOTH_SURFACE, SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ThrusterComponent thruster = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ShieldComponent shield = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE, DOUBLE_CONNECTOR}, UUID.randomUUID().toString(), NORTH, EAST);
        board.addComponent(3,3, thruster);
        board.addComponent(2,4, shield);
        board.addComponent(2,2, cannon);

        float tot1 = board.checkActivatableThrustPower(0);
        float tot2 = board.checkActivatableFirePower(0);
        boolean check = board.checkShipAtStart();

        assertEquals(1.0, tot1, 0.1); //insert delta to avoid "rejection"
        assertEquals(1.0, tot2, 0.1);
        assertTrue(check);

    }

    @Test
    public void exposedConnectors(){
        RocketshipBoard board = new RocketshipBoard(TRIAL, PlayerColor.RED);
        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{SMOOTH_SURFACE, SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ThrusterComponent thruster = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ShieldComponent shield = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{SMOOTH_SURFACE, SMOOTH_SURFACE, UNIVERSAL_CONNECTOR, DOUBLE_CONNECTOR}, UUID.randomUUID().toString(), NORTH, EAST);
        try{
            board.addComponent(2,2, cannon);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try{
            board.addComponent(3,3, thruster);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try{
            board.addComponent(2,4, shield);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        int exp = 0;
        try{
            exp = board.countExposedConnectors();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(2, exp);
        assertTrue(board.checkShipAtStart());
    }

    @Test
    public void connectedComponentsAndPlaceholders(){
        RocketshipBoard board = new RocketshipBoard(TRIAL, PlayerColor.YELLOW);
        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{SMOOTH_SURFACE, SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ThrusterComponent thruster = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ShieldComponent shield = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE, DOUBLE_CONNECTOR}, UUID.randomUUID().toString(), NORTH, EAST);
        PowerCenterComponent power = new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString(), 2);
        board.addComponent(3,3, thruster);
        board.addComponent(2,4, shield);
        board.addComponent(2,2, cannon);
        board.addComponent(3,4, power);
        board.putBatteriesAndCrew();


        boolean check = board.checkShipAtStart();
        assertFalse(check);
        assertEquals(2, board.checkTotalBatteries());
        assertEquals(2, board.checkTotalCrew());
    }

    @Test
    public void rotateComponent(){
        RocketshipBoard board = new RocketshipBoard(TRIAL, PlayerColor.YELLOW);
        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{SMOOTH_SURFACE, SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ThrusterComponent thruster = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ShieldComponent shield = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE, DOUBLE_CONNECTOR}, UUID.randomUUID().toString(), NORTH, EAST);
        board.addComponent(3,3, thruster);
        board.addComponent(2,4, shield);
        System.out.println(renderCell(cannon, 13));
        cannon.rotateClockwise();
        System.out.println(renderCell(cannon, 13));
        board.addComponent(2,2, cannon);

        boolean check = board.checkShipAtStart();
        assertFalse(check);
    }

    @Test
    public void rotateHandComponent(){
        RocketshipBoard board = new RocketshipBoard(TRIAL, PlayerColor.YELLOW);
        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{SMOOTH_SURFACE, SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        System.out.println(renderCell(cannon, 13));
        board.addHand(cannon);
        board.rotateHand();
        System.out.println(renderCell(cannon, 13));
        assertEquals(ConnectorType.SINGLE_CONNECTOR, cannon.getConnectorByDirection(SOUTH));

    }

    @Test
    public void components(){
        UnturnedComponents unturned = new UnturnedComponents();
        System.out.println(unturned.draw().toString());
        System.out.println(unturned.draw().toString());
        assertEquals(150, unturned.countUnturnedComponents());
    }

    @Test
    public void printRocketship(){
        ClientController c = new ClientController(ConnectionType.SOCKET, UIType.TUI);
        try {
            c.initializeUI(UIType.TUI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Map<DeltaType, Delta> deltas = new HashMap<>();

        deltas.put(DeltaType.FLIGHT_TYPE_DELTA, new FlightTypeDelta(TWO));

        c.getViewModel().updateView(deltas);

        RocketshipBoard board = new RocketshipBoard(TRIAL, PlayerColor.YELLOW);


        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{SMOOTH_SURFACE, SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ThrusterComponent thruster = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ShieldComponent shield = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE, DOUBLE_CONNECTOR}, UUID.randomUUID().toString(), NORTH, EAST);
        board.addComponent(3,3, thruster);
        board.addComponent(2,4, shield);
        board.addComponent(2,2, cannon);

        Component[][] b = new Component[5][7];

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 7; j++){
                b[i][j] = board.getComponentAt(i,j);
            }
        }

        c.getView().renderMyRocketshipBoard(b);
        List<String> boardToPrint = ((TUI) c.getView()).getMyRocketshipBoard();
        for(int i = 0; i < boardToPrint.size(); i++){
            System.out.println(boardToPrint.get(i));
        }
    }

    @Test
    public void serializableTest(){
        Component rotatedComponent = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{SMOOTH_SURFACE, SINGLE_CONNECTOR, UNIVERSAL_CONNECTOR, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        System.out.println("Before serialization: " + rotatedComponent);
        rotatedComponent.rotateClockwise();

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("component.ser"))) {
            out.writeObject(rotatedComponent);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Component deserialized = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("component.ser"))) {
            deserialized = (Component) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("After deserialization: " + deserialized);

        assertEquals(SMOOTH_SURFACE, deserialized.getConnectorByDirection(NORTH));
        assertEquals(SMOOTH_SURFACE, deserialized.getConnectorByDirection(EAST));
        assertEquals(SINGLE_CONNECTOR, deserialized.getConnectorByDirection(SOUTH));
        assertEquals(UNIVERSAL_CONNECTOR, deserialized.getConnectorByDirection(WEST));

    }

    @Test
    public void componentFromReserve(){
        RocketshipBoard board = new RocketshipBoard(TWO, PlayerColor.YELLOW);
        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{SMOOTH_SURFACE, SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ThrusterComponent thruster = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ShieldComponent shield = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE, DOUBLE_CONNECTOR}, UUID.randomUUID().toString(), NORTH, EAST);
        board.addComponent(3,3, thruster);
        board.addComponent(2,4, shield);
        board.addHand(cannon);

        assertEquals(cannon, board.getHandComponent());

        board.putHandInReserved();

        assertEquals(cannon, board.returnReservedComponents()[0]);

        board.getReservedComponent(1);

        assertEquals(cannon, board.getHandComponent());
    }

    public RocketshipBoard createCasualRocketship(FlightType type){
        RocketshipBoard board = new RocketshipBoard(type, PlayerColor.GREEN);
        int i = 0;
        int max = type == TRIAL ? 18 : 27;
        Random random = new Random();
        UnturnedComponents unturned = new UnturnedComponents();
        while(i < max - 1){
            board.addHand(unturned.draw());
            int row = 0;
            int col = 0;
            boolean exit = false;
            boolean[][] boardOK = board.getBuildableCells();
            for(int k = 0; k < 5; k++){
                for(int kk = 0; kk < 7; kk++){
                    if(boardOK[k][kk]){
                        row = k;
                        col = kk;
                        exit = true;
                        break;
                    }
                }
                if(exit)
                    break;
            }
            try{
                board.addComponent(row, col, board.getHandComponent());
                board.removeHand();
            } catch (RuntimeException ignored) {
            }
            i++;
        }
        return board;
    }

    @Test
    public void casualRocketshipTRIAL(){
        RocketshipBoard board = createCasualRocketship(TRIAL);
        System.out.println("COMPLIANT WITH RULES: " + board.checkShipAtStart());
        List<String> boardToPrint = renderRocketshipTrial(board.getWholeShip());
        for(int lin = 0; lin < boardToPrint.size(); lin++) {
            System.out.println(boardToPrint.get(lin));
        }
    }

    @Test
    public void casualRocketshipTWO(){
        RocketshipBoard board = createCasualRocketship(TWO);
        System.out.println("COMPLIANT WITH RULES: " + board.checkShipAtStart());
        List<String> boardToPrint = renderRocketshipTwo(board.getWholeShip());
        for(int lin = 0; lin < boardToPrint.size(); lin++){
            System.out.println(boardToPrint.get(lin));
        }
    }

    @Test
    public void getRowAndColumnByARandomShip(){
        RocketshipBoard board = createCasualRocketship(TWO);
        int row = 5;
        int col = 7;
        for(int i = 0; i < row; i++) {
            System.out.print("get row by EAST:" + board.getRowOrColumn(i, EAST));
            System.out.println();
        }
        for(int i = 0; i < row; i++){
            System.out.print("get row by WEST:" + board.getRowOrColumn(i, WEST));
            System.out.println();
        }
        for(int i = 0; i < col; i++) {
            System.out.print("get column by NORTH:" + board.getRowOrColumn(i, NORTH));
            System.out.println();
        }
        for(int i = 0; i < col; i++){
            System.out.print("get column by SOUTH:" +board.getRowOrColumn(i, SOUTH));
            System.out.println();
        }
    }

    @Test
    public void getAdjacentComponentsTest(){
        RocketshipBoard board = new RocketshipBoard(TWO, PlayerColor.YELLOW);

        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());
        ThrusterComponent thruster = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{SINGLE_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString());
        ShieldComponent shield = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{SMOOTH_SURFACE, UNIVERSAL_CONNECTOR, SMOOTH_SURFACE, DOUBLE_CONNECTOR}, UUID.randomUUID().toString(), NORTH, EAST);
        board.addComponent(3,3, thruster);
        board.addComponent(2,4, shield);
        board.addComponent(2,5, cannon);

        List<String> boardToPrint = renderRocketshipTwo(board.getWholeShip());
        for(int lin = 0; lin < boardToPrint.size(); lin++) {
            System.out.println(boardToPrint.get(lin));
        }
        for(int i = 0; i < 2; i++) {
            System.out.println(board.getConnectedComponents(shield).get(i).getComponentType());
        }
        assertNotNull(board.getConnectedComponents(shield));

    }

    @Test
    public void getConnectedComponentsTest(){
        RocketshipBoard board = new RocketshipBoard(TWO, PlayerColor.YELLOW);

        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());
        StockComponent stock = new StockComponent(ComponentType.STOCK, new ConnectorType[]{UNIVERSAL_CONNECTOR, SMOOTH_SURFACE, SMOOTH_SURFACE, SMOOTH_SURFACE}, UUID.randomUUID().toString(), 2, true);
        ShieldComponent shield = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString(), NORTH, EAST);
        PowerCenterComponent power = new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{SMOOTH_SURFACE, SMOOTH_SURFACE, UNIVERSAL_CONNECTOR, SMOOTH_SURFACE}, UUID.randomUUID().toString(), 2);
        board.addComponent(2,4, shield);
        board.addComponent(2,5, cannon);
        board.addComponent(1,4, power);
        board.addComponent(3,4, stock);

        List<String> boardToPrint = renderRocketshipTwo(board.getWholeShip());
        for(int lin = 0; lin < boardToPrint.size(); lin++) {
            System.out.println(boardToPrint.get(lin));
        }
        for(int i = 0; i < 4; i++) {
            System.out.println(board.getConnectedComponents(shield).get(i).getComponentType());
        }
        assertNotNull(board.getConnectedComponents(shield));

    }

    @Test
    public void hasThatComponentOnRocketship() {
        RocketshipBoard board = new RocketshipBoard(TRIAL, PlayerColor.YELLOW);
        ThrusterComponent thruster = new ThrusterComponent(ComponentType.SINGLE_THRUSTER, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());
        DoubleThrusterComponent doubleThruster = new DoubleThrusterComponent(ComponentType.DOUBLE_THRUSTER, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());

        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());
        DoubleCannonComponent doubleCannon = new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());

        ShieldComponent shield = new ShieldComponent(ComponentType.SHIELD, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString(), NORTH, EAST);
        PowerCenterComponent power = new PowerCenterComponent(ComponentType.POWER_CENTER, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString(), 2);

        AlienAddOnComponent alienAddOnB = new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString(), CrewType.ALIEN_BROWN);
        AlienAddOnComponent alienAddOnP = new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString(), CrewType.ALIEN_PURPLE);
        SimpleCabinComponent cabinB = new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());
        SimpleCabinComponent cabinP = new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());

        board.addComponent(2,2, cannon);
        board.addComponent(2,1, doubleCannon);

        board.addComponent(3,2, thruster);
        board.addComponent(3,1, doubleThruster);

        board.addComponent(2,4, shield);
        board.addComponent(2,5, power);

        board.addComponent(1,3, cabinB);
        board.addComponent(1,4, alienAddOnB);


        board.addComponent(3,3, cabinP);
        board.addComponent(3,4, alienAddOnP);

        board.putBatteriesAndCrew();

        assertTrue(board.hasSingleThrusters());
        assertTrue(board.hasDoubleThrusters());
        assertTrue(board.hasSingleCannons());
        assertTrue(board.hasDoubleCannons());
        assertTrue(board.hasBatteries());
        assertTrue(board.hasShield(NORTH));


        List<String> boardToPrint = renderRocketshipTwo(board.getWholeShip());
        for(int lin = 0; lin < boardToPrint.size(); lin++) {
            System.out.println(boardToPrint.get(lin));
        }

        if(board.hasSingleThrusters()){
            System.out.println("Yes, it has SingleThrusters");
        }
        else{
            System.out.println("No, it doesn't have any SingleThrusters");
        }

        if(board.hasDoubleThrusters()){
            System.out.println("Yes, it has DoubleThrusters");
        }
        else{
            System.out.println("No, it doesn't have any DoubleThrusters");
        }

        if(board.hasSingleCannons()){
            System.out.println("Yes, it has SingleCannons");
        }
        else{
            System.out.println("No, it doesn't have any SingleCannons");
        }

        if(board.hasDoubleCannons()){
            System.out.println("Yes, it has DoubleCannons");
        }
        else{
            System.out.println("No, it doesn't have any DoubleCannons");
        }

        if(board.hasBatteries()){
            System.out.println("Yes, it has Batteries");
        }
        else{
            System.out.println("No, it doesn't have any Batteries");
        }

        if(board.hasShield(NORTH)){
            System.out.println("Yes, it has a shield and the side is protected ");
        }
        else{
            System.out.println("No, it does not have a shield and the side is not protected");
        }

    }

    @Test
    public void doesnthaveThatComponentOnRocketship() {
        RocketshipBoard board = new RocketshipBoard(TRIAL, PlayerColor.YELLOW);
        SimpleCabinComponent cabin = new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());

        board.addComponent(2,4, cabin);

        List<String> boardToPrint = renderRocketshipTwo(board.getWholeShip());
        for(int lin = 0; lin < boardToPrint.size(); lin++) {
            System.out.println(boardToPrint.get(lin));
        }

        if(board.hasSingleThrusters()){
            System.out.println("Yes, it has SingleThrusters");
        }
        else{
            System.out.println("No, it doesn't have any SingleThrusters");
        }

        if(board.hasDoubleThrusters()){
            System.out.println("Yes, it has DoubleThrusters");
        }
        else{
            System.out.println("No, it doesn't have any DoubleThrusters");
        }

        if(board.hasSingleCannons()){
            System.out.println("Yes, it has SingleCannons");
        }
        else{
            System.out.println("No, it doesn't have any SingleCannons");
        }

        if(board.hasDoubleCannons()){
            System.out.println("Yes, it has DoubleCannons");
        }
        else{
            System.out.println("No, it doesn't have any DoubleCannons");
        }

        if(board.hasBatteries()){
            System.out.println("Yes, it has Batteries");
        }
        else{
            System.out.println("No, it doesn't have any Batteries");
        }

        if(board.hasShield(NORTH)){
            System.out.println("Yes, it has a shield and the side is protected ");
        }
        else{
            System.out.println("No, it does not have a shield and the side is not protected");
        }

        assertFalse(board.hasSingleThrusters());
        assertFalse(board.hasDoubleThrusters());
        assertFalse(board.hasSingleCannons());
        assertFalse(board.hasDoubleCannons());
        assertFalse(board.hasBatteries());
        assertFalse(board.hasShield(NORTH));
    }

    @Test
    public void checkDefaultFirePowerTest() {
        RocketshipBoard board = new RocketshipBoard(TRIAL, PlayerColor.YELLOW);
        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());
        DoubleCannonComponent doubleCannon = new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());

        board.addComponent(2, 2, cannon);
        board.addComponent(2, 1, doubleCannon);

        List<String> boardToPrint = renderRocketshipTwo(board.getWholeShip());
        for (int lin = 0; lin < boardToPrint.size(); lin++) {
            System.out.println(boardToPrint.get(lin));
        }
        float firePower = board.checkDefaultFirePower();
        System.out.println("DefaultFirePower: " + firePower);

        assertTrue(firePower >= 0);
    }

    @Test
    public void checkDefaultFirePowerTestWithPurpleAlien() {
        RocketshipBoard board = new RocketshipBoard(TRIAL, PlayerColor.YELLOW);
        CannonComponent cannon = new CannonComponent(ComponentType.SINGLE_CANNON, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());
        DoubleCannonComponent doubleCannon = new DoubleCannonComponent(ComponentType.DOUBLE_CANNON, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());

        AlienAddOnComponent alienAddOnP = new AlienAddOnComponent(ComponentType.ALIEN_ADD_ON, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString(), CrewType.ALIEN_PURPLE);
        SimpleCabinComponent cabinP = new SimpleCabinComponent(ComponentType.SIMPLE_CABIN, new ConnectorType[]{UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR, UNIVERSAL_CONNECTOR}, UUID.randomUUID().toString());

        board.addComponent(2, 2, cannon);
        board.addComponent(2, 1, doubleCannon);

        board.addComponent(3,3, cabinP);
        board.addComponent(3,4, alienAddOnP);

        board.resetSimpleCabinSupportedAtmospheres(); //Add purple atmospheres
        System.out.println(board.getPossibleAlienOptions()); //print type of member
        board.putAlienCrew(3,3, CrewType.ALIEN_PURPLE); //Add purple member

        List<String> boardToPrint = renderRocketshipTwo(board.getWholeShip());
        for(int lin = 0; lin < boardToPrint.size(); lin++) {
            System.out.println(boardToPrint.get(lin));
        }
        float firePower = board.checkDefaultFirePower();
        System.out.println("DefaultFirePower: " + firePower);

        assertEquals(3.0, firePower, 1);
    }


    public List<String> renderRocketshipTrial(Component[][] newboard){
        List<String> myCompleteRocketship = new ArrayList<>();

        int maxLength = 13;
        int numRows = 5; // valore della prima riga
        int numCols = 4;

        boolean[][] shape = {
                {false, false, false, true, false, false, false},
                {false, false, true, true, true, false, false},
                {false, true, true, true, true, true, false},
                {false, true, true, true, true, true, false},
                {false, true, true, false, true, true, false},
        };

        for (int i = 0; i < shape.length; i++) {
            StringBuilder[] rowLines = new StringBuilder[7];
            for (int k = 0; k < rowLines.length; k++) {
                rowLines[k] = new StringBuilder();
            }

            for (int j = 0; j < shape[i].length; j++) {
                String cell;
                if(shape[i][j]){
                    cell = renderCell(newboard[i][j], maxLength);
                }
                else{
                    cell = renderEmptyCell(maxLength);
                }
                String[] lines = cell.split("\n");

                for (int k = 0; k < lines.length; k++) {
                    rowLines[k].append(lines[k]);
                }
            }

            for (int k = 0; k < rowLines.length; k++) {
                if (k % 7 == 3) {
                    myCompleteRocketship.add(numRows + rowLines[k].toString());
                    numRows++;
                }
                else {
                    myCompleteRocketship.add(" " + rowLines[k].toString());
                }
            }
        }

        StringBuilder LastLine = new StringBuilder();
        LastLine.append("  "); //due spazi, sono la spaziatura dal margine, serve per l'aggiunga dei numeri per le righe
        for(int i = 0; i <= shape.length + 1; i++){
            LastLine.append(" ".repeat((maxLength + 1) / 2)).append(numCols + i).append(" ".repeat(maxLength - maxLength / 2)); //spazi prima del numero + numero + spazi dopo
        }
        myCompleteRocketship.add(LastLine.toString());

        return myCompleteRocketship;
    }

    public List<String> renderRocketshipTwo(Component[][] newboard){
        List<String> myCompleteRocketship = new ArrayList<>();

        int maxLength = 13;
        int numRows = 5; //valore della prima colonna
        int numCols = 4;

        boolean[][] shape = {
                {false, false, true, false, true, false, false},
                {false, true, true, true, true, true, false},
                {true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true},
                {true, true, true, false, true, true, true},
        };

        for (int i = 0; i < shape.length; i++) {
            StringBuilder[] rowLines = new StringBuilder[7];
            for (int k = 0; k < rowLines.length; k++) {
                rowLines[k] = new StringBuilder();
            }

            for (int j = 0; j < shape[i].length; j++) {
                String cell;
                if(shape[i][j]){
                    cell = renderCell(newboard[i][j], maxLength);
                }
                else{
                    cell = renderEmptyCell(maxLength);
                }
                String[] lines = cell.split("\n");

                for (int k = 0; k < lines.length; k++) {
                    rowLines[k].append(lines[k]);
                }
            }


            for (int k = 0; k < rowLines.length; k++) {
                if (k % 7 == 3) {
                    myCompleteRocketship.add(numRows + " " + rowLines[k].toString());
                    numRows++;
                }
                else {
                    myCompleteRocketship.add("  " + rowLines[k].toString());
                }
            }
        }

        StringBuilder LastLine = new StringBuilder();
        LastLine.append("  "); //due spazi, sono la spaziatura dal margine, serve per l'aggiunga dei numeri per le righe
        for(int i = 0; i <= shape.length + 1; i++){
            LastLine.append(" ".repeat((maxLength + 1) / 2)).append(numCols + i).append(" ".repeat(maxLength - maxLength / 2)); //spazi prima del numero + numero + spazi dopo
        }
        myCompleteRocketship.add(LastLine.toString());

        return myCompleteRocketship;
    }

    public String renderCell(Component newPiece, int length){
        ComponentType referenceType = newPiece.getComponentType();

        String north = renameConnectors(newPiece.getConnectorByDirection(ComponentSide.NORTH));
        String south = renameConnectors(newPiece.getConnectorByDirection(ComponentSide.SOUTH));
        String west = renameConnectors(newPiece.getConnectorByDirection(ComponentSide.WEST));
        String east = renameConnectors(newPiece.getConnectorByDirection(ComponentSide.EAST));
        String type = renameType(newPiece);
        String specialLine = renderSpecialLine(newPiece);

        if (referenceType.equals(ComponentType.SINGLE_CANNON) || referenceType.equals(ComponentType.DOUBLE_CANNON)) { //lato dove punta il cannone
            switch (newPiece.getReferenceSide()) {
                case NORTH -> north = " X ";
                case SOUTH -> south = " X ";
                case WEST -> west = " X ";
                case EAST -> east = " X ";
            }
        }
        else if (referenceType.equals(ComponentType.SINGLE_THRUSTER) || referenceType.equals(ComponentType.DOUBLE_THRUSTER)) { //lato dove punta il motore
            south = " X ";
        }

        String firstLine = "╔" + "═".repeat(length) + "╗";
        String secondLine = "║" + center(north, length) + "║";
        String thirdLine = "║" + " ".repeat(length) + "║";
        String fourthLine = "║" + west + " " + center(type, length - west.length() - east.length() - 2) + " " + east + "║";
        String fifthLine = "║" + center(specialLine, length) + "║";
        String sixthLine = "║" + center(south, length) + "║";
        String lastLine = "╚" + "═".repeat(length) + "╝";

        return firstLine + "\n" + secondLine + "\n" + thirdLine + "\n" + fourthLine + "\n" + fifthLine + "\n" + sixthLine + "\n" + lastLine;
    }

    public static String renderEmptyCell(int length) {
        String emptyLine = " ".repeat(length + 2); // +2 is it to "║"
        return (emptyLine + "\n").repeat(7);
    }

    static String center(String content, int width) {
        String removeColorSpaces = content.replaceAll("\u001B\\[[\\d;]+m", "");

        int left = (width - removeColorSpaces.length()) / 2;
        int right = width - removeColorSpaces.length() - left;
        return " ".repeat(left) + content + " ".repeat(right);
    }

    public String renameConnectors(ConnectorType type) {
        return switch (type) {
            case SINGLE_CONNECTOR -> " ↑ ";
            case DOUBLE_CONNECTOR -> " ⇑ ";
            case UNIVERSAL_CONNECTOR -> " U ";
            case SMOOTH_SURFACE -> "   ";
            case EMPTY_SPACE -> " . ";
        };
    }

    public String renameType(Component newPiece) {
        String type = " . ";
        switch (newPiece.getComponentType()) {
            case EMPTY_COMPONENT:
                type = " . ";
                break;
            case PIPES:
                type = "PIPES";
                break;
            case STOCK:
                if(newPiece instanceof StockComponent stock) {
                    if(stock.isSpecial()) {
                        type = "\u001B[31mSTOCK\u001B[0m";
                    }
                    else {
                        type = "STOCK";
                    }
                };
                break;
            case SHIELD:
                type = "SHELD";
                break;
            case ALIEN_ADD_ON:
                if(newPiece instanceof AlienAddOnComponent addOn) {
                    if(addOn.getAlienColor().equals(CrewType.ALIEN_PURPLE)) {
                        type = "\u001B[38;5;135mADDON\u001B[0m";
                    }
                    else if(addOn.getAlienColor().equals(CrewType.ALIEN_BROWN)) {
                        type = "\u001B[38;5;94mADDON\u001B[0m";
                    }
                }
                break;
            case POWER_CENTER:
                type = "POWER";
                break;
            case SIMPLE_CABIN:
                type = "CABIN";
                break;
            case DOUBLE_CANNON:
                type = "2CANN";
                break;
            case SINGLE_CANNON:
                type = "1CANN";
                break;
            case STARTING_CABIN :
                if(newPiece instanceof StarterCabinComponent starter) {
                    switch(starter.getColor()) {
                        case PlayerColor.RED -> type = "\u001B[31mCABIN\u001B[0m";
                        case PlayerColor.GREEN -> type = "\u001B[32mCABIN\u001B[0m";
                        case PlayerColor.BLUE -> type = "\u001B[34mCABIN\u001B[0m";
                        case PlayerColor.YELLOW -> type = "\u001B[33mCABIN\u001B[0m";
                    }
                }
                break;
            case DOUBLE_THRUSTER:
                type = "2THRU";
                break;
            case SINGLE_THRUSTER:
                type = "1THRU";
                break;
        };
        return type;
    }

    public String renderSpecialLine(Component newPiece) {
        String renderInformation = "  ";
        ComponentType referenceType = newPiece.getComponentType();

        if (referenceType.equals(ComponentType.SHIELD) && newPiece instanceof ShieldComponent shield) {
            if(shield.protectedSides().getFirst().equals(ComponentSide.NORTH)){
                renderInformation = "N - E";
            }
            else if(shield.protectedSides().getFirst().equals(ComponentSide.EAST)){
                renderInformation = "E - S";
            }
            else if(shield.protectedSides().getFirst().equals(ComponentSide.SOUTH)){
                renderInformation = "S - W";
            }
            else if(shield.protectedSides().getFirst().equals(ComponentSide.WEST)){
                renderInformation = "W - N";
            }

        } else if (referenceType.equals(ComponentType.STOCK) && newPiece instanceof StockComponent stock) {
            int maxCapacity = stock.getMaxCapacity();
            int rendered = 0;
            StringBuilder renderCapacity = new StringBuilder();

            for (ResourceType type : stock.getStock()) {
                if (rendered < maxCapacity) {
                    if(maxCapacity == 2) {
                        renderCapacity.append(" ").append(type.toString());
                    }
                    else {
                        renderCapacity.append(type.toString());
                    }
                    rendered++;
                }
            }
            while(rendered < maxCapacity){
                if(maxCapacity == 2) {
                    renderCapacity.append(" ").append("□");
                }
                else {
                    renderCapacity.append("□");
                }
                rendered++;
            }
            renderInformation = renderCapacity.toString();
        }

        else if (referenceType.equals(ComponentType.POWER_CENTER) && newPiece instanceof PowerCenterComponent powerCenter) {
            int rendered = 0;
            int maxCapacity = powerCenter.getMaxCapacity();
            StringBuilder renderCapacity = new StringBuilder();

            for (int i = 0; i < powerCenter.getTotalResource(); i++) {
                if (rendered < maxCapacity) {
                    if(maxCapacity == 2) {
                        renderCapacity.append(" ").append("■");
                    }
                    else {
                        renderCapacity.append("■");
                    }
                    rendered++;
                }
            }
            while(rendered < maxCapacity){
                if(maxCapacity == 2) {
                    renderCapacity.append(" ").append("□");
                }
                else {
                    renderCapacity.append("□");
                }
                rendered++;
            }
            renderInformation = renderCapacity.toString();
        }
        return renderInformation;
    }

    public void fixDisconnectedShip(FlightType type){
        final RocketshipBoard board = new RocketshipBoard(type, PlayerColor.GREEN);
        int i = 0;
        int max = type == TRIAL ? 18 : 27;
        Random random = new Random();
        UnturnedComponents unturned = new UnturnedComponents();
        while(i < max - 1){
            board.addHand(unturned.draw());
            int row = 0;
            int col = 0;
            boolean exit = false;
            boolean[][] boardOK = board.getBuildableCells();
            for(int k = 0; k < 5; k++){
                for(int kk = 0; kk < 7; kk++){
                    if(boardOK[k][kk]){
                        row = k;
                        col = kk;
                        exit = true;
                        break;
                    }
                }
                if(exit)
                    break;
            }
            try{
                board.addComponent(row, col, board.getHandComponent());
                board.removeHand();
            } catch (RuntimeException ignored) {
            }
            i++;
        }
        if(type == TRIAL){
            System.out.println("COMPLIANT WITH RULES: " + board.checkShipAtStart());
            List<String> boardToPrint = renderRocketshipTrial(board.getWholeShip());
            for(int lin = 0; lin < boardToPrint.size(); lin++){
                System.out.println(boardToPrint.get(lin));
            }
        }
        else{
            System.out.println("COMPLIANT WITH RULES: " + board.checkShipAtStart());
            List<String> boardToPrint = renderRocketshipTwo(board.getWholeShip());
            for(int lin = 0; lin < boardToPrint.size(); lin++){
                System.out.println(boardToPrint.get(lin));
            }
        }

        board.fixDisconnectedPlayersRocketship();

        if(type == TRIAL){
            System.out.println("COMPLIANT WITH RULES: " + board.checkShipAtStart());
            List<String> boardToPrint = renderRocketshipTrial(board.getWholeShip());
            for(int lin = 0; lin < boardToPrint.size(); lin++){
                System.out.println(boardToPrint.get(lin));
            }
        }
        else{
            System.out.println("COMPLIANT WITH RULES: " + board.checkShipAtStart());
            List<String> boardToPrint = renderRocketshipTwo(board.getWholeShip());
            for(int lin = 0; lin < boardToPrint.size(); lin++){
                System.out.println(boardToPrint.get(lin));
            }
        }
    }

    @Test
    public void fixDisconnectedTrialShip(){
        try{
            fixDisconnectedShip(TRIAL);
        } catch (RuntimeException e) {
            if(e.getMessage().equals("Ship is still not fixed"))
                assertTrue(false);
        }
        assertTrue(true);
    }

    @Test
    public void fixDisconnectedTwoShip(){
        try{
            fixDisconnectedShip(TWO);
        } catch (RuntimeException e) {
            if(e.getMessage().equals("Ship is still not fixed"))
                assertTrue(false);
        }
        assertTrue(true);
    }

    @Test
    public void fixManyShips() {
        int i;
        for (i = 0; i < 10000; i++) {
            try {
                if (i % 2 == 0)
                    fixDisconnectedTwoShip();
                else
                    fixDisconnectedTrialShip();
            } catch (RuntimeException e) {
                if (e.getMessage().equals("Ship is still not fixed"))
                    assertTrue(false);
            }
        }
        System.out.println("Iterations: " + i);
        assertTrue(true);
    }

}
