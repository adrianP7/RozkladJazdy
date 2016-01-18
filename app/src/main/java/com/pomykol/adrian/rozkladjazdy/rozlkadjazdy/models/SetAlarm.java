package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import android.widget.Toast;

import com.google.gson.Gson;
import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.ResultReceiver;

import java.util.Calendar;

/**
 * Created by adrian on 01.12.15.
 * This class sets the alarm on android
 */
public class SetAlarm {

    public void setAlarm(Context context){

        Intent intent = new Intent(context, ResultReceiver.class);
        intent.putExtra("ID", "RozkladJazdyMainAPP");
        intent.putExtra("type", "setAlarm");
        intent.setAction("com.pomykol.adrian.rozkladjazdy.ResultReceiver.ALARM");

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 41536, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        calSet = (Calendar) Repository.getInstance().getAlarmClass().getAlarm().clone();
        int minuteBefore = Repository.getInstance().getRememberBefore();
        minuteBefore = 0 - minuteBefore;
        calSet.add(Calendar.MINUTE, minuteBefore);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), sentPI);

    }

    public void cancelAlarm(Context context, Boolean toast) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ResultReceiver.class);
        manager.cancel(PendingIntent.getService(context, 41536, intent, 0));
        Repository.getInstance().clearAlarmClass();
        saveAlarmToPreferences(context, null);
        if(toast)
            Toast.makeText(context, R.string.alarm_canceled, Toast.LENGTH_SHORT).show();
    }

    public void saveAlarmToPreferences(Context context, AlarmClass alarmClass){
        Gson gson = new Gson();
        String json = gson.toJson(alarmClass);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("AlarmClass", json).commit();
    }
}
