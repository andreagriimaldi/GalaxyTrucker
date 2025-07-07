package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.enums.ConnectionType;
import it.polimi.ingsw.enums.UIType;
import org.jetbrains.annotations.NotNull;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.InetAddress;
import java.util.Enumeration;

import static it.polimi.ingsw.Constants.printIP;


public class ClientApp {


    public static void main(String @NotNull [] args) {
        System.out.println(" .d8888b.         d8888 888             d8888 Y88b   d88P Y88b   d88P      88888888888 8888888b.  888     888  .d8888b.  888    d8P  8888888888 8888888b.  \n" +
                "d88P  Y88b       d88888 888            d88888  Y88b d88P   Y88b d88P           888     888   Y88b 888     888 d88P  Y88b 888   d8P   888        888   Y88b \n" +
                "888    888      d88P888 888           d88P888   Y88o88P     Y88o88P            888     888    888 888     888 888    888 888  d8P    888        888    888 \n" +
                "888            d88P 888 888          d88P 888    Y888P       Y888P             888     888   d88P 888     888 888        888d88K     8888888    888   d88P \n" +
                "888  88888    d88P  888 888         d88P  888    d888b        888              888     8888888P\"  888     888 888        8888888b    888        8888888P\"  \n" +
                "888    888   d88P   888 888        d88P   888   d88888b       888              888     888 T88b   888     888 888    888 888  Y88b   888        888 T88b   \n" +
                "Y88b  d88P  d8888888888 888       d8888888888  d88P Y88b      888              888     888  T88b  Y88b. .d88P Y88b  d88P 888   Y88b  888        888  T88b  \n" +
                " \"Y8888P88 d88P     888 88888888 d88P     888 d88P   Y88b     888              888     888   T88b  \"Y88888P\"   \"Y8888P\"  888    Y88b 8888888888 888   T88b \n" +
                "\n");

        boolean connectionTypeWasDefined = false;
        ConnectionType connectionType = ConnectionType.RMI;

        for (String arg : args) {
            if (arg.equals("--socket")) {
                connectionType = ConnectionType.SOCKET;
                connectionTypeWasDefined = true;
                break;
            } else if (arg.equals("--rmi")) {
                connectionType = ConnectionType.RMI;
                connectionTypeWasDefined = true;
                break;
            }
        }

        if (!connectionTypeWasDefined) {
            System.err.println("Connection type was not defined.");
        }


        boolean uiTypeWasDefined = false;
        UIType uiType = UIType.TUI;

        for (String arg : args) {
            if (arg.equals("--tui")) {
                uiType = UIType.TUI;
                uiTypeWasDefined = true;
                break;
            } else if (arg.equals("--gui")) {
                uiType = UIType.GUI;
                uiTypeWasDefined = true;
                break;
            }
        }

        if (!uiTypeWasDefined) {
            System.out.println("UI type was not defined.");
        } else {
            System.out.println("Starting client using " + connectionType.toString() + " and " + uiType.toString());

            /** HERE THE CLIENT CONTROLLER IS ACTUALLY INITIALIZED **/
            printIP();
            initializeClient(connectionType, uiType);
        }
    }


    public static void initializeClient(ConnectionType connectionType, UIType uiType) {
        ClientController clientController = new ClientController(connectionType, uiType);
    }



}