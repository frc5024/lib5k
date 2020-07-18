package io.github.frc5024.lib5k.hardware.ctre.sensors;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.ParamEnum;

import edu.wpi.first.wpilibj.SpeedController;
import io.github.frc5024.lib5k.hardware.common.sensors.simulation.EncoderSimUtil;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;

/**
 * A wrapper around the CTRE CANIfier's encoder interface that integrates with 5024's encoder system
 */
public class CANIfierEncoder extends CANifier implements EncoderSimulation {

    private int cpr;
    private double offset;


    // Simulation
    private EncoderSimUtil sim;

    /**
     * Constructor.
     * 
     * @param deviceNumber The CAN Device ID of the CANIfier.
     * @param cpr Sensor counts per rotation
     */
    public CANIfierEncoder(int deviceNumber, int cpr) {
        super(deviceNumber);
        this.cpr = cpr;
        offset = getPosition();
    }

    @Override
    public void initSimulationDevice(SpeedController controller, double gearbox_ratio, double max_rpm,
            double ramp_time) {
        
        sim = new EncoderSimUtil("CANIfierEncoder", getDeviceID(), cpr, controller, gearbox_ratio, max_rpm, ramp_time);

    }

    @Override
    public void update() {
        // Handle simulation updates
        sim.update();

    }

    @Override
    public void setPhaseInverted(boolean inverted) {

        // CANIfier does not support this feature
        throw new RuntimeException("The CANIfier does not support inverting encoders");
    }

    @Override
    public boolean getInverted() {
        return false;
    }

    @Override
    public double getPosition() {
        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getRotations() - offset;
        }
        return (super.getQuadraturePosition() / cpr) - offset;
    }

    @Override
    public double getVelocity() {
        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getVelocity();
        }
        return super.getQuadratureVelocity() / cpr;
    }

}