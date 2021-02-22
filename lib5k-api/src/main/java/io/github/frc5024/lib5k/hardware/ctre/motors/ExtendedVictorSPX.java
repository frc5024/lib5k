package io.github.frc5024.lib5k.hardware.ctre.motors;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.RobotBase;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;

/**
 * The ExtendedVictorSPX contains an extra feature from WPI_VictorSPX: - Small
 * fixes for 2020 simulation voltage bugs in HALSIM
 */
public class ExtendedVictorSPX extends WPI_VictorSPX {
    public CTREConfig config;

    /**
     * Create an extended Victor SPX
     * 
     * @param id CAN ID
     */
    public ExtendedVictorSPX(int id) {
        this(id, new CTREConfig());
    }

    /**
     * Create an extended Victor SPX
     * 
     * @param id     CAN ID
     * @param config configuration
     */
    public ExtendedVictorSPX(int id, CTREConfig config) {
        super(id);
        this.config = config;
        setSafetyEnabled(false);

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
    public ExtendedVictorSPX makeSlave(int id) {
        ExtendedVictorSPX victor = CTREMotorFactory.createVictorSPX(id);

        victor.follow(this);

        return victor;
    }
}