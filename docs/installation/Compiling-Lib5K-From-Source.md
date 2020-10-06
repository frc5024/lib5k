# Compiling Lib5K From Source

Compiling Lib5K from source is reasonably simple.

## Compiling for testing
Just like any gradle project, we can do a local build with
```sh
# Linux
./gradlew build

# Windows
.\gradlew.bat build
```

## Compiling a local copy of the documentation
If you are looking to view the JavaDoc locally, you can manually generate it with:
```sh
# Linux
./gradlew clean build document

# Windows
.\gradlew.bat clean build document
```

Keep in mind, any code pushed to the `master` branch will automatically have its javadoc built in our CI pipeline by [`.github/workflows/docs.yml`](https://github.com/frc5024/lib5k/blob/master/.github/workflows/docs.yml), and will be published to [frc5024.github.io/lib5k](https://frc5024.github.io/lib5k).

## Building a test release

If you are making a change to Lib5K that is needed for a robot project, but you don't have time to make a proper release (or the build is of a test branch), you can build only the needed files with:
```sh
# Linux
./gradlew clean buildBeta

# Windows
.\gradlew.bat clean buildBeta
```

The library files will be exported to the `_release` folder.

## Building a full release

To build a proper release of Lib5K (only do this on the `master` branch), first update the version number in `build.gradle`, then run:
```sh
# Linux
./gradlew clean buildRelease

# Windows
.\gradlew.bat clean buildRelease
```

The library files will be exported to the `_release` folder. Make a [new release](https://github.com/frc5024/lib5k/releases/new) on GitHub, and upload all generated files.