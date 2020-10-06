# Lib5K Documentation

Welcome to the lib5k wiki!

**If you are looking the the Lib5K JavaDoc, go [here](./javadoc)**

## The history of Lib5K

Lib5K is the software library that powers the Raider Robotics control system. It originally started as a summer project by [@ewpratten]() back in the 2018 offseason, and in it's early versions, only contained logging tools. During the 2019 season, Lib5K was slightly expanded to contain some custom PID controllers, and utils.

Lib5K development really picked up during summer 2019, where the library (and all of Raider Robotics development) switched from C++ to Java Native. This switch also brought a lot of the core features to Lib5K, and the whole team got involved in development during the 2020 season.

Lib5K is now a team project, and is available for other teams to use in their codebases, according to the [GNU General Public License](https://github.com/frc5024/lib5k/blob/master/LICENSE)

## Design principles

Lib5K is designed around extreme modularity. This comes from the way Raider Robotics functions. Our team is constantly prototyping and swapping out robot components with newer and better revisions. This requires our software to be flexible to the point that we can switch an entire system from using CTRE motor controllers and limit switches, to using REVRobotics motor controllers and rangefinders in 15 minutes between matches at an event.

Along with the modularity of Lib5K, we have a custom simulation environment, where every device is automatically simulated in the background. This way, our developers don't have to worry about setting up the robot simulation environment. It just \*happens\*. Our simulation tools also have built in interpolation, so we can do things like: estimating the robot's pose from two motor outputs. (We use this to accurately test our autonomous paths in our simulator without needing a robot)

Lib5K has, and will always be open source, so if you see one of our robots do something cool, you can come and take a look at the code that powers it (and even use it for yourself).

## Awards

So far, Lib5K has helped us win multiple *Innovation in Control* awards at district events.

## Usage examples

If you are looking for example code, take a look at the [examples](https://github.com/frc5024/lib5k/tree/master/examples) folder.