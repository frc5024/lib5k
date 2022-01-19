package io.github.frc5024.lib5k.control_loops.statespace;

import edu.wpi.first.math.controller.LinearQuadraticRegulator;
import edu.wpi.first.math.estimator.KalmanFilter;
import edu.wpi.first.math.system.LinearSystem;
import edu.wpi.first.math.Num;
import io.github.frc5024.lib5k.control_loops.models.SystemCharacteristics;

/**
 * Common interface for a state-space system
 */
public interface StateSpaceSystem extends SystemCharacteristics {

    /**
     * Get the system plant
     * 
     * @return Linear system plant
     */
    public LinearSystem<? extends Num, ? extends Num, ? extends Num> getPlant();

    /**
     * Get the system observer
     * 
     * @return System observer
     */
    public KalmanFilter<? extends Num, ? extends Num, ? extends Num> getObserver();

    /**
     * Get the system linear-quadratic regulator
     * 
     * @return System LQR
     */
    public LinearQuadraticRegulator<? extends Num, ? extends Num, ? extends Num> getLQR();

}