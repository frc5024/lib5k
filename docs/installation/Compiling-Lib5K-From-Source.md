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

### Compile errors

There are a few possibilities for compile errors when building the project for the first time. See the [troubleshooting](/lib5k/technical/Troubleshooting) page for solutions.

## Compiling a local copy of the documentation

If you are looking to view the JavaDoc locally, you can manually generate it with:

```sh
# Linux
./gradlew clean :lib5k:build :lib5k:customJavadoc

# Windows
.\gradlew.bat clean :lib5k:build :lib5k:customJavadoc
```

Keep in mind, any code pushed to the `master` branch will automatically have its javadoc built in our CI pipeline by [`.github/workflows/docs.yml`](https://github.com/frc5024/lib5k/blob/master/.github/workflows/docs.yml), and will be published to [frc5024.github.io/lib5k/javadoc](https://frc5024.github.io/lib5k/javadoc).

## Building a release for testing

It is very common to manually build yourself a release of Lib5K for use in another project. This is generally caused by a quick fix being required, when you don't have time to make a full library release on GitHub. To build a "beta" release of Lib5K, make sure you are in the branch you are wanting to build, then go run the command listed in the [releasing a new version](/lib5k/installation/Creating-a-release) guide. Instead of uploading the files to GitHub, just copy them over to your other project.