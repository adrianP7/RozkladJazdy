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

 * Neither the name of the Sony Ericsson Mobile Communications AB nor the names
 of its contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

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

package com.pomykol.adrian.rozkladjazdy.smartWatchWidget;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


class SmartWatchWidget extends ControlExtension {

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.RGB_565;

    private int mWidth = 220;
    private int mHeight = 176;

    TextView lineName;
    TextView currentStop;
    TextView lastStop;
    TextView departure;
    TextView nextDeparture;
    TextView remainingTime;
    TextView minutes;
    ImageView departuresNotValid;

    boolean refresh = false;
    boolean showProblem = false;

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case Control.Intents.SWIPE_DIRECTION_UP:
                break;
            case Control.Intents.SWIPE_DIRECTION_LEFT:
                break;
            case Control.Intents.SWIPE_DIRECTION_DOWN:
                break;
            case Control.Intents.SWIPE_DIRECTION_RIGHT:
                break;
            default:
                break;
        }
    }
    @Override
    public void onTouch(final ControlTouchEvent event) {
        int action = event.getAction();
        switch(action) {
            case Control.Intents.TOUCH_ACTION_PRESS:
                break;
            case Control.Intents.TOUCH_ACTION_RELEASE:
                refresh=false;
                updateCurrentDisplay();
                break;
            case Control.Intents.TOUCH_ACTION_LONGPRESS:
                if(refresh==false){
                    refresh = true;
                    refreshRequest();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Creates a control extension.
     *
     * @param hostAppPackageName Package name of host application.
     * @param context The context.
     */
    SmartWatchWidget(final String hostAppPackageName, final Context context) {
        super(context, hostAppPackageName);
        // Determine host application screen size.
        determineSize(context, hostAppPackageName);
    }
    @Override
    public void onResume() {
        Log.d(SmartWatchWidgetExtensionService.LOG_TAG, "Starting control");
        updateCurrentDisplay();
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
    }

    /**
     * Checks if the control extension supports the given width.
     *
     * @param context The context.
     * @param width width The width.
     * @return True if the control extension supports the given width.
     */
    public static boolean isWidthSupported(Context context, int width) {
        return width == context.getResources().getDimensionPixelSize(
                R.dimen.smart_watch_2_control_width)
                || width == context.getResources().getDimensionPixelSize(
                R.dimen.smart_watch_control_width);
    }

    /**
     * Checks if the control extension supports the given height.
     *
     * @param context The context.
     * @param height height The height.
     * @return True if the control extension supports the given height.
     */
    public static boolean isHeightSupported(Context context, int height) {
        return height == context.getResources().getDimensionPixelSize(
                R.dimen.smart_watch_2_control_height)
                || height == context.getResources().getDimensionPixelSize(
                R.dimen.smart_watch_control_height);
    }

    /**
     * Determines the width and height in pixels of a given host application.
     *
     * @param context The context.
     * @param hostAppPackageName The host application.
     */
    private void determineSize(Context context, String hostAppPackageName) {
        Log.d(SmartWatchWidgetExtensionService.LOG_TAG, "Now determine screen size.");

        boolean smartWatch2Supported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(context,
                hostAppPackageName);
        if (smartWatch2Supported) {
            mWidth = context.getResources().getDimensionPixelSize(
                    R.dimen.smart_watch_2_control_width);
            mHeight = context.getResources().getDimensionPixelSize(
                    R.dimen.smart_watch_2_control_height);
        } else {
            mWidth = context.getResources()
                    .getDimensionPixelSize(R.dimen.smart_watch_control_width);
            mHeight = context.getResources().getDimensionPixelSize(
                    R.dimen.smart_watch_control_height);
        }
    }

    private void updateCurrentDisplay() {
        updateGenericDisplay();
    }


    private void updateGenericDisplay() {
        // Create bitmap to draw in.
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, BITMAP_CONFIG);

        // Set default density to avoid scaling.
        bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        LinearLayout root = new LinearLayout(mContext);
        root.setLayoutParams(new ViewGroup.LayoutParams(mWidth, mHeight));
        root.setGravity(Gravity.CENTER);

        LayoutInflater inflater = (LayoutInflater)mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout widgetLayout = (LinearLayout)inflater.inflate(R.layout.widget_layout,
                root, true);

        //set widget
        lineName = (TextView)widgetLayout.findViewById(R.id.line_name);
        currentStop = (TextView)widgetLayout.findViewById(R.id.current_stop);
        lastStop = (TextView)widgetLayout.findViewById(R.id.last_stop);
        departure = (TextView)widgetLayout.findViewById(R.id.departure);
        nextDeparture = (TextView)widgetLayout.findViewById(R.id.next_departure);
        remainingTime = (TextView)widgetLayout.findViewById(R.id.remaining_time);
        minutes = (TextView)widgetLayout.findViewById(R.id.minutes);
        departuresNotValid = (ImageView)widgetLayout.findViewById(R.id.departures_not_valid);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(WidgetSingleton.getInstance().getElement() != null){
            if (WidgetSingleton.getInstance().getElement().getSelectedDate().equals(sdf.format(new Date()))){
                showProblem = false;
            }else {
                showProblem = true;
            }
        }
        if (showProblem==true){
            departuresNotValid.setVisibility(View.VISIBLE);
        }else{
            departuresNotValid.setVisibility(View.GONE);
        }
            if(WidgetSingleton.getInstance().getElement() != null){
                if(WidgetSingleton.getInstance().getElement().getLine().length()>9){
                    lineName.setText(WidgetSingleton.getInstance().getElement().getLine().substring(0,9)+".");
                }else {
                    lineName.setText(WidgetSingleton.getInstance().getElement().getLine());
                }
                if(WidgetSingleton.getInstance().getElement().getCurrentBusStop().length()>14){
                    currentStop.setText(WidgetSingleton.getInstance().getElement().getCurrentBusStop().substring(0,14)+".");
                }else {
                    currentStop.setText(WidgetSingleton.getInstance().getElement().getCurrentBusStop());
                }
                if(WidgetSingleton.getInstance().getElement().getLastBusStop().length()>14){
                    lastStop.setText(WidgetSingleton.getInstance().getElement().getLastBusStop().substring(0,14)+".");
                }else {
                    lastStop.setText(WidgetSingleton.getInstance().getElement().getLastBusStop());
                }
                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();


                List<String> times = WidgetSingleton.getInstance().getElement().getTimesList();
                int timesSize = times.size();
                int min;
                int hour;
                boolean end = false;
                for(int i=0; i<timesSize; i++){
                    hour = Integer.parseInt(times.get(i).substring(0, 2));
                    min = Integer.parseInt(times.get(i).substring(3, 5));

                    calSet = (Calendar) calNow.clone();
                    calSet.set(Calendar.HOUR_OF_DAY, hour);
                    calSet.set(Calendar.MINUTE, min);
                    if(calNow.getTimeInMillis() < calSet.getTimeInMillis()){
                            departure.setText(times.get(i)+" ");
                        if ((i+1)<timesSize){
                            nextDeparture.setText(times.get(i+1));
                        }
                        long timeToDeparture = calSet.getTimeInMillis() - calNow.getTimeInMillis();
                        timeToDeparture = timeToDeparture/60000;

                        if (timeToDeparture>60){
                            remainingTime.setText(">1h");
                            minutes.setText("");
                        }else {
                            remainingTime.setText(timeToDeparture+"");
                            minutes.setText(" min");
                        }
                        end = true;
                    }
                    if (end==true)
                        break;
                }
            }

        root.measure(mWidth, mHeight);
        root.layout(0, 0, mWidth, mHeight);

        Canvas canvas = new Canvas(bitmap);
        widgetLayout.draw(canvas);

        showBitmap(bitmap);
    }

    private void refreshRequest(){
        Intent logIntent = new Intent();
        logIntent.putExtra("ID", "SmartWatchAPP");
        logIntent.putExtra("type", "refreshWidgetRequest");
        logIntent.setAction("com.pomykol.adrian.rozkladjazdy.ResultReceiver.SMARTWATCH");
        mContext.sendBroadcast(logIntent);
    }
}
