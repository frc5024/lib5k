package io.github.frc5024.lib5k.examples.vision_align.subsystems;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.lib5k.hardware.limelightvision.products.LimeLight2;
import io.github.frc5024.lib5k.hardware.limelightvision.settings.LimeLightLEDMode;
import io.github.frc5024.lib5k.hardware.limelightvision.settings.LimeLightOperationMode;
import io.github.frc5024.lib5k.hardware.limelightvision.settings.LimeLightStreamMode;
import io.github.frc5024.lib5k.vision.types.HyperbolicAxisAlignedBoundingBox;

/**
 * A subsystem that handles LimeLight controls
 */
public class Vision extends SubsystemBase {
    private static Vision instance = null;

    public static Vision getInstance() {
        if (instance == null) {
            instance = new Vision();
        }
        return instance;
    }

    // Camera
    private LimeLight2 limelight;

    // Image data
    private boolean hasTargetInSights = false;
    private boolean enabled = false;
    private HyperbolicAxisAlignedBoundingBox target;

    private Vision() {
        // Connect to the limelight
        limelight = new LimeLight2("limelight", "limelight.local");

        // Port-forward the limelight through the RoboRIO
        limelight.portForwardCameraStream(5800);
    }

    @Override
    public void periodic() {

        // Set camera mode based on being enabled
        if (enabled) {
            limelight.setLEDMode(LimeLightLEDMode.ON);
            limelight.setOperationMode(LimeLightOperationMode.VISION);
            limelight.setStreamMode(LimeLightStreamMode.PIP_MAIN);
        } else {
            limelight.setLEDMode(LimeLightLEDMode.OFF);
            limelight.setOperationMode(LimeLightOperationMode.DRIVER);
            limelight.setStreamMode(LimeLightStreamMode.STANDARD);
        }

        // Run vision loop if enabled
        if (enabled) {
            // Check if there is a target in sight
            hasTargetInSights = limelight.hasTarget();
            if (hasTargetInSights) {

                // Set the last seen target
                this.target = limelight.getTargetBoundsOrNull();

            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean hasTarget() {
        return hasTargetInSights;
    }

    public HyperbolicAxisAlignedBoundingBox getTarget() {
        return target;
    }

    public Rotation2d getXAngle() {
        return getTarget().getXRotation();
    }

    public Rotation2d getYAngle() {
        return getTarget().getYRotation();
    }

}