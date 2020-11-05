package io.github.frc5024.purepursuit.util;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import io.github.frc5024.lib5k.utils.algorithmic.MutableTranslation;

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
    public static List<Translation2d> smooth(ArrayList<Translation2d> positions, double weight_data,
            double weight_smooth, double tolerance) {
        // Make a copy of list of positions to erode, and a list of their original
        // positions
        MutableTranslation[] erodingPoints = ListMutationConversionUtils.makeMutable(positions);
        MutableTranslation[] linearPoints = ListMutationConversionUtils.makeMutable(positions);

        // ArrayList<Translation2d> newPositions = positions;

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
                MutableTranslation previousPointState = erodingPoints[i].copy();

                // Compute the first erosion factor
                MutableTranslation firstFactor = previousPointState
                        .plus(linearPoints[i].minus(previousPointState).times(weight_data));

                // Compute the second erosion factor
                MutableTranslation secondFactor = erodingPoints[i - 1].copy().plus(erodingPoints[i - 1].copy())
                        .minus(previousPointState.copy().times(2.0)).times(weight_smooth);

                // Save the eroded point
                erodingPoints[i] = firstFactor.plus(secondFactor);

                // Calculate the amount of change
                MutableTranslation erosionDifference = previousPointState.minus(erodingPoints[i]);
                change += Math.abs(erosionDifference.getX()) + Math.abs(erosionDifference.getY());
            }

            countdown -= 1;

        }

        return List.of(ListMutationConversionUtils.makeImmutable(erodingPoints));
    }
}