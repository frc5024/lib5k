package frc.common.wrappers;

import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

public class EncoderPair {
    public EncoderFollower left;
    public EncoderFollower right;

    public EncoderPair(TankModifier modifier) {
        this.left = new EncoderFollower(modifier.getLeftTrajectory());
        this.right = new EncoderFollower(modifier.getRightTrajectory());
    }

    public void setPIDVA(double p, double i, double d, double v, double a){
        left.configurePIDVA(p, i, d, v, a);
        right.configurePIDVA(p, i, d, v, a);
    }

    public boolean isFinished() {
        return left.isFinished() || right.isFinished();
    }

    public void reset(){
        left.reset();
        right.reset();
    }
}