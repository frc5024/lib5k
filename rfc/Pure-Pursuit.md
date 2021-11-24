# Pure Pursuit Control

Date: January 2020<br>
Proposed by: Evan Pratten <@ewpratten><br>
Implemented: February 2020

## Abstract

2020 robot Darth Raider is in need of a high-speed, medium-accuracy autonomous path following solution. Given current issues with the [RAMSETE](https://books.google.ca/books/about/RAMSETE.html?id=b7dY8eJ67bUC&source=kp_book_description&redir_esc=y) controller used throughout January, and in tech demos, I think switching to *Pure Pursuit* is a better choice, as it reduces code complexity, and is more friendly to our specific application (adjusting to field inaccuracy in real-time).

## Implementation

Controller implementation and design is based on [this document](https://www.ri.cmu.edu/pub_files/pub3/coulter_r_craig_1992_1/coulter_r_craig_1992_1.pdf) published by the [CMU Robotics Institute](https://www.ri.cmu.edu/), and [this document](https://www.chiefdelphi.com/uploads/default/original/3X/b/e/be0e06de00e07db66f97686505c3f4dde2e332dc.pdf) published by [Concord Robotics](https://www.frc1721.org/).

Pure Pursuit works by searching for goal poses along a segmented path **N** meters in front of the robot. This value is called the *lookahead gain* of the controller. Increasing this gain causes the robot to take shortcuts in its path, effectively smoothing the path, as well as causing the robot to take more efficient paths.

![](/lib5k/assets/pure_pursuit_lookahead1.png)

Smaller and larger lookahead gains will cause different behavior:

![](/lib5k/assets/pure_pursuit_lookahead2.png)

## Revisions

 1. Added support for swerve-drive calculation
 2. [Fixed a bug where path follower would occasionally fail to find a path, and lock up](https://github.com/frc5024/lib5k/commit/e4bf56180d0e9760ac12241ee2cf4a35c00ace6d)
 3. [Split implementation into `Follower` and `DualPIDTankDriveTrain`](https://github.com/frc5024/lib5k/pull/131)