package io.github.frc5024.lib5k.hardware.common.sensors.interfaces;

import edu.wpi.first.wpilibj.SpeedController;
import io.github.frc5024.lib5k.utils.interfaces.PeriodicComponent;

public interface EncoderSimulation extends PeriodicComponent, CommonEncoder {

    /**
     * Init simulation for this encoder
     * 
     * @param controller    The motor controller that controls the encoder
     * @param gearbox_ratio The gear ratio between the motor and encoder
     * @param max_rpm       The max RPM of the motor
     * @param ramp_time     The approx amount of time it takes for the motor to go
     *                      from 0-100% under load
     */
    public void initSimulationDevice(SpeedController controller, double gearbox_ratio, double max_rpm,
            double ramp_time);

}