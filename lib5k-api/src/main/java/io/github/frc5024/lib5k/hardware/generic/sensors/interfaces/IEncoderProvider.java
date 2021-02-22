package io.github.frc5024.lib5k.hardware.generic.sensors.interfaces;

import io.github.frc5024.lib5k.hardware.generic.sensors.EncoderBase;

@Deprecated(since = "July 2020", forRemoval = true)
@SuppressWarnings("checkstyle:javadocmethod")
public interface IEncoderProvider {

    /**
     * Get the default encoder
     * 
     * @return Default encoder
     */
    @SuppressWarnings({"deprecation", "removal"})
    public EncoderBase getDefaultEncoder();

    /**
     * Get an encoder of a specific ID. This is useful if a system has more than one
     * sensor, or if it has more than one input, but only one sensor.
     * 
     * If the system only has one sensor, this should probably just return
     * getDefaultEncoder()
     * 
     * @param id Encoder ID
     * @param phase True if flipped phase
     * @return Encoder
     */
    @SuppressWarnings({"deprecation", "removal"})
    public EncoderBase getEncoder(int id, boolean phase);
}