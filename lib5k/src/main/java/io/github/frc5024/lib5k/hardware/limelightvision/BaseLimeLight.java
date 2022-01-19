package io.github.frc5024.lib5k.hardware.limelightvision;

import javax.annotation.CheckForNull;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.util.net.PortForwarder;
import io.github.frc5024.lib5k.hardware.limelightvision.settings.LimeLightLEDMode;
import io.github.frc5024.lib5k.hardware.limelightvision.settings.LimeLightOperationMode;
import io.github.frc5024.lib5k.hardware.limelightvision.settings.LimeLightStreamMode;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.utils.FMSNetworking;
import io.github.frc5024.lib5k.utils.FMSNetworking.SocketType;
import io.github.frc5024.lib5k.vision.types.HyperbolicAxisAlignedBoundingBox;

/**
 * The base for interaction with LimeLight products. Following this
 * specification:
 * https://docs.limelightvision.io/en/latest/networktables_api.html
 */
public abstract class BaseLimeLight {

    // Limelight sizing divisor
    private static final double SIZING_DIVISOR = 320.0;

    // Logger
    private RobotLogger logger = RobotLogger.getInstance();

    // Network Table
    private final NetworkTable dataTable;

    // Camera dimensions
    private final double maxXAngleDegrees;
    private final double maxYAngleDegrees;

    // Other info
    private final String ipAddress;

    /**
     * Create a BaseLimeLight
     * 
     * @param remoteTable      Device NT table
     * @param ipAddress        Device IP address
     * @param maxXAngleDegrees Maximum X angle of the camera lense
     * @param maxYAngleDegrees Maximum Y angle of the camera lense
     */
    public BaseLimeLight(NetworkTable remoteTable, String ipAddress, double maxXAngleDegrees, double maxYAngleDegrees) {

        // Set up the NT table
        this.dataTable = remoteTable;
        this.ipAddress = ipAddress;

        // Set up the camera dimensions
        this.maxXAngleDegrees = maxXAngleDegrees;
        this.maxYAngleDegrees = maxYAngleDegrees;
    }

    /**
     * Port-forward the camera stream through the RoboRIO
     * 
     * @param localPort RoboRIO port to forward through
     */
    public void portForwardCameraStream(int localPort) {
        // Error if not a valid camera port
        if (!FMSNetworking.isPortAllowedByFMS(localPort, SocketType.TCP)) {
            throw new IllegalArgumentException(
                    String.format("TCP port %d is not allowed through the field firewall", localPort));
        }

        // Log action
        logger.log(String.format("Forwarding %s:5800/tcp to 0.0.0.0:%d/tcp", this.ipAddress, localPort));

        // Forward the MJPG stream port
        PortForwarder.add(localPort, this.ipAddress, 5800);
    }

    /**
     * Check if the LimeLight can see its target
     * 
     * @return Can see target?
     */
    public boolean hasTarget() {
        return dataTable.getEntry("tv").getNumber(0).intValue() == 1;
    }

    /**
     * Get the bounding box for the LimeLight target, or NULL if no target is found
     * 
     * @return HyperbolicAxisAlignedBoundingBox or NULL
     */
    public @CheckForNull HyperbolicAxisAlignedBoundingBox getTargetBoundsOrNull() {

        // Handle no target
        if (!hasTarget()) {
            return null;
        }

        // Get X and Y offsets
        double xOffset = dataTable.getEntry("tx").getDouble(0.0);
        double yOffset = dataTable.getEntry("ty").getDouble(0.0);

        // Get bounding dimensions
        double horizontalWidth = dataTable.getEntry("thor").getDouble(0.0);
        double verticalWidth = dataTable.getEntry("tvert").getDouble(0.0);

        // Convert the bounds to AABB sizes
        horizontalWidth = (horizontalWidth / (SIZING_DIVISOR / 2));
        verticalWidth = (verticalWidth / (SIZING_DIVISOR / 2));

        // Get position offsets
        double xOffsetNorm = xOffset / maxXAngleDegrees;
        double yOffsetNorm = yOffset / maxYAngleDegrees;

        // Corner coords
        double topLeftX = xOffsetNorm - (horizontalWidth / 2);
        double topLeftY = yOffsetNorm + (verticalWidth / 2);
        double bottomRightX = xOffsetNorm + (horizontalWidth / 2);
        double bottomRightY = yOffsetNorm - (verticalWidth / 2);

        // Calculate TL and BR corners
        Translation2d topLeft = new Translation2d(topLeftX, topLeftY);
        Translation2d bottomRight = new Translation2d(bottomRightX, bottomRightY);

        // Build an AABB
        HyperbolicAxisAlignedBoundingBox bounds = new HyperbolicAxisAlignedBoundingBox(topLeft, bottomRight,
                Rotation2d.fromDegrees(xOffset), Rotation2d.fromDegrees(yOffset),
                Rotation2d.fromDegrees(horizontalWidth * maxXAngleDegrees),
                Rotation2d.fromDegrees(verticalWidth * maxYAngleDegrees));

        return bounds;
    }

    /**
     * Get the number of MS of latency the camera takes to process an image
     * 
     * @return Latency in MS
     */
    public double getImageProcessingLatencyMS() {
        return dataTable.getEntry("tl").getDouble(0.0);
    }

    /**
     * Get the ID of the currently active image processing pipeline on-device
     * 
     * @return Current image pipeline ID
     */
    public int getActivePipelineID() {
        return dataTable.getEntry("getpipe").getNumber(0).intValue();
    }

    /**
     * Switch to a new image processing pipeline
     * 
     * @param id Pipeline ID
     */
    public void setActivePipeline(int id) {
        dataTable.getEntry("pipeline").setNumber(id);
    }

    /**
     * Set the LimeLight LED mode
     * 
     * @param mode LED mode
     */
    public void setLEDMode(LimeLightLEDMode mode) {
        dataTable.getEntry("ledMode").setNumber(mode.getValue());
    }

    /**
     * Set the camera operation mode
     * 
     * @param mode Operation mode
     */
    public void setOperationMode(LimeLightOperationMode mode) {
        dataTable.getEntry("camMode").setNumber(mode.getValue());
    }

    /**
     * Set the streaming mode
     * 
     * @param mode Streaming mode
     */
    public void setStreamMode(LimeLightStreamMode mode) {
        dataTable.getEntry("stream").setNumber(mode.getValue());
    }

    /**
     * Set if the camera should be saving a frame once every 2 seconds to internal
     * storage. Useful for critical gameplay review
     * 
     * @param enabled Enable snapshots
     */
    public void enableSnapshots(boolean enabled) {
        dataTable.getEntry("snapshot").setNumber(enabled ? 1 : 0);
    }

    

}