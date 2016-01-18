package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models;

import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopDetails.BusStopDetailsBaseClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList.BusStopsBaseClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.lines.LinesBaseClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable.TimetableBaseClass;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by adrian on 06.11.15.
 * Class used by retrofit. It contains www pages from which data is being retrieved.
 */
public interface RetrofitService {
    @GET("/api/aprzystanki.php")
    void aprzystanki(Callback<BusStopsBaseClass> response);

    @GET("/api/aprzystanek.php")
    void aprzystanek(@Query("id") int id_stop, Callback<BusStopDetailsBaseClass> response);

    @GET("/api/alinie.php")
    void alinie(Callback<LinesBaseClass> response);

    @GET("/api/alinia_rozklad.php")
    void alinia_rozklad(@Query("pierwszy_przystanek") String firstStopID, @Query("nazwa_lini") String lineName, @Query("data") String date, Callback<TimetableBaseClass> response);
}
