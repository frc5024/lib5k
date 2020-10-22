package io.github.frc5024.lib5k.hardware.common.sensors.interfaces;

import io.github.frc5024.lib5k.hardware.common.drivebase.IDifferentialDrivebase;
import io.github.frc5024.lib5k.hardware.common.sensors.simulation.GyroSimUtil;

public interface ISimGyro extends IGyroscope {

    /**
     * Set up drivebase-locked gyro simulation. This estimates the gyro's angle by
     * analyzing encoder data over time. This is only accurate in simulations, and
     * should never be trusted IRL
     * 
     * @param drivebase Drivebase to read from
     * @return Reference to the simulator
     */
    public GyroSimUtil initDrivebaseSimulation(IDifferentialDrivebase drivebase);

}