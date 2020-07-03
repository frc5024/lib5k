package io.github.frc5024.asynchal;

public interface Pollable {

    /**
     * Check for, and handle any updates since the last call. This should only be
     * called from the Poller
     */
    public void checkForUpdates();
}