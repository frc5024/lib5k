---
layout: default
---

# AsyncDigitalInput Class Documentation

`AsyncDigitalInput` is fully cross-compatible with WPILib's `DigitalInput`. This class just adds two callbacks to any digital input device:

 - Trigger
   - This callback is called once every time voltage is applied to the specified digital pin
 - Release
   - This callback is called once every time voltage is removed from the specified digital pin

## Example
```java
ADIExample(){
    // Create an input on pin 0
    AsyncDigitalInput input = new AsyncDigitalInput(0);

    // Register callbacks
    input.registerTriggerCallback(this::onTrigger);
    input.registerReleaseCallback(this::onRelease);
}

// This will be run whenever the input is triggered
void onTrigger(){
    RobotLogger.getInstance().log("ADIExample", "Digital input was triggered");
}

// This will be run whenever the input is released
void onRelease(){
    RobotLogger.getInstance().log("ADIExample", "Digital input was released");
}
```