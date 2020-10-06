package io.github.frc5024.purepursuit.pathgen;

import java.util.ArrayList;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.geometry.Translation2d;

/**
 * This class is used to generate points along a bezier curve
 * TODO Add consist point distance, Allow controlling the path with weights and ratios
 */
public class BezierPath extends Path{
	
	// What percentage of the curve should each point be at.
	private double spacing;

	private static ArrayList<int[]> binomialLookUpTable;

	

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

		if(binomialLookUpTable == null){
			initilizeLookupTable();
		}

		// TODO implement spacing and weights and ratios
		calculatePoints(wayPoints);

	}

	/**
	 * 
	 * @param n row of lut
	 * @param k coloumn of lut
	 * @return a binomial from the lut
	 */
	private double binomial(int k, int n){
		// Adds new entries to LUT table if they are missing.
		while(n >= binomialLookUpTable.size()){

			// continues pascal triangle
			int lutSize = binomialLookUpTable.size();

			int[] newRow = new int[lutSize + 1];
			newRow[0] = 1;

			for(int i = 1, prev = lutSize - 1; i < lutSize; i++){
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
	 * @param t t value for equation between 0, 1
	 * @param i	the current point
	 * @param n the number of points starting at 0
	 * @return the bezier value to add to point x,y
	 */
	private double calculateBezier(double t, int i, int n){

		// Formula to calculate value
		return binomial(i, n) * Math.pow(t, i) * Math.pow((1 - t), (n - i));
	}


	/**
	 * calculates points along a bezier curve
	 * @param wayPoints the waypoints to form the hull
	 */
	private void calculatePoints(Translation2d[] wayPoints){
		// The number of waypoints starting from 1
		int numberOfWayPoints = wayPoints.length - 1;
		
		// Increases the t at the rate of 1 %
		for(int i = 0; i< 101; i++){

			// Resets x, y variables
			double x = 0;
			double y = 0;
			
			// Calculates the T value
			double t = .01 * i;

			// TODO Make customizable
			// calculates x, y value for each point
			for(int j = 0; j < numberOfWayPoints + 1; j++){
				double bezierNumber = calculateBezier(t, j, numberOfWayPoints);
				x += wayPoints[j].getX() * bezierNumber;
				y += wayPoints[j].getY() * bezierNumber;
			}


			// Adds point to arraylist
			points.add(new Translation2d(x, y));
		}
	}


	/**
	 * Initalizes the lookup table with 5 rows
	 */
	private void initilizeLookupTable(){
		initilizeLookupTable(5);
	}


	/**
	 * Initalizes the lookup table, the lookup table takes the form of pascals triangle
	 * https://en.wikipedia.org/wiki/Pascal%27s_triangle
	 * @param size the amount of rows to initalizes.
	 */
	private void initilizeLookupTable(int size){
		// Intializes the lookup table
		binomialLookUpTable = new ArrayList<>();

		// Adds the first to rows
		binomialLookUpTable.add(0, new int[]{1});
		binomialLookUpTable.add(1, new int[]{1,1});

		// start on the third row then generate values for the new row
		for(int i = 2; i < size; i++){
			// Create a new row 1 larger than its index
			int[] newRow = new int[i + 1];

			// For each coloumn add the 2 values in the above row
			for(int j = 1; j < newRow.length - 1; j++){
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
