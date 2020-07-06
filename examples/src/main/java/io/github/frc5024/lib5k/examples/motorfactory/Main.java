package io.github.frc5024.lib5k.examples.motorfactory;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import io.github.frc5024.lib5k.examples.motorfactory.subsystems.CTREMotorSubsystem;
import io.github.frc5024.lib5k.examples.motorfactory.subsystems.RevMotorSubsystem;

public class Main extends TimedRobot {

    // Creates the two motor subsystems
    private CTREMotorSubsystem ctreMotorSubsystem;

    private RevMotorSubsystem revMotorSubsystem;

    // starts the robot
    public static void main(String[] args){
        RobotBase.startRobot(Main::new);
    }


    
    public Main(){
        // Initializes the subsystems
        ctreMotorSubsystem = new CTREMotorSubsystem();
        revMotorSubsystem = new RevMotorSubsystem();

        // Registers the subsystems
        ctreMotorSubsystem.register();
        revMotorSubsystem.register();


    }

    @Override
    public void robotPeriodic() {
        
    }

}