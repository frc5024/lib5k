package io.github.frc5024.lib5k.hardware.ctre.sensors;

import com.ctre.phoenix.sensors.CANCoder;

import edu.wpi.first.hal.SimBoolean;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.SpeedController;
import io.github.frc5024.lib5k.control_loops.SlewLimiter;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;

/**
 * A wrapper around CTRE's CANCoder that integrates with 5024's encoder system
 */
public class ExtendedCANCoder extends CANCoder implements CommonEncoder, EncoderSimulation {

    private int cpr;

    /* Simulation vars */
    private SpeedController controller;
    private double last_time;
    private double gearbox_ratio, max_rpm;

    /* Simulation */
    private SimDevice m_simDevice;
    private SimDouble m_simTicks;
    private SimDouble m_simRotations;
    private SimDouble m_simVelocity;
    private SimBoolean m_simInverted;
    private static int s_instanceCount = 0;
    private SlewLimiter m_simSlew;

    /**
     * Constructor.
     * 
     * @param deviceNumber The CAN Device ID of the CANCoder.
     * @param cpr Sensor counts per rotation
     */
    public ExtendedCANCoder(int deviceNumber, int cpr) {
        super(deviceNumber);
        this.cpr = cpr;
    }

    @Override
    public void initSimulationDevice(SpeedController controller, double gearbox_ratio, double max_rpm,
            double ramp_time) {
        // Set locals
        this.controller = controller;
        this.gearbox_ratio = gearbox_ratio;
        this.max_rpm = max_rpm;
        this.m_simSlew = new SlewLimiter(ramp_time);

        // Init sim device
        m_simDevice = SimDevice.create("CANCoder", s_instanceCount + 1);

        if (m_simDevice != null) {
            m_simTicks = m_simDevice.createDouble("Ticks", false, 0.0);
            m_simRotations = m_simDevice.createDouble("Rotations", true, 0.0);
            m_simVelocity = m_simDevice.createDouble("RPM", true, 0.0);
            m_simInverted = m_simDevice.createBoolean("Inverted", true, false);
        }

        // Move to next instance
        s_instanceCount++;

    }

    @Override
    public void update() {
        // Handle simulation updates
        if (m_simDevice != null) {
            // If this is the first loop, simply re-set the timer, and skip
            if (last_time == 0) {
                last_time = FPGAClock.getFPGASeconds();
                return;
            }

            // Determine dt
            double current_time = FPGAClock.getFPGASeconds();
            double dt = current_time - last_time;
            last_time = current_time;

            // Calc encoder position
            double rpm = (m_simSlew.feed(controller.get()) * max_rpm) / gearbox_ratio;
            double revs = (rpm / 60.0) * dt; // RPM -> RPS -> Multiply by seconds to find rotations since last update
            m_simTicks.set((int) (m_simTicks.get() + (revs * cpr)));
            m_simRotations.set((m_simRotations.get() + revs));
            m_simVelocity.set(rpm);
        }

    }

    @Override
    public void setPhaseInverted(boolean inverted) {

        // Handle simulation vs reality
        if (m_simDevice != null) {
            m_simInverted.set(inverted);
        } else {
            configSensorDirection(inverted);
        }
    }

    @Override
    public boolean getInverted() {

        // Handle simulation
        if (m_simDevice != null) {
            return m_simInverted.get();
        }
        return configGetSensorDirection();
    }

    @Override
    public double getPosition() {
        // Handle simulation
        if (m_simDevice != null) {
            return m_simTicks.get();
        }
        return super.getPosition();
    }

    @Override
    public double getVelocity() {
        // Handle simulation
        if (m_simDevice != null) {
            return m_simVelocity.get();
        }
        return super.getVelocity();
    }

}