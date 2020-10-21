package io.github.frc5024.lib5k.bases.drivetrain.implementations;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import org.junit.Test;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj.util.Units;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.lib5k.control_loops.ExtendedPIDController;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.kauai.gyroscopes.NavX;

public class DualPIDTankDriveTrainTest {

    static class TestDriveTrain extends DualPIDTankDriveTrain {

        // PID controllers
        private static ExtendedPIDController velocityController = new ExtendedPIDController(0.478, 0.0, 0.008);
        private static ExtendedPIDController rotationController = new ExtendedPIDController(0.0088, 0.01, 0.0106);

        // Parameters
        private static double TRACK_WIDTH_M = 0.1524;
        private static double ENCODER_RAMP_RATE = 0.12;
        private static double GEAR_RATIO = 8.45;
        private static double WHEEL_RADIUS = Units.inchesToMeters(3.0);

        // Motors
        private ExtendedTalonSRX leftFrontMotor = new ExtendedTalonSRX(1);
        private ExtendedTalonSRX leftRearMotor = new ExtendedTalonSRX(2);
        private ExtendedTalonSRX rightFrontMotor = new ExtendedTalonSRX(3);
        private ExtendedTalonSRX rightRearMotor = new ExtendedTalonSRX(4);

        // Encoders
        private EncoderSimulation leftEncoder = (EncoderSimulation) leftFrontMotor.getCommonEncoder(1400);
        private EncoderSimulation rightEncoder = (EncoderSimulation) rightFrontMotor.getCommonEncoder(1400);

        // Gyroscope
        private NavX gyroscope = NavX.getInstance();

        // Motor and encoder inversion multiplier
        private double motorInversionMultiplier = 1.0;
        private double encoderInversionMultiplier = 1.0;

        public TestDriveTrain() {
            super(velocityController, rotationController);

            // Set inversions on motors
            leftFrontMotor.setInverted(false);
            leftRearMotor.setInverted(false);
            rightFrontMotor.setInverted(true);
            rightRearMotor.setInverted(true);

            // Set the sensor phases
            leftFrontMotor.setSensorPhase(false);
            rightFrontMotor.setSensorPhase(false);

            // Set up motor simulation
            leftEncoder.initSimulationDevice(leftFrontMotor, GEAR_RATIO,
                    new DCBrushedMotor(DCMotor.getCIM(2)).getFreeSpeedRPM(), ENCODER_RAMP_RATE);
            rightEncoder.initSimulationDevice(rightFrontMotor, GEAR_RATIO,
                    new DCBrushedMotor(DCMotor.getCIM(2)).getFreeSpeedRPM(), ENCODER_RAMP_RATE);

            // Set up gyroscope
            gyroscope = NavX.getInstance();

            // Set up gyro simulation
            gyroscope.initDrivebaseSimulation(this);
        }

        @Override
        public double getLeftMeters() {
            return leftEncoder.getPosition() * (2 * Math.PI * WHEEL_RADIUS) * encoderInversionMultiplier;
        }

        @Override
        public double getRightMeters() {
            return rightEncoder.getPosition() * (2 * Math.PI * WHEEL_RADIUS) * encoderInversionMultiplier;
        }

        @Override
        public double getWidthMeters() {
            return TRACK_WIDTH_M;
        }

        @Override
        protected void handleVoltage(double leftVolts, double rightVolts) {
            leftFrontMotor.setVoltage(leftVolts * motorInversionMultiplier);
            rightFrontMotor.setVoltage(rightVolts * motorInversionMultiplier);
        }

        @Override
        protected void resetEncoders() {
            leftEncoder.reset();
            rightEncoder.reset();
        }

        @Override
        protected void setMotorsInverted(boolean motorsInverted) {
            this.motorInversionMultiplier = (motorsInverted) ? -1.0 : 1.0;
        }

        @Override
        protected void setEncodersInverted(boolean encodersInverted) {
            this.encoderInversionMultiplier = (encodersInverted) ? -1.0 : 1.0;
        }

        @Override
        protected void handleGearShift(Gear gear) {
            // Do nothing here since we dont have a gear shifter
        }

        @Override
        protected void enableBrakes(boolean enabled) {
            if (enabled) {
                leftFrontMotor.setNeutralMode(NeutralMode.Brake);
                rightFrontMotor.setNeutralMode(NeutralMode.Brake);
            } else {
                leftFrontMotor.setNeutralMode(NeutralMode.Coast);
                rightFrontMotor.setNeutralMode(NeutralMode.Coast);
            }
        }

        @Override
        protected Rotation2d getCurrentHeading() {
            return gyroscope.getRotation();
        }

        @Override
        protected void runIteration() {
        }

        @Override
        public void setRampRate(double rampTimeSeconds) {
            leftFrontMotor.configOpenloopRamp(rampTimeSeconds);
            rightFrontMotor.configOpenloopRamp(rampTimeSeconds);
        }

    }

    @Test
    public void chartDriveTrainResponse() {
        
    }

}