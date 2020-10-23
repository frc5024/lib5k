package io.github.frc5024.lib5k.bases.drivetrain.implementations;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Test;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import io.github.frc5024.lib5k.bases.drivetrain.commands.PathFollowerCommand;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;
import io.github.frc5024.lib5k.unittest.FakeScheduler;
import io.github.frc5024.lib5k.unittest.Grapher;
import io.github.frc5024.lib5k.utils.RobotMath;
import io.github.frc5024.purepursuit.pathgen.Path;
import io.github.frc5024.purepursuit.pathgen.SmoothPath;

public class DualPIDTankDriveTrainTest {

    private static double SIMULATION_TIME_SECONDS = 15.0;
    private static double PERIOD_SECONDS = 0.02;

    public void chartDriveTrainResponseForPath(TankDriveTrain drivetrain, Path path, Grapher grapher)
            throws IOException {

        // Get a command that can follow the path
        PathFollowerCommand command = drivetrain.createPathingCommand(path, 0.2);

        // Epsilon
        double threshEps = 2.0;

        // Set up data
        ArrayList<Double> referenceXSet = new ArrayList<>();
        ArrayList<Double> referenceYSet = new ArrayList<>();
        ArrayList<Double> measurementXSet = new ArrayList<>();
        ArrayList<Double> measurementYSet = new ArrayList<>();

        // Create a fake scheduler
        FakeScheduler runner = new FakeScheduler(drivetrain, command, PERIOD_SECONDS, SIMULATION_TIME_SECONDS) {

            @Override
            public void init() {

            }

            @Override
            public void periodic(double dt, double cycleNumber, double timeSinceStart, double timeToTimeout) {
                // Get the current and goal poses
                Translation2d currentPose = drivetrain.getPose().getTranslation();
                Translation2d goalPose = command.getMostRecentGoal();

                // Log everything
                referenceXSet.add(goalPose.getX());
                referenceYSet.add(goalPose.getY());
                measurementXSet.add(currentPose.getX());
                measurementYSet.add(currentPose.getY());

                // Ensure the current pose is near the goal
                if (RobotMath.epsilonEquals(currentPose, goalPose, new Translation2d(threshEps, threshEps))) {
                    assertTrue(String.format("Robot position %s near goal %s", currentPose, goalPose), true);
                } else {
                    try {
                        finish();
                        grapher.save();
                    } catch (IOException e) {
                    }
                    // Disable time override
                    FPGAClock.enableSystemClockOverride(false, 0.0);
                    assertTrue(String.format("Robot position %s near goal %s", currentPose, goalPose), false);
                }

            }

            @Override
            public void finish() {

                // Add all data sets
                grapher.addSeries("Reference", referenceXSet, referenceYSet);
                grapher.addSeries("Measurement", measurementXSet, measurementYSet);

            }

        };

        runner.run();
        grapher.save();

    }

    @AfterClass
    public static void after() {
        MockDualPIDTankDriveTrain.INSTANCE.close();
    }
    

    @Test
    public void testDriveTrainFollowingFourPointPath() throws IOException {

        // Build a drivetrain
        MockDualPIDTankDriveTrain testDrivetrain = MockDualPIDTankDriveTrain.INSTANCE;

        // Create the path
        Path path = new Path(new Translation2d(0.0, 0.0), new Translation2d(1.0, 3.0), new Translation2d(2.0, 2.0),
                new Translation2d(3.0, 3.0));

        testDrivetrain.reset();
        testDrivetrain.resetPose(new Pose2d());

        // Chart
        chartDriveTrainResponseForPath(testDrivetrain, path, new Grapher("DualPIDTankDriveTrain", "ResponseFourPoint"));

    }

    // @Test
    // public void testDriveTrainFollowingSmoothPath() throws IOException {

    //     // Build a drivetrain
    //     MockDualPIDTankDriveTrain testDrivetrain = MockDualPIDTankDriveTrain.INSTANCE;

    //     // Create the path
    //     SmoothPath path = new SmoothPath(0.5, 0.5, 0.5, new Translation2d(0.0, 0.0), new Translation2d(1.0, 3.0),
    //             new Translation2d(2.0, 2.0), new Translation2d(3.0, 3.0));

    //     testDrivetrain.reset();
    //     testDrivetrain.resetPose(new Pose2d());

    //     // Chart
    //     chartDriveTrainResponseForPath(testDrivetrain, path, new Grapher("DualPIDTankDriveTrain", "ResponseSmoothed"));

    // }

}