package it.polimi.ingsw.client;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.util.Objects;

public class GUIApp extends Application {
    // constants for screen resolution
    private static int W = 1920;
    private static int H = 1080;
    private static ClientController controller;
    private static PrintWriter writer;

    /**
     * setController() allows the GUI app to have a reference to the ClientController
     */
    public static void setController(ClientController c){
        controller = c;
    }

    public static void setWriter(PrintWriter w){
        writer = w;
    }

    public void start(Stage primaryStage){
        primaryStage.setWidth(W);
        primaryStage.setHeight(H);

        primaryStage.setFullScreen(true);

        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/GUI/icon.png")));
        primaryStage.getIcons().add(logo);

        ViewModel vm = controller.getViewModel();
        GUI gui = new GUI(vm, primaryStage, writer, controller, W, H);
        controller.setGUI(gui);
        gui.initializeView();
    }

    public static void launchGUI(){
        javafx.application.Application.launch(String.valueOf(GUIApp.class));
    }
}
