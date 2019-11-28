package frc.lib5k.components;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.lib5k.components.Limelight;

public class Limelight {
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    
    NetworkTableEntry tv = table.getEntry("tv"); 
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");
    NetworkTableEntry tl = table.getEntry("tl");
    NetworkTableEntry ts = table.getEntry("ts");
    NetworkTableEntry tshort = table.getEntry("tshort");
    NetworkTableEntry tlong = table.getEntry("tlong");
    NetworkTableEntry camtran = table.getEntry("camtran");
    NetworkTableEntry SLED = table.getEntry("ledMode");
    NetworkTableEntry cameraMode = table.getEntry("camMode");
    NetworkTableEntry setPipeId = table.getEntry("pipeline");
    NetworkTableEntry setStream = table.getEntry("stream");
    NetworkTableEntry enableSnapshot = table.getEntry("snapshot");
    NetworkTableEntry getPipe = table.getEntry("getpipe");

    public class LimelightTarget {
        /**
         * Horizontal Offset From Crosshair To Target
         * @return -27 degrees to 27 degrees
         */
        public double getX() {
            return tx.getDouble(0.0);
        }

        /**
         * Vertical Offset From Crosshair To Target
         * @return -20.5 degrees to 20.5 degrees 
         */
        public double getY() {
            return tx.getDouble(0.0);
        }

        /**
         * Target Area
         * @return 0% of image to 100% of image
         */
        public double getArea() {
            return ta.getDouble(0.0);
        }
        
        /**
         * Skew or rotation
         * @return -90 degrees to 0 degrees
         */
        public double getSkew() {
            return ts.getDouble(0.0);
        }
        
        /**
         * Sidelength of shortest side of the fitted bounding box 
         * @return pixels
         */
        public double getShortestSide() {
            return tshort.getDouble(0.0);
        }
        
        /**
         * Sidelength of longest side of the fitted bounding box
         * @return pixels
         */
        public double getLongestSide() {
            return tlong.getDouble(0.0);
        }
        
        /**
         * Results of a 3D position solution
         * @return 6 numbers: Translation (x,y,y) Rotation(pitch,yaw,roll)
         */
        public String getTranslationOrNull() {
            return camtran.getString("");
        }
    }
    
    /**
    * Whether the limelight has any valid targets
    * @return true or false
    */
    public boolean isTargetVisible() {
        return (int)tv.getNumber(0) == 1;
    }

    /**
    * True active pipeline index of the camera      
    * @return 0-9
    */
    public int getPipelineID() {
        return (int)getPipe.getNumber(0);
    }

    /**
    * TODO
    * @return null
    */
    public LimelightTarget getTarget() {
        return null;
    }

    /**
    * The pipeline’s latency contribution
    * @return ms
    */
    public double getLatency() {
        return tl.getDouble(0.0);
    }

    /**
    * Sets limelight’s LED state
    */
    public void setLED(int mode) {
        SLED.setValue(mode);
    }

    /**
    * Sets limelight’s operation mode
    */
    public void setCameraMode(int mode) {
        cameraMode.setValue(mode);
    }
    
    /**
    * Sets limelight’s current pipeline
    */
    public void setPipelineID(int id) {
        setPipeId.setValue(id);
    }

    /**
    * Sets limelight’s streaming mode
    */
    public void setStreamMode(int mode) {
        setStream.setValue(mode);
    }
    
    /**
    * Allows users to take snapshots during a match
    */
    public void enableSnapshots(boolean enabled){
        enableSnapshot.setBoolean(enabled);
    }
}