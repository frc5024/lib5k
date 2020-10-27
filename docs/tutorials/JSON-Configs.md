# Storing Robot Configuration

Robot code must be highly configurable. Components change, values need to be tweaked, and sensors get relocated. A common approach to robot configuration is to define a few `static final` classes throughout the codebase, and fill them with settings variables. Lib5K provides a much cleaner way to handle robot configuration. This is via the use of a [JSON](https://en.wikipedia.org/wiki/JSON) configuration file stored on the robot.

## How it works

First, you define a Java class to store your data. This is basically just a class filled with empty variables. You can then pass this class, and a filename into [`SingleInstanceJSONConfig<T>`](/lib5k/javadoc/io/github/frc5024/lib5k/config/SingleInstanceJSONConfig.html). `SingleInstanceJSONConfig<T>` will parse the provided JSON file, and return an object of your class, but with all the variables filled out. This process uses Google's [GSON](https://github.com/google/gson) library in the backend, so anything that works in GSON will work here too.

By default, `SingleInstanceJSONConfig<T>` will try to load a file called `robotconfig.json`. This file should be placed in `src/main/deploy` in your code. When you deploy code to the robot, the file is copied to `/home/lvuser/deploy` on the RoboRIO.

## Pre-made datatypes

Some config types are very commonly used. The same data will always be needed for defining a motor, for example. Due to this, Lib5K includes datatypes for many common config settings, which you can just drop in your java file:

 - [`JSONMotor`](/lib5k/javadoc/io/github/frc5024/lib5k/config/types/JSONMotor.html)
 - [`JSONEncoder`](/lib5k/javadoc/io/github/frc5024/lib5k/config/types/JSONEncoder.html)
 - [`JSONPIDGains`](/lib5k/javadoc/io/github/frc5024/lib5k/config/types/JSONPIDGains.html)

The JavaDoc for each of these classes also contains an example of how to represent the data in JSON.

## Example

The following is an example Java object, and the JSON file to go along with it:

```java
class DriveTrainConfig {
    JSONMotor leftMotor;
    JSONEncoder leftEncoder;
    JSONMotor rightMotor;
    JSONEncoder rightEncoder;
}

class ElevatorConfig {
    JSONMotor motor;
    JSONEncoder encoder;
}

class MyConfig {
    DriveTrainConfig drivetrain;
    ElevatorConfig elevator;
}
```

```json
{
    "drivetrain": {
        "leftMotor": {
            "id": 0
        },
        "rightMotor": {
            "id": 1,
            "inverted": true
        },
        "leftEncoder": {
            "cpr":1440
        },
        "rightEncoder": {
            "cpr":1440
        }
    },
    "elevator": {
        "motor": {
            "id": 2,
            "inverted": true
        },
        "encoder": {
            "cpr":4096
        }
    }
}
```

## Loading the example

Load the example above would be as simple as: 

```java
static SingleInstanceJSONConfig<MyConfig> configLoader = new SingleInstanceJSONConfig<>(MyConfig.class);
static MyConfig config = configLoader.getConfig();
```

## Making Gradle pre-check your config before deploying to a robot

Lib5K provides the [`ConfigValidator`](/lib5k/javadoc/io/github/frc5024/lib5k/unittest/ConfigValidator.html) tool to automatically check your config file for you during build time. Just [add a new unit test](https://www.vogella.com/tutorials/JUnit/article.html) to the robot that looks something like:

```java

@Test public void validateConfig() {
    new ConfigValidator(myRobotConfig).validate();
}
```

Where `myRobotConfig` is a static config object defined in your actual robot code. See Lib5K's [`SingleInstanceJSONConfigTest`](https://github.com/frc5024/lib5k/blob/master/lib5k/src/test/java/io/github/frc5024/lib5k/config/SingleInstanceJSONConfigTest.java) class for an example.