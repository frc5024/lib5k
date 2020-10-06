# The 5024 Coordinate System

When navigating the FRC field, it is important to know where everything is. The Raider Robotics Software Development team uses a special coordinate to express on-field positions. It is important to note that our coordinate system does not match the system used by WPILib and by many other teams.

## How our system works

All on-field positions (aka. Poses) are expressed as a 3-component vector:
```
[X, Y, θ]
```

Where `X` and `Y` are 2-dimensional coordinates in meters, and `θ` is a heading in radians.

![Diagram of coordinate system](https://i.imgur.com/xPmW4Z0.png)

*Diagram of coordinate system*

When working with coordinates, we always assume that during autonomous, our robot starts in the red alliance's side of the field above.

All poses are written in code using WPILib's [Pose2D](https://first.wpi.edu/FRC/roborio/release/docs/java/edu/wpi/first/wpilibj/geometry/Pose2d.html) object.

## Benefits of placing 0y in the field centre

In FRC games like [PowerUP](https://en.wikipedia.org/wiki/FIRST_Power_Up) and [DeepSpace](https://en.wikipedia.org/wiki/Destination:_Deep_Space), the field is perfectly mirrored, so we only need to write and test autonomous paths for one side of the field, and we can multiply `Y` and `θ` by `-1` to use the paths on the opposite side.

## Converting a PathWeaver coordinate to a 5024 coordinate

Due to the fact WPILib's coordinate system places (0,0) in the upper left of the field, to convert to the 5024 coordinate system, you must add half the field width in meters to the `Y` component. With the current field layout, this works out to `(8.23 / 2.0) = 4.115` meters, but the field size changes all the time. Any time the size changes, make sure to edit [the `PathImporter`](https://github.com/frc5024/lib5k/blob/master/purepursuit/src/main/java/io/github/frc5024/purepursuit/pathgen/PathImporter.java#L51) to update the field width number.