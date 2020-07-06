package io.github.frc5024.lib5k.examples.motorfactory.subsystems;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.ExtendedSparkMax;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.RevConfig;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.RevMotorFactory;

public class RevMotorSubsystem extends SubsystemBase{

    // Creates the SparkMax motor variables
    ExtendedSparkMax sparkMax1;
    ExtendedSparkMax sparkMax2;
    ExtendedSparkMax sparkMax3;


    public RevMotorSubsystem(){

        // This is the default motor config and returns a motor with the defualt configuration that has id 7 and is a brushless motor
        sparkMax1 = RevMotorFactory.createSparkMax(7, MotorType.kBrushless);

        // Creates a spark motor that uses a custom config that sets the motor to inverted
        sparkMax2 = RevMotorFactory.createSparkMax(8, new RevConfig(MotorType.kBrushed, true));

        // This makes the motor a follow the sparkMax2 it shares the same config as well
        sparkMax3 = sparkMax2.makeSlave(9);

    }



    @Override
    public void periodic() {
        // This sets sparkMax1 to 1 speed
        sparkMax1.set(1);

        // this sets sparkMax2 and sparkMax3 to speed -1
        sparkMax2.set(-1);

    }
    
}