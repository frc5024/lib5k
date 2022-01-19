package io.github.frc5024.lib5k.examples.autonomous_framework.autonomous;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import io.github.frc5024.lib5k.autonomous.AutonomousSequence;
import io.github.frc5024.lib5k.autonomous.commands.LogCommand;

public class ActionB implements AutonomousSequence {

    @Override
    public String getName() {
        return "Action B";
    }

    @Override
    public CommandBase getCommand() {

        // We will just return a logging command that prints the action name, and our
        // pose.
        // Generally, we would return a SequentialCommand here, and call the DriveTrain
        // to reset out pose from inside it. (see 2020 code for an example)
        return new LogCommand(getName(),
                String.format("This action has a starting pose of: %s", getStartingPose().toString()));
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(1.0, 3.0, Rotation2d.fromDegrees(90.0));
    }

}