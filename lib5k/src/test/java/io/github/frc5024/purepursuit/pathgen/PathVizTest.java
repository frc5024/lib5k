package io.github.frc5024.purepursuit.pathgen;

import java.io.IOException;

import org.junit.Test;

import edu.wpi.first.wpilibj.geometry.Translation2d;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;

public class PathVizTest {

    @Test
    public void testFourPointPathViz() throws IOException {

        // Create a path
        Path path = new Path(new Translation2d(0.0, 0.0), new Translation2d(1.0, 3.0), new Translation2d(2.0, 2.0),
                new Translation2d(3.0, 3.0));

        // Get chart
        XYChart chart = path.getPathVisualization();

        // Save the chart visualization
        BitmapEncoder.saveBitmap(chart, "./build/tmp/PurePursuit_UnitTest_FourPointPath", BitmapFormat.PNG);
        System.out.println("Test result PNG generated to ./build/tmp/PurePursuit_UnitTest_FourPointPath.png");

    }

    @Test
    public void testSmoothFourPointPathViz() throws IOException {

        // Create a path
        Path path = new SmoothPath(0.5, 0.5, 0.5, new Translation2d(0.0, 0.0), new Translation2d(1.0, 3.0),
                new Translation2d(2.0, 2.0), new Translation2d(3.0, 3.0));

        // Get chart
        XYChart chart = path.getPathVisualization();

        // Save the chart visualization
        BitmapEncoder.saveBitmap(chart, "./build/tmp/PurePursuit_UnitTest_SmoothFourPointPath", BitmapFormat.PNG);
        System.out.println("Test result PNG generated to ./build/tmp/PurePursuit_UnitTest_SmoothFourPointPath.png");

    }

}