package ca.retrylife.frc.limelight;

/**
 * Limelight calibrated crosshair for use with raw contours
 */
public class Crosshair {

    private double x, y;

    /**
     * Create a Crosshair
     * 
     * @param x X coord in normalized screen space
     * @param y Y coord in normalized screen space
     */
    public Crosshair(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get normalized X coordinate
     * 
     * @return X coordinate
     */
    public double getX() {
        return this.x;

    }

    /**
     * Get normalized Y coordinate
     * 
     * @return Y coordinate
     */
    public double getY() {
        return this.y;
    }
}