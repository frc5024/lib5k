package io.github.frc5024.lib5k.hardware.revrobotics.sensors;

import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;
import io.github.frc5024.lib5k.control_loops.SlewLimiter;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.EncoderType;

import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.wpilibj.SpeedController;

public class SparkMaxEncoder extends CANEncoder implements CommonEncoder, EncoderSimulation {

    private int cpr;
    private double offset;


    /* Simulation vars */
    private SpeedController controller;
    private double last_time;
    private double gearbox_ratio, max_rpm;

    
    /* Simulation */
    private SimDevice m_simDevice;
    private SimDouble m_simTicks;
    private SimDouble m_simRotations;
    private static int s_instanceCount = 0;
    private SlewLimiter m_simSlew;

    public SparkMaxEncoder(CANSparkMax device, EncoderType sensorType, int counts_per_rev) {
        super(device, sensorType, counts_per_rev);
        this.cpr = counts_per_rev;
        offset = getPosition();
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
        m_simDevice = SimDevice.create("SparkMaxEncoder", s_instanceCount + 1);

        if (m_simDevice != null) {
            m_simTicks = m_simDevice.createDouble("Ticks", true, 0.0);
            m_simRotations = m_simDevice.createDouble("Rotations", true, 0.0);
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
        }
    }


    @Override
    public void setPhaseInverted(boolean inverted) {
        this.setInverted(inverted);
    }

    @Override
    public double getPosition() {
        return super.getPosition() - offset;
    }
    
}