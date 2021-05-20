---
sort: 1
---

# Introduction

The core concept of basic control theory is: *"I am at A, and want to get to B in the fastest and most reliable way possible"*

When programming a robot, *A* and *B* can be:

 - Positions
   - Moving an elevator up and down
   - Driving forward a meter
   - Rotating an arm to an angle
 - Velocities
   - Keeping a flywheel at a specific speed
   - Driving at a specific speed

There are a large number of methods for calculating the correct actions to get from *A* to *B*. In Lib5K, we make use of two main methods (with a third used very rarely):

 - [Proportional + Integral + Derivative + Feed Forward](/technical/control/PID) (*PID* or *PID+F*)
   - The simplest method
   - Requires a lot of work to tune correctly
 - [State-Space](/technical/control/State-Space)
   - Very math heavy
   - The most advanced method
   - With accurate measurements, produces results more accurate than we ever actually need
 - Articulated and Mobile Robots for SErvices and TEchnologies (*RAMSETE*)
   - No longer included in Lib5K
   - Was used once to handle velocity control of our 2020 robot: *Darth Raider*

## Bang-Bang: The simplest control method

We will use a *Bang-Bang* controller to better explain the issue at hand. Let's say that we have an elevator (a system that must move a manipulator up and down a rail vertically) that we want to raise until it is 1 meter off the ground. This elevator comes with:

 - A motor
 - A sensor that can tell us how high off the ground this elevator is

The simplest solution to this problem is to write a function that takes in the desired height and the elevator's current height, and returns the number of volts that should be applied to the motor (assuming `+12` volts will raise it at top speed). This can be implemented as such:

```java
double calculateVoltageForElevator(double desiredHeight, double currentHeight) {
    // If the current height is below the desired height, move up
    if(currentHeight < desiredHeight) {
        return 12.0;
    } else {
        // If the current height is too high, move down
        return -12.0;
    }
}
```

An issue is raised here: *physics*. 

The elevator will move upwards until it reaches it's desired height. At this point, the power will cut off, and it will decelerate past the desired height, which will cause the "elevator too high" code to lower the elevator. It will be lowered until it hits the desired height, decelerate, and the cycle will continue. The name Bang-Bang comes from the fact that all control comes from two extremes of the output (go up, go down), while not taking into account any physical calculations.