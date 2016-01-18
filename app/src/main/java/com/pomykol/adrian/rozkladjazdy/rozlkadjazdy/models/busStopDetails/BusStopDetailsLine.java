package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopDetails;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adrian on 09.11.15.
 */
public class BusStopDetailsLine implements Serializable{
    @SerializedName("id_przystanku")
    public String id_przystanku;

    @SerializedName("nazwa_przystanku")
    public String nazwa_przystanku;

    @SerializedName("opis_przystanku")
    public String opis_przystanku;

    @SerializedName("id_trasy")
    public String id_trasy;

    @SerializedName("id_lini")
    public String id_lini;

    @SerializedName("opoznienie")
    public String opoznienie;

    @SerializedName("nazwa_lini")
    public String nazwa_lini;

    @SerializedName("opis_lini")
    public String opis_lini;

    @SerializedName("wariant_trasy")
    public String wariant_trasy;

    @SerializedName("pierwszy_przystanek")
    public String pierwszy_przystanek;

    @SerializedName("nazwa_pierwszy_przystanek")
    public String nazwa_pierwszy_przystanek;

    @SerializedName("ostatni_przystanek")
    public String ostatni_przystanek;

}
