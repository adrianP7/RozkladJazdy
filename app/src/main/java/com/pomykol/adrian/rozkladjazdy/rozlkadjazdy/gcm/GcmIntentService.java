package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.gcm;

/**
 * Created by adrian on 18.11.15.
 */
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities.MainActivity;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.ResultReceiver;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Calendar;

public class GcmIntentService extends IntentService {
    //public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "GcmIntentService";
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            //errors
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotificationFromAdmin("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotificationFromAdmin("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                // Post notification of received message.
                if (extras.getString("who").equals("admin")){ //if admin message detected
                    if (Repository.getInstance().getAdminNotification() == true){
                        sendNotificationFromAdmin(extras.getString("Notice"));
                    }
                }else if (extras.getString("who").equals("user")){ //if user message detected
                    if(Repository.getInstance().getUserNotification() == true){
                        sendNotificationFromUser(extras.getString("Notice"));
                    }
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotificationFromAdmin(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Wiadomość od Administratora")
                        .setSmallIcon(R.drawable.ic_directions_bus_black_48dp)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true)
                        .setContentText(msg)
                        .setVibrate(null)
                        .setSound(null)
        .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));

        //send notification to SmartWatch
        Log.d("SEND2Watch", "send test notification to SmartWatch");
        Intent logIntent = new Intent();
        logIntent.putExtra("ID", "RozkladJazdyMainAPP");
        logIntent.putExtra("from", "Administrator");
        logIntent.putExtra("message", msg);
        logIntent.setAction("com.pomykol.adrian.rozkladjazdy.smartWatchNotification.NOTIFICATIONS");
        sendBroadcast(logIntent);

        //Notifications
        if(Repository.getInstance().getRingtoneNotification().equals(true)){
            //Tone
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
        }
        if(Repository.getInstance().getVibrationNotification().equals(true)){
            //Vibration
            mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }
        if(Repository.getInstance().getLedNotification().equals(true)){
            //LED
            mBuilder.setLights(ContextCompat.getColor(getApplicationContext(), R.color.ledColour), 1000, 1000);
        }
        mNotificationManager.notify(Repository.getInstance().NOTIFICATION_ID, mBuilder.build());
        Repository.getInstance().NOTIFICATION_ID++;
    }

    private void sendNotificationFromUser(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Wiadomość o kontroli")
                        .setSmallIcon(R.drawable.ic_directions_bus_black_48dp)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true)
                        .setContentText(msg)
                        .setVibrate(null)
                        .setSound(null)
                        .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));


        //send notification to SmartWatch
        Log.d("SEND2Watch", "send test notification to SmartWatch");
        Intent logIntent = new Intent();
        logIntent.putExtra("ID", "RozkladJazdyMainAPP");
        logIntent.putExtra("from", "Kontrola biletów");
        logIntent.putExtra("message", msg);
        logIntent.setAction("com.pomykol.adrian.rozkladjazdy.smartWatchNotification.NOTIFICATIONS");
        sendBroadcast(logIntent);

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
            mBuilder.setLights(ContextCompat.getColor(getApplicationContext(), R.color.ledColour), 1000, 1000);
        }

        mNotificationManager.notify(Repository.getInstance().NOTIFICATION_ID, mBuilder.build());
        //validity of the notification
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), ResultReceiver.class);
        intent.setAction("com.pomykol.adrian.rozkladjazdy.ResultReceiver.CANCEL_NOTIFICATION");
        intent.putExtra("ID", "RozkladJazdyMainAPP");
        intent.putExtra("type", "cancelNotification");
        intent.putExtra("notification_id", Repository.getInstance().NOTIFICATION_ID);
        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 41536, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sentPI);

        Repository.getInstance().NOTIFICATION_ID++;
    }
}