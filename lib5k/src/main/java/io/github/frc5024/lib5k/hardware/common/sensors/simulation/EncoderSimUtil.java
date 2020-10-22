package io.github.frc5024.lib5k.hardware.common.sensors.simulation;

import edu.wpi.first.hal.SimBoolean;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.SpeedController;
import io.github.frc5024.lib5k.control_loops.SlewLimiter;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;
import io.github.frc5024.lib5k.utils.TimeScale;
import io.github.frc5024.lib5k.utils.interfaces.PeriodicComponent;

/**
 * An internal class used for simulating encoder hardware
 */
public class EncoderSimUtil implements PeriodicComponent {

    /* Simulation vars */
    private SpeedController controller;
    private double gearbox_ratio, max_rpm;
    private int cpr;

    // Timing
    private TimeScale dtCalculator;

    /* Simulation */
    private SimDevice simDevice;
    private SimDouble simTicks;
    private SimDouble simRotations;
    private SimDouble simVelocity;
    private SimBoolean simInverted;
    private SlewLimiter simSlew;

    /**
     * Create a new EncoderSim utility
     * 
     * @param name          Wrapper name
     * @param id            Device ID
     * @param cpr           Counts per revolution
     * @param controller    Motor controller
     * @param gearbox_ratio Gearbox ration between motor and encoder
     * @param max_rpm       Max motor RPM
     * @param ramp_time     Amount of time to get from 0 to 100% in seconds
     */
    public EncoderSimUtil(String name, int id, int cpr, SpeedController controller, double gearbox_ratio,
            double max_rpm, double ramp_time) {
        // Set locals
        this.controller = controller;
        this.gearbox_ratio = gearbox_ratio;
        this.max_rpm = max_rpm;
        this.simSlew = new SlewLimiter(ramp_time);
        this.cpr = cpr;

        dtCalculator = new TimeScale();

        // Init sim device
        simDevice = SimDevice.create("GenericEncoder", id);

        if (simDevice != null) {
            simTicks = simDevice.createDouble("Ticks", false, 0.0);
            simRotations = simDevice.createDouble("Rotations", true, 0.0);
            simVelocity = simDevice.createDouble("RPM", true, 0.0);
            simInverted = simDevice.createBoolean("Inverted", true, false);
        }
    }

    @Override
    public void update() {
        // Handle simulation updates
        if (simDevice != null) {

            // Determine dt
            double dt = dtCalculator.calculate();

            // Calc encoder position
            double rpm = ((simSlew.feed(controller.get() * ((controller.getInverted()) ? -1 : 1)) * max_rpm)
                    / gearbox_ratio) * ((simInverted.get()) ? -1 : 1);
            double revs = (rpm / 60.0) * dt; // RPM -> RPS -> Multiply by seconds to find rotations since last update
            simTicks.set((int) (simTicks.get() + (revs * cpr)));
            simRotations.set((simRotations.get() + revs));
            simVelocity.set(rpm);
        }

    }

    /**
     * Set if the encoder is inverted
     * 
     * @param inverted Is inverted?
     */
    public void setInverted(boolean inverted) {
        if (simDevice != null) {
            simInverted.set(inverted);
        }
    }

    /**
     * Get if encoder is inverted
     * 
     * @return Is inverted
     */
    public boolean getInverted() {
        if (simDevice != null) {
            return simInverted.get();
        }
        return false;
    }

    /**
     * Get raw tick count
     * 
     * @return Raw ticks
     */
    public double getTicks() {
        if (simDevice != null) {
            return simTicks.get();
        }
        return 0.0;
    }

    /**
     * Get rotation count
     * 
     * @return Rotations
     */
    public double getRotations() {
        if (simDevice != null) {
            return simRotations.get();
        }
        return 0.0;
    }

    /**
     * Get velocity in RPM
     * 
     * @return RPM
     */
    public double getVelocity() {
        if (simVelocity != null) {
            return simTicks.get();
        }
        return 0.0;
    }

    /**
     * Get if the simulation is ready
     * 
     * @return Is ready?
     */
    public boolean simReady() {
        return simDevice != null;
    }

    /**
     * Reset the simulation
     */
    public void reset() {
        if (simDevice != null) {
            simTicks.set(0.0);
            simRotations.set(0.0);
            simVelocity.set(0.0);
        }
        simSlew.reset();
    }

}