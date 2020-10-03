package io.github.frc5024.common_drive.geometry;

import edu.wpi.first.wpiutil.math.Matrix;
import edu.wpi.first.wpiutil.math.Num;
import edu.wpi.first.wpiutil.math.numbers.N1;

/**
 * An N-dimensional polytope This represents the region consisting of all points
 * X such that H * X <= k
 * 
 * @param <Dimensions>  Number of dimensions
 * @param <Constraints> Number of constraints
 * @param <Verticies>   Number of verticies
 */
public class HVPolytope<Dimensions extends Num, Constraints extends Num, Verticies extends Num> {

    Matrix<Constraints, Dimensions> H;
    Matrix<Constraints, N1> k;
    Matrix<Dimensions, Verticies> verticies;

    /**
     * Constructs a polytope given the H and k matrices
     * 
     * @param H         H
     * @param k         K
     * @param verticies Number of verts
     */
    public HVPolytope(Matrix<Constraints, Dimensions> H, Matrix<Constraints, N1> k,
            Matrix<Dimensions, Verticies> verticies) {
        this.H = H;
        this.k = k;
        this.verticies = verticies;
    }
}