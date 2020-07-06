package io.github.frc5024.lib5k.autonomous.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.frc5024.lib5k.logging.RobotLogger;

/**
 * A command that will simply log a pre-set message to the console when run
 */
public class LogCommand extends InstantCommand {

    private String msg;
    private String component;

    /**
     * A command that will simply log a pre-set message to the console when run
     * 
     * @param msg Message to log
     */
    public LogCommand(String msg) {
        this.msg = msg;
        this.component = "";
    }

    /**
     * A command that will simply log a pre-set message to the console when run
     * 
     * @param component Component the log is coming from 
     * @param msg Message to log
     */
    public LogCommand(String component, String msg) {
        this.msg = msg;
        this.component = component;
    }



    @Override
    public void execute() {

        // Log the message
        RobotLogger.getInstance().log(component, msg);
    }
}