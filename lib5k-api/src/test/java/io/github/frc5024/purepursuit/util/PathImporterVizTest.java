package io.github.frc5024.purepursuit.util;

import org.junit.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.wpi.first.wpilibj.Filesystem;
import io.github.frc5024.purepursuit.pathgen.RawPath;

public class PathImporterVizTest {

    @Test
    public void testImportedJSONPathSimpleCurveVisualization() throws FileNotFoundException, IOException {

        // Get the path file
        File file = new File(Filesystem.getDeployDirectory() + "/Example_Curve.wpilib.json");

        // Load the file as a path
        RawPath path = PathImporter.jsonToPath(file);

        // Get chart
        XYChart chart = path.getPathVisualization();

        // Save the chart visualization
        BitmapEncoder.saveBitmap(chart, "./build/tmp/PurePursuit_UnitTest_ImportedJSONPathSimpleCurve",
                BitmapFormat.PNG);
        System.out.println(
                "Test result PNG generated to ./build/tmp/PurePursuit_UnitTest_ImportedJSONPathSimpleCurve.png");
    }
    
    @Test
    public void testImportedJSONPathComplexRoutingVisualization() throws FileNotFoundException, IOException {

        // Get the path file
        File file = new File(Filesystem.getDeployDirectory() + "/Complex_Routing.wpilib.json");

        // Load the file as a path
        RawPath path = PathImporter.jsonToPath(file);

        // Get chart
        XYChart chart = path.getPathVisualization();

        // Save the chart visualization
        BitmapEncoder.saveBitmap(chart, "./build/tmp/PurePursuit_UnitTest_ImportedJSONPathComplexRouting", BitmapFormat.PNG);
        System.out.println("Test result PNG generated to ./build/tmp/PurePursuit_UnitTest_ImportedJSONPathComplexRouting.png");
    }
}