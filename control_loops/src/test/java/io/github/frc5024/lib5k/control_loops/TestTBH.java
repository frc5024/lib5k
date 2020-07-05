package io.github.frc5024.lib5k.control_loops;

import org.junit.Test;

public class TestTBH {

    /**
     * This test simply checks if the TBH controller actually puts out some output
     */
    @Test
    public void ensureTBHProvidesOutput() {
        
        // Create controller
        TBHController controller = new TBHController(0.3);

        // Calculate & check
        assert controller.calculate(100) > 0.0;
    }
    
}