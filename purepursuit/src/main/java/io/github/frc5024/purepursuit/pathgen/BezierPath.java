package io.github.frc5024.purepursuit.pathgen;

import java.util.ArrayList;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.geometry.Translation2d;

/**
 * This class is used to generate points along a bezier curve currently only quadratic bezier curves can be made.
 * I will update it as I learn more
 */
public class BezierPath extends Path{
	
	// What percentage of the curve should each point be at.
	private double spacing;

	/**
	 * 
	 * @param wayPoints The 3 way points of a quadratic bezier curve
	 */
	public BezierPath(Translation2d[] wayPoints){
		this(wayPoints, new double[]{1, 1, 1}, .1);
	}
	
	/**
	 * 
	 * @param wayPoints The 3 waypoints of a quadratic bezier curve
	 * @param weights The weights for each point point 0 and 2 should stay as close to 1 as possible
	 * @param spacing How seperated each point will be. 1 / spacing = points
	 */
	public BezierPath(Translation2d[] wayPoints, double[] weights, double spacing){
		this.waypoints = wayPoints;
		this.points = new ArrayList<>();

		this.spacing = MathUtils.clamp(spacing, .001, 1);


		for(int i=0; i < (1 / spacing) + 1; i++){
			double t = spacing * i;

			this.points.add(generateQuadraticPose(wayPoints, weights, t));
		}

	}


	/**
	 * This doesn't allow for more point. This is due to my lack of understanding of bezier curves
	 * I intend on updating it as I learn more.
	 * 
	 * @param points The three points that make up the hull
	 * @param weights The weight each point has
	 * @param t Where to draw the point
	 * 
	 * @return a Translation2d of where that point is
	 */
	private static Translation2d generateQuadraticPose(Translation2d[] points, double[] weights, double t) {
		// Clamps t to make sure its between 0 and 1 this is for added saftey
		t = MathUtils.clamp(t, 0, 1);

		// Calculates the x value
		double x = (points[1].getX() * weights[1])
				+ Math.pow(1 - t, 2) * ((weights[0] * points[0].getX()) - (weights[1] * points[1].getX()))
				+ Math.pow(t, 2) * ((weights[2] * points[2].getX()) - (weights[1] * points[1].getX()));

		// Calculates the y value
		double y = (points[1].getY() * weights[1])
				+ Math.pow(1 - t, 2) * ((weights[0] * points[0].getY()) - (weights[1] * points[1].getY()))
				+ Math.pow(t, 2) * ((weights[2] * points[2].getY()) - (weights[1] * points[1].getY()));

		// This should be rounded!!!!
		return new Translation2d(x, y);
	}

	





}
