package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by adrian on 08.11.15.
 */
public class BusStops implements Serializable{

    @SerializedName("lista")
    public List<BusStop> przystanki;

}
