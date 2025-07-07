package it.polimi.ingsw.client.Screens;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.GUI;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.model.utilities.components.Component;
import it.polimi.ingsw.enums.ComponentSide;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TakeoffScreen {
    private Stage stage;

    private final GUI gui;
    private final PrintWriter writer;
    private final ClientController controller;
    private final Scene scene;
    private final VBox root;

    private int num;
    private int times = 0;

    List<String> aliensChoices;
    List<String> responses;

    private int Width;
    private int Height;

    public TakeoffScreen(Stage stage, GUI gui, PrintWriter writer, ClientController c, int W, int H){
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

        scene = new Scene(root, Width, Height);

        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        root.getStylesheets().add(getClass()
                .getResource("/it/polimi/ingsw/GUI/style.css")
                .toExternalForm());

        displayTransition();
    }

    public Scene getScene(){
        return scene;
    }

    public void updateGUI(){
        if(gui.isFixedShip()){
            if(gui.isToPopulate()){
                if(!gui.isPopulated()){
                    showPopulate();
                }
                else{
                    showWaiting();
                }
            }
            else{
                showWaiting();
            }
        }
        else{
            showShipRepair();
        }
    }

    private void displayTransition(){
        root.getChildren().clear();

        root.setStyle("-fx-background-color: linear-gradient(to bottom, #00048B, #FFBFE4);");

        Label waitingLabel = new Label("Transitioning to takeoff phase!");
        waitingLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(80, 80);

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(waitingLabel, spinner);

        root.getChildren().add(vbox);
    }

    private void showWaiting(){
        root.getChildren().clear();

        root.setStyle("-fx-background-color: linear-gradient(to bottom, #00008B, #FFE4C4);");

        Label waitingLabel = new Label("Your ship is ready for the flight, waiting on other players!");
        waitingLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(80, 80);

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(waitingLabel, spinner);

        root.getChildren().add(vbox);

        Task<Void> attesaTask5 = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (!gui.getPhase().equals(GamePhase.FLIGHT_PHASE)) {
                    Thread.sleep(100);
                }
                return null;
            }
        };

        attesaTask5.setOnSucceeded(e -> {
            Platform.runLater(gui::showFlight);
        });

        new Thread(attesaTask5).start();
    }

    private void showShipRepair(){
        if(gui.getType().equals(FlightType.TRIAL)){
            showShipRepairTrial();
        }
        else{
            showShipRepairTwo();
        }
    }

    private void showShipRepairTrial(){
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

        Button detachBtn = new Button("Detach component");
        detachBtn.setMaxWidth(Double.MAX_VALUE);
        detachBtn.setOnAction(e -> {
            rocketPane.setOnMouseClicked(event -> {
                double mouseX = event.getX();
                double mouseY = event.getY();

                int col = (int) ((mouseX - externalOffsetX) / (cellWidth + spacingX));
                int row = (int) ((mouseY - externalOffsetY) / (cellHeight + spacingY));

                if (row >= 0 && col >= 0) {
                    write("DETACH " + (row + 5) + " " + (col + 4));
                    rocketPane.setOnMouseClicked(null);
                }
            });
        });

        detachBtn.setId("AssemblyButton");

        controls.getChildren().addAll(
                detachBtn
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

    private void showShipRepairTwo(){
        root.getChildren().clear();

        root.setStyle("-fx-background-color: linear-gradient(to bottom, #191970, #7B68EE);");

        AnchorPane anchor = new AnchorPane();
        anchor.setPrefSize(Width, Height);
        anchor.getStyleClass().add("TrialRocketship");

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

        Button detachBtn = new Button("Detach component");
        detachBtn.setMaxWidth(Double.MAX_VALUE);
        detachBtn.setOnAction(e -> {
            rocketPane.setOnMouseClicked(event -> {
                double mouseX = event.getX();
                double mouseY = event.getY();

                int col = (int) ((mouseX - externalOffsetX) / (cellWidth + spacingX));
                int row = (int) ((mouseY - externalOffsetY) / (cellHeight + spacingY));

                if (row >= 0 && col >= 0) {
                    write("DETACH " + (row + 5) + " " + (col + 4));
                    rocketPane.setOnMouseClicked(null);
                }
            });
        });

        detachBtn.setId("AssemblyButton");

        controls.getChildren().addAll(
                detachBtn
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

    private void showPopulate(){
        root.getChildren().clear();

        root.setStyle("-fx-background-color: linear-gradient(to bottom, #8A2BE2, #A0522D);");

        num = gui.getNumberOfCabins();
        aliensChoices = gui.getAliensChoices();
        responses = new LinkedList<>();

        if(gui.getType().equals(FlightType.TRIAL)){
            showPopulateTrial(aliensChoices.getFirst());
        }
        else{
            showPopulateTwo(aliensChoices.getFirst());
        }
    }

    private void showPopulateTrial(String option){
        root.getChildren().clear();

        AnchorPane anchor = new AnchorPane();
        anchor.setPrefSize(Width, Height);
        anchor.getStyleClass().add("TrialRocketship");

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

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPrefWidth(1000);

        Label title = new Label("You need to choose who's going to come with you in this cosmic trip!");

        title.setStyle("-fx-font-size: 20px;");

        Label opt = new Label(option);

        opt.setStyle("-fx-font-size: 15px;");

        Button human = new Button("Humans");
        Button purple = new Button("Purple");
        Button brown = new Button("Browns");

        human.setId("AssemblyButton");
        purple.setId("AssemblyButton");
        brown.setId("AssemblyButton");

        human.setOnAction(e -> {
            responses.add("HUMAN");
            times++;
            nextCabin();
        });

        purple.setOnAction(e -> {
            responses.add("PURPLE");
            times++;
            nextCabin();
        });

        brown.setOnAction(e -> {
            responses.add("BROWN");
            times++;
            nextCabin();
        });

        HBox buttonBox = new HBox(10, human, purple, brown);
        buttonBox.setAlignment(Pos.TOP_LEFT);

        controls.getChildren().addAll(title,opt,buttonBox);

        rocketPane.setLayoutX((double) Width/85);
        rocketPane.setLayoutY((double) Height/54);

        mainLayout.setLayoutX(Width/1.8);
        mainLayout.setLayoutY((double) Height/54);

        root.getChildren().add(anchor);

        mainLayout.getChildren().addAll(controls);
        anchor.getChildren().add(rocketPane);
        anchor.getChildren().add(mainLayout);
    }

    private void showPopulateTwo(String option){
        root.getChildren().clear();

        AnchorPane anchor = new AnchorPane();
        anchor.setPrefSize(Width, Height);
        anchor.getStyleClass().add("TrialRocketship");

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

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPrefWidth(1000);

        Label title = new Label("You need to choose who's going to come with you in this cosmic trip!");

        title.setStyle("-fx-font-size: 20px;");

        Label opt = new Label(option);

        opt.setStyle("-fx-font-size: 15px;");

        Button human = new Button("Humans");
        Button purple = new Button("Purple");
        Button brown = new Button("Browns");

        human.setId("AssemblyButton");
        purple.setId("AssemblyButton");
        brown.setId("AssemblyButton");

        human.setOnAction(e -> {
            responses.add("HUMAN");
            times++;
            nextCabin();
        });

        purple.setOnAction(e -> {
            responses.add("PURPLE");
            times++;
            nextCabin();
        });

        brown.setOnAction(e -> {
            responses.add("BROWN");
            times++;
            nextCabin();
        });

        HBox buttonBox = new HBox(10, human, purple, brown);
        buttonBox.setAlignment(Pos.TOP_LEFT);

        controls.getChildren().addAll(title,opt,buttonBox);

        rocketPane.setLayoutX((double) Width/85);
        rocketPane.setLayoutY((double) Height/54);

        mainLayout.setLayoutX(Width/1.4);
        mainLayout.setLayoutY((double) Height/54);

        root.getChildren().add(anchor);

        mainLayout.getChildren().addAll(controls);
        anchor.getChildren().add(rocketPane);
        anchor.getChildren().add(mainLayout);
    }

    private void nextCabin(){
        if(times == num){
            StringBuilder response = new StringBuilder("POPULATE ");
            for(int i = 0; i < num; i++){
                response.append(responses.get(i)).append(" ");
            }
            write(response.toString());
            System.out.println(response.toString());
        }
        else if(times < num){
            if(gui.getType().equals(FlightType.TRIAL)){
                showPopulateTrial(aliensChoices.get(times));
            }
            else{
                showPopulateTwo(aliensChoices.get(times));
            }
        }
    }

    private void write(String s){
        writer.println(s);
        writer.flush();
    }
}
