package io.github.frc5024.lib5k.hardware.generic.motors.motorcollection;

import java.util.function.Consumer;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import io.github.frc5024.lib5k.hardware.common.motors.interfaces.IMotorCollection;
import io.github.frc5024.lib5k.hardware.common.motors.interfaces.IRampRateController;
import io.github.frc5024.lib5k.hardware.common.motors.interfaces.IVoltageOutputController;
import io.github.frc5024.lib5k.control_loops.TimedSlewLimiter;
import io.github.frc5024.lib5k.logging.Loggable;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.utils.ObjectCounter;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.lib5k.telemetry.ComponentTelemetry;;

/**
 * Collection of multiple Spark controllers that wraps a SpeedControllerGroup
 */
public class SparkCollection extends SpeedControllerGroup
        implements IMotorCollection, IVoltageOutputController, IRampRateController, Loggable {
    RobotLogger logger = RobotLogger.getInstance();

    /* Motor controllers */
    @SuppressWarnings("unused")
    private Spark master;
    private Spark[] slaves;

    /* Telemetry */
    private double output;
    private boolean inverted;
    private String name;
    private NetworkTable telemetryTable;

    /* Locals */
    private TimedSlewLimiter slewLimiter;

    public SparkCollection(Spark master, Spark... slaves) {
        super(master, slaves);

        // Set locals
        this.master = master;
        this.slaves = slaves;

        // Configure a slew limiter with no slew
        slewLimiter = new TimedSlewLimiter(0.0);
        slewLimiter.setEnabled(false);

        // Determine name
        name = String.format("SparkCollection (Master ID %d)", master.getChannel());

        // Get the telemetry NetworkTable
        telemetryTable = ComponentTelemetry.getInstance().getTableForComponent(name);

    }

    @Override
    public void set(double speed) {
        output = speed;

        // Process, then set the speed
        super.set(slewLimiter.feed(speed));
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

    @Override
    public void setVoltage(double volts) {

        // Determine Robot bus voltage
        double busVoltage = RR_HAL.getSimSafeVoltage();

        // Just stop the motor if the bus is at 0V
        // Many things would go wrong otherwise (do you really want a div-by-zero error
        // on your drivetrain?)
        if (busVoltage == 0.0) {
            set(0.0);
            return;
        }

        // Convert voltage to a motor speed
        double calculated_speed = volts / busVoltage;

        // Set the output to the calculated speed
        set(calculated_speed);

    }

    @Override
    public double getEstimatedVoltage() {

        // Determine Robot bus voltage
        double busVoltage = RR_HAL.getSimSafeVoltage();

        // Convert percent output to voltage
        double voltage_estimate = get() * busVoltage;

        return voltage_estimate;
    }

    @Override
    public void setRampRate(double secondsToFull) {
        slewLimiter.setRate(secondsToFull);

    }

    @Override
    public double getRampRate() {
        return slewLimiter.getRate();
    }

    @Override
    public void enableRampRateLimiting(boolean enabled) {
        slewLimiter.setEnabled(enabled);

    }

    /**
     * For-Each over each slave controller
     * 
     * @param consumer Method to run
     */
    public void forEachSlave(Consumer<Spark> consumer) {
        for (Spark spark : slaves) {
            consumer.accept(spark);
        }
    }

    @Override
    public void logStatus() {
        // Build info string
        String data = String.format("Output: %.2f, Inverted %b, Ramp rate: %.2f", output, inverted, getRampRate());

        // Log string
        logger.log(name, data, Level.kInfo);

    }

    @Override
    public void updateTelemetry() {
        telemetryTable.getEntry("Output").setNumber(output);
        telemetryTable.getEntry("Is Inverted").setBoolean(inverted);
        telemetryTable.getEntry("Ramp Rate").setNumber(getRampRate());

    }

}