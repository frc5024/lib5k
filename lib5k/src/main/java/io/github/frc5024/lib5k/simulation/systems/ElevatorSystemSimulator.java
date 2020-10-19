package io.github.frc5024.lib5k.simulation.systems;

import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.system.LinearSystem;
import edu.wpi.first.wpiutil.math.Matrix;
import edu.wpi.first.wpiutil.math.numbers.N1;
import edu.wpi.first.wpiutil.math.numbers.N2;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.control_loops.models.SystemCharacteristics;
import io.github.frc5024.lib5k.control_loops.statespace.StateSpaceSystem;

/**
 * ElevatorSystemSimulator is a utility for simulating the outputs of an
 * Elevator system based on its characteristics.
 */
public class ElevatorSystemSimulator extends ElevatorSim {

    /**
     * Creates a simulated elevator mechanism.
     *
     * @param plant            The linear system that represents the elevator.
     * @param characteristics  The characteristics of the system
     * @param drumRadiusMeters The radius of the drum that the elevator spool is
     *                         wrapped around.
     * @param minHeightMeters  The min allowable height of the elevator.
     * @param maxHeightMeters  The max allowable height of the elevator.
     */
    public ElevatorSystemSimulator(LinearSystem<N2, N1, N1> plant, SystemCharacteristics characteristics,
            double drumRadiusMeters, double minHeightMeters, double maxHeightMeters) {
        this(plant, characteristics.getMotorCharacteristics(), characteristics.getGearRatio(), drumRadiusMeters,
                minHeightMeters, maxHeightMeters);
    }

    /**
     * Creates a simulated elevator mechanism.
     *
     * @param plant              The linear system that represents the elevator.
     * @param characteristics    The characteristics of the system
     * @param drumRadiusMeters   The radius of the drum that the elevator spool is
     *                           wrapped around.
     * @param minHeightMeters    The min allowable height of the elevator.
     * @param maxHeightMeters    The max allowable height of the elevator.
     * @param measurementStdDevs The standard deviations of the measurements.
     */
    public ElevatorSystemSimulator(LinearSystem<N2, N1, N1> plant, SystemCharacteristics characteristics,
            double drumRadiusMeters, double minHeightMeters, double maxHeightMeters,
            Matrix<N1, N1> measurementStdDevsMatrix) {
        this(plant, characteristics.getMotorCharacteristics(), characteristics.getGearRatio(), drumRadiusMeters,
                minHeightMeters, maxHeightMeters, measurementStdDevsMatrix);
    }

    /**
     * Creates a simulated elevator mechanism.
     *
     * @param system           A state-space system representing the elevator
     * @param drumRadiusMeters The radius of the drum that the elevator spool is
     *                         wrapped around.
     * @param minHeightMeters  The min allowable height of the elevator.
     * @param maxHeightMeters  The max allowable height of the elevator.
     */
    public ElevatorSystemSimulator(StateSpaceSystem system, double drumRadiusMeters, double minHeightMeters,
            double maxHeightMeters) {
        this((LinearSystem<N2, N1, N1>) system.getPlant(), system.getMotorCharacteristics(), system.getGearRatio(),
                drumRadiusMeters, minHeightMeters, maxHeightMeters);
    }

    /**
     * Creates a simulated elevator mechanism.
     *
     * @param system             A state-space system representing the elevator
     * @param drumRadiusMeters   The radius of the drum that the elevator spool is
     *                           wrapped around.
     * @param minHeightMeters    The min allowable height of the elevator.
     * @param maxHeightMeters    The max allowable height of the elevator.
     * @param measurementStdDevs The standard deviations of the measurements.
     */
    public ElevatorSystemSimulator(StateSpaceSystem system, double drumRadiusMeters, double minHeightMeters,
            double maxHeightMeters, Matrix<N1, N1> measurementStdDevsMatrix) {
        this((LinearSystem<N2, N1, N1>) system.getPlant(), system.getMotorCharacteristics(), system.getGearRatio(),
                drumRadiusMeters, minHeightMeters, maxHeightMeters, measurementStdDevsMatrix);
    }

    /**
     * Creates a simulated elevator mechanism.
     *
     * @param plant            The linear system that represents the elevator.
     * @param gearbox          The type of and number of motors in the elevator
     *                         gearbox.
     * @param gearing          The gearing of the elevator (numbers greater than 1
     *                         represent reductions).
     * @param drumRadiusMeters The radius of the drum that the elevator spool is
     *                         wrapped around.
     * @param minHeightMeters  The min allowable height of the elevator.
     * @param maxHeightMeters  The max allowable height of the elevator.
     */
    public ElevatorSystemSimulator(LinearSystem<N2, N1, N1> plant, DCBrushedMotor gearbox, double gearing,
            double drumRadiusMeters, double minHeightMeters, double maxHeightMeters) {
        super(plant, gearbox, gearing, drumRadiusMeters, minHeightMeters, maxHeightMeters);
    }

    /**
     * Creates a simulated elevator mechanism.
     *
     * @param plant              The linear system that represents the elevator.
     * @param gearbox            The type of and number of motors in the elevator
     *                           gearbox.
     * @param gearing            The gearing of the elevator (numbers greater than 1
     *                           represent reductions).
     * @param drumRadiusMeters   The radius of the drum that the elevator spool is
     *                           wrapped around.
     * @param minHeightMeters    The min allowable height of the elevator.
     * @param maxHeightMeters    The max allowable height of the elevator.
     * @param measurementStdDevs The standard deviations of the measurements.
     */
    public ElevatorSystemSimulator(LinearSystem<N2, N1, N1> plant, DCBrushedMotor gearbox, double gearing,
            double drumRadiusMeters, double minHeightMeters, double maxHeightMeters,
            Matrix<N1, N1> measurementStdDevs) {
        super(plant, gearbox, gearing, drumRadiusMeters, minHeightMeters, maxHeightMeters, measurementStdDevs);
    }

    /**
     * Creates a simulated elevator mechanism.
     *
     * @param gearbox          The type of and number of motors in the elevator
     *                         gearbox.
     * @param gearing          The gearing of the elevator (numbers greater than 1
     *                         represent reductions).
     * @param carriageMassKg   The mass of the elevator carriage.
     * @param drumRadiusMeters The radius of the drum that the elevator spool is
     *                         wrapped around.
     * @param minHeightMeters  The min allowable height of the elevator.
     * @param maxHeightMeters  The max allowable height of the elevator.
     */
    public ElevatorSystemSimulator(DCBrushedMotor gearbox, double gearing, double carriageMassKg,
            double drumRadiusMeters, double minHeightMeters, double maxHeightMeters) {
        super(gearbox, gearing, carriageMassKg, drumRadiusMeters, minHeightMeters, maxHeightMeters);

    }

    /**
     * Creates a simulated elevator mechanism.
     *
     * @param gearbox            The type of and number of motors in the elevator
     *                           gearbox.
     * @param gearing            The gearing of the elevator (numbers greater than 1
     *                           represent reductions).
     * @param carriageMassKg     The mass of the elevator carriage.
     * @param drumRadiusMeters   The radius of the drum that the elevator spool is
     *                           wrapped around.
     * @param minHeightMeters    The min allowable height of the elevator.
     * @param maxHeightMeters    The max allowable height of the elevator.
     * @param measurementStdDevs The standard deviations of the measurements.
     */
    public ElevatorSystemSimulator(DCBrushedMotor gearbox, double gearing, double carriageMassKg,
            double drumRadiusMeters, double minHeightMeters, double maxHeightMeters,
            Matrix<N1, N1> measurementStdDevs) {
        super(gearbox, gearing, carriageMassKg, drumRadiusMeters, minHeightMeters, maxHeightMeters, measurementStdDevs);
    }

}