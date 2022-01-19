package io.github.frc5024.lib5k.hardware.common.pdp;

import java.util.function.Consumer;

import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.PowerDistribution;

public class CurrentLimit {

    // Hook to set voltage
    private Consumer<Double> voltageHook;

    // The channel the motor is on
    private int pdpChannel;

    // The amps to hold at
    private double holdAmps;

    // The amps allowed to spike to
    private double spikeAmps;

    // The duration the device can run over the hold amps
    private int duration;

    // Timer for measuring duration
    private Timer timer;

    /**
     * Creates a new current limit
     * 
     * @param pdpChannel the pdp channel the motor is on
     * @param holdAmps the amps that the motor should hold at
     * @param spikeAmps the amps that the device can spike to
     * @param duration the time the motor is allowed over the hold, in seconds
     * @param voltageHook hook for setting the motor voltage
     */
    public CurrentLimit(int pdpChannel, double holdAmps, double spikeAmps, int duration, Consumer<Double> voltageHook){
        this.voltageHook = voltageHook;

        this.holdAmps = holdAmps;

        this.spikeAmps = spikeAmps;

        this.duration = duration;

        timer = new Timer();
        timer.stop();
        timer.reset();
    }

    /**
     * This is run by the CurrentLimitManager and shouldn't normally be used
     * 
     * @param pdp the pdp the device is connected to
     */
    public void run(PowerDistribution pdp){
        double current = pdp.getCurrent(pdpChannel);

        // if volts are at allowed levels stop the timer and the timer equals 0 if the device is above the hold start the timer
        if(current <= holdAmps && !(timer.get() > 0)){
            timer.stop();
            timer.reset();
        }else if(current <= spikeAmps && current > holdAmps && !(timer.get() > 0)){
            timer.start();
        }
        
        // If the motor is above the spike or the duration lower the motor voltage to its hold amps
        if(current > spikeAmps || timer.get() > duration){
            voltageHook.accept(holdAmps);
        }


    }


}