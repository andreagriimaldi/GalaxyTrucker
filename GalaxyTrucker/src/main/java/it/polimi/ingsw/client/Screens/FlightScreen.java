package it.polimi.ingsw.client.Screens;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.GUI;
import it.polimi.ingsw.enums.ComponentSide;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.components.Component;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.net.URL;
import java.util.Objects;

import static it.polimi.ingsw.enums.FlightType.TRIAL;

public class FlightScreen {
    private Stage stage;

    private int Width;
    private int Height;

    private final ClientController controller;

    private final PrintWriter writer;

    private final GUI gui;
    private final Scene scene;
    private final VBox root;

    private Timeline refreshTimeline;

    private final TextArea logArea;

    private String previousLog;

    private boolean watchingRocketship;

    private boolean flightboardHasArrived;

    public FlightScreen(Stage stage, GUI gui, PrintWriter writer, ClientController c, int W, int H){
        this.gui = gui;
        this.writer = writer;
        this.controller = c;
        this.Width = W;
        this.Height = H;
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        String cssPath = "/it/polimi/ingsw/GUI/style.css";
        String cssFile = getClass().getResource(cssPath).toExternalForm();

        if (cssFile != null) {
            root.getStylesheets().add(cssFile);
        } else {
            System.err.println("CSS file not found at " + cssPath);
        }

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefWidth(250);
        logArea.setPrefHeight(800);

        previousLog = null;

        this.stage = stage;

        scene = new Scene(root, Width, Height);
    }

    public Scene getScene(){
        return scene;
    }

    public void updateGUI(){
        if(gui.getRenderRS()){
            displayRocketship(gui.getCurrentShip());
            gui.setRenderRs();
        }
        if(!watchingRocketship){
            flightboardHasArrived = gui.getFlightBoardCreated();
            setupMainView();
        }
        updateLogArea();
    }

    public void setupMainView() {
        watchingRocketship = false;
        root.getChildren().clear();

        BorderPane bp = new BorderPane();
        bp.setPrefSize(Width, Height);

        ImageView fb = createFlightBoard();
        double scale = 0.8;
        double scaledWidth = fb.getImage().getWidth() * scale;
        fb.setFitWidth(scaledWidth);
        fb.setPreserveRatio(true);

        double S = 30;

        Group flightBoardGroup = new Group(fb);
        if(flightboardHasArrived) {
            double[][] coords;
            if (gui.getType().equals(FlightType.TWO)) {
                coords = new double[][]{
                        {203.2, 124.0}, {265.6, 96.8}, {327.2, 87.2}, {387.2, 78.4},
                        {450.4, 79.2}, {516.0, 86.4}, {576.0, 100.8}, {638.4, 127.2},
                        {692.8, 162.4}, {729.6, 220.8}, {723.2, 296.8}, {682.4, 350.4},
                        {628.8, 387.2}, {565.6, 409.6}, {504.8, 421.6}, {444.0, 427.2},
                        {380.0, 425.6}, {314.4, 418.4}, {253.6, 404.0}, {194.4, 380.8},
                        {144.0, 340.8}, {99.2, 280.0}, {102.4, 208.0}, {152.8, 161.6}
                };
            } else {
                coords = new double[][]{
                        {207.2, 99.2}, {279.2, 75.2}, {351.2, 64.0},
                        {429.6, 65.6}, {502.4, 73.6}, {575.2, 99.2},
                        {643.2, 144.0}, {676.0, 226.4}, {636.0, 299.2},
                        {569.6, 339.2}, {499.2, 361.6}, {424.0, 372.0},
                        {347.2, 373.6}, {276.0, 361.6}, {203.2, 337.6},
                        {136.0, 292.0}, {100.8, 211.2}, {142.4, 137.6}};
            }

            int position = 0;
            for (double[] p : coords) {
                double x = p[0], y = p[1];
                Polygon marker = new Polygon(
                        0, -S / 2,
                        -S / 2, S / 2,
                        S / 2, S / 2
                );

                PlayerColor currentCell = gui.getFlightBoard().get(position);
                if (currentCell == null) {
                    marker.setFill(Color.TRANSPARENT);
                    marker.setStroke(Color.TRANSPARENT);
                } else {
                    marker.setFill(setColor(currentCell));
                    marker.setStroke(setColor(currentCell));
                }
                position++;

                marker.setTranslateX(x);
                marker.setTranslateY(y);

                flightBoardGroup.getChildren().add(marker);
            }
        }

        Pane flightBoardPane = new Pane(flightBoardGroup);
        bp.setLeft(flightBoardPane);

        ImageView card = createCurrentCard();
        card.setPreserveRatio(true);
        card.setScaleX(0.8);
        card.setScaleY(0.8);
        card.setTranslateY(-50);
        bp.setCenter(card);

        VBox buttons = createButtons();
        buttons.setAlignment(Pos.BOTTOM_LEFT);
        buttons.setSpacing(10);
        buttons.setTranslateY(-50);
        buttons.setTranslateX(500);
        buttons.setScaleX(1.5);
        buttons.setScaleY(1.5);
        bp.setBottom(buttons);

        ScrollPane logPane = new ScrollPane(logArea);
        logPane.setFitToWidth(true);
        logPane.setFitToHeight(true);
        logPane.setPrefWidth(350);
        logPane.setMaxWidth(350);

        TextField input = new TextField();
        input.setPromptText("Put your choice here");
        input.setOnAction(e -> {
            String choice = input.getText().trim();
            if(!choice.isEmpty()){
                write("CHOOSE "+ choice);
                input.clear();
            }
        });

        Button sendBtn = new Button("Send ->");
        sendBtn.setOnAction(e -> {
            String choice = input.getText().trim();
            if (!choice.isEmpty()) {
                write("CHOOSE " + choice);
                input.clear();
            }
        });

        HBox inputBox = new HBox(5, input, sendBtn);
        inputBox.setSpacing(20);
        inputBox.setScaleX(1.2);
        inputBox.setScaleY(1.2);
        inputBox.setTranslateX(50);
        VBox logBox = new VBox(5, logPane, inputBox);
        logBox.setTranslateY(50);
        logBox.setSpacing(20);
        logBox.setAlignment(Pos.BOTTOM_CENTER);
        input.setMaxWidth(Double.MAX_VALUE);
        bp.setRight(logBox);

        root.getChildren().add(bp);
    }

    /*
    public void updateLogArea() {
        String log = gui.getLastLog();
        if(log != null && !log.isEmpty() && !log.equals(previousLog)){
            log = fixNames(log);
            logArea.appendText(log + '\n');
            logArea.setScrollTop(Double.MAX_VALUE);
        }
        previousLog = log;
    }
    */

    public void updateLogArea() {
        String log;
        while ((log = gui.pollLog()) != null) {
            logArea.appendText(fixNames(log) + '\n');
        }
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    public String fixNames(String log){
        log = log.replace("\u001B[31mRED\u001B[0m", "Red");
        log =  log.replace("\u001B[34mBLUE\u001B[0m", "Blue");
        log = log.replace("\u001B[33mYELLOW\u001B[0m", "Yellow");
        log =  log.replace("\u001B[32mGREEN\u001B[0m", "Green");
        return log;
    }

    public ImageView createFlightBoard(){
        String path = gui.getType().equals(FlightType.TRIAL)
                ? "/it/polimi/ingsw/GUI/cardboard/cardboard-3.png"
                : "/it/polimi/ingsw/GUI/cardboard/cardboard-5.png";
        ImageView iv = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
        iv.getStyleClass().add("fb-image");
        return iv;
    }

    public ImageView createCurrentCard() {
        URL cardURL = gui.getCurrentCardURL();
        if (cardURL == null) {
            String fallback = gui.getType().equals(FlightType.TRIAL)
                    ? "/it/polimi/ingsw/GUI/cards/GT-cards_I_IT_0121.jpg"
                    : "/it/polimi/ingsw/GUI/cards/GT-cards_II_IT_0121.jpg";
            cardURL = getClass().getResource(fallback);
        }
        ImageView iv = new ImageView(new Image(cardURL.toExternalForm()));
        iv.getStyleClass().add("card-image");
        return iv;
    }

    public VBox createButtons(){
        Button myShip = new Button("Your Rocketship");
        String myColor = gui.getColor().name().toLowerCase();
        String textColor = myColor.equals("yellow")? "black":"white";
        myShip.setStyle(String.format("-fx-background-color:%s;" +
                " -fx-text-fill:%s; " +
                "-fx-font-weight:bold;", myColor, textColor));
        myShip.setOnAction(e->{
            write("VIEW");
        });

        HBox topRow = new HBox(myShip);

        for(PlayerColor player : gui.getOtherColors()){
            Button otherShip = new Button(player.name()+ "'s Rocketship");
            otherShip.setStyle(String.format(
                            "-fx-background-color:%s;"+
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;",
                    player.name().toLowerCase())
            );
            otherShip.setOnAction(e->{
                write("VIEW " + player.name());
            });
            topRow.getChildren().add(otherShip);
        }

        topRow.setSpacing(30);
        topRow.setTranslateX(100);

        Button forfeit =  new Button("FORFEIT");
        forfeit.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: yellow;" +
                        "-fx-font-weight: bold;"
        );
        forfeit.setOnAction(e->{
            write("FORFEIT");
        });

        HBox bottomRow = new HBox(forfeit);
        bottomRow.setTranslateX(175);

        return new VBox(topRow, bottomRow);
    }

    private void write(String s){
        writer.println(s);
        writer.flush();
    }

    public javafx.scene.paint.Paint setColor(PlayerColor color){
        return switch (color){
            case RED ->     Color.RED;
            case BLUE ->    Color.BLUE;
            case YELLOW ->  Color.YELLOW;
            case GREEN ->   Color.GREEN;
        };
    }

    private void displayRocketship(PlayerColor color){
        watchingRocketship = true;
        if(gui.getType().equals(TRIAL)){
            if(color.equals(gui.getColor())){
                displayMyRocketshipTrial();
            }
            else{
                displayOtherRocketshipTrial(color);
            }
        }
        else{
            if(color.equals(gui.getColor())){
                displayMyRocketshipTwo();
            }
            else{
                displayOtherRocketshipTwo(color);
            }
        }
    }

    private void displayMyRocketshipTrial(){
        root.getChildren().clear();

        BorderPane bp = new BorderPane();
        bp.setPrefSize(Width, Height);


        Pane rocketPane = new Pane();
        rocketPane.setPrefSize(Width/1.5, Height/1.5);

        double resize = (double) (Height/1.5) / 679;

        double externalOffsetX = 38 * resize;   // margine a sinistra
        double externalOffsetY = 32 * resize;   // margine in alto
        double cellWidth = 120 * resize;   // larghezza cella
        double cellHeight = 120 * resize;   // altezza cella
        double spacingX = 4 * resize;    // spazio orizzontale tra le celle
        double spacingY = 4 * resize;    // spazio verticale tra le celle
        double reservedY = 22 * resize;
        double firstReservedX = 670 * resize;

        String bgPath = "/it/polimi/ingsw/GUI/cardboard/cardboard-1.jpg";
        Image bgImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(bgPath)
        ));
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(Width/1.5);
        bgView.setFitHeight(Height/1.5);
        bgView.setPreserveRatio(true);
        rocketPane.getChildren().add(bgView);

        Component[][] board = gui.getMyRocketshipBoard();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                String id = board[row][col].getID();
                if(!id.equals("GT-new_tiles_16_forweb157")){
                    ComponentSide side = board[row][col].getReferenceSide();

                    String tilePath = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
                    Image tileImg = new Image(Objects.requireNonNull(
                            getClass().getResourceAsStream(tilePath)
                    ));
                    ImageView iv = new ImageView(tileImg);
                    iv.setFitWidth(cellWidth);
                    iv.setFitHeight(cellHeight);
                    iv.setPreserveRatio(true);

                    switch(side){
                        case EAST -> {
                            iv.setRotate(90);
                        }
                        case SOUTH -> {
                            iv.setRotate(180);
                        }
                        case WEST -> {
                            iv.setRotate(270);
                        }
                    }

                    double x = externalOffsetX + col * (cellWidth + spacingX);
                    double y = externalOffsetY + row * (cellHeight + spacingY);
                    iv.setLayoutX(x);
                    iv.setLayoutY(y);

                    rocketPane.getChildren().add(iv);
                }
            }
        }
        rocketPane.setTranslateX(100);
        bp.setLeft(rocketPane);

        Button goBackBtn = new Button("Go Back");
        goBackBtn.setOnAction(e ->{
                    watchingRocketship = false;
                    updateGUI();
                }
        );
        goBackBtn.setStyle(
                "-fx-background-color: orange;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;"
        );
        goBackBtn.setTranslateX(550);
        goBackBtn.setTranslateY(-40);
        goBackBtn.setScaleX(1.5);
        goBackBtn.setScaleY(1.5);
        bp.setBottom(goBackBtn);

        ScrollPane logPane = new ScrollPane(logArea);
        logPane.setFitToWidth(true);
        logPane.setFitToHeight(true);
        logPane.setPrefWidth(270);
        logPane.setMaxWidth(270);

        TextField input = new TextField();
        input.setPromptText("Put your choice here");
        input.setOnAction(e -> {
            String choice = input.getText().trim();
            if(!choice.isEmpty()){
                write("CHOOSE "+ choice);
                input.clear();
            }
        });

        Button sendBtn = new Button("Send ->");
        sendBtn.setOnAction(e -> {
            String choice = input.getText().trim();
            if (!choice.isEmpty()) {
                write("CHOOSE " + choice);
                input.clear();
            }
        });

        HBox inputBox = new HBox(5, input, sendBtn);
        inputBox.setSpacing(20);
        VBox logBox = new VBox(5, logPane, inputBox);
        logBox.setSpacing(20);
        logBox.setAlignment(Pos.BOTTOM_CENTER);
        input.setMaxWidth(Double.MAX_VALUE);
        logBox.setTranslateX(-50);
        bp.setRight(logBox);

        root.getChildren().add(bp);
    }

    private void displayMyRocketshipTwo(){
        root.getChildren().clear();

        BorderPane bp = new BorderPane();
        bp.setPrefSize(Width, Height);


        Pane rocketPane = new Pane();
        rocketPane.setPrefSize(Width/1.5, Height/1.5);

        double resize = (double) (Height/1.5) / 679;

        double externalOffsetX = 38 * resize;   // margine a sinistra
        double externalOffsetY = 32 * resize;   // margine in alto
        double cellWidth = 120 * resize;   // larghezza cella
        double cellHeight = 120 * resize;   // altezza cella
        double spacingX = 4 * resize;    // spazio orizzontale tra le celle
        double spacingY = 4 * resize;    // spazio verticale tra le celle
        double reservedY = 22 * resize;
        double firstReservedX = 670 * resize;

        String bgPath = "/it/polimi/ingsw/GUI/cardboard/cardboard-1b.jpg";
        Image bgImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(bgPath)
        ));
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(Width/1.5);
        bgView.setFitHeight(Height/1.5);
        bgView.setPreserveRatio(true);
        rocketPane.getChildren().add(bgView);

        Component[][] board = gui.getMyRocketshipBoard();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                String id = board[row][col].getID();
                if(!id.equals("GT-new_tiles_16_forweb157")){
                    ComponentSide side = board[row][col].getReferenceSide();

                    String tilePath = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
                    Image tileImg = new Image(Objects.requireNonNull(
                            getClass().getResourceAsStream(tilePath)
                    ));
                    ImageView iv = new ImageView(tileImg);
                    iv.setFitWidth(cellWidth);
                    iv.setFitHeight(cellHeight);
                    iv.setPreserveRatio(true);

                    switch(side){
                        case EAST -> {
                            iv.setRotate(90);
                        }
                        case SOUTH -> {
                            iv.setRotate(180);
                        }
                        case WEST -> {
                            iv.setRotate(270);
                        }
                    }

                    double x = externalOffsetX + col * (cellWidth + spacingX);
                    double y = externalOffsetY + row * (cellHeight + spacingY);
                    iv.setLayoutX(x);
                    iv.setLayoutY(y);

                    rocketPane.getChildren().add(iv);
                }
            }
        }
        rocketPane.setTranslateX(100);
        bp.setLeft(rocketPane);

        Button goBackBtn = new Button("Go Back");
        goBackBtn.setOnAction(e ->{
                    watchingRocketship = false;
                    updateGUI();
                }
        );
        goBackBtn.setStyle(
                "-fx-background-color: orange;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;"
        );
        goBackBtn.setTranslateX(550);
        goBackBtn.setTranslateY(-40);
        goBackBtn.setScaleX(1.5);
        goBackBtn.setScaleY(1.5);
        bp.setBottom(goBackBtn);

        ScrollPane logPane = new ScrollPane(logArea);
        logPane.setFitToWidth(true);
        logPane.setFitToHeight(true);
        logPane.setPrefWidth(270);
        logPane.setMaxWidth(270);

        TextField input = new TextField();
        input.setPromptText("Put your choice here");
        input.setOnAction(e -> {
            String choice = input.getText().trim();
            if(!choice.isEmpty()){
                write("CHOOSE "+ choice);
                input.clear();
            }
        });

        Button sendBtn = new Button("Send ->");
        sendBtn.setOnAction(e -> {
            String choice = input.getText().trim();
            if (!choice.isEmpty()) {
                write("CHOOSE " + choice);
                input.clear();
            }
        });

        HBox inputBox = new HBox(5, input, sendBtn);
        inputBox.setSpacing(20);
        VBox logBox = new VBox(5, logPane, inputBox);
        logBox.setSpacing(20);
        logBox.setAlignment(Pos.BOTTOM_CENTER);
        input.setMaxWidth(Double.MAX_VALUE);
        logBox.setTranslateX(-50);
        bp.setRight(logBox);

        root.getChildren().add(bp);
    }

    private void displayOtherRocketshipTrial(PlayerColor color){
        root.getChildren().clear();

        BorderPane bp = new BorderPane();
        bp.setPrefSize(Width, Height);


        Pane rocketPane = new Pane();
        rocketPane.setPrefSize(Width/1.5, Height/1.5);

        double resize = (double) (Height/1.5) / 679;

        double externalOffsetX = 38 * resize;   // margine a sinistra
        double externalOffsetY = 32 * resize;   // margine in alto
        double cellWidth = 120 * resize;   // larghezza cella
        double cellHeight = 120 * resize;   // altezza cella
        double spacingX = 4 * resize;    // spazio orizzontale tra le celle
        double spacingY = 4 * resize;    // spazio verticale tra le celle
        double reservedY = 22 * resize;
        double firstReservedX = 670 * resize;

        String bgPath = "/it/polimi/ingsw/GUI/cardboard/cardboard-1.jpg";
        Image bgImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(bgPath)
        ));
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(Width/1.5);
        bgView.setFitHeight(Height/1.5);
        bgView.setPreserveRatio(true);
        rocketPane.getChildren().add(bgView);

        Component[][] board = gui.getOtherRocketshipBoard(color);
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                String id = board[row][col].getID();
                if(!id.equals("GT-new_tiles_16_forweb157")){
                    ComponentSide side = board[row][col].getReferenceSide();

                    String tilePath = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
                    Image tileImg = new Image(Objects.requireNonNull(
                            getClass().getResourceAsStream(tilePath)
                    ));
                    ImageView iv = new ImageView(tileImg);
                    iv.setFitWidth(cellWidth);
                    iv.setFitHeight(cellHeight);
                    iv.setPreserveRatio(true);

                    switch(side){
                        case EAST -> {
                            iv.setRotate(90);
                        }
                        case SOUTH -> {
                            iv.setRotate(180);
                        }
                        case WEST -> {
                            iv.setRotate(270);
                        }
                    }

                    double x = externalOffsetX + col * (cellWidth + spacingX);
                    double y = externalOffsetY + row * (cellHeight + spacingY);
                    iv.setLayoutX(x);
                    iv.setLayoutY(y);

                    rocketPane.getChildren().add(iv);
                }
            }
        }
        rocketPane.setTranslateX(100);
        bp.setLeft(rocketPane);

        Button goBackBtn = new Button("Go Back");
        goBackBtn.setOnAction(e ->{
                    watchingRocketship = false;
                    updateGUI();
                }
        );
        goBackBtn.setStyle(
                "-fx-background-color: orange;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;"
        );
        goBackBtn.setTranslateX(550);
        goBackBtn.setTranslateY(-40);
        goBackBtn.setScaleX(1.5);
        goBackBtn.setScaleY(1.5);
        bp.setBottom(goBackBtn);

        ScrollPane logPane = new ScrollPane(logArea);
        logPane.setFitToWidth(true);
        logPane.setFitToHeight(true);
        logPane.setPrefWidth(270);
        logPane.setMaxWidth(270);

        TextField input = new TextField();
        input.setPromptText("Put your choice here");
        input.setOnAction(e -> {
            String choice = input.getText().trim();
            if(!choice.isEmpty()){
                write("CHOOSE "+ choice);
                input.clear();
            }
        });

        Button sendBtn = new Button("Send ->");
        sendBtn.setOnAction(e -> {
            String choice = input.getText().trim();
            if (!choice.isEmpty()) {
                write("CHOOSE " + choice);
                input.clear();
            }
        });

        HBox inputBox = new HBox(5, input, sendBtn);
        inputBox.setSpacing(20);
        VBox logBox = new VBox(5, logPane, inputBox);
        logBox.setSpacing(20);
        logBox.setAlignment(Pos.BOTTOM_CENTER);
        input.setMaxWidth(Double.MAX_VALUE);
        logBox.setTranslateX(-50);
        bp.setRight(logBox);

        root.getChildren().add(bp);
    }

    private void displayOtherRocketshipTwo(PlayerColor color){
        root.getChildren().clear();

        BorderPane bp = new BorderPane();
        bp.setPrefSize(Width, Height);


        Pane rocketPane = new Pane();
        rocketPane.setPrefSize(Width/1.5, Height/1.5);

        double resize = (double) (Height/1.5) / 679;

        double externalOffsetX = 38 * resize;   // margine a sinistra
        double externalOffsetY = 32 * resize;   // margine in alto
        double cellWidth = 120 * resize;   // larghezza cella
        double cellHeight = 120 * resize;   // altezza cella
        double spacingX = 4 * resize;    // spazio orizzontale tra le celle
        double spacingY = 4 * resize;    // spazio verticale tra le celle
        double reservedY = 22 * resize;
        double firstReservedX = 670 * resize;

        String bgPath = "/it/polimi/ingsw/GUI/cardboard/cardboard-1b.jpg";
        Image bgImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(bgPath)
        ));
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(Width/1.5);
        bgView.setFitHeight(Height/1.5);
        bgView.setPreserveRatio(true);
        rocketPane.getChildren().add(bgView);

        Component[][] board = gui.getOtherRocketshipBoard(color);
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                String id = board[row][col].getID();
                if(!id.equals("GT-new_tiles_16_forweb157")){
                    ComponentSide side = board[row][col].getReferenceSide();

                    String tilePath = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
                    Image tileImg = new Image(Objects.requireNonNull(
                            getClass().getResourceAsStream(tilePath)
                    ));
                    ImageView iv = new ImageView(tileImg);
                    iv.setFitWidth(cellWidth);
                    iv.setFitHeight(cellHeight);
                    iv.setPreserveRatio(true);

                    switch(side){
                        case EAST -> {
                            iv.setRotate(90);
                        }
                        case SOUTH -> {
                            iv.setRotate(180);
                        }
                        case WEST -> {
                            iv.setRotate(270);
                        }
                    }

                    double x = externalOffsetX + col * (cellWidth + spacingX);
                    double y = externalOffsetY + row * (cellHeight + spacingY);
                    iv.setLayoutX(x);
                    iv.setLayoutY(y);

                    rocketPane.getChildren().add(iv);
                }
            }
        }
        rocketPane.setTranslateX(100);
        bp.setLeft(rocketPane);

        Button goBackBtn = new Button("Go Back");
        goBackBtn.setOnAction(e ->{
                    watchingRocketship = false;
                    updateGUI();
                }
        );
        goBackBtn.setStyle(
                "-fx-background-color: orange;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;"
        );
        goBackBtn.setTranslateX(550);
        goBackBtn.setTranslateY(-40);
        goBackBtn.setScaleX(1.5);
        goBackBtn.setScaleY(1.5);
        bp.setBottom(goBackBtn);

        ScrollPane logPane = new ScrollPane(logArea);
        logPane.setFitToWidth(true);
        logPane.setFitToHeight(true);
        logPane.setPrefWidth(270);
        logPane.setMaxWidth(270);

        TextField input = new TextField();
        input.setPromptText("Put your choice here");
        input.setOnAction(e -> {
            String choice = input.getText().trim();
            if(!choice.isEmpty()){
                write("CHOOSE "+ choice);
                input.clear();
            }
        });

        Button sendBtn = new Button("Send ->");
        sendBtn.setOnAction(e -> {
            String choice = input.getText().trim();
            if (!choice.isEmpty()) {
                write("CHOOSE " + choice);
                input.clear();
            }
        });

        HBox inputBox = new HBox(5, input, sendBtn);
        inputBox.setSpacing(20);
        VBox logBox = new VBox(5, logPane, inputBox);
        logBox.setSpacing(20);
        logBox.setAlignment(Pos.BOTTOM_CENTER);
        input.setMaxWidth(Double.MAX_VALUE);
        logBox.setTranslateX(-50);
        bp.setRight(logBox);

        root.getChildren().add(bp);
    }
}