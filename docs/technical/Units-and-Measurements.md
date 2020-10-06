# Standard Units and Measurements

All code **must** use the following SI units:
 - Meters
 - Degrees
 - Rotations-per-minute (RPM)
 - Kg
 - Joules
 - Milliseconds
 - Hertz
 - Volts
 - Amperes


## The exceptions

All irregular uses of units are written in JavaDocs. Here are explanations of when we don't follow our standard use of units.

## Robot design

Due to the fact that the Raider Robotics build, design, and mechanical teams use Imperial measurements, we must sometimes include them in our code. When dealing with robot measurements such as:
 - Chassis size
 - Wheel radius

these measurements must be written in the `RobotConstants` (sometimes called `RobotConfig`) file, and converted in that file. For example, if I had the radius of a wheel, and I was putting it in our code, I would add the following to `RobotConstants`:
```java
public static final double WHEEL_RADIUS = Units.inchesToMeters(3.0);
```

This way, the only file that knows about imperial measurements is `RobotConstants`, and everything else sees the converted measurement in meters.

## Timing

Everything in our code is measured in milliseconds, with the only exception being *ramp-rate* measurements. These are measured in seconds. 

## Angles

While the team works in degrees, and all our sensors work in degrees, our coordinate system uses radians. We solve this by using the WPILib's [Rotation2d](https://first.wpi.edu/FRC/roborio/release/docs/java/edu/wpi/first/wpilibj/geometry/Rotation2d.html) object:

```java
// Create an angle from degrees
Rotation2d myAngle = Rotation2d.fromDegrees(360.0);

// Get the angle as degrees
double myDegrees = myAngle.toDegrees();
```