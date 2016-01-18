package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.lines;

import com.google.gson.annotations.SerializedName;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList.BusStop;

import java.io.Serializable;
import java.util.List;

/**
 * Created by adrian on 09.11.15.
 */
public class Lines implements Serializable {
    @SerializedName("lista")
    public List<Line> lines;
}
