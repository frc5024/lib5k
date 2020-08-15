package io.github.frc5024.purepursuit.pathgen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.wpi.first.wpilibj.geometry.Translation2d;

/**
 * The PathImporter is a tool for importing Paths from config files
 */
public class PathImporter {

    /**
     * Load a WPILib PathWeaver JSON file as a Lib5K Path
     * 
     * @param jsonFile JSON file
     * @return Loaded Path object
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static RawPath jsonToPath(File jsonFile) throws IOException, FileNotFoundException {

        // Create a file reader
        FileReader reader = new FileReader(jsonFile);

        // Create a new datatype for a list of WPI_PathPoints
        Type listType = new TypeToken<List<WPI_PathPoint>>() {
        }.getType();

        // Parse with Gson
        Gson gson = new Gson();
        List<WPI_PathPoint> rawPoints = gson.fromJson(reader, listType);

        // Strip out only the info we need from the JSON file
        Translation2d[] points = new Translation2d[rawPoints.size()];

        int i = 0;
        for (WPI_PathPoint point : rawPoints) {

            // Get the generated position
            Translation2d genPose = point.getTranslation();
        
            // Modify the point to match the 5024 coordinate system (0.0 is centre of the field width)
            points[i] = new Translation2d(genPose.getX(), genPose.getY() + (8.23 / 2.0));
            i++;
        }

        // Build a path object
        RawPath path = new RawPath(points);

        return path;

    }
}