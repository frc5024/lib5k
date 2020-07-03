# lib5k
The software libraries that power all Raider Robotics projects

## Modules

This repository is split into modules. This allows us to only import what we need per project instead of loading unnecessary libs (for example, parts management does not need access to CTRE libs). The following table lists all avalible modules, and their uses.

| Gradle Name    | JitPack Name  | Description                                                          |
|----------------|---------------|----------------------------------------------------------------------|
| `:purepursuit` | `purepursuit` | This library contains everything needed for autonomous path planning |

## Development

### Upgrading third-party library versions
Many modules rely on third-party libraries. To upgrade the versions, edit the appropriate variable in `gradle_utils/libversions.gradle`

### Adding a new module
Any folder containing a `build.gradle` file can be a module. Make sure to add the new folder to `settings.gradle`. Otherwise, it will not be built

## Troubleshooting

### My robot program is throwing JNI or HAL errors at runtime
While Lib5K uses and imports third-party libraries, only the java bindings are actually used. This means that any [JNI](https://en.wikipedia.org/wiki/Java_Native_Interface)-based library (CTRE, NavX, RevRobotics ...) will require a [WPILib Vendordep](https://docs.wpilib.org/en/stable/docs/software/wpilib-overview/3rd-party-libraries.html?highlight=vendor) to be installed in the application as well.