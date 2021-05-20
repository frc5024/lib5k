# Building a State Machine

The code for our robots is designed around the concept of a [Finite State Machine](https://en.wikipedia.org/wiki/Finite-state_machine). This tutorial will not explain what a state machine is, or why we use them. This is a tutorial on how to use Lib5K's `libKontrol` submodule to create your own mini superstructure.


## Planning your state machine

On the team, we generally accompany all software state machines with a visual diagram. These diagrams are used for:

 - Explaining the system to other members and mentors
 - Use in presentations about our work
 - Graphics for each robot's technical binder

In this tutorial I will be building a state machine for a simple claw. This claw has a button on the inside, and is actuated by a solenoid. The goal for this system is as follows:

 - Claw is normally closed
 - If a request is made to accept an item into the claw, it will open
 - The claw will remain open until the button on the inside is triggered by an item entering the claw
 - Once the button is triggered, the claw will close

## Building the system template

For the sake of this tutorial, I will create a simple subsystem and define it's ins and outs as follows:

```java
public class ClawSystem extends SubsystemBase{
    // The button on the claw
    private LimitSwitch clawButton;

    // The controlling solenoid
    private Solenoid clawSolenoid;

    // A simple constructor to set up the class
    public ClawSystem(Solenoid clawSolenoid, LimitSwitch clawButton){
        this.clawSolenoid = clawSolenoid;
        this.clawButton = clawButton;
    }

    // Methods for controlling this system
    public void grabItem(){}
    public void stop(){}
}
```

The rest of this tutorial will add on to the ClawSystem class.

## Defining state machine states

The first step to creating a state machine for this system is to define the names of all it's states. We will do this with an [enum](https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html). 

As mentioned above, this system can be doing one of two things.

   1. Being closed
   2. Open, and accepting items

We will name these states **CLOSED** and **ACCEPTING** respectively. To define this in the code, we will write the following inside the class body:

```java
private enum ClawSystemStates{
    CLOSED, ACCEPTING
}
```

### Giving each state an action

Now that we have defined a name for each system state, we must write some code to be run during each state. *Remember: these functions are run once every period while the state is active*

The functions for each state are a bit special. They must take in a `StateMetadata` object. This object will let you query the statemachine's current status from your code.

Let's start by defining the **CLOSED** state's action. All this needs to do is: tell the solenoid to turn off. This action only needs to be run once, so we will check if this is the first time this function has been run since the last state change, and only execute code there. This information comes from the `StateMetadata` object provided to our function.

```java
// Function to be run while the system is in it's CLOSED state
private void handleClosed(StateMetadata<ClawSystemStates> meta){
    // Check if this is the first run. (this will only be true once)
    if(meta.isFirstRun()){
        // Turn off the solenoid
        clawSolenoid.set(false);
    }
}
```

*Notice the angle brackets in the function parameters. Both the `StateMetadata` object, and the `StateMachine` object that will be introduced below require these. Inside, you must always write the name of the enum you defined for declaring state names. This is called a [template class](https://docs.oracle.com/javase/tutorial/java/generics/types.html).*

Next, we will define the action for the **ACCEPTING** state. This will make one call to open the claw, then close the claw once a button press is detected. Since we already have a state defined that closes the claw, we don't have to do it again. Just switch back to that state.

```java
// Function to be run while the system is in it's ACCEPTING state
private void handleAccepting(StateMetadata<ClawSystemStates> meta){
    // Check if this is the first run. (this will only be true once)
    if(meta.isFirstRun()){
        // Turn on the solenoid, opening the claw
        clawSolenoid.set(true);
    }
    // Check if the claw's button had been pressed
    if(clawButton.get()){
        // Switch to the CLOSED state
        meta.getParent().setState(ClawSystemStates.CLOSED);
    }
}
```

## Configuring the state machine

Now that we have defined our states and their actions, we can actually create the `StateMachine` object for this system. Define a StateMachine object in the class body, then create it in the constructor.

```java
public ClawSystem( ... ){
    ...
    // Define the statemachine
    this.stateMachine = new StateMachine<ClawSystemStates>("Claw StateMachine");
    // Set the CLOSED state as default 
    stateMachine.setDefaultState(ClawSystemStates.CLOSED, this::handleClosed);
    // Add the ACCEPTING state to the statemachine
    stateMachine.addState(ClawSystemStates.ACCEPTING, this::handleAccepting);
}
```

## Running the state machine

We now need to run the state machine. We can do this by calling the state machines `update` method in the subsystems periodic loop.

```java
// Subsystem periodic looper
@override
public void periodic(){
    // every loop update the state machine
    stateMachine.update();
}
```


Now, we must fill in the `grabItem` and `stop` functions that were stubbed:

```java
public void grabItem(){
    stateMachine.setState(ClawSystemStates.ACCEPTING);
}
public void stop(){
    stateMachine.setState(ClawSystemStates.CLOSED);
}
```

## Result

That's it! You now have a simple system with it's own state machine. The final code should look like this:

```java
public class ClawSystem extends SubsystemBase{
    // The button on the claw
    private LimitSwitch clawButton;
    // The controlling solenoid
    private Solenoid clawSolenoid;
    // State names
    private enum ClawSystemStates{
        CLOSED, ACCEPTING
    }
    // StateMachine
    private StateMachine<ClawSystemStates> stateMachine;
    // A simple constructor to set up the class
    public ClawSystem(Solenoid clawSolenoid, LimitSwitch clawButton){
        this.clawSolenoid = clawSolenoid;
        this.clawButton = clawButton;
        // Define the statemachine
        this.stateMachine = new StateMachine<ClawSystemStates>("Claw StateMachine");
        // Enable logging (this line is subject to change)
        stateMachine.setConsoleHook(RobotLogger.getInstance()::log);
        // Set the CLOSED state as default 
        stateMachine.setDefaultState(ClawSystemStates.CLOSED, this::handleClosed);
        // Add the ACCEPTING state to the statemachine
        stateMachine.addState(ClawSystemStates.ACCEPTING, this::handleAccepting);
    }
    // Subsystem periodic looper
    @override
    public void periodic(){
        // every loop update the state machine
        stateMachine.update();
    }
    // Methods for controlling this system
    public void grabItem(){
        stateMachine.setState(ClawSystemStates.ACCEPTING);
    }
    public void stop(){
        stateMachine.setState(ClawSystemStates.CLOSED);
    }
    // Function to be run while the system is in it's CLOSED state
    private void handleClosed(StateMetadata<ClawSystemStates> meta){
        // Check if this is the first run. (this will only be true once)
        if(meta.isFirstRun()){
            // Turn off the solenoid
            clawSolenoid.set(false);
        }
    }
    // Function to be run while the system is in it's ACCEPTING state
    private void handleAccepting(StateMetadata<ClawSystemStates> meta){
        // Check if this is the first run. (this will only be true once)
        if(meta.isFirstRun()){
            // Turn on the solenoid, opening the claw
            clawSolenoid.set(true);
        }
        // Check if the claw's button had been pressed
        if(clawButton.get()){
            // Switch to the CLOSED state
            meta.getParent().setState(ClawSystemStates.CLOSED);
        }
    }
}
```

For any member that were on the team for the 2020 season, yes. This is the same setup we used for Darth Raider. Just cleaner, and with fewer bugs. (I think :rofl:)