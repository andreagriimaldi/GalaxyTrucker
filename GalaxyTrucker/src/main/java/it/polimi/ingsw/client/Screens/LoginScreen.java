package it.polimi.ingsw.client.Screens;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.GUI;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class LoginScreen{

    private int Width;
    private int Height;

    private final ClientController controller;

    private final PrintWriter writer;

    private final GUI gui;
    private final Scene scene;
    private final VBox root;
    private TextField ipField;

    private Timeline refreshTimeline;
    private List<String[]> availableGames = new ArrayList<>();

    public LoginScreen(GUI gui, PrintWriter writer, ClientController c, int W, int H){
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

        if(cssFile != null){
            root.getStylesheets().add(cssFile);
        } else {
            System.err.println("CSS file not found at " + cssPath);
        }

        setupIPInput();

        scene = new Scene(root, Width, Height);
    }

    public Scene getScene(){
        return scene;
    }

    private void setupIPInput() {
        root.getChildren().clear();

        ImageView logo = null;
        String logoPath = "/it/polimi/ingsw/GUI/general/logo.jpg";
        java.net.URL logoURL = getClass().getResource(logoPath);

        if (logoURL != null) {
            logo = new ImageView(new Image(logoURL.toExternalForm()));
            logo.getStyleClass().add("logo-image");
            logo.setFitWidth(500);
            logo.setPreserveRatio(true);
            logo.setTranslateY(-50);
        } else {
            System.err.println("Logo not found at: " + logoPath);
        }


        Button connectLocallyButton = new Button("Connect Locally");
        connectLocallyButton.getStyleClass().add("button");
        connectLocallyButton.setOnAction(e -> {
            String IP = "CONNECT LOCAL";
            write(IP);
        });
        connectLocallyButton.setScaleX(2.0);
        connectLocallyButton.setScaleY(2.0);

        Label ipLabel = new Label("Insert server's IP:");
        ipField = new TextField();
        ipField.setPromptText("es. 192.168.1.100");

        Button connectButton = new Button("Connect");
        connectButton.getStyleClass().add("button");
        connectButton.setOnAction(e -> {
            String IP = "CONNECT " + ipField.getText();
            write(IP);
        });

        VBox connectBox = new VBox(15);
        connectBox.setAlignment(Pos.BOTTOM_CENTER);
        connectBox.getChildren().addAll(ipLabel, ipField, connectButton);
        connectBox.setTranslateY(50);


        root.getChildren().addAll(logo, connectLocallyButton, connectBox);

        Task<Void> attesaTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (!controller.isConnected()) {
                    Thread.sleep(100);
                }
                return null;
            }
        };

        attesaTask.setOnSucceeded(e -> {
            setupRegister();
        });

        new Thread(attesaTask).start();

    }

    private void setupRegister(){
        root.getChildren().clear();

        Label userLabel = new Label("Insert username:");
        TextField userField = new TextField();
        userField.setPromptText("es. LucaGentile");

        Label passLabel = new Label("Insert password:");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Your password");

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();
            String registerMessage = "REGISTER " + username + " " + password;
            write(registerMessage);
        });

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();
            String loginMessage = "LOGIN " + username + " " + password;
            write(loginMessage);
        });

        root.getChildren().addAll(userLabel, userField, passLabel, passField, registerButton, loginButton);

        Task<Void> attesaTask1 = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (!controller.isLoggedIn()) {
                    Thread.sleep(100);
                }
                return null;
            }
        };

        attesaTask1.setOnSucceeded(e -> {
            setupGames();
        });

        new Thread(attesaTask1).start();
    }

    public void setupGames(){
        root.getChildren().clear();

        availableGames = gui.getAvailableGames();

        Label joinLabel = new Label("Ongoing games:");
        joinLabel.setStyle("-fx-font-size: 25px; -fx-underline: true; -fx-font-weight: bold;");
        ListView<String> gamesListView = new ListView<>();
        gamesListView.setPrefHeight(250);

        for (String[] gameInfo : availableGames) {
            String displayText = String.format(
                    "ID: %s | Players: %s | Maximum players: %s | Level: %s",
                    gameInfo[0], gameInfo[1], gameInfo[2], gameInfo[3]
            );
            gamesListView.getItems().add(displayText);
        }

        Button joinButton = new Button("Join game");
        joinButton.setOnAction(e -> {
            int selectedIndex = gamesListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                String[] selectedGame = availableGames.get(selectedIndex);
                String gameId = selectedGame[0];
                String joinMsg = "JOIN_GAME " + gameId;
                write(joinMsg);
            } else {
                // Qui si potrebbe mostrare un alert all'utente: "Seleziona prima una partita!"
            }
        });

        Separator sep = new Separator();


        Label startLabel = new Label("Start a game:");
        startLabel.setStyle("-fx-font-size: 25px; -fx-underline: true; -fx-font-weight: bold;");

        Label playersLabel = new Label("Number of players (2-4):");
        ChoiceBox<Integer> playersChoice = new ChoiceBox<>();
        playersChoice.getItems().addAll(2, 3, 4);
        playersChoice.setValue(2);

        Label levelLabel = new Label("Choose flight's level:");
        ChoiceBox<String> levelChoice = new ChoiceBox<>();
        levelChoice.getItems().addAll("TRIAL", "TWO");
        levelChoice.setValue("TRIAL");

        Button startButton = new Button("Start the game");
        startButton.setOnAction(e -> {
            Integer numPlayers = playersChoice.getValue();
            String selectedLevel = levelChoice.getValue();
            String startMsg = "CREATE_NEW_GAME " + selectedLevel + " " + numPlayers;
            write(startMsg);
        });

        root.getChildren().addAll(
                joinLabel,
                gamesListView,
                joinButton,
                sep,
                startLabel,
                playersLabel,
                playersChoice,
                levelLabel,
                levelChoice,
                startButton
        );

        Timeline refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), ev -> {
                    availableGames= gui.getAvailableGames();
                    gamesListView.getItems().clear();
                    for (String[] gameInfo : availableGames) {
                        String displayText = String.format(
                                "ID: %s | Players: %s | Maximum players: %s | Level: %s",
                                gameInfo[0], gameInfo[1], gameInfo[2], gameInfo[3]
                        );
                        gamesListView.getItems().add(displayText);
                    }
                }),
                new KeyFrame(Duration.seconds(5))
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();

        Task<Void> attesaTask2 = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000);
                while (!controller.isWaiting()) {
                    Thread.sleep(100);
                }
                return null;
            }
        };

        attesaTask2.setOnSucceeded(e -> {
            setupWaitingForGame();
        });

        new Thread(attesaTask2).start();
    }

    private void setupWaitingForGame(){
        stopAutoRefresh();
        root.getChildren().clear();

        Label waitingLabel = new Label("Waiting for other players to join...");
        waitingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(80, 80);

        Button cancelButton = new Button("Quit game");
        cancelButton.setStyle("-fx-font-size: 14px; -fx-background-color: #FF6F61; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> {
            write("QUIT_GAME");
            setupGames();
        });

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(waitingLabel, spinner, cancelButton);

        root.getChildren().add(vbox);

        Task<Void> attesaTask3 = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (!controller.isInGame()) {
                    Thread.sleep(100);
                }
                return null;
            }
        };

        attesaTask3.setOnSucceeded(e -> {
            Platform.runLater(gui::showAssembly);
        });

        new Thread(attesaTask3).start();
    }

    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            refreshTimeline = null;
        }
    }


    private void write(String s){
        writer.println(s);
        writer.flush();
    }
}
