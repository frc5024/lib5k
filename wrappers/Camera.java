package frc.common.wrappers;

import java.util.logging.Logger;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;

import frc.common.utils.FileUtils;

/**
 * Stream a USB camera feed over http to a driverstation
 */
public class Camera {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    UsbCamera camera;
    MjpegServer camera_server;

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
        logger.info(name +" initalized on TCP port " + http_port);
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
        logger.info(name +" (USB port "+ usb_port +") initalized on TCP port " + http_port);
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
            logger.info("Camera config has been loaded from: " + filepath);
        } catch (Exception e) {
            logger.warning("Unable to load camera config file: " + filepath);
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
        logger.info("Camera connection mode has been set to: "+ strategy_string);
    }
}