package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models;

import java.util.Calendar;

/**
 * Created by adrian on 17.11.15.
 * Class sets alarm
 */
public class AlarmClass {
    private Calendar alarm;
    private String line;
    private String currentBusStop;
    private String firstBusStop;
    private String lastBusStop;


    public void setAlarm(Calendar alarm) { this.alarm = alarm;}
    public Calendar getAlarm() { return alarm; }
    public void clearAlarm(){
        try{
            alarm=null;
        }catch(Exception e){}
    }

    public void setLine(String line){ this.line = line; }
    public String getLine(){ return line; }

    public void setCurrentBusStop(String currentBusStop){ this.currentBusStop = currentBusStop; }
    public String getCurrentBusStop(){ return currentBusStop; }

    public void setFirstBusStop(String firstBusStop){ this.firstBusStop = firstBusStop; }
    public String getFirstBusStop(){ return firstBusStop; }

    public void setLastBusStop(String lastBusStop){ this.lastBusStop = lastBusStop; }
    public String getLastBusStop(){ return lastBusStop; }
}
