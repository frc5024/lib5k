# The RobotLogger

When developing control system code, it is very important to keep logs of everything that happens on the system. This way, if anything goes wrong, the software developers have information on what was happening at the time of the issue. Lib5K has a custom logging system built-in that is specifically designed for use in the FRC competition environment.

## Key features of the Lib5K logger

The Lib5K logger (RobotLogger) is designed around the idea of caching and bursting data. The logger runs in its own thread at 20hz, and will cache all logs printed between periods, then bust print them to NetConsole and to the robot's session logs.

The main reason we log this way instead of just using something like [log4j](https://logging.apache.org/log4j/2.x/) or print statements is: we try to never block the main thread with IO that is not absolutely critical to gameplay. Also, if for some reason, the logger crashes, it won't take out the main thread, so gameplay can continue without interruption.

All generated logs are backed up via Lib5K's [robot sessions](/lib5k/technical/Robot-Sessions) system.

## Producing logs from your code

To write a log from your code, you just need to call one function:

```java
RobotLogger.getInstance().log("This is my log message");
```

If you are looking for a little extra functionality, the following methods are also available. To log some data, combine the log method with [`String.format`]():

```java
RobotLogger.getInstance().log(String.format("I am a %d programmer", 1337));
```

To skip the logging cache, use the following. Only ever do this if logging from the Robot constructor. Everything else should use the cache.

```java
RobotLogger.getInstance().log("This is my really important log message", Level.kRobot);
```

These are the available log levels:

```java

// Standard log levels
RobotLogger.getInstance().log("This is my debug message", Level.kDebug);
RobotLogger.getInstance().log("This is my info message", Level.kInfo);
RobotLogger.getInstance().log("This is my warning message", Level.kWarning);

// Log levels for special uses (mostly used internally in Lib5K)
RobotLogger.getInstance().log("This is my non-cached message", Level.kRobot);
RobotLogger.getInstance().log("This is my critical library message", Level.kLibrary);
```

### An example logfile

The logs produced by the robot look like this:

```
INFO at 0.36s: io...autonomous_path_following.Main::<init>() -> Adding autonomous paths
INFO at 0.38s: io...autonomous.RobotProgram::addAutonomous() -> Added autonomous sequence: ForwardOneMeter
INFO at 0.38s: io...roborio.FaultReporter::handleRailStatuses() -> 3v3 Rail Enabled
DEBUG at 0.87s: io...DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
INFO at 0.87s: io...logging.USBLogger::update() -> A CMOD occured
DEBUG at 0.89s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 0.91s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 0.93s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 0.95s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 0.97s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 0.99s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 1.01s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 1.03s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 1.05s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 1.07s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 1.09s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 1.10s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 1.13s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 1.13s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
DEBUG at 1.15s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
INFO at 1.16s: io...autonomous.RobotProgram::disabledInit() -> Robot disabled
INFO at 1.17s: io...autonomous_path_following.Main::disabled() -> Stopping drivetrain
DEBUG at 1.17s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
INFO at 1.17s: io...autonomous_path_following.Main::disabled() -> Stopping drivetrain
DEBUG at 1.17s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
INFO at 1.17s: io...common_drive.DriveTrainBase::stop() -> Stopped DriveTrain
DEBUG at 1.18s: io...common_drive.DriveTrainBase::runIteration() -> DriveTrain.periodic() has not yet been run by the scheduler... waiting
INFO at 1.26s: io...common_drive.DriveTrainBase::handleOpenLoopControl() -> Switched to open-loop control
INFO at 8.15s: io...autonomous.RobotProgram::autonomousInit() -> Autonomous started
INFO at 8.16s: io...autonomous.RobotProgram::autonomousInit() -> Starting autonomous sequence: ForwardOneMeter
INFO at 8.21s: io...hardware.ni.roborio.FaultReporter::update() -> Robot FPGA outputs have been enabled
INFO at 8.21s: io...common_drive.DriveTrainBase::setPose() -> Setting pose to: Pose2d(Translation2d(X: 0.00, Y: 0.00), Rotation2d(Rads: 0.00, Deg: 0.00))
INFO at 8.22s: io...common_drive.commands.PathFollowCommand::initialize() -> Reset path follower
INFO at 8.22s: io...common_drive.commands.PathFollowCommand::initialize() -> Opening a CSV logfile to save path progress to
INFO at 8.23s: io...common_drive.DriveTrainBase::setActiveSide() -> Set active side to: kPrimary
INFO at 8.27s: io...common_drive.DriveTrainBase::handleRabbitChase() -> Switched to 'rabbit chase' mode
INFO at 8.27s: io...common_drive.DriveTrainBase::handleRabbitChase() -> Position goal is: Translation2d(X: 0.15, Y: 0.00)
INFO at 8.27s: io...common_drive.DriveTrainBase::handleRabbitChase() -> Switching to default gear
INFO at 9.08s: io...common_drive.commands.PathFollowCommand::end() -> Robot successfully reached goal pose: Translation2d(X: 1.00, Y: 0.00)
INFO at 9.08s: io...common_drive.DriveTrainBase::setActiveSide() -> Set active side to: kPrimary
INFO at 9.08s: io...common_drive.DriveTrainBase::stop() -> Stopped DriveTrain
INFO at 9.08s: io...common_drive.commands.PathFollowCommand::end() -> Saving CSV logfile
INFO at 9.09s: io...common_drive.DriveTrainBase::handleOpenLoopControl() -> Switched to open-loop control
INFO at 10.89s: io...autonomous.RobotProgram::disabledInit() -> Robot disabled
INFO at 10.90s: io...autonomous_path_following.Main::disabled() -> Stopping drivetrain
INFO at 10.90s: io...common_drive.DriveTrainBase::stop() -> Stopped DriveTrain
INFO at 10.93s: io...roborio.FaultReporter::update() -> Robot FPGA outputs have been disabled
```

## Analyzing logs in real time

Lib5K comes with a few Python scripts for quality-of-life. One of these is [`logreader.py`](https://github.com/frc5024/lib5k/blob/master/scripts/logreader.py). This script will connect to a robot over SSH and display the log data in real time with configurable filtering.

**Note: This WILL NOT work if your robot's router has the firewall enabled. Disable the firewall in the radio configuration utility**

### Using logreader

Let's say we want to read the logs from a real robot, and view all log levels. To do this, we would run:

```sh
python3 /path/to/logreader.py 5024 -t WARNING,INFO,DEBUG -f
```

If we wanted to connect to a simulation running on the same computer using the same settings as above, we just need to add `-l`:

```sh
python3 /path/to/logreader.py 5024 -t WARNING,INFO,DEBUG -f -l
```

### Program options

```
usage: python3 logreader.py [-h] [-f] [-i IP] [-l] [-t LEVEL_TYPES] [-c CLASSES]
                 [-m METHODS] [-v]
                 team

A tool for reading and sorting log data in real time over a network

positional arguments:
  team                  FRC Team number of the robot or simulation

optional arguments:
  -h, --help            show this help message and exit
  -f, --follow          Setting this flag will follow the logs instead of just
                        dumping a snapshot
  -i IP, --ip IP        Robot or simulation IP address
  -l, --local-simulation
                        Use this flag if connecting to a simulation running on
                        the same computer
  -t LEVEL_TYPES, --level-types LEVEL_TYPES
                        Comma-seperated list of allowed log levels. Options:
                        DEBUG, INFO, WARNING
  -c CLASSES, --classes CLASSES
                        Comma-seperated list of class names. This can be used
                        to only view logs from a specific list of classes
  -m METHODS, --methods METHODS
                        Comma-seperated list of method names. This can be used
                        to only view logs from a specific list of methods
  -v, --verbose         For logreader debugging
```

**NOTE: By default, logreader hides all DEBUG messages. They must be shown manually**
