package frc.lib5k.components.motors;

import java.util.function.Consumer;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import frc.lib5k.components.motors.interfaces.IMotorCollection;
import frc.lib5k.interfaces.Loggable;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;
import frc.lib5k.utils.telemetry.ComponentTelemetry;

/**
 * Collection of multiple motor controllers of mixed types that wraps a
 * SpeedControllerGroup
 */
public class MixedMotorCollection extends SpeedControllerGroup implements IMotorCollection, Loggable {
    RobotLogger logger = RobotLogger.getInstance();

    /* Motor controllers */
    private SpeedController master;
    private SpeedController[] slaves;

    /* Telemetry */
    private double output;
    private boolean inverted;
    private String name;
    private NetworkTable telemetryTable;

    /* ID tracking */
    private static int count = 0;
    private int id;

    public MixedMotorCollection(SpeedController master, SpeedController... slaves) {
        super(master, slaves);

        // Set locals
        this.master = master;
        this.slaves = slaves;

        // Determine ID
        id = count++;

        // Determine name
        name = String.format("MixedMotorCollection (Master ID %d)", id);

        // Get the telemetry NetworkTable
        telemetryTable = ComponentTelemetry.getInstance().getTableForComponent(name);

    }

    @Override
    public void set(double speed) {
        output = speed;

        super.set(speed);
    }

    @Override
    public void setBuffer(double speed) {
        if (speed != output) {
            set(speed);
        }

    }

    @Override
    public void setInverted(boolean isInverted) {
        inverted = isInverted;
        super.setInverted(isInverted);

    }

    @Override
    public void pidWrite(double output) {
        this.output = output;
        super.pidWrite(output);

    }

    /**
     * For-Each over each slave controller
     * 
     * @param consumer Method to run
     */
    public void forEachSlave(Consumer<SpeedController> consumer) {
        for (SpeedController slave : slaves) {
            consumer.accept(slave);
        }
    }

    @Override
    public void logStatus() {
        // Build info string
        String data = String.format("Output: %.2f, Inverted %b", output, inverted);

        // Log string
        logger.log(name, data, Level.kInfo);

    }

    @Override
    public void updateTelemetry() {
        telemetryTable.getEntry("Output").setNumber(output);
        telemetryTable.getEntry("Is Inverted").setBoolean(inverted);

    }

}