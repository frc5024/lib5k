package io.github.frc5024.lib5k.hardware.revrobotics.motors;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANEncoder;
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
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;


public class ExtendedSparkMax extends CANSparkMax implements Sendable {

    // HALSIM
    private SimDevice m_simDevice;
    private SimDouble m_simSpeed;

    public ExtendedSparkMax(int deviceID, MotorType type) {
        super(deviceID, type);

        SendableRegistry.addLW(this, "ExtendedSparkMax", deviceID);

        // handle simulation device settings
        m_simDevice = SimDevice.create("ExtendedSparkMax", getDeviceId());
        if (m_simDevice != null) {
            m_simSpeed = m_simDevice.createDouble("Speed", true, 0.0);
        }
    }

    /**
	 * Returns and object for interfacing with the encoder connected to the 
	 * encoder pins or front port of the SPARK MAX.
	 * 
	 * The default encoder type is assumed to be the hall effect for brushless.
	 * This can be modified for brushed DC to use a quadrature encoder.
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
	 * Returns and object for interfacing with the encoder connected to the 
	 * encoder pins or front port of the SPARK MAX.
	 * 
	 * The default encoder type is assumed to be the hall effect for brushless.
	 * This can be modified for brushed DC to use a quadrature encoder.
	 * 
	 * @param sensorType The encoder type for the motor: kHallEffect or kQuadrature
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
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Speed Controller");
        builder.setSafeState(() -> {
            set(0.0);
        });
        builder.addDoubleProperty("Value", this::get, this::set);

    }
    
}