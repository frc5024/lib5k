package io.github.frc5024.lib5k.examples.autonomous_path_following.sequences;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import io.github.frc5024.lib5k.autonomous.AutonomousSequence;
import io.github.frc5024.lib5k.bases.drivetrain.Chassis;
import io.github.frc5024.lib5k.examples.autonomous_path_following.subsystems.DriveTrain;
import io.github.frc5024.purepursuit.pathgen.Path;

public class ForwardOneMeter implements AutonomousSequence {

    @Override
    public String getName() {
        return "ForwardOneMeter";
    }

    @Override
    public CommandBase getCommand() {
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Pose set command
        output.addCommands(new InstantCommand(() -> {
            DriveTrain.getInstance().resetPose(getStartingPose());
        }));

        // Path follow command
        output.addCommands(DriveTrain.getInstance()
                .createPathingCommand(new Path(getStartingPose().getTranslation(),
                        getStartingPose().getTranslation().plus(new Translation2d(1.0, 0.0))), 0.1)
                .withMaxSpeed(0.8).withLookahead(0.2).setFrontSide(Chassis.Side.kFront));

        return output;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0));
    }

}