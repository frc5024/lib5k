package frc.lib5k.kinematics;

/**
 * Used to store PID control data
 */
public class PIDProfile {
    public double kp, ki, kd;

    public PIDProfile(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

}