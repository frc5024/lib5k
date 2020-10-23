package io.github.frc5024.lib5k.hardware.generic.sensors;

import edu.wpi.first.wpilibj.SpeedController;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;
import io.github.frc5024.lib5k.hardware.common.sensors.simulation.EncoderSimUtil;

/**
 * A mock encoder
 */
public class MockCommonEncoder implements EncoderSimulation {
    private boolean phase = false;
    private int cpr;
    private EncoderSimUtil sim;
    private double offset;

    private static int id = 0;
    private int thisId = 0;

    /**
     * Create a MockCommonEncoder
     * 
     * @param cpr Counts per rotation of the encoder
     */
    public MockCommonEncoder(int cpr) {
        this.cpr = cpr;
        offset = getPosition();
        thisId = id;
        id++;
    }

    @Override
    public void setPhaseInverted(boolean inverted) {
        // Handle simulation vs reality
        if (sim != null && sim.simReady()) {
            sim.setInverted(inverted);
        } else {
            this.phase = inverted;
        }

    }

    @Override
    public boolean getInverted() {

        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getInverted();
        }

        return this.phase;
    }

    @Override
    public double getPosition() {
        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getRotations() - offset;
        }
        return 0;
    }

    @Override
    public double getVelocity() {
        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getVelocity();
        }
        return 0;
    }

    @Override
    public void initSimulationDevice(SpeedController controller, double gearbox_ratio, double max_rpm,
            double ramp_time) {

        sim = new EncoderSimUtil("MockCommonEncoder", thisId, cpr, controller, gearbox_ratio, max_rpm, ramp_time);
    }

    @Override
    public void update() {
        sim.update();
    }

    @Override
    public void reset() {
        if (sim != null && sim.simReady()) {
            sim.reset();
        }
    }

    @Override
    public void close() throws Exception {
        if (sim != null) {
            sim.close();
        }
    }

}