package io.github.frc5024.lib5k.vision.utils;

import io.github.frc5024.lib5k.utils.math.average.MovingAverage;
import io.github.frc5024.lib5k.vision.types.AxisAlignedBoundingBox;

/**
 * A tool for calculating the average of multiple AxisAlignedBoundingBoxes
 */
public class AxisAlignedBoundingBoxMovingAverage extends MovingAverage<AxisAlignedBoundingBox> {

    /**
     * Create a AxisAlignedBoundingBoxMovingAverage that will compare the N most
     * recent AxisAlignedBoundingBoxes fed to it
     * 
     * @param size Number of AxisAlignedBoundingBoxes to sample in average
     *             calculation
     */
    public AxisAlignedBoundingBoxMovingAverage(int size) {
        super(size, AxisAlignedBoundingBox.OPERATORS);
    }

}