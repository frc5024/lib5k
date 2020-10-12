# Different Types of DriveTrains

In FRC, we commonly encounter three main types of chassis / drivetrain design:

 - Tank tracks
   - Can be constructed with actual tracks, or with multiple linked wheels
   - Has the highest traction and pushing power
   - Is the worst at turning
   - Is generally the fastest
   - Is the easiest to build
   - Is the most common
   - Requires advanced path-planning code to control autonomously
 - Holonomic
   - Can move in any direction by driving pairs of wheels
   - Has a diagonal wheel in each corner of the frame
   - Has almost no pushing power
   - Has almost no traction
   - Can turn very well
   - Is fairly easy to program
   - Is the least used
 - Swerve
   - Can move in any direction by rotating its wheels
   - Has a free-spinning wheel in each corner of the frame
   - High development cost (can be over $1000 to implement)
   - High power draw
   - Generally good pushing power
   - Great traction
   - Can lock itself in place by rotating all wheels to an "X" pattern
   - Can turn very well
   - High effort to program and coordinate, but easy to use after the base code is stable

## What we use at 5024

We tend to develop robots with a drop-center tank-driven drivetrain. This means that we have 6 wheels, in pairs of 3, where the center wheels are lower to the ground than the rest. This generally keeps our robots mainly resting on the back 4 wheels, and we only use the front 2 when driving over an obstacle.

Since we use a nearly identical drivetrain every year, Lib5K includes a lot of code to get this system working with very little effort, so we can spend our development time focusing on more important systems.