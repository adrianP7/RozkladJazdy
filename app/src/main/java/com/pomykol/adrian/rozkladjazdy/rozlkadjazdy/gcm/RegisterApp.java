package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.gcm;

/**
 * Created by adrian on 18.11.15.
 */
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities.MainActivity;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class RegisterApp extends AsyncTask<Void, Void, String> {
    //This class registration application in the service GCM

    private static final String TAG = "GCM";
    Context ctx;
    GoogleCloudMessaging gcm;
    String SENDER_ID = "57425428224";
    String regid = null;
    private int appVersion;
    public RegisterApp(Context ctx, GoogleCloudMessaging gcm, int appVersion){
        this.ctx = ctx;
        this.gcm = gcm;
        this.appVersion = appVersion;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(Void... arg0) {
        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(ctx);
            }
            gcm.unregister();
            regid = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regid;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            sendRegistrationIdToBackend();

            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the regID - no need to register again.
            storeRegistrationId(ctx, regid);
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return msg;
    }

    private void storeRegistrationId(Context ctx, String regid) {
        final SharedPreferences prefs = ctx.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("registration_id", regid);
        editor.putInt("appVersion", appVersion);
        editor.commit();
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putBoolean("gcmEnable", true).commit();
    }


    private void sendRegistrationIdToBackend() {
        URI url = null;
        try {
            url = new URI(Repository.getInstance().getRegisterPage()+"?regId=" + regid);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(url);
        try {
            httpclient.execute(request);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(ctx, R.string.gcm_registration_completed, Toast.LENGTH_SHORT).show();
        Log.v(TAG, result);
    }
}