package io.github.frc5024.lib5k.hardware.common.sensors.simulation;

import io.github.frc5024.lib5k.utils.interfaces.PeriodicComponent;

public class GyroSimUtil implements PeriodicComponent {

    public GyroSimUtil() {
        this(0.02, 40);
    }

    public GyroSimUtil(double threadPeriod, double rotationGain) {

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

}