package io.github.frc5024.lib5k.hardware.ctre.sensors;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import io.github.frc5024.lib5k.hardware.common.drivebase.IDifferentialDrivebase;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.ISimGyro;

public class ExtendedPigeonIMU extends PigeonIMU implements ISimGyro {

    private boolean inverted = false;

    public ExtendedPigeonIMU(int canID) {
        super(canID);
    }

    public ExtendedPigeonIMU(TalonSRX talonSrx) {
        super(talonSrx);
    }

    @Override
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    @Override
    public double getHeading() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void calibrate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public double getAngle() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getRate() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void close() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void initDrivebaseSimulation(IDifferentialDrivebase drivebase) {
        // TODO Auto-generated method stub

    }
    
}