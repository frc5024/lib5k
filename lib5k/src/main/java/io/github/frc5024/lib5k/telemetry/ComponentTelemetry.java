package io.github.frc5024.lib5k.telemetry;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

@Deprecated(since = "October 2020", forRemoval = true)
public class ComponentTelemetry {
    private static ComponentTelemetry m_instance = null;

    private String baseTable = "Lib5K-Telemetry";
    private String componentsTable = "Components";

    /**
     * Telem setup
     */
    private ComponentTelemetry() {
    }

    /**
     * Get the ComponentTelemetry instance
     * 
     * @return Instance
     */
    public static ComponentTelemetry getInstance() {
        if (m_instance == null) {
            m_instance = new ComponentTelemetry();
        }

        return m_instance;
    }

    /**
     * Get the NetworkTable for a component
     * 
     * @param componentName Component name
     * @return Component table
     */
    public NetworkTable getTableForComponent(String componentName) {
        return NetworkTableInstance.getDefault().getTable(baseTable).getSubTable(componentsTable)
                .getSubTable(componentName);

    }
}