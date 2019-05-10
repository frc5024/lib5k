package frc.common.wrappers;

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
        try {
            String config = FileUtils.readFile(filepath);
            this.camera.setConfigJson(config);
        } catch (Exception e) {
            System.out.println("WARNING: Unable to load camera config file: " + filepath);
        }  
    }

    /**
     * Set the camera connection mode
     * 
     * @param enabled Should the camera always be streaming video?
     */
    public void keepCameraAwake(boolean enabled) {
        if (enabled) {
            this.camera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
        } else {
            this.camera.setConnectionStrategy(ConnectionStrategy.kAutoManage);
        }
    }
}