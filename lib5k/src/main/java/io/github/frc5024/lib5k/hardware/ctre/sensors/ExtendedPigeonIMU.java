package io.github.frc5024.lib5k.hardware.ctre.sensors;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import io.github.frc5024.lib5k.hardware.common.drivebase.IDifferentialDrivebase;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.ISimGyro;
import io.github.frc5024.lib5k.hardware.common.sensors.simulation.GyroSimUtil;

/**
 * A wrapper for the CTRE PigeonIMU that brings it into the 5024 ecosystem.
 * 
 * http://www.ctr-electronics.com/gadgeteer-imu-module-pigeon.html#product_tabs_technical_resources
 */
public class ExtendedPigeonIMU extends PigeonIMU implements ISimGyro {

    // Inversion tracker
    private boolean inverted = false;
    private boolean calibrated = false;

    // Simulation
    private GyroSimUtil sim;

    /**
     * Connect to a Pigeon IMU
     * 
     * @param canID Device CAN ID
     */
    public ExtendedPigeonIMU(int canID) {
        super(canID);
        super.configFactoryDefault();
    }

    /**
     * Connect to a Pigeon IMU
     * 
     * @param talonSrx Attached talonSRX object
     */
    public ExtendedPigeonIMU(TalonSRX talonSrx) {
        super(talonSrx);
        super.configFactoryDefault();
    }

    @Override
    public void setInverted(boolean inverted) {
        if (sim != null && sim.simReady()) {
            sim.setInverted(inverted);
        }
        this.inverted = inverted;
    }

    @Override
    public boolean getInverted() {
        if (sim != null && sim.simReady()) {
            return sim.getInverted();
        }
        return inverted;
    }

    @Override
    public double getHeading() {
        return Math.IEEEremainder(getAngle(), 360) * (inverted ? -1.0 : 1.0);
    }

    @Override
    public void calibrate() {
        if (sim != null && sim.simReady()) {
            sim.calibrate();
        } else {
            // * Note: The PigeonIMU is not designed to be calibrated on-robot
            // * super.enterCalibrationMode(CalibrationMode.BootTareGyroAccel);
        }
        calibrated = true;

    }

    @Override
    public boolean getCalibrated() {
        return calibrated;
    }

    @Override
    public void reset() {
        if (sim != null && sim.simReady()) {
            sim.reset();
        } else {
            // CTRE doesn't really provide a reset for us to use
            super.setCompassAngle(0.0);
        }

    }

    @Override
    public double getAngle() {
        if (sim != null && sim.simReady()) {
            return sim.getAngle();
        }
        return super.getCompassHeading() * ((inverted) ? -1 : 1);
    }

    @Override
    public double getRate() {
        if (sim != null && sim.simReady()) {
            return sim.getRate();
        }

        // Build a data frame array
        double[] data = new double[3];

        // Read a data frame from sensor
        super.getRawGyro(data);

        // Return the Z axis in dps
        return data[2] * ((inverted) ? -1 : 1);
    }

    @Override
    public void close() throws Exception {
        if (sim != null) {
            sim.close();
        }

    }

    @Override
    public GyroSimUtil initDrivebaseSimulation(final IDifferentialDrivebase drivebase) {
        return initDrivebaseSimulation(drivebase, true);
    }

    /**
     * Init gyroscope simulation from fusing encoder readings
     * 
     * @param drivebase      Robot drivetrain
     * @param startOwnThread Should this simulation run in its own thread?
     */
    public GyroSimUtil initDrivebaseSimulation(final IDifferentialDrivebase drivebase, boolean startOwnThread) {

        // Set up simulation
        sim = new GyroSimUtil("ExtendedPigeonIMU", super.getDeviceID(), drivebase, 0.02, 40.0);

        if (startOwnThread) {
            sim.start();
        }

        return sim;
    }

}