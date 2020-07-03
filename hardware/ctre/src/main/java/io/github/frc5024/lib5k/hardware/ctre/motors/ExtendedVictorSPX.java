package io.github.frc5024.lib5k.hardware.ctre.motors;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.RobotBase;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;

/**
 * The ExtendedVictorSPX contains an extra feature from WPI_VictorSPX:
 *  - Small fixes for 2020 simulation voltage bugs in HALSIM
 */
public class ExtendedVictorSPX extends WPI_VictorSPX {

    public ExtendedVictorSPX(int id) {
        super(id);
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
}