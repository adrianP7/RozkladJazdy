package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by adrian on 06.11.15.
 */
public class BusStop implements Serializable{

    public BusStop(BusStop busStop) {
        // Copy all the fields of BusStop.
        this.id_przystanku = busStop.id_przystanku;
        this.nazwa_przystanku = busStop.nazwa_przystanku;
        this.opis_przystanku = busStop.opis_przystanku;
        this.przystanki = busStop.przystanki;
        this.poczatek_trasy=busStop.poczatek_trasy;
    }

    @SerializedName("id_przystanku")
    public String id_przystanku;

    @SerializedName("nazwa_przystanku")
    public String nazwa_przystanku;

    @SerializedName("opis_przystanku")
    public String opis_przystanku;

    @SerializedName("przystanki")
    public String przystanki;

    @SerializedName("poczatek_trasy")
    public String poczatek_trasy;

}