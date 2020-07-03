package io.github.frc5024.purepursuit.util;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.geometry.Translation2d;

public class Smoothing {

    private Smoothing(){}


    /**
     * This is an algorithm taken from team 2168 to smooth paths
     * @param positions an array list of positions to smooth
     * @param weight_data changes the amount of smoothing
     * @param weight_smooth the larger this is the smoother the path is
     * @param tolerance amount the path will change
     * @return a smoothed path
     */
    public static ArrayList<Translation2d> smooth(ArrayList<Translation2d> positions, double weight_data, double weight_smooth, double tolerance){
        // Make a copy of list of positions
        ArrayList<Translation2d> newPositions = positions;
        
        // Change variable
        double change = tolerance;
        double aux1;
        double aux2;

        
        while(change >= tolerance){
            change = 0.0;

            for(int i=1; i<positions.size()-1; i++){
                aux1 = newPositions.get(i).getX();
                aux2 = newPositions.get(i).getY();
                
                newPositions.set(i, new Translation2d(
                    aux1 + weight_data * (positions.get(i).getX() - aux1) + weight_smooth * (newPositions.get(i-1).getX() + newPositions.get(i+1).getX()- (2.0 * aux1)), 
                    aux2 + weight_data * (positions.get(i).getY() - aux2) + weight_smooth * (newPositions.get(i-1).getY() + newPositions.get(i+1).getY()- (2.0 * aux2)))
                    );

                change += Math.abs(aux1 - newPositions.get(i).getX()) + Math.abs(aux2 - newPositions.get(i).getY()) ;
            }

        }

        return newPositions;
    }
}