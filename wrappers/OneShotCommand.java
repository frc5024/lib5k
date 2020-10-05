package frc.common.wrappers;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Its a command, but it can only run once
 */
public abstract class OneShotCommand extends Command {

    @Override
    protected boolean isFinished() {
        return true;
    }

    @Override
    protected void end() {
        // Just call our custom execute function
        doOnce();
    }

    protected abstract void doOnce();
    
}