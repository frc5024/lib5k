package frc.common.wrappers;

import frc.common.utils.RobotLogger;
import frc.common.utils.RobotLogger.Level;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSink;
import frc.common.utils.FileUtils;
import frc.common.network.NetworkTables;

/**
 * Stream a USB camera feed over http to a driverstation
 */
public class Camera {
    private final RobotLogger logger = RobotLogger.getInstance();

    UsbCamera camera;
    MjpegServer camera_server;
    NetworkTables nt;

    String name;

    /**
     * Initalize with basic params
     * 
     * @param name Friendly name of camera
     * @param http_port TCP port for camera stream. MUST be from 1181 to 1191
     */
    public Camera(String name, int http_port) {
        this.camera = CameraServer.getInstance().startAutomaticCapture();
        this.camera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320, 240, 15);
        this.camera_server = new MjpegServer(name, http_port);
        this.camera_server.setSource(this.camera);
        System.out.println(name + " initialized on TCP port " + http_port);

        this.name = name;
        initNT();
    }

    /**
     * Initalize with basic params
     * 
     * @param name Friendly name of camera
     * @param http_port TCP port for camera stream. MUST be from 1181 to 1191
     * @param usb_port USB port (starting from 0)
     */
    public Camera(String name, int http_port, int usb_port) {
        this.camera = CameraServer.getInstance().startAutomaticCapture(usb_port);
        this.camera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320, 240, 15);
        this.camera_server = new MjpegServer(name, http_port);
        this.camera_server.setSource(this.camera);
        System.out.println(name + " (USB port " + usb_port + ") initialized on TCP port " + http_port);

        this.name = name;
        initNT();
    }

    private void initNT() {
        this.nt = NetworkTables.getInstance();

        nt.getEntry("cameras/" + name, "alive").setBoolean(true);
        nt.getEntry("cameras/" + name, "isVisionMode").setBoolean(false);
    }

    /**
     * Set the camera resolution and framerate
     * 
     * @param height Height in pixels
     * @param width Width in pixels
     * @param fps Frames per second
     */
    public void setResolution(int height, int width, int fps) {
        this.camera.setVideoMode(VideoMode.PixelFormat.kMJPEG, height, width, fps);
        logger.log(name +"'s resolution set to " + width + "x" + height, Level.kLibrary);
    }

    /**
     * Load a json file as a config
     * 
     * @param filepath Path to json file
     */
    public void loadJsonConfig(String filepath) {
        //Temp filepath fix for @ewpratten's computer
        if (filepath == "/home/ewpratten/frc/MiniBot/deploy/maincamera.json") {
            filepath = "/home/ewpratten/frc/MiniBot/src/main/deploy/maincamera.json";
        }

        try {
            String config = FileUtils.readFile(filepath);
            this.camera.setConfigJson(config);
            logger.log(name +"'s config has been loaded from: " + filepath, Level.kLibrary);
        } catch (Exception e) {
            logger.log("Unable to load camera config file: " + filepath, Level.kWarning);
        }  
    }

    /**
     * Set the camera connection mode
     * 
     * @param enabled Should the camera always be streaming video?
     */
    public void keepCameraAwake(boolean enabled) {
        ConnectionStrategy strategy = enabled ? ConnectionStrategy.kKeepOpen : ConnectionStrategy.kAutoManage;
        String strategy_string = enabled ? "Stay Awake" : "Auto Manage";

        this.camera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
        logger.log(name + "'s connection mode has been set to: " + strategy_string, Level.kLibrary);
    }
    
    public UsbCamera getCameraSever() {
        return camera;
    }
}