package io.github.frc5024.lib5k.hardware.generic.cameras;


import edu.wpi.first.wpilibj.Solenoid;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;


public class USBVisionCamera extends AutoCamera {

    public enum LEDMode {
        ON, OFF, BLINK;
    }

    private Solenoid m_relay;
    private LEDMode m_desiredMode;
    private final double blink_ms = 25;

    /**
     * Create a USBVisionCamera
     * 
	 * To run the camera, you must call the update method in a periodic loop.
	 * 
     * @param name     Camera name
     * @param usb_slot USB slot of camera
     * @param pcm_port PCM port for camera LED ring power source
     */
    public USBVisionCamera(String name, int usb_slot, int pcm_port) {
        this(name, usb_slot, 0, pcm_port);
    }

    /**
     * Create a USBVisionCamera
	 * 
	 * To run the camera, you must call the update method in a periodic loop.
     * 
     * @param name     Camera name
     * @param usb_slot USB slot of camera
     * @param pcm_id   PCM CAN ID
     * @param pcm_port PCM port for camera LED ring power source
     */
    public USBVisionCamera(String name, int usb_slot, int pcm_id, int pcm_port) {
        super(name, usb_slot);

        // Init the relay
        m_relay = new Solenoid(pcm_id, pcm_port);

        // Set the desired mode
        m_desiredMode = LEDMode.OFF;
    }

    /**
     * Enable or disable the LEDRing around the camera
     * 
     * @param enabled Should the LEDRing be enabled?
     */
    public void setLED(boolean enabled) {

        // Set the LEDMode
        setLED((enabled) ? LEDMode.ON : LEDMode.OFF);
    }

    /**
     * Set the LEDRing mode
     * 
     * @param mode LEDRing mode
     */
    public void setLED(LEDMode mode) {
        m_desiredMode = mode;
    }

    /**
     * Update the LEDs
	 * 
	 * Must be called in a periodic loop
     */
    public void update() {

        // Handle the LEDMode
        switch (m_desiredMode) {
            case ON:
                m_relay.set(true);
                break;
            case OFF:
                m_relay.set(false);
                break;
            case BLINK:
                boolean should_enable = FPGAClock.getMillisecondCycle(blink_ms);
                m_relay.set(should_enable);
                break;
            default:
                m_relay.set(false);

        }
    }
}