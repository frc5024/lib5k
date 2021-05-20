package io.github.frc5024.lib5k.vision.utils;

import io.github.frc5024.lib5k.utils.math.average.MovingAverage;
import io.github.frc5024.lib5k.vision.types.HyperbolicAxisAlignedBoundingBox;

/**
 * A tool for calculating the average of multiple
 * HyperbolicAxisAlignedBoundingBoxes
 */
public class HyperbolicAxisAlignedBoundingBoxMovingAverage extends MovingAverage<HyperbolicAxisAlignedBoundingBox> {

    /**
     * Create a HyperbolicAxisAlignedBoundingBoxMovingAverage that will compare the
     * N most recent HyperbolicAxisAlignedBoundingBoxes fed to it
     * 
     * @param size Number of HyperbolicAxisAlignedBoundingBoxes to sample in average
     *             calculation
     */
    public HyperbolicAxisAlignedBoundingBoxMovingAverage(int size) {
        super(size, HyperbolicAxisAlignedBoundingBox.OPERATORS);
    }

}