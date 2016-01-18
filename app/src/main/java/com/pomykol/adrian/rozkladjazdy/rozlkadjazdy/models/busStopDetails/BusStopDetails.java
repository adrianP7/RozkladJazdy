package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopDetails;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by adrian on 09.11.15.
 */
public class BusStopDetails implements Serializable {
    @SerializedName("lista_lini")
    public List<BusStopDetailsLine> busStops;
}
