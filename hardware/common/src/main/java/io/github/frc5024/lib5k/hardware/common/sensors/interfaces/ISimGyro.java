package io.github.frc5024.lib5k.hardware.common.sensors.interfaces;

import io.github.frc5024.lib5k.hardware.common.drivebase.IDifferentialDrivebase;

public interface ISimGyro extends IGyroscope {

    /**
     * Set up drivebase-locked gyro simulation. This estimates the gyro's angle by
     * analyzing encoder data over time. This is only accurate in simulations, and
     * should never be trusted IRL
     * 
     * @param drivebase Drivebase to read from
     */
    public void initDrivebaseSimulation(IDifferentialDrivebase drivebase);

}