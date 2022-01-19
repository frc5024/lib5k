package io.github.frc5024.lib5k.hardware.ctre.sensors;

import com.ctre.phoenix.sensors.CANCoder;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import io.github.frc5024.lib5k.hardware.common.sensors.simulation.EncoderSimUtil;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;

/**
 * A wrapper around CTRE's CANCoder that integrates with 5024's encoder system
 */
public class ExtendedCANCoder extends CANCoder implements EncoderSimulation {

    private int cpr;
    private double offset;

    // Simulation
    private EncoderSimUtil sim;

    /**
     * Constructor.
     * 
     * @param deviceNumber The CAN Device ID of the CANCoder.
     * @param cpr Sensor counts per rotation
     */
    public ExtendedCANCoder(int deviceNumber, int cpr) {
        super(deviceNumber);
        this.cpr = cpr;
        offset = getPosition();
    }

    @Override
    public void initSimulationDevice(MotorController controller, double gearbox_ratio, double max_rpm,
            double ramp_time) {
        
        sim = new EncoderSimUtil("CANCoder", getDeviceID(), cpr, controller, gearbox_ratio, max_rpm, ramp_time);

    }

    @Override
    public void update() {
        // Handle simulation updates
        sim.update();

    }

    @Override
    public void setPhaseInverted(boolean inverted) {

        // Handle simulation vs reality
        if (sim != null && sim.simReady()) {
            sim.setInverted(inverted);
        } else {
            configSensorDirection(inverted);
        }
    }

    @Override
    public boolean getInverted() {

        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getInverted();
        }
        return configGetSensorDirection();
    }

    @Override
    public double getPosition() {
        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getRotations() - offset;
        }
        return (super.getPosition() / 360.0) - offset;
    }

    @Override
    public double getVelocity() {
        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getVelocity();
        }
        return super.getVelocity() / 360.0;
    }

    @Override
    public void reset() {
        super.setPosition(0.0);
        if (sim != null && sim.simReady()) {
            sim.reset();
        }
    }

    @Override
    public void close() throws Exception {
        if(sim!=null){
            sim.close();
        }
    }
}