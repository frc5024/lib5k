package frc.lib5k.control;

public class SlewLimiter {
    double limit;
    double output = 0.0;

    public SlewLimiter(double limit) {
        this.limit = limit;
    }

    public double feed(double value) {
        double error = value - output;
        if(error > limit){
            error = limit;
        }else if (error < (limit * -1)){
            error = limit * -1;
        }
        output += error;
        return output;
    }

    public void reset(){
        output = 0.0;
    }
    
}