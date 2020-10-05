package frc.common.control;

import frc.common.utils.LatchedBoolean;

public class VirtualGearShifter{
    boolean isHighGear = false;
    double lowGearLimit, coast_timeout;
    double coast_timer = 0.0;
    
    LatchedBoolean reset_latch;

    public VirtualGearShifter(double lowGearLimit, double acceleration_step){
        this.lowGearLimit = lowGearLimit;
        this.coast_timeout = 1 / acceleration_step;
        this.reset_latch = new LatchedBoolean();
    }

    public double feed(double value) {
        if (shouldCoast()) {
            this.coast_timer -= 1.0;
        }
        
        if (this.isHighGear){
            return value;
        } else {
            return value * this.lowGearLimit;
        }
    }

    public void shift(boolean doShift) {
        this.isHighGear = doShift;
        this.coast_timer = this.coast_timeout;
    }
    
    public boolean shouldCoast() {
        return this.coast_timer > 0.0;
    }


    public boolean shouldReset() {
        return this.reset_latch.feed(shouldCoast());
    }

}