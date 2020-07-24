package ca.retrylife.frc.limelight;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import static ca.retrylife.frc.limelight.util.NetworkTableUtil.simpleListener;

import ca.retrylife.frc.limelight.controls.LEDMode;
import ca.retrylife.frc.limelight.controls.OperationMode;
import ca.retrylife.frc.limelight.controls.StreamMode;

public class Limelight {

    /* Camera data stream */
    private NetworkTable table;

    /* Camera data */
    private boolean tv;
    private double tx, ty, ta, ts, tl, tshort, tlong, thor, tvert;
    private double[] camTran, cornx, corny;
    private boolean portraitMode;

    public Limelight() {

        // Get the Limelight NetworkTable
        this.table = NetworkTableInstance.getDefault().getTable("limelight");

        // Register all listeners
        registerListeners();

    }

    /**
     * Registers all value listeners
     */
    private void registerListeners() {
        // TV
        simpleListener(this.table, "tv", (v) -> {
            this.tv = (v.getDouble() == 0.0) ? false : true;
        });

        // TX
        simpleListener(this.table, "tx", (v) -> {
            this.tx = v.getDouble();
        });

        // TY
        simpleListener(this.table, "ty", (v) -> {
            this.ty = v.getDouble();
        });

        // TA
        simpleListener(this.table, "ta", (v) -> {
            this.ta = v.getDouble();
        });

        // TL
        simpleListener(this.table, "tl", (v) -> {
            this.tl = v.getDouble();
        });

        // TSHORT
        simpleListener(this.table, "tshort", (v) -> {
            this.tshort = v.getDouble();
        });

        // TLONG
        simpleListener(this.table, "tlong", (v) -> {
            this.tlong = v.getDouble();
        });

        // THOR
        simpleListener(this.table, "thor", (v) -> {
            this.thor = v.getDouble();
        });

        // TVERT
        simpleListener(this.table, "tvert", (v) -> {
            this.tvert = v.getDouble();
        });

        // CAMTRAN
        simpleListener(this.table, "camtran", (v) -> {
            this.camTran = v.getDoubleArray();
        });

        // TCORNX
        simpleListener(this.table, "tcornx", (v) -> {
            this.cornx = v.getDoubleArray();
        });

        // TCORNY
        simpleListener(this.table, "tcorny", (v) -> {
            this.corny = v.getDoubleArray();
        });

    }

    /**
     * Enable or disable portraitMode
     */
    public void setPortrait(boolean portraitMode) {
        this.portraitMode = portraitMode;
    }

    /**
     * Check if the Limelight has a target in view
     * 
     * @return Can see target?
     */
    public boolean hasTarget() {
        return this.tv;
    }

    /**
     * Get the pipeline’s latency contribution (ms) Add at least 11ms for image
     * capture latency.
     * 
     * @return Pipeline latency
     */
    public double getLatency() {
        return this.tl;
    }

    /**
     * Get the detected vision target, or null if none found
     * 
     * @return Detected target
     */
    public Target getTarget() {
        // Return null if no target is found
        if (!hasTarget()) {
            return null;
        }
        
        if (portraitMode) {
            return new Target(ty, tx, ta, ts, tshort, tlong, thor, tvert);
        } else {
            return new Target(tx, ty, ta, ts, tshort, tlong, thor, tvert);
        }
    }

    /**
     * Get the camera translation, or null if not possible. NOTE: This feature must
     * be enabled in the Limelight settings
     * 
     * @return Camera translation
     */
    public CameraTranslation getCameraTranslation() {
        // Ensure a camtran exists
        if (!hasTarget() || this.camTran.length < 6) {
            return null;
        }

        return new CameraTranslation(this.camTran[0], this.camTran[1], this.camTran[2], this.camTran[3],
                this.camTran[4], this.camTran[5]);
    }

    /**
     * Get the true active pipeline ID (0 to 9)
     * 
     * @return Pipeline ID
     */
    public int getPipelineID() {
        return this.table.getEntry("getpipe").getNumber(0).intValue();
    }

    /**
     * Get an array of corner X coordinates. "Send Contours" must be enabled on the
     * Limelight
     * 
     * @return Corner X coords
     */
    public double[] getXCorners() {
        return this.cornx;
    }

    /**
     * Get an array of corner Y coordinates. "Send Contours" must be enabled on the
     * Limelight
     * 
     * @return Corner Y coords
     */
    public double[] getYCorners() {
        return this.corny;
    }

    /**
     * Get raw contour data
     * 
     * @return Array of raw contours
     */
    public Contour[] getRawContours() {
        Contour[] output = new Contour[3];

        // Get each contour
        output[0] = new Contour(this.table.getEntry("tx0").getDouble(0.0), this.table.getEntry("ty0").getDouble(0.0),
                this.table.getEntry("ta0").getDouble(0.0), this.table.getEntry("ts0").getDouble(0.0));
        output[1] = new Contour(this.table.getEntry("tx1").getDouble(0.0), this.table.getEntry("ty1").getDouble(0.0),
                this.table.getEntry("ta1").getDouble(0.0), this.table.getEntry("ts1").getDouble(0.0));
        output[2] = new Contour(this.table.getEntry("tx2").getDouble(0.0), this.table.getEntry("ty2").getDouble(0.0),
                this.table.getEntry("ta2").getDouble(0.0), this.table.getEntry("ts2").getDouble(0.0));

        return output;
    }

    /**
     * Get raw crosshair data
     * 
     * @return Array of raw crosshairs
     */
    public Crosshair[] getRawCrosshairs() {
        Crosshair[] output = new Crosshair[2];

        // Get each crosshair
        output[0] = new Crosshair(this.table.getEntry("cx0").getDouble(0.0), this.table.getEntry("cy0").getDouble(0.0));
        output[1] = new Crosshair(this.table.getEntry("cx1").getDouble(0.0), this.table.getEntry("cy1").getDouble(0.0));

        return output;
    }

    /**
     * Set the camera LED mode
     * 
     * @param mode Mode
     */
    public void setLEDMode(LEDMode mode) {
        this.table.getEntry("ledMode").setNumber(mode.getValue());
    }

    /**
     * Set the camera operation mode
     * 
     * @param mode Mode
     */
    public void setOperationMode(OperationMode mode) {
        this.table.getEntry("camMode").setNumber(mode.getValue());
    }

    /**
     * Set the vision pipeline ID
     * 
     * @param id Pipeline ID
     */
    public void setPipelineID(int id) {
        this.table.getEntry("pipeline").setNumber(id);
    }

    /**
     * Set the camera stream mode
     * 
     * @param mode Stream mode
     */
    public void setStreamMode(StreamMode mode) {
        this.table.getEntry("stream").setNumber(mode.getValue());
    }

    /**
     * Enable camera snapshots twice per second
     * 
     * @param enable Enable snapshots?
     */
    public void enableSnapshots(boolean enable) {
        this.table.getEntry("snapshot").setBoolean(enable);
    }

}