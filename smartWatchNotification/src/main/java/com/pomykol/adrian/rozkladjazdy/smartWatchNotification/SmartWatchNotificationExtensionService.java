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

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

/**
 * The extension service handles extension registration and inserts data
 * into the notification database.
 */
public class SmartWatchNotificationExtensionService extends ExtensionService {

    /** Extension specific id for the source. */
    public static final String EXTENSION_SPECIFIC_ID = "EXTENSION_SPECIFIC_ID_SAMPLE_NOTIFICATION";

    public static final String LOG_TAG = "SmartWatchNotification";

    public SmartWatchNotificationExtensionService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate: SmartWatchNotificationExtensionService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy: SmartWatchNotificationExtensionService");
    }

    @Override
    protected void onViewEvent(Intent intent) {
        String action = intent.getStringExtra(Notification.Intents.EXTRA_ACTION);
        int eventId = intent.getIntExtra(Notification.Intents.EXTRA_EVENT_ID, -1);
        if (Notification.SourceColumns.ACTION_1.equals(action)) {
            doAction1(eventId);
        } else if (Notification.SourceColumns.ACTION_2.equals(action)) {
            doAction2(eventId);
        } else if (Notification.SourceColumns.ACTION_3.equals(action)) {
            doAction3(eventId);
        }
    }

    @Override
    protected void onRefreshRequest() {
        // Do nothing. Only relevant for polling extensions.
    }

    /**
     * Shows a toast on the phone with the information associated with an
     * event.
     *
     * @param eventId The event id
     */
    public void doAction1(int eventId) { //when first button tap
        Log.d(LOG_TAG, "doAction1 event id: " + eventId);
        Cursor cursor = null;
        try {
            Intent logIntent = new Intent();
            logIntent.putExtra("ID", "SmartWatchAPP");
            logIntent.putExtra("type", "callApplication");
            logIntent.setAction("com.pomykol.adrian.rozkladjazdy.ResultReceiver.SMARTWATCH");
            getApplicationContext().sendBroadcast(logIntent);

        } catch (SQLException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public void doAction2(int eventId) { //when 2nd button tap
        Log.d(LOG_TAG, "doAction1 event id: " + eventId);
        Cursor cursor = null;
        try {
            Intent logIntent = new Intent();
            logIntent.putExtra("ID", "SmartWatchAPP");
            logIntent.putExtra("type", "cancelAlarm");
            logIntent.setAction("com.pomykol.adrian.rozkladjazdy.ResultReceiver.SMARTWATCH");
            getApplicationContext().sendBroadcast(logIntent);

        } catch (SQLException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public void doAction3(int eventId) { //when 3rd button tap
        Log.d(LOG_TAG, "doAction1 event id: " + eventId);
        Cursor cursor = null;
        try {
            //return
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new SmartWatchNotificationRegistrationInformation(this);
    }

    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }
}
