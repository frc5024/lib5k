package ca.retrylife.frc.limelight.util;

import java.util.function.Consumer;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableValue;

/**
 * Utilities for working with NetworkTables
 */
public class NetworkTableUtil {

    /**
     * Set up an EntryListener for both "new" and "update" events
     * 
     * @param table         NetworkTable containing value
     * @param key           Key to listen for
     * @param entryConsumer Action for value change
     */
    public static void simpleListener(NetworkTable table, String key, Consumer<NetworkTableValue> entryConsumer) {
        table.addEntryListener(key, (t, k, e, v, f) -> {
            entryConsumer.accept(v);
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }

}