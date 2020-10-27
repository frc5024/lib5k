package io.github.frc5024.lib5k.examples.vision_align.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import io.github.frc5024.lib5k.examples.vision_align.OI;
import io.github.frc5024.lib5k.examples.vision_align.RobotConfig;
import io.github.frc5024.lib5k.examples.vision_align.subsystems.DriveTrain;
import io.github.frc5024.lib5k.examples.vision_align.subsystems.Vision;
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

        // Handle vision align vs. normal operation
        if (OI.getVisionAlign()) {

            // Enable the vision system
            Vision.getInstance().setEnabled(true);

            // CHeck if the robot sees a vision target
            if (Vision.getInstance().hasTarget()) {

                // Get angles to vision target
                double xAngle = Vision.getInstance().getXAngle().getDegrees();
                double yAngle = Vision.getInstance().getYAngle().getDegrees();

                // Multiply angles by P gains
                xAngle *= RobotConfig.VISION_ROTATION_GAIN;
                yAngle *= RobotConfig.VISION_DISTANCE_GAIN;

                // Write outputs
                DriveTrain.getInstance().handleDriverInputs(yAngle, xAngle);

            }

        } else {

            // Disable the vision system
            Vision.getInstance().setEnabled(true);

            // Handle drive
            DriveTrain.getInstance().handleDriverInputs(InputUtils.scale(OI.getThrottle(), ScalingMode.CUBIC),
                    InputUtils.scale(OI.getTurn(), ScalingMode.CUBIC));
        }
    }
}