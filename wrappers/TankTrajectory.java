package frc.common.wrappers;

import jaci.pathfinder.Pathfinder; 
import jaci.pathfinder.PathfinderFRC;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import edu.wpi.first.wpilibj.Notifier;
import com.kauailabs.navx.frc.AHRS;

import frc.common.wrappers.GearBox;

/**
 * A Wrapper around a pair of EncoderFollowers 
 * 
 * This code is based off of this example:
 * http://wpilib.screenstepslive.com/s/currentCS/m/84338/l/1021631-integrating-path-following-into-a-robot-program
 * 
 * And uses Jaci's pathfinder library
 */
public class TankTrajectory{
    GearBox left_gearbox;
    GearBox right_gearbox;

    AHRS gyro;
    
    double max_velocity = 0.0;
    double period;
    boolean gyro_inverted;
    boolean is_finished = false;
    boolean path_inverted;

    EncoderFollower left_encoderfollower;
    EncoderFollower right_encoderollower;

    Notifier follower_notifier;

    /**
     * TankTraectory Constructor
     * 
     * @param left_gearbox The left GearBox of the robot's DrieTrain
     * @param right_gearbox The right GearBox of the robot's DrieTrain
     * @param gyro The NAVX gyro object
     * @param period The trajectory period time in seconds
     * @param gyro_inverted Should the gyro readings be inverted?
     * @param path_inverted Should the robot follow the path backwards? (Usefull for backing up in a path)
     */
    public TankTrajectory(GearBox left_gearbox,  GearBox right_gearbox, AHRS gyro, double period, boolean gyro_inverted, boolean path_inverted){
        this.left_gearbox = left_gearbox;
        this.right_gearbox = right_gearbox;

        this.gyro = gyro;

        this.period = period;

        this.gyro_inverted = gyro_inverted;
        this.path_inverted = path_inverted;
    }

    /**
     * Configure the TankTrajectory with information about the robot
     * 
     * @param left_ticks Current encoder reading of the left GearBox
     * @param right_ticks Current encoder reading of the right GearBox
     * @param wheel_diameter The wheel diameter of the drivetrain wheels
     * @param max_velocity The maximum velocity of the robot
     */
    public void configure(int left_ticks, int right_ticks, int ticks_per_rev, int wheel_diameter, double max_velocity){
        this.left_encoderfollower.configureEncoder(left_ticks, ticks_per_rev, wheel_diameter);
        this.right_encoderollower.configureEncoder(right_ticks, ticks_per_rev, wheel_diameter);

        this.max_velocity = max_velocity;

        this.follower_notifier = new Notifier(this::feed);
    }

    /**
     * Configures the TankTrajectory to use the example PIDA values. 
     * 
     * Not recommended.
     */
    public void defaultPID(){
        if (this.max_velocity != 0.0){
            this.left_encoderfollower.configurePIDVA(1.0, 0.0, 0.0, 1 / this.max_velocity, 0);
            this.right_encoderollower.configurePIDVA(1.0, 0.0, 0.0, 1 / this.max_velocity, 0);
        } else {
            System.out.println("TankTrajectory must be configured before using defauldPID");
        }
    }

    public void setPIDA(double p, double i, double d, double a){
        if (this.max_velocity != 0.0){
            this.left_encoderfollower.configurePIDVA(p, i, d, 1 / this.max_velocity, a);
            this.right_encoderollower.configurePIDVA(p, i, d, 1 / this.max_velocity, a);
        } else {
            System.out.println("TankTrajectory must be configured before using setPIDA");
        }
    }

    /**
     * Starts the notifier
     */
    public void start(){
        this.follower_notifier.startPeriodic(this.period);
        this.is_finished = false;

        /* Set inverse motion mode on gearboxes if needed */
        if (this.path_inverted) {
            this.left_gearbox.setInverseMotion(!this.left_gearbox.getInverseMotion());
            this.right_gearbox.setInverseMotion(!this.right_gearbox.getInverseMotion());
        }
    }

    /**
     * Stops the notifier and the DriveTrain motors
     */
    public void stop(){
        this.follower_notifier.stop();
        this.left_gearbox.set(0.0);
        this.right_gearbox.set(0.0);
        this.is_finished = true;

        /* Reset inverse motion mode on gearboxes if needed */
        if (this.path_inverted) {
            this.left_gearbox.setInverseMotion(!this.left_gearbox.getInverseMotion());
            this.right_gearbox.setInverseMotion(!this.right_gearbox.getInverseMotion());
        }
    }

    /**
     * Called by the notifier to control the motors based on sensor readings
     */
    private void feed() {
        if (this.left_encoderfollower.isFinished() || this.right_encoderollower.isFinished()) {
            stop();
        } else {
            int left_ticks = (this.path_inverted) ? this.right_gearbox.getTicks() : this.left_gearbox.getTicks();
            int right_ticks = (this.path_inverted) ? this.left_gearbox.getTicks() : this.right_gearbox.getTicks();
            
            double left_speed = this.left_encoderfollower.calculate(left_ticks);
            double right_speed = this.right_encoderollower.calculate(right_ticks);

            double heading;

            if (this.gyro_inverted) {
                heading = -this.gyro.getAngle();
            } else {
                heading = this.gyro.getAngle();
            }

            double desired_heading = Pathfinder.r2d(this.left_encoderfollower.getHeading());
            double heading_difference = Pathfinder.boundHalfDegrees(desired_heading - heading);

            double turn = 0.8 * (-1.0 / 80.0) * heading_difference;

            if (this.path_inverted) {
                this.left_gearbox.set((right_speed - turn)*-1);
                this.right_gearbox.set((left_speed + turn)*-1);
            } else {
                this.left_gearbox.set(left_speed + turn);
                this.right_gearbox.set(right_speed - turn);
            }
        }
    }
    
    /**
     * Has the path been finished?
     * 
     * This is useful for running motion profiles sequentially in a command group
     * 
     * @return Has the path finished?
     */
    public boolean isFinished() {
        return this.is_finished;
    };

}