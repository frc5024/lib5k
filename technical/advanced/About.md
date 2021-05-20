# What Lib5K does

Lib5K is a mix of a hardware abstraction library and a utilities library. This library sits on top of [WPILib](https://wpilib.org/) and every library provided by third-party vendors. Unlike WPILib, we integrate vendor libraries into Lib5K. This integration allows us to write a unified abstraction layer, where the programmer need not worry about the differences between vendor implementations.

## Vendor abstraction

We have a few core hardware vendors, each with their own package in Lib5K:

| Vendor                                   | Package               | Description                                                                                           |
|------------------------------------------|-----------------------|-------------------------------------------------------------------------------------------------------|
| [Cross The Road Electronics](https://www.ctr-electronics.com/) (CTRE)    | [`ctre`](https://github.com/frc5024/lib5k/tree/master/lib5k/src/main/java/io/github/frc5024/lib5k/hardware/ctre)            | Provides "smart" motor controllers, rotary encoders, IMU devices, and power-management devices.       |
| [Rev Robotics](https://www.revrobotics.com/)                         | [`revrobotics`](https://github.com/frc5024/lib5k/tree/master/lib5k/src/main/java/io/github/frc5024/lib5k/hardware/revrobotics)     | Provides "smart" motor controllers, rotary encoders, pneumatic pressure sensors, and LED controllers. |
| [Kauai Labs](https://www.kauailabs.com/) | [`kauai`](https://github.com/frc5024/lib5k/tree/master/lib5k/src/main/java/io/github/frc5024/lib5k/hardware/kauai)           | Provides IMU devices, and co-processors.                                                              |
| [National Instruments](https://www.ni.com/en-ca.html)                 | [`ni`](https://github.com/frc5024/lib5k/tree/master/lib5k/src/main/java/io/github/frc5024/lib5k/hardware/ni)              | Provides the RoboRIO, cRIO, and core hardware libraries.                                              |
| [Limelight Vision](https://limelightvision.io/)                     | [`limelightvision`](https://github.com/frc5024/lib5k/tree/master/lib5k/src/main/java/io/github/frc5024/lib5k/hardware/limelightvision) | Provides high-end all-in-one computer vision tools and hardware.                                      |

Some issues and incompatibilities come from having multiple vendors that provide similar products. For example: while functionally the same, Rev Robotics and CTRE provide software libraries for controlling their motors that differ wildly. CTRE's library is mostly raw bindings to C++ code that seems to have been ported from their C# library for use in their own device ecosystem, where Rev Robotics has a library that functions much like a standard Java library, heavily object oriented.

To combat these problems, Lib5K extends all classes provided by these vendors, and binds key functionality to a common interface. This can be seen with the [`TalonEncoder`](https://cs.5024.ca/lib5k/javadoc/io/github/frc5024/lib5k/hardware/ctre/sensors/TalonEncoder.html) and [`SparkMaxEncoder`](https://cs.5024.ca/lib5k/javadoc/io/github/frc5024/lib5k/hardware/revrobotics/sensors/SparkMaxEncoder.html) classes, where we bind vendor-specific implementations to our [`CommonEncoder`](https://cs.5024.ca/lib5k/javadoc/io/github/frc5024/lib5k/hardware/common/sensors/interfaces/CommonEncoder.html) interface.

## Hardware access

Java and the RoboRIO are not designed to work together, which leads us to the complex system that is used to run Java code on the RoboRIO. The RoboRIO is a National Instruments robotics controller, and since this is a National Instruments device, it is really only designed to run [LabVIEW](https://en.wikipedia.org/wiki/LabVIEW). Luckily, NI provides tooling to interface with the RoboRIO's on-board FPGA from C. Paired with the `FRCNetComm` library provided for communication with DriverStation, WPILib has created a hardware abstraction library, simply called [`hal`](https://github.com/wpilibsuite/allwpilib/tree/master/hal). HAL is then exposed to Java via the [Java Native Interface](https://en.wikipedia.org/wiki/Java_Native_Interface), where we can use it in our library.