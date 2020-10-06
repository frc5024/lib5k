# Using Lib5K in your robot project

The process for creating a robot project that uses Lib5K is a little different from the normal process for setting up a WPILib project. This document is specifically written for 5024 members.

## The steps
 1) Creating a base project
 2) Downloading and configuring Lib5K as a Java library
 3) Download all vendor configuration files
 4) Setting up GitHub CI
 5) Configuring the correct Java package
 6) Ensuring everything works

## Creating a base project

If this project is an official team project, get the lead programmer or a mentor to create a new public GitHub repo for this project. Make sure to follow the naming convention:

 - Real season projects are named after the game (ex. "2018 Power Up" = "PowerUp")
 - Offseason projects use the same name as the season they come after, plus `-Offseason` (ex. "PowerUp" -> "PowerUp-Offseason")
 - Anything else can have whatever name you want. If the repo is for an offseason project, make it private until everything looks presentable

Now that a repo is created, clone it, and use the WPILib VSCode extension to [create a new WPILib project](https://docs.wpilib.org/en/stable/docs/software/wpilib-overview/creating-robot-program.html#creating-a-new-wpilib-project). Make sure to set the language as `java`, use the `Advanced Skeleton` as the project `Template`, `5024` as the team number, and enable `Desktop Support`. You may need to generate the project into a new folder on your computer, then copy all the files (including hidden files) into the repo, and push to git.

## Downloading and configuring Lib5K as a Java library

*If you are needing to use a custom version of Lib5K, or use some un-released features, follow the [compiling Lib5K from source]() guide, and use the files in the `_release` folder instead of downloading from GitHub*

You will need to got to the [latest release](https://github.com/frc5024/lib5k/releases/latest) of Lib5K, and download the following files:
 - `lib5k-<version>-all.jar`
 - Any file ending in `.py`

Create two folders in the root of the robot project repo named `libs` and `scripts`. Put the `.jar` file in `libs` and the `.py` files in `scripts`.

Once that is done, open up `build.gradle` and add the following lines inside the `dependencies` section:
```groovy
dependencies {

    // Lib5K
    compile fileTree(dir: 'libs', include: '*.jar')

}
```

Open up `.gitignore` file, and if there is a line that says `*.jar`, remove it.

## Download all vendor configuration files

Create a folder (if not already created) in the repo root called `vendordeps`. 

 - Go to the [Kauai Labs website](https://pdocs.kauailabs.com/navx-mxp/software/roborio-libraries/java/), find the "Online Installation Method" section, and follow the instructions.
 - Download [this file](http://devsite.ctr-electronics.com/maven/release/com/ctre/phoenix/Phoenix-latest.json), and place it in the `vendordeps` folder.
 - Download [this file](https://www.revrobotics.com/content/sw/max/sdk/REVRobotics.json), and place it in the `vendordeps` folder.
 - Download [this file](https://raw.githubusercontent.com/wpilibsuite/allwpilib/master/wpilibNewCommands/WPILibNewCommands.json), and place it in the `vendordeps` folder.

Depending on the hardware the team decides to use during the season, other JSON files may be needed. They are usually listed [here](https://docs.wpilib.org/en/stable/docs/software/wpilib-overview/3rd-party-libraries.html).

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

If the team wants to use JavaDoc (this is recommended), make another file called `docs.yml`:

```yml
name: Deploy docs

on:
  push:
    branches:
      - master

jobs:  
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2

      - uses: actions/setup-java@v1
        with:
          java-version: 11

      - uses: eskatos/gradle-command-action@v1
        with:
          arguments: clean javadoc

      - name: Check GitHub Pages status
        uses: crazy-max/ghaction-github-status@v1
        with:
          pages_threshold: major_outage

      - name: Deploy to GitHub Pages
        if: success()
        uses: crazy-max/ghaction-github-pages@v2
        with:
          target_branch: gh-pages
          build_dir: docs
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

The documentation will automatically be published to `frc5024.github.io/<reponame>`

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
.\gradlew.bat buil
```

and make sure everything builds correctly. Then, push to Git, and the robot project is ready to go!