# Lib5K and Multithreading

A standard Lib5k robot application is multithreaded, with two primary threads:

 - Main thread
   - Handles FPGA and HAL setup, along with initialization of both WPILib and Lib5k
 - "Robot" thread
   - Notifier-driven
   - Contains the command scheduler
   - Handles system watchdog calls
   - Periodically interacts with the DriverStation socket
   - Handles I/O

Along with these, Lib5k introduces more threads through some library features:

 - [Logging](https://github.com/frc5024/lib5k/blob/master/logging/src/main/java/io/github/frc5024/lib5k/logging/RobotLogger.java) thread
   - Notifier-driven
   - Very I/O heavy
   - Handles logging to multiple outputs simultaneously
     - Over the network
     - To the local filesystem
     - To the session folder on the robot USB drive
 - [FaultReporter](https://github.com/frc5024/lib5k/blob/master/hardware/ni/src/main/java/io/github/frc5024/lib5k/hardware/ni/roborio/FaultReporter.java) thread
   - Notifier-driven
   - Periodically requests "health reports" from all system I/O
   - Reports any unusual issues to the log
   - Is very useful for diagnosing hard-to-find issues
     - This was used at an event in 2020 to find an MXP fault that was causing incorrect gyroscope data to be read

## Notifiers

[Notifiers](https://github.com/wpilibsuite/allwpilib/blob/a6a71f8c76d457a65b142d999dc0d6509254ae0d/hal/src/main/native/athena/Notifier.cpp) are interrupt-driven threads that run at precisely-timed intervals. These threads are run by the RoboRIO FPGA's internal clock.

When running on-robot, a notifier is a reliable way to have a task run at an exact interval, but when run in a simulation, then timing will rely on a simulated FPGA, which is less accurate. This is why Lib5k handles all mathematical calculations with an extra `dt` value, that scales the result based on the reliability of the notifier.

## The scheduler

The main robot tasks are running in a time-shared main thread. The model of this thread is: every scheduled task gets to run one iteration per 20ms. This is where Commands and Subsystems run.