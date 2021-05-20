# Computer Vision

5024 makes heavy use of [computer vision](https://en.wikipedia.org/wiki/Computer_vision) in our software. Cameras are great sensors for interacting with the physical world. This document is an overview of how we handle computer vision in Lib5K. At 5024, all of our vision processing happens off-board on co-processors such as raspberry pis, laptops, and primarily [LimeLight](https://limelightvision.io/) products. 

## LimeLight client code

In Lib5K, interacting with a limelight can be done through the [`LimeLight1`](/lib5k/javadoc/io/github/frc5024/lib5k/hardware/limelightvision/products/LimeLight1.html) and [`LimeLight2`](/lib5k/javadoc/io/github/frc5024/lib5k/hardware/limelightvision/products/LimeLight2.html) classes for the LimeLight version 1 and version 2/2+ respectively. The [`BaseLimeLight`](/lib5k/javadoc/io/github/frc5024/lib5k/hardware/limelightvision/BaseLimeLight.html) class contains all common code between the two. These classes follow the [LimeLight API spec](https://docs.limelightvision.io/en/latest/networktables_api.html).

## Vision targets

All vision targets extend the [`Contour`](/lib5k/javadoc/io/github/frc5024/lib5k/vision/types/Contour.html) base class. When interacting with vision targets in code, we generally do math on their [bounding boxes](https://en.wikipedia.org/wiki/Minimum_bounding_box#Axis-aligned_minimum_bounding_box). In Lib5K, the [`AxisAlignedBoundingBox`](/lib5k/javadoc/io/github/frc5024/lib5k/vision/types/AxisAlignedBoundingBox.html) class is used to represent these boxes. Bounding boxes use their own coordinate system, that is not the same as the robot coordinate system. All coordinates are expressed between -1 and 1, where `(-1, 1)` is the top left corner of the image frame, and `(1, -1)` is the bottom right corner. This means that `(0, 0)` is the exact centre of the image frame.

Since these coordinates will refer to different real-world positions based on the type of camera lense & amount of distortion of the camera, we provide another vision target class (this is what the LimeLights return). The [`HyperbolicAxisAlignedBoundingBox`](/lib5k/javadoc/io/github/frc5024/lib5k/vision/types/HyperbolicAxisAlignedBoundingBox.html) is a [non-euclidean](https://en.wikipedia.org/wiki/Non-Euclidean_geometry) `AxisAlignedBoundingBox` that is represented in [hyperbolic space](https://en.wikipedia.org/wiki/Hyperbolic_geometry). In simple terms, you can imagine drawing a rectangle on the surface of a sphere, then looking at it from inside the sphere at the center. This rectangle will have the following properties:

 - An X angle relative to where you are facing
 - A Y angle relative to where you are facing
 - An angle for each edge, relative to where you are facing

The reason we represent vision targets in hyperbolic space is to make using them much easier. Let's say you needed to write some code that rotates a robot to face the center of the nearest vision target. All you would need to do is feed the target's X angle into a PID controller, and the output of the controller into the drivetrain. If you wanted to rotate to an edge of the target, just use the angle of that edge instead. Here is a short pseudo-code example:

```java
// Camera and drivetrain
LimeLight2 limelight;
DualPIDTankDriveTrain drivetrain;

// Configure the camera
limelight.setLEDMode(LimeLightLEDMode.ON);
limelight.setOperationMode(LimeLightOperationMode.VISION);
limelight.setStreamMode(LimeLightStreamMode.PIP_MAIN);

// Always face the nearest target
while (true) {
    if (limelight.hasTarget()) {

        // Feed the target X angle into the drivetrain
        drivetrain.setGoalHeading(limelight.getXRotation(), Rotation2d.fromDrgrees(5.0));

    }
}
```