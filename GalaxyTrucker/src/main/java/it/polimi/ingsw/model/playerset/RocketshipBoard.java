package it.polimi.ingsw.model.playerset;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.utilities.components.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * RocketshipBoard's class represents each player's personal rocketship board
 */

public class RocketshipBoard {
    private final int maxrow = 5;
    private final int maxcol = 7;
    private Component[][] field;
    private Component[] reserved;
    private List<Component> destroyed;
    private Component handComponent;
    private final FlightType flightType;
    List<int[]> availableCells = new ArrayList<>();

    private boolean purple = false;
    private boolean brown = false;

    public RocketshipBoard(FlightType flightType, PlayerColor color){
        this.flightType = flightType;
        field = new Component[maxrow][maxcol];

        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                field[i][j] = new EmptyComponent();
            }
        }

        reserved = new Component[2];
        reserved[0] = new EmptyComponent();
        reserved[1] = new EmptyComponent();

        destroyed = new LinkedList<>();
        handComponent = new EmptyComponent();

        availableCells.add(new int[]{1, 2});
        availableCells.add(new int[]{1, 3});
        availableCells.add(new int[]{1, 4});
        for (int i = 2; i < maxrow; i++){
            for (int j = 1; j < 6; j++){
                if (j != 3){
                    availableCells.add(new int[]{i, j});

                }
            }
        }
        availableCells.add(new int[]{2, 3});
        availableCells.add(new int[]{3, 3});
        switch(flightType){
            case TRIAL:
                availableCells.add(new int[]{0, 3});
                break;
            case TWO:
                availableCells.add(new int[]{0, 2});
                availableCells.add(new int[]{0, 4});
                availableCells.add(new int[]{1, 1});
                availableCells.add(new int[]{1, 5});
                for (int i = 2; i < maxrow; i++){
                    availableCells.add(new int[]{i, 0});
                    availableCells.add(new int[]{i, 6});
                }
                break;
        }
        String id = "";
        switch(color){
            case RED -> {
                id = "GT-new_tiles_16_forweb52";
            }
            case BLUE -> {
                id = "GT-new_tiles_16_forweb33";
            }
            case GREEN -> {
                id = "GT-new_tiles_16_forweb34";
            }
            case YELLOW -> {
                id = "GT-new_tiles_16_forweb61";
            }
        }
        field[2][3] = new StarterCabinComponent(ComponentType.STARTING_CABIN, new ConnectorType[]{ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR, ConnectorType.UNIVERSAL_CONNECTOR}, id, color);
    }

    /**
     * Hand represents the situation when the players draw a component from the heap. addHand() takes the component and places it into player's hand.
     * If the player has a valid component in hand the method throws an exception
     */
    public void addHand(Component newC){
        if(handComponent.getComponentType().equals(ComponentType.EMPTY_COMPONENT)){
            handComponent = newC;
        }
        else throw new HandAlreadyFullException();
    }

    /**
     * removeHand() simply discard the component card that the player's holding in his hand.
     * The method throws an exception if the player has in hand an empty component
     */
    public void removeHand(){
        if(handComponent.getComponentType().equals(ComponentType.EMPTY_COMPONENT))
            throw new EmptyHandException();
        else{
            handComponent = new EmptyComponent();
        }
    }

    /**
     * getHandComponent() returns the component that the player's holding in his hand.
     * If the player's got no component the method throws an exception
     */
    public Component getHandComponent(){
        if(handComponent.getComponentType().equals(ComponentType.EMPTY_COMPONENT))
            return new EmptyComponent();
        else{
            return handComponent;
        }
    }

    /**
     * rotateHand() gets the component in hand and proceeds to turn it clockwise one time. Throws an exception if
     */
    public void rotateHand(){
        if(!getHandComponent().getComponentType().equals(ComponentType.EMPTY_COMPONENT)){
            getHandComponent().rotateClockwise();
        }
        else throw new EmptyHandException();
    }

    /**
     * putHandInReserved() takes the hand component and puts in the reserve. If there are already two reserved components
     * it throws a RuntimeException
     */
    public void putHandInReserved(){
        if(!getHandComponent().getComponentType().equals(ComponentType.EMPTY_COMPONENT)){
            if(reserved[0].getComponentType().equals(ComponentType.EMPTY_COMPONENT)){
                reserved[0] = getHandComponent();
                removeHand();
            }
            else if(reserved[1].getComponentType().equals(ComponentType.EMPTY_COMPONENT)){
                reserved[1] = getHandComponent();
                removeHand();
            }
            else throw new RuntimeException("You can't reserve more than two components");
        }
    }

    /**
     * getReservedComponent() allows the user to put in his hand one of the two reserved components. If the hand
     * is already full an exception is thrown
     * @param choice must be 1 or 2, indicating which component needs to be taken
     */
    public void getReservedComponent(int choice){
        if(getHandComponent().getComponentType().equals(ComponentType.EMPTY_COMPONENT)){
            if(choice > 2 || choice < 1){
                throw new IllegalArgumentException("There are only two reserved components");
            }
            else{
                addHand(reserved[choice - 1]);
                reserved[choice - 1] = new EmptyComponent();
            }
        }
        else throw new HandAlreadyFullException();
    }

    /**
     * getReservedComponentType() returns the type of the requested reserved component
     * @param choice indicates which of the two reserved component we are considering
     */
    public ComponentType getReservedComponentType(int choice){
        if(choice > 2 || choice < 1){
            throw new IllegalArgumentException("There are only two reserved components");
        }
        else{
            return reserved[choice - 1].getComponentType();
        }
    }

    /**
     * returnReservedComponents() simply returns the two reserved components
     */
    public Component[] returnReservedComponents(){
        Component[] res = new Component[2];
        res[0] = reserved[0];
        res[1] = reserved[1];
        return res;
    }

    /**
     * isCellValid() determines whether a certain cell is part of the board or not (because boards are not rectangles or squares)
     * @param row is the row number of the cell
     * @param col is the column number of the cell
     * @return true if the cell is part of the board, false if that cell is not valid
     */
    public boolean isCellValid(int row, int col){

        return availableCells.stream().anyMatch(couple -> couple[0] == row && couple[1] == col);
    }

    /**
     * isCellFree() determines whether a certain valid cell is free or not
     * @param row is the row number of the cell
     * @param col is the column number of the cell
     * @return true if the cell is valid and is free, false in any other case
     */
    public boolean isCellFree(int row, int col){

        if(this.isCellValid(row, col)){
            if(field[row][col].getComponentType().equals(ComponentType.EMPTY_COMPONENT))
                return true;
        }
        return false;
    }

    /**
     * hasComponentNear() determines whether a certain valid cell has at least a component next to itself
     * @param row is the row number of the cell
     * @param col is the column number of the cell
     * @return true if the cell is valid and has a component next to it, false in any other case
     */
    private boolean hasComponentNear(int row, int col){
        if(isCellValid(row, col)){
            for(int i = row - 1; i < row + 2; i++){
                for(int j = col - 1; j < col + 2; j++){
                    if((i != row || j != col) && isCellValid(i,j) && !isCellFree(i,j) && (i == row || j == col))
                        return true;
                }
            }
            return false;
        }
        else return false;
    }

    /**
     * isCellBuildable() check if a certain cell is valid and buildable (to be buildable a cell has to have a component adjacent to it)
     * @param row is the row number of the cell
     * @param col is the column number of the cell
     * @return true if the cell is buildable, false in any other case
     */
    public boolean isCellBuildable(int row, int col){
        if(isCellValid(row, col) && hasComponentNear(row, col))
            return true;
        else
            return false;
    }

    /**
     * getComponentAt() simply returns the component card at the requested cell without removing it from the board
     * @param row is the row number of the cell
     * @param col is the column number of the cell
     * @return the component if the cell is valid and has a component on it, returns an empty component if the cell is
     * empty or not valid. Throws an exception if the cell does not physically exists
     */
    public Component getComponentAt(int row, int col){
        if(isCellValid(row, col) && !isCellFree(row, col))
            return field[row][col];
        else if((row >= 0) && (row < maxrow) && (col >= 0) && (col < maxcol)){
            return new EmptyComponent();
        }
        else throw new CellNotValidException();
    }

    /**
     * getWholeShip() returns a matrix containing a copy of the board
     * @return a double array containing all the components of the ship, even the empty ones. The component at the position
     * (i,j) will be found at result[i][j]
     */
    public Component[][] getWholeShip(){
        Component[][] ship = new Component[maxrow][maxcol];
        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                ship[i][j] = getComponentAt(i,j);
            }
        }
        return ship;
    }

    /**
     * getBuildableCells() is useful to determine where the player can place a component. If a cell is valid and free
     * and buildable (at least one component next to it) then a component can be placed on it
     * @return a matrix of booleans. If the cell at the position (i,j) is buildable then result[i][j] is true, otherwise
     * is false
     */
    public boolean[][] getBuildableCells(){
        boolean[][] ship = new boolean[maxrow][maxcol];

        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                ship[i][j] = false;
            }
        }

        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                if(isCellBuildable(i,j) && isCellFree(i,j))
                    ship[i][j] = true;
            }
        }

        return ship;
    }

    /**
     * getRowOrColumn() returns either an entire column or an entire row, depending on the specified side
     * @param rc is the number of row/column we want to get
     * @param side represents from which side we want to take the row/column
     * @return a List containing all the components of row or column
     */
    public List<Component> getRowOrColumn(int rc, ComponentSide side){
        if(side.equals(ComponentSide.NORTH) || side.equals(ComponentSide.SOUTH)){
            List<Component> column = new LinkedList<>();
            for(int i = 0; i < maxrow; i++){
                Component currentComponent = field[i][rc];
                if(!currentComponent.getComponentType().equals(ComponentType.EMPTY_COMPONENT)) {
                    column.add(currentComponent);
                }
            }
            return column;
        } // EAST OR WEST
        else{
            List<Component> row = new LinkedList<>();
            for(int i = 0; i < maxcol; i++){
                Component currentComponent = field[rc][i];
                if(!currentComponent.getComponentType().equals(ComponentType.EMPTY_COMPONENT)) {
                    row.add(currentComponent);
                }
            }
            return row;
        }
    }

    /**
     * addComponent() places the component on the board at the designated cell. It throws an exception if the cell is
     * not buildable or not free
     * @param row is the row number of the cell
     * @param col is the column number of the cell
     * @param c is the component itself
     */
    public void addComponent(int row, int col, Component c){
        if(isCellFree(row, col) && isCellBuildable(row, col)){
            field[row][col] = c;
        }
        else if(isCellFree(row, col)){
            throw new CellNotBuildableException();
        }
        else if(isCellValid(row, col)){
            throw new CellAlreadyTakenException();
        }
        else throw new RuntimeException();
    }

    /**
     * removeComponent() ensures that the cell is valid, taken by an actual component (different from the start cabin).
     * If these conditions are granted it removes the component. Otherwise, it throws an exception.
     * The removed component will be added to destroyed component's list
     * @param row is the row number of the cell we want to remove
     * @param col is the column number of the cell we want to remove
     */
    public void removeComponent(int row, int col){
        if(!isCellFree(row, col) /* && (row != 2 || col != 3)*/){
            Component removed = field[row][col];
            destroyed.add(removed);
            field[row][col] = new EmptyComponent();
        }
        //else if(!isCellFree(row, col)){
            //throw new RuntimeException("Central cabin can't be removed"); UPDATED TO ALLOW REMOVAL OF CENTRAL CABIN
        //}
        else throw new RuntimeException("This cell is already free");
    }


    public int countComponentsDestroyedDuringFlight() {
        return destroyed.size();
    }

    /**
     * hasComponent() scans the whole board to find if a certain component is present on it
     * @param c is the component we want to find
     * @return true if the board has the component, false otherwise
     */
    private boolean hasComponent(Component c){
        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                if(c.equals(field[i][j]))
                    return true;
            }
        }
        return false;
    }

    /**
     * getCoordinates() return an array of two int containing the coordinates of the component if present,
     * otherwise throws an exception
     * @param c is the component which coordinates we want to get
     * @return an array of two int, first element is the row number, second element is the column number
     */
    public int[] getCoordinates(Component c){
        if(hasComponent(c)){
            int[] result = new int[2];
            for(int i = 0; i < maxrow; i++){
                for(int j = 0; j < maxcol; j++){
                    if(field[i][j].equals(c)){
                        result[0] = i;
                        result[1] = j;
                    }
                }
            }
            return result;
        }
        else throw new RuntimeException("Component is not on the board");

    }

    /**
     * getComponentByType() returns a list containing all the component on the board of a specified type
     * @param type is the type of components we want to create a list with
     * @return a list with all the components of that type
     */
    public List<Component> getComponentByType(ComponentType type){
        List<Component> result = new ArrayList<>();
        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                if(isCellValid(i, j) && !isCellFree(i, j)){
                    if(field[i][j].getComponentType().equals(type)){
                        result.add(getComponentAt(i,j));
                    }
                }
            }
        }
        return result;
    }

    /**
     * getAdjacentComponents() returns a list containing all the components on the board adjacent to the specified one
     * @param component is the component of which we want to know adjacent ones
     * @return a list containing all the adjacent components
    */
    public List<Component> getAdjacentComponents(Component component) {
        Map<Component, ComponentSide> map = getAdjacentComponentsAndSides(component);
        return new ArrayList<>(map.keySet());
    }

    /**
     * getAdjacentComponent() returns the component adjacent to the specified one on the specified side
     * @param component is the component of which we want to know the adjacent one on that side
     */
    public Component getAdjacentComponent(Component component, ComponentSide side){
        if(hasComponent(component)){
            int i = getCoordinates(component)[0];
            int j = getCoordinates(component)[1];
            switch(side){
                case NORTH -> {
                    if(isCellValid(i - 1, j) && !isCellFree(i - 1, j)){
                        return getComponentAt(i - 1, j);
                    }
                }
                case EAST -> {
                    if(isCellValid(i, j + 1) && !isCellFree(i, j + 1)){
                        return getComponentAt(i, j + 1);
                    }
                }
                case SOUTH -> {
                    if(isCellValid(i + 1, j) && !isCellFree(i + 1, j)){
                        return getComponentAt(i + 1, j);
                    }
                }
                case WEST -> {
                    if(isCellValid(i, j - 1) && !isCellFree(i, j - 1)){
                        return getComponentAt(i, j - 1);
                    }
                }
            }
            return new EmptyComponent();
        }
        else throw new RuntimeException("No such component on the board");
    }

    /**
     * getAdjacentComponentsAndSides() returns a map containing all the components on the board adjacent to the specified
     * one paired to their side of connection
     * @param component is the component of which we want to know the adjacent ones and their side with respect to the connection
     * @return a mapo containing each adiacent component and his position in respect to the central component
     */
    public Map<Component, ComponentSide> getAdjacentComponentsAndSides(Component component){
        if(hasComponent(component)){
            try{
                int[] coords = getCoordinates(component);
                Map<Component, ComponentSide> result = new HashMap<>();
                ComponentSide side = null;

                for(int i = coords[0] - 1; i < coords[0] + 2; i++){
                    for(int j = coords[1] - 1; j < coords[1] + 2; j++){
                        if((i != coords[0] || j != coords[1]) && !isCellFree(i,j) && (i == coords[0] || j == coords[1])){
                            try{
                                if(i == coords[0]) {
                                    if(j == coords[1] - 1) {
                                        side = ComponentSide.WEST;
                                    }
                                    else if(j == coords[1] + 1) {
                                        side = ComponentSide.EAST;
                                    }
                                }
                                else if(j == coords[1]) {
                                    if(i == coords[0] - 1) {
                                        side = ComponentSide.NORTH;
                                    }
                                    else if (i == coords[0] + 1) {
                                        side = ComponentSide.SOUTH;
                                    }
                                }

                                if (side != null) {
                                    result.put(getComponentAt(i, j), side);
                                }
                            }
                            catch (Exception e){
                                System.out.println(e.getMessage());
                            }
                        }

                    }
                }
                return result;

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }

        }
        else throw new RuntimeException("Component is not on the board");

    }


    /**
     * getConnectedComponents() simply returns a list of all the components adjacent and connected on the board to the specified one.
     * If an adjacent component is not properly connected to the central one it will not be included in the list
     * @param component is the component of which we want to know adjacent and connected ones
     * @return a list with all the adjacent and connected components
     */
    public List<Component> getConnectedComponents(Component component){
        if(hasComponent(component)){
            try{
                Map<Component, ComponentSide> adjacentComponents = getAdjacentComponentsAndSides(component);
                List<Component> result = new ArrayList<>();
                for(Component c: adjacentComponents.keySet()){
                    if(adjacentComponents.get(c).equals(ComponentSide.NORTH)){
                        if(component.getConnectorByDirection(ComponentSide.NORTH).connects(c.getConnectorByDirection(ComponentSide.SOUTH))){
                            result.add(c);
                        }
                    }
                    else if(adjacentComponents.get(c).equals(ComponentSide.EAST)){
                        if(component.getConnectorByDirection(ComponentSide.EAST).connects(c.getConnectorByDirection(ComponentSide.WEST))){
                            result.add(c);
                        }
                    }
                    else if(adjacentComponents.get(c).equals(ComponentSide.SOUTH)){
                        if(component.getConnectorByDirection(ComponentSide.SOUTH).connects(c.getConnectorByDirection(ComponentSide.NORTH))){
                            result.add(c);
                        }
                    }
                    else if(adjacentComponents.get(c).equals(ComponentSide.WEST)){
                        if(component.getConnectorByDirection(ComponentSide.WEST).connects(c.getConnectorByDirection(ComponentSide.EAST))){
                            result.add(c);
                        }
                    }
                }
                return result;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        else throw new RuntimeException("Component is not on the board");
    }


    /**
     * getAdjacentConnector returns the connector immediately adjacent to the specified side of the specified component. If the
     * adjacent cell is empty or not valid the connector returned will be an empty space.
     * @return the connector of the adjacent component in the specified direction
     * @throws Exception if the specified component is not on the board
     */
    public ConnectorType getAdjacentConnector(Component c, ComponentSide side){
        int i = getCoordinates(c)[0];
        int j = getCoordinates(c)[1];
        switch(side){
            case NORTH -> {
                if(isCellValid(i - 1, j) && !isCellFree(i - 1, j)){
                    return getComponentAt(i - 1, j).getConnectorByDirection(ComponentSide.SOUTH);
                }
                else{
                    return ConnectorType.EMPTY_SPACE;
                }
            }
            case EAST -> {
                if(isCellValid(i, j + 1) && !isCellFree(i, j + 1)){
                    return getComponentAt(i, j + 1).getConnectorByDirection(ComponentSide.WEST);
                }
                else{
                    return ConnectorType.EMPTY_SPACE;
                }
            }
            case SOUTH -> {
                if(isCellValid(i + 1, j) && !isCellFree(i + 1, j)){
                    return getComponentAt(i + 1, j).getConnectorByDirection(ComponentSide.NORTH);
                }
                else{
                    return ConnectorType.EMPTY_SPACE;
                }
            }
            case WEST -> {
                if(isCellValid(i, j - 1) && !isCellFree(i, j - 1)){
                    return getComponentAt(i, j - 1).getConnectorByDirection(ComponentSide.EAST);
                }
                else{
                    return ConnectorType.EMPTY_SPACE;
                }
            }
        }
        throw new RuntimeException("You're not supposed to see this ;)");
    }


    /**
     * countExposedConnectors() checks, for every card in the board, how many exposed connectors it has. If the card next to the one is
     * placed increases the count if the checked card has a connector and the other one has a smooth side. If the card next to the one is empty,
     * or it is not a valid position, the count is increased if the card has a connector on that side
     * @return an int representing the number of exposed connectors
     */
    public int countExposedConnectors(){
        int count = 0;

        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                if(isCellValid(i,j) && !isCellFree(i,j)){
                    ConnectorType[] connectors = field[i][j].listConnectors();

                    //North connector
                    if(isCellValid(i - 1,j) && !isCellFree(i - 1,j)){
                        if(getComponentAt(i - 1, j).getConnectorByDirection(ComponentSide.SOUTH).equals(ConnectorType.SMOOTH_SURFACE) && !connectors[0].equals(ConnectorType.SMOOTH_SURFACE))
                            count++;
                    }
                    else{
                        if(!connectors[0].equals(ConnectorType.SMOOTH_SURFACE))
                            count++;
                    }

                    //East connector
                    if(isCellValid(i,j + 1) && !isCellFree(i, j + 1)){
                        if(getComponentAt(i, j + 1).getConnectorByDirection(ComponentSide.WEST).equals(ConnectorType.SMOOTH_SURFACE) && !connectors[1].equals(ConnectorType.SMOOTH_SURFACE))
                            count++;
                    }
                    else{
                        if(!connectors[1].equals(ConnectorType.SMOOTH_SURFACE))
                            count++;
                    }

                    //South connector
                    if(isCellValid(i + 1,j) && !isCellFree(i + 1,j)){
                        if(getComponentAt(i + 1, j).getConnectorByDirection(ComponentSide.NORTH).equals(ConnectorType.SMOOTH_SURFACE) && !connectors[2].equals(ConnectorType.SMOOTH_SURFACE))
                            count++;
                    }
                    else{
                        if(!connectors[2].equals(ConnectorType.SMOOTH_SURFACE))
                            count++;
                    }

                    //West connector
                    if(isCellValid(i,j - 1) && !isCellFree(i, j-1)){
                        if(getComponentAt(i, j - 1).getConnectorByDirection(ComponentSide.EAST).equals(ConnectorType.SMOOTH_SURFACE) && !connectors[3].equals(ConnectorType.SMOOTH_SURFACE))
                            count++;
                    }
                    else{
                        if(!connectors[3].equals(ConnectorType.SMOOTH_SURFACE))
                            count++;
                    }

                }
            }
        }
        return count;
    }

    /**
     * checkTotalCrew() at first generates a list containing all the cabins (both starting and simple) and then
     * calculates the number of residents for each one
     * @return an int representing the total number of crew member on the board
     */
    public int checkTotalCrew(){
        List<Component> comp = getComponentByType(ComponentType.STARTING_CABIN);
        comp.addAll(getComponentByType(ComponentType.SIMPLE_CABIN));

        return comp.stream().map(c -> ((HousingUnit) c).getNumberOfResidents()).reduce(0, Integer::sum);
    }

    /**
     * checkTotalThruster() returns the maximum power of the ship, considering activated all the double thrusters
     */
    /*
    public int checkTotalThrusters(){
        List<Component> comp = getComponentByType(ComponentType.SINGLE_THRUSTER);
        comp.addAll(getComponentByType(ComponentType.DOUBLE_THRUSTER));

        return comp.stream().map(c -> ((ThrusterComponent) c).getThrusterPower()).reduce(0, Integer::sum);
    }

     */

    /**
     * hasSingleThrusters() checks whether the player has at least one single thruster component on their rocketship
     * @return true if the player has at least a single thruster, false otherwise
     */
    public boolean hasSingleThrusters() {
        List<Component> comp = getComponentByType(ComponentType.SINGLE_THRUSTER);
        if(comp.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * hasSingleThrusters() checks whether the player has at least one double thruster component on their rocketship
     * @return true if the player has at least a double thruster, false otherwise
     */
    public boolean hasDoubleThrusters() {
        List<Component> comp = getComponentByType(ComponentType.DOUBLE_THRUSTER);
        if(comp.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * hasSingleThrusters() checks whether the player has at least one single cannon component on their rocketship
     * @return true if the player has at least a single cannon, false otherwise
     */
    public boolean hasSingleCannons() {
        List<Component> comp = getComponentByType(ComponentType.SINGLE_CANNON);
        if(comp.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * hasSingleThrusters() checks whether the player has at least one double cannon component on their rocketship
     * @return true if the player has at least a double cannon, false otherwise
     */
    public boolean hasDoubleCannons() {
        List<Component> comp = getComponentByType(ComponentType.DOUBLE_CANNON);
        if(comp.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * hasSingleThrusters() checks whether the player has at least one battery on their rocketship
     * @return true if the player has at least a battery, false otherwise
     */
    public boolean hasBatteries() {
        int numberOfBatteries = checkTotalBatteries();

        if(numberOfBatteries > 0) {
            return true;
        } else {
            return false;
        }
    }







    /**
     * checkTotalBatteries() returns the number of the batteries on the board (of course not considering the ones used
     * to power up a double component)
     */
    public int checkTotalBatteries(){
        List<Component> comp = getComponentByType(ComponentType.POWER_CENTER);

        return comp.stream().map( c -> ((PowerCenterComponent) c).getTotalResource()).reduce(0, Integer::sum);
    }

    /**
     * hasShield() checks if the ship has a shield (yet to be activated) on the specified side
     * @param side is the side we want to know if it's covered
     * @return true if a shield is able to defend that side, false otherwise
     */
    public boolean hasShield(ComponentSide side){
        List<Component> comp = getComponentByType(ComponentType.SHIELD);

        return comp.stream().flatMap(c -> ((ShieldComponent) c).protectedSides().stream()).anyMatch(s -> s.equals(side));
    }

    /**
     * checkSingleCannonsFirePower() checks the firepower conferred by the single thrusters alone
     * @return the thrust power of single thrusters only
     */
    public float checkSingleCannonsFirePower() {
        List<Component> singleCannons = getComponentByType(ComponentType.SINGLE_CANNON);

        float firePower = 0;
        firePower = firePower + singleCannons.stream().map(c -> ((CannonComponent) c).getFirePower()).reduce(0f, Float::sum);

        return firePower;
    }

    /**
     * checkDefaultFirePower() checks the firepower that would be in effect in case no battery were to be used
     * @return firepower in case no battery were to be used
     */
    public float checkDefaultFirePower() { // FIRE POWER IF NO BATTERIES ARE USED
        float defaultFirePower = checkSingleCannonsFirePower();

        if(defaultFirePower > 0) {
            if(hasPurpleAlien()) {
                defaultFirePower = defaultFirePower + 2;
            }
        }
        return defaultFirePower;
    }

    /**
     * checkActivatableFirePower() checks the firepower that would be in effect in case x batteries were to be used. no battery is actually consumed
     * @param x is the number of batteries that are used
     * @return thrust power in case x batteries were to be used
     */
    public float checkActivatableFirePower(int x) { // FIRE POWER IF X BATTERIES ARE USED
        float singleCannonFirePower = checkSingleCannonsFirePower();

        List<Component> doubleCannons = getComponentByType(ComponentType.DOUBLE_CANNON);

        List<Component> topXCannons = doubleCannons.stream()
                .sorted(Comparator.comparingDouble(c -> ((DoubleCannonComponent) c).getFirePower()))
                .limit(x)
                .collect(Collectors.toList());

        float availableDoubleCannonContribution = topXCannons.stream().map(c -> ((DoubleCannonComponent) c).getFirePower()).reduce(0f, Float::sum);

        float activatableFirePower = singleCannonFirePower + availableDoubleCannonContribution;


        if(activatableFirePower > 0) {
            if(hasPurpleAlien()) {
                float purpleAlienFirePowerContribution = 2;
                activatableFirePower += purpleAlienFirePowerContribution;
            }
        }

        return activatableFirePower;
    }


    /**
     * checkMaxFirePower() checks the firepower that would be in effect in case all batteries were to be used. no battery is actually consumed
     * @return firepower in case all available batteries were to be used
     */
    public float checkMaxFirePower() {
        int totalBatteries = checkTotalBatteries();

        float maxFirePower = checkActivatableFirePower(totalBatteries);

        return maxFirePower;
    }

    /**
     * checkAvailableDoubleCannons() is used for counting the number of double cannons present on the rocketship
     * @return the number of double cannons
     */
    public int checkAvailableDoubleCannons() {
        List<Component> doubleCannons = getComponentByType(ComponentType.DOUBLE_CANNON);
        int totalBatteries = checkTotalBatteries();
        List<Component> availableCannons = doubleCannons.stream()
                .limit(totalBatteries)
                .collect(Collectors.toList());
        return availableCannons.size();
    }


    /**
     * hasBrownAlien() checks if the rocketship is housing a purple alien
     * @return true if present, false otherwise
     */
    public boolean hasPurpleAlien() {
        List<Component> simpleCabins = getComponentByType(ComponentType.SIMPLE_CABIN);
        return simpleCabins.stream().flatMap(c -> ((HousingUnit) c).getResidents().stream()).anyMatch(cm -> cm.getCrewType() == CrewType.ALIEN_PURPLE);
    }


    /**
     * checkSingleThrustersThrustPower() checks the thrust power conferred by the single thrusters alone
     * @return the thrust power of single thrusters only
     */
    public float checkSingleThrustersThrustPower() {
        List<Component> singleThrusters = getComponentByType(ComponentType.SINGLE_THRUSTER);

        float thrustPower = 0;
        thrustPower = thrustPower + singleThrusters.stream().map(c -> ((ThrusterComponent) c).getThrustPower()).reduce(0, Integer::sum);

        return thrustPower;
    }


    /**
     * checkDefaultThrustPower() checks the thrust power that would be in effect in case no battery were to be used
     * @return thrust power in case no battery were to be used
     */
    public float checkDefaultThrustPower() { // THRUST POWER IF NO BATTERIES ARE USED
        float defaultThrustPower = checkSingleThrustersThrustPower();

        if(defaultThrustPower > 0) {
            if(hasBrownAlien()) {
                defaultThrustPower = defaultThrustPower + 2;
            }
        }
        return defaultThrustPower;
    }

    /**
     * checkActivatableThrustPower() checks the thrust power that would be in effect in case x batteries were to be used. no battery is actually consumed
     * @param x is the number of batteries that are used
     * @return thrust power in case x batteries were to be used
     */
    public float checkActivatableThrustPower(int x) { // THRUST POWER IF X BATTERIES ARE USED
        float singleThrustersThrustPower = checkSingleThrustersThrustPower();

        List<Component> doubleThrusters = getComponentByType(ComponentType.DOUBLE_THRUSTER);

        List<Component> availableThrusters = doubleThrusters.stream()
                .limit(x)
                .collect(Collectors.toList());

        float availableDoubleThrustersContribution = availableThrusters.stream().map(c -> ((DoubleThrusterComponent) c).getThrustPower()).reduce(0, Integer::sum);

        float activatableThrustPower = singleThrustersThrustPower + availableDoubleThrustersContribution;


        if(activatableThrustPower > 0) {
            if(hasBrownAlien()) {
                float brownAlienThrustPowerContribution = 2;
                activatableThrustPower += brownAlienThrustPowerContribution;
            }
        }

        return activatableThrustPower;
    }

    /**
     * checkMaxThrustPower() checks the thrust power that would be in effect in case all batteries were to be used. no battery is actually consumed
     * @return thrust power in case all available batteries were to be used
     */
    public float checkMaxThrustPower() {
        int totalBatteries = checkTotalBatteries();

        float maxThrustPower = checkActivatableThrustPower(totalBatteries);

        return maxThrustPower;
    }


    /**
     * checkAvailableDoubleThrusters() is used for counting the number of double thrusters present on the rocketship
     * @return the number of double thrusters
     */
    public int checkAvailableDoubleThrusters() {
        List<Component> doubleThrusters = getComponentByType(ComponentType.DOUBLE_THRUSTER);
        int totalBatteries = checkTotalBatteries();
        List<Component> availableThrusters = doubleThrusters.stream()
                .limit(totalBatteries)
                .collect(Collectors.toList());
        return availableThrusters.size();
    }


    /**
     * hasBrownAlien() checks if the rocketship is housing a brown alien
     * @return true if present, false otherwise
     */
    public boolean hasBrownAlien() {
        List<Component> simpleCabins = getComponentByType(ComponentType.SIMPLE_CABIN);
        return simpleCabins.stream().flatMap(c -> ((HousingUnit) c).getResidents().stream()).anyMatch(cm -> cm.getCrewType() == CrewType.ALIEN_BROWN);
    }


    /**
     * removeCubeByPrice() removes one cube of the most expensive type that is present of the ship
     */
    public void removeCubeByPrice() {
        try {
            List<StockComponent> stocks = getComponentByType(ComponentType.STOCK).stream()
                    .map(component -> (StockComponent) component)
                    .toList();

            ResourceTypes[] priorities = {
                    ResourceTypes.REDCUBE,
                    ResourceTypes.YELLOWCUBE,
                    ResourceTypes.GREENCUBE,
                    ResourceTypes.BLUECUBE
            };

            for (ResourceTypes resource : priorities) {
                StockComponent stock = stocks.stream()
                        .filter(s -> s.isThereTypeObject(resource))
                        .findFirst().orElse(null);
                if (stock != null) {
                    stock.removeResource(resource);
                    return;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * removeBatteries() removes a specified number of batteries from the rocketship
     * @param num indicates the number of batteries to remove
     * @throws Exception
     */
    public void removeBatteries(int num) throws Exception {
        List<PowerCenterComponent> powerCenters = getComponentByType(ComponentType.POWER_CENTER).stream()
                .map(c -> (PowerCenterComponent) c).toList();

        for(int i = 0; i < num; i++){
            PowerCenterComponent toRemoveFrom = powerCenters.stream()
                    .filter(pc -> pc.getTotalResource() > 0)
                    .findFirst()
                    .orElseThrow(() -> new NoBatteriesLeftToRemoveException());
            toRemoveFrom.removeResource();
        }
    }

    /**
     * removeCrewmates() removes a specified number of crew members from the rocketship
     * @param num indicates the number of players to remove
     */
    public void removeCrewmates(int num) {
        try {
            List<HousingUnit> simpleCabins = getComponentByType(ComponentType.SIMPLE_CABIN).stream()
                    .map(c -> (HousingUnit) c)
                    .toList();

            HousingUnit starterCabin = (HousingUnit) (getComponentByType(ComponentType.STARTING_CABIN)).get(0);

            for (int j = 0; j < num; j++) {

                HousingUnit toRemoveFrom = simpleCabins.stream().filter(hu -> hu.getNumberOfResidents() > 0).findFirst().orElse(null);
                if (toRemoveFrom != null) {
                    toRemoveFrom.removeOneCrewMember();
                } else {
                    if (starterCabin.getNumberOfResidents() > 0) {
                        starterCabin.removeOneCrewMember();
                    } else {
                        System.out.println("no crewmates left to remove"); // [] andrebbe in realtà loggato il player
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * smuggle() removes a number of cubes from the stock components of the rocketship, starting from the most valuable.
     * this is in accordance with the smugglers adventure card, since it's the very method that is called by the card
     * @param penalty indicates the number of cubes to remove
     */
    public void smuggle(int penalty) {
        try {

            List<Component> stocks = getComponentByType(ComponentType.STOCK);

            boolean allFree = stocks.stream()
                    .allMatch(c -> ((StockComponent) c).getTotalResource() == 0);
            if(allFree) {
                System.out.println("no cubes to remove");
            }


            List<PowerCenterComponent> powerCenters = getComponentByType(ComponentType.POWER_CENTER).stream()
                    .map(c -> (PowerCenterComponent) c).toList();


            for (int i = 0; i < penalty; i++) {
                if (allFree) {
                    PowerCenterComponent toRemoveFrom = powerCenters.stream()
                            .filter(pc -> pc.getTotalResource() > 0)
                            .findFirst()
                            .orElse(null);

                    if (toRemoveFrom != null) {
                        System.out.println("removing battery");
                        toRemoveFrom.removeResource();
                    } else {
                        System.out.println("no batteries to remove");
                        break;
                    }
                } else {
                    System.out.println("removing most valuable cube");
                    removeCubeByPrice();
                    allFree = stocks.stream()
                            .allMatch(c -> ((StockComponent) c).getTotalResource() == 0);
                    if(allFree) {
                        System.out.println("no cubes to remove left");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * checkConnectors() scans the whole ship to find if some components has non-matching connectors with the
     * connectors next to it
     * @return true if every component on the ship has matching (or exposed) connectors, false if there's a
     * component with at least a non-matching side
     */
    public boolean checkConnectors(){
        boolean up = false, down = false, left = false, right = false;
        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                if(isCellValid(i,j) && !isCellFree(i,j)){
                    up = getAdjacentConnector(getComponentAt(i,j), ComponentSide.NORTH).matches(getComponentAt(i,j).getConnectorByDirection(ComponentSide.NORTH));
                    down = getAdjacentConnector(getComponentAt(i,j), ComponentSide.SOUTH).matches(getComponentAt(i,j).getConnectorByDirection(ComponentSide.SOUTH));
                    left = getAdjacentConnector(getComponentAt(i,j), ComponentSide.WEST).matches(getComponentAt(i,j).getConnectorByDirection(ComponentSide.WEST));
                    right = getAdjacentConnector(getComponentAt(i,j), ComponentSide.EAST).matches(getComponentAt(i,j).getConnectorByDirection(ComponentSide.EAST));

                    if( !(up && down && left && right) )
                        return false;
                }
            }
        }
        return true;
    }


    /**
     * setSimpleCabinSupportedAtmospheres() is in charge of updating, for each simple cabin, which color of alien is
     * currently accepted
     */
    public void resetSimpleCabinSupportedAtmospheres() {
        List<Component> simpleCabins = getComponentByType(ComponentType.SIMPLE_CABIN);

        List<Component> connectedComponents = new LinkedList<>();
        for(Component currentCabin : simpleCabins) {
            try{
                connectedComponents = getConnectedComponents(currentCabin);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            Set<CrewType> supportedAtmospheres = new HashSet<>();
            for (Component currentComponent : connectedComponents) {
                if(currentComponent.getComponentType() == ComponentType.ALIEN_ADD_ON) {
                    AlienAddOnComponent alienAddOnComponent = (AlienAddOnComponent) currentComponent;
                    supportedAtmospheres.add(alienAddOnComponent.getAlienColor());
                }
            }

            ((SimpleCabinComponent) currentCabin).
                    updateSupportedAtmospheres(new ArrayList<>(supportedAtmospheres));

        }
    }


    /**
     * checkIntegrity() scans the whole ship to determine if there are parts of it not connected to the others.
     * @return a list that contains the subgroups of connected components. For example, if there are three groups of
     * components without link to each other this method returns a list with the three sets of components.
     * The ship is integrous if the size of the returned list is one, otherwise not every component is connected to any
     * other one
     */
    public List<HashSet<Component>> checkIntegrity() {
        Graph graph = new Graph();

        for (int i = 0; i < maxrow; i++) {
            for (int j = 0; j < maxcol; j++) {
                if (isCellValid(i, j) && !isCellFree(i, j)) {

                    Component here = getComponentAt(i, j);
                    graph.addNode(here);

                    // NORD
                    if (isCellValid(i - 1, j) && !isCellFree(i - 1, j)) {
                        Component north = getComponentAt(i - 1, j);
                        if (here.getConnectorByDirection(ComponentSide.NORTH)
                                .connects(north.getConnectorByDirection(ComponentSide.SOUTH))) {
                            graph.addArch(here, north);
                        }
                    }
                    // EST
                    if (isCellValid(i, j + 1) && !isCellFree(i, j + 1)) {
                        Component east = getComponentAt(i, j + 1);
                        if (here.getConnectorByDirection(ComponentSide.EAST)
                                .connects(east.getConnectorByDirection(ComponentSide.WEST))) {
                            graph.addArch(here, east);
                        }
                    }
                    // SUD
                    if (isCellValid(i + 1, j) && !isCellFree(i + 1, j)) {
                        Component south = getComponentAt(i + 1, j);
                        if (here.getConnectorByDirection(ComponentSide.SOUTH)
                                .connects(south.getConnectorByDirection(ComponentSide.NORTH))) {
                            graph.addArch(here, south);
                        }
                    }
                    // OVEST
                    if (isCellValid(i, j - 1) && !isCellFree(i, j - 1)) {
                        Component west = getComponentAt(i, j - 1);
                        if (here.getConnectorByDirection(ComponentSide.WEST)
                                .connects(west.getConnectorByDirection(ComponentSide.EAST))) {
                            graph.addArch(here, west);
                        }
                    }
                }
            }
        }

        ConnectedComponents cc = new ConnectedComponents();
        List<HashSet<Component>> componentsGroupings = cc.findConnectedComponents(graph);
        prioritizeStartingCabinGroup(componentsGroupings);
        return componentsGroupings;
    }

    /**
     * prioritizeStartingCabinGroup() sorts the subgraphs of components by simply moving the subgraph that contains the starting cabin to the first position in the list.
     * the reason behind this is to have the automatic removal of all but one subgroups make more sense in case a player is disconnected
     * @param componentsGroupings
     */
    public void prioritizeStartingCabinGroup(List<HashSet<Component>> componentsGroupings) {
        for (int i = 0; i < componentsGroupings.size(); i++) {
            for (Component c : componentsGroupings.get(i)) {
                if (c.getComponentType() == ComponentType.STARTING_CABIN) {
                    // swap the set at index i with the set at index 0
                    if (i != 0) {
                        HashSet<Component> temp = componentsGroupings.get(0);
                        componentsGroupings.set(0, componentsGroupings.get(i));
                        componentsGroupings.set(i, temp);
                    }
                    return; // found and moved the group with the starting cabin, if present
                }
            }
        }
    }

    /**
     * checkRules() scans the cells in front of a cannon or behind a thruster. If those cells are not empty the ship
     * is not built right.
     * @return true if all the cells in front of cannons and behind thrusters are empty, false otherwise
     */
    private boolean checkRules(){
        List<Component> cannons = getComponentByType(ComponentType.SINGLE_CANNON);
        cannons.addAll(getComponentByType(ComponentType.DOUBLE_CANNON));

        List<Component> thrusters = getComponentByType(ComponentType.SINGLE_THRUSTER);
        thrusters.addAll(getComponentByType(ComponentType.DOUBLE_THRUSTER));

        for(Component cannon: cannons){
            ComponentSide side = ((CannonComponent) cannon).getCannonSide();
            Component infront = getAdjacentComponent(cannon, side);

            if(!infront.getComponentType().equals(ComponentType.EMPTY_COMPONENT))
                return false;
        }

        for(Component thruster: thrusters){
            Component infront = getAdjacentComponent(thruster, ComponentSide.SOUTH);

            if(!infront.getComponentType().equals(ComponentType.EMPTY_COMPONENT))
                return false;
        }

        return true;
    }

    /**
     * checkShipAtStart() checks that every component is linked up correctly, that the rules are respected, that
     * the ship is integrous (no connected components) and that there are not more instances of the same component
     * @return true if the ship is compliant with the rules, false otherwise
     */
    public boolean checkShipAtStart() {
        boolean connectors = false, integrity = false, rules = false, redudant = true;

        connectors = checkConnectors();

        List<HashSet<Component>> connectedcomponents = checkIntegrity();

        if(connectedcomponents.size() == 1)
            integrity = true;

        rules = checkRules();

        redudant = hasRepeatedComponents();

        return (connectors && integrity && rules && !redudant);
    }

    /**
     * checkShipEverytime() checks if the ship is integer (no connected components) and if there's more than
     * one instance of the same component
     * @return true if the ship is compliant with the rules, false otherwise
     */
    public boolean checkShipEverytime(){
        boolean integrity = false, redundant = true;

        List<HashSet<Component>> connectedcomponents = checkIntegrity();

        if(connectedcomponents.size() == 1) {
            integrity = true;
        }

        redundant = hasRepeatedComponents();

        System.out.println("integrity: " + integrity + ", redundant: " + redundant);

        return (integrity && !redundant);
    }

    /**
     * fixDisconnectedRocketship() is useful to fix the ship of a user that disconnected himself during takeoff phase
     */
    public void fixDisconnectedPlayersRocketship(){
        if(!checkShipAtStart()){
            boolean integrity = false, rules = false, redudant = true;

            List<HashSet<Component>> connectedcomponents = checkIntegrity();

            if(connectedcomponents.size() == 1)
                integrity = true;

            redudant = hasRepeatedComponents();

            if(redudant){
                throw new RuntimeException("Redundant components");
            }

            if(!integrity){
                autoFixIntegrity(connectedcomponents);
            }

            while(!checkConnectors()){
                autoFixConnectors();
            }

            rules = checkRules();

            if(!rules){
                autoFixRules();
            }

            if(!checkShipAtStart()){
                List<HashSet<Component>> cc = checkIntegrity();
                if(!(cc.size() == 1)){
                    autoFixIntegrity(cc);
                }
            }

            if(!checkShipAtStart())
                throw new RuntimeException("Ship is still not fixed");
        }
    }

    /**
     * autoFixRules() is capable of automatically modify a ship in order to make it compliant to the rules
     */
    private void autoFixRules(){
        List<Component> cannons = getComponentByType(ComponentType.SINGLE_CANNON);
        cannons.addAll(getComponentByType(ComponentType.DOUBLE_CANNON));

        List<Component> thrusters = getComponentByType(ComponentType.SINGLE_THRUSTER);
        thrusters.addAll(getComponentByType(ComponentType.DOUBLE_THRUSTER));

        List<Component> wasted = new LinkedList<>();

        for(Component cannon: cannons){
            if(!wasted.contains(cannon)){
                ComponentSide side = ((CannonComponent) cannon).getCannonSide();
                Component infront = getAdjacentComponent(cannon, side);

                if(!infront.getComponentType().equals(ComponentType.EMPTY_COMPONENT)){
                    try {
                        removeComponent(getCoordinates(infront)[0], getCoordinates(infront)[1]);
                        wasted.add(infront);
                    } catch (RuntimeException e) {
                        if (e.getMessage().equals("Central cabin can't be removed"))
                            removeComponent(getCoordinates(cannon)[0], getCoordinates(cannon)[1]);
                        wasted.add(cannon);
                    }
                }
            }
        }

        for(Component thruster: thrusters){
            if(!wasted.contains(thruster)){
                Component infront = getAdjacentComponent(thruster, ComponentSide.SOUTH);

                if(!infront.getComponentType().equals(ComponentType.EMPTY_COMPONENT)){
                    try {
                        removeComponent(getCoordinates(infront)[0], getCoordinates(infront)[1]);
                        wasted.add(infront);
                    } catch (RuntimeException e) {
                        if (e.getMessage().equals("Central cabin can't be removed"))
                            removeComponent(getCoordinates(thruster)[0], getCoordinates(thruster)[1]);
                        wasted.add(thruster);
                    }
                }
            }
        }
    }

    /**
     * autoFixIntegrity() automatically removes all the components not connected to the central cabin
     */
    private void autoFixIntegrity(List<HashSet<Component>> connectedcomponents){
        for(int i = 0; i < connectedcomponents.size(); i++){
            for(Component c : connectedcomponents.get(i)){
                if(c.getComponentType().equals(ComponentType.STARTING_CABIN)){
                    chooseBrokenShip(i);
                    return;
                }
            }
        }
        throw new RuntimeException("No component contains the starting cabin");
    }


    /**
     * autoFixConnectors() automatically removes all the components not matching with their near components
     */
    private void autoFixConnectors(){
        boolean up = false, down = false, left = false, right = false;
        List<Component> wrong = new LinkedList<>();
        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                if(isCellValid(i,j) && !isCellFree(i,j)){
                    up = getAdjacentConnector(getComponentAt(i,j), ComponentSide.NORTH).matches(getComponentAt(i,j).getConnectorByDirection(ComponentSide.NORTH));
                    down = getAdjacentConnector(getComponentAt(i,j), ComponentSide.SOUTH).matches(getComponentAt(i,j).getConnectorByDirection(ComponentSide.SOUTH));
                    left = getAdjacentConnector(getComponentAt(i,j), ComponentSide.WEST).matches(getComponentAt(i,j).getConnectorByDirection(ComponentSide.WEST));
                    right = getAdjacentConnector(getComponentAt(i,j), ComponentSide.EAST).matches(getComponentAt(i,j).getConnectorByDirection(ComponentSide.EAST));

                    if( !(up && down && left && right) ){
                        if(!up)
                            wrong.add(getAdjacentComponent(getComponentAt(i, j), ComponentSide.NORTH));
                        if(!down)
                            wrong.add(getAdjacentComponent(getComponentAt(i, j), ComponentSide.SOUTH));
                        if(!left)
                            wrong.add(getAdjacentComponent(getComponentAt(i, j), ComponentSide.WEST));
                        if(!right)
                            wrong.add(getAdjacentComponent(getComponentAt(i, j), ComponentSide.EAST));
                    }

                }
            }
        }
        int[] occurrences = new int[wrong.size()];
        HashSet<Component> wrongSet = new HashSet<>();
        HashMap<Component, Integer> wrongMap = new HashMap<>();
        for(int i = 0; i < wrong.size(); i++){
            boolean added = wrongSet.add(wrong.get(i));
            if(added){
                wrongMap.put(wrong.get(i), 1);
                occurrences[i] = 1;
            }
            else {
                int times = wrongMap.get(wrong.get(i));
                wrongMap.put(wrong.get(i), times + 1);
                occurrences[i] = times;
            }
        }
        int maxindex = -1;
        int maxelem = -1;
        for(int j = 0; j < wrong.size(); j++){
            if(!wrong.get(j).getComponentType().equals(ComponentType.STARTING_CABIN)){
                if(occurrences[j] > maxelem) {
                    maxelem = occurrences[j];
                    maxindex = j;
                }
            }
        }
        if(maxindex != -1){
            removeComponent(getCoordinates(wrong.get(maxindex))[0], getCoordinates(wrong.get(maxindex))[1]);
        }
        else throw new RuntimeException();
    }

    /**
     * getNumberOfConnectedComponents() returns the total number of parts in which the ship is broken into
     */
    public int getSizeOfSetOfConnectedComponents(){
        return checkIntegrity().size();
    }

    /**
     * getAllComponentsID() returns a list containing all the IDs of components on the board
     */
    private List<String> getAllComponentsID(){
        List<Component> comps = new LinkedList<>();

        for(int i = 0; i < maxrow; i++){
            for(int j = 0; j < maxcol; j++){
                try{
                    if(!isCellFree(i,j)) { // ADDED LATER: PURPOSE IS TO IGNORE EMPTY COMPONENTS, THAT WERE ALL CONSIDERED ONE COMPONENT
                        Component c = getComponentAt(i,j);
                        if(c.getComponentType() != ComponentType.EMPTY_COMPONENT) {
                            comps.add(c);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Something is wrong");
                }
            }
        }

        return comps.stream().map(c -> c.getID()).toList();
    }

    /**
     * hasRepeatedComponents() check if the ship mistakenly has a specific component more than once
     * @return true if the ship has the same component more than once, false otherwise
     */
    private boolean hasRepeatedComponents() {
        List<String> idcomp = getAllComponentsID();

        HashSet<String> compset = new HashSet<>(idcomp);

        boolean isRedundant = !(compset.size() == idcomp.size());

        return isRedundant;
    }

    /**
     * getPossibleAlienOptions() returns for every component which type of crew it can get
     * @return a List containing all the simple cabin and an integer. 0 if the cabin can store humans, 1 purple, 2 brown,
     * 3 purple or brown
     */
    public List<Map<Component, Integer>> getPossibleAlienOptions(){
        List<Component> simpleCabins = getComponentByType(ComponentType.SIMPLE_CABIN);
        List<Map<Component,Integer>> result = new LinkedList<>();

        for(Component c: simpleCabins){
            SimpleCabinComponent scc = (SimpleCabinComponent) c;
            Map<Component, Integer> link = new HashMap<>();
            if(scc.getSupportedAtmospheres().contains(CrewType.ALIEN_PURPLE) && scc.getSupportedAtmospheres().contains(CrewType.ALIEN_BROWN)){
                link.put(c,3);
                result.add(link);
            }
            else if(scc.getSupportedAtmospheres().contains(CrewType.ALIEN_PURPLE)){
                link.put(c,1);
                result.add(link);
            }
            else if(scc.getSupportedAtmospheres().contains(CrewType.ALIEN_BROWN)){
                link.put(c,2);
                result.add(link);
            }
            else{
                link.put(c,0);
                result.add(link);
            }
        }

        return result;
    }

    /**
     * putBatteriesAndCrew() is in charge of putting on the ship batteries and human crew on the starting cabin
     */
    public void putBatteriesAndCrew(){
        List<Component> battery = getComponentByType(ComponentType.POWER_CENTER);

        for(Component c: battery){
            int capacity = ((PowerCenterComponent) c).getMaxCapacity();
            for(int i = 0; i < capacity; i++){
                ((PowerCenterComponent) c).addResource(new BatteryToken());
            }
        }

        List<Component> starting = getComponentByType(ComponentType.STARTING_CABIN);
        StarterCabinComponent startCabin = (StarterCabinComponent) starting.getFirst();
        startCabin.addOneCrewMember(new CrewMember(CrewType.HUMAN));
        startCabin.addOneCrewMember(new CrewMember(CrewType.HUMAN));
    }

    /**
     * putAlienCrew() puts a crew member of the specified type on the selected component
     * @return an int representing which category of crew has been added. 0 for humans, 1 for purple, 2 for brown
     */
    public int putAlienCrew(int row, int col, CrewType type){
        Component c = getComponentAt(row,col);
        SimpleCabinComponent scc = (SimpleCabinComponent) c;
        int outcome = -1;

        if((type.equals(CrewType.ALIEN_BROWN) && brown) || (type.equals(CrewType.ALIEN_PURPLE) && purple)){
            type = CrewType.HUMAN;
            outcome = 0;
        }


        if(!scc.getSupportedAtmospheres().contains(type)){
            type = CrewType.HUMAN;
            outcome = 0;
        }


        if(type.equals(CrewType.HUMAN)){
            scc.addOneCrewMember(new CrewMember(type));
            scc.addOneCrewMember(new CrewMember(type));
            outcome = 0;
        }
        else {
            scc.addOneCrewMember(new CrewMember(type));
            switch(type){
                case ALIEN_PURPLE -> {
                    purple = true;
                    outcome = 1;
                }
                case ALIEN_BROWN -> {
                    brown = true;
                    outcome = 2;
                }
            }
        }
        return outcome;
    }

    /**
     * chooseBrokenShip() receives an int representing which of the connected parts of the ship the user wants
     * to save and proceeds to remove all the components that belong to other part(s)
     * @param choice represent which part of the ship is saved by user. The parameter must be between 0 and
     * the number of connected parts
     */
    public void chooseBrokenShip(int choice){
        List<HashSet<Component>> cc = checkIntegrity();

        if(choice < cc.size()){
            for(int i = 0; i < cc.size(); i++){
                if(i != choice){
                    HashSet<Component> erase = cc.get(i);

                    for(Component c: erase){
                        int[] coords = getCoordinates(c);
                        removeComponent(coords[0], coords[1]);
                    }
                }
            }
        }
        else throw new RuntimeException("The ship has not this many connected components");
    }

    /**
     * method that adds the cubes received from a card to a player's rocketship. If there isn't enough space
     * for a cube, it throws away the less precious ones
     * @param cubes array of cubes the program will have to go through
     */
    public void addCubes(CubeToken[] cubes) throws Exception {
        ResourceTypes[] reversePriorities = {
                ResourceTypes.BLUECUBE,
                ResourceTypes.GREENCUBE,
                ResourceTypes.YELLOWCUBE,
                ResourceTypes.REDCUBE
        };

        List<StockComponent> stocks =  getComponentByType(ComponentType.STOCK).stream()
                .map(component -> (StockComponent) component)
                .toList();
        if(stocks.isEmpty()) return;

        List<StockComponent> specialStocks = stocks.stream()
                .filter(StockComponent::isSpecial)
                .toList();
        for (CubeToken cube : cubes) {
            ResourceTypes cubeType = cube.getType();

            StockComponent availableStock = stocks.stream()
                    .filter(stock ->
                            !stock.isFull() &&
                                    (stock.isSpecial() || (!stock.isSpecial() && !cubeType.equals(ResourceTypes.REDCUBE)))
                    )
                    .findFirst()
                    .orElse(null);

            if (availableStock != null) {
                availableStock.addResource(cube);
                continue;
            }

            // IF NO STOCK IS AVAILABLE
            List<StockComponent> searchList = cubeType.equals(ResourceTypes.REDCUBE) ? specialStocks : stocks;

            StockComponent stockToRemoveFrom = null;

            for (ResourceTypes priorityType : reversePriorities) {
                stockToRemoveFrom = searchList.stream()
                        .filter(sc -> sc.isThereTypeObject(priorityType))
                        .findFirst()
                        .orElse(null);

                if (stockToRemoveFrom != null) {
                    try {
                        stockToRemoveFrom.removeResource(priorityType);
                        stockToRemoveFrom.addResource(cube);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            //if no cube to remove was found => do nothing. it's as if the cube to add was discarded
            if (stockToRemoveFrom == null) {
                continue;
            }

            //all of this to avoid throwing exception if the rocketship is full of cubes that cant be removed
        }
    }

    /**
     * getLimitComponent() gets the first component that is encountered in a specific line, crossing the rocketship and starting from the edge
     * @param rc is the int that specifies the row or column where the limit component has to be taken from
     * @param side specifies the side of the rocketship from which the limit component has to be taken from
     * @return returns a reference to the component to remove from the rocketship
     */
    public Component getLimitComponent(int rc, ComponentSide side){
        List<Component> target = getRowOrColumn(rc, side);

        Component toRemove = null;
        if(target != null && !target.isEmpty()){
            switch (side) {
                case NORTH, WEST -> {
                    toRemove = target.getFirst();
                }
                case SOUTH, EAST -> {
                    toRemove = target.getLast();
                }
            }
        }
        return toRemove;
    }

    /**
     * getValueOfCubes() scans the whole ship counting all the cubes and their associated value
     * @return the sum of all the values of the cubes in the ship
     */
    public int getValueOfCubes(){
        return getComponentByType(ComponentType.STOCK).stream().map(c -> ((StockComponent) c).getValueOfStock()).reduce(0, (a, b) -> a + b);
    }
}






































