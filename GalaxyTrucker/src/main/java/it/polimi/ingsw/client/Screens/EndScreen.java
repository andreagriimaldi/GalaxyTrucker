package it.polimi.ingsw.client.Screens;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.GUI;
import it.polimi.ingsw.enums.ComponentSide;
import it.polimi.ingsw.enums.FlightType;
import it.polimi.ingsw.model.playerset.Player;
import it.polimi.ingsw.model.playerset.PlayerColor;
import it.polimi.ingsw.model.utilities.components.Component;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class EndScreen {
    private Stage stage;

    private final GUI gui;
    private final PrintWriter writer;
    private final ClientController controller;
    private final Scene scene;
    private final VBox root;

    private boolean rocketship = false;
    private PlayerColor shipColor;

    private int Width;
    private int Height;

    public EndScreen(Stage stage, GUI gui, PrintWriter writer, ClientController c, int W, int H){
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
    }

    public Scene getScene(){
        return scene;
    }

    public void updateGUI(){
        if(rocketship){
            displayRocketship(shipColor);
        }
        else{
            displayEndScreen();
        }
    }

    public void displayEndScreen(){
        root.getChildren().clear();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #AAAAAA, #000043);");

        root.setAlignment(Pos.CENTER);
        root.setSpacing(15);
        root.setPadding(new Insets(20));

        String positionLog = "";

        Map<PlayerColor, double[]> scores = gui.getFinalScore();

        Map<PlayerColor, Double> positions = new HashMap<>();

        for(PlayerColor c: scores.keySet()){
            double total = scores.get(c)[scores.get(c).length - 1];
            positions.put(c,total);
        }

        Map<PlayerColor, Double> sortedDescending = positions.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        int pos0 = getPosition(sortedDescending, gui.getColor());
        if (pos0 < 0) {
            positionLog = "Something went wrong: your color not in ranking.";
        } else {
            int myPosition = pos0 + 1;
            switch (myPosition) {
                case 1 -> positionLog = "You are the winner!";
                case 2 -> positionLog = "You have finished second. You'll do better next time";
                case 3 -> positionLog = "You have finished third. You'll do better next time";
                case 4 -> positionLog = "You have finished fourth. You'll do better next time";
                default -> positionLog = "ERROR: unexpected position " + myPosition;
            }
        }


        Label position = new Label(positionLog);
        position.setStyle("-fx-text-fill: white; -fx-font-size: 30px;");
        position.setAlignment(Pos.CENTER);
        position.setMaxWidth(Double.MAX_VALUE);

        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (Map.Entry<PlayerColor, Double> entry : sortedDescending.entrySet()) {
            sb.append(rank)
                    .append(". ")
                    .append(entry.getKey().name())
                    .append(" → ")
                    .append(entry.getValue())
                    .append(" credits");
            if (rank < sortedDescending.size()) {
                sb.append("\n");
            }
            rank++;
        }
        String multiLogText = sb.toString();


        Label score = new Label(multiLogText);
        score.setWrapText(true);
        score.setTextAlignment(TextAlignment.CENTER);
        score.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        score.setMaxWidth( (int) root.getWidth() - 40 );
        score.setAlignment(Pos.CENTER);
        score.setMaxWidth(400);

        FlowPane buttonPane = new FlowPane();
        buttonPane.setHgap(10);
        buttonPane.setVgap(10);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setPadding(new Insets(10));
        buttonPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 1;");

        List<PlayerColor> colors = gui.getOtherColors();
        if(!colors.contains(gui.getColor())){
            colors.add(gui.getColor());
        }

        for (PlayerColor color : colors) {
            String c = "";
            switch (color){
                case RED -> {
                    c = "red";
                }
                case BLUE -> {
                    c = "blue";
                }
                case YELLOW -> {
                    c = "yellow";
                }
                case GREEN -> {
                    c = "green";
                }
            }
            Button b = new Button("Show " + c);
            b.setOnAction(e -> {
                rocketship = true;
                shipColor = color;
                updateGUI();
            });
            switch (color){
                case RED -> {
                    b.setStyle("-fx-font-size: 14px; -fx-background-color: #ff4444; -fx-text-fill: white;");
                }
                case BLUE -> {
                    b.setStyle("-fx-font-size: 14px; -fx-background-color: #0000FF; -fx-text-fill: white;");
                }
                case YELLOW -> {
                    b.setStyle("-fx-font-size: 14px; -fx-background-color: #FFD700; -fx-text-fill: white;");
                }
                case GREEN -> {
                    b.setStyle("-fx-font-size: 14px; -fx-background-color: #008000; -fx-text-fill: white;");
                }
            }
            buttonPane.getChildren().add(b);
        }

        root.getChildren().addAll(position, score, buttonPane);
    }

    public void displayRocketship(PlayerColor color){
        if(gui.getType().equals(FlightType.TRIAL)){
            displayRocketshipTrial(color);
        }
        else{
            displayRocketshipTwo(color);
        }
    }

    public void displayRocketshipTrial(PlayerColor color){
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

        String bgPath = "/it/polimi/ingsw/GUI/cardboard/cardboard-1.jpg";
        Image bgImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(bgPath)
        ));
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(Width/1.5);
        bgView.setFitHeight(Height/1.5);
        bgView.setPreserveRatio(true);
        rocketPane.getChildren().add(bgView);

        Component[][] board = null;
        if(color.equals(gui.getColor())){
            board = gui.getMyRocketshipBoard();
        }
        else{
            board = gui.getOtherRocketshipBoard(color);
        }
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

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPrefWidth(250);

        Button backBtn = new Button("Back to the menu");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> {
            rocketship = false;
            updateGUI();
        });

        double[] values = gui.getFinalScore().get(color);

        String[] labels = {
                "(FLIGHT CREDITS)",
                "(PROFIT FROM RESOURCES)",
                "(CONNECTORS PRIZE)",
                "(LOST COMPONENT PENALTY)",
                "(FINISHING ORDER BONUS)",
                "(TOTAL)"
        };

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            String prefix = (i < values.length - 1) ? "+ " : "= ";
            sb.append(prefix)
                    .append(values[i])
                    .append(" ")
                    .append(labels[i])
                    .append("\n");
        }

        Label creditsLabel = new Label(sb.toString().trim());
        creditsLabel.setWrapText(true);
        creditsLabel.setTextAlignment(TextAlignment.CENTER);
        creditsLabel.setStyle("-fx-font-family: monospace; -fx-text-fill: white;");


        controls.getChildren().addAll(backBtn, creditsLabel);

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

    public void displayRocketshipTwo(PlayerColor color){
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

        String bgPath = "/it/polimi/ingsw/GUI/cardboard/cardboard-1b.jpg";
        Image bgImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(bgPath)
        ));
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(Width/1.5);
        bgView.setFitHeight(Height/1.5);
        bgView.setPreserveRatio(true);
        rocketPane.getChildren().add(bgView);

        Component[][] board = null;
        if(color.equals(gui.getColor())){
            board = gui.getMyRocketshipBoard();
        }
        else{
            board = gui.getOtherRocketshipBoard(color);
        }
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

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPrefWidth(250);

        Button backBtn = new Button("Back to the menu");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> {
            rocketship = false;
            updateGUI();
        });

        double[] values = gui.getFinalScore().get(color);

        String[] labels = {
                "(FLIGHT CREDITS)",
                "(PROFIT FROM RESOURCES)",
                "(CONNECTORS PRIZE)",
                "(LOST COMPONENT PENALTY)",
                "(FINISHING ORDER BONUS)",
                "(TOTAL)"
        };

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            String prefix = (i < values.length - 1) ? "+ " : "= ";
            sb.append(prefix)
                    .append(values[i])
                    .append(" ")
                    .append(labels[i])
                    .append("\n");
        }

        Label creditsLabel = new Label(sb.toString().trim());
        creditsLabel.setWrapText(true);
        creditsLabel.setTextAlignment(TextAlignment.CENTER);
        creditsLabel.setStyle("-fx-font-family: sans-serif; -fx-text-fill: white;");


        controls.getChildren().addAll(backBtn, creditsLabel);

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

    private void write(String s){
        writer.println(s);
        writer.flush();
    }

    public static <K, V> int getPosition(Map<K, V> sortedMap, K targetKey) {
        int index = 0;
        for (K key : sortedMap.keySet()) {
            if (key.equals(targetKey)) {
                return index;             // zero-based: 0 è primo, 1 è secondo, …
            }
            index++;
        }
        return -1;  // non trovato
    }

}
