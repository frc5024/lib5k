package io.github.frc5024.lib5k.unittest;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GrapherTest {

    @Test
    public void testGrapherThrowsNoErrors() {

        Grapher g = new Grapher("GrapherTest", "testGrapherThrowsNoErrors");

        ArrayList<Double> x = new ArrayList<>();
        ArrayList<Double> y = new ArrayList<>();

        x.add(0.);
        y.add(0.);

        g.addSeries("Test", x, y);

        assertTrue("Passed test", true);
    }

    @Test
    public void testGrapherThrowsErrorWhenXYNotEqual() {

        Grapher g = new Grapher("GrapherTest", "testGrapherThrowsErrorWhenXYNotEqual");

        ArrayList<Double> x = new ArrayList<>();
        ArrayList<Double> y = new ArrayList<>();

        x.add(0.);
        y.add(0.);
        y.add(1.);

        assertThrows(IllegalArgumentException.class, () -> {
            g.addSeries("Test", x, y);
        });

    }

    @Test
    public void testGrapherThrowsErrorWhenInfinite() {

        Grapher g = new Grapher("GrapherTest", "testGrapherThrowsErrorWhenInfinite");

        ArrayList<Double> x = new ArrayList<>();
        ArrayList<Double> y = new ArrayList<>();

        x.add(Double.NEGATIVE_INFINITY);
        y.add(0.);

        assertThrows(IllegalArgumentException.class, () -> {
            g.addSeries("Test", x, y);
        });

    }

    @Test
    public void testGrapherThrowsErrorWhenNAN() {

        Grapher g = new Grapher("GrapherTest", "testGrapherThrowsErrorWhenNAN");

        ArrayList<Double> x = new ArrayList<>();
        ArrayList<Double> y = new ArrayList<>();

        x.add(Double.NaN);
        y.add(0.);

        assertThrows(IllegalArgumentException.class, () -> {
            g.addSeries("Test", x, y);
        });

    }

}