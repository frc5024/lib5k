package frc.lib5k.components;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.lib5k.utils.FileUtils;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;

/**
 * Automatic camera streaming. Because I am too lazy to type this stuff out
 * manually every time I start a new project.
 * <p>
 * This should be re-implemented with h.264 streaming before 2020...
 * <i>FIRST</i> needs some better networking gear. I have a gigabit link
 * internally in the robot, and our shop also happens to have.. Guess what? A
 * gigabit wifi link! Come on guys.. It's 2019. Give me something more than
 * "Maybe 4mb/s".
 * <p>
 * For now, we are stuck with the mess that is MJPG.. Or offload video encoding,
 * which would be a great idea if people would actually respect the hardware
 * (I'm talking to you, whoever hot glued my raspberry PI to a piece of
 * cardboard, then zip-tied it to some lexan). I understand that nobody could
 * find a case for it, but 1. there where 6 weeks to buy one, and 2. you have
 * seen me drive a robot right? If all 6 wheels are touching the ground or it
 * isn't drifting, it's not me.
 * <p>
 * If you actually took the time to read this, I think you either need a break,
 * or you should go find yourself a task to work on.
 * <p>
 * Here is a blog post talking about H.264:
 * https://rianadon.github.io/blog/2019/04/04/guide-to-h264-streaming-frc.html
 */
public class AutoCamera {
    static int m_cameraCount = 0;
    RobotLogger logger = RobotLogger.getInstance();
    UsbCamera m_UsbCamera;
    MjpegServer m_cameraServer;

    public AutoCamera() {
        this("Unnamed (Automatic) Camera", 0);
    }

    public AutoCamera(int usb_slot) {
        this("Unnamed Camera", usb_slot);
    }

    public AutoCamera(String name, int usb_slot) {
        // Create a USBCamera
        m_UsbCamera = CameraServer.getInstance().startAutomaticCapture(name, usb_slot);
        m_UsbCamera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320, 240, 15);

        // Add self to shuffleboard
        Shuffleboard.getTab("DriverStation").add(m_UsbCamera);
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

}