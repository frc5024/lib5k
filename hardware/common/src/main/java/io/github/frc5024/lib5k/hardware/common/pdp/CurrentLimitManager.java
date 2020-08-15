package io.github.frc5024.lib5k.hardware.common.pdp;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * A class for running current limits
 */
public class CurrentLimitManager {

    // The Robot's PDP
    private PowerDistributionPanel pdp;
    

    // List of all active current limits
    private static ArrayList<CurrentLimit> currentLimits;
    
    /**
     * 
     * @param pdp the robot pdp
     */
    public CurrentLimitManager(PowerDistributionPanel pdp){
        this.pdp = pdp;
        this.pdp.clearStickyFaults();
        this.pdp.resetTotalEnergy();
    }

    /**
     * 
     * @param currentLimit adds a current limit to the active list
     */
    public static void addCurrentLimit(CurrentLimit currentLimit){
        currentLimits.add(currentLimit);
    }

    /**
     * 
     * @param currentLimit removes a current limit to the active list
     */
    public static void removeCurrentLimit(CurrentLimit currentLimit){
        currentLimits.remove(currentLimit);
    }

    // Runs the current limit for all active current limit
    public void performCurrentLimits(){
        for (CurrentLimit currentLimit : currentLimits) {
            currentLimit.run(pdp);
        }
    }

}