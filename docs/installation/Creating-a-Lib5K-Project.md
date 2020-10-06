# Using Lib5K in your robot project

The process for creating a robot project that uses Lib5K is a little different from the normal process for setting up a WPILib project. This document is specifically written for 5024 members.

## The steps

 1. Creating a base project
 2. Downloading and configuring Lib5K as a Java library
 3. Download all vendor configuration files
 4. Setting up GitHub CI
 5. Configuring the correct Java package
 6. Ensuring everything works

## Creating a base project

If this project is an official team project, get the lead programmer or a mentor to create a new public GitHub repo for this project. Make sure to follow the naming convention:

 - Real season projects are named after the game (ex. "2018 Power Up" = "PowerUp")
 - Offseason projects use the same name as the season they come after, plus `-Offseason` (ex. "PowerUp" -> "PowerUp-Offseason")
 - Anything else can have whatever name you want. If the repo is for an offseason project, make it private until everything looks presentable

Now that a repo is created, clone it, and use the WPILib VSCode extension to [create a new WPILib project](https://docs.wpilib.org/en/stable/docs/software/wpilib-overview/creating-robot-program.html#creating-a-new-wpilib-project). Make sure to set the language as `java`, use the `Advanced Skeleton` as the project `Template`, `5024` as the team number, and enable `Desktop Support`. You may need to generate the project into a new folder on your computer, then copy all the files (including hidden files) into the repo, and push to git.

## Downloading and configuring Lib5K as a Java library

*If you are needing to use a custom version of Lib5K, or use some un-released features, follow the [compiling Lib5K from source](/lib5k/installation/Compiling-Lib5K-From-Source) guide, and use the files in the `lib5k/build/libs` directory instead of downloading from GitHub*

You will need to got to the [latest release](https://github.com/frc5024/lib5k/releases/latest) of Lib5K, and download the following files:
 - `lib5k-all.jar`
 - `gradle5k.gradle`
 - `vendordeps.zip`
 - Any file ending in `.py`

### Lib5K-All

This file contains everything needed to develop code, and deploy with Lib5K. The JAR comes with both the needed `.class` files for the Java compiler, and some extra files that provide VSCode with information about all the code inside (these files are what allow you to press `F12` to get a definition).

Create a new folder at the root of your project called `libs`, and put the JAR file inside.

Now, you just need to tell Gradle that this file exists. Open up `build.gradle` and add the following lines inside the `dependencies` section:
```groovy
dependencies {

    // Lib5K
    compile fileTree(dir: 'libs', include: '*.jar')

}
```

Open up `.gitignore` file, and if there is a line that says `*.jar`, remove it.

### Python scripts

The `.py` files should all be placed into a folder at the root of your project called `scripts`

### Gradle5K

Gradle5K is a script we provide that contains everything needed to set up your project. Place it in the root of your project. To load the script, you will need to make a few changes to your `build.gradle` file. First, at the very top of the file, add the following (be sure to set the [latest](https://plugins.gradle.org/plugin/edu.wpi.first.GradleRIO) version number):

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "edu.wpi.first:GradleRIO:2020.3.2"
  }
}
```

Next, after the pre-existing `plugins` block, add this line:

```groovy
apply from: "./gradle5k.gradle"
```

Finally, in the `dependencies` block, **remove** the following lines:

```groovy
implementation wpi.deps.wpilib()
nativeZip wpi.deps.wpilibJni(wpi.platforms.roborio)
nativeDesktopZip wpi.deps.wpilibJni(wpi.platforms.desktop)
```

Make sure not to remove the second set of lines that looks similar. The first set loads WPILib (which we do for you in our script), and the second set loads third party code (which we do not do for you).

### Vendordeps

Your project will likely come with a folder called `vendordeps`. This is used by WPILib to load third party code into your project. If you do not have the folder, create it in the root of your project. Unzip the `vendordeps.zip` file we provide, and move all the files into your `vendordeps` folder. This will make sure you have the correct versions of everything we need.

## Setting up GitHub CI

Create a `.github` folder and a folder in it called `workflows`. Add the following to a file in `workflows` called `build.yml`

```yml
name: Build Robot Code
on: [pull_request, push]
jobs:
  gradle:
    strategy:
      matrix:
        os: [ubuntu-latest,  windows-latest]
    runs-on: ${{ matrix.os }}
    steps:
    - uses: actions/checkout@v2
    - uses: actions/setup-java@v1
      with:
        java-version: 11
    - uses: eskatos/gradle-command-action@v1
      with:
        arguments: build
```

## Configuring the correct Java package

Create a new folder at `src/main/java` with the path `io/github/frc5024/y<current_year>/<robot_name>`. For example, in 2020, the folder would be at `src/main/java/io/github/frc5024/y2020/darthraider`.

Move the `Main.java` file that was generated by WPILib into this folder, and fix the package name.

Create a new file in the new folder called `Robot.java`, and add the following to it (subject to change with future releases of Lib5K):

```java
package <package_name>;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import io.github.frc5024.lib5k.autonomous.RobotProgram;

public class Main extends RobotProgram {

    public Main() {
        super(false, true, Shuffleboard.getTab("Main"));
    }

    @Override
    public void autonomous(boolean init) {
    }

    @Override
    public void disabled(boolean init) {
    }

    @Override
    public void teleop(boolean init) {
    }

    @Override
    public void test(boolean init) {
    }
}
```

Now, open up `build.gradle` and change the `ROBOT_MAIN_CLASS` variable to match the new package name (don't forget to add `.Main` to the end of the package)

## Ensuring everything works

Now, just run
```sh
# Linux:
./gradlew build

# Windows:
.\gradlew.bat build
```

and make sure everything builds correctly. Then, push to Git, and the robot project is ready to go! If anything goes wrong in this step, see the [troubleshooting](/lib5k/technical/Troubleshooting) page for solutions.