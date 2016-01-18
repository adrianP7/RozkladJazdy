package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable;

import com.google.gson.annotations.SerializedName;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList.BusStops;

import java.io.Serializable;

/**
 * Created by adrian on 10.11.15.
 */
public class TimetableBaseClass implements Serializable{
    @SerializedName("linia")
    public Timetable line;
}
