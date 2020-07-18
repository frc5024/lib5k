package io.github.frc5024.lib5k.examples.motorfactory;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import io.github.frc5024.lib5k.autonomous.RobotProgram;
import io.github.frc5024.lib5k.examples.motorfactory.subsystems.CTREMotorSubsystem;
import io.github.frc5024.lib5k.examples.motorfactory.subsystems.RevMotorSubsystem;

public class Main extends RobotProgram {

    // Creates the two motor subsystems
    private CTREMotorSubsystem ctreMotorSubsystem;

    private RevMotorSubsystem revMotorSubsystem;

    // starts the robot
    public static void main(String[] args){
        RobotBase.startRobot(Main::new);
    }


    
    public Main(){
        super(false, true);
        // Initializes the subsystems
        ctreMotorSubsystem = new CTREMotorSubsystem();
        revMotorSubsystem = new RevMotorSubsystem();

        // Registers the subsystems
        ctreMotorSubsystem.register();
        revMotorSubsystem.register();


    }

    @Override
    public void autonomous(boolean init) {
        

    }

    @Override
    public void teleop(boolean init) {
        

    }

    @Override
    public void disabled(boolean init) {
        

    }

    @Override
    public void test(boolean init) {
        

    }


}