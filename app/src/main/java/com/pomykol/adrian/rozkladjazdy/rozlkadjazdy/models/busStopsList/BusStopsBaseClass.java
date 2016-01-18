package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


/**
 * Created by adrian on 08.11.15.
 */
public class BusStopsBaseClass implements Serializable {

    @SerializedName("przystanki")
    public BusStops busStops;
}