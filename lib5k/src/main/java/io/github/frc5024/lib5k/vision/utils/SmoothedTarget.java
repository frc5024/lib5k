package io.github.frc5024.lib5k.vision.utils;

import java.util.LinkedList;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import io.github.frc5024.lib5k.vision.types.HyperbolicAxisAlignedBoundingBox;

/**
 * A class for getting more accurate limelight data Averages a series of targets
 * to get an average.
 */
public class SmoothedTarget {

	// List of targets
	private LinkedList<HyperbolicAxisAlignedBoundingBox> targets;

	/**
	 * Creates a new smooth target
	 */
	public SmoothedTarget() {
		targets = new LinkedList<>();
	}

	/**
	 * Creates a new smoothed target
	 * @param newTargets targets to add to the target list
	 */
	public SmoothedTarget(HyperbolicAxisAlignedBoundingBox... newTargets) {
		targets = new LinkedList<>();

		for (HyperbolicAxisAlignedBoundingBox target : newTargets) {
			targets.addLast(target);
		}

	}

	/**
	 * Adds a single new target to the target list
	 * @param newTarget the new target to add
	 */
	public void addTarget(HyperbolicAxisAlignedBoundingBox newTarget) {
		targets.addLast(newTarget);
	}

	/**
	 * Removes all targets from the target list
	 */
	public void clearTargets() {
		targets.clear();
	}

	/**
	 * Give a smoothed target by giving the mean target information
	 * @return a smoothed target
	 */
	public HyperbolicAxisAlignedBoundingBox getSmoothedTarget() {
		// Creates a empty target
		HyperbolicAxisAlignedBoundingBox baseTarget = new HyperbolicAxisAlignedBoundingBox(new Translation2d(),
				new Translation2d(), new Rotation2d(), new Rotation2d());

		// Adds each target to the baseTarget
		for (HyperbolicAxisAlignedBoundingBox target : targets) {
			baseTarget = baseTarget.add(target);
		}

		int length = targets.size();

		// Smoothes all components
		Rotation2d smoothedXRotation = new Rotation2d(baseTarget.getXRotation().getRadians() / length);
		Rotation2d smoothedYRotation = new Rotation2d(baseTarget.getYRotation().getRadians() / length);
		Rotation2d smoothedLeftBoundRotation = new Rotation2d(baseTarget.getLeftBoundRotation().getRadians() / length);
		Rotation2d smoothedRightBoundRotation = new Rotation2d(
				baseTarget.getRightBoundRotation().getRadians() / length);
		Rotation2d smoothedTopBoundRotation = new Rotation2d(baseTarget.getTopBoundRotation().getRadians() / length);
		Rotation2d smoothedBottomBoundRotation = new Rotation2d(
				baseTarget.getBottomBoundRotation().getRadians() / length);

		// Returns smoothed HyperbolicAxisAlignedBoundingBox
		return new HyperbolicAxisAlignedBoundingBox(baseTarget.getTopLeftCorner().div(length),
				baseTarget.getBottomLeftCorner().div(length), smoothedXRotation, smoothedYRotation,
				smoothedLeftBoundRotation, smoothedRightBoundRotation, smoothedTopBoundRotation,
				smoothedBottomBoundRotation);
		
	}

}