package frc.common.field;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj.Notifier;

import frc.common.field.Match;

public class FieldStatusThread {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Notifier thread;

    Match current_match;

    public FieldStatusThread() {
        this.current_match = new Match();

        this.thread = new Notifier(this::update);
    }

    public void start(double period) {
        logger.info("FieldStatusThread Starting");
        this.thread.startPeriodic(period);
    }

    public void stop() {
        logger.warning("FieldStatusThread has been stopped");
        this.thread.stop();
    }

    private void update() {
        this.current_match.update();
    }

    public Match getCurrentMatch() {
        return current_match;
    }
}