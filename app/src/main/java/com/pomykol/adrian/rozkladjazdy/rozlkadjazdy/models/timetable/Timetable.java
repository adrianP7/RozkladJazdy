package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable;

import com.google.gson.annotations.SerializedName;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList.BusStop;

import java.io.Serializable;
import java.util.List;

/**
 * Created by adrian on 10.11.15.
 */
public class Timetable implements Serializable {
    @SerializedName("trasa")
    public List<Track> track;

    @SerializedName("godziny")
    public List<Time> time;
}
