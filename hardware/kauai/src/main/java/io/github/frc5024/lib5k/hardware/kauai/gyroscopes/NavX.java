package io.github.frc5024.lib5k.hardware.kauai.gyroscopes;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import io.github.frc5024.lib5k.hardware.common.drivebase.IDifferentialDrivebase;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.ISimGyro;

/**
 * A wrapper for the {@link AHRS} / NavX gyroscope
 * 
 * This wrapper adds support for gyro simulation, and adds some lib5k-specific
 * methods
 */
public class NavX extends AHRS implements ISimGyro {

    private static NavX m_instance = null;

    private boolean inverted = false;

    /* Simulation */
    private final double ROTATION_SPEED_GAIN = 40;
    private final double SIMULATION_PERIOD = 0.02;
    private final Port port;
    private IDifferentialDrivebase m_simDrivebase;
    private final double[] m_simSensorReadings = new double[2];
    private Notifier m_simThread;
    private SimDevice m_simDevice;
    private SimDouble m_simAngle;
    private SimDouble m_simRate;

    public NavX() {
        this(Port.kMXP);
    }

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

    public void initDrivebaseSimulation(final IDifferentialDrivebase drivebase) {

        m_simDevice = SimDevice.create("NavX", this.port.value);

        if (m_simDevice != null) {
            m_simAngle = m_simDevice.createDouble("Angle", true, 0.0);
            m_simRate = m_simDevice.createDouble("Rate", true, 0.0);
            m_simDrivebase = drivebase;

            // Create and start a simulation thread
            m_simThread = new Notifier(this::updateSimData);
            m_simThread.startPeriodic(SIMULATION_PERIOD);

        }
    }

    private void updateSimData() {

        // Ensure sim is running
        if (m_simDevice != null) {

            // Get drivebase sensor readings
            final double leftReading = m_simDrivebase.getLeftMeters();
            final double rightReading = m_simDrivebase.getRightMeters();

            // Determine change from last reading
            final double leftDiff = leftReading - m_simSensorReadings[0];
            final double rightDiff = rightReading - m_simSensorReadings[1];

            // Calculate angle
            final double omega = ((leftDiff - rightDiff) / m_simDrivebase.getWidthMeters() * ROTATION_SPEED_GAIN);

            // Set last readings
            m_simSensorReadings[0] = leftReading;
            m_simSensorReadings[1] = rightReading;

            // Publish readings
            m_simAngle.set(m_simAngle.get() + omega);
            m_simRate.set(omega);
        }

    }

    @Override
    public void setInverted(final boolean inverted) {
        this.inverted = inverted;
    }

    @Override
    public double getAngle() {

        if (m_simDevice != null) {
            return m_simAngle.get();
        }

        return super.getAngle();
    }

    @Override
    public double getHeading() {
        return Math.IEEEremainder(getAngle(), 360) * (inverted ? -1.0 : 1.0);
    }

    @Override
    public double getRate() {

        if (m_simAngle != null) {
            return m_simRate.get() * (inverted ? -1.0 : 1.0);
        }

        return super.getRate() * (inverted ? -1.0 : 1.0);
    }


}