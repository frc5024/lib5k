# Current Limiting Example

This is an example on how to use Current Limiting


## Overview

Current Limiting involves 2 class 

* CurrentLimitManager
* CurrentLimit


#### CurrentLimitManager

the CurretLimitManager class deals with running currentLimits.

it's constructed by passing a pdp object into it.

```java 
CurrentLimitManager currentLimitManager = new CurrentLimitManager(new PowerDistributionPanel(1));
```

Every loop the CurrentLimitManager should call the method performCurrentLimits, this performs the current limiting to all CurrentLimits added to the manager.

```java 
currentLimitManager.performCurrentLimits();
```


#### CurrentLimit

The CurrentLimit class can be used in conjunction with the CurrentLimitManager or on its own.

To make a currentLimit you need to pass the constructor, The PDP channel that the device is on, the amps to hold at, the spike amps allowed, the duration of time allowed to be over the spike in seconds, and the method used to set the voltage of the device.

```java
CurrentLimit currentLimit = new CurrentLimit(1, 30, 32, 2, motor::setVoltage);
```

To make a CurrentLimit run when the CurrentLimitManager is run you must call the static method addCurrentLimit and pass it the CurrentLimit object you wish to add.

```java 
CurrentLimitManager.addCurrentLimit(currentLimit);
```

To remove the CurrentLimit from running when the CurrentLimitManager you call the static CurrentLimitManager method removeCurrentLimit and pass it the CurrentLimit object you wish to remove.


```java
CurrentLimitManager.removeCurrentLimit(currentLimit);
```

To run a current limit independently of the CurrentLimitManager you can call the method run on a CurrentLimit, you will need to provide it with a PowerDistributionPanel object.

```java
currentLimit.run(new PowerDistributionPanel(1));
```



