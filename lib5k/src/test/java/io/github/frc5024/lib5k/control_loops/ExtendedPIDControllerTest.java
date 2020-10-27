package io.github.frc5024.lib5k.control_loops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import io.github.frc5024.lib5k.control_loops.statespace.wrappers.SimpleFlywheelController;
import io.github.frc5024.lib5k.simulation.systems.FlywheelSystemSimulator;
import io.github.frc5024.lib5k.unittest.FakeScheduler;
import io.github.frc5024.lib5k.unittest.Grapher;
import io.github.frc5024.lib5k.utils.RobotMath;
import io.github.frc5024.lib5k.utils.RobotPresets;

public class ExtendedPIDControllerTest {

    private static double SIMULATION_TIME_SECONDS = 5.0;
    private static double PERIOD_SECONDS = 0.02;

    @Test
    public void chartFlywheelSystemResponse() throws IOException {

        // Build a flywheel controller
        ExtendedPIDController controller = new ExtendedPIDController(0.043, 0.0, 0.0);

        // Build a flywheel simulator
        FlywheelSystemSimulator simulator = new FlywheelSystemSimulator(
                new SimpleFlywheelController(RobotPresets.DarthRaider.FlywheelPreset.MOTOR_TYPE,
                        RobotPresets.DarthRaider.FlywheelPreset.LAUNCHER_MASS_KG,
                        RobotPresets.DarthRaider.FlywheelPreset.LAUNCHER_DIAMETER,
                        RobotPresets.DarthRaider.FlywheelPreset.FLYWHEEL_MASS_KG,
                        RobotPresets.DarthRaider.FlywheelPreset.FLYWHEEL_DIAMETER,
                        RobotPresets.DarthRaider.FlywheelPreset.REALISTIC_MAX_VELOCITY_RPM,
                        RobotPresets.DarthRaider.FlywheelPreset.SENSOR_GEAR_RATIO, 12.0,
                        RobotPresets.DarthRaider.FlywheelPreset.VELOCITY_EPSILON_RPM));

        // Graph
        Grapher graph = new Grapher("ExtendedPIDController", "FlywheelResponse");
        ArrayList<Double> timeSet = new ArrayList<>();
        ArrayList<Double> referenceSet = new ArrayList<>();
        ArrayList<Double> voltageSet = new ArrayList<>();
        ArrayList<Double> measurementSet = new ArrayList<>();

        // Scheduler
        FakeScheduler scheduler = new FakeScheduler(PERIOD_SECONDS, SIMULATION_TIME_SECONDS) {

            @Override
            public void init() {
                // Stop the motor
                simulator.setInputVoltage(0.0);
            }

            @Override
            public void periodic(double dt, double cycleNumber, double timeSinceStart, double timeToTimeout) {
                // Handle setting reference high for the first half of the test, low for the
                // second half
                double reference;
                if (timeSinceStart < timeToTimeout) {
                    reference = RobotPresets.DarthRaider.FlywheelPreset.REALISTIC_MAX_VELOCITY_RPM;
                } else {
                    reference = 0.0;
                }
                controller.setReference(reference);

                // Get the controller output
                double measurement = simulator.getAngularVelocityRPM();

                // Calculate a new voltage
                double output = RobotMath.clamp(controller.calculate(measurement), -12.0, 12.0);

                // Feed back into the simulator
                simulator.setInputVoltage(output);
                simulator.update(dt);

                // Plot data
                timeSet.add(cycleNumber * dt);
                referenceSet.add(reference);
                voltageSet.add(output);
                measurementSet.add(measurement);

            }

            @Override
            public void finish() {
                graph.addSeries("Reference", timeSet, referenceSet).setYAxisGroup(1);
                graph.addSeries("Voltage", timeSet, voltageSet);
                graph.addSeries("Measurement", timeSet, measurementSet).setYAxisGroup(1);

                controller.close();

            }

            @Override
            public boolean isFinished() {
                return false;
            }

        };

        // Run
        scheduler.run();

        // Save
        graph.save();

    }

    @Test
    public void testFlywheelControlStability() {

        // Build a flywheel controller
        ExtendedPIDController controller = new ExtendedPIDController(0.043, 0.0, 0.0);

        // Veocity eps
        double velocityEpsilon = 210;

        // Build a flywheel simulator
        FlywheelSystemSimulator simulator = new FlywheelSystemSimulator(
                new SimpleFlywheelController(RobotPresets.DarthRaider.FlywheelPreset.MOTOR_TYPE,
                        RobotPresets.DarthRaider.FlywheelPreset.LAUNCHER_MASS_KG,
                        RobotPresets.DarthRaider.FlywheelPreset.LAUNCHER_DIAMETER,
                        RobotPresets.DarthRaider.FlywheelPreset.FLYWHEEL_MASS_KG,
                        RobotPresets.DarthRaider.FlywheelPreset.FLYWHEEL_DIAMETER,
                        RobotPresets.DarthRaider.FlywheelPreset.REALISTIC_MAX_VELOCITY_RPM,
                        RobotPresets.DarthRaider.FlywheelPreset.SENSOR_GEAR_RATIO, 12.0, velocityEpsilon));

        // Scheduler
        FakeScheduler scheduler = new FakeScheduler(PERIOD_SECONDS, SIMULATION_TIME_SECONDS) {

            @Override
            public void init() {
                // Stop the motor
                simulator.setInputVoltage(0.0);
            }

            @Override
            public void periodic(double dt, double cycleNumber, double timeSinceStart, double timeToTimeout) {
                // Handle setting reference high for the first half of the test, low for the
                // second half
                double reference;
                if (timeSinceStart < timeToTimeout) {
                    reference = RobotPresets.DarthRaider.FlywheelPreset.REALISTIC_MAX_VELOCITY_RPM;
                } else {
                    reference = 0.0;
                }
                controller.setReference(reference);

                // Get the controller output
                double measurement = simulator.getAngularVelocityRPM();

                // Calculate a new voltage
                double output = RobotMath.clamp(controller.calculate(measurement), -12.0, 12.0);

                // Feed back into the simulator
                simulator.setInputVoltage(output);
                simulator.update(dt);

                // 1 second after the reference switches, ensure the controller is stable
                if ((timeSinceStart > 1 && timeSinceStart < 2) || (timeSinceStart > 3 && timeSinceStart < 4)) {

                    // Ensure the measurement is good
                    assertTrue(
                            String.format("%.2f within %.2fRPM of %.2f at %.2f seconds", measurement, velocityEpsilon,
                                    reference, timeSinceStart),
                            RobotMath.epsilonEquals(measurement, reference, velocityEpsilon));
                }

            }

            @Override
            public void finish() {
                controller.close();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

        };

        // Run
        scheduler.run();

    }

}