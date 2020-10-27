package io.github.frc5024.lib5k.utils;

public class FMSNetworking {

    public enum SocketType {
        UDP, TCP;
    }

    /**
     * Check if a network port is allowed through the FMS firewall
     * 
     * @param port Port
     * @param type Type of socket
     * @return Is allowed?
     */
    public static boolean isPortAllowedByFMS(int port, SocketType type) {

        // RoboRIO CS ports
        if (port >= 1180 && port <= 1190) {
            return true;
        }

        // Team-use
        if (port >= 5800 && port <= 5810) {
            return true;
        }

        // TCP
        if (type.equals(SocketType.TCP)) {
            if (port == 1745 || port == 80 || port == 443 || port == 554) {
                return true;
            }
        } else if (type.equals(SocketType.UDP)) {
            if (port == 1130 || port == 1140 || port == 554) {
                return true;
            }
        }

        return false;
    }

}