package frc.lib5k.components;

import java.util.ArrayList;

public abstract class EncoderBase {
    int encoder_offset;
    private int speed = 0;
    private int previous_ticks;
    private ArrayList<Integer> pastSpeeds = new ArrayList<Integer>();
    private final int MAX_READINGS = 5;

    public abstract int getRawTicks();

    public int getTicks() {
        return getRawTicks() - encoder_offset;
    }

    public double getMeters(int tpr, double wheel_circumference) {
        return ((getTicks() / tpr) * wheel_circumference) / 100.0;
    }

    public double getMetersPerCycle(int tpr, double wheel_circumference) {
        return ((getSpeed() / tpr) * wheel_circumference) / 100.0;
    }

    public void zero() {
        encoder_offset = getRawTicks();
    }

    public void fullReset() {
        encoder_offset = 0;
    }

    public int getSpeed() {
        return speed;
    }

    public double getAverageSpeed() {
        double output = 0.0;

        for (Integer speed : pastSpeeds) {
            output += speed;
        }

        return output / pastSpeeds.size();
    }

    public void update() {
        // Determine current speed
        int current_ticks = getTicks();
        speed = current_ticks - previous_ticks;
        previous_ticks = current_ticks;

        // Clean up average
        if (pastSpeeds.size() >= MAX_READINGS) {
            pastSpeeds.remove(0);
        }

        pastSpeeds.add(speed);

    }

}