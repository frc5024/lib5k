package io.github.frc5024.lib5k.hardware.generic.sensors;

import edu.wpi.first.hal.SimBoolean;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import io.github.frc5024.lib5k.control_loops.SlewLimiter;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;

public class GenericEncoder extends Encoder implements CommonEncoder, EncoderSimulation {

    private int cpr;
    private boolean phase;

    /* Simulation vars */
    private SpeedController controller;
    private double last_time;
    private double gearbox_ratio, max_rpm;

    /* Simulation */
    private SimDevice m_simDevice;
    private SimDouble m_simTicks;
    private SimDouble m_simRotations;
    private SimDouble m_simVelocity;
    private SimBoolean m_simInverted;
    private static int s_instanceCount = 0;
    private SlewLimiter m_simSlew;

    /**
     * Encoder constructor. Construct a Encoder given a and b channels.
     *
     * <p>
     * The encoder will start counting immediately.
     *
     * @param channelA         The a channel DIO channel. 0-9 are on-board, 10-25
     *                         are on the MXP port
     * @param channelB         The b channel DIO channel. 0-9 are on-board, 10-25
     *                         are on the MXP port
     * @param reverseDirection represents the orientation of the encoder and inverts
     *                         the output values if necessary so forward represents
     *                         positive values.
     */
    public GenericEncoder(final int channelA, final int channelB, boolean reverseDirection, int cpr) {
        super(channelA, channelB, reverseDirection);
        setCPR(cpr);
    }

    /**
     * Encoder constructor. Construct a Encoder given a and b channels.
     *
     * <p>
     * The encoder will start counting immediately.
     *
     * @param channelA The a channel digital input channel.
     * @param channelB The b channel digital input channel.
     */
    public GenericEncoder(final int channelA, final int channelB, int cpr) {
        super(channelA, channelB);
        setCPR(cpr);
    }

    /**
     * Encoder constructor. Construct a Encoder given a and b channels.
     *
     * <p>
     * The encoder will start counting immediately.
     *
     * @param channelA         The a channel digital input channel.
     * @param channelB         The b channel digital input channel.
     * @param reverseDirection represents the orientation of the encoder and inverts
     *                         the output values if necessary so forward represents
     *                         positive values.
     * @param encodingType     either k1X, k2X, or k4X to indicate 1X, 2X or 4X
     *                         decoding. If 4X is selected, then an encoder FPGA
     *                         object is used and the returned counts will be 4x the
     *                         encoder spec'd value since all rising and falling
     *                         edges are counted. If 1X or 2X are selected then a
     *                         m_counter object will be used and the returned value
     *                         will either exactly match the spec'd count or be
     *                         double (2x) the spec'd count.
     */
    public GenericEncoder(final int channelA, final int channelB, boolean reverseDirection,
            final EncodingType encodingType, int cpr) {
        super(channelA, channelB, reverseDirection, encodingType);
        setCPR(cpr);
    }

    /**
     * Encoder constructor. Construct a Encoder given a and b channels. Using an
     * index pulse forces 4x encoding
     *
     * <p>
     * The encoder will start counting immediately.
     *
     * @param channelA         The a channel digital input channel.
     * @param channelB         The b channel digital input channel.
     * @param indexChannel     The index channel digital input channel.
     * @param reverseDirection represents the orientation of the encoder and inverts
     *                         the output values if necessary so forward represents
     *                         positive values.
     */
    public GenericEncoder(final int channelA, final int channelB, final int indexChannel, boolean reverseDirection,
            int cpr) {
        super(channelA, channelB, indexChannel, reverseDirection);
        setCPR(cpr);
    }

    /**
     * Encoder constructor. Construct a Encoder given a and b channels. Using an
     * index pulse forces 4x encoding
     *
     * <p>
     * The encoder will start counting immediately.
     *
     * @param channelA     The a channel digital input channel.
     * @param channelB     The b channel digital input channel.
     * @param indexChannel The index channel digital input channel.
     */
    public GenericEncoder(final int channelA, final int channelB, final int indexChannel, int cpr) {
        super(channelA, channelB, indexChannel);
        setCPR(cpr);
    }

    /**
     * Encoder constructor. Construct a Encoder given a and b channels as digital
     * inputs. This is used in the case where the digital inputs are shared. The
     * Encoder class will not allocate the digital inputs and assume that they
     * already are counted.
     *
     * <p>
     * The encoder will start counting immediately.
     *
     * @param sourceA          The source that should be used for the a channel.
     * @param sourceB          the source that should be used for the b channel.
     * @param reverseDirection represents the orientation of the encoder and inverts
     *                         the output values if necessary so forward represents
     *                         positive values.
     */
    public GenericEncoder(DigitalSource sourceA, DigitalSource sourceB, boolean reverseDirection, int cpr) {
        super(sourceA, sourceB, reverseDirection);
        setCPR(cpr);
    }

    /**
     * Encoder constructor. Construct a Encoder given a and b channels as digital
     * inputs. This is used in the case where the digital inputs are shared. The
     * Encoder class will not allocate the digital inputs and assume that they
     * already are counted.
     *
     * <p>
     * The encoder will start counting immediately.
     *
     * @param sourceA The source that should be used for the a channel.
     * @param sourceB the source that should be used for the b channel.
     */
    public GenericEncoder(DigitalSource sourceA, DigitalSource sourceB, int cpr) {
        super(sourceA, sourceB);
        setCPR(cpr);
    }

    /**
     * Encoder constructor. Construct a Encoder given a and b channels as digital
     * inputs. This is used in the case where the digital inputs are shared. The
     * Encoder class will not allocate the digital inputs and assume that they
     * already are counted.
     *
     * <p>
     * The encoder will start counting immediately.
     *
     * @param sourceA          The source that should be used for the a channel.
     * @param sourceB          the source that should be used for the b channel.
     * @param reverseDirection represents the orientation of the encoder and inverts
     *                         the output values if necessary so forward represents
     *                         positive values.
     * @param encodingType     either k1X, k2X, or k4X to indicate 1X, 2X or 4X
     *                         decoding. If 4X is selected, then an encoder FPGA
     *                         object is used and the returned counts will be 4x the
     *                         encoder spec'd value since all rising and falling
     *                         edges are counted. If 1X or 2X are selected then a
     *                         m_counter object will be used and the returned value
     *                         will either exactly match the spec'd count or be
     *                         double (2x) the spec'd count.
     */
    public GenericEncoder(DigitalSource sourceA, DigitalSource sourceB, boolean reverseDirection,
            final EncodingType encodingType, int cpr) {
        super(sourceA, sourceB, reverseDirection, encodingType);
        setCPR(cpr);
    }

    /**
     * Encoder constructor. Construct a Encoder given a, b and index channels as
     * digital inputs. This is used in the case where the digital inputs are shared.
     * The Encoder class will not allocate the digital inputs and assume that they
     * already are counted.
     *
     * <p>
     * The encoder will start counting immediately.
     *
     * @param sourceA          The source that should be used for the a channel.
     * @param sourceB          the source that should be used for the b channel.
     * @param indexSource      the source that should be used for the index channel.
     * @param reverseDirection represents the orientation of the encoder and inverts
     *                         the output values if necessary so forward represents
     *                         positive values.
     */
    public GenericEncoder(DigitalSource sourceA, DigitalSource sourceB, DigitalSource indexSource,
            boolean reverseDirection, int cpr) {
        super(sourceA, sourceB, indexSource, reverseDirection);
        setCPR(cpr);
    }

    /**
     * Encoder constructor. Construct a Encoder given a, b and index channels as
     * digital inputs. This is used in the case where the digital inputs are shared.
     * The Encoder class will not allocate the digital inputs and assume that they
     * already are counted.
     *
     * <p>
     * The encoder will start counting immediately.
     *
     * @param sourceA     The source that should be used for the a channel.
     * @param sourceB     the source that should be used for the b channel.
     * @param indexSource the source that should be used for the index channel.
     */
    public GenericEncoder(DigitalSource sourceA, DigitalSource sourceB, DigitalSource indexSource, int cpr) {
        super(sourceA, sourceB, indexSource);
        setCPR(cpr);
    }

    private void setCPR(int cpr) {

        // Set CPR
        this.cpr = cpr;

        // Set distance per pulse to 1/cpr. This makes *cpr* pulses equal to 1 unit (or
        // 1 rotation)
        super.setDistancePerPulse(1 / cpr);
    }

    @Override
    public void initSimulationDevice(SpeedController controller, double gearbox_ratio, double max_rpm,
            double ramp_time) {
        // Set locals
        this.controller = controller;
        this.gearbox_ratio = gearbox_ratio;
        this.max_rpm = max_rpm;
        this.m_simSlew = new SlewLimiter(ramp_time);

        // Init sim device
        m_simDevice = SimDevice.create("GenericEncoder", s_instanceCount + 1);

        if (m_simDevice != null) {
            m_simTicks = m_simDevice.createDouble("Ticks", false, 0.0);
            m_simRotations = m_simDevice.createDouble("Rotations", true, 0.0);
            m_simVelocity = m_simDevice.createDouble("RPM", true, 0.0);
            m_simInverted = m_simDevice.createBoolean("Inverted", true, false);
        }

        // Move to next instance
        s_instanceCount++;

    }

    @Override
    public void update() {
        // Handle simulation updates
        if (m_simDevice != null) {
            // If this is the first loop, simply re-set the timer, and skip
            if (last_time == 0) {
                last_time = FPGAClock.getFPGASeconds();
                return;
            }

            // Determine dt
            double current_time = FPGAClock.getFPGASeconds();
            double dt = current_time - last_time;
            last_time = current_time;

            // Calc encoder position
            double rpm = (m_simSlew.feed(controller.get()) * max_rpm) / gearbox_ratio;
            double revs = (rpm / 60.0) * dt; // RPM -> RPS -> Multiply by seconds to find rotations since last update
            m_simTicks.set((int) (m_simTicks.get() + (revs * cpr)));
            m_simRotations.set((m_simRotations.get() + revs));
            m_simVelocity.set(rpm);
        }

    }

    @Override
    public void setPhaseInverted(boolean inverted) {
        // Handle simulation vs reality
        if (m_simDevice != null) {
            m_simInverted.set(inverted);
        } else {
            setReverseDirection(inverted);
            this.phase = inverted;
        }

    }

    @Override
    public boolean getInverted() {
        // Handle simulation
        if (m_simDevice != null) {
            return m_simInverted.get();
        }
        return this.phase;
    }

    @Override
    public double getPosition() {
        // Handle simulation
        if (m_simDevice != null) {
            return m_simTicks.get();
        }
        return super.get();
    }

    @Override
    public double getVelocity() {
        // Handle simulation
        if (m_simDevice != null) {
            return m_simVelocity.get();
        }
        return super.getRate();
    }

}