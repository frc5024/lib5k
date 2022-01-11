package io.github.frc5024.lib5k.examples.motorfactory.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREConfig;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonFX;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedVictorSPX;

public class CTREMotorSubsystem extends SubsystemBase {

    // Create the variables for the motors
    ExtendedVictorSPX victor1;
    ExtendedVictorSPX victor2;
    ExtendedVictorSPX victor3;
    ExtendedTalonSRX talon1;
    ExtendedTalonSRX talon2;
    ExtendedTalonFX talon3;
    ExtendedTalonFX talon4;

    public CTREMotorSubsystem() {

        // This creates a talonSRX with an id of 1, with a custom config seting the
        // motor inverted
        talon1 = CTREMotorFactory.createTalonSRX(1, new CTREConfig(true));

        // This adds a following motor to the talon1 it recieves the same config as talon1
        talon2 = talon1.makeSlave(2);

        // This creates a new talonFx and gives it a slave that will follow it and shares its config
        talon3 = CTREMotorFactory.createTalonFX(3);
        talon4 = talon3.makeSlave(4);

        // This creates a new victoreSPX and gives it a custom config it also adds 2 slaves to it
        victor1 = CTREMotorFactory.createVictorSPX(5, new CTREConfig(false, true, true, true, 33, 15, 30, 0, true));
        victor2 = victor1.makeSlave(6);
        victor3 = victor1.makeSlave(7);
    }

    @Override
    public void periodic() {
        // sets talon1 and talon2 to 1
        talon1.set(1);

        // sets talon3 and talon4 to .5
        talon3.set(.5);

        // sets victor1, victor2, and victor3 to -1
        victor1.set(-1);
    }


}