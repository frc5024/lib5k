package io.github.frc5024.lib5k.examples.autonomous_path_following.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import io.github.frc5024.lib5k.examples.autonomous_path_following.OI;
import io.github.frc5024.lib5k.examples.autonomous_path_following.subsystems.DriveTrain;
import io.github.frc5024.lib5k.utils.InputUtils.ScalingMode;

public class DriveCommand extends CommandBase {

    public DriveCommand() {
        addRequirements(DriveTrain.getInstance());
    }

    @Override
    public void initialize() {
        DriveTrain.getInstance().stop();
    }

    @Override
    public void end(boolean interrupted) {
        DriveTrain.getInstance().stop();
    }

    @Override
    public void execute() {
        DriveTrain.getInstance().drive(OI.getThrottle(), OI.getTurn(), ScalingMode.CUBIC);
    }
}