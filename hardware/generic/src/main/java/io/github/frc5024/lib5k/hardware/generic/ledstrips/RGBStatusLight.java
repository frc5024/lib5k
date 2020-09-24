package io.github.frc5024.lib5k.hardware.generic.ledstrips;

public class RGBStatusLight {

    // LED channels
    private AnalogOutput rChannel;
    private AnalogOutput gChannel;
    private AnalogOutput bChannel;

    // Maximum voltage
    private double maxVoltage;

    public RGBStatusLight(AnalogOutput r, AnalogOutput g, AnalogOutput b, double maxVoltage) {
        this.rChannel = r;
        this.gChannel = g;
        this.bChannel = b;
        this.maxVoltage = maxVoltage;
    }

    public void setRGB(float r, float g, float b) {
        this.rChannel.setVoltage((double) (r * this.maxVoltage));
        this.gChannel.setVoltage((double) (g * this.maxVoltage));
        this.bChannel.setVoltage((double) (b * this.maxVoltage));
    }
}
