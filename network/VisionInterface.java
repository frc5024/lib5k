package frc.common.network;

import edu.wpi.first.wpilibj.Notifier;

public class VisionInterface {
    private static VisionInterface instance = null;
    Notifier notifier;

    NetworkTables nt_inst;

    double distance = 0.0;
    double angle = 0.0;
    double pitch = 0.0;

    private VisionInterface() {
        nt_inst = NetworkTables.getInstance();
        notifier = new Notifier(this::update);

        // Push a placeholder for each entry to NetworkTables
        nt_inst.getEntry("vision", "target_distance").setNumber(0.0);
        nt_inst.getEntry("vision", "target_angle").setNumber(0.0);
        nt_inst.getEntry("vision", "target_pitch").setNumber(0.0);
    }

    public void start(double period){
        this.notifier.startPeriodic(period);
    }

    public static VisionInterface getInstance() {
        if (instance == null) {
            instance = new VisionInterface();
        }
        return instance;
    }

    private void update() {
        this.distance = nt_inst.getEntry("vision", "target_distance").getDouble(0.0);
        this.angle = nt_inst.getEntry("vision", "target_angle").getDouble(0.0);
        this.pitch = nt_inst.getEntry("vision", "target_pitch").getDouble(0.0);
    }

    public double getDistance() {
        return this.distance;
    }

    public double getAngle() {
        return this.angle;
    }

    public double getPitch() {
        return this.pitch;
    }
}