package frc.lib5k.components;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.lib5k.components.Limelight;

public class Limelight {
    
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    
    
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");
    NetworkTableEntry SLED = table.getEntry("ledMode");

    //read values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);

    //post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);

    public class LimelightTarget {
        public double getX() {
            return tx.getDouble(0.0);
        }

        public double getY() {
            return tx.getDouble(0.0);
        }

        public double getArea() {
            
        }
        
        public double getSkew() {
            
        }
        
        public double getShortestSide() {

        }
        
        public double getLongestSide() {

        }
        
        public int[] getTranslation() {

        }
    }
 
    public boolean isTargetVisible() {
        return table.getEntry("tv") == 1;
    }

    public int getPipelineID() {
        return table.getEntry("getpipe");
    }

    public double getLatency() {
        return table.getEntry("tl");
    }

    public void setLED(int mode) {
        SLED.setValue(mode);
    }

    public void setCameraMode(int mode) {
        
    }
    
    public void setPipelineID(int id) {

    }

    public void setStreamMode(int mode) {
        
    }
    
    public void enableSnapshots(boolean enabled){
        
    }
}