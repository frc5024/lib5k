package frc.common.loopers;

import edu.wpi.first.wpilibj.Notifier;

public abstract class Looper {
    protected Notifier thread;
    protected double period;

    public Looper() {

        // Create the notifier
        thread = new Notifier(this::update);
    }

    public void start(double period) {
        this.period = period;
        this.thread.startPeriodic(period);
    }
    
    public void stop() {
        this.thread.stop();
    }

    protected abstract void update();
}