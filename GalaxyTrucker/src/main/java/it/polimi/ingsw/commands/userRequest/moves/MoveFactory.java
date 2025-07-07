package it.polimi.ingsw.commands.userRequest.moves;

import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.MoveType;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.components.Component;

public class MoveFactory {

    /**
     * createChoosePlanetMove() allows the user to choose on which planet he wants to land
     * @return a ChoosePlanetMove that indicates on which planet the user is going to land
     */
    public static FlightChoiceMove createFlightChoiceMove(int choice) {
        return new FlightChoiceMove(GamePhase.FLIGHT_PHASE, MoveType.FLIGHT_CHOICE, choice);
    }

    /**
     * createFetchUnturnedComponentMove() allows the user to pick a random component
     * @return a FetchUnturnedComponentMove that simply indicates that the user wants to pick a random
     * unturned component
     */
    public static FetchUnturnedComponentMove createFetchUnturnedComponentMove(){
        return new FetchUnturnedComponentMove(GamePhase.ASSEMBLY_PHASE, MoveType.FETCH_UNTURNED);
    }

    /**
     * createFetchUnturnedComponentMove() allows the user to pick a specific turned component
     * @return a FetchTurnedComponentMove that indicates which component the user wants to pick
     */
    public static FetchTurnedComponentMove createFetchTurnedComponentMove(String ID){
        return new FetchTurnedComponentMove(GamePhase.ASSEMBLY_PHASE, MoveType.FETCH_TURNED, ID);
    }

    /**
     * createPutInReservedMove() allows the user to put the component that he's holding in hand in the reserved section
     * @return a PutInReservedMove that simply indicates that the component the player's holding is going to be
     * put in the reserved section
     */
    public static PutInReservedMove createPutInReservedMove(){
        return new PutInReservedMove(GamePhase.ASSEMBLY_PHASE, MoveType.PUT_IN_RESERVED);
    }

    /**
     * createFetchFromReservedMove() allows the user to put in his hand one of the reserved components
     * @return a FetchFromReservedMove that indicates which component the user wants to pick
     */
    public static FetchFromReservedMove createFetchFromReservedMove(int choice){
        return new FetchFromReservedMove(GamePhase.ASSEMBLY_PHASE, MoveType.FETCH_FROM_RESERVED, choice);
    }

    /**
     * createPlaceComponentMove() allows the user to place the component that he's holding in his hand
     * into the specified place
     * @return a PlaceComponentMove that allows the user to place a component in a specified place of the board
     */
    public static PlaceComponentMove createPlaceComponentMove(int row, int col){
        return new PlaceComponentMove(GamePhase.ASSEMBLY_PHASE, MoveType.PLACE_COMPONENT, row, col);
    }

    /**
     * createDetachComponentMove() allows the user to remove a component during the takeoff phase in order to fix the
     * ship
     * @return a DetachComponentMove that allows the user to remove a component from the ship
     */
    public static DetachComponentMove createDetachComponentMove(int row, int col){
        return new DetachComponentMove(GamePhase.TAKEOFF_PHASE, MoveType.DETACH_COMPONENT, row, col);
    }

    public static FlipHourglassMove createFlipHourglassMove() {
        return new FlipHourglassMove(GamePhase.ASSEMBLY_PHASE, MoveType.FLIP_HOURGLASS);
    }

    /**
     * createReadyForTakeOffMove() is used by the user to tell that he's ready for the takeoff
     * @return a ReadyForTakeOffMove, meaning that the construction phase is over
     */
    public static ReadyForTakeOffMove createReadyForTakeOffMove(){
        return new ReadyForTakeOffMove(GamePhase.ASSEMBLY_PHASE, MoveType.READY_FOR_TAKEOFF);
    }

    /**
     * createRotateComponentMove() allows the user to rotate clockwise the component he's holding in hand
     * @return a RotateComponentMove, meaning that the user wants to rotate the component he's holding
     */
    public static RotateComponentMove createRotateComponentMove(){
        return new RotateComponentMove(GamePhase.ASSEMBLY_PHASE, MoveType.ROTATE_COMPONENT);
    }

    /**
     * createComponentRejectedMove() allows the user to reject a component by putting it back on the board
     * @return a ComponentRejectedMove, meaning that the user wants to give back his hand component
     */
    public static ComponentRejectedMove createComponentRejectedMove(){
        return new ComponentRejectedMove(GamePhase.ASSEMBLY_PHASE, MoveType.REJECT_COMPONENT);
    }


    public static QuitGameMove createQuitGameMove() {
        return new QuitGameMove(GamePhase.END_FLIGHT_PHASE, MoveType.QUIT_GAME);
    }

    public static ForfeitMove createForfeitMove() {
        return new ForfeitMove(GamePhase.FLIGHT_PHASE, MoveType.FORFEIT);
    }

    public static PopulateShipMove createPopulateShipMove(String choice){
        return new PopulateShipMove(GamePhase.TAKEOFF_PHASE, MoveType.POPULATE_SHIP, choice);
    }

    public static RequestUpdatedRocketshipMove createRequestUpdatedRocketshipMove(PlayerColor color, GamePhase gamePhase) {
        return new RequestUpdatedRocketshipMove(gamePhase, MoveType.REQUEST_UPDATED_ROCKETSHIP, color);
    }

}





























