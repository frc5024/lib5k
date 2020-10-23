package io.github.frc5024.lib5k.unittest;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;
import io.github.frc5024.lib5k.logging.RobotLogger;

/**
 * FakeScheduler allows a user to Simulate robot timekeeping and task scheduling
 * in a unit test
 */
public abstract class FakeScheduler implements Runnable {

    // Number of samples
    private int numSamples;
    private double dt;
    private double timeout;

    // Scheduler
    private Subsystem subsystem;
    private CommandBase command;

    /**
     * Create a FakeScheduler
     * 
     * @param periodSeconds        Period time in seconds
     * @param maximumExecutionTime The longest this can run
     */
    public FakeScheduler(double periodSeconds, double maximumExecutionTime) {
        this(null, null, periodSeconds, maximumExecutionTime);
    }

    /**
     * Create a FakeScheduler
     * 
     * @param subsystem            Subsystem to run
     * @param command              Command to run
     * @param periodSeconds        Period time in seconds
     * @param maximumExecutionTime The longest this can run
     */
    public FakeScheduler(Subsystem subsystem, CommandBase command, double periodSeconds, double maximumExecutionTime) {
        this.subsystem = subsystem;
        this.command = command;

        // Determine the number of samples needed
        numSamples = (int) (maximumExecutionTime / periodSeconds);
        this.dt = periodSeconds;
        this.timeout = maximumExecutionTime;

    }

    /**
     * Called on init
     */
    public abstract void init();

    /**
     * Called every loop
     * 
     * @param dt             Time since last loop
     * @param cycleNumber    The number of loops that have passed
     * @param timeSinceStart How long it has been since the scheduler started
     * @param timeToTimeout  How long until the test stops
     */
    public abstract void periodic(double dt, double cycleNumber, double timeSinceStart, double timeToTimeout);

    /**
     * Called when done
     */
    public abstract void finish();

    /**
     * Check if the scheduler is finished early
     * 
     * @return Is finished?
     */
    public boolean isFinished() {
        return false;
    }

    @Override
    public void run() {

        // Enable time override
        FPGAClock.enableSystemClockOverride(true, 0.0);

        // Init
        if (command != null) {
            command.initialize();
        }
        init();

        // Run the system loop
        systemLoop: {
            for (int i = 0; i < numSamples; i++) {

                // Update the scheduler
                if (subsystem != null) {
                    subsystem.periodic();
                }if (command != null) {
                    command.execute();
                }
                periodic(dt, i, i * dt, timeout - (i * dt));

                // Flush the logger
                RobotLogger.getInstance().flush();

                // Increment the system clock
                FPGAClock.incrementSystemClockOverride(dt);

                // Check if the system is finished
                if (isFinished() || (command != null && command.isFinished())) {
                    if (command != null) {
                        command.end(false);
                    }
                    break systemLoop;
                }
            }
            if (command != null) {
                command.end(true);
            }
        }

        // Finish
        finish();

        // Disable time override
        FPGAClock.enableSystemClockOverride(false, 0.0);

    }

}