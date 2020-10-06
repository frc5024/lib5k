// package io.github.frc5024.common_drive;

// import com.google.common.annotations.Beta;

// import edu.wpi.first.wpilibj.controller.StateSpaceLoop;
// import edu.wpi.first.wpiutil.math.numbers.*;
// import io.github.frc5024.common_drive.gearing.Gear;
// import io.github.frc5024.common_drive.queue.Position;

// @Deprecated(since = "Unstable", forRemoval = false)
// @Beta
// public class PolyDriveTrain {

//     // State-space loops
//     private StateSpaceLoop<N7, N2, N4> kf;
//     private StateSpaceLoop<N2, N2, N2> loop;

//     // Drivetrain gearing
//     private Gear leftGear;
//     private Gear rightGear;

//     // Position tracking
//     private Position position;
//     private Position lastPosition;

//     // Counter
//     private int counter;

//     // Configuration
//     DriveTrainConfig config;

//     // Goal velocity
//     private double goalLeftVelocity = 0.0;
//     private double goalRightVelocity = 0.0;


//     public PolyDriveTrain(DriveTrainConfig config, StateSpaceLoop<N7, N2, N4> kf) {


//         // Zero positioning data
//         this.position.zero();
//         this.lastPosition.zero();
//     }

// }