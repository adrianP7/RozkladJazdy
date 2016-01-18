package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adrian on 10.11.15.
 */
public class Time implements Serializable, Comparable<Time> {
    @SerializedName("nazwa_lini")
    public String nazwa_lini;

    @SerializedName("opis_lini")
    public String opis_lini;

    @SerializedName("wariant_trasy")
    public String wariant_trasy;

    @SerializedName("pierwszy_przystanek")
    public String pierwszy_przystanek;

    @SerializedName("dzien_tyg")
    public String dzien_tyg;

    @SerializedName("godzina")
    public String godzina;

    @Override
    public int compareTo(Time o) {
        int compareTime = godzina.compareTo(o.godzina);
            return compareTime;
    }
}
