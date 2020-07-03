package ca.retrylife.frc.limelight;

/**
 * A raw contour as defined by:
 * http://docs.limelightvision.io/en/latest/networktables_api.html#advanced-usage-with-raw-contours
 */
public class Contour {
    private double x, y, area, skew;

    /**
     * Create an empty Contour
     */
    public Contour() {
        this(0, 0, 0, 0);
    }

    /**
     * Create a Contour
     * 
     * @param x    Raw X screenspace
     * @param y    Raw Y screenspace
     * @param area Screen area [0 to 100%]
     * @param skew Target skew [-90 to 0 degs]
     */
    public Contour(double x, double y, double area, double skew) {
        this.x = x;
        this.y = y;
        this.area = area;
        this.skew = skew;

    }

    /**
     * Get the raw X screenspace
     * 
     * @return X screenspace
     */
    public double getX() {
        return this.x;
    }

    /**
     * Get the raw Y screenspace
     * 
     * @return Y screenspace
     */
    public double getY() {
        return this.y;
    }

    /**
     * Get contour screen cover as a percentage
     * 
     * @return Contour area
     */
    public double getArea() {
        return this.area;
    }

    /**
     * Get target skew from -90 to 0 degrees
     * 
     * @return Target skew
     */
    public double getSkew() {
        return this.skew;
    }

    /**
     * Get the contour aspect ratio
     * 
     * @return Aspect ratio
     */
    public double getAspect() {
        return (this.x / this.y);
    }
}