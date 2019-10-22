package frc.lib5k.components;

public class GearBoxEncoder extends EncoderBase {
    GearBox box;

    public GearBoxEncoder(GearBox box) {
        this.box = box;
    }

    @Override
    public int getRawTicks() {
        return box.getTicks();
    }

}