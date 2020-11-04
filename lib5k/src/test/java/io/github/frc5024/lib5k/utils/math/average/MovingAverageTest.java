package io.github.frc5024.lib5k.utils.math.average;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.github.frc5024.lib5k.utils.RobotMath;

public class MovingAverageTest {

    @Test
    public void testEmptyBuffer() {

        // AVG tool
        DoubleMovingAverage avg = new DoubleMovingAverage(5);

        // Check that we get null value and no size
        assertEquals(0, avg.getUsage());
        assertEquals(null, avg.getAverage());

    }

    @Test
    public void testOneItem() {

        // AVG tool
        DoubleMovingAverage avg = new DoubleMovingAverage(5);

        avg.add(20.0);

        // Check that we get 1 item thats equal to the value
        assertEquals(1, avg.getUsage());
        assertEquals(20.0, avg.getAverage(), RobotMath.kVerySmallNumber);

    }

    @Test
    public void testFull() {

        // AVG tool
        DoubleMovingAverage avg = new DoubleMovingAverage(5);

        avg.add(20.0);
        avg.add(20.0);
        avg.add(25.0);
        avg.add(30.0);
        avg.add(30.0);

        // Check that we get 1 item thats equal to the value
        assertEquals(5, avg.getUsage());
        assertEquals(25.0, avg.getAverage(), RobotMath.kVerySmallNumber);

    }


}