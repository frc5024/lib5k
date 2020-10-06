package io.github.frc5024.lib5k.hardware.ni.roborio;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.util.WPILibVersion;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;

public class MOTD {
    private static RobotLogger logger = RobotLogger.getInstance();

    /**
     * Print an MOTD with some version info
     */
    public static void printFullMOTD() {
        String message = new StringBuilder().append("--- MOTD ---\n").append("Versions:\n")
                .append(String.format("\tWPILib: %s%n", WPILibVersion.Version))
                .append(String.format("\tLib5K: %s%n", RR_HAL.getLibraryVersion())).append(String
                        .format("\tFPGA: %d.%d%n", RobotController.getFPGAVersion(), RobotController.getFPGARevision()))
                .toString();

        logger.log(message, Level.kRobot);
    }
}