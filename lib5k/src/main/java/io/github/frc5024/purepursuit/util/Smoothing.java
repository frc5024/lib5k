package io.github.frc5024.purepursuit.util;

import java.util.ArrayList;

import edu.wpi.first.math.geometry.Translation2d;

public class Smoothing {

    /**
     * This is a util
     */
    private Smoothing() {
    }

    /**
     * This is an algorithm taken from team 2168 to smooth paths
     * 
     * @param positions     an array list of positions to smooth
     * @param weight_data   changes the amount of smoothing
     * @param weight_smooth the larger this is the smoother the path is
     * @param tolerance     amount the path will change
     * @return a smoothed path
     */
    public static ArrayList<Translation2d> smooth(ArrayList<Translation2d> positions, double weight_data,
            double weight_smooth, double tolerance) {
        // Make a copy of list of positions
        ArrayList<Translation2d> newPositions = positions;

        // Change variable
        double change = tolerance;

        // This exists to prevent an infinite loop
        int countdown = 10000;

        // Run erosion until we reach a timeout, or the tolerance
        while (change >= tolerance && countdown > 0) {
            change = 0.0;

            // Erode every point once
            for (int i = 1; i < positions.size() - 1; i++) {

                // Get the state of this point in the last erosion step
                Translation2d previousPointState = newPositions.get(i);

                // Compute the first erosion factor
                Translation2d firstFactor = previousPointState
                        .plus(positions.get(i).minus(previousPointState).times(weight_data));

                // Compute the second erosion factor
                Translation2d secondFactor = newPositions.get(i - 1).plus(newPositions.get(i + 1))
                        .minus(previousPointState.times(2.0)).times(weight_smooth);

                // Save the eroded point
                newPositions.set(i, firstFactor.plus(secondFactor));

                // Calculate the amount of change
                Translation2d erosionDifference = previousPointState.minus(newPositions.get(i));
                change += Math.abs(erosionDifference.getX()) + Math.abs(erosionDifference.getY());
            }

            countdown -= 1;

        }

        return newPositions;
    }
}