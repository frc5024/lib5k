package frc.common;

import java.util.ArrayList;
import edu.wpi.first.wpilibj.Notifier;

/**
 * A logger the buffers it's contents, then pushes them to thwe console every few miliseconds
 */
public class PeriodicLogger{
    double period;
    Notifier thread;
    ArrayList<String> buffer = new ArrayList<String>();
    
    public PeriodicLogger(double period){
        this.period = period;
        this.thread = new Notifier(this::display);
    }

    public void log(String x) {
        this.buffer.add(x);
    }

    public void info(String x) {
        this.buffer.add("INFO: "+x);
    }

    public void warn(String x) {
        this.buffer.add("WARNING: "+x);
    }

    public void start(){
        this.thread.startPeriodic(this.period);
    }
    
    public void stop() {
        this.thread.stop();
    }

    private void display(){
        for (String x : this.buffer){
            System.out.println(x);
        }
        this.buffer = new ArrayList<String>();
    }

} 