package io.github.frc5024.common_drive;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.common_drive.calculation.DifferentialDriveCalculation;
import io.github.frc5024.lib5k.utils.InputUtils;
import io.github.frc5024.lib5k.utils.InputUtils.ScalingMode;
import io.github.frc5024.common_drive.controller.PDFController;
import io.github.frc5024.common_drive.controller.PIFController;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.common_drive.queue.DriveTrainOutput;
import io.github.frc5024.common_drive.queue.DriveTrainSensors;
import io.github.frc5024.common_drive.queue.WriteLock;
import io.github.frc5024.common_drive.types.ChassisSide;
import io.github.frc5024.common_drive.types.MotorMode;
import io.github.frc5024.common_drive.types.ShifterType;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;

/**
 * The base for all drivetrains
 */
public abstract class DriveTrainBase extends SubsystemBase {

    private Consumer<String> loggingHook = null;

    // Configuration
    private DriveTrainConfig config;

    // Active side
    private ChassisSide activeSide = ChassisSide.kPrimary;

    // States & machine
    public enum States {
        kOpenLoop, // Open-loop control
        kPivoting, // In-place pivot control
        kRabbitChase, // "chasing" a point in space
        kLocked, // Resisting external forces
        kNeutral, // Allowing external forces to push robot around
    }

    private StateMachine<States> stateMachine;

    // Thread & timekeeping
    private Notifier thread;
    private double lastTime;

    // I/O statuses
    private DriveTrainSensors lastInput;
    private DriveTrainSensors input;
    private DriveTrainSensors inputOffset;
    private DriveTrainOutput output;

    // Encoder rates
    private double leftEncoderMPS;
    private double rightEncoderMPS;

    // Goals
    private double throttleGoal;
    private double turnGoal;
    private double epsilon;
    private Rotation2d pivotGoal = new Rotation2d();
    private Translation2d absPositionGoal = new Translation2d();
    private WriteLock<Gear> gearGoal;

    // Localization
    private DifferentialDriveOdometry localizer;

    // Controllers
    private PIFController turnController;
    private PDFController distanceController;

    // Defaults
    private Gear defaultGear;

    public DriveTrainBase(DriveTrainConfig config) {

        // Set config
        this.config = config;

        // Set the gear goal to the default gear
        this.defaultGear = (this.config.defaultHighGear) ? Gear.HIGH : Gear.LOW;
        this.gearGoal = new WriteLock<>(this.defaultGear);

        // Configure state machine
        this.stateMachine = new StateMachine<>("DriveTrainBase States");
        this.stateMachine.setDefaultState(States.kOpenLoop, this::handleOpenLoopControl);
        this.stateMachine.addState(States.kPivoting, this::handlePivotControl);
        this.stateMachine.addState(States.kRabbitChase, this::handleRabbitChase);
        this.stateMachine.addState(States.kLocked, this::handleLocked);
        this.stateMachine.addState(States.kNeutral, this::handleNeutral);

        // Set up localization
        this.localizer = new DifferentialDriveOdometry(new Rotation2d());

        // Set up controllers
        this.turnController = new PIFController(this.config.turningGains, true);
        this.distanceController = new PDFController(this.config.distanceGains, true);

        // Set up some empty status frames
        this.lastInput = new DriveTrainSensors();
        this.input = new DriveTrainSensors();
        this.inputOffset = new DriveTrainSensors();
        this.output = new DriveTrainOutput();

        // Set up thread (DO THIS LAST)
        this.thread = new Notifier(this::runIteration);
        this.thread.startPeriodic((double) this.config.dt_ms * 0.001);

    }

    /**
     * Zero all sensor readings (no-destructive to other subsystems)
     */
    public void zero() {
        log("Setting sensor offsets");
        this.inputOffset = input.copy();
        log("Resetting localization to [0,0,0]");
        this.localizer.resetPosition(new Pose2d(), getAdjustedInputs().rotation);
        this.turnController.reset();
        this.distanceController.reset();
    }

    /**
     * Stop everything
     */
    public void stop() {
        log("Stopped DriveTrain");
        drive(0.0, 0.0, ScalingMode.LINEAR);
        this.turnController.reset();
        this.distanceController.reset();
    }

    /**
     * Run a thread iteration
     */
    protected void runIteration() {

        // Read sensors
        this.lastInput = getAdjustedInputs();
        this.input = this.readInputs();
        this.extrapolateSensors();

        // Run any tasks that need to happen after 1 sensor reading
        if (this.lastTime == 0.0) {

            // Re-init localization with proper gyro angle
            this.localizer = new DifferentialDriveOdometry(getAdjustedInputs().rotation);

        }

        this.lastTime = FPGAClock.getFPGAMilliseconds();

        // Reset outputs
        this.output.zero();

        // Update state machine
        this.stateMachine.update();

        // Write outputs
        this.output.timestamp_ms = FPGAClock.getFPGAMilliseconds();
        this.consumeOutputs(this.output);

    }

    /**
     * This grabs extra information by referencing sensor data over time
     */
    private void extrapolateSensors() {

        // Get the inputs
        DriveTrainSensors adjustedInputs = getAdjustedInputs();

        // Find DT between data
        double dt = adjustedInputs.timestamp_ms - this.lastInput.timestamp_ms;

        // Calculate encoder rates
        this.leftEncoderMPS = (adjustedInputs.leftEncoderMetres - this.lastInput.leftEncoderMetres) / dt;
        this.rightEncoderMPS = (adjustedInputs.rightEncoderMetres - this.lastInput.rightEncoderMetres) / dt;

        // Update localization
        this.localizer.update(adjustedInputs.rotation, adjustedInputs.leftEncoderMetres,
                adjustedInputs.rightEncoderMetres);

    }

    /**
     * Handler for open-loop control
     * 
     * @param meta Statemachine metadata
     */
    private void handleOpenLoopControl(StateMetadata<States> meta) {
        if (meta.isFirstRun()) {
            log("Switched to open-loop control");

            // Send motor mode command
            this.output.motorMode.write(MotorMode.kDefault);

            // Set ramp rate
            this.output.motorRamp.write(this.config.defaultRampSeconds);
        }

        // Write gearing data if needed
        if (this.gearGoal.write) {

            // Write data
            this.writeGearShift(this.gearGoal.value);

            // Clear lock
            this.gearGoal.zero();
        }

        // Calculate motor voltages from inputs
        DifferentialDriveWheelSpeeds outputs = DifferentialDriveCalculation.semiConstCurve(throttleGoal, turnGoal);
        outputs = DifferentialDriveCalculation.normalize(outputs);

        // Write outputs to frame
        this.writePercentOutputs(outputs.leftMetersPerSecond, outputs.rightMetersPerSecond);
    }

    /**
     * Handler for pivot control
     * 
     * @param meta Statemachine metadata
     */
    private void handlePivotControl(StateMetadata<States> meta) {
        if (meta.isFirstRun()) {
            log("Switched to pivot control");
            log(String.format("Pivot goal is: %s", this.pivotGoal));

            // Reset PIF
            this.turnController.reset();

            // Pivot requires the default gear to be selected
            log("Switching to default gear");
            this.writeGearShift(this.defaultGear);

            // Send motor mode command
            this.output.motorMode.write(MotorMode.kBrake);

            // Set ramp rate
            this.output.motorRamp.write(this.config.defaultRampSeconds);
        }

        // Determine current angle
        Rotation2d robotAngle = this.localizer.getPoseMeters().getRotation();

        // Determine error
        double error = MathUtils.getWrappedError(robotAngle.getDegrees(), this.pivotGoal.getDegrees());

        // Check if we have reached the goal
        if (MathUtils.epsilonEquals(error, 0.0, this.epsilon)) {
            log("Finished pivoting");
            this.stateMachine.setState(this.stateMachine.defaultStateKey);
            return;
        }

        // Determine DT
        double dt = getAdjustedInputs().timestamp_ms - this.lastInput.timestamp_ms;

        // Calculate output
        double output = this.turnController.calculate(error, dt, true);

        // Write output frame
        this.writePercentOutputs(output, -output);
    }

    /**
     * Handle Rabbit Chase mode
     * 
     * @param meta Statemachine metadata
     */
    private void handleRabbitChase(StateMetadata<States> meta) {
        if (meta.isFirstRun()) {
            log("Switched to 'rabbit chase' mode");
            log(String.format("Position goal is: %s", this.absPositionGoal));

            // Reset PIF
            this.turnController.reset();
            this.distanceController.reset();

            // Rabbit Chase requires the default gear to be selected
            log("Switching to default gear");
            this.writeGearShift(this.defaultGear);

            // Send motor mode command
            this.output.motorMode.write(MotorMode.kBrake);

            // Set ramp rate
            this.output.motorRamp.write(this.config.pathingRampSeconds);
        }

        // Determine DT
        double dt = getAdjustedInputs().timestamp_ms - this.lastInput.timestamp_ms;

        // Get the robot pose
        Pose2d robotPose = this.localizer.getPoseMeters();

        // Calculate positional error
        Translation2d error = this.absPositionGoal.minus(robotPose.getTranslation());
        double distanceError = Math.sqrt(Math.pow((this.absPositionGoal.getX() - robotPose.getTranslation().getX()), 2)
                + Math.pow((this.absPositionGoal.getY() - robotPose.getTranslation().getY()), 2));

        // Calculate rotational error
        Rotation2d angularError = Rotation2d.fromDegrees(Math.toDegrees(Math.atan2(error.getY(), error.getX())));

        // Calculate speed multiplier based on distance from target.
        // This lets the robot curve towards the target, instead of snapping to it.
        // This is a trick I learned from a programmer at 1114. It provides really
        // smooth outputs
        // https://bitbucket.org/kaleb_dodd/simbot2019public/src/abc56f5220b5c94bca216f86e3b6b5757d0ffeff/src/main/java/frc/subsystems/Drive.java#lines-337
        double speedMul = ((-1 * Math.min(Math.abs(angularError.getDegrees()), 90.0) / 90.0) + 1);

        // Calculate needed throttle
        double throttleOutput = this.distanceController.calculate(distanceError, dt);

        // Restrict throttle output
        throttleOutput *= speedMul;

        // Calculate rotation PIF
        double turnOutput = this.turnController.calculate(angularError.getDegrees(), dt, false);

        // Calculate motor outputs
        double left = throttleOutput + turnOutput;
        double right = throttleOutput - turnOutput;

        // Write output frame
        this.writePercentOutputs(left, right);

    }

    /**
     * Handle locked mode
     * 
     * @param meta Statemachine metadata
     */
    private void handleLocked(StateMetadata<States> meta) {

        if (meta.isFirstRun()) {
            log("Locked. Holding position");

            // Reset PIF
            this.turnController.reset();

            // Pivot requires the default gear to be selected
            log("Switching to default gear");
            this.writeGearShift(this.defaultGear);

            // Send motor mode command
            this.output.motorMode.write(MotorMode.kBrake);

            // Set ramp rate
            this.output.motorRamp.write(this.config.defaultRampSeconds);

            // Read the current angle as the goal angle
            this.pivotGoal = this.localizer.getPoseMeters().getRotation();
            log(String.format("Locking on angle: %s", this.pivotGoal));

            log("Will remain locked until a new state is set");
        }

        // Determine current angle
        Rotation2d robotAngle = this.localizer.getPoseMeters().getRotation();

        // Determine error
        double error = MathUtils.getWrappedError(robotAngle.getDegrees(), this.pivotGoal.getDegrees());

        // Determine DT
        double dt = getAdjustedInputs().timestamp_ms - this.lastInput.timestamp_ms;

        // Calculate output
        double output = this.turnController.calculate(error, dt, true);

        // Write output frame
        this.writePercentOutputs(output, -output);

    }

    /**
     * Handle neutral mode
     * 
     * @param meta Statemachine metadata
     */
    private void handleNeutral(StateMetadata<States> meta) {
        if (meta.isFirstRun()) {
            log("Switched to neutral mode");

            // Send motor mode command
            this.output.motorMode.write(MotorMode.kCoast);

            // Set ramp rate
            this.output.motorRamp.write(0.0);

            log("Will now be idle until a new state is set");
        }

        // Write gearing data if needed
        if (this.gearGoal.write) {

            // Write data
            this.writeGearShift(this.gearGoal.value);

            // Clear lock
            this.gearGoal.zero();
        }
    }

    /**
     * Drive the robot with controller inputs
     * 
     * @param throttle Throttle percentage [-1.0 to 1.0]
     * @param turn     Turn percentage [-1.0 to 1.0]
     * @param scaling  Scaling method
     */
    public void drive(double throttle, double turn, ScalingMode scaling) {

        // Scale inputs
        throttle = InputUtils.scale(throttle, scaling);
        turn = InputUtils.scale(turn, scaling);

        // Write to goal
        this.throttleGoal = throttle;
        this.turnGoal = turn;

        // Set state
        this.stateMachine.setState(States.kOpenLoop);
    }

    /**
     * Turn to an angle
     * 
     * @param angle    Angle to turn to
     * @param epsilon  Allowed error
     * @param relative Is angle robot-relative?
     */
    public void turnTo(Rotation2d angle, Rotation2d epsilon, boolean relative) {

        // If the angle is robot-relative, convert to field-relative
        this.pivotGoal = angle;
        if (relative) {
            this.pivotGoal = angle.minus(this.localizer.getPoseMeters().getRotation());
        }

        // Set epsilon
        this.epsilon = epsilon.getDegrees();

        // Set state
        stateMachine.setState(States.kPivoting);
    }

    /**
     * Turn to face a field-relative point
     * 
     * @param point   Point on field
     * @param epsilon Allowed error
     */
    public void turnTo(Translation2d point, Rotation2d epsilon) {

        // Modify point by robot position
        Translation2d modGoal = point.minus(this.localizer.getPoseMeters().getTranslation());

        // Calculate angle
        this.pivotGoal = Rotation2d.fromDegrees(Math.toDegrees(Math.atan2(modGoal.getY(), modGoal.getX())));

        // Set epsilon
        this.epsilon = epsilon.getDegrees();

        // Set state
        stateMachine.setState(States.kPivoting);
    }

    /**
     * Set the robot to drive to a point smoothly. This point can be modified while
     * the robot is driving during path-following situations.
     * 
     * @param goal          Goal point in space (field-relative)
     * @param epsilonMeters Allowed radius from the point for the robot to stop
     */
    public void driveTo(Translation2d goal, double epsilonMeters) {

        // Set goal position
        this.absPositionGoal = goal;

        // Set epsilon
        this.epsilon = epsilonMeters;

        // Set state
        stateMachine.setState(States.kRabbitChase);
    }

    /**
     * Set what gearing the drivetrain should be using. This only works in open-loop
     * mode!
     * 
     * @param gear Desired gearing
     */
    public void setGear(Gear gear) {
        log(String.format("Setting wanted gearing to: %s", gear));

        this.gearGoal.write(gear);
    }

    /**
     * Write output percents, while respecting active side
     * 
     * @param left  Left side voltage
     * @param right Right side voltage
     */
    private void writePercentOutputs(double left, double right) {
        this.writeVoltages(left * 12.0, right * 12.0);
    }

    /**
     * Write output voltages, while respecting active side
     * 
     * @param left  Left side voltage
     * @param right Right side voltage
     */
    private void writeVoltages(double left, double right) {
        double direction = (this.activeSide == ChassisSide.kPrimary) ? 1 : -1;

        this.output.leftVoltage = left * direction;
        this.output.rightVoltage = right * direction;
    }

    /**
     * Write gearshift data. If this drivetrain does not have shifters, this just
     * writes the default value
     * 
     * @param gear Gear to shift to.
     */
    private void writeGearShift(Gear gear) {

        // Check if the drivetrain supports gearshifting
        if (this.config.shifterType != ShifterType.NO_SHIFTER) {
            // Write the selected
            this.output.gear.write(gear);
        } else {
            // Write the default gear
            this.output.gear.write(this.defaultGear);
        }
    }

    /**
     * Get all sensor readings that the base needs <br>
     * User must override this.
     * 
     * @return Sensor readings
     */
    public abstract DriveTrainSensors readInputs();

    /**
     * Handle all outputs <br>
     * User must override this.
     * 
     * @param output Motor & config outputs
     */
    public abstract void consumeOutputs(DriveTrainOutput output);

    /**
     * Read adjusted sensor readings
     * 
     * @return Adjusted sensor readings
     */
    private DriveTrainSensors getAdjustedInputs() {

        // Build an output
        DriveTrainSensors adj = new DriveTrainSensors();

        // Adjust sensor readings
        adj.rotation = this.input.rotation.minus(this.inputOffset.rotation);
        adj.angle = this.input.angle - this.inputOffset.angle;
        adj.angularRate = this.input.angularRate - this.inputOffset.angularRate;
        adj.leftEncoderMetres = this.input.leftEncoderMetres - this.inputOffset.leftEncoderMetres;
        adj.rightEncoderMetres = this.input.rightEncoderMetres - this.inputOffset.rightEncoderMetres;

        return adj;
    }

    /**
     * Set which side of the drivetrain to treat as the "front"
     * 
     * @param side Side to choose as active
     */
    public void setActiveSide(ChassisSide side) {
        log(String.format("Set active side to: %s", side.toString()));

        this.activeSide = side;

    }

    /**
     * Get the robot pose
     * 
     * @return Pose
     */
    public Pose2d getPose() {
        return this.localizer.getPoseMeters();
    }

    /**
     * Read the current state of the internal state machine. This should only really
     * be used if you are writing a complex extension on top of this base.
     * 
     * @return Internal state
     */
    public States getInternalState() {
        return this.stateMachine.getCurrentState();
    }

    /**
     * Check if the drivetrain is currently done doing its task, and is sitting at
     * it's goal. This can be used for calculating isFinished() methods for commands
     * that rely on drivetrain positioning
     * 
     * @return Is drivetrain at goal?
     */
    public boolean isAtGoal() {
        // Read the internal state
        States currentState = getInternalState();
        return currentState == States.kOpenLoop || currentState == States.kLocked || currentState == States.kNeutral;
    }

    public void setPose(Pose2d pose) {
        log(String.format("Setting pose to: %s", pose));
        // Set pose
        this.localizer.resetPosition(pose, getAdjustedInputs().rotation);

        // Reset encoders
        this.inputOffset.leftEncoderMetres = this.input.leftEncoderMetres;
        this.inputOffset.rightEncoderMetres = this.input.rightEncoderMetres;
    }

    /**
     * Set a method for this class to use for logging
     * 
     * @param hook Logging method
     */
    public void setConsoleHook(Consumer<String> hook) {
        this.loggingHook = hook;
        this.stateMachine.setConsoleHook(hook);
    }

    private void log(String message) {
        if (loggingHook != null) {
            loggingHook.accept(message);
        }
    }

}