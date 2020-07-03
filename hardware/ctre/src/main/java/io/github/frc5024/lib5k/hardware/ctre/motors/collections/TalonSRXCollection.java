package io.github.frc5024.lib5k.hardware.ctre.motors.collections;


import java.util.function.Consumer;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.hal.SimBoolean;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import io.github.frc5024.lib5k.hardware.common.motors.interfaces.ICurrentController;
import io.github.frc5024.lib5k.hardware.common.motors.interfaces.IMotorCollection;
import io.github.frc5024.lib5k.hardware.common.motors.interfaces.IMotorGroupSafety;
import io.github.frc5024.lib5k.hardware.common.motors.interfaces.IRampRateController;
import io.github.frc5024.lib5k.hardware.common.motors.interfaces.IVoltageOutputController;
import io.github.frc5024.lib5k.hardware.ctre.util.TalonHelper;
import io.github.frc5024.lib5k.control_loops.TimedSlewLimiter;
import io.github.frc5024.lib5k.logging.Loggable;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.utils.ObjectCounter;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.lib5k.telemetry.ComponentTelemetry;
/**
 * Collection of multiple WPI_TalonSRX controllers that wraps a
 * SpeedControllerGroup
 */
public class TalonSRXCollection extends SpeedControllerGroup implements IMotorCollection, ICurrentController,
         IMotorGroupSafety, IVoltageOutputController, IRampRateController, Loggable {
    RobotLogger logger = RobotLogger.getInstance();

    /* Talon SRX Objects */
    private WPI_TalonSRX master;
    private WPI_TalonSRX[] slaves;

    /* Locals */
    private String name;
    private double output, currentThresh, currentHold, rampRate;
    private boolean inverted, voltageCompEnabled, currentLimited;

    /* Telemetry */
    private NetworkTable telemetryTable;
    private SimDevice m_simDevice;
    private SimBoolean m_simConnected;
    private SimBoolean m_simInverted;
    private SimDouble m_simSpeed;

    public TalonSRXCollection(WPI_TalonSRX master, WPI_TalonSRX... slaves) {
        super(master, slaves);

        // Set talons
        this.master = master;
        this.slaves = slaves;

        // Default the master
        master.configFactoryDefault();

        // Slave each slave
        forEachSlave((slave) -> {
            slave.configFactoryDefault();
            slave.follow(master);

        });

        // Determine name
        name = String.format("TalonSRXCollection (Master ID %d)", master.getDeviceID());

        // Get the telemetry NetworkTable
        telemetryTable = ComponentTelemetry.getInstance().getTableForComponent(name);

        // handle simulation device settings
        m_simDevice = SimDevice.create("TalonSRXCollection", master.getDeviceID());
        if (m_simDevice != null) {
            m_simConnected = m_simDevice.createBoolean("Connected", true, true);
            m_simInverted = m_simDevice.createBoolean("Inverted", true, false);
            m_simSpeed = m_simDevice.createDouble("Speed", true, 0.0);
        }

    }

    @Override
    public void set(double speed) {
        output = speed;

        if (m_simDevice != null) {
            m_simSpeed.set(speed);
        }

        super.set(speed);
    }

    @Override
    public void setBuffer(double speed) {
        if (speed != output) {
            set(speed);
        }

    }

    @Override
    public void setInverted(boolean isInverted) {
        inverted = isInverted;

        if (m_simDevice != null) {
            m_simInverted.set(isInverted);
        }

        super.setInverted(isInverted);

    }

    @Override
    public void pidWrite(double output) {
        this.output = output;
        super.pidWrite(output);

    }

    private double getControllerVoltage() {
        if (m_simDevice != null) {

            // Determine sim-safe voltage
            return RR_HAL.getSimSafeVoltage();
        } else {

            // Determine TalonSRX input bus voltage
            return master.getBusVoltage();
        }
    }

    @Override
    public void setVoltage(double volts) {

        double busVoltage = getControllerVoltage();


        // Just stop the motor if the bus is at 0V
        // Many things would go wrong otherwise (do you really want a div-by-zero error
        // on your drivetrain?)
        if (busVoltage == 0.0) {
            set(0.0);
            return;
        }

        // Convert voltage to a motor speed
        double calculated_speed = volts / busVoltage;

        // Set the output to the calculated speed
        set(calculated_speed);

    }

    @Override
    public double getEstimatedVoltage() {
        if (m_simDevice != null) {

            return get() * getControllerVoltage();

        }

        return master.getMotorOutputVoltage();

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

        // Enable limits
        enableCurrentLimit(true);
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

    @Override
    public void setRampRate(double secondsToFull) {
        rampRate = secondsToFull;

    }

    @Override
    public double getRampRate() {
        return rampRate;
    }

    @Override
    public void enableRampRateLimiting(boolean enabled) {

        // Handle TalonSRX configuration
        if (enabled) {
            master.configOpenloopRamp(rampRate);

            forEachSlave((slave) -> {
                slave.configOpenloopRamp(rampRate);
            });
        } else {
            master.configOpenloopRamp(0.0);

            forEachSlave((slave) -> {
                slave.configOpenloopRamp(0.0);
            });
        }

    }

    @Override
    public void setMasterMotorSafety(boolean enabled) {
        master.setSafetyEnabled(enabled);

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

   

    public void setNeutralMode(NeutralMode mode) {

        // Set master mode
        master.setNeutralMode(mode);

        // Set slaves modes
        forEachSlave((talon) -> {
            talon.setNeutralMode(mode);
        });

    }

    /**
     * Log all component data with RobotLogger
     */
    @Override
    public void logStatus() {

        // Build info string
        String data = String.format(
                "Output: %.2f, Inverted %b, Current limited: %b, Voltage Compensation: %b, Current threshold: %.2f, Current hold: %.2f, Ramp rate: %.2f",
                output, inverted, currentLimited, voltageCompEnabled, currentThresh, currentHold, rampRate);

        // Log string
        logger.log(name, data, Level.kInfo);

    }

    @Override
    public void updateTelemetry() {
        telemetryTable.getEntry("Output").setNumber(output);
        telemetryTable.getEntry("Is Inverted").setBoolean(inverted);
        telemetryTable.getEntry("Is Current Limited").setBoolean(currentLimited);
        telemetryTable.getEntry("Voltage Compensation").setBoolean(voltageCompEnabled);
        telemetryTable.getEntry("Curent Threshold").setNumber(currentThresh);
        telemetryTable.getEntry("Current Hold").setNumber(currentHold);
        telemetryTable.getEntry("Ramp Rate").setNumber(rampRate);

    }

}