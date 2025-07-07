package it.polimi.ingsw.client;

import it.polimi.ingsw.commands.serverResponse.deltas.*;
import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.utilities.adventure.AdventureCard;
import it.polimi.ingsw.model.utilities.components.Component;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.components.EmptyComponent;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.*;

public class ViewModel extends Observable {

    private UIType uiType;
    private View view;

    private FlightType type;

    private final int maxrow = 5;
    private final int maxcol = 7;

    private List<String> logs;
    private int myCredits;

    private GameProgression progression;
    private GamePhase phase;

    private PlayerColor myColor;
    private Component[][] myBoard;
    private Component[] myReserved;
    private Component myHand;


    private Map<PlayerColor, Component[][]> othersBoards;
    private Map<PlayerColor, Component[]> othersReserved;
    private Map<PlayerColor, Component> othersHands;

    private AdventureCard drawnCard;

    private List<Component> turned;

    private List<PlayerColor> flightBoard;

    private Map<PlayerColor, Map<CreditsType, Float>> credits;
    private List<Map.Entry<PlayerColor, Float>> ranking;


    public ViewModel(UIType type){
        this.myColor = null;
        this.logs = new ArrayList<String>();
        this.myBoard = new Component[5][7];
        this.myReserved = new Component[2];
        this.myHand = new EmptyComponent();

        this.othersBoards = new HashMap<>();
        this.othersReserved = new HashMap<>();
        this.othersHands = new HashMap<>();

        turned = new LinkedList<>();

        this.uiType = type;

        this.drawnCard = null;

        this.ranking = new LinkedList<>();
    }

    public PlayerColor getMyColor(){
        return myColor;
    }

    public UIType getUiType(){
        return uiType;
    }

    public View getView(){
        return view;
    }

    public GamePhase getGamePhase() {
        return phase;
    }

    public boolean checkColorInGame(PlayerColor color) {
        return (othersBoards.containsKey(color) || myColor == color);
    }

    public void addView(View v){
        view = v;
    }


    public void addLogEntry(String log) { //NB: metodo chiamato da ServerHandler
        this.logs.add(log);
    }

    public void updateView(Map<DeltaType, Delta> deltas) { //NB: metodo chiamato da ServerHandler

        for (Delta d : deltas.values()) {
            switch (d.getDeltaType()) {
                case ROCKETSHIP_DELTA -> {

                    System.out.println("received rocketship delta");

                    RocketshipDelta rsd = (RocketshipDelta) d;
                    PlayerColor rsColor = rsd.getPlayerColor();
                    Component[][] updatedBoard = rsd.getBoard();

                    if (rsColor == myColor) {
                        myBoard = updatedBoard;
                        setChanged();
                        notifyObservers(new UpdateMessage(DeltaType.ROCKETSHIP_DELTA, myBoard, false, null));
                    } else {
                        othersBoards.put(rsColor, updatedBoard);
                        setChanged();
                        notifyObservers(new UpdateMessage(DeltaType.ROCKETSHIP_DELTA, updatedBoard, true, rsColor));
                    }
                }

                case RESERVED_DELTA -> {
                    ReservedDelta rd = (ReservedDelta) d;
                    PlayerColor rColor = rd.getPlayerColor();
                    Component[] updatedReserved = rd.getReserved();

                    if (rColor == myColor) {
                        myReserved = updatedReserved;
                        setChanged();
                        notifyObservers(new UpdateMessage(DeltaType.RESERVED_DELTA, myReserved, false, null));
                    } else {
                        othersReserved.put(rColor, updatedReserved);
                        setChanged();
                        notifyObservers(new UpdateMessage(DeltaType.RESERVED_DELTA, updatedReserved, true, rColor));
                    }
                }

                case HAND_DELTA -> {
                    HandDelta hd = (HandDelta) d;
                    PlayerColor hColor = hd.getPlayerColor();
                    Component updatedHand = hd.getHand();

                    if (hColor.equals(myColor)) {
                        myHand = updatedHand;
                        setChanged();
                        notifyObservers(new UpdateMessage(DeltaType.HAND_DELTA, myHand, false, null));
                    } else {
                        othersHands.put(hColor, updatedHand);
                        setChanged();
                        notifyObservers(new UpdateMessage(DeltaType.HAND_DELTA, updatedHand, true, hColor));
                    }
                }

                case TURNED_DELTA -> {
                    TurnedDelta td = (TurnedDelta) d;
                    turned = td.getTurned();
                    setChanged();
                    notifyObservers(new UpdateMessage(DeltaType.TURNED_DELTA, turned, false, null));
                }

                case PHASE_DELTA -> {
                    PhaseDelta pd = (PhaseDelta) d;
                    phase = pd.getPhase();
                    setChanged();
                    notifyObservers(new UpdateMessage(DeltaType.PHASE_DELTA, phase, false, null));
                }

                case PROGRESSION_DELTA -> {
                    ProgressionDelta pd = (ProgressionDelta) d;
                    progression = pd.getProgression();
                    setChanged();
                    notifyObservers(new UpdateMessage(DeltaType.PROGRESSION_DELTA, progression, false, null));
                }

                case FLIGHT_TYPE_DELTA -> {
                    FlightTypeDelta ftd = (FlightTypeDelta) d;
                    type = ftd.getType();
                    setChanged();
                    notifyObservers(new UpdateMessage(DeltaType.FLIGHT_TYPE_DELTA, type, false, null));
                }

                case ADVENTURE_DELTA -> {
                    AdventureDelta ad = (AdventureDelta) d;
                    drawnCard = ad.getDrawnCard();
                    setChanged();
                    notifyObservers(new UpdateMessage(DeltaType.ADVENTURE_DELTA, drawnCard, false, null));
                }

                case FLIGHT_BOARD_DELTA -> {
                    FlightBoardDelta fbd = (FlightBoardDelta) d;
                    flightBoard = fbd.getFlightBoard();
                    setChanged();
                    notifyObservers(new UpdateMessage(DeltaType.FLIGHT_BOARD_DELTA, flightBoard, false, null));
                }

                case COLOR_DELTA -> {
                    ColorDelta cd = (ColorDelta) d;
                    myColor = cd.getColor();
                    setChanged();
                    notifyObservers(new UpdateMessage(DeltaType.COLOR_DELTA, myColor, false, null));
                }

                case OTHER_COLOR_DELTA -> {
                    OtherColorDelta ocd = (OtherColorDelta) d;
                    List<PlayerColor> otherColors = ocd.getOtherColors();
                    for(PlayerColor pc: otherColors){
                        setChanged();
                        notifyObservers(new UpdateMessage(DeltaType.OTHER_COLOR_DELTA, pc, false, null));
                    }
                }

                case PEEKABLE_CARDS_DELTA -> {
                    PeekableCardsDelta pcd = (PeekableCardsDelta) d;
                    List<AdventureCard> one = pcd.getCardStack(1);
                    List<AdventureCard> two = pcd.getCardStack(2);
                    List<AdventureCard> three = pcd.getCardStack(3);
                    List<List<AdventureCard>> cards = new ArrayList<>();
                    cards.add(one);
                    cards.add(two);
                    cards.add(three);
                    setChanged();
                    notifyObservers(new UpdateMessage(DeltaType.PEEKABLE_CARDS_DELTA, cards, false, null));
                }

                case BACKUP_DELTA -> {
                    BackupDelta bd = (BackupDelta) d;
                    myColor = bd.getMyColor();
                    setChanged();
                    notifyObservers(new UpdateMessage(DeltaType.BACKUP_DELTA, bd, false, null));
                }

                case RANKING_DELTA -> {
                    RankingDelta rd = (RankingDelta) d;
                    credits = rd.getCredits();
                    ranking = rd.getRanking();

                    if(uiType.equals(UIType.GUI)){
                        GUI gui = (GUI) view;
                        gui.processAllScores(rd);
                    }

                    setChanged();
                    notifyObservers(new UpdateMessage(DeltaType.RANKING_DELTA, rd, false, null));
                }
            }
        }
    }

    /**
     * getFlightType() returns FlightType if it is already been specified, throws an exception otherwise
     */
    public FlightType getFlightType(){
        if(type instanceof FlightType){
            return type;
        }
        else throw new RuntimeException("Flight Type not currently specified");
    }



    public void switchView(){
        setChanged();
        notifyObservers(new UpdateMessage(DeltaType.VIEW_DELTA, null, false, null));
    }

    public void viewMyRocketship() {
        setChanged();
        notifyObservers(new UpdateMessage(DeltaType.VIEW_DELTA, myColor, false, null));
    }

    public void viewRocketship(String color) {
        setChanged();
        switch(color) {
            case "RED" -> {
                notifyObservers(new UpdateMessage(DeltaType.VIEW_DELTA, PlayerColor.RED, false, null));
            }
            case "BLUE" -> {
                notifyObservers(new UpdateMessage(DeltaType.VIEW_DELTA, PlayerColor.BLUE, false, null));
            }
            case "YELLOW" -> {
                notifyObservers(new UpdateMessage(DeltaType.VIEW_DELTA, PlayerColor.YELLOW, false, null));
            }
            case "GREEN" -> {
                notifyObservers(new UpdateMessage(DeltaType.VIEW_DELTA, PlayerColor.GREEN, false, null));
            }
        }
    }



    public void displayCardStack(int choice){
        if(type.equals(FlightType.TWO)){
            setChanged();
            notifyObservers(new UpdateMessage(DeltaType.PEEK_A_STACK_DELTA, choice, false, null));
        }
    }

    public void stopPeeking(){
        if(type.equals(FlightType.TWO)){
            setChanged();
            notifyObservers(new UpdateMessage(DeltaType.STOP_PEEKING_DELTA, null, false, null));
        }
    }

    /**
     * getTurnedID() returns the ID of the turned component at the specified index
     */
    public String getTurnedID(int index){
        if(index < turned.size()){
            return turned.get(index).getID();
        }
        else throw new IllegalArgumentException("Index should be between 1 and turned list's size");
    }

    public void reconnectToGame(String game){
        if(uiType.equals(UIType.GUI)) {
            GUI gui = (GUI) view;
            gui.onReconnect(game);
        }
    }

    public void setLastLog(String log){
        if(uiType.equals(UIType.GUI)) {
            GUI gui = (GUI) view;
            gui.setLastLog(log);
            gui.pushLog(log);
        }
    }

    public void setFixedShip(){
        if(uiType.equals(UIType.GUI)) {
            GUI gui = (GUI) view;
            gui.setFixedShip();
        }
        else{
            TUI tui = (TUI) view;
            tui.setFixedShip();
        }
    }

    public void setNotCompliant(){
        if(uiType.equals(UIType.TUI)) {
            TUI tui = (TUI) view;
            tui.setNotCompliant();
            tui.updateTUI();
        }
    }

    public void setPaused(){
        if(uiType.equals(UIType.GUI)) {
            GUI gui = (GUI) view;
            gui.setPaused();
            Platform.runLater(() -> {
                Stage stage = gui.getStage(); // supponendo tu abbia accesso
                if (stage.isShowing()) {
                    gui.startPaused();
                } else {
                    stage.setOnShown(e -> gui.startPaused());
                }
            });
            gui.updateGUI();
        }
    }

    public void setNotPaused(){
        if(uiType.equals(UIType.GUI)) {
            GUI gui = (GUI) view;
            gui.setNotPaused();
            gui.updateGUI();
        }
    }

    public void setToPopulate(){
        if(uiType.equals(UIType.GUI)){
            GUI gui = (GUI) view;
            gui.setToPopulate();
            gui.updateGUI();
        }
    }

    public void setPopulated(){
        if(uiType.equals(UIType.GUI)){
            GUI gui = (GUI) view;
            gui.setPopulated();
            gui.updateGUI();
        }
    }

    public void assemblyOver(){
        if(uiType.equals(UIType.GUI)){
            GUI gui = (GUI) view;
            gui.assemblyOver();
            gui.updateGUI();
        }
    }

    public void setFinalNumberOfPlayers(){
        if(uiType.equals(UIType.GUI)){
            GUI gui = (GUI) view;
            gui.setFinalNumberOfPlayers(othersBoards.size());
        }
    }

    public void addFinalScore(PlayerColor color, double[] score){
        if(uiType.equals(UIType.GUI)){
            GUI gui = (GUI) view;
            gui.addFinalScore(color, score);
        }
    }



}
