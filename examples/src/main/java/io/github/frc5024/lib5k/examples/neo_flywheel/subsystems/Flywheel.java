package io.github.frc5024.lib5k.examples.neo_flywheel;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.ExtendedSparkMax;

/**
 * This is the flywheel subsystem. It needs to do the following:
 * 
 * <ul>
 * <li>Allow other classes to set the "goal RPM"</li>
 * <li>Quickly reach, and maintain that speed</li>
 * </ul>
 * 
 * This example is build around the RevRobotics NEO brushless motor.
 * https://www.revrobotics.com/rev-21-1650/
 * 
 */
public class Flywheel extends SubsystemBase {

    // Every subsystem needs an instance
    // For info on how instances work, read about Java's "Singleton design pattern"
    private static Flywheel instance;

    public static Flywheel getInstance() {
        if (instance == null) {
            instance = new Flywheel();
        }
        return instance;
    }

    // We need a motor controller, an encoder, a PID controller, and a motor model.
    // Normally, we would use a PID controller that runs on the roboRIO because it
    // gives us more flexibility, but we have had some good experience with the
    // SparkMax's in-built controller.
    private ExtendedSparkMax motorController;
    private CommonEncoder encoder;
    private CANPIDController pidController;
    private DCBrushedMotor model;

    private Flywheel() {

        // We need to set the model to represent the characteristics of the motor being
        // used. This saves us some work when configuring the system later.
        model = DCBrushedMotor.NEO;

        // We will set up the motor controller, then get references to it's encoder and
        // PID controller.
        // Here, we will assume the SparkMax has been assigned CAN id #15
        this.motorController = new ExtendedSparkMax(15, MotorType.kBrushless);
        this.encoder = motorController.getCommonEncoder();
        this.pidController = motorController.getPIDController();


    }
}