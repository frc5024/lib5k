# Robot Sessions

All file management inside of Lib5K is built around the concept of "sessions". The idea is that, every time the robot turns on, a new timestamped folder is created. By calling the helper functions in the [`FileManagement`](/lib5k/javadoc/io/github/frc5024/lib5k/utils/FileManagement.html) class, robot code can read and write files to the latest session folder, and everything will end up automatically being neatly organized and timestamped in the background.

The reason we do this is simple. Think of the following scenario: We have some code that saves a CSV file containing a robot's flywheel velocity and the goal velocity for a ball shooter. Everything is going fine at an event until suddenly, one match, we miss every single one of our shots, as they are all going too high. To debug this, all a developer needs to do is, once the robot gets off the field, unmount the robot's USB stick, and open up the latest folder (timestamped with the start of the match), which contains logs from the most recent match, and the shooter CSV file. Inside the CSV file, we discover that an integer overflow is causing the shooter to spin up to an unachievable rate.

If we didn't do this session system, the team member would have had to spend a large amount of time scrolling through a massive log of the entire event trying to find the correct match and data.

## The robot USB stick

In order to make use of the logging functionality, all robots must have 1 USB drive plugged into one of the RoboRIO's USB ports. The FileManagement software will automatically find the USB and create a "sessions" folder in it to store all session info. If two USBs are found, the one mounted at `/media/sda1` will be used. For information on UNIX filepaths and mounting, see [here](https://linuxize.com/post/how-to-mount-and-unmount-file-systems-in-linux/). (The RoboRIO will automatically mount any attached USB when it boots)

## Downloading logs after a game

Downloading logs is really simple. All you need to do is: turn off the robot, unplug the USB, download the `sessions` folder to your computer, then plug the USB pack in to the robot. Generally, we like checking the sessions folder into git, and publishing it on GitHub about once every few days. This way, we can easily share logs with eachother, and pull them up on our phones for reference.