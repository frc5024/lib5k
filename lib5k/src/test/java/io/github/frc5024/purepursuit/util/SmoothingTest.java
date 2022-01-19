package io.github.frc5024.purepursuit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;
import edu.wpi.first.math.geometry.Translation2d;
import io.github.frc5024.purepursuit.util.Smoothing;


public class SmoothingTest{

    ArrayList<Translation2d> testPath = new ArrayList<Translation2d>();

    @Test
    public void TestPoints(){
        testPath.add(new Translation2d(1,1));
        testPath.add(new Translation2d(2,2));
        testPath.add(new Translation2d(3,3));
        ArrayList<Translation2d> newPath = Smoothing.smooth(testPath, 1, .75, .01);

        // Size remains the samne
        assertEquals(newPath.size(), testPath.size());

        // First point is the same
        assertEquals(newPath.indexOf(0), testPath.indexOf(0));

        // Midpoint placed properly
        assertTrue(newPath.indexOf(1) <= testPath.indexOf(1));

        // End Point is the same
        assertEquals(newPath.indexOf(2), testPath.indexOf(2));
        
    }







}