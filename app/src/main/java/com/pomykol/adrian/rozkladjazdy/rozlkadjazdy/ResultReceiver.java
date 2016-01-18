package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities.MainActivity;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.SetAlarm;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.SmartWatchWidgetClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by adrian on 17.11.15.
 */
public class ResultReceiver extends BroadcastReceiver{
    //This receiver receive information from other services

    private String lineName;
    private String currentBusStop;
    private Calendar timeCalendar;
    private String time;
    private String lastBusStop;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Log.d("ResultReceiver", "ID: "+extras.getString("ID"));
        if(extras.getString("ID") != null) {
            if(extras.getString("ID").equals("SmartWatchAPP")) {
                if(extras.getString("type").equals("refreshWidgetRequest")) {
                    //send notification to SmartWatch
                    Log.d("SEND2Watch", "send test notification to SmartWatch");
                    Intent logIntent = new Intent();
                    logIntent.putExtra("ID", "RozkladJazdyMainAPP");
                    logIntent.putExtra("type", "widget");

                    String objToStr = new Gson().toJson(Repository.getInstance().getSmartWatchWidgetClass());

                    logIntent.putExtra("class", objToStr);
                    logIntent.putExtra("from", "timeTable");
                    logIntent.setAction("com.pomykol.adrian.rozkladjazdy.smartWatchWidget.DATA");
                    context.sendBroadcast(logIntent);
                }
                if(extras.getString("type").equals("callApplication")) {
                    intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                if(extras.getString("type").equals("cancelAlarm")) {
                    //cancel alarm
                    SetAlarm setAlarm = new SetAlarm();
                    setAlarm.cancelAlarm(context, true);
                    setAlarm.saveAlarmToPreferences(context, null);
                }
            }
        }
        if(extras.getString("ID") != null) {
            if(extras.getString("ID").equals("RozkladJazdyMainAPP")) { //main app
                if (extras.getString("type").equals("setAlarm")) {
                    String info = "";
                    String info2 = "";
                    if (Repository.getInstance().getAlarmClass() != null) {
                        lineName = Repository.getInstance().getAlarmClass().getLine();
                        currentBusStop = Repository.getInstance().getAlarmClass().getCurrentBusStop();
                        timeCalendar = Repository.getInstance().getAlarmClass().getAlarm();
                        lastBusStop = Repository.getInstance().getAlarmClass().getLastBusStop();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String formatted = sdf.format(timeCalendar.getTime());
                        time = formatted.toString();
                        Calendar calNow = Calendar.getInstance();
                        long sub = (timeCalendar.getTimeInMillis() - calNow.getTimeInMillis());
                        long minutesToDeparture = sub/1000/60;
                        String timeleft = minutesToDeparture+"";//formatted.toString();
                        info = "Linia: " + lineName + " → " + lastBusStop + " Pozostało: " + timeleft + " min";
                        info2 = "Linia: " + lineName + ", " + currentBusStop + " → " + lastBusStop + "\n" + time + "\n" + "Pozostało: " + timeleft + " min";
                        saveWidgetToPreferences(null, context);
                        //send notification to SmartWatch
                        Log.d("SEND2Watch", "send test notification to SmartWatch");
                        Intent logIntent = new Intent();
                        logIntent.putExtra("ID", "RozkladJazdyMainAPP");
                        logIntent.putExtra("from", "Alarm");
                        logIntent.putExtra("message", info2);
                        logIntent.setAction("com.pomykol.adrian.rozkladjazdy.smartWatchNotification.NOTIFICATIONS");
                        context.sendBroadcast(logIntent);
                        Repository.getInstance().clearAlarmClass();
                    }
                    if(info.length()>2 && info2.length()>2)
                        triggerNotification(info, info2, context, intent);
                }
                if (extras.getString("type").equals("cancelNotification")) {
                    int id = intent.getIntExtra("notification_id", -1);
                    if (id != -1) {
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(id);
                    }
                }
            }
        }

    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void triggerNotification(String info, String info2, Context context, Intent intent) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_directions_bus_black_48dp)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(info2))
                        .setContentText(info)
                        .setVibrate(null)
                        .setSound(null)
                        .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0));

        //Notifications
        if(Repository.getInstance().getRingtoneNotification().equals(true)){
            //Ton
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
        }
        if(Repository.getInstance().getVibrationNotification().equals(true)){
            //Vibration
            mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }
        if(Repository.getInstance().getLedNotification().equals(true)){
            //LED
            mBuilder.setLights(ContextCompat.getColor(context, R.color.ledColour), 1000, 1000);
        }

        // Creates an explicit intent for an Activity
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(418, mBuilder.build());
    }
    private void saveWidgetToPreferences(SmartWatchWidgetClass smartWatchWidgetClass, Context context){
        Gson gson = new Gson();
        String json = gson.toJson(smartWatchWidgetClass);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("SmartWatchWidgetClass", json).commit();

    }
}