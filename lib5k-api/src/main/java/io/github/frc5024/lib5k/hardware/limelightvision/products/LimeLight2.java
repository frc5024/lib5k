package io.github.frc5024.lib5k.hardware.limelightvision.products;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import io.github.frc5024.lib5k.hardware.limelightvision.BaseLimeLight;

/**
 * Client for the LimeLight version 2.0 and 2+
 */
public class LimeLight2 extends BaseLimeLight {

    /**
     * Create a LimeLight2 client using the default NT table "limelight"
     * 
     * @param ipAddress IP address of the device
     */
    public LimeLight2(String ipAddress) {
        this("limelight", ipAddress);
    }

    /**
     * Create a LimeLight2 client
     * 
     * @param networkTableName NT table for the device
     * @param ipAddress        IP address of the device
     */
    public LimeLight2(String networkTableName, String ipAddress) {
        this(NetworkTableInstance.getDefault().getTable(networkTableName), ipAddress);
    }

    /**
     * Create a LimeLight2 client
     * 
     * @param networkTable NT table for the device
     * @param ipAddress    IP address of the device
     */
    public LimeLight2(NetworkTable networkTable, String ipAddress) {
        super(networkTable, ipAddress, 27.0, 20.5);
    }

}