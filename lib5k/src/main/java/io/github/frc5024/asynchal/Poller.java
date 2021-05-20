package io.github.frc5024.asynchal;

import java.util.ArrayList;

import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.SafeNotifier;

/**
 * You have been lied to. This library is not fully real-time. I don't want to
 * deal with writing a JNI wrapper for some FPGA DMA code, so this quick-refresh
 * loop is the best you'll get for callbacks.
 */
public class Poller {
    private static Poller instance;

    private SafeNotifier thread;

    private ArrayList<Pollable> pollables = new ArrayList<>();

    /**
     * Poller constructor
     */
    private Poller() {

        // Start up the polling thread
        this.thread = new SafeNotifier("Poller", this::update);
        this.thread.startPeriodic(0.01);
    }

    /**
     * Get a poller instance
     * 
     * @return Instance
     */
    public static Poller getInstance() {
        if (instance == null) {
            instance = new Poller();
        }
        return instance;
    }

    /**
     * Register a pollable component
     * 
     * @param p Pollable
     */
    public void register(Pollable p) {
        pollables.add(p);
    }

    /**
     * de-Register a pollable component
     * 
     * @param p Pollable
     */
    public void deregister(Pollable p) {
        pollables.remove(p);
    }

    /**
     * Update the thread
     */
    private void update() {
        for (Pollable p : pollables) {
            p.checkForUpdates();
        }
    }

}