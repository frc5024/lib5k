package io.github.frc5024.purepursuit.pathgen;

import java.util.ArrayList;
import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.util.Units;

/**
 * This class is used to generate points along a bezier curves
 */
public class BezierPath extends Path {

	// Maximum space allowed between points
	private double maxSeperation;

	private static final double DEFAULT_POINT_SPACING = Units.inchesToMeters(4);

	// The lookup table
	private static ArrayList<int[]> binomialLookUpTable;

	/**
	 * 
	 * @param wayPoints The waypoints of the bezier curve
	 */
	public BezierPath(Translation2d[] wayPoints) {
		this(wayPoints, new double[0]);
	}

	/**
	 * 
	 * @param wayPoints The waypoints of the bezier curve
	 * @param weights   The weights for each point point 0 and 2 should stay as
	 *                  close to 1 as possible
	 */
	public BezierPath(Translation2d[] wayPoints, double[] weights) {
		this(wayPoints, weights, DEFAULT_POINT_SPACING);
	}

	/**
	 * 
	 * @param wayPoints The waypoints of the bezier curve
	 * @param weights   The weights for each point point 0 and 2 should stay as
	 *                  close to 1 as possible
	 * @param spacing   the maximum amount of spacing allowed
	 */
	public BezierPath(Translation2d[] wayPoints, double[] weights, double spacing) {
		this.waypoints = wayPoints;
		this.points = new ArrayList<>();
		this.maxSeperation = spacing;

		if (binomialLookUpTable == null) {
			initilizeLookupTable();
		}

		// This makes sure the proper length of weights is provided
		double[] newWeights = new double[wayPoints.length];

		for (int i = 0; i < wayPoints.length; i++) {
			if (i < weights.length) {
				newWeights[i] = weights[i];
			} else {
				newWeights[i] = 1;
			}
		}

		// Calculates the points 
		calculatePoints(wayPoints, newWeights);

	}

	/**
	 * 
	 * @param n row of lut
	 * @param k coloumn of lut
	 * @return a binomial from the lut
	 */
	private double binomial(int k, int n) {
		// Adds new entries to LUT table if they are missing.
		while (n >= binomialLookUpTable.size()) {
			// continues pascal triangle
			int lutSize = binomialLookUpTable.size();

			int[] newRow = new int[lutSize + 1];
			newRow[0] = 1;

			for (int i = 1, prev = lutSize - 1; i < lutSize; i++) {
				newRow[i] = binomialLookUpTable.get(prev)[i - 1] + binomialLookUpTable.get(prev)[i];
			}

			newRow[lutSize] = 1;

			binomialLookUpTable.add(lutSize, newRow);
		}

		// Looks up value
		return binomialLookUpTable.get(n)[k];
	}

	/**
	 * Calculates the bezier values
	 * 
	 * @param t      t value for equation between 0, 1
	 * @param i      the current point
	 * @param n      the number of points starting at 0
	 * @param weight the weight to mulitply the value by
	 * @return the bezier value to add to point x,y
	 */
	private double calculateBezier(double t, int i, int n, double weight) {

		// Formula to calculate value
		return weight * binomial(i, n) * Math.pow(t, i) * Math.pow((1 - t), (n - i));
	}

	/**
	 * calculates points along a bezier curve
	 * 
	 * @param wayPoints the waypoints to form the hull
	 * @param weights   values to increase each points weight by
	 */
	private void calculatePoints(Translation2d[] wayPoints, double[] weights) {
		// The number of waypoints starting from 1
		int numberOfWayPoints = wayPoints.length - 1;

		// Add first waypoints
		points.add(wayPoints[0]);

		// Increases the t at the rate of 1 %
		for (int i = 1; i < 101; i++) {

			// Resets x, y variables
			double x = 0;
			double y = 0;

			// Calculates the T value
			double t = .01 * i;

			// calculates x, y value for each point
			for (int j = 0; j < numberOfWayPoints + 1; j++) {
				double bezierNumber = calculateBezier(t, j, numberOfWayPoints, weights[j]);
				x += wayPoints[j].getX() * bezierNumber;
				y += wayPoints[j].getY() * bezierNumber;
			}

			populatePoints(x, y);

			// Adds point to arraylist
			points.add(new Translation2d(x, y));
		}
	}

	/**
	 * Populates points between x,y and the most recent point
	 * 
	 * @param x the x point
	 * @param y the y point
	 */
	private void populatePoints(double x, double y) {
		// the deltas between the last point and the new one
		double xDelta = x - points.get(points.size() - 1).getX();
		double yDelta = y - points.get(points.size() - 1).getY();
		double pointsDelta = Math.hypot(xDelta, yDelta);

		// If the delta is larger than max allowed add more spaces
		if (pointsDelta > maxSeperation) {

			// Calculate the amount of points need and their required seperation
			double factor = pointsDelta / maxSeperation;
			double xSpacing = xDelta / factor;
			double ySpacing = yDelta / factor;

			// add the points
			for (int i = 1; i < (int) (factor) + 1; i++) {
				points.add(new Translation2d(xSpacing + points.get(points.size() - 1).getX(),
						ySpacing + points.get(points.size() - 1).getY()));
			}

		}
	}

	/**
	 * Initalizes the lookup table with 5 rows
	 */
	private void initilizeLookupTable() {
		initilizeLookupTable(5);
	}

	/**
	 * Initalizes the lookup table, the lookup table takes the form of pascals
	 * triangle https://en.wikipedia.org/wiki/Pascal%27s_triangle
	 * 
	 * @param size the amount of rows to initalizes.
	 */
	private void initilizeLookupTable(int size) {
		// Intializes the lookup table
		binomialLookUpTable = new ArrayList<>();

		// Adds the first to rows
		binomialLookUpTable.add(0, new int[] { 1 });
		binomialLookUpTable.add(1, new int[] { 1, 1 });

		// start on the third row then generate values for the new row
		for (int i = 2; i < size; i++) {
			// Create a new row 1 larger than its index
			int[] newRow = new int[i + 1];

			// For each coloumn add the 2 values in the above row
			for (int j = 1; j < newRow.length - 1; j++) {
				newRow[j] = binomialLookUpTable.get(i - 1)[j - 1] + binomialLookUpTable.get(i - 1)[j];
			}

			// Set the ends of each row to 1
			newRow[0] = 1;
			newRow[i] = 1;

			// Adds the new row to the LUT
			binomialLookUpTable.add(i, newRow);
		}

	}

}
