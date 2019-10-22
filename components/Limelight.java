package frc.lib5k.components;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.lib5k.spatial.VisionTarget;

public class Limelight {

    public enum LEDMode {
        DEFAULT(0), OFF(1), BLINK(2), ON(3);

        private int m_mode;

        LEDMode(int mode) {
            m_mode = mode;
        }

        public int get() {
            return m_mode;
        }
    }

    private NetworkTable m_limelightData;
    private boolean m_isPortrait;

    /**
     * Create a Limelight object using the default NetworkTables interface
     * 
     * @param is_portrait Is the camera mounted in portrait mode
     */
    public Limelight(boolean is_portrait) {
        this("limelight", is_portrait);
    }

    /**
     * Create a Limelight object using a custom NetworkTable table
     * 
     * @param name        Table name
     * @param is_portrait Is the camera mounted in portrait mode
     */
    public Limelight(String name, boolean is_portrait) {
        this.m_limelightData = NetworkTableInstance.getDefault().getTable(name);
        this.m_isPortrait = is_portrait;
    }

    /**
     * Set the Limelight pipeline id
     * 
     * @param pipeline_id Limelight pipeline
     */
    public void setPipeline(int pipeline_id) {
        m_limelightData.getEntry("pipeline").setNumber(pipeline_id);
    }

    /**
     * Set the Limelight camera feed. On most setups, this should always be 0
     * 
     * @param camera_id Camera ID
     */
    public void setCamera(int camera_id) {
        m_limelightData.getEntry("camera").setNumber(camera_id);
    }

    /**
     * Set the Limelight's LED mode.
     * 
     * @param mode LEDMode to send to Limelight
     */
    public void setLED(LEDMode mode) {
        m_limelightData.getEntry("ledMode").setNumber(mode.get());
    }

    /**
     * Check if the Limelight can see a target
     * 
     * @return Is a target visible?
     */
    public boolean hasTarget() {
        return m_limelightData.getEntry("tv").getDouble(0) == 1;
    }

    /**
     * Get a VisionTarget for the Limelight target
     * 
     * @return Limelight VisionTarget
     */
    public VisionTarget getTarget() {

        double x;
        double y;

        // Handle possible mounting positions
        if (m_isPortrait) {
            x = m_limelightData.getEntry("ty").getDouble(0);
            y = m_limelightData.getEntry("tx").getDouble(0);
        } else {
            x = m_limelightData.getEntry("tx").getDouble(0);
            y = m_limelightData.getEntry("ty").getDouble(0);
        }

        // Get the target area
        double area = m_limelightData.getEntry("ta").getDouble(0);

        // Create a target object
        VisionTarget target = new VisionTarget(x, y, area);

        // Set if the target exists
        target.setExists(hasTarget());

        return target;
    }
}