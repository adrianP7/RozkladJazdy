package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models;

import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 16.11.15.
 * This class sets the data for the widget
 */
public class SmartWatchWidgetClass {
    private String line;
    private String currentBusStop;
    private String firstBusStop;
    private String lastBusStop;
    private String selectedDate;
    private String firstStopId;
    private List<String> timesList = new ArrayList<>();

    public void setLine(String line){ this.line = line; }
    public String getLine(){ return line; }

    public void setCurrentBusStop(String currentBusStop){ this.currentBusStop = currentBusStop; }
    public String getCurrentBusStop(){ return currentBusStop; }

    public void setFirstBusStop(String firstBusStop){ this.firstBusStop = firstBusStop; }
    public String getFirstBusStop(){ return firstBusStop; }

    public void setLastBusStop(String lastBusStop){ this.lastBusStop = lastBusStop; }
    public String getLastBusStop(){ return lastBusStop; }

    public void setTimesList(List<String> timesList){ this.timesList = timesList; }
    public List<String> getTimesList(){ return timesList; }

    public void setSelectedDate(String selectedDate) { this.selectedDate = selectedDate;}
    public String getSelectedDate() { return selectedDate; }

    public void setFirstStopId(String firstStopId) { this.firstStopId = firstStopId;}
    public String getFirstStopId() { return firstStopId; }

}
