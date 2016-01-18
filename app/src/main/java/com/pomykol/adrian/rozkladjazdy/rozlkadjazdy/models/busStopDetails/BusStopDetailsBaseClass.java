package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopDetails;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adrian on 09.11.15.
 */
public class BusStopDetailsBaseClass implements Serializable {
    @SerializedName("przystanek")
    public BusStopDetails busStopDetails;
}
