package frc.lib5k.components.motors;

import java.util.function.Consumer;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import frc.lib5k.interfaces.Loggable;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;

/**
 * Collection of multiple WPI_TalonSRX controllers that wraps a
 * SpeedControllerGroup, and exposes a SpeedController
 */
public class TalonSRXCollection implements IMotorCollection, ICurrentController, Loggable {
    RobotLogger logger = RobotLogger.getInstance();

    /* Talon SRX Objects */
    private WPI_TalonSRX master;
    private WPI_TalonSRX[] slaves;

    /* Wrapped SpeedController */
    SpeedControllerGroup m_controllerGroup;

    /* Locals */
    private String name;
    private double output, currentThresh, currentHold;
    private boolean inverted, voltageCompEnabled, currentLimited;

    public TalonSRXCollection(WPI_TalonSRX master, WPI_TalonSRX... slaves) {

        // Set talons
        this.master = master;
        this.slaves = slaves;

        // Set SpeedControllerGroup
        m_controllerGroup = new SpeedControllerGroup(master, slaves);

        // Slave each slave
        forEachSlave((slave) -> {
            slave.follow(master);
        });

        // Determine name
        name = String.format("TalonSRXCollection (Master ID %d)", master.getDeviceID());

    }

    @Override
    public void set(double speed) {
        output = speed;

        m_controllerGroup.set(speed);
    }

    @Override
    public void setBuffer(double speed) {
        if (speed != output) {
            set(speed);
        }

    }

    @Override
    public double get() {
        return m_controllerGroup.get();
    }

    @Override
    public void setInverted(boolean isInverted) {
        inverted = isInverted;
        m_controllerGroup.setInverted(isInverted);

    }

    @Override
    public boolean getInverted() {
        return m_controllerGroup.getInverted();
    }

    @Override
    public void disable() {
        m_controllerGroup.disable();

    }

    @Override
    public void stopMotor() {
        m_controllerGroup.stopMotor();

    }

    @Override
    public void pidWrite(double output) {
        this.output = output;
        m_controllerGroup.pidWrite(output);

    }

    @Override
    public void setCurrentLimit(int threshold, int duration, int hold, int timeout) {
        // Set telem data
        currentHold = hold;
        currentThresh = threshold;

        // Set master limits
        TalonHelper.configCurrentLimit(master, threshold, hold, duration, timeout);

        // Set each slave
        forEachSlave((slave) -> {
            TalonHelper.configCurrentLimit(slave, threshold, hold, duration, timeout);
        });
    }

    /**
     * Set if current limiting should be enabled
     * 
     * @param on Should enable?
     */
    @Override
    public void enableCurrentLimit(boolean on) {
        currentLimited = on;

        // Set master mode
        master.enableCurrentLimit(on);

        // Set each slave
        forEachSlave((slave) -> {
            slave.enableCurrentLimit(on);
        });

    }

    /**
     * Set if voltage compensation should be enabled
     * 
     * @param on Should enable?
     */
    @Override
    public void setCompensation(boolean on) {
        voltageCompEnabled = on;

        // Set master mode
        master.enableVoltageCompensation(on);

        // Set each slave
        forEachSlave((slave) -> {
            slave.enableVoltageCompensation(on);
        });

    }

    /**
     * Get the master TalonSRX
     * 
     * @return Master
     */
    public WPI_TalonSRX getMaster() {
        return master;

    }

    /**
     * For-Each over each slave controller
     * 
     * @param consumer Method to run
     */
    public void forEachSlave(Consumer<WPI_TalonSRX> consumer) {
        for (WPI_TalonSRX talon : slaves) {
            consumer.accept(talon);
        }
    }

    /**
     * Log all component data with RobotLogger
     */
    @Override
    public void logStatus() {

        // Build info string
        String data = String.format(
                "Output: %.2f, Inverted %b, Current limited: %b, Voltage Compensation: %b, Current threshold: %.2f, Current hold: %.2f",
                output, inverted, currentLimited, voltageCompEnabled, currentThresh, currentHold);

        // Log string
        logger.log(name, data, Level.kInfo);

    }

    @Override
    public void updateTelemetry() {
        // TODO Auto-generated method stub

        // TODO: lib5k NT table

    }

}