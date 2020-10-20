---
sort: 2
---

# Tank Drive

The tank-drivetrain is a system with 3 inputs and 2 outputs. These are:

 - Inputs
   - Z angle
   - Left-side distance
   - Right-side distance
 - Outputs
   - Left-side voltage
   - Right-side voltage

The primary problem with a tank-based system is the challenge of navigating from point A to B on the field. Much like a real-life tank, or even a car, tank-drive robots cannot strafe, which means navigation between two poses in space requires careful planning of a smooth arc through space. 

Want to move 1 meter to the right? Much like parallel-parking a car, a tank-drive robot will need to determine the correct sequence of movements to get to that pose (generally an S curve in this situation).

## Localization

When controlling a tank-drive robot, we never instruct the left and right tracks individually. Instead, we send the robot a movement vector, and rely on software to get to the new position as fast as possible. In order for this to work though, the robot must know where it is. This is done with out localization system.

By fusing the left and right distance measurements with the Z angle over time, we can perform [dead reckoning](https://en.wikipedia.org/wiki/Dead_reckoning) to accurately estimate the robot's pose at any given point in time.

## Implementing a Tank Drive DriveTrain

Lib5K provides a helper base class called [`TankDriveTrain`](/lib5k/javadoc/io/github/frc5024/lib5k/bases/drivetrain/implementations/TankDriveTrain.html), which extends the base class for all drivetrains, [`AbstractDriveTrain`](/lib5k/javadoc/io/github/frc5024/lib5k/bases/drivetrain/AbstractDriveTrain.html). An example of implementing a drivetrain for autonomous movement can be found [in this example](https://github.com/frc5024/lib5k/blob/master/examples/src/main/java/io/github/frc5024/lib5k/examples/autonomous_path_following/subsystems/DriveTrain.java).

The idea behind `TankDriveTrain` is, Lib5K handles all the complex path planning code, and the user (you) is expected to implement function calls to the robot hardware. This lets us keep the same backend code, and switch out components like motor controllers and gyroscopes without needing to re-implement code.

When writing an [`AutonomousSequence`](/lib5k/javadoc/io/github/frc5024/lib5k/autonomous/AutonomousSequence.html), you can call one of the following methods on your drivetrain to get a pre-built path following, or on-the-spot turning command:

```java
PathFollowerCommand createPathingCommand(Path path, double epsRadius);
TurnToCommand createTurnCommand(Rotation2d heading, Rotation2d epsilon, double maxSpeedPercent, boolean fieldRelative);
```

These methods are documented [here](/lib5k/javadoc/io/github/frc5024/lib5k/bases/drivetrain/AbstractDriveTrain.html#createPathingCommand(io.github.frc5024.purepursuit.pathgen.Path,double)) and [here](/lib5k/javadoc/io/github/frc5024/lib5k/bases/drivetrain/AbstractDriveTrain.html#createTurnCommand(edu.wpi.first.wpilibj.geometry.Rotation2d,edu.wpi.first.wpilibj.geometry.Rotation2d,double,boolean)).

You can also manually make the drivetrain drive to a pose, or face an angle using [`setGoalPose​`](/lib5k/javadoc/io/github/frc5024/lib5k/bases/drivetrain/AbstractDriveTrain.html#setGoalPose(edu.wpi.first.wpilibj.geometry.Translation2d,edu.wpi.first.wpilibj.geometry.Translation2d)) and [`setGoalHeading​`](/lib5k/javadoc/io/github/frc5024/lib5k/bases/drivetrain/AbstractDriveTrain.html#setGoalHeading(edu.wpi.first.wpilibj.geometry.Rotation2d,edu.wpi.first.wpilibj.geometry.Rotation2d)).

For driver-operated control, see [`handleDriverInputs​`](/lib5k/javadoc/io/github/frc5024/lib5k/bases/drivetrain/implementations/TankDriveTrain.html#handleDriverInputs(double,double)) (example [here](https://github.com/frc5024/lib5k/blob/master/examples/src/main/java/io/github/frc5024/lib5k/examples/drivebase_simulation/commands/DriveCommand.java)).