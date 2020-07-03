# PurePursuit extension for Lib5K <br> [![](https://jitpack.io/v/frc5024/purepursuit.svg)](https://jitpack.io/#frc5024/purepursuit)
This java library contains path generation utils, and a [PurePursuit](https://www.ri.cmu.edu/pub_files/pub3/coulter_r_craig_1992_1/coulter_r_craig_1992_1.pdf) implementation for use on both tank, and holonomic drivebases.

## Installation
This library is a [Lib5K extension]() and uses the following implementation:
```groovy
implementation 'com.github.frc5024:purepursuit:master-SNAPSHOT'
```

## Library Maintenance
To update any dependencies/libraries, go to the `build.gradle` file, and edit the versions under "Dependency Versions". The links will tell you the latest version available.

## Unit tests
This library is fully covered by unit tests. To test the code, run:
```sh
./gradlew test
```

## Credits
The path generation, and controller algorithms were designed from scratch by [@ewpratten](https://github.com/ewpratten) for use on [@frc5024](https://github.com/frc5024)'s 2020 robot, [*Darth Raider*](https://cs.5024.ca/webdocs/docs/robots/darthRaider). The lookahead finding algorithm is a Java implementation of [@AtsushiSakai](https://github.com/AtsushiSakai)'s [lookahead finder](https://github.com/AtsushiSakai/PythonRobotics/blob/9274aacefb8fb52feac544fc26d075046f703afd/PathTracking/pure_pursuit/pure_pursuit.py#L77-L108).