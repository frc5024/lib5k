---
sort: 2
---

# PID+F Control

PID+F stands for Proportional + Integral + Derivative + Feed Forward. This comes from the equation that it refers to:

```
(P + I + D) + F
```

*P*, *I*, and *D* are all variables that are made up of a calculation times a constant gain value. *F* is a constant

## Proportional

P is the simplest element of the equation. The P value is made up of the difference between the current state and the reference (the elevator's current height, and desired height from the [Introduction](/lib5k/technical/control/Introduction.html)), multiplied by the constant `Kp`.

```java
double calculateP(double desiredHeight, double currentHeight, double Kp) {
    return (desiredHeight - currentHeight) * Kp;
}
```

The farther away the elevator is from its goal height, the larger the value this function will return. Reducing the `Kp` value from its default of 1 will make the output less "aggressive". Sometimes you can get away with controlling a mechanism using only this function, and ignoring *I*, *D*,  and *F*. We did this in 2018 on our elevator, in 2019 on our climber, and something very similar in 2020 on our intake and hopper.

## Integral

Integral and Derivative have nearly the same effect, but one key difference. Integral control (commonly called "PI control") is always cautious. This means that as the system reaches its goal (elevator reaches its desired height), the controller will stop just before the goal is reached. This is great for systems like a flywheel, where we just want to get up near a specific speed, and can handle being 5 to 10 RPM under the goal. We also always use PI control to handle driving a specific distance during autonomous gameplay. Since the robot keeps a fair bit of momentum, we always slightly undershoot our goal positions on the ground, then let the weight of the robot carry it the rest of the way. 

The I value is an accumulator. Every loop of the controller will add the current error to a counter. This is then multiplied by the constant `Ki`.

```java
// Accumulator value
private double integral = 0.0;

double calculateI(double desiredHeight, double currentHeight, double Ki) {
    // Add to the accumulator
    integral += (desiredHeight - currentHeight);

    // Return the calculated I value
    return integral * Ki;    
}
```

## Derivative

Unlike Integral, Derivative will always overshoot the goal a bit, then come "back down". On an elevator, the manipulator would raise a few cm higher than it should, then come back down to the desired height. Derivative control is very good when you need a system to be fast and aggressive. We use Derivative control on robots to control their heading. Any time you see one of our robots performing an autonomous turn, you will notice that it slightly overshoots, then jerks back to exactly the angle it was intending to face. We do this on purpose for a few reasons: 

 - It is a fast movement
 - We use the jerkiness to overcome the force of static friction when playing on carpets
 - We are less dependant on the robot's bus voltage. A low battery does not have much of an effect on Derivative control, while it has a big effect on Integral control

The D value is simply the difference between the current error, and last error, multiplied by the constant `Kd`.

```java
// Last error tracker
private double lastError = 0.0;

double calculateD(double desiredHeight, double currentHeight, double Ki) {
    // Determine the current error
    double error = (desiredHeight - currentHeight);

    // Determine the derivative
    double derivative = error - lastError;

    // Keep track of the error
    lastError = error;

    // Return the calculated D value
    return derivative * Kd;
}
```

## Feed Forward

The final component of a PID+F controller is the Feed Forward value. Many times, we just set this to 0 and ignore it, but Feed Forward is very useful when a robot must lift something. 

On an elevator, we would set this to a specific number that is defined as "the number of volts needed to hold the elevator in place, and fight gravity". This value is generally fairly easy to guess, but it gets complicated in the following situations:

 - When using an arm
 - When holding an object

When an elevator picks up an object, we generally switch between an "unburdened" value and a "burdened" value. each of these being the correct Feed Forward for not holding anything, and holding a game piece respectively.

To make this calculation process much easier, the WPILib team have written [this document](https://docs.wpilib.org/en/stable/docs/software/advanced-control/controllers/feedforward.html) that explains how to use their tools to calculate Feed Forward for any system.

## Using a PID controller in your code

Lib5K tries to make the process of using a PID / PID+F controller in your program really easy. Here is an example subsystem for an elevator:

```java
class Elevator extends SubsystemBase {

    // Motor
    private SpeedController motor;

    // Range finder pointed at the ground
    // The rangefinder will come with instructions on how to set this up
    // Generally, you multiply the voltage by a specific number
    // We don't cover this here because the process varies between products
    private AnalogInput rangeFinder;

    // PID Controller
    private SettlingController controller;

    // Store the desired height
    private double desiredHeight = 0.0;

    public Elevator() {

        // PIDF gains
        double Kp = 0.8;
        double Ki = 0.0003;
        double Kd = 0.0;
        double FF = 4.0;

        // Set up locals
        this.motor = new ExtendedTalonSRX(0);
        this.rangeFinder = new AnalogInput(0);
        this.controller = new ExtendedPIDController(Kp, Ki, Kd, FF);
    }

    @Override
    public void periodic() {

        // Pass the measurements into the calculate function, and send the calculated voltage to the motor
        motor.setVoltage(controller.calculate(desiredHeight - rangeFinder.get()));
    }

    public void setHeight(double heightMeters) {
        this.desiredHeight = heightMeters;
    }
}
```