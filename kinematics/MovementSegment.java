package frc.lib5k.kinematics;

/**
 * Movement data
 */
public class MovementSegment {
    private double speed, turn = 0.0;
    private boolean complete = false;

    /**
     * Create a new MovementSegment
     * 
     * @param speed Segment speed
     * @param turn Segment rotation speed
     * @param complete Has the segment finished?
     */
    public MovementSegment(double speed, double turn, boolean complete) {
        this.speed = speed;
        this.turn = turn;
        this.complete = complete;
    }

    public double getSpeed() {
        return speed;
    }

    public double getTurn() {
        return turn;
    }

    public boolean isFinished() {
        return complete;
    }
}