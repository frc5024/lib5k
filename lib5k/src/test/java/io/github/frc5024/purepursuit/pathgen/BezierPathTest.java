package io.github.frc5024.purepursuit.pathgen;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;

import edu.wpi.first.wpilibj.geometry.Translation2d;

public class BezierPathTest {

	// Default waypoints
	Translation2d[] wayPoints = { new Translation2d(0, 0), new Translation2d(15, 15), new Translation2d(30, 5) };
	Translation2d[] wayPoints6 = { new Translation2d(10, 10), new Translation2d(15, 37), new Translation2d(25, 35),
		new Translation2d(40, 32), new Translation2d(60, 17), new Translation2d(5, 5)};

	@Test
	public void testBezierPathGeneration() throws IOException {

		// Creates a bezier path
		BezierPath path = new BezierPath(wayPoints);

		// Makes a chart and saves it
		XYChart chart = path.getPathVisualization();

		BitmapEncoder.saveBitmap(chart, "./build/tmp/PurePursuit_UnitTest_QuadraticBezierCurve",
                BitmapFormat.PNG);
        System.out.println(
                "Test result PNG generated to ./build/tmp/PurePursuit_UnitTest_QuadraticBezierCurve.png");

	}

	@Test
	public void testLookUpList() throws IOException {
		
		// Creates a bezier path
		BezierPath path = new BezierPath(wayPoints6);

		// Makes a chart and saves it
		XYChart chart = path.getPathVisualization();

		BitmapEncoder.saveBitmap(chart, "./build/tmp/PurePursuit_UnitTest_LookupTableTest",
                BitmapFormat.PNG);
        System.out.println(
                "Test result PNG generated to ./build/tmp/PurePursuit_UnitTest_QuadraticBezierCurve.png");
		
		
	}

}
