package io.github.frc5024.lib5k.hardware.kauai.gyroscopes;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import io.github.frc5024.lib5k.hardware.common.drivebase.IDifferentialDrivebase;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.ISimGyro;
import io.github.frc5024.lib5k.hardware.common.sensors.simulation.GyroSimUtil;
import io.github.frc5024.lib5k.utils.annotations.FieldTested;

/**
 * A wrapper for the {@see AHRS} / NavX gyroscope
 * 
 * This wrapper adds support for gyro simulation, and adds some lib5k-specific
 * methods
 */
@FieldTested(year = 2020)
public class NavX extends AHRS implements ISimGyro {

    private static NavX m_instance = null;

    // Trackers
    private boolean inverted = false;
    private boolean calibrated = false;

    // Simulation
    private GyroSimUtil sim;
    private SPI.Port port;

    /**
     * Connect to a NavX on the MXP port
     */
    public NavX() {
        this(Port.kMXP);
    }

    /**
     * Connect to a NavX
     * 
     * @param port Connected port
     */
    public NavX(final SPI.Port port) {
        super(port);
        this.port = port;
    }

    /**
     * Get the Default NavX instance
     * 
     * @return NavX instance
     */
    public static NavX getInstance() {
        if (m_instance == null) {
            m_instance = new NavX();
        }

        return m_instance;
    }

    /**
     * Init gyroscope simulation from fusing encoder readings
     * 
     * @param drivebase Robot drivetrain
     */
    public void initDrivebaseSimulation(final IDifferentialDrivebase drivebase) {

        // Set up simulation
        sim = new GyroSimUtil("NavX", port.value, drivebase, 0.02, 40.0);
        sim.start();
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
            super.calibrate();
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
            super.reset();
        }

    }

    @Override
    public double getAngle() {
        if (sim != null && sim.simReady()) {
            return sim.getAngle();
        }
        return super.getAngle() * ((inverted) ? -1 : 1);
    }

    @Override
    public double getRate() {
        if (sim != null && sim.simReady()) {
            return sim.getRate();
        }
        return super.getRate() * ((inverted) ? -1 : 1);
    }

    @Override
    public void close() {
        if (sim != null) {
            sim.close();
        }

    }

}