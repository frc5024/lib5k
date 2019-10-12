package frc.lib5k.kinematics;

/**
 * Used to set bounds for robot movement
 */
public class DriveConstraints {
    double minV, maxV;

    public DriveConstraints(double minVelocity, double maxVelocity) {
        this.minV = minVelocity;
        this.maxV = maxVelocity;
    }

    public double getMaxVel() {
        return maxV;
    }

    public double getMinVel() {
        return minV;
    }

    public void setMaxVel(double max) {
        this.maxV = max;
    }

    public void setMinVel(double min) {
        this.minV = min;
    }

}