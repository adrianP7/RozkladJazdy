package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.gcm.RegisterApp;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.SetAlarm;

import java.util.Calendar;

//settings activity class
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_SYNC_CONN = "pref_syncConnectionType";
    private boolean ringtone;
    private boolean vibration;
    private boolean ledNotf;
    Preference registerGCM;
    Preference adminNotGCM;
    Preference userNotGCM;
    private boolean adminNotf;
    private boolean userNotf;

    //for GCM
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "GCMRelated";
    GoogleCloudMessaging gcm;
    String regid;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set preferences
        addPreferencesFromResource(R.xml.preferences);
        CheckBoxPreference prefRingtone = (CheckBoxPreference) findPreference("pref_key_set_ringtone");
        prefRingtone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ringtone = Boolean.valueOf(newValue.toString());
                Repository.getInstance().setRingtoneNotification(ringtone);
                return true;
            }
        });
        CheckBoxPreference prefVibr = (CheckBoxPreference) findPreference("pref_key_set_vibration");
        prefVibr.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                vibration = Boolean.valueOf(newValue.toString());
                Repository.getInstance().setVibrationNotification(vibration);
                return true;
            }
        });
        CheckBoxPreference prefLed = (CheckBoxPreference) findPreference("pref_key_set_led");
        prefLed.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ledNotf = Boolean.valueOf(newValue.toString());
                Repository.getInstance().setLedNotification(ledNotf);
                return true;
            }
        });
        CheckBoxPreference prefAdmin = (CheckBoxPreference) findPreference("pref_key_set_notification_from_admin");
        prefAdmin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                adminNotf = Boolean.valueOf(newValue.toString());
                Repository.getInstance().setAdminNotification(adminNotf);
                return true;
            }
        });
        CheckBoxPreference prefUser = (CheckBoxPreference) findPreference("pref_key_set_notification_from_user");
        prefUser.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                userNotf = Boolean.valueOf(newValue.toString());
                Repository.getInstance().setUserNotification(userNotf);
                return true;
            }
        });

        registerGCM = findPreference("pref_key_enable_gcm");
        adminNotGCM = findPreference("pref_key_set_notification_from_admin");
        userNotGCM = findPreference("pref_key_set_notification_from_user");

        //GCM
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            regid = getRegistrationId(getApplicationContext());
            if(!regid.isEmpty()){
                registerGCM.setEnabled(false);
                adminNotGCM.setEnabled(true);
                userNotGCM.setEnabled(true);
            }else{
                registerGCM.setEnabled(true);
                adminNotGCM.setEnabled(false);
                userNotGCM.setEnabled(false);
            }
        }
        registerGCM.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (checkPlayServices()) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    regid = getRegistrationId(getApplicationContext());

                    if (regid.isEmpty()) {
                        registerGCM.setEnabled(false);
                        adminNotGCM.setEnabled(true);
                        userNotGCM.setEnabled(true);

                        Preference prefAdmin = findPreference("pref_key_set_notification_from_admin");
                        if (prefAdmin instanceof CheckBoxPreference) {
                            CheckBoxPreference check = (CheckBoxPreference)prefAdmin;
                            check.setChecked(true);
                            Repository.getInstance().setAdminNotification(true);}
                        Preference prefUser = findPreference("pref_key_set_notification_from_user");
                        if (prefUser instanceof CheckBoxPreference) {
                            CheckBoxPreference check = (CheckBoxPreference)prefUser;
                            check.setChecked(true);
                            Repository.getInstance().setUserNotification(true);
                        }

                        new RegisterApp(getApplicationContext(), gcm, getAppVersion(getApplicationContext())).execute();
                    }else{
                        Toast.makeText(getApplicationContext(), "Device already Registered", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_SYNC_CONN)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
        if ("pref_key_api_set".equals(key)) {
            Repository.getInstance().setApi(sharedPreferences.getString("pref_key_api_set", ""));
        }
        if ("pref_key_alarm_alert".equals(key)) {
            String val = sharedPreferences.getString("pref_key_alarm_alert", "");
            if (val == null || val.trim().equals("") || val.trim().equals("0")) {
                val = Repository.getInstance().getRememberBefore()+"";
                EditTextPreference editTextPreference = (EditTextPreference) findPreference("pref_key_alarm_alert");
                editTextPreference.setText(val);
                sharedPreferences.edit().putString(key, val).commit();
                Toast.makeText(getApplicationContext(),R.string.null_remember_before, Toast.LENGTH_LONG).show();
            }
                if (!sharedPreferences.getString("pref_key_alarm_alert", "").equals("")) {
                    Repository.getInstance().setRememberBefore(Integer.parseInt(sharedPreferences.getString("pref_key_alarm_alert", "")));
                    if (Repository.getInstance().getAlarmClass() != null) {
                        SetAlarm setAlarm = new SetAlarm();
                        Calendar calNow = Calendar.getInstance();
                        Calendar calSet = (Calendar) Repository.getInstance().getAlarmClass().getAlarm().clone();
                        int minuteBefore = Repository.getInstance().getRememberBefore();
                        minuteBefore = 0 - minuteBefore;
                        calSet.add(Calendar.MINUTE, minuteBefore);
                        if (calSet.getTimeInMillis() - calNow.getTimeInMillis() > 60000) {
                            setAlarm.setAlarm(getApplicationContext());
                            setAlarm.saveAlarmToPreferences(getApplicationContext(), Repository.getInstance().getAlarmClass());
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.given_date_has_already_been, Toast.LENGTH_LONG).show();
                            setAlarm.cancelAlarm(getApplicationContext(), true);
                        }
                    }
                }
        }

        if ("pref_key_set_ringtone".equals(key)) {
            Repository.getInstance().setRingtoneNotification(ringtone);
            Log.d("ringtone", ringtone + "");
        }

        if ("pref_key_set_vibration".equals(key)) {
            Repository.getInstance().setRingtoneNotification(vibration);
            Log.d("vibration", vibration+"" );
        }

        if ("pref_key_set_led".equals(key)) {
            Repository.getInstance().setRingtoneNotification(ledNotf);
            Log.d("ledNotf", ledNotf+"" );
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    //GCM
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(getApplicationContext());
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }


    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}