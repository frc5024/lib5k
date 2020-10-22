package io.github.frc5024.lib5k.bases.drivetrain.implementations;

import java.io.IOException;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import org.junit.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj.util.Units;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.lib5k.bases.drivetrain.commands.PathFollowerCommand;
import io.github.frc5024.lib5k.control_loops.ExtendedPIDController;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;
import io.github.frc5024.lib5k.hardware.common.sensors.simulation.GyroSimUtil;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.kauai.gyroscopes.NavX;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.utils.TimeScale;
import io.github.frc5024.purepursuit.pathgen.Path;

public class DualPIDTankDriveTrainTest {

    private static double SIMULATION_TIME_SECONDS = 15.0;
    private static double PERIOD_SECONDS = 0.02;

    static class TestDriveTrain extends DualPIDTankDriveTrain {

        // PID controllers
        private static ExtendedPIDController velocityController = new ExtendedPIDController(1.0, 0.0, 0.0);
        private static ExtendedPIDController rotationController = new ExtendedPIDController(0.002, 0, 0); // new
                                                                                                           // ExtendedPIDController(0.0088,
                                                                                                           // 0.01,
                                                                                                           // 0.0106);

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
        private GyroSimUtil gyroSim;

        // Motor and encoder inversion multiplier
        private double motorInversionMultiplier = 1.0;
        private double encoderInversionMultiplier = 1.0;

        public TestDriveTrain() {
            super(velocityController, rotationController, 1.0);

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
            gyroSim = gyroscope.initDrivebaseSimulation(this);
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
            leftEncoder.update();
            rightEncoder.update();
            gyroSim.update();
        }

        @Override
        public void setRampRate(double rampTimeSeconds) {
            leftFrontMotor.configOpenloopRamp(rampTimeSeconds);
            rightFrontMotor.configOpenloopRamp(rampTimeSeconds);
        }

    }

    @Test
    public void chartDriveTrainResponse() throws IOException {

        // Create a drivetrain
        TestDriveTrain drivetrain = new TestDriveTrain();
        RobotLogger.getInstance().flush();

        // Create a new path
        Path path = new Path(0.5, new Translation2d(0.0, 0.0), new Translation2d(1.0, 3.0), new Translation2d(2.0, 2.0),
                new Translation2d(3.0, 3.0));

        // Get a command that can follow the path
        PathFollowerCommand command = drivetrain.createPathingCommand(path, 0.2);

        // Determine the number of samples needed
        int numSamples = (int) (SIMULATION_TIME_SECONDS / PERIOD_SECONDS);

        // Set up a chart
        double[] referenceXSet = new double[numSamples];
        double[] referenceYSet = new double[numSamples];
        double[] measurementXSet = new double[numSamples];
        double[] measurementYSet = new double[numSamples];

        // Init the command
        command.initialize();
        RobotLogger.getInstance().flush();

        // Globally override the calculation timer
        TimeScale.globallyOverrideCalculationOutput(0.02);

        // Run the simulation for the set time
        for (int i = 0; i < numSamples; i++) {

            // Update the drivetrain and command
            drivetrain.periodic();
            command.execute();
            RobotLogger.getInstance().flush();

            // Get the current and goal poses
            Translation2d currentPose = drivetrain.getPose().getTranslation();
            Translation2d goalPose = command.getMostRecentGoal();

            // Log everything
            referenceXSet[i] = goalPose.getX();
            referenceYSet[i] = goalPose.getY();
            measurementXSet[i] = currentPose.getX();
            measurementYSet[i] = currentPose.getY();
        }

        // Build chart
        XYChart chart = new XYChartBuilder().width(1000).height(600).build();

        // Add data
        chart.addSeries("Reference", referenceXSet, referenceYSet);
        chart.addSeries("Measurement", measurementXSet, measurementYSet);

        // Configure chart styling
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setMarkerSize(8);

        // Save the chart
        BitmapEncoder.saveBitmap(chart, "./build/tmp/DualPIDTankDriveTrain_UnitTest_Response", BitmapFormat.PNG);
        System.out.println("Test result PNG generated to ./build/tmp/DualPIDTankDriveTrain_UnitTest_Response.png");

    }

}