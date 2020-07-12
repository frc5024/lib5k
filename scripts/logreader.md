# LogReader documentation

`logreader.py` is a script for reading robot logs. It can run in two modes:
    - Snapshot (default)
      - Just dumps all logs written from boot till the time the script is run
    - Follow
      - Prints logs as they are written by the robot program

This script requires the use of Lib5K's RobotLogger class, and can be run on both a real robot, and in a simulation.

## Usage

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

For example, If I want to connect to a simulation on my laptop, and only view warnings and debug messages from the robot class in real time, I can use this command:
```sh
python3 logreader.py 5024 -l -c Robot -t WARNING,DEBUG
```

**NOTE: By default, logreader hides all DEBUG messages. They must be shown manually**

If I want everything, I can use:
```sh
python3 logreader.py 5024 -l
```

## Example logfile

This is a logfile pulled from one of Lib5K's example programs:
```log
INFO at 0.0000s: io.github.frc5024.libkontrol.statemachines.StateMachine::log() -> Added state: kIdle
INFO at 0.0000s: io.github.frc5024.libkontrol.statemachines.StateMachine::log() -> Set state kIdle as default
INFO at 0.0000s: io.github.frc5024.libkontrol.statemachines.StateMachine::log() -> Added state: kSpinup
INFO at 0.0000s: io.github.frc5024.libkontrol.statemachines.StateMachine::log() -> Added state: kAtGoal
INFO at 0.0000s: io.github.frc5024.lib5k.examples.neo_flywheel.Main::disabledInit() -> Robot disabled
INFO at 0.0000s: io.github.frc5024.lib5k.examples.neo_flywheel.subsystems.Flywheel::handleIdle() -> Became idle
INFO at 105.0000s: io.github.frc5024.lib5k.examples.neo_flywheel.Main::teleopInit() -> Teleop started
INFO at 105.0000s: io.github.frc5024.lib5k.examples.neo_flywheel.subsystems.Flywheel::setGoalVelocity() -> Setting goal velocity to 3000.00
INFO at 105.0000s: io.github.frc5024.lib5k.examples.neo_flywheel.subsystems.Flywheel::handleSpinup() -> Spinning up
INFO at 116.0000s: io.github.frc5024.lib5k.examples.neo_flywheel.Main::disabledInit() -> Robot disabled
INFO at 116.0000s: io.github.frc5024.lib5k.examples.neo_flywheel.subsystems.Flywheel::handleIdle() -> Became idle
```
