package frc.common.field;

import edu.wpi.first.wpilibj.Notifier;

import frc.common.field.Match;

public class FieldStatusThread {
    Notifier thread;

    Match current_match;

    public FieldStatusThread() {
        this.current_match = new Match();

        this.thread = new Notifier(this::update);
    }

    public void start(double period) {
        System.out.println("NOTICE: Starting FieldStatusThread");
        this.thread.startPeriodic(period);
    }

    public void stop() {
        this.thread.stop();
    }

    private void update() {
        this.current_match.update();
    }

    public Match getCurrentMatch() {
        return current_match;
    }
}