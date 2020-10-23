package io.github.frc5024.lib5k.bases.drivetrain.implementations;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.lib5k.bases.drivetrain.commands.PathFollowerCommand;
import io.github.frc5024.lib5k.control_loops.ExtendedPIDController;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;
import io.github.frc5024.lib5k.hardware.common.sensors.simulation.GyroSimUtil;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.kauai.gyroscopes.NavX;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.unittest.FakeScheduler;
import io.github.frc5024.lib5k.utils.RobotMath;
import io.github.frc5024.lib5k.utils.TimeScale;
import io.github.frc5024.purepursuit.pathgen.Path;
import io.github.frc5024.purepursuit.pathgen.SmoothPath;

public class DualPIDTankDriveTrainTest {

    private static double SIMULATION_TIME_SECONDS = 15.0;
    private static double PERIOD_SECONDS = 0.02;

    static class TestDriveTrain extends DualPIDTankDriveTrain {

        // PID controllers
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
        private GyroSimUtil gyroSim;

        // Motor and encoder inversion multiplier
        private double motorInversionMultiplier = 1.0;
        private double encoderInversionMultiplier = 1.0;

        public TestDriveTrain() {
            super(rotationController, 0.5);

            // Set inversions on motors
            leftFrontMotor.setInverted(false);
            leftRearMotor.setInverted(false);
            rightFrontMotor.setInverted(true);
            rightRearMotor.setInverted(true);

            // Set the sensor phases
            leftEncoder.setPhaseInverted(false);
            rightEncoder.setPhaseInverted(false);

            // Set up motor simulation
            leftEncoder.initSimulationDevice(leftFrontMotor, GEAR_RATIO,
                    new DCBrushedMotor(DCMotor.getCIM(2)).getFreeSpeedRPM(), ENCODER_RAMP_RATE);
            rightEncoder.initSimulationDevice(rightFrontMotor, GEAR_RATIO,
                    new DCBrushedMotor(DCMotor.getCIM(2)).getFreeSpeedRPM(), ENCODER_RAMP_RATE);

            // Set up gyroscope
            gyroscope = NavX.getInstance();

            // Set up gyro simulation
            gyroSim = gyroscope.initDrivebaseSimulation(this, false);
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

        @Override
        public void close() {
            try {
                leftEncoder.close();
                rightEncoder.close();
                gyroSim.close();
            } catch (Exception e) {

            }
        }

    }

    // The test drivetrain
    private static TestDriveTrain testDrivetrain;

    @BeforeClass
    public static void before(){
        testDrivetrain = new TestDriveTrain();
    }

    @AfterClass
    public static void after() {
        testDrivetrain.close();
    }

    public void chartDriveTrainResponseForPath(TankDriveTrain drivetrain, Path path, String outfileName)
            throws IOException {

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

        // Run the simulation for the set time
        int i = 0;
        simRunner: {
            for (; i < numSamples; i++) {

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

                // Handle command finishing
                if (command.isFinished()) {
                    command.end(false);
                    break simRunner;
                }

            }
            command.end(true);
        }
        RobotLogger.getInstance().flush();

        // Build chart
        XYChart chart = new XYChartBuilder().width(1000).height(600).build();

        // Add data
        chart.addSeries("Reference", Arrays.copyOfRange(referenceXSet, 0, i), Arrays.copyOfRange(referenceYSet, 0, i));
        chart.addSeries("Measurement", Arrays.copyOfRange(measurementXSet, 0, i),
                Arrays.copyOfRange(measurementYSet, 0, i));

        // Configure chart styling
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setMarkerSize(8);

        // Save the chart
        BitmapEncoder.saveBitmap(chart, "./build/tmp/" + outfileName, BitmapFormat.PNG);
        System.out.println("Test result PNG generated to ./build/tmp/" + outfileName + ".png");

    }

    public void ensureDriveTrainStaysOnCourseForPath(TankDriveTrain drivetrain, Path path, double threshEps) {

        // Get a command that can follow the path
        PathFollowerCommand command = drivetrain.createPathingCommand(path, 0.2);


        // Create a fake scheduler
        FakeScheduler runner = new FakeScheduler(drivetrain, command, PERIOD_SECONDS, SIMULATION_TIME_SECONDS){

			@Override
			public void init() {
				
			}

			@Override
            public void periodic(double dt, double cycleNumber, double timeSinceStart, double timeToTimeout) {
                // Get the current and goal poses
                Translation2d currentPose = drivetrain.getPose().getTranslation();
                Translation2d goalPose = command.getMostRecentGoal();

                // Ensure the current pose is near the goal
                assertTrue(String.format("Robot position %s near goal %s", currentPose, goalPose),
                        RobotMath.epsilonEquals(currentPose, goalPose, new Translation2d(threshEps, threshEps)));
				
			}

			@Override
			public void finish() {
				
			}
            
        };

        runner.run();

        // // Determine the number of samples needed
        // int numSamples = (int) (SIMULATION_TIME_SECONDS / PERIOD_SECONDS);

        // // Init the command
        // command.initialize();
        // RobotLogger.getInstance().flush();

        // // Run the simulation for the set time
        // int i = 0;
        // simRunner: {
        //     for (; i < numSamples; i++) {

        //         // Update the drivetrain and command
        //         drivetrain.periodic();
        //         command.execute();
        //         RobotLogger.getInstance().flush();

        //         // Get the current and goal poses
        //         Translation2d currentPose = drivetrain.getPose().getTranslation();
        //         Translation2d goalPose = command.getMostRecentGoal();

        //         // Ensure the current pose is near the goal
        //         assertTrue(String.format("Robot position %s near goal %s", currentPose, goalPose),
        //                 RobotMath.epsilonEquals(currentPose, goalPose, new Translation2d(threshEps, threshEps)));

        //         // Handle command finishing
        //         if (command.isFinished()) {
        //             command.end(false);
        //             break simRunner;
        //         }

        //     }
        //     command.end(true);
        // }
        // RobotLogger.getInstance().flush();

    }

    @Test
    public void testDriveTrainFollowingFourPointPath() throws IOException {

        // Create the path
        Path path = new Path(new Translation2d(0.0, 0.0), new Translation2d(1.0, 3.0), new Translation2d(2.0, 2.0),
                new Translation2d(3.0, 3.0));

        // Test name
        String file = "DualPIDTankDriveTrain_UnitTest_ResponseFourPoint";

        // Globally override the calculation timer
        TimeScale.globallyOverrideCalculationOutput(0.02);

        testDrivetrain.reset();
        testDrivetrain.resetPose(new Pose2d());

        // Chart
        chartDriveTrainResponseForPath(testDrivetrain, path, file);

        testDrivetrain.reset();
        testDrivetrain.resetPose(new Pose2d());

        // Check proximity
        ensureDriveTrainStaysOnCourseForPath(testDrivetrain, path, 0.5);

        // Reset the calculation timer
        TimeScale.globallyOverrideCalculationOutput(null);

    }

    @Test
    public void testDriveTrainFollowingSmoothPath() throws IOException {

        // Create the path
        SmoothPath path = new SmoothPath(0.5, 0.5, 0.5, new Translation2d(0.0, 0.0), new Translation2d(1.0, 3.0),
                new Translation2d(2.0, 2.0), new Translation2d(3.0, 3.0));

        // Test name
        String file = "DualPIDTankDriveTrain_UnitTest_ResponseSmoothed";

        // Globally override the calculation timer
        TimeScale.globallyOverrideCalculationOutput(0.02);

        testDrivetrain.reset();
        testDrivetrain.resetPose(new Pose2d());

        // Chart
        chartDriveTrainResponseForPath(testDrivetrain, path, file);

        testDrivetrain.reset();
        testDrivetrain.resetPose(new Pose2d());

        // Check proximity
        ensureDriveTrainStaysOnCourseForPath(testDrivetrain, path, 0.5);

        // Reset the calculation timer
        TimeScale.globallyOverrideCalculationOutput(null);

    }

}