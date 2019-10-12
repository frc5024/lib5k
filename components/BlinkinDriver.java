package frc.lib5k.components;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * A wrapper for the REV Blinkin LED driver <br>
 * http://www.revrobotics.com/rev-11-1105/
 */
public class BlinkinDriver extends SendableBase {

    public enum LEDSetting {
        RED(0.61),
        // kRED(0.61),
        // kRED(0.61),
        // kRED(0.61),
        kShotWhite(-0.81),
        kStrobeBlue(-0.09),
        kStrobeRed(-0.11),
        kChase(-0.29),
        kGreen(0.77), kOrange(0.65), kOff(0.99), BLUE(0.61);

        private double m_pwmVal;

        LEDSetting(double pwm_val) {
            m_pwmVal = pwm_val;
        }

        public double get() {
            return m_pwmVal;
        }

    }

    Spark m_controller;

    /**
     * A wrapper for the REV Blinkin LED driver
     * 
     * @param pwm_port The PWM channel for the deivce
     */
    public BlinkinDriver(int pwm_port) {
        m_controller = new Spark(pwm_port);
    }

    /**
     * Set the LED driver to a specific setting
     * 
     * @param setting LED pattern
     */
    public void set(LEDSetting setting) {
        m_controller.set(setting.get());
    }

    /**
     * This method handles interaction with some internal WPIlib services
     */
    @Override
    public void initSendable(SendableBuilder builder) {
        // TODO Auto-generated method stub

    }

}