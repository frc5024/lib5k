package io.github.frc5024.lib5k.examples.drivebase_simulation.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import io.github.frc5024.lib5k.examples.drivebase_simulation.OI;
import io.github.frc5024.lib5k.examples.drivebase_simulation.subsystems.DriveTrain;
import io.github.frc5024.lib5k.utils.InputUtils;
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
        DriveTrain.getInstance().handleDriverInputs(InputUtils.scale(OI.getThrottle(), ScalingMode.CUBIC),
                InputUtils.scale(OI.getTurn(), ScalingMode.CUBIC));
    }
}