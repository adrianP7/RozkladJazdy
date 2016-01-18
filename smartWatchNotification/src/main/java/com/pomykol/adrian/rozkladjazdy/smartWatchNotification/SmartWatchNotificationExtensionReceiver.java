/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.pomykol.adrian.rozkladjazdy.smartWatchNotification;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;


/**
 * The extension receiver receives the extension intents and starts the
 * extension service when they arrive.
 */
public class SmartWatchNotificationExtensionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.d(SmartWatchNotificationExtensionService.LOG_TAG, "onReceive: " + intent.getAction());
        intent.setClass(context, SmartWatchNotificationExtensionService.class);
        context.startService(intent);

        try {
            Bundle extras = intent.getExtras();
            if(extras.getString("ID") != null)
                if (extras.getString("ID").equals("RozkladJazdyMainAPP")) {
                    String from = extras.getString("from");
                    String message = extras.getString("message");
                    addData(context, from, message);
                }
        }catch (Exception e){Log.d("BŁAD odb", e.toString());}
    }


    private void addData(Context context, String from, String message) {
        long time = System.currentTimeMillis();
        long sourceId = NotificationUtil.getSourceId(context,
                SmartWatchNotificationExtensionService.EXTENSION_SPECIFIC_ID);
        if (sourceId == NotificationUtil.INVALID_ID) {
            Log.e(SmartWatchNotificationExtensionService.LOG_TAG, "Failed to insert data");
            return;
        }
        String profileImage = ExtensionUtils.getUriString(context,
                R.drawable.ic_directions_bus_black_92dp);

        // Build the notification.
        ContentValues eventValues = new ContentValues();
        eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
        eventValues.put(Notification.EventColumns.DISPLAY_NAME, from);
        eventValues.put(Notification.EventColumns.MESSAGE, message);
        eventValues.put(Notification.EventColumns.PERSONAL, 1);
        eventValues.put(Notification.EventColumns.PROFILE_IMAGE_URI, profileImage);
        eventValues.put(Notification.EventColumns.PUBLISHED_TIME, time);
        eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);

        NotificationUtil.addEvent(context, eventValues);
    }
}
