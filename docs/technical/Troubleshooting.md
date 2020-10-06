# Troubleshooting

## My robot program is throwing JNI or HAL errors at runtime

While Lib5K uses and imports third-party libraries, only the java bindings are actually used. This means that any [JNI](https://en.wikipedia.org/wiki/Java_Native_Interface)-based library (CTRE, NavX, RevRobotics ...) will require a [WPILib Vendordep](https://docs.wpilib.org/en/stable/docs/software/wpilib-overview/3rd-party-libraries.html?highlight=vendor) to be installed in the application as well

## When building Lib5K, I get a ton of errors about NavX, TalonSRX, and SparkMax

These are caused by the way gradle is currently set up. We are waiting on an unpdate from WPILib, but for now, just run this command once:

```sh
./gradlew bootstrap
```