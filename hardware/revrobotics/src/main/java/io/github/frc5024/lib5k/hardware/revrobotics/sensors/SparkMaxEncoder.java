package io.github.frc5024.lib5k.hardware.revrobotics.sensors;

import io.github.frc5024.lib5k.hardware.common.sensors.simulation.EncoderSimUtil;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.EncoderType;

import edu.wpi.first.wpilibj.SpeedController;

public class SparkMaxEncoder extends CANEncoder implements EncoderSimulation {

    private CANSparkMax device;
    private int cpr;
    private EncoderSimUtil sim;
    private double offset;

    public SparkMaxEncoder(CANSparkMax device, EncoderType sensorType, int counts_per_rev) {
        super(device, sensorType, counts_per_rev);
        this.device = device;
        this.cpr = counts_per_rev;
        offset = getPosition();
    }

    @Override
    public void initSimulationDevice(SpeedController controller, double gearbox_ratio, double max_rpm,
            double ramp_time) {
        sim = new EncoderSimUtil("SparkMAX", device.getDeviceId(), cpr, controller, gearbox_ratio, max_rpm, ramp_time);
    }

    @Override
    public void update() {
        sim.update();
    }

    @Override
    public void setPhaseInverted(boolean inverted) {
        // Handle simulation vs reality
        if (sim != null && sim.simReady()) {
            sim.setInverted(inverted);
        } else {
            this.setInverted(inverted);
        }
    }

    @Override
    public boolean getInverted() {

        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getInverted();
        }

        return super.getInverted();
    }

    @Override
    public double getPosition() {
        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getRotations() - offset;
        }
        return super.getPosition() - offset;
    }

    @Override
    public double getVelocity() {
        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getVelocity();
        }
        return super.getVelocity();
    }

}