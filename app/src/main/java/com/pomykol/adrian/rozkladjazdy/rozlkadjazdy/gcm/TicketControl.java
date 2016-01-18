package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.gcm;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 19.11.15.
 */
public class TicketControl {

    public void postData(String message) {
        String messageToAll = message;
        try {
            messageToAll = URLEncoder.encode(message, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        final String finalMessageToAll = messageToAll;
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    // Create a new HttpClient and Post Header
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://apom.hcore.pl/rozklad/wyslij_wiadomosc_uzytkownik.php");

                    try {
                        // Add your data
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        //nameValuePairs.add(new BasicNameValuePair("id", "0"));
                        nameValuePairs.add(new BasicNameValuePair("message", finalMessageToAll));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        // Execute HTTP Post Request
                        HttpResponse response = httpclient.execute(httppost);

                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                    }catch(Exception e){
                        Log.d("BŁAD!!", e.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }
}
