package io.github.frc5024.lib5k.autonomous;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * A wrapper for everything needed in an autonomous sequence
 */
public interface AutonomousSequence {

    /**
     * Get the name of the sequence
     * 
     * @return Sequence name
     */
    public String getName();

    /**
     * Get the command that runs the sequence
     * 
     * @return Sequence command
     */
    public CommandBase getCommand();

    /**
     * Get the robot's initial pose for this sequence
     * 
     * @return Initial pose
     */
    public Pose2d getStartingPose();

}