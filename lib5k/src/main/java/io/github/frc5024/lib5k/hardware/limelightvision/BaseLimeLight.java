package io.github.frc5024.lib5k.hardware.limelightvision;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import edu.wpi.first.networktables.NetworkTable;
import io.github.frc5024.lib5k.vision.types.HyperbolicAxisAlignedBoundingBox;

public abstract class BaseLimeLight {

    // Network Table
    private final NetworkTable dataTable;

    // Camera dimensions
    private final double maxXAngleDegrees;
    private final double maxYAngleDegrees;

    // Other info
    private final String ipAddress;

    public BaseLimeLight(NetworkTable remoteTable, String ipAddress, double maxXAngleDegrees, double maxYAngleDegrees) {

        // Set up the NT table
        this.dataTable = remoteTable;
        this.ipAddress = ipAddress;

        // Set up the camera dimensions
        this.maxXAngleDegrees = maxXAngleDegrees;
        this.maxYAngleDegrees = maxYAngleDegrees;
    }

    public void portForwardCameraStream(int localPort) {

    }
    
    public boolean hasTarget() {
        return false;
    }

    public @Nullable @CheckForNull HyperbolicAxisAlignedBoundingBox getTargetOrNull() {
        return null;
    }

}