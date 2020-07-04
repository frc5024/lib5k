package io.github.frc5024.lib5k.hardware.revrobotics.motors;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class RevMotorConfig {

    public MotorType motorType;

    public Boolean restoreFactoryDefaults;

    public boolean isInverted;

    public boolean setCurrentLimit;

    public int freeLimit;

    public int stallLimit;

    public int rpmLimit;

    public boolean enableBrakes;

    public RevMotorConfig() {

        motorType = MotorType.kBrushless;

        restoreFactoryDefaults = true;

        isInverted = false;

        setCurrentLimit = true;

        freeLimit = 30;

        stallLimit = 0;

        rpmLimit = 20000;

        enableBrakes = false;

    }

    public RevMotorConfig(MotorType motorType, boolean isInverted, boolean restoreFactoryDefaults, boolean currentLimit,
            int freeLimit, int stallLimit, int rpmLimit, boolean enableBrakes) {

        this.motorType = motorType;

        this.restoreFactoryDefaults = restoreFactoryDefaults;

        this.isInverted = isInverted;

        this.setCurrentLimit = currentLimit;

        this.freeLimit = freeLimit;

        this.stallLimit = stallLimit;

        this.rpmLimit = rpmLimit;

        this.enableBrakes = enableBrakes;

    }

}