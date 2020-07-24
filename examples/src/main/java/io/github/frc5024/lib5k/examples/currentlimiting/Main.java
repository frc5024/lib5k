package io.github.frc5024.lib5k.examples.currentlimiting;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import io.github.frc5024.lib5k.autonomous.RobotProgram;
import io.github.frc5024.lib5k.examples.currentlimiting.subsystem.MotorSubsystem;
import io.github.frc5024.lib5k.hardware.common.pdp.CurrentLimitManager;

public class Main extends RobotProgram {

    // Variable setup for the CurrentLimitManager and it requirements
    private CurrentLimitManager currentLimitManager;

    private PowerDistributionPanel pdp;

    private MotorSubsystem motorSubsystem;

    public static void main(String[] args){
        RobotBase.startRobot(Main::new);
    }


    public Main() {
        super(false, true);

        // Creates the pdp object
        pdp = new PowerDistributionPanel(1);

        // Setting up the subsystem
        motorSubsystem = new MotorSubsystem();
        motorSubsystem.register();

        // This creates a new Current Limit Manager
        currentLimitManager = new CurrentLimitManager(pdp);

    }
    

    @Override
    public void periodic(boolean init) {
        // This runs the Current Limit and should be called after the motors are
        currentLimitManager.performCurrentLimits(); 
    }

    @Override
    public void autonomous(boolean init) {
        // TODO Auto-generated method stub

    }

    @Override
    public void teleop(boolean init) {
        // TODO Auto-generated method stub

    }

    @Override
    public void disabled(boolean init) {
        // TODO Auto-generated method stub

    }

    @Override
    public void test(boolean init) {
        // TODO Auto-generated method stub

    }

}