package io.github.frc5024.lib5k.hardware.ctre.sensors;

import io.github.frc5024.lib5k.hardware.common.sensors.simulation.EncoderSimUtil;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;

public class TalonEncoder implements EncoderSimulation {

    private BaseTalon talon;
    private boolean phase = false;
    private int cpr;
    private EncoderSimUtil sim;
    private double offset;

    /**
     * Create a Talon Encoder
     * 
     * @param talon Master talon
     * @param cpr   Counts per rotation of the encoder
     */
    public TalonEncoder(BaseTalon talon, int cpr) {
        this.talon = talon;
        this.cpr = cpr;
        offset = getPosition();
    }

    @Override
    public void setPhaseInverted(boolean inverted) {
        // Handle simulation vs reality
        if (sim != null && sim.simReady()) {
            sim.setInverted(inverted);
        } else {
            this.phase = inverted;
            talon.setSensorPhase(inverted);
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
        return (talon.getSelectedSensorPosition() / this.cpr) - offset;
    }

    @Override
    public double getVelocity() {
        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getVelocity();
        }
        return talon.getSelectedSensorVelocity() / 1000 / this.cpr;
    }

    @Override
    public void initSimulationDevice(MotorController controller, double gearbox_ratio, double max_rpm,
            double ramp_time) {

        sim = new EncoderSimUtil("TalonSRX Encoder", talon.getDeviceID(), cpr, controller, gearbox_ratio, max_rpm,
                ramp_time);
    }

    @Override
    public void update() {
        sim.update();
    }

    @Override
    public void reset() {
        talon.setSelectedSensorPosition(0);
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