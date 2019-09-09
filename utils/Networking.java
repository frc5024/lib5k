package frc.lib5k.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import frc.lib5k.utils.RobotLogger.Level;

public class Networking {
    private static RobotLogger logger = RobotLogger.getInstance();

    /**
     * A list of valid TCP and UDP ports as defined in the FMS whitepaper
     */
    public static int[] validPorts = {
        554, 1180, 1181, 1182, 1183, 1184, 1185, 1186, 
        1187, 1188, 1189, 1190, 5800, 5801, 5802, 5803, 
        5804, 5805, 5806, 5807, 5808, 5809, 5810
    };

    public static String readFrom(ServerSocket sock){
        // Accept data from socket
        Socket conn;
        try {
            conn = sock.accept();
        } catch (IOException e) {
            logger.log("Failed to read data from socket", Level.kWarning);
            return null;
        }

        // Buffer data from conn
        BufferedReader inputStream ;
        try {
            inputStream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e) {
            logger.log("Failed to read data from socket", Level.kWarning);
            return null;
        }

        // Return the buffered input
        try {
            return inputStream.readLine();
        } catch (IOException e) {
            logger.log("Failed to read data from socket", Level.kWarning);
            return null;
        }

    }
}