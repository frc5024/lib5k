# Flywheels & Shooters

We like to use the words *flywheel* and *shooter* interchangeably, but it is important to note they are technically different things. A shooter is a system that uses transfer of momentum to launch an object through the air, and a flywheel is a solid weight that can be attached to a rotary shaft to increase its stability and momentum.

Although Lib5K uses the word *flywheel* to refer to a *shooter*, this document will call this system a *shooter* for simplicity.

## System design

Despite what some people may say, shooters are some of the simplest systems to design. A basic shooter consists of a powered wheel (called a launcher), and something solid (called a hood or backplate). When an object is pushed through the gap between the launcher and hood, it is slightly compressed, as the launcher spins it through, and out the other side. Assuming the launcher weighs enough, and is rotating fast enough, the object can be thrown many meters through the air, and with considerable backspin. If the launcher is not heavy enough, a flywheel is added to it. 5024 has recently used a steel cylinder as a flywheel on our 2020 robot, Darth Raider.

## Inputs & Outputs

Software wise, a shooter is also very simple, with a single input, and a single output. The output is just a motor (or multiple, in a gearbox), and the input is a sensor that can accurately measure angular velocity (an encoder or tacheometer).

This system can be controlled via either a [PID Controller](/lib5k/technical/control/PID.html), or a state-space controller. The latter of which will be explained in full in this document.

## Tips for working with 5024

This system must be as precise as possible for the kind of performance you will be asked to provide. It is common for team members to underestimate the importance of a good and reliable sensor in this system because "you can just set a voltage, and let it spin". As you should know by now, just "setting a voltage" is highly impractical due to physics and unpredictable batteries. We require a good sensor, so we can make use of mathematical models when controlling the shooter. The more precise the sensor, the more precise the model, which means the shooter can reach specific goal velocities faster.

## Required measurements

When setting up the system software-wise, you must take a few measurements to as much precision as possible of the physical system. Do not trust rough estimates, as the mechanical margin for error on some parts manufactured by the team is many times too large to be of any use. Get a scale, and a good measuring device.

### The motor(s)

Firstly, mark down the model of motor, and how many are being used. In 2020, this would be 1 NEO.

### The launcher

Mark down the mass of the launcher wheel in KG. If there is more than one wheel on the shooter (like the 4 used in 2020), count them all, and sum their masses.

Next, mark down the diameter of the wheel(s) in meters.

### The flywheel

Mark down the mass and diameter of the flywheel weight (assuming there is one) in KG and meters respectively. If there is a heavy rod connecting the weights to the wheels, include that mass too.

### The gear ratio

Measure the ratio between the *sensor* and *motors*, **not** from the motors to the wheels. If using a motor like a NEO or Falcon, which have built-in sensors, this ratio is simply `1.0`, since there are no gears between the motor and itself. This value should be expressed as `output / input`.

### Maximum velocity

Mark down the maximum velocity of the system *measured by the sensor*. This will require powering up the system, and logging the measured velocity of the sensor. This most likely will *not* be near the maximum velocity specified on the motor datasheet, since manufacturing error and imperfections will inevitably cause the flywheel to spin a bit slower than expected due to friction.

## Programming the system

Actual implementation of a subsystem is just like normal. This generally means building a [state machine](/lib5k/tutorials/Building-a-State-Machine.html), and creating a Singleton class. This document will only cover setting up the state-space controller, and simulation support.

This pseudo-code is based around the [`SimpleFlywheelController`](/lib5k/javadoc/io/github/frc5024/lib5k/control_loops/statespace/wrappers/SimpleFlywheelController.html) and the [`FlywheelSystemSimulator`](/lib5k/javadoc/io/github/frc5024/lib5k/simulation/systems/FlywheelSystemSimulator.html) classes provided by Lib5K. An example project for this system can also be found [here](https://github.com/frc5024/lib5k/tree/master/examples/src/main/java/io/github/frc5024/lib5k/examples/statespace_flywheel). The following example does not use a state machine, just for visual simplicity. State machines are strongly recommended for all systems.

```java
// Measurements from earlier
final DCBrushedMotor MOTORS = new DCBrushedMotor(DCMotor.getNEO(2)); // This would represent two NEOs in a gearbox
final double LAUNCHER_MASS = 0.85638239; // Kg
final double LAUNCHER_DIAMETER = 0.635029; // M
final double FLYWHEEL_MASS = 0.1995806; // Kg
final double FLYWHEEL_DIAMETER = 0.0508; // M
final double GEAR_RATIO = 1.0; // NEOs have encoders built-in
final double MAX_RPM = 4450.0;
final double EPSILON = 80.0; // 80 RPM is generally a good epsilon for shooters, but it can be changed

// Motor objects
ExtendedSparkMax neo1 = new ExtendedSparkMax(1, MotorType.kBrushless);
ExtendedSparkMax neo2 = new ExtendedSparkMax(2, MotorType.kBrushless);

// Encoder
CommonEncoder encoder = neo1.getCommonEncoder();

// The state-space controller
SimpleFlywheelController controller = new SimpleFlywheelController(
                MOTORS,
                LAUNCHER_MASS,
                LAUNCHER_DIAMETER,
                FLYWHEEL_MASS,
                FLYWHEEL_DIAMETER,
                MAX_RPM,
                GEAR_RATIO, 
                12.0, // This is the maximum voltage this system can use
                EPSILON);

// The simulator
FlywheelSystemSimulator simulator = new FlywheelSystemSimulator(controller);

// Method for getting the current velocity of the launcher
private double getLauncherVelocityRPM() {
    // Handle simulation vs. non-simulation
    if (RobotBase.isSimulation()) {
        return simulator.getAngularVelocityRPM();
    } else {
        return encoder.getVelocity();
    }
}

// Method for setting the motor voltages
private void setLauncherVoltage(double volts) {
    // Handle simulation vs. non-simulation
    if (RobotBase.isSimulation()) {
        simulator.setInputVoltage(volts);
    } else {
        neo1.setVoltage(volts);
        neo2.setVoltage(volts);
    }
}

// A method that runs once every 0.02 seconds
public void periodic() {
    // Update the simulation if needed
    if (RobotBase.isSimulation()) {
        simulator.update(0.02);
    }

    // Get the next voltage output from the controller
    double voltage = controller.computeNextVoltage(getLauncherVelocityRPM(), 0.02);

    // Set the motors to the new voltage
    setLauncherVoltage(voltage);
}

// This should be called by anything that wants to set the velocity of the launcher
public void setDesiredVelocity(double desiredRPM) {
    controller.setDesiredVelocity(desiredRPM);
}

```
