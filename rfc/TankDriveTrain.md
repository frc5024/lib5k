# DriveTrain Base Classes

Date: Oct 16, 2020<br>
Proposed by: Evan Pratten <@ewpratten><br>
Implemented: Oct 22, 2020<br>
Related Pull Requests: [1](https://github.com/frc5024/lib5k/pull/116), [2](https://github.com/frc5024/lib5k/pull/131), [3](https://github.com/frc5024/lib5k/pull/137)

## Abstract

DriveTrain code barely changes between robots, yet is one of the largest, most complex systems. The solution is to move the majority of the DriveTrain code into Lib5K, and make use of *inheritance* for actual implementation. By moving DriveTrains into Lib5K, we also open up the possibility of the library interacting with the drivetrain more seamlessly including:

 - Cleaner simulation code
 - Pre-made autonomous driving commands
 - Extensive system unit tests

## Implementation

This change is implemented by splitting our regular DriveTrain into a few abstract layers:

 - [`AbstractDriveTrain`](/lib5k/javadoc/io/github/frc5024/lib5k/bases/drivetrain/AbstractDriveTrain.html)
   - Contains common code between Tank, Holonomic, and Swerve drivetrains
 - [`TankDriveTrain`](/lib5k/javadoc/io/github/frc5024/lib5k/bases/drivetrain/implementations/TankDriveTrain.html)
   - Implements `AbstractDriveTrain`
   - Contains code specific to any type of tank-style drivetrain
 - [`DualPIDTankDriveTrain`](/lib5k/javadoc/io/github/frc5024/lib5k/bases/drivetrain/implementations/DualPIDTankDriveTrain.html)
   - Implements `TankDriveTrain`
   - Contains path-following code that requires PID controllers
 - [`OpenLoopTankDriveTrain`](/lib5k/javadoc/io/github/frc5024/lib5k/bases/drivetrain/implementations/OpenLoopTankDriveTrain.html)
   - Implements `TankDriveTrain`
   - Only contains code for driving with a controller

## Revisions

 1. [Split `TankDriveTrain` into `DualPIDTankDriveTrain`, and `OpenLoopTankDriveTrain`](https://github.com/frc5024/lib5k/commit/f40a52dd3742ce2baf666ac8abc5f8dd37518663)
 2. [Move Pure Pursuit control code inside of `DualPIDTankDriveTrain`](https://github.com/frc5024/lib5k/pull/131)
 3. Added path following unit tests to Lib5K