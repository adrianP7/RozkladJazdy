package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy;

import android.util.Log;

import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.AlarmClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.Delay;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.SmartWatchWidgetClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopDetails.BusStopDetailsLine;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList.BusStop;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.lines.Line;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable.Time;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by adrian on 08.11.15.
 * This class contains data that must be available for all classes
 */
public class Repository {

    private String api ;
    private boolean ringtoneNotification;
    private boolean vibrationNotification;
    private boolean ledNotification;
    private boolean adminNotification;
    private boolean userNotification;
    private int rememberBefore;

    public int NOTIFICATION_ID = 0;
    private List<BusStop> przystanki;
    private List<BusStopDetailsLine> busStopDetailsLines;
    private List<Line> lines;
    private List<Track> tracks;
    private List<Time> times;
    private List<Delay> delays = new ArrayList<Delay>();
    private String currentlySelectedTime = "";
    private SmartWatchWidgetClass smartWatchWidgetClass;
    private AlarmClass alarmClass;
    private Calendar lastTicketContlorAlert;

    private String actualBusStop;
    private String busStopsType;
    private String currentBusStop;
    private String lineName;
    private String variant;
    private String firstStopName;
    private String lastStopName;
    private String firstStopID;
    public boolean timeTableActivityAlarmMode = false;
    private String selectedDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static Repository ourInstance = new Repository();

    public static Repository getInstance() {
        return ourInstance;
    }

    private Repository() {
        selectedDate = sdf.format(new Date());
        Log.d("SetData", selectedDate);
    }

    //setters and getters

    public void setApi(String api) { this.api = api;}
    public String getApi() { return api; }

    public String getRegisterPage() {
        return getApi().toString()+"/rejestruj_gmc.php";
    }


    public void setRingtoneNotification(Boolean ringtoneNotification) { this.ringtoneNotification = ringtoneNotification;}
    public Boolean getRingtoneNotification() { return ringtoneNotification; }

    public void setVibrationNotification(Boolean vibrationNotification) { this.vibrationNotification = vibrationNotification;}
    public Boolean getVibrationNotification() { return vibrationNotification; }

    public void setLedNotification(Boolean ledNotification) { this.ledNotification = ledNotification;}
    public Boolean getLedNotification() { return ledNotification; }

    public void setAdminNotification(Boolean adminNotification) { this.adminNotification = adminNotification;}
    public Boolean getAdminNotification() { return adminNotification; }

    public void setUserNotification(Boolean userNotification) { this.userNotification = userNotification;}
    public Boolean getUserNotification() { return userNotification; }

    public void setActualBusStop(String actualBusStop) { this.actualBusStop = actualBusStop;}
    public String getActualBusStop() { return actualBusStop; }

    public void setBusStopsType(String busStopsType) { this.busStopsType = busStopsType;}
    public String getBusStopsType() { return busStopsType; }

    public void setCurrentBusStop(String currentBusStop) { this.currentBusStop = currentBusStop;}
    public String getCurrentBusStop() { return currentBusStop; }

    public void setLineName(String lineName) { this.lineName = lineName;}
    public String getLineName() { return lineName; }

    public void setVariant(String variant) { this.variant = variant;}
    public String getVariant() { return variant; }

    public void setFirstStopName(String firstStopName) { this.firstStopName = firstStopName;}
    public String getFirstStopName() { return firstStopName; }

    public void setLastStopName(String lastStopName) { this.lastStopName = lastStopName;}
    public String getLastStopName() { return lastStopName; }

    public void setFirstStopID(String firstStopID) { this.firstStopID = firstStopID;}
    public String getFirstStopID() { return firstStopID; }


    public void setPrzystanki(List<BusStop> przystanki){
        this.przystanki = przystanki;
    }
    public List<BusStop> getPrzystanki(){
        return przystanki;
    }
    public void clearPrzystanki(){
        try{
            przystanki.clear();
        }catch(Exception e){}
    }

    public void setBusStopDetailsLines(List<BusStopDetailsLine> busStopDetailsLines){
        this.busStopDetailsLines = busStopDetailsLines;
    }
    public List<BusStopDetailsLine> getBusStopDetailsLines(){
        return busStopDetailsLines;
    }
    public void clearBusStopDetailsLines(){
        try{
            busStopDetailsLines.clear();
        }catch(Exception e){}
    }

    public void setLines(List<Line> lines) { this.lines = lines;}
    public List<Line> getLines() { return lines; }
    public void clearLines(){
        try{
            lines.clear();
        }catch(Exception e){}
    }

    public void setTrack(List<Track> tracks) {
        this.tracks = tracks;
        setTracksLabels();
    }
    public List<Track> getTracks() { return tracks; }
    public void clearTracks(){
        try{
            tracks.clear();
        }catch(Exception e){}
    }

    public void setTime(List<Time> times) {
        this.times = times;
        Collections.sort(times);
    }
    public List<Time> getTimes() { return times; }
    public void clearTimes(){
        try{
            times.clear();
        }catch(Exception e){}
    }

    public void setDelay(List<Delay> delays) { this.delays = delays;}
    public List<Delay> getDelays() { return delays; }
    public void clearDelays(){
        try{
            delays.clear();
        }catch(Exception e){}
    }

    public void setCurrentlySelectedTime(String currentlySelectedTime) { this.currentlySelectedTime = currentlySelectedTime;}
    public String getCurrentlySelectedTime() { return currentlySelectedTime; }
    public void clearCurrentlySelectedTime(){
        try{
            currentlySelectedTime="";
        }catch(Exception e){}
    }



    public void setSelectedDate(String selectedDate) { this.selectedDate = selectedDate;}
    public String getSelectedDate() { return selectedDate; }

    public void setRememberBefore(int rememberBefore) { this.rememberBefore = rememberBefore;}
    public int getRememberBefore() { return rememberBefore; }

    public void setLastTicketContlorAlert(Calendar lastTicketContlorAlert) { this.lastTicketContlorAlert = lastTicketContlorAlert;}
    public Calendar getLastTicketContlorAlert() { return lastTicketContlorAlert; }


    public void setSmartWatchWidgetClass(SmartWatchWidgetClass smartWatchWidgetClass) { this.smartWatchWidgetClass = smartWatchWidgetClass;}
    public SmartWatchWidgetClass getSmartWatchWidgetClass() { return smartWatchWidgetClass; }
    public void clearSmartWatchWidgetClass(){
        try{
            smartWatchWidgetClass=null;
        }catch(Exception e){}
    }

    public void setAlarmClass(AlarmClass alarmClass) { this.alarmClass = alarmClass;}
    public AlarmClass getAlarmClass() { return alarmClass; }
    public void clearAlarmClass(){
        try{
            alarmClass=null;
        }catch(Exception e){}
    }

    public void setTracksLabels(){
        String variant = "";
        Track tr = new Track();
        tr.nazwa_przystanku = "Nazwa przystanku";
        tr.opoznienie = "Opóźnienie";
        tr.nazwa_lini = "This_is_label_0";
        tracks.add(0, tr);
        for(int i =0; i<getTracks().size();i++){
            try {
                if (!tracks.get(i).wariant_trasy.equals(variant)) {
                    variant = tracks.get(i).wariant_trasy;
                    Track trx = new Track();
                    trx.nazwa_przystanku = "Trasa dla wariantu: "+variant;
                    trx.opoznienie = "";
                    trx.nazwa_lini = "This_is_label_1";
                    tracks.add(i, trx);
                }
            }catch(Exception e){}
        }
    }
}
