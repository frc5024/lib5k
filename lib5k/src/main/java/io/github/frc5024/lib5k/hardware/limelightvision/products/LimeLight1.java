package io.github.frc5024.lib5k.hardware.limelightvision.products;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import io.github.frc5024.lib5k.hardware.limelightvision.BaseLimeLight;

/**
 * Client for the LimeLight version 1.0
 */
public class LimeLight1 extends BaseLimeLight {

    /**
     * Create a LimeLight1 client using the default NT table "limelight"
     * 
     * @param ipAddress IP address of the device
     */
    public LimeLight1(String ipAddress) {
        this("limelight", ipAddress);
    }

    /**
     * Create a LimeLight1 client
     * 
     * @param networkTableName NT table for the device
     * @param ipAddress        IP address of the device
     */
    public LimeLight1(String networkTableName, String ipAddress) {
        this(NetworkTableInstance.getDefault().getTable(networkTableName), ipAddress);
    }

    /**
     * Create a LimeLight1 client
     * 
     * @param networkTable NT table for the device
     * @param ipAddress    IP address of the device
     */
    public LimeLight1(NetworkTable networkTable, String ipAddress) {
        super(networkTable, ipAddress, 27.0, 20.5);
    }

}