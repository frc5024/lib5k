package frc.common.field;

import edu.wpi.first.wpilibj.DriverStation;

public class Match {
    DriverStation ds = DriverStation.getInstance();

    String gsm;
    int alliance_station;
    int match_number;

    public Match() {
        update();
    }
    
    public void update() {
        this.alliance_station = this.ds.getLocation();
        this.gsm = this.ds.getGameSpecificMessage();
        this.match_number = this.ds.getMatchNumber();
    }
    
    /* Getters */
    public String getGSM() {
        return this.gsm;
    }

    public char[] getSplitGSM() {
        return this.gsm.toCharArray();
    }

    public int getNumber() {
        return this.match_number;
    }

    public int getAllianceStation() {
        return this.alliance_station;
    }
}