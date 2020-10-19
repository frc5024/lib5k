package io.github.frc5024.lib5k.simulation.systems;

import edu.wpi.first.wpilibj.simulation.FlywheelSim;
import edu.wpi.first.wpilibj.system.LinearSystem;
import edu.wpi.first.wpiutil.math.Matrix;
import edu.wpi.first.wpiutil.math.numbers.N1;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.control_loops.models.SystemCharacteristics;
import io.github.frc5024.lib5k.control_loops.statespace.StateSpaceSystem;

public class FlywheelSystemSimulator extends FlywheelSim {

    /**
     * Creates a simulated flywheel mechanism.
     * 
     * @param plant           The linear system that represents the flywheel
     * @param characteristics The characteristics of the system
     */
    public FlywheelSystemSimulator(LinearSystem<N1, N1, N1> plant, SystemCharacteristics characteristics) {
        this(plant, characteristics.getMotorCharacteristics(), characteristics.getGearRatio());
    }

    /**
     * Creates a simulated flywheel mechanism.
     * 
     * @param system A StateSpaceSystem representing the flywheel
     */
    public FlywheelSystemSimulator(StateSpaceSystem system) {
        this((LinearSystem<N1, N1, N1>) system.getPlant(), system.getMotorCharacteristics(), system.getGearRatio());
    }

    /**
     * Creates a simulated flywheel mechanism.
     *
     * @param plant   The linear system that represents the flywheel.
     * @param gearbox The type of and number of motors in the flywheel gearbox.
     * @param gearing The gearing of the flywheel (numbers greater than 1 represent
     *                reductions).
     */
    public FlywheelSystemSimulator(LinearSystem<N1, N1, N1> plant, DCBrushedMotor gearbox, double gearing) {
        super(plant, gearbox.toDCMotor(), gearing);
    }

    /**
     * Creates a simulated flywheel mechanism.
     *
     * @param plant              The linear system that represents the flywheel.
     * @param gearbox            The type of and number of motors in the flywheel
     *                           gearbox.
     * @param gearing            The gearing of the flywheel (numbers greater than 1
     *                           represent reductions).
     * @param measurementStdDevs The standard deviations of the measurements.
     */
    public FlywheelSystemSimulator(LinearSystem<N1, N1, N1> plant, DCBrushedMotor gearbox, double gearing,
            Matrix<N1, N1> measurementStdDevs) {
        super(plant, gearbox.toDCMotor(), gearing, measurementStdDevs);
    }

    /**
     * Creates a simulated flywheel mechanism.
     *
     * @param gearbox          The type of and number of motors in the flywheel
     *                         gearbox.
     * @param gearing          The gearing of the flywheel (numbers greater than 1
     *                         represent reductions).
     * @param jKgMetersSquared The moment of inertia of the flywheel. If this is
     *                         unknown, use the
     *                         {@link #FlywheelSystemSimulator(LinearSystem, DCBrushedMotor, double, Matrix)}
     *                         constructor.
     */
    public FlywheelSystemSimulator(DCBrushedMotor gearbox, double gearing, double jKgMetersSquared) {
        super(gearbox.toDCMotor(), gearing, jKgMetersSquared);
    }

    /**
     * Creates a simulated flywheel mechanism.
     *
     * @param gearbox            The type of and number of motors in the flywheel
     *                           gearbox.
     * @param gearing            The gearing of the flywheel (numbers greater than 1
     *                           represent reductions).
     * @param jKgMetersSquared   The moment of inertia of the flywheel. If this is
     *                           unknown, use the
     *                           {@link #FlywheelSystemSimulator(LinearSystem, DCBrushedMotor, double, Matrix)}
     *                           constructor.
     * @param measurementStdDevs The standard deviations of the measurements.
     */
    public FlywheelSystemSimulator(DCBrushedMotor gearbox, double gearing, double jKgMetersSquared,
            Matrix<N1, N1> measurementStdDevs) {
        super(gearbox.toDCMotor(), gearing, jKgMetersSquared, measurementStdDevs);
    }

}