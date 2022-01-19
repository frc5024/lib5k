package io.github.frc5024.lib5k.hardware.revrobotics.sensors;



import io.github.frc5024.lib5k.hardware.common.sensors.simulation.EncoderSimUtil;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.ExtendedSparkMax;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.RevMotorFactory;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;

import com.revrobotics.CANEncoder;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;

import com.revrobotics.CANSparkMax;
import com.revrobotics.EncoderType;
import com.revrobotics.REVLibError;



public class SparkMaxEncoder implements EncoderSimulation{

    private CANSparkMax device;
    private int cpr;
    private EncoderSimUtil sim;
    private double offset;
    private RelativeEncoder deviceEncoder;

    /**
     * Create a SparkMaxEncoder
     * 
     * @param device         Master motor controller
     */
    public SparkMaxEncoder(CANSparkMax device) {
        deviceEncoder = device.getEncoder();
        this.device = device;
        offset = deviceEncoder.getPosition();
    }

    
    public void initSimulationDevice(MotorController controller, double gearbox_ratio, double max_rpm,
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
            deviceEncoder.setInverted(inverted);
        }
    }

    @Override
    public boolean getInverted() {

        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getInverted();
        }

        return deviceEncoder.getInverted();
    }

    @Override
    public double getPosition() {
        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getRotations() - offset;
        }
        return deviceEncoder.getPosition() - offset;
    }

    @Override
    public double getVelocity() {
        // Handle simulation
        if (sim != null && sim.simReady()) {
            return sim.getVelocity();
        }
        return deviceEncoder.getVelocity();
    }

    @Override
    public void reset() {
        deviceEncoder.setPosition(0.0);
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