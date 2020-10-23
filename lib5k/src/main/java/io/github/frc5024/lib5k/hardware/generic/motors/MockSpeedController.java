package io.github.frc5024.lib5k.hardware.generic.motors;

import edu.wpi.first.wpilibj.SpeedController;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.generic.sensors.MockCommonEncoder;

/**
 * For mocking a speed controller in tests
 */
public class MockSpeedController implements SpeedController {

    private double speed = 0.0;
    private boolean inverted = false;

    public CommonEncoder getCommonEncoder(int cpr) {
        return new MockCommonEncoder(cpr);
    }

    @Override
    public void set(double speed) {
        this.speed = speed * (getInverted() ? -1 : 1);
    }

    @Override
    public double get() {
        return speed;
    }

    @Override
    public void stopMotor() {
        set(0.0);
    }

    @Override
    public void disable() {
        set(0.0);
    }

    @Override
    public void pidWrite(double output) {
        set(output);

    }

    @Override
    public void setInverted(boolean inverted) {
        this.inverted = inverted;

    }

    @Override
    public boolean getInverted() {
        return inverted;
    }

}