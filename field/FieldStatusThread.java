package frc.common.field;

import frc.common.utils.RobotLogger;
import frc.common.utils.RobotLogger.Level;

import edu.wpi.first.wpilibj.Notifier;

import frc.common.field.Match;

public class FieldStatusThread {
    private final RobotLogger logger = RobotLogger.getInstance();

    Notifier thread;

    Match current_match;

    public FieldStatusThread() {
        this.current_match = new Match();

        this.thread = new Notifier(this::update);
    }

    public void start(double period) {
        logger.log("FieldStatusThread Starting", Level.kRobot);
        this.thread.startPeriodic(period);
    }

    public void stop() {
        logger.log("FieldStatusThread has been stopped", Level.kWarning);
        this.thread.stop();
    }

    private void update() {
        this.current_match.update();
    }

    public Match getCurrentMatch() {
        return current_match;
    }
}