package frc.common.loopers;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.common.loopables.LoopableSubsystem;
import frc.common.utils.RobotLogger;
import frc.common.utils.RobotLogger.Level;

public class SubsystemLooper extends Looper {
    private RobotLogger logger = RobotLogger.getInstance();

    ArrayList<LoopableSubsystem> subsystems = new ArrayList<LoopableSubsystem>();

    double dt;

    public SubsystemLooper() {
        logger.log("[Subsystem Looper] Constructing", Level.kRobot);
    }

    /**
     * Register a LoopableSubsystem with the looper
     */
    public void register(LoopableSubsystem subsystem) {
        subsystems.add(subsystem);
        logger.log("[Subsystem Looper] Registered " + subsystem.name, Level.kRobot);
    }

    private double timedExecute(Runnable function) {
        double start = Timer.getFPGATimestamp();

        // Run the function
        try {
            function.run();
        } catch (Exception e) {
            logger.log("[Subsystem Looper] A registered subsystem failed to execute");
            logger.log("" + e);
        }
        
        // Return execution time
        return Timer.getFPGATimestamp() - start;
    }

    @Override
    protected void update() {
        double inputTime = 0;

        // Run and check total time for inputs
        for (LoopableSubsystem subsystem : subsystems) {
            subsystem.last_timestamp = Timer.getFPGATimestamp();
            inputTime += timedExecute(subsystem::periodicInput);
        }

        if (inputTime > (period / 2)) {
            logger.log("[SubsystemLooper] Subsystem inputs are using more than half of the alotted looper time",
                    Level.kWarning);
        }

        double outputTime = 0;

        // Run and check total time for inputs
        for (LoopableSubsystem subsystem : subsystems) {
            subsystem.last_timestamp = Timer.getFPGATimestamp();
            outputTime += timedExecute(subsystem::periodicOutput);
        }

        if (outputTime > (period / 2)) {
            logger.log("[SubsystemLooper] Subsystem outputs are using more than half of the alotted looper time",
                    Level.kWarning);
        }

        // Calculate dt
        this.dt = inputTime + outputTime;
    }

    public void outputTelemetry() {
        SmartDashboard.putNumber("SubsystemLooper DT", dt);
    }
}