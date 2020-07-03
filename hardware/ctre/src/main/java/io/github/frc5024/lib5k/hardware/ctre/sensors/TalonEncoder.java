package io.github.frc5024.lib5k.hardware.ctre.sensors;

import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;

public class TalonEncoder implements CommonEncoder {

    private BaseTalon talon;
    private boolean phase = false;
    private int cpr;

    public TalonEncoder(BaseTalon talon, int cpr) {
        this.talon = talon;
        this.cpr = cpr;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void setInverted(boolean inverted) {
        this.phase = inverted;
        talon.setSensorPhase(inverted);

    }

    @Override
    public boolean getInverted() {
        return this.phase;
    }

    @Override
    public double getPosition() {
        return talon.getSelectedSensorPosition() / this.cpr;
    }

    @Override
    public double getVelocity() {
        return talon.getSelectedSensorVelocity() / 1000 / this.cpr;
    }

}