package io.github.frc5024.lib5k.simulation.systems;

import java.io.IOException;

import org.junit.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

import io.github.frc5024.lib5k.control_loops.statespace.wrappers.SimpleFlywheelController;
import io.github.frc5024.lib5k.utils.RobotPresets;

public class FlywheelSystemSimulatorTest {

    private static double SIMULATION_TIME_SECONDS = 5.0;
    private static double PERIOD_SECONDS = 0.02;

    @Test
    public void chartFlywheelSystemResponse() throws IOException {

        // Build a flywheel controller
        SimpleFlywheelController controller = new SimpleFlywheelController(
                RobotPresets.DarthRaider.FlywheelPreset.MOTOR_TYPE,
                RobotPresets.DarthRaider.FlywheelPreset.LAUNCHER_MASS_KG,
                RobotPresets.DarthRaider.FlywheelPreset.LAUNCHER_DIAMETER,
                RobotPresets.DarthRaider.FlywheelPreset.FLYWHEEL_MASS_KG,
                RobotPresets.DarthRaider.FlywheelPreset.FLYWHEEL_DIAMETER,
                RobotPresets.DarthRaider.FlywheelPreset.REALISTIC_MAX_VELOCITY_RPM,
                RobotPresets.DarthRaider.FlywheelPreset.SENSOR_GEAR_RATIO, 12.0,
                RobotPresets.DarthRaider.FlywheelPreset.VELOCITY_EPSILON_RPM);

        // Build a flywheel simulator
        FlywheelSystemSimulator simulator = new FlywheelSystemSimulator(controller);

        // Determine the number of samples needed
        int numSamples = (int) (SIMULATION_TIME_SECONDS / PERIOD_SECONDS);

        // Set up a chart
        double[] timeSet = new double[numSamples];
        double[] referenceSet = new double[numSamples];
        double[] measurementSet = new double[numSamples];

        // Run the simulation for the set time
        simulator.setInputVoltage(0.0);
        for (int i = 0; i < numSamples; i++) {
            // Handle setting reference high for the first half of the test, low for the
            // second half
            double reference;
            if (i < (numSamples / 2)) {
                reference = 12.0;
            } else {
                reference = 0.0;
            }

            // Feed back into the simulator
            simulator.setInputVoltage(reference);
            simulator.update(PERIOD_SECONDS);

            // Get the controller output
            double measurement = simulator.getAngularVelocityRPM();

            // Log everything
            timeSet[i] = i * PERIOD_SECONDS;
            referenceSet[i] = reference;
            measurementSet[i] = measurement;
        }

        // Build chart
        XYChart chart = new XYChartBuilder().width(1000).height(600).build();

        // Add data
        chart.addSeries("Reference", timeSet, referenceSet).setYAxisGroup(1);
        chart.addSeries("Measurement", timeSet, measurementSet);

        // Configure chart styling
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setMarkerSize(8);

        // Save the chart
        BitmapEncoder.saveBitmap(chart, "./build/tmp/FlywheelSystemSimulator_UnitTest_Response", BitmapFormat.PNG);
        System.out.println("Test result PNG generated to ./build/tmp/FlywheelSystemSimulator_UnitTest_Response.png");

    }

}