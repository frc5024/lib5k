package io.github.frc5024.lib5k.examples.currentlimiting.subsystem;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.lib5k.hardware.common.pdp.CurrentLimit;
import io.github.frc5024.lib5k.hardware.common.pdp.CurrentLimitManager;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREConfig;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonFX;

public class MotorSubsystem extends SubsystemBase {

    private ExtendedTalonFX talonMotor;

    // Current Limit variable
    private CurrentLimit talonCurrentLimit;

    private boolean isNew;

    public MotorSubsystem() {
        isNew = true;

        talonMotor = CTREMotorFactory.createTalonFX(1, new CTREConfig());

        // Creates a new currentlimit
        talonCurrentLimit = new CurrentLimit(1, 30, 32, 2, talonMotor::setVoltage);

        // Adds the current limit to the manager
        CurrentLimitManager.addCurrentLimit(talonCurrentLimit);

    }

    @Override
    public void periodic() {
        if(RobotState.isTest()){
            if(isNew){
                // removes the current limit from the manager
                CurrentLimitManager.removeCurrentLimit(talonCurrentLimit);
                isNew = false;
            }

            // runs the current limit independantly
            talonCurrentLimit.run(new PowerDistributionPanel(1));
            
        }

    }



    
}
