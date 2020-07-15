package io.github.frc5024.lib5k.hardware.ctre.motors;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.RobotBase;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.ctre.sensors.TalonEncoder;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.csvlogging.LoggingObject;
import io.github.frc5024.lib5k.csvlogging.StatusLogger;

/**
 * The ExtendedTalonSRX contains two extra features from WPI_TalonSRX: - Ablilty
 * to get the attached sensor as a CommonEncoder object - Small fixes for 2020
 * simulation voltage bugs in HALSIM
 */
public class ExtendedTalonSRX extends WPI_TalonSRX {
    public CTREConfig config;

    // CSV Logging
    private LoggingObject csvLog;

    public ExtendedTalonSRX(int id) {
        this(id, new CTREConfig());
    }

    public ExtendedTalonSRX(int id, CTREConfig config) {
        super(id);
        this.config = config;

        // Set up csv logging
        csvLog = StatusLogger.getInstance().createLoggingObject(String.format("ExtendedTalonSRX_%d", id), "voltage",
                "inverted");

    }

    /**
     * Get the attached encoder
     * 
     * @return Encoder
     */
    public CommonEncoder getCommonEncoder(int cpr) {
        return new TalonEncoder(this, cpr);
    }

    @Override
    public void setInverted(boolean inverted) {
        // Set log value
        csvLog.setValue("inverted", inverted);
        super.setInverted(inverted);
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

        // Set log value
        csvLog.setValue("voltage", outputVolts);
    }

    public ExtendedTalonSRX makeSlave(int id) {
        ExtendedTalonSRX slave = CTREMotorFactory.createTalonSRX(id, this.config);

        slave.follow(this);

        return slave;

    }

}