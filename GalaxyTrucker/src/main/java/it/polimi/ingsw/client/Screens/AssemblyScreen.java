package it.polimi.ingsw.client.Screens;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.GUI;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.adventure.AdventureCard;
import it.polimi.ingsw.model.utilities.components.Component;
import it.polimi.ingsw.enums.ComponentSide;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import static it.polimi.ingsw.enums.FlightType.TRIAL;

public class AssemblyScreen {
    private Stage stage;

    private final GUI gui;
    private final PrintWriter writer;
    private final ClientController controller;
    private final Scene scene;
    private final VBox root;

    private boolean turned = false;

    private boolean peeking = false;
    private int peekchoice = -1;

    private int Width;
    private int Height;

    public AssemblyScreen(Stage stage, GUI gui, PrintWriter writer, ClientController c, int W, int H){
        this.gui = gui;
        this.writer = writer;
        this.controller = c;
        this.Width = W;
        this.Height = H;

        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(Width, Height);
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        this.stage = stage;

        displayWaitingForData();

        scene = new Scene(root, Width, Height);

        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        root.getStylesheets().add(getClass()
                .getResource("/it/polimi/ingsw/GUI/style.css")
                .toExternalForm());
    }

    public void updateGUI(){
        Width = gui.getWidth();
        Height = gui.getHeight();
        switch(gui.getProgression()){
            case RUNNING_GAME -> {
                switch(gui.getPhase()){
                    case ASSEMBLY_PHASE -> {
                        if(isPeeking()){
                            switch(getPeekchoice()){
                                case -1 -> {
                                    displayCardsChoice();
                                }
                                default -> displayCardsDeck();
                            }
                        }
                        else{
                            if(turned){
                                displayTurnedComponents();
                            }
                            else{
                                PlayerColor other = gui.getCurrentShip();
                                if(other.equals(gui.getColor())){
                                    displayMyRocketship();
                                }

                                else {
                                    displayOtherRocketship(other);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Scene getScene(){
        return scene;
    }

    private void displayWaitingForData(){
        root.getChildren().clear();

        root.setStyle("-fx-background-color: linear-gradient(to bottom, #DDA0DD, #663399);");

        Label waitingLabel = new Label("Game is going to start very soon!");
        waitingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(80, 80);

        VBox vbox = new VBox(15);
        vbox.setFillWidth(true);
        VBox.setVgrow(vbox, Priority.ALWAYS);
        vbox.setMaxHeight(Double.MAX_VALUE);
        vbox.setId("WaitingForDataScreen");
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(waitingLabel, spinner);

        root.getChildren().add(vbox);
        Task<Void> attesaTask4 = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (!gui.isDisplayable()) {
                    Thread.sleep(100);
                }
                return null;
            }
        };

        attesaTask4.setOnSucceeded(e -> {
            displayMyRocketship();
        });

        new Thread(attesaTask4).start();
    }

    private void displayMyRocketship(){
        if(gui.getType().equals(TRIAL)){
            displayMyRocketshipTrial();
        }
        else{
            displayMyRocketshipTwo();
        }
    }

    private void displayMyRocketshipTrial(){
        root.getChildren().clear();

        root.setStyle("-fx-background-color: linear-gradient(to bottom, #191970, #7B68EE);");

        AnchorPane anchor = new AnchorPane();
        anchor.setPrefSize(Width, Height);
        anchor.getStyleClass().add("TrialRocketship");

        Label label = new Label(gui.getLastLog());
        label.setLayoutX(Width/92);
        label.setLayoutY(Height/1.45);
        anchor.getChildren().add(label);

        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(0));
        mainLayout.setAlignment(Pos.TOP_LEFT);

        Pane rocketPane = new Pane();
        rocketPane.setPrefSize(Width/1.5, Height/1.5);

        double resize = (double) (Height/1.5) / 679;

        double externalOffsetX = 38 * resize;
        double externalOffsetY = 32 * resize;
        double cellWidth = 120 * resize;
        double cellHeight = 120 * resize;
        double spacingX = 4 * resize;
        double spacingY = 4 * resize;
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

        Component[] reserved = gui.getMyReservedComponents();
        for(int i = 0; i < reserved.length; i++){
            String id = reserved[i].getID();
            if(!id.equals("GT-new_tiles_16_forweb157")){
                String path = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
                Image tileImg = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream(path)
                ));
                ImageView iv = new ImageView(tileImg);
                iv.setFitWidth(cellWidth);
                iv.setFitHeight(cellHeight);
                iv.setPreserveRatio(true);

                switch(reserved[i].getReferenceSide()){
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

                double x = firstReservedX + i*(spacingX + cellWidth);
                double y = reservedY;
                iv.setLayoutX(x);
                iv.setLayoutY(y);

                rocketPane.getChildren().add(iv);
            }
        }

        Component hand = gui.getMyHandComponent();

        String id = hand.getID();
        String path = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));

        ImageView handView = new ImageView(img);
        handView.setFitWidth(cellWidth);
        handView.setFitHeight(cellHeight);
        handView.setPreserveRatio(true);

        switch (hand.getReferenceSide()) {
            case EAST -> handView.setRotate(90);
            case SOUTH -> handView.setRotate(180);
            case WEST -> handView.setRotate(270);
        }

        handView.setLayoutX((double) Width/1.75);
        handView.setLayoutY((double) Height/54);

        anchor.getChildren().add(handView);

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPrefWidth(250);

        Button fetchUnturnedBtn = new Button("Fetch unturned component");
        fetchUnturnedBtn.setMaxWidth(Double.MAX_VALUE);
        fetchUnturnedBtn.setOnAction(e -> write("FETCH_UNTURNED"));

        Button showTurned = new Button("Show turned component");
        showTurned.setMaxWidth(Double.MAX_VALUE);
        showTurned.setOnAction(e -> {turned = true; updateGUI();});


        Button fetchReservedBtn = new Button("Fetch from reserve");
        fetchReservedBtn.setMaxWidth(Double.MAX_VALUE);
        fetchReservedBtn.setOnAction(e -> {
            rocketPane.setOnMouseClicked(event -> {
                double mouseX = event.getX();

                if(mouseX/resize <= 797){
                    write("FETCH_RESERVED 1");
                    rocketPane.setOnMouseClicked(null);
                }
                else{
                    write("FETCH_RESERVED 2");
                    rocketPane.setOnMouseClicked(null);
                }
            });
        });

        Button rotateBtn = new Button("Rotate component in hand");
        rotateBtn.setMaxWidth(Double.MAX_VALUE);
        rotateBtn.setOnAction(e -> write("ROTATE"));

        Button reserveBtn = new Button("Reserve component");
        reserveBtn.setMaxWidth(Double.MAX_VALUE);
        reserveBtn.setOnAction(e -> write("RESERVE"));

        Button rejectBtn = new Button("Reject component in hand");
        rejectBtn.setMaxWidth(Double.MAX_VALUE);
        rejectBtn.setOnAction(e -> write("REJECT"));

        Button placeBtn = new Button("Place component");
        placeBtn.setMaxWidth(Double.MAX_VALUE);
        placeBtn.setOnAction(e -> {
            rocketPane.setOnMouseClicked(event -> {
                double mouseX = event.getX();
                double mouseY = event.getY();

                int col = (int) ((mouseX - externalOffsetX) / (cellWidth + spacingX));
                int row = (int) ((mouseY - externalOffsetY) / (cellHeight + spacingY));

                if (row >= 0 && col >= 0) {
                    write("PLACE " + (row + 5) + " " + (col + 4));
                    rocketPane.setOnMouseClicked(null);
                }
            });
        });

        Button switchViewBtn = new Button("Switch player");
        switchViewBtn.setMaxWidth(Double.MAX_VALUE);
        switchViewBtn.setOnAction(e -> write("SWITCH_VIEW"));

        Button readyBtn = new Button("Ready for takeoff");
        readyBtn.setMaxWidth(Double.MAX_VALUE);
        readyBtn.setOnAction(e -> {
            write("READY_FOR_TAKEOFF");
            gui.showTakeoff();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        String s;
        if(Width == 1920){
            s = "Switch to 720p";
        }
        else{
            s = "Switch to 1080p";
        }
        Button res = new Button(s);
        res.setMaxWidth(Double.MAX_VALUE);
        res.setOnAction(e -> gui.switchResolution());
        res.setId("AssemblyButton");

        fetchUnturnedBtn.setId("AssemblyButton");
        showTurned.setId("AssemblyButton");
        fetchReservedBtn.setId("AssemblyButton");
        rotateBtn.setId("AssemblyButton");
        reserveBtn.setId("AssemblyButton");
        rejectBtn.setId("AssemblyButton");
        placeBtn.setId("AssemblyButton");
        switchViewBtn.setId("AssemblyButton");
        readyBtn.setId("AssemblyButton");

        if(!gui.getMyHandComponent().getID().equals("GT-new_tiles_16_forweb157")){
            placeBtn.setStyle(
                    "-fx-background-color: #E67E00;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;"
            );
        }

        controls.getChildren().addAll(
                fetchUnturnedBtn,
                showTurned,
                fetchReservedBtn,
                rotateBtn,
                reserveBtn,
                rejectBtn,
                placeBtn,
                switchViewBtn,
                readyBtn,
                spacer,
                res
        );

        rocketPane.setLayoutX(Width/96);
        rocketPane.setLayoutY(Height/54);

        mainLayout.setLayoutX(Width/1.4);
        mainLayout.setLayoutY(Height/54);

        root.getChildren().add(anchor);

        mainLayout.getChildren().addAll(controls);
        anchor.getChildren().add(rocketPane);
        anchor.getChildren().add(mainLayout);
    }

    private void displayMyRocketshipTwo(){
        root.getChildren().clear();

        root.setStyle("-fx-background-color: linear-gradient(to bottom, #C71585, #DA70D6);");

        AnchorPane anchor = new AnchorPane();
        anchor.setPrefSize(Width, Height);
        anchor.getStyleClass().add("TrialRocketship");

        Label label = new Label(gui.getLastLog());
        label.setLayoutX(Width/92);
        label.setLayoutY(Height/1.45);

        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(0));
        mainLayout.setAlignment(Pos.TOP_LEFT);

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

        Component[] reserved = gui.getMyReservedComponents();
        for(int i = 0; i < reserved.length; i++){
            String id = reserved[i].getID();
            if(!id.equals("GT-new_tiles_16_forweb157")){
                String path = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
                Image tileImg = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream(path)
                ));
                ImageView iv = new ImageView(tileImg);
                iv.setFitWidth(cellWidth);
                iv.setFitHeight(cellHeight);
                iv.setPreserveRatio(true);

                switch(reserved[i].getReferenceSide()){
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

                double x = firstReservedX + i*(spacingX + cellWidth);
                double y = reservedY;
                iv.setLayoutX(x);
                iv.setLayoutY(y);

                rocketPane.getChildren().add(iv);
            }
        }

        Component hand = gui.getMyHandComponent();

        String id = hand.getID();
        String path = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));

        ImageView handView = new ImageView(img);
        handView.setFitWidth(cellWidth);
        handView.setFitHeight(cellHeight);
        handView.setPreserveRatio(true);

        switch (hand.getReferenceSide()) {
            case EAST -> handView.setRotate(90);
            case SOUTH -> handView.setRotate(180);
            case WEST -> handView.setRotate(270);
        }

        handView.setLayoutX((double) Width/1.75);
        handView.setLayoutY((double) Height/54);

        anchor.getChildren().add(handView);

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPrefWidth(250);

        Button fetchUnturnedBtn = new Button("Fetch unturned component");
        fetchUnturnedBtn.setMaxWidth(Double.MAX_VALUE);
        fetchUnturnedBtn.setOnAction(e -> write("FETCH_UNTURNED"));

        Button showTurned = new Button("Show turned component");
        showTurned.setMaxWidth(Double.MAX_VALUE);
        showTurned.setOnAction(e -> {turned = true; updateGUI();});

        Button peek = new Button("Peek");
        peek.setMaxWidth(Double.MAX_VALUE);
        peek.setOnAction(e -> {peeking = true; updateGUI();});

        Button fetchReservedBtn = new Button("Fetch from reserve");
        fetchReservedBtn.setMaxWidth(Double.MAX_VALUE);
        fetchReservedBtn.setOnAction(e -> {
            rocketPane.setOnMouseClicked(event -> {
                double mouseX = event.getX();

                if(mouseX/resize <= 797){
                    write("FETCH_RESERVED 1");
                    rocketPane.setOnMouseClicked(null);
                }
                else{
                    write("FETCH_RESERVED 2");
                    rocketPane.setOnMouseClicked(null);
                }
            });
        });

        Button rotateBtn = new Button("Rotate component in hand");
        rotateBtn.setMaxWidth(Double.MAX_VALUE);
        rotateBtn.setOnAction(e -> write("ROTATE"));

        Button reserveBtn = new Button("Reserve component");
        reserveBtn.setMaxWidth(Double.MAX_VALUE);
        reserveBtn.setOnAction(e -> write("RESERVE"));

        Button rejectBtn = new Button("Reject component in hand");
        rejectBtn.setMaxWidth(Double.MAX_VALUE);
        rejectBtn.setOnAction(e -> write("REJECT"));

        Button placeBtn = new Button("Place component");
        placeBtn.setMaxWidth(Double.MAX_VALUE);
        placeBtn.setOnAction(e -> {
            rocketPane.setOnMouseClicked(event -> {
                double mouseX = event.getX();
                double mouseY = event.getY();

                int col = (int) ((mouseX - externalOffsetX) / (cellWidth + spacingX));
                int row = (int) ((mouseY - externalOffsetY) / (cellHeight + spacingY));

                if (row >= 0 && col >= 0) {
                    write("PLACE " + (row + 5) + " " + (col + 4));
                    rocketPane.setOnMouseClicked(null);
                }
            });
        });

        Button switchViewBtn = new Button("Switch player");
        switchViewBtn.setMaxWidth(Double.MAX_VALUE);
        switchViewBtn.setOnAction(e -> write("SWITCH_VIEW"));

        Button hourGlassBtn = new Button("Flip hourglass");
        hourGlassBtn.setMaxWidth(Double.MAX_VALUE);
        hourGlassBtn.setOnAction(e -> write("FLIP_HOURGLASS"));

        Button readyBtn = new Button("Ready for takeoff");
        readyBtn.setMaxWidth(Double.MAX_VALUE);
        readyBtn.setOnAction(e -> {
                write("READY_FOR_TAKEOFF");
                gui.showTakeoff();
                });
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        String s;
        if(Width == 1920){
            s = "Switch to 720p";
        }
        else{
            s = "Switch to 1080p";
        }
        Button res = new Button(s);
        res.setMaxWidth(Double.MAX_VALUE);
        res.setOnAction(e -> gui.switchResolution());
        res.setId("AssemblyButton");

        fetchUnturnedBtn.setId("AssemblyButton");
        showTurned.setId("AssemblyButton");
        fetchReservedBtn.setId("AssemblyButton");
        rotateBtn.setId("AssemblyButton");
        reserveBtn.setId("AssemblyButton");
        rejectBtn.setId("AssemblyButton");
        placeBtn.setId("AssemblyButton");
        switchViewBtn.setId("AssemblyButton");
        hourGlassBtn.setId("AssemblyButton");
        readyBtn.setId("AssemblyButton");
        peek.setId("AssemblyButton");

        if(!gui.getMyHandComponent().getID().equals("GT-new_tiles_16_forweb157")){
            placeBtn.setStyle(
                    "-fx-background-color: #E67E00;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;"
            );
        }

        controls.getChildren().addAll(
                fetchUnturnedBtn,
                showTurned,
                fetchReservedBtn,
                rotateBtn,
                reserveBtn,
                rejectBtn,
                placeBtn,
                switchViewBtn,
                hourGlassBtn,
                readyBtn,
                peek,
                spacer,
                res
        );

        rocketPane.setLayoutX((double) Width/85);
        rocketPane.setLayoutY((double) Height/54);

        mainLayout.setLayoutX(Width/1.4);
        mainLayout.setLayoutY((double) Height/54);

        root.getChildren().add(anchor);

        mainLayout.getChildren().addAll(controls);
        anchor.getChildren().add(rocketPane);
        anchor.getChildren().add(mainLayout);
        anchor.getChildren().add(label);
    }



    private void displayOtherRocketship(PlayerColor color){
        if(gui.getType().equals(TRIAL)){
            displayOtherRocketshipTrial(color);
        }
        else{
            displayOtherRocketshipTwo(color);
        }
    }

    private void displayOtherRocketshipTrial(PlayerColor color){
        root.getChildren().clear();

        switch (color){
            case RED -> {
                root.setStyle("-fx-background-color: linear-gradient(to bottom, #A52A2A, #7B68EE);");
            }
            case BLUE -> {
                root.setStyle("-fx-background-color: linear-gradient(to bottom, #0000FF, #7B68EE);");
            }
            case GREEN -> {
                root.setStyle("-fx-background-color: linear-gradient(to bottom, #006400, #7B68EE);");
            }
            case YELLOW -> {
                root.setStyle("-fx-background-color: linear-gradient(to bottom, #FFD700, #7B68EE);");
            }
        }

        AnchorPane anchor = new AnchorPane();
        anchor.setPrefSize(Width, Height);
        anchor.getStyleClass().add("TrialRocketship");

        Label log = new Label(gui.getLastLog());
        log.setLayoutX(Width/92);
        log.setLayoutY(Height/1.35);
        anchor.getChildren().add(log);

        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(0));
        mainLayout.setAlignment(Pos.TOP_LEFT);

        Pane rocketPane = new Pane();
        rocketPane.setPrefSize(Width/1.5, Height/1.5);

        double resize = (double) (Height/1.5) / 679;

        double externalOffsetX = 38 * resize;
        double externalOffsetY = 32 * resize;
        double cellWidth = 120 * resize;
        double cellHeight = 120 * resize;
        double spacingX = 4 * resize;
        double spacingY = 4 * resize;
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

        Component[] reserved = gui.getOtherReservedComponents(color);
        for(int i = 0; i < reserved.length; i++){
            String id = reserved[i].getID();
            if(!id.equals("GT-new_tiles_16_forweb157")){
                String path = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
                Image tileImg = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream(path)
                ));
                ImageView iv = new ImageView(tileImg);
                iv.setFitWidth(cellWidth);
                iv.setFitHeight(cellHeight);
                iv.setPreserveRatio(true);

                switch(reserved[i].getReferenceSide()){
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

                double x = firstReservedX + i*(spacingX + cellWidth);
                double y = reservedY;
                iv.setLayoutX(x);
                iv.setLayoutY(y);

                rocketPane.getChildren().add(iv);
            }
        }

        Component hand = gui.getOtherHandComponent(color);

        String id = hand.getID();
        String path = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));

        ImageView handView = new ImageView(img);
        handView.setFitWidth(cellWidth);
        handView.setFitHeight(cellHeight);
        handView.setPreserveRatio(true);

        switch (hand.getReferenceSide()) {
            case EAST -> handView.setRotate(90);
            case SOUTH -> handView.setRotate(180);
            case WEST -> handView.setRotate(270);
        }

        handView.setLayoutX((double) Width/1.75);
        handView.setLayoutY((double) Height/15);

        anchor.getChildren().add(handView);

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPrefWidth(250);

        Button switchViewBtn = new Button("Switch player");
        switchViewBtn.setMaxWidth(Double.MAX_VALUE);
        switchViewBtn.setOnAction(e -> write("SWITCH_VIEW"));

        Button myShipBtn = new Button("Back to the ship");
        myShipBtn.setMaxWidth(Double.MAX_VALUE);
        myShipBtn.setOnAction(e -> gui.backToMyShip());

        switchViewBtn.setId("AssemblyButton");
        myShipBtn.setId("AssemblyButton");

        controls.getChildren().addAll(
                switchViewBtn,
                myShipBtn
        );

        rocketPane.setLayoutX((double) Width/85);
        rocketPane.setLayoutY((double) Height/15);

        mainLayout.setLayoutX(Width/1.4);
        mainLayout.setLayoutY((double) Height/15);

        String c;
        switch(color){
            case RED -> {
                c = "Red";
            }
            case BLUE -> {
                c = "Blue";
            }
            case YELLOW -> {
                c = "Yellow";
            }
            case GREEN -> {
                c = "Green";
            }
            default -> c = "ERROR";
        }

        Label label = new Label(c + "'s player rocketship");
        label.setStyle("-fx-font-size: 25px; -fx-text-fill: white;");

        label.setLayoutX((double) Width/85);
        label.setLayoutY((double) Height/54);

        anchor.getChildren().add(label);

        root.getChildren().add(anchor);

        mainLayout.getChildren().addAll(controls);
        anchor.getChildren().add(rocketPane);
        anchor.getChildren().add(mainLayout);
    }

    private void displayOtherRocketshipTwo(PlayerColor color){
        root.getChildren().clear();

        switch (color){
            case RED -> {
                root.setStyle("-fx-background-color: linear-gradient(to bottom, #A52A2A, #7B68EE);");
            }
            case BLUE -> {
                root.setStyle("-fx-background-color: linear-gradient(to bottom, #0000FF, #7B68EE);");
            }
            case GREEN -> {
                root.setStyle("-fx-background-color: linear-gradient(to bottom, #006400, #7B68EE);");
            }
            case YELLOW -> {
                root.setStyle("-fx-background-color: linear-gradient(to bottom, #FFD700, #7B68EE);");
            }
        }

        AnchorPane anchor = new AnchorPane();
        anchor.setPrefSize(Width, Height);
        anchor.getStyleClass().add("TrialRocketship");

        Label log = new Label(gui.getLastLog());
        log.setLayoutX(Width/92);
        log.setLayoutY(Height/1.35);
        anchor.getChildren().add(log);

        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(0));
        mainLayout.setAlignment(Pos.TOP_LEFT);

        Pane rocketPane = new Pane();
        rocketPane.setPrefSize(Width/1.5, Height/1.5);

        double resize = (double) (Height/1.5) / 679;

        double externalOffsetX = 38 * resize;
        double externalOffsetY = 32 * resize;
        double cellWidth = 120 * resize;
        double cellHeight = 120 * resize;
        double spacingX = 4 * resize;
        double spacingY = 4 * resize;
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

        Component[] reserved = gui.getOtherReservedComponents(color);
        for(int i = 0; i < reserved.length; i++){
            String id = reserved[i].getID();
            if(!id.equals("GT-new_tiles_16_forweb157")){
                String path = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
                Image tileImg = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream(path)
                ));
                ImageView iv = new ImageView(tileImg);
                iv.setFitWidth(cellWidth);
                iv.setFitHeight(cellHeight);
                iv.setPreserveRatio(true);

                switch(reserved[i].getReferenceSide()){
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

                double x = firstReservedX + i*(spacingX + cellWidth);
                double y = reservedY;
                iv.setLayoutX(x);
                iv.setLayoutY(y);

                rocketPane.getChildren().add(iv);
            }
        }

        Component hand = gui.getOtherHandComponent(color);

        String id = hand.getID();
        String path = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));

        ImageView handView = new ImageView(img);
        handView.setFitWidth(cellWidth);
        handView.setFitHeight(cellHeight);
        handView.setPreserveRatio(true);

        switch (hand.getReferenceSide()) {
            case EAST -> handView.setRotate(90);
            case SOUTH -> handView.setRotate(180);
            case WEST -> handView.setRotate(270);
        }

        handView.setLayoutX((double) Width/1.75);
        handView.setLayoutY((double) Height/15);

        anchor.getChildren().add(handView);

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPrefWidth(250);

        Button switchViewBtn = new Button("Switch player");
        switchViewBtn.setMaxWidth(Double.MAX_VALUE);
        switchViewBtn.setOnAction(e -> write("SWITCH_VIEW"));

        Button myShipBtn = new Button("Back to the ship");
        myShipBtn.setMaxWidth(Double.MAX_VALUE);
        myShipBtn.setOnAction(e -> gui.backToMyShip());

        switchViewBtn.setId("AssemblyButton");
        myShipBtn.setId("AssemblyButton");

        controls.getChildren().addAll(
                switchViewBtn,
                myShipBtn
        );

        rocketPane.setLayoutX((double) Width/85);
        rocketPane.setLayoutY((double) Height/15);

        mainLayout.setLayoutX(Width/1.4);
        mainLayout.setLayoutY((double) Height/15);

        String c;
        switch(color){
            case RED -> {
                c = "Red";
            }
            case BLUE -> {
                c = "Blue";
            }
            case YELLOW -> {
                c = "Yellow";
            }
            case GREEN -> {
                c = "Green";
            }
            default -> c = "ERROR";
        }

        Label label = new Label(c + "'s player rocketship");
        label.setStyle("-fx-font-size: 25px; -fx-text-fill: white;");

        label.setLayoutX((double) Width/85);
        label.setLayoutY((double) Height/54);

        anchor.getChildren().add(label);

        root.getChildren().add(anchor);

        mainLayout.getChildren().addAll(controls);
        anchor.getChildren().add(rocketPane);
        anchor.getChildren().add(mainLayout);
    }


    private void displayTurnedComponents(){
        root.getChildren().clear();
        if(gui.getType() == TRIAL){
            root.setStyle("-fx-background-color: linear-gradient(to bottom, #191970, #7B68EE);");
        }
        else{
            root.setStyle("-fx-background-color: linear-gradient(to bottom, #C71585, #DA70D6);");
        }

        AnchorPane anchor = new AnchorPane();
        anchor.setPrefSize(Width, Height);

        Label label = new Label(gui.getLastLog());
        label.setLayoutX(Width/92);
        label.setLayoutY(Height/1.45);
        anchor.getChildren().add(label);

        double gridX = (double) Width /85;         // posizione orizzontale della griglia
        double gridY = (double) Height/54;          // posizione verticale della griglia
        double gridWidth = Height/1.1;     // larghezza fissa della griglia
        double gridHeight = Height/1.1;    // altezza fissa della griglia

        int rows = 6;
        int cols = 6;
        double cellWidth = gridWidth / cols;
        double cellHeight = gridHeight / rows;

        GridPane grid = new GridPane();
        grid.setPrefSize(gridWidth, gridHeight);
        grid.setMinSize(gridWidth, gridHeight);
        grid.setMaxSize(gridWidth, gridHeight);
        grid.setLayoutX(gridX);
        grid.setLayoutY(gridY);
        grid.setGridLinesVisible(false); // invisibile

        for (int i = 0; i < cols; i++) {
            ColumnConstraints cc = new ColumnConstraints(cellWidth);
            cc.setPrefWidth(cellWidth);
            cc.setMinWidth(cellWidth);
            cc.setMaxWidth(cellWidth);
            grid.getColumnConstraints().add(cc);
        }
        for (int i = 0; i < rows; i++) {
            RowConstraints rc = new RowConstraints(cellHeight);
            rc.setPrefHeight(cellHeight);
            rc.setMinHeight(cellHeight);
            rc.setMaxHeight(cellHeight);
            grid.getRowConstraints().add(rc);
        }

        List<Component> components = gui.getTurnedComponents();
        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);
            String id = comp.getID();
            if(!id.equals("GT-new_tiles_16_forweb157")){
                ComponentSide side = comp.getReferenceSide();

                String path = "/it/polimi/ingsw/GUI/tiles/" + id + ".jpg";
                Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
                ImageView iv = new ImageView(img);
                iv.setPreserveRatio(true);
                iv.setFitWidth(cellWidth * 0.88);
                iv.setFitHeight(cellHeight * 0.88);

                switch (side) {
                    case EAST -> iv.setRotate(90);
                    case SOUTH -> iv.setRotate(180);
                    case WEST -> iv.setRotate(270);
                }

                int row = i / cols;
                int col = i % cols;

                StackPane tileCell = new StackPane(iv);
                tileCell.setMinSize(cellWidth, cellHeight);
                tileCell.setMaxSize(cellWidth, cellHeight);
                tileCell.setPrefSize(cellWidth, cellHeight);
                grid.add(tileCell, col, row);
            }
        }

        VBox controls = new VBox(10);
        controls.setPrefWidth(250);
        controls.setLayoutX(Width - 300); // fisso rispetto alla finestra
        controls.setLayoutY(120);         // verticale fissato

        Button chooseBtn = new Button("Choose component");
        chooseBtn.setMaxWidth(Double.MAX_VALUE);
        chooseBtn.setId("AssemblyButton");

        Button backBtn = new Button("Back to the ship");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setId("AssemblyButton");

        controls.getChildren().addAll(chooseBtn, backBtn);

        chooseBtn.setOnAction(e -> {
            for (Node node : grid.getChildren()) {
                node.setOnMouseClicked(event -> {
                    Integer col = GridPane.getColumnIndex(node);
                    Integer row = GridPane.getRowIndex(node);
                    if (row != null && col != null) {
                        int index = row * cols + col;
                        write("FETCH_TURNED " + (index + 1));
                    }
                });
            }
        });

        backBtn.setOnAction(e -> {
            turned = false;
            updateGUI();
        });

        controls.setLayoutX(Width/1.4);
        controls.setLayoutY((double) Height/54);

        anchor.getChildren().addAll(grid, controls);
        root.getChildren().add(anchor);

    }

    private void displayCardsChoice(){
        root.getChildren().clear();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #191970, #7B68EE);");

        AnchorPane anchor = new AnchorPane();
        anchor.setPrefSize(Width, Height);

        Label label = new Label(gui.getLastLog());
        label.setLayoutX(Width/92);
        label.setLayoutY(Height/1.45);
        anchor.getChildren().add(label);

        double gridX = (double) Width/8;
        double gridY = (double) Height/4.8;
        double cellSize = (double) Height/4;
        int cols = 3;
        int rows = 1;

        GridPane grid = new GridPane();
        grid.setPrefSize(cellSize * cols, cellSize);
        grid.setLayoutX(gridX);
        grid.setLayoutY(gridY);
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(Insets.EMPTY);
        grid.setGridLinesVisible(false);

        for (int i = 0; i < cols; i++) {
            ColumnConstraints cc = new ColumnConstraints(cellSize);
            cc.setMinWidth(cellSize);
            cc.setPrefWidth(cellSize);
            cc.setMaxWidth(cellSize);
            grid.getColumnConstraints().add(cc);
        }

        RowConstraints rc = new RowConstraints(cellSize);
        rc.setMinHeight(cellSize);
        rc.setPrefHeight(cellSize);
        rc.setMaxHeight(cellSize);
        grid.getRowConstraints().add(rc);

        String[] ids = {"GT-cards_II_IT_0121", "GT-cards_II_IT_0121", "GT-cards_I_IT_0121"};
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            String path = "/it/polimi/ingsw/GUI/cards/" + id + ".jpg";

            ImageView iv = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
            iv.setPreserveRatio(true);
            iv.setFitWidth(cellSize * 0.95);
            iv.setFitHeight(cellSize * 0.95);

            StackPane tileCell = new StackPane(iv);
            tileCell.setPrefSize(cellSize, cellSize);
            tileCell.setMinSize(cellSize, cellSize);
            tileCell.setMaxSize(cellSize, cellSize);
            tileCell.setAlignment(Pos.CENTER);

            final int choice = i;

            tileCell.setOnMouseClicked(e -> {
                peekchoice = (choice + 1);
                updateGUI();
            });

            grid.add(tileCell, i, 0);
        }


        VBox controls = new VBox(10);
        controls.setPrefWidth(250);
        controls.setLayoutX(Width/1.5);
        controls.setLayoutY(Height/4.8);

        Button back = new Button("Back to the ship");
        back.setMaxWidth(Double.MAX_VALUE);
        back.setOnAction(e ->
        {
            peeking = false;
            peekchoice = -1;
            updateGUI();
        });

        back.setId("AssemblyButton");
        controls.getChildren().addAll(back);

        anchor.getChildren().addAll(grid, controls);
        root.getChildren().add(anchor);
    }

    private void displayCardsDeck(){
        root.getChildren().clear();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #191970, #7B68EE);");

        AnchorPane anchor = new AnchorPane();
        anchor.setPrefSize(Width, Height);

        Label label = new Label(gui.getLastLog());
        label.setLayoutX(Width/92);
        label.setLayoutY(Height/1.45);
        anchor.getChildren().add(label);

        double gridX = (double) Width/8;
        double gridY = (double) Height/4.8;
        double cellSize = (double) Height/4;
        int cols = 3;
        int rows = 1;

        GridPane grid = new GridPane();
        grid.setPrefSize(cellSize * cols, cellSize);
        grid.setLayoutX(gridX);
        grid.setLayoutY(gridY);
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(Insets.EMPTY);
        grid.setGridLinesVisible(false);

        for (int i = 0; i < cols; i++) {
            ColumnConstraints cc = new ColumnConstraints(cellSize);
            cc.setMinWidth(cellSize);
            cc.setPrefWidth(cellSize);
            cc.setMaxWidth(cellSize);
            grid.getColumnConstraints().add(cc);
        }

        RowConstraints rc = new RowConstraints(cellSize);
        rc.setMinHeight(cellSize);
        rc.setPrefHeight(cellSize);
        rc.setMaxHeight(cellSize);
        grid.getRowConstraints().add(rc);

        if(peekchoice == -1)
            throw new RuntimeException();

        List<AdventureCard> cards = gui.getPeekableCards(peekchoice);

        String[] ids = {cards.getFirst().getID(), cards.get(1).getID(), cards.get(2).getID()};
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            String path = "/it/polimi/ingsw/GUI/cards/" + id + ".jpg";

            ImageView iv = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
            iv.setPreserveRatio(true);
            iv.setFitWidth(cellSize * 0.95);
            iv.setFitHeight(cellSize * 0.95);

            StackPane tileCell = new StackPane(iv);
            tileCell.setPrefSize(cellSize, cellSize);
            tileCell.setMinSize(cellSize, cellSize);
            tileCell.setMaxSize(cellSize, cellSize);
            tileCell.setAlignment(Pos.CENTER);

            grid.add(tileCell, i, 0);
        }


        VBox controls = new VBox(10);
        controls.setPrefWidth(250);
        controls.setLayoutX(Width/1.5);
        controls.setLayoutY(Height/4.8);

        Button back = new Button("Back to the ship");
        back.setMaxWidth(Double.MAX_VALUE);
        back.setOnAction(e ->
        {
            peeking = false;
            peekchoice = -1;
            updateGUI();
        });

        back.setId("AssemblyButton");
        controls.getChildren().addAll(back);

        anchor.getChildren().addAll(grid, controls);
        root.getChildren().add(anchor);
    }


    private void write(String s){
        writer.println(s);
        writer.flush();
    }

    private boolean isPeeking(){
        return peeking;
    }

    private int getPeekchoice(){
        return peekchoice;
    }

}
