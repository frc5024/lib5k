package io.github.frc5024.lib5k.hardware.common.sensors.simulation;

import edu.wpi.first.hal.SimBoolean;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.Notifier;
import io.github.frc5024.lib5k.hardware.common.drivebase.IDifferentialDrivebase;

/**
 * A utility class for simulating gyroscope data based on two encoders
 */
public class GyroSimUtil implements AutoCloseable {

    // Internal thread
    private double threadPeriod;
    private Notifier thread;

    // Sim device
    private SimDevice simDevice;
    private SimDouble simAngle;
    private SimDouble simRate;
    private SimBoolean simCalibrated;
    private SimBoolean simInverted;

    // Drivetrain sensor reading trackers
    private IDifferentialDrivebase drivebase;
    private final double[] simSensorReadings = new double[2];

    // Rotation gain
    private double kGain;

    /**
     * Create a GyroSimUtil
     * 
     * @param name      Device name
     * @param id        Device ID
     * @param drivebase Attached drivebase
     */
    public GyroSimUtil(String name, int id, IDifferentialDrivebase drivebase) {
        this(name, id, drivebase, 0.02, 40);
    }

    /**
     * Create a GyroSimUtil
     * 
     * @param name         Device name
     * @param id           Device ID
     * @param drivebase    Attached drivebase
     * @param threadPeriod Internal thread time in seconds
     * @param rotationGain Gain on the sensor reading (default 40)
     */
    public GyroSimUtil(String name, int id, IDifferentialDrivebase drivebase, double threadPeriod,
            double rotationGain) {

        // Set up locals
        this.thread = new Notifier(this::update);
        this.threadPeriod = threadPeriod;
        this.drivebase = drivebase;
        this.kGain = rotationGain;

        // Init sim device
        simDevice = SimDevice.create(name, id);
        if (simDevice != null) {
            simAngle = simDevice.createDouble("Angle", false, 0.0);
            simRate = simDevice.createDouble("Rate", true, 0.0);
            simCalibrated = simDevice.createBoolean("Calibrated", true, false);
            simInverted = simDevice.createBoolean("Inverted", true, false);
        }

    }

    /**
     * Start the simulation
     */
    public void start() {
        if (simDevice != null) {
            thread.startPeriodic(threadPeriod);
        }
    }

    /**
     * Update the simulation data
     */
    public void update() {

        // Ensure sim is running
        if (simDevice != null) {

            // Get drivebase sensor readings
            final double leftReading = drivebase.getLeftMeters();
            final double rightReading = drivebase.getRightMeters();

            // Determine change from last reading
            final double leftDiff = leftReading - simSensorReadings[0];
            final double rightDiff = rightReading - simSensorReadings[1];

            // Calculate angle
            final double omega = ((leftDiff - rightDiff) / drivebase.getWidthMeters() * kGain);

            // Set last readings
            simSensorReadings[0] = leftReading;
            simSensorReadings[1] = rightReading;

            // Publish readings
            simAngle.set((simAngle.get() + omega) * ((simInverted.get()) ? -1 : 1));
            simRate.set(omega * ((simInverted.get()) ? -1 : 1));
        }
    }

    /**
     * Set if the sensor is inverted
     * 
     * @param inverted Is inverted
     */
    public void setInverted(boolean inverted) {
        if (simDevice != null) {
            simInverted.set(inverted);
        }
    }

    /**
     * Get if the sensor is inverted
     * 
     * @return Is inverted?
     */
    public boolean getInverted() {
        if (simDevice != null) {
            return simInverted.get();
        }
        return false;
    }

    /**
     * Calibrate the sensor (this resets everything)
     */
    public void calibrate() {
        if (simDevice != null) {
            // Set calibrated flag
            simCalibrated.set(true);

            // Reset sensor
            reset();
        }
    }

    /**
     * Get if the sensor has been calibrated yet
     * 
     * @return Has been calibrated?
     */
    public boolean isCalibrated() {
        if (simDevice != null) {
            return simCalibrated.get();
        }
        return false;
    }

    /**
     * Reset the sensor
     */
    public void reset() {
        if (simDevice != null) {
            simAngle.set(0.0);
            simRate.set(0.0);
        }
    }

    /**
     * Get the sensor angle
     * 
     * @return Angle (degrees)
     */
    public double getAngle() {
        if (simDevice != null) {
            return simAngle.get();
        }
        return 0.0;
    }

    /**
     * Get the sensor rate
     * 
     * @return Rate (degrees per second)
     */
    public double getRate() {
        if (simDevice != null) {
            return simRate.get();
        }
        return 0.0;
    }

    @Override
    public void close() {
        simDevice.close();
    }

    /**
     * Return if simulation is ready
     * 
     * @return Is ready?
     */
    public boolean simReady() {
        return simDevice != null;
    }

}