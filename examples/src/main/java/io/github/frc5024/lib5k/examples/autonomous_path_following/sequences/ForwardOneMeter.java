package io.github.frc5024.lib5k.examples.autonomous_path_following.sequences;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import io.github.frc5024.lib5k.autonomous.AutonomousSequence;
import io.github.frc5024.lib5k.examples.autonomous_path_following.subsystems.DriveTrain;
import io.github.frc5024.purepursuit.pathgen.Path;

public class ForwardOneMeter implements AutonomousSequence {

    @Override
    public String getName() {
        return "ForwardOneMeter";
    }

    @Override
    public CommandBase getCommand() {
        return DriveTrain.getInstance().createPathingCommand(new Path(getStartingPose().getTranslation(),
                getStartingPose().getTranslation().plus(new Translation2d(1.0, 0.0))), false, 0.2, 0.1);
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0));
    }

}