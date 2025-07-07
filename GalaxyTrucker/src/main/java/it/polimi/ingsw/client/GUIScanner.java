package it.polimi.ingsw.client;

import java.io.InputStream;
import java.util.Scanner;

public class GUIScanner {
    private final Scanner scanner;

    public GUIScanner(InputStream input){
        this.scanner = new Scanner(input);
    }

    public String nextLine() {
        while (!hasNextLine()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return scanner.nextLine();
    }

    public boolean hasNextLine(){
        return scanner.hasNextLine();
    }
}
