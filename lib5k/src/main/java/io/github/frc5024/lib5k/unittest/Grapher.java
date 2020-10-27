package io.github.frc5024.lib5k.unittest;

import java.io.IOException;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * Grapher is a utility for producing graphs from a unit test
 */
public class Grapher {

    // CHart
    private XYChart chart;

    // Names
    private String name;
    private String identifier;

    /**
     * Create a Grapher (this only works in unit tests)
     * 
     * @param testName       Name of the test producing this graph
     * @param testIdentifier Identifier of this specific graph
     */
    public Grapher(String testName, String testIdentifier) {
        this(testName, testIdentifier, 1000, 600);
    }

    /**
     * Create a Grapher (this only works in unit tests)
     * 
     * @param testName       Name of the test producing this graph
     * @param testIdentifier Identifier of this specific graph
     * @param widthPX        Width of the graph
     * @param heightPX       Height of the graph
     */
    public Grapher(String testName, String testIdentifier, int widthPX, int heightPX) {

        // Make a big warning if not running in simulation
        if (RobotBase.isReal()) {
            throw new RuntimeException("Grapher does not work on a real robot!");
        }

        // Build chart
        chart = new XYChartBuilder().width(widthPX).height(heightPX).build();

        // Configure chart styling
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setMarkerSize(8);
    }

    /**
     * Add an X/Y series to the graph
     * 
     * @param name Series name
     * @param x    X data
     * @param y    Y data
     * @return XYSeries for customization
     */
    public XYSeries addSeries(String name, List<Double> x, List<Double> y) {

        // The sizes must be the same
        if (x.size() != y.size()) {
            throw new IllegalArgumentException("X and Y data must be the same length");
        }

        // Pre-check the data
        for (int i = 0; i < x.size(); i++) {

            // Handle infinity
            if (Double.isInfinite(x.get(i))) {
                throw new IllegalArgumentException(String.format(
                        "X data element %d (value: %s) is invalid. Inputs must be finite", i, x.get(i).toString()));
            }

            // Handle NAN
            if (Double.isNaN(x.get(i))) {
                throw new IllegalArgumentException(String.format(
                        "X data element %d (value: %s) is invalid. Inputs must be numbers", i, x.get(i).toString()));
            }
        }
        for (int i = 0; i < y.size(); i++) {

            // Handle infinity
            if (Double.isInfinite(y.get(i))) {
                throw new IllegalArgumentException(String.format(
                        "Y data element %d (value: %s) is invalid. Inputs must be finite", i, y.get(i).toString()));
            }

            // Handle NAN
            if (Double.isNaN(y.get(i))) {
                throw new IllegalArgumentException(String.format(
                        "Y data element %d (value: %s) is invalid. Inputs must be numbers", i, y.get(i).toString()));
            }
        }

        // Add the series
        return chart.addSeries(name, x, y);

    }

    /**
     * Access the internal XYChart
     * 
     * @return Internal chart
     */
    public XYChart getChart() {
        return chart;
    }

    /**
     * Save this graph to disk
     * 
     * @throws IOException Thrown if save failed
     */
    public void save() throws IOException {
        BitmapEncoder.saveBitmap(chart, String.format("./build/tmp/%s_UnitTest_%s", this.name, this.identifier),
                BitmapFormat.PNG);
        System.out.println(String.format("Test result PNG generated to ./build/tmp/%s_UnitTest_%s.png", this.name,
                this.identifier));
    }

}