package io.github.frc5024.lib5k.hardware.revrobotics.sensors;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.AnalogInput;

/**
 * A wrapper for the Rev Robotics Analog Pressure Sensor
 * 
 * https://www.revrobotics.com/rev-11-1107/
 */
public class PressureSensor extends AnalogInput {

    // Simulation
    private SimDevice simDevice;
    private SimDouble simPreassure;

    /**
     * Construct a pressure sensor.
     *
     * @param channel The channel number of the sensor. 0-3 are on-board 4-7 are on
     *                the MXP port.
     */
    public PressureSensor(int channel) {
        super(channel);

        // Set up simulation
        simDevice = SimDevice.create("RevPressureSensor", channel);
        if (simDevice != null) {
            simPreassure = simDevice.createDouble("PSI", false, 60.0);
        }
    }

    /**
     * Get the sensed air pressure in PSI
     * 
     * @return PSI
     */
    public double getPressurePSI() {

        // Simulation reading
        if (simDevice != null) {
            return simPreassure.get();
        }

        // Real reading
        return 250.0 * getVoltage() / 5.0 - 25.0;
    }

}