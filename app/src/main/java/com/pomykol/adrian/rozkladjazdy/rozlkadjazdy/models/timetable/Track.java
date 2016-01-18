package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by adrian on 10.11.15.
 */
public class Track implements Serializable {
    @SerializedName("id_lini")
    public String id_lini;

    @SerializedName("nazwa_lini")
    public String nazwa_lini;

    @SerializedName("opis_lini")
    public String opis_lini;

    @SerializedName("wariant_trasy")
    public String wariant_trasy;

    @SerializedName("pierwszy_przystanek")
    public String pierwszy_przystanek;

    @SerializedName("id_trasy")
    public String id_trasy;

    @SerializedName("id_przystanku")
    public String id_przystanku;

    @SerializedName("opoznienie")
    public String opoznienie;

    @SerializedName("nazwa_przystanku")
    public String nazwa_przystanku;

    @SerializedName("opis_przystanku")
    public String opis_przystanku;


}
