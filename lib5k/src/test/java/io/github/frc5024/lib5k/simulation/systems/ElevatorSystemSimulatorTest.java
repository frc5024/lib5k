package io.github.frc5024.lib5k.simulation.systems;

import java.io.IOException;

import org.junit.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

import io.github.frc5024.lib5k.control_loops.statespace.wrappers.RangeFinderElevatorController;
import io.github.frc5024.lib5k.utils.RobotPresets;

public class ElevatorSystemSimulatorTest {

    private static double SIMULATION_TIME_SECONDS = 5.0;
    private static double PERIOD_SECONDS = 0.02;

    @Test
    public void chartElevatorSystemResponse() throws IOException {

        // Build a flywheel controller
        RangeFinderElevatorController controller = new RangeFinderElevatorController(
                RobotPresets.Imaginary.ElevatorPreset.MOTOR_TYPE,
                RobotPresets.Imaginary.ElevatorPreset.CARRIAGE_MASS_KG,
                RobotPresets.Imaginary.ElevatorPreset.CARRIAGE_MAX_VELOCITY,
                RobotPresets.Imaginary.ElevatorPreset.CARRIAGE_MAX_ACCELERATION,
                RobotPresets.Imaginary.ElevatorPreset.POSITION_EPSILON,
                RobotPresets.Imaginary.ElevatorPreset.VELOCITY_EPSILON);

        // Build a flywheel simulator
        ElevatorSystemSimulator simulator = new ElevatorSystemSimulator(controller, 1.0, 0.0, 2.5);

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
            double measurement = simulator.getPositionMeters();

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
        BitmapEncoder.saveBitmap(chart, "./build/tmp/ElevatorSystemSimulator_UnitTest_Response", BitmapFormat.PNG);
        System.out.println("Test result PNG generated to ./build/tmp/ElevatorSystemSimulator_UnitTest_Response.png");

    }

}