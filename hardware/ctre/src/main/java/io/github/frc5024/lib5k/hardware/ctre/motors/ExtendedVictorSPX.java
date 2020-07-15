package io.github.frc5024.lib5k.hardware.ctre.motors;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.RobotBase;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.csvlogging.LoggingObject;
import io.github.frc5024.lib5k.csvlogging.StatusLogger;

/**
 * The ExtendedVictorSPX contains an extra feature from WPI_VictorSPX:
 *  - Small fixes for 2020 simulation voltage bugs in HALSIM
 */
public class ExtendedVictorSPX extends WPI_VictorSPX {
    public CTREConfig config;

    // CSV Logging
    private LoggingObject csvLog;


    public ExtendedVictorSPX(int id){
        this(id, new CTREConfig());
    }

    public ExtendedVictorSPX(int id, CTREConfig config) {
        super(id);
        this.config = config;

        // Set up csv logging
        csvLog = StatusLogger.getInstance().createLoggingObject(String.format("ExtendedTalonSRX_%d", id), "voltage",
                "inverted");

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


    public ExtendedVictorSPX makeSlave(int id){
        ExtendedVictorSPX victor = CTREMotorFactory.createVictorSPX(id, this.config);
    
        victor.follow(this);

        return victor;
    }
}