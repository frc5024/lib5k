# Releasing a New Version

Releasing a new version of Lib5K is very simple. Firstly, make sure the version code is correct in `build.gradle`, then, in the terminal, run:

```sh
./gradlew clean assemble
```

This will place some files into the `lib5k/build/libs/` directory. Follow [this guide](https://docs.github.com/en/free-pro-team@latest/github/administering-a-repository/managing-releases-in-a-repository) from GitHub to create a new release under the Lib5K repository, and add every generated file to the release's files list.