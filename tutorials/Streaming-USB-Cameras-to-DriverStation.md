# Streaming a Camera Feed to DriverStation

To simplify the process of streaming a camera's output to driverstation, we have build AutoCamera. AutoCamera is a wrapper for USB camera devices plugged directly into the RoboRIO that will handle configuration, management, and publishing automatically.

## Connecting to a camera
To connect a USB camera to an AutoCamera object, the camera must be plugged in to one of the RoboRIO's two USB ports, or a USB splitter.

```java
// Create an AutoCamera with only 1 webcam connected to the RoboRIO
// AutoCamera will ask WPIlib to find the device. If more than one camera is plugged in, the first camera to turn on will be the selected device.
AutoCamera myCamera = new AutoCamera();
// Create an AutoCamera with a specific USB port number (these are actually unix video device IDs. Where device n is /dev/video<n>)
AutoCamera myCamera = new AutoCamera(0);
// Create an AutoCamera with a specific USB port and name
AutoCamera myCamera = new AutoCamera("Fancy Camera", 0);
```

Calling any of these will connect to the specified camera, configure the video feed to *240x320px* at *15fps*, and display the feed on [Shuffleboard](https://frc-docs.readthedocs.io/en/latest/docs/software/wpilib-tools/shuffleboard/index.html).

## Configuring the video feed
*240x320px* at *15fps* may not be your desired video stream parameters. These can be changed via the `setResolution` method.
```java
// Configure the camera to run at 800x600px and 30fps
myCamera.setResolution(600,800,30);
```


## Loading configuration files
Camera configuration files can be generated via the web interface (see below). These files contain static values for each camera parameter (we use this to configure our cameras for each venue). To configure a camera with a file, use the `loadJsonConfig` method.
```java
// Load a camera config stored at /home/lvuser/deploy/myCamera.json
myCamera.loadJsonConfig("/home/lvuser/deploy/myCamera.json");
```

## Keeping the camera feed alive
When a camera feed is not being watched, it will shut down to save bandwidth. A downside is that, when viewed after shut down, the feed will take a few seconds to start up again. To prevent this, we can tell the camera to continue streaming when it's not being watched.
```java
// Tell the camera to keep streaming
myCamera.keepCameraAwake(true);
```