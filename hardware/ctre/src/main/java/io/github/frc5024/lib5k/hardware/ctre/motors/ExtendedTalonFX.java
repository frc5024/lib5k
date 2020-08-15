package io.github.frc5024.lib5k.hardware.ctre.motors;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.RobotBase;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.ctre.sensors.TalonEncoder;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;

/**
 * The ExtendedTalonFX contains two extra features from WPI_TalonFX:
 *  - Ablilty to get the attached sensor as a CommonEncoder object
 *  - Small fixes for 2020 simulation voltage bugs in HALSIM
 */
public class ExtendedTalonFX extends WPI_TalonFX {
    public CTREConfig config;

    public ExtendedTalonFX(int id) {
        this(id, new CTREConfig());
    }

    public ExtendedTalonFX(int id, CTREConfig config) {
        super(id);
        this.config = config;
    }

    /**
     * Get the attached encoder
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

    public ExtendedTalonFX makeSlave(int id){
        ExtendedTalonFX talon = CTREMotorFactory.createTalonFX(id);
    
        talon.follow(this);

        return talon;
    }




}