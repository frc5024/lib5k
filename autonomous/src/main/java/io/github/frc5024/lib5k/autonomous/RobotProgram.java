package io.github.frc5024.lib5k.autonomous;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.CommandBase;

import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.hardware.ni.roborio.FaultReporter;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;

/**
 * RobotProgram is the base class for all robot programs.
 * 
 * This contains tools for various operation modes. The command scheduler is run
 * behind the scenes.
 */
public abstract class RobotProgram extends TimedRobot {
    // Internal logger
    public RobotLogger logger = RobotLogger.getInstance();

    // Fault reporter
    private FaultReporter faultReporter = FaultReporter.getInstance();

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
     */
    public RobotProgram(boolean runSchedulerInTestMode, boolean stopAutonomousInTeleop) {
        this(runSchedulerInTestMode, stopAutonomousInTeleop, Shuffleboard.getTab("Main"));
    }

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

        // Create a chooser
        chooser = new SendableChooser<>();

        // Start logger
        logger.start(0.02);

        // Report language
        RR_HAL.reportFRCVersion("Java", RR_HAL.getLibraryVersion());

    }

    /**
     * For publishing the chooser (this can be overridden for custom dashboards)
     * 
     * @param component Chooser component
     */
    public void publishChooser(Sendable component) {
        dashboard.add(component);
    }

    /**
     * This is run all the time
     * 
     * @param init Did the robot just start?
     */
    public abstract void periodic(boolean init);

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

    @Override
    public void robotInit() {

        // Publish the chooser
        publishChooser(chooser);

        // Call robot
        periodic(true);

    }

    @Override
    public void robotPeriodic() {

        // Call robot
        periodic(false);
    }

    /**
     * Set the default autonomous sequence
     * 
     * @param sequence Default
     */
    public void setDefaultAutonomous(AutonomousSequence sequence) {
        logger.log("Default autonomous sequence set to: %s", RobotLogger.Level.kRobot, sequence.getName());

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
        logger.log("Added autonomous sequence: %s", RobotLogger.Level.kRobot, sequence.getName());

        // Add to shuffleboard
        chooser.addOption(sequence.getName(), sequence);
    }

    /**
     * Get the selected autonomous. If this is not called in autonomous(), it will
     * return the default
     * 
     * @return Selected autonomous sequence
     */
    public AutonomousSequence getSelectedAutonomous() {
        return autonomous;
    }

    @Override
    public void autonomousInit() {
        logger.log("Autonomous started");

        // Get sequence from chooser
        autonomous = chooser.getSelected();

        // Start the sequence
        if (autonomous != null) {
            logger.log("Starting autonomous sequence: %s", (Object) autonomous.getName());
            autonomous.getCommand().schedule();
        } else {
            logger.log("No autonomous selected, or sequence was null", RobotLogger.Level.kWarning);
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
        logger.log("Teleop started");

        if (this.autonomous != null && this.stopAutonomousInTeleop) {
            logger.log("Autonomous command has been canceled");
            this.autonomous.getCommand().cancel();
        } else {
            logger.log("There is no autonomous command to cancel, or stopAutonomousInTeleop is false");
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
        logger.log("Robot disabled");

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
        logger.log("Robot is in test mode");

        if (this.autonomous != null) {
            logger.log("Autonomous command has been canceled for safety reasons");
            this.autonomous.getCommand().cancel();
        } else {
            logger.log("There is no autonomous command to cancel");
        }

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