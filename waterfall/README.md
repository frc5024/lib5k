# waterfall
A library for serializing and storing the large amounts of data that come off our robots

## Why this library exists
This exists as an easy way to implement CSV writing across platforms and projects. Usually, something like this would be bundled with lib5k, but we want the ability to interact with CSV files without needing to install extra RoboRIO-specific libraries (like HAL and NetworkTables)

## Usage

```java
// SystemWriter example
// NOTE: this is pseudocode

// Create a writer
SystemWriter writer = new SystemWriter(new Path(String.format("/path/to/output-%s.csv", getDate())), "Label 1", "Label 2");


// in a periodic function somewhere
periodic() {

    // Do stuff ...

    // Save data
    writer.accessMap().set("Label 1", getValue1());
    writer.accessMap().set("Label 2", getValue2());
    writer.writeOut(getTimeMS());
}

// Data will be saved to /path/to/output-<current_date>.csv
```