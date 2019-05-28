package frc.common.network;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

public class NetworkTables {
    private static NetworkTables instance = null;

    NetworkTableInstance inst = NetworkTableInstance.getDefault();

    /**
     * Dont ever call this. Bad things can happen.
     * 
     * This function only exsists for use during robot configuration
     */
    public void setPeriod(double period) {
        this.inst.setUpdateRate(period);
    }

    public static NetworkTables getInstance() {
        if (instance == null) {
            instance = new NetworkTables();
        }
        return instance;
    }

    public NetworkTable getTable(String table) {
        return inst.getTable(table);
    }

    public NetworkTableEntry getEntry(String table, String entry) {
        return inst.getTable(table).getEntry(entry);
    }
}