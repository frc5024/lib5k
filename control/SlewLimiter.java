package frc.common.control;

public class SlewLimiter {
    double limit;
    double output = 0.0;

    public SlewLimiter(double limit) {
        this.limit = limit;
    }

    public double feed(double value) {
        double error = value - this.output;
        if(error > this.limit){
            error = this.limit;
        }else if (error < (this.limit * -1)){
            error = this.limit * -1;
        }
        this.output += error;
        return this.output;
    }

    public void reset(){
        this.output = 0.0;
    }
    
}