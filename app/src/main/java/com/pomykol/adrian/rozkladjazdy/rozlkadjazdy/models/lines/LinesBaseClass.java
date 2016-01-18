package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.lines;

import com.google.gson.annotations.SerializedName;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList.BusStops;

import java.io.Serializable;

/**
 * Created by adrian on 09.11.15.
 */
public class LinesBaseClass implements Serializable {
    @SerializedName("linie")
    public Lines lines;
}
