/**
 * This file part of an effort by Evan Pratten <ewpratten@gmail.com> to build a 
 * system that allows new programmers make use of advanced control theory without 
 * needing to learn the underlying math.
 * 
 * The class(es) below rely on some work that was done by 
 * Matt Morley <matthew.morley.ca@gmail.com> and Tyler Veness <calcmogul@gmail.com> 
 * upstream at WPILib to allow teams to write statespace code in Java via JNI.
 * 
 * The underlying code is built on top of an advanced robotics software 
 * library from MIT called DRAKE (https://drake.mit.edu)
 * 
 * If you are interested in learning how this code works, I recommend you take a 
 * look at Tyler's paper titled: "Controls Engineering in the FIRST Robotics Competition".
 * The paper is available at: https://file.tavsys.net/control/controls-engineering-in-frc.pdf
 */

package io.github.frc5024.lib5k.control_loops.statespace.wrappers;

import edu.wpi.first.wpilibj.controller.LinearQuadraticRegulator;
import edu.wpi.first.wpilibj.estimator.KalmanFilter;
import edu.wpi.first.wpilibj.system.LinearSystem;
import edu.wpi.first.wpilibj.system.LinearSystemLoop;
import edu.wpi.first.wpilibj.system.plant.LinearSystemId;
import edu.wpi.first.wpiutil.math.Num;
import edu.wpi.first.wpiutil.math.numbers.N2;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.control_loops.statespace.StateSpaceSystem;
import io.github.frc5024.lib5k.control_loops.statespace.util.easylqr.DriveTrainMath;

public class TankDriveTrainController implements StateSpaceSystem {

    // Plant, observer, and LQR
    private LinearSystem<N2, N2, N2> plant;
    private KalmanFilter<N2, N2, N2> observer;
    private LinearQuadraticRegulator<N2, N2, N2> lqr;

    // State space loop
    private LinearSystemLoop<N2, N2, N2> loop;

    // Characteristics
    private DCBrushedMotor motor;
    private double gearing;

    // Timekeeping
    private double lastTimeSeconds = 0.0;

    public TankDriveTrainController(DCBrushedMotor motorType, double robotMassKG, double robotRadiusM,
            double trackWidthM, double gearRatio) {

        // Determine the robot's MOI
        double j = DriveTrainMath.calculateJ(robotMassKG, robotRadiusM);

        // Build the system plant
        plant = LinearSystemId.createDrivetrainVelocitySystem(motorType, robotMassKG, robotRadiusM, trackWidthM, j,
                gearRatio);

        // Build an observer
        

        // Set characteristics
        this.motor = motorType;
        this.gearing = gearRatio;

    }

    @Override
    public DCBrushedMotor getMotorCharacteristics() {
        return this.motor;
    }

    @Override
    public double getGearRatio() {
        return this.gearing;
    }

    @Override
    public LinearSystem<? extends Num, ? extends Num, ? extends Num> getPlant() {
        return this.plant;
    }

    @Override
    public KalmanFilter<? extends Num, ? extends Num, ? extends Num> getObserver() {
        return this.observer;
    }

    @Override
    public LinearQuadraticRegulator<? extends Num, ? extends Num, ? extends Num> getLQR() {
        return this.lqr;
    }

}