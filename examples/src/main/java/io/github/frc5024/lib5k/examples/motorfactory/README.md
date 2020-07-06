# Motor Factory Example

This is an example on how to use Motor Factories


## Overview

Motor Factories are used for making configured motors easily.

Currently there are 2 different motor factories

* CTREMotorFactory
    * Used to make CTRE manufactured motors such as TalonSRX, TalonFX, and VictorSPX
    * Uses the CTREConfig class for configuration

* RevMotorFactory
    * Used to make Rev Robotics manufactured motors such as the SparkMax
    * Uses the RevConfig class for configuration


#### CTREMotorFactory

CTREMotorFactory has 2 static methods per motor type

The first is a method which takes in just the motor id and returns a motor with the default configuration

```java
// TalonSRX with default configuration
CTREMotorFactory.createTalonSRX(1);

// TalonFX with default configuration
CTREMotorFactory.createTalonFX(1);

// VictorSPX with default configuration
CTREMotorFactory.createVictorSPX(1);
```

The second is a method which takes in a motor id and a CTREConfig object.

```java
// TalonSRX with a custom configuration
CTREMotorFactory.createTalonSRX(1, new CTREConfig(true));

// TalonFX with a custom configuration
CTREMotorFactory.createTalonFX(1, new CTREConfig(true));

// VictorSPX with a custom configuration
CTREMotorFactory.createVictorSPX(1, new CTREConfig(true, 36, 37, 15, 0, true));
```

The CTREConfig class has multiple overloads which provide a default config, a config for motor inversion, a config for current limiting, and a config for all options.

```java
// A default Config
new CTREConfig();

// A inverted motor config
new CTREConfig(true);

// A current limit config
new CTREConfig(true, 36, 37, 15, 0, true);


// A full config
new CTREConfig(true, true, false, true, true, 33, 15, 30, 0, true)
```


#### RevMotorFactory

RevMotorFactory has 2 static methods per motor

The first one creates a motor with the default configuration

```java
// Creates a new sparkMax the motor type needs to be specified
RevMotorFactory.createSparkMax(1, MotorType.kBrushless);
```

The second method accepts a RevConfig object.

```java
// Creates a motor with custom inversion
RevMotorFactory.createSparkMax(8, new RevConfig(MotorType.kBrushed, true));
```

The RevConfig class provides multiple overloads, each requires the motorType to be specified, one allows for motor inversion, one for current limit config, and one that allows for full configuration

```java
// A default motor config
new RevConfig(MotorType.kBrushless);

// A motor config that lets the direction be flipped
new RevConfig(MotorType.kBrushed, true);

// A motor config for configuring current limits
new RevConfig(MotorType.kBrushless, true, 30, 0, 20000);

// A full motor config
new RevConfig(MotorType.kBrushless, true, false, true, 30, 0, 20000, false);

```

If you want to make a motor follow another you call the method makeSlave on the motor you wish to follow, this will make a motor with the id passed into the method it will also share the same config as the master motor.

```java
// Creates a master motor
ExtendedTalonSRX master = CTREMotorFactory.createTalonSRX(1, new CTREConfig(true));

// Makes the slave motor, it is also returned by this method
master.makeSlave(1);

// Creates a master motor
ExtendedSparkMax master2 = RevMotorFactory.createSparkMax(2, MotorType.kBrushless);

// Makes the slave motor, it is also returned by this method
master2.makeSlave(2);
```




