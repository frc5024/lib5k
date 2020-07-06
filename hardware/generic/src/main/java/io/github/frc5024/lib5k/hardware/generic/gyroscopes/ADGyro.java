package io.github.frc5024.lib5k.hardware.generic.gyroscopes;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;
import io.github.frc5024.lib5k.hardware.common.drivebase.IDifferentialDrivebase;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.ISimGyro;
import io.github.frc5024.lib5k.hardware.common.sensors.simulation.GyroSimUtil;

/**
 * A wrapper around the Analog Devices ADXRS450.
 * 
 * This wrapper adds support for gyro simulation, and adds some lib5k-specific
 * methods
 */
public class ADGyro extends ADXRS450_Gyro implements ISimGyro {

    private static ADGyro m_instance = null;

    // Trackers
    private boolean inverted = false;
    private boolean calibrated = false;

    // Simulation
    private GyroSimUtil sim;

    public ADGyro() {
        super(SPI.Port.kOnboardCS0);
    }

    /**
     * Get the Default NavX instance
     * 
     * @return NavX instance
     */
    public static ADGyro getInstance() {
        if (m_instance == null) {
            m_instance = new ADGyro();
        }

        return m_instance;
    }

    public void initDrivebaseSimulation(final IDifferentialDrivebase drivebase) {

        // Set up simulation
        sim = new GyroSimUtil("NavX", SPI.Port.kOnboardCS0.value, drivebase, 0.02, 40.0);
        sim.start();
     }
 
     @Override
     public void setInverted(boolean inverted) {
         if (sim != null && sim.simReady()) {
             sim.setInverted(inverted);
         }
         this.inverted = inverted;
     }
 
     @Override
     public boolean getInverted() {
         if (sim != null && sim.simReady()) {
             return sim.getInverted();
         }
         return inverted;
     }
 
     @Override
     public double getHeading() {
         return Math.IEEEremainder(getAngle(), 360) * (inverted ? -1.0 : 1.0);
     }
 
     @Override
     public void calibrate() {
         if (sim != null && sim.simReady()) {
             sim.calibrate();
         } else {
             super.calibrate();
         }
         calibrated = true;
 
     }
 
     @Override
     public boolean getCalibrated() {
         return calibrated;
     }
 
     @Override
     public void reset() {
         if (sim != null && sim.simReady()) {
             sim.reset();
         } else {
             super.reset();
         }
 
     }
 
     @Override
     public double getAngle() {
         if (sim != null && sim.simReady()) {
             return sim.getAngle();
         }
         return super.getAngle() * ((inverted) ? -1 : 1);
     }
 
     @Override
     public double getRate() {
         if (sim != null && sim.simReady()) {
             return sim.getRate();
         }
         return super.getRate() * ((inverted) ? -1 : 1);
     }
 
     @Override
     public void close() {
         if (sim != null) {
             sim.close();
         }
 
     }
}