package io.github.frc5024.lib5k.hardware.ctre.motors;

import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.wpilibj.RobotBase;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.ctre.sensors.TalonEncoder;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.logging.RobotLogger;

import java.lang.AutoCloseable;
import java.lang.reflect.Field;

/**
 * The ExtendedTalonSRX contains two extra features from WPI_TalonSRX: - Ability
 * to get the attached sensor as a CommonEncoder object - Small fixes for 2020
 * simulation voltage bugs in HALSIM
 */
public class ExtendedTalonSRX extends WPI_TalonSRX implements AutoCloseable {
    public CTREConfig config;

    /**
     * Create an extended Talon SRX
     * 
     * @param id CAN ID
     */
    public ExtendedTalonSRX(int id) {
        this(id, new CTREConfig());
    }

    /**
     * Create an extended Talon SRX
     * 
     * @param id     CAN ID
     * @param config configuration
     */
    public ExtendedTalonSRX(int id, CTREConfig config) {
        super(id);
        this.config = config;
        setSafetyEnabled(false);
    }

    @Override
    public void setSafetyEnabled(boolean enabled) {
        RobotLogger.getInstance().log("Safety enabled: %b", enabled);
        super.setSafetyEnabled(enabled);
    }

    /**
     * Get the attached encoder
     * 
     * @param cpr Encoder counts per revolution
     * @return Encoder
     */
    public CommonEncoder getCommonEncoder(int cpr) {
        return new TalonEncoder(this, cpr);
    }

    @Override
    public double getMotorOutputVoltage() {
        if (RobotBase.isSimulation()) {
            return get() * RR_HAL.getSimSafeVoltage();
        }

        return super.getMotorOutputVoltage();
    }

    @Override
    public void setVoltage(double outputVolts) {
        if (RobotBase.isSimulation()) {
            set(outputVolts / RR_HAL.getSimSafeVoltage());
        } else {
            super.setVoltage(outputVolts);
        }
    }

    /**
     * Make a slave of this controller
     * 
     * @param id New controller CAN id
     * @return Slave controller
     */
    public ExtendedTalonSRX makeSlave(int id) {
        ExtendedTalonSRX slave = CTREMotorFactory.createTalonSRX(id);

        slave.follow(this);

        return slave;

    }

    @Override
    public void close() {

        try {
            // Attempt to close the handle on the simulation device
            Field hiddenSimulationDeviceField = getClass().getDeclaredField("m_simDevice");
            hiddenSimulationDeviceField.setAccessible(true);
            SimDevice simDevice = (SimDevice) hiddenSimulationDeviceField.get(this);

            // Close the sim device
            if (simDevice != null) {
                simDevice.close();
            }

        } catch (Exception e) {

        }

        try {
            // Attempt to close the handle on the CAN device
            Field hiddenHandleField = getClass().getDeclaredField("m_handle");
            hiddenHandleField.setAccessible(true);
            Long handle = (Long) hiddenHandleField.get(this);

            // Close the real device
            if (handle != null) {
                MotControllerJNI.JNI_destroy_MotController(handle);
            }

        } catch (Exception e) {

        }

    }

}