package io.github.frc5024.lib5k.hardware.revrobotics.motors;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANError;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.EncoderType;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.hardware.revrobotics.sensors.SparkMaxEncoder;
import io.github.frc5024.lib5k.telemetry.interfaces.TelemetryProvider;
import io.github.frc5024.lib5k.utils.Functional;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;

/**
 * An extension of the CANSparkMax with simulation support, and mappings from
 * CANEncoder to CommonEncoder for hot-swappability with CTRE products
 */
public class ExtendedSparkMax extends CANSparkMax implements TelemetryProvider {
    public RevConfig config;

    // HALSIM
    private SimDevice m_simDevice;
    private SimDouble m_simSpeed;

    // System name
    private String name;

    // Properties
    private int maxStallCurrent = 240;

    /**
     * Create an extended spark max
     * 
     * @param deviceID  Device CAN id
     * @param motorType Device motor type
     */
    public ExtendedSparkMax(int deviceID, MotorType motorType) {
        super(deviceID, motorType);
        this.config = new RevConfig(motorType);

    }

    /**
     * Create an extended spark max
     * 
     * @param deviceID Device CAN id
     * @param config   device config
     */
    public ExtendedSparkMax(int deviceID, RevConfig config) {
        super(deviceID, config.motorType);
        this.config = config;

        setTelemetryEnabled(true);
        SendableRegistry.addLW(this, "ExtendedSparkMax", deviceID);
        this.name = SendableRegistry.getName(this);

        // handle simulation device settings
        m_simDevice = SimDevice.create("ExtendedSparkMax", getDeviceId());
        if (m_simDevice != null) {
            m_simSpeed = m_simDevice.createDouble("Speed", true, 0.0);
        }
    }

    /**
     * Returns and object for interfacing with the encoder connected to the encoder
     * pins or front port of the SPARK MAX.
     * 
     * The default encoder type is assumed to be the hall effect for brushless. This
     * can be modified for brushed DC to use a quadrature encoder.
     * 
     * Assumes that the encoder the is integrated encoder, configured as:
     * EncoderType.kHallEffect, 0 counts per revolution.
     * 
     * @return An object for interfacing with the integrated encoder.
     */
    public CommonEncoder getCommonEncoder() {
        return getCommonEncoder(EncoderType.kHallSensor, 0);
    }

    /**
     * Returns and object for interfacing with the encoder connected to the encoder
     * pins or front port of the SPARK MAX.
     * 
     * The default encoder type is assumed to be the hall effect for brushless. This
     * can be modified for brushed DC to use a quadrature encoder.
     * 
     * @param sensorType     The encoder type for the motor: kHallEffect or
     *                       kQuadrature
     * @param counts_per_rev The counts per revolution of the encoder
     * @return An object for interfacing with an encoder
     */
    public CommonEncoder getCommonEncoder(EncoderType sensorType, int counts_per_rev) {
        return new SparkMaxEncoder(this, sensorType, counts_per_rev);
    }

    @Override
    public void set(double speed) {

        // Set sim speed
        if (m_simDevice != null) {
            m_simSpeed.set(speed);
        }

        super.set(speed);
    }

    @Override
    public double get() {
        if (m_simDevice != null) {
            return m_simSpeed.get();
        }

        return super.get();
    }

    @Override
    public void setVoltage(double outputVolts) {
        if (RobotBase.isSimulation()) {
            set(outputVolts / RR_HAL.getSimSafeVoltage());
        } else {
            super.setVoltage(outputVolts);
        }
    }

    @Override
    public double getMotorTemperature() {
        if (RobotBase.isSimulation()) {
            return 0.0;
        } else {
            return super.getMotorTemperature();
        }
    }

    @Override
    public double getOutputCurrent() {
        if (RobotBase.isSimulation()) {
            return 0.0;
        } else {
            return super.getOutputCurrent();
        }
    }

    @Override
    public CANError setSmartCurrentLimit(int stallLimit, int freeLimit, int limitRPM) {
        maxStallCurrent = stallLimit;
        return super.setSmartCurrentLimit(stallLimit, freeLimit, limitRPM);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Speed Controller");
        builder.setSafeState(() -> {
            set(0.0);
        });
        builder.addDoubleProperty("Value", this::get, this::set);
        builder.addDoubleProperty("BusVoltage", this::getBusVoltage, Functional::unused);
        builder.addDoubleProperty("Temperature", this::getMotorTemperature, Functional::unused);
        builder.addDoubleProperty("Current", this::getOutputCurrent, Functional::unused);
        builder.addBooleanProperty("IsOverdrawingCurrent", () -> {
            return (maxStallCurrent - getOutputCurrent()) < 1;
        }, Functional::unused);
    }

    /**
     * Make a slave of this motor
     * 
     * @param id Slave CAN id
     * @return Slave
     */
    public ExtendedSparkMax makeSlave(int id) {
        return makeSlave(id, false);
    }

    /**
     * Make a slave of this motor
     * 
     * @param id     Slave CAN id
     * @param invert Is Slave inverted?
     * @return Slave
     */
    public ExtendedSparkMax makeSlave(int id, boolean invert) {
        ExtendedSparkMax slave = RevMotorFactory.createSparkMax(id, config.motorType);

        slave.follow(this, invert);

        return slave;

    }

    @Override
    public String getName() {
        return this.name;
    }

}