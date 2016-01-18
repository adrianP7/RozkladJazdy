package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.lines;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adrian on 09.11.15.
 */
public class Line implements Serializable{

    public Line(Line line) {
        // Copy all the fields of Line.
        this.id_lini = line.id_lini;
        this.nazwa_lini = line.nazwa_lini;
        this.opis_lini = line.opis_lini;
        this.wariant_trasy = line.wariant_trasy;
        this.pierwszy_przystanek=line.pierwszy_przystanek;
        this.nazwa_pierwszy_przystanek=line.nazwa_pierwszy_przystanek;
        this.ostatni_przystanek=line.ostatni_przystanek;
    }

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

    @SerializedName("nazwa_pierwszy_przystanek")
    public String nazwa_pierwszy_przystanek;

    @SerializedName("ostatni_przystanek")
    public String ostatni_przystanek;

}
