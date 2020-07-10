package io.github.frc5024.lib5k.autonomous;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.CommandBase;

import io.github.frc5024.lib5k.logging.RobotLogger;

/**
 * RobotProgram is the base class for all robot programs.
 * 
 * This contains tools for various operation modes. The command scheduler is run
 * behind the scenes.
 */
public abstract class RobotProgram extends TimedRobot {
    // Internal logger
    public RobotLogger logger = RobotLogger.getInstance();

    // Scheduler
    private CommandScheduler scheduler;

    // Settings
    private boolean runSchedulerInTestMode;
    private boolean stopAutonomousInTeleop;

    // Autonomous
    private ShuffleboardTab dashboard;
    private SendableChooser<AutonomousSequence> chooser;
    private AutonomousSequence autonomous = null;

    /**
     * Create a robot program
     * 
     * @param runSchedulerInTestMode Should the command scheduler be run when the
     *                               robot is in test mode?
     * @param stopAutonomousInTeleop Should the active autonomous command be killed
     *                               as soon as teleop starts?
     * @param primaryDashboard       The primary Shuffleboard tab
     */
    public RobotProgram(boolean runSchedulerInTestMode, boolean stopAutonomousInTeleop,
            ShuffleboardTab primaryDashboard) {

        // Get a scheduler instance
        this.scheduler = CommandScheduler.getInstance();

        // Set settings
        this.runSchedulerInTestMode = runSchedulerInTestMode;
        this.stopAutonomousInTeleop = stopAutonomousInTeleop;
        this.dashboard = primaryDashboard;

        // Send chooser to dashboard
        chooser = new SendableChooser<>();
        dashboard.add(chooser);

        // Start logger
        logger.start(0.02);

    }

    /**
     * This is run during autonomous
     * 
     * @param init Did autonomous just start?
     */
    public abstract void autonomous(boolean init);

    /**
     * This is run during teleop
     * 
     * @param init Did teleop just start?
     */
    public abstract void teleop(boolean init);

    /**
     * This is run during disabled
     * 
     * @param init Did disabled just start?
     */
    public abstract void disabled(boolean init);

    /**
     * This is run during test mode
     * 
     * @param init Did test mode just start?
     */
    public abstract void test(boolean init);

    /**
     * Set the default autonomous sequence
     * 
     * @param sequence Default
     */
    public void setDefaultAutonomous(AutonomousSequence sequence) {
        logger.log("RobotProgram", String.format("Default autonomous sequence set to: %s", sequence.getName()), RobotLogger.Level.kRobot);

        // Add to shuffleboard
        chooser.setDefaultOption(sequence.getName(), sequence);

        // Set the command
        autonomous = sequence;

    }

    /**
     * Add a new autonomous sequence
     * 
     * @param sequence Sequence
     */
    public void addAutonomous(AutonomousSequence sequence) {
        logger.log("RobotProgram", String.format("Added autonomous sequence: %s", sequence.getName()), RobotLogger.Level.kRobot);

        // Add to shuffleboard
        chooser.addOption(sequence.getName(), sequence);
    }

    @Override
    public void autonomousInit() {

        // Get sequence from chooser
        autonomous = chooser.getSelected();

        // Start the sequence
        if (autonomous != null) {
            logger.log("RobotProgram", String.format("Starting autonomous sequence: %s", autonomous.getName()));
            autonomous.getCommand().schedule();
        }else{
            logger.log("RobotProgram", "No autonomous selected, or sequence was null", RobotLogger.Level.kWarning);
        }

        // Call autonomous
        autonomous(true);
    }

    @Override
    public void autonomousPeriodic() {

        // Call autonomous
        autonomous(false);

        // Run scheduler
        scheduler.run();
    }

    @Override
    public void teleopInit() {

        if (this.autonomous != null && this.stopAutonomousInTeleop) {
            logger.log("RobotProgram", "Autonomous command has been canceled");
            this.autonomous.getCommand().cancel();
        }

        // Call teleop
        teleop(true);
    }

    @Override
    public void teleopPeriodic() {

        // Call teleop
        teleop(false);

        // Run scheduler
        scheduler.run();
    }

    @Override
    public void disabledInit() {

        // Call disabled
        disabled(true);
    }

    @Override
    public void disabledPeriodic() {

        // Call disabled
        disabled(false);

        // Run scheduler
        scheduler.run();
    }

    @Override
    public void testInit() {

        // Call test
        test(true);
    }

    @Override
    public void testPeriodic() {

        // Call test
        test(false);

        // Run scheduler if enable d
        if (runSchedulerInTestMode) {
            scheduler.run();
        }
    }

}