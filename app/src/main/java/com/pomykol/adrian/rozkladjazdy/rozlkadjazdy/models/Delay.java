package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models;

/**
 * Created by adrian on 11.11.15.
 * Class sets the delay
 */
public class Delay {
    private int delay;
    private String variant;

    public void setDelay(int delay){
        this.delay=delay;
    }
    public int getDelay(){
        return delay;
    }

    public void setVariant(String variant){
        this.variant=variant;
    }
    public String getVariant(){
        return variant;
    }
}
