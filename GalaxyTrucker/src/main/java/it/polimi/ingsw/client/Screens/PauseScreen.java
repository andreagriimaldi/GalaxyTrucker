package it.polimi.ingsw.client.Screens;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.GUI;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintWriter;

public class PauseScreen {
    private Stage stage;

    private final GUI gui;
    private final PrintWriter writer;
    private final ClientController controller;
    private final Scene scene;
    private final VBox root;

    private int Width;
    private int Height;

    public PauseScreen(Stage stage, GUI gui, PrintWriter writer, ClientController c, int W, int H){
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
        if(gui.getLastLog() != null) {
            if(gui.getLastLog().contains("seconds left"))
                displayPause();
            else if(gui.getLastLog().contains("time is up. the remaining player won the game"))
                displayWin();
        }
    }

    public void displayPause(){
        root.getChildren().clear();

        root.setStyle("-fx-background-color: linear-gradient(to bottom, #00048B, #FFBFE4);");

        Label waitingLabel = new Label("One player left. Timer has started");
        waitingLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(waitingLabel);

        if(gui.getLastLog().contains("seconds left")){
            Label timerLabel = new Label(gui.getLastLog());
            timerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            vbox.getChildren().addAll(timerLabel);
        }

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(80, 80);

        vbox.getChildren().add(spinner);

        root.getChildren().add(vbox);
    }

    public void displayWin(){
        root.getChildren().clear();

        root.setStyle("-fx-background-color: linear-gradient(to bottom, #00048B, #FFBFE4);");

        Label waitingLabel = new Label("You are the last remaining player. You win!!");
        waitingLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(waitingLabel);


        root.getChildren().add(vbox);
    }
}
