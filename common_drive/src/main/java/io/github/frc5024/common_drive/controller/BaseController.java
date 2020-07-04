package io.github.frc5024.common_drive.controller;

public interface BaseController {
    
    public double calculate(double error, double dt);

    public void reset();
}