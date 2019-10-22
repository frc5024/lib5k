package frc.lib5k.components;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class TalonEncoder extends EncoderBase {
    WPI_TalonSRX talon;

    public TalonEncoder(WPI_TalonSRX talon) {
        this.talon = talon;
    }

    @Override
    public int getRawTicks() {
        return talon.getSelectedSensorPosition();
    }
    
}