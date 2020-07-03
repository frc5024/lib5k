---
layout: default
---

# AsyncADXRS450_Gyro Class Documentation

`AsyncADXRS450_Gyro` is fully cross-compatible with WPILib's `ADXRS450_Gyro`. This class adds two callbacks to any ADXRS450 device attached over [SPI](https://en.wikipedia.org/wiki/Serial_Peripheral_Interface):

 - Angle
   - This callback is fed the gyro's angle, any time a change is detected
 - Motion
   - This callback is called whenever motion is detected over a configurable threshold
   - Useful for detecting being hit while aiming

## Example
```java
AGyroExample(){
    // Connect to the Gyro via SPI
    AsyncADXRS450_Gyro gyro = new AsyncADXRS450_Gyro();

    // Register a callback for changing angle
    gyro.registerAngleCallback(this::onAngleChange);

    // Register a callback for motion
    // Define anything over 2 degrees per ms as "motion"
    // This number is probably too big for IRL use, but is fine for demonstration
    gyro.registerMotionCallback(this::onMotion, 2.0);
}

// This is run whenever the angle changes
void onAngleChange(double angle){
    RobotLogger.getInstance().log("AGyroExample", String.format("Angle changed to %.2f", angle));
}

// This is run whenever motion is detected
void onMotion(){
    RobotLogger.getInstance().log("AGyroExample", "Motion detected");
}
```