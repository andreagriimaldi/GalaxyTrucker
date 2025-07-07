package it.polimi.ingsw;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Constants {
    public final static String localMachineServerIP = "localhost";
    public final static int SOCKET_PORT = 1098;
    public final static int BACKLOG = 25;

    public final static String RMI_ENTRY_POINT_NAME = "rmi://server/startconnection";
    public final static String RMI_SERVER_NAME = "TEST_SERVER";
    public final static int RMI_REGISTRY_PORT = 1099;

    public final static int heartbeatGenerationIntervalInMs = 1000;
    public final static int heartbeatTimeoutInMs = 3000;
    public final static int heartbeatCheckIntervalInMs = 10000;


    public final static int TIME_IN_BETWEEN_CARDS = 6000;


    // da controllare
    public static boolean isValidIPv4(String ip) {
        if (ip == null || ip.isEmpty()) return false;

        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;

        for (String part : parts) {
            try {
                if (part.length() > 1 && part.startsWith("0")) return false; // No leading zeroes
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;

    }


    public static boolean isNumeric(String input) {
        if (input == null) return false;
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /*
    public static String computePasswordHash(String password) {

    }
     */

    public static void printIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            System.out.println("");

            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();

                // Ignore loopback and inactive interfaces
                if (iface.isLoopback() || !iface.isUp()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // Only print IPv4 addresses
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(":") == -1) {
                        System.out.println("Interface: " + iface.getDisplayName());
                        System.out.println("Local IP: " + addr.getHostAddress());
                        System.out.println("");
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}