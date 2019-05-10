package frc.robot.common;

import java.util.ArrayList;

public class Logger{
    double period;
    ArrayList<String> buffer = new ArrayList<String>();
    
    public Logger(double period){
        this.period = period;
    }

    public void log(String x){
        this.buffer.add(x);
    }

    public void display(){
        for (String x : this.buffer){
            System.out.println(x);
        }
        this.buffer = new ArrayList<String>();
    }

}