package io.github.frc5024.lib5k.hardware.generic.cameras;

import edu.wpi.first.cscore.MjpegServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoMode;
import edu.wpi.first.cscore.VideoSource;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.RobotBase;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.lib5k.utils.FileUtils;

/**
 * Automatic camera streaming.
 */
public class AutoCamera {
    static int m_cameraCount = 0;
    RobotLogger logger = RobotLogger.getInstance();
    UsbCamera m_UsbCamera;
    MjpegServer m_cameraServer;
    
    /**
     * Create a dynamic auto camera
     */
    public AutoCamera() {
        this("Unnamed (Automatic) Camera", 0);
    }

    /**
     * Connect to a camera in a specific USB slot
     * 
     * @param usb_slot USB device number (/dev/video[number])
     */
    public AutoCamera(int usb_slot) {
        this("Unnamed Camera", usb_slot);
    }

    /**
     * Connect to a camera in a specific USB slot
     * 
     * @param name     Camera name
     * @param usb_slot USB device number (/dev/video[number])
     */
    public AutoCamera(String name, int usb_slot) {
        // Create a USBCamera
        m_UsbCamera = CameraServer.getInstance().startAutomaticCapture(name, usb_slot);
        // m_UsbCamera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320, 240, 15);
    }

    /**
     * Set camera visible
     * 
     * @param show Should the video feed be visible?
     */
    public void showCamera(boolean show) {
        if (RobotBase.isReal()) {
            if (show) {
                m_UsbCamera.setExposureAuto();
            } else {
                m_UsbCamera.setExposureHoldCurrent();
                m_UsbCamera.setExposureManual(10);
                m_UsbCamera.setExposureManual(0);
            }
        }
    }

    /**
     * Set the camera resolution and framerate
     * 
     * @param height Height in pixels
     * @param width  Width in pixels
     * @param fps    Frames per second
     */
    public void setResolution(int height, int width, int fps) {
        m_UsbCamera.setVideoMode(VideoMode.PixelFormat.kMJPEG, height, width, fps);
        logger.log(m_UsbCamera.getName() + "'s resolution set to " + width + "x" + height, Level.kLibrary);
    }

    /**
     * Load a json file as a config
     * 
     * @param filepath Path to json file
     */
    public void loadJsonConfig(String filepath) {

        try {
            String config = FileUtils.readFile(filepath);

            m_UsbCamera.setConfigJson(config);

            logger.log(m_UsbCamera.getName() + "'s config has been loaded from: " + filepath, Level.kLibrary);
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

        m_UsbCamera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
        logger.log(m_UsbCamera.getName() + "'s connection mode has been set to: " + strategy_string, Level.kLibrary);
    }

    /**
     * Get the camera feed object
     * 
     * @return Camera VideoSource
     */
    public VideoSource getFeed() {
        return m_UsbCamera;
    }

}