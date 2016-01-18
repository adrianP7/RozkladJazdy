package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.RetrofitService;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.AlarmClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.Delay;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.SetAlarm;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.SmartWatchWidgetClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList.BusStop;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList.BusStopsBaseClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.lines.Line;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.lines.LinesBaseClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable.Time;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable.TimetableBaseClass;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

//this is main class
public class MainActivity extends AppCompatActivity {

    Button busStopButton;
    Button busLineButton;
    private NavigationView navigationView;
    ImageButton turnOffNotification;
    int startPanel = 0;

    SharedPreferences preferences;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //set settings
        Repository.getInstance().setApi(preferences.getString("pref_key_api_set", "http://apom.hcore.pl/rozklad"));
        Repository.getInstance().setRingtoneNotification(preferences.getBoolean("pref_key_set_ringtone", true));
        Repository.getInstance().setVibrationNotification(preferences.getBoolean("pref_key_set_vibration", true));
        Repository.getInstance().setLedNotification(preferences.getBoolean("pref_key_set_led", true));
        Repository.getInstance().setAdminNotification(preferences.getBoolean("pref_key_set_notification_from_admin", false));
        Repository.getInstance().setUserNotification(preferences.getBoolean("pref_key_set_notification_from_user", false));
        Repository.getInstance().setRememberBefore(Integer.parseInt(preferences.getString("pref_key_alarm_alert", "10")));

        try {
            LinearLayout mainPanel = (LinearLayout)  findViewById(R.id.main_panel);
            mainPanel.setVisibility(View.GONE);
            loadData();
        }catch(Exception e){
        }

        Gson gson = new Gson();
        String smartWatchWidgetClassInJson = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("SmartWatchWidgetClass", "notFound");
        if(!smartWatchWidgetClassInJson.equals("notFound")){
            SmartWatchWidgetClass obj = gson.fromJson(smartWatchWidgetClassInJson, SmartWatchWidgetClass.class);
            Repository.getInstance().setSmartWatchWidgetClass(obj);
        }

        String alarmClassInJson = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AlarmClass", "notFound");
        if(!alarmClassInJson.equals("notFound")){
            //send notification to SmartWatch
            Log.d("SEND2Watch", "send test notification to SmartWatch");
            Intent logIntent = new Intent();
            logIntent.putExtra("ID", "RozkladJazdyMainAPP");
            logIntent.putExtra("type", "widget");
            String objToStr = new Gson().toJson(Repository.getInstance().getSmartWatchWidgetClass());
            logIntent.putExtra("class", objToStr);
            logIntent.putExtra("from", "timeTable");
            logIntent.setAction("com.pomykol.adrian.rozkladjazdy.smartWatchWidget.DATA");
            sendBroadcast(logIntent);

        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        turnOffNotification = (ImageButton) findViewById(R.id.main_turn_off_notification);
        turnOffNotification.setVisibility(View.GONE);
        turnOffNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage(R.string.alarm_dialog_message).setPositiveButton(R.string.yes, alarmDialogClickListener)
                        .setNegativeButton(R.string.no, alarmDialogClickListener).show();

            }
        });
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent;

                switch (menuItem.getItemId()) {
                    case R.id.busStops:
                        if (startPanel == 2){
                            intent = new Intent(getApplicationContext(), BusStopActivity.class);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    mDrawerLayout.closeDrawers();
                                }
                            }, 200);
                            startActivity(intent);
                        }
                        return true;
                    case R.id.lines:
                        if (startPanel == 2){
                            intent = new Intent(getApplicationContext(), LinesActivity.class);
                            startActivity(intent);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    mDrawerLayout.closeDrawers();
                                }
                            }, 200);
                        }
                        return true;
                    case R.id.settings:
                        intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                mDrawerLayout.closeDrawers();
                            }
                        }, 200);
                        return true;
                    case R.id.info:
                        intent = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(intent);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                mDrawerLayout.closeDrawers();
                            }
                        }, 200);

                        return true;
                    default:
                        return false;
                }
            }
        });

        busStopButton = (Button) findViewById(R.id.menu_bus_stop_button);
        busLineButton = (Button) findViewById(R.id.menu_bus_line_button);
        busStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BusStopActivity.class);
                startActivity(intent);
            }
        });
        busLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LinesActivity.class);
                startActivity(intent);


            }
        });
    }
    //dialogs windows
    DialogInterface.OnClickListener alarmDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    SetAlarm setAlarm = new SetAlarm();
                    setAlarm.cancelAlarm(getApplicationContext(), true);
                    setWidgetText();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    Log.d("haa", "nope");
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.refresh_api){
            loadData();
            loadDataToWidget();
            checkAlarmWidget();
            setWidgetText();
        }
        if (id == R.id.busStops) {
            Intent intent = new Intent(getApplicationContext(), BusStopActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.lines) {
            Intent intent = new Intent(getApplicationContext(), LinesActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.info) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //check date
        if(Repository.getInstance().getSmartWatchWidgetClass() != null ){
            if (Repository.getInstance().getSmartWatchWidgetClass().getSelectedDate().equals(sdf.format(new Date()))){
                Log.d("acht", "data jest poprawna");
            }else {
                loadDataToWidget();
            }
        }
        Log.d("onResume", "ok");
        setWidgetText();
        checkAlarmWidget();
    }
    private void checkAlarmWidget(){
        if(Repository.getInstance().getAlarmClass() != null){
            SetAlarm setAlarm = new SetAlarm();
            Calendar alarm = Repository.getInstance().getAlarmClass().getAlarm();
            Calendar now = Calendar.getInstance();
            if(alarm.getTimeInMillis() - ((Repository.getInstance().getRememberBefore())*60*1000)>now.getTimeInMillis())
                setAlarm.saveAlarmToPreferences(getApplicationContext(), null);
        }
    }
    private void setWidgetText(){
        TextView widgetLineName = (TextView)findViewById(R.id.widgetLineNameTextView);
        TextView widgetCurrentBusStop = (TextView)findViewById(R.id.widgetCurrentBusStopTextView);
        TextView widgetDirection = (TextView)findViewById(R.id.widgetDirectionTextView);
        TextView widgetHours = (TextView)findViewById(R.id.widgetHoursTextView);
        TextView widgetInfo = (TextView)findViewById(R.id.widget_info);
        TextView widgetArrow = (TextView)findViewById(R.id.widgetArrow);
        TextView widgetTimeToDeparture = (TextView)findViewById(R.id.timeToDeparture);
        TextView alarmLineName = (TextView)findViewById(R.id.alarmLineTextView);
        TextView alarmCurrentBusStop = (TextView)findViewById(R.id.alarmCurrentBusStopTextView);
        TextView alarmLastBusStop = (TextView)findViewById(R.id.alarmLastBusStopTextView);
        TextView alarmData = (TextView)findViewById(R.id.alarmDateTextView);
        TextView alarmRememberBefore = (TextView)findViewById(R.id.alarmRememberBefore);
        TextView alarmArrow = (TextView)findViewById(R.id.alarmArrow);

        if(Repository.getInstance().getSmartWatchWidgetClass() != null){
            widgetArrow.setText("→");
            widgetLineName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.text_size_large));
            widgetLineName.setTypeface(null, Typeface.BOLD);

            widgetLineName.setText(Repository.getInstance().getSmartWatchWidgetClass().getLine());
            List<String> timesList = Repository.getInstance().getSmartWatchWidgetClass().getTimesList();
            boolean end =false;
            StringBuffer times = new StringBuffer();
            for(int i = 0; i<timesList.size(); i++){
                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();
                Calendar previousDeparture = (Calendar) calSet.clone();
                Calendar departure = (Calendar) calSet.clone();
                int hour = Integer.parseInt(timesList.get(i).substring(0, 2));
                int min = Integer.parseInt(timesList.get(i).substring(3, 5));
                calSet = (Calendar) calNow.clone();
                calSet.set(Calendar.HOUR_OF_DAY, hour);
                calSet.set(Calendar.MINUTE, min);
                if(i-1>=0){
                    previousDeparture.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timesList.get(i-1).substring(0, 2)));
                    previousDeparture.set(Calendar.MINUTE, Integer.parseInt(timesList.get(i-1).substring(3, 5)));
                }
                if (calNow.getTimeInMillis()<calSet.getTimeInMillis() && calNow.getTimeInMillis()>= previousDeparture.getTimeInMillis() && end == false){
                    times.append("<b>" + "[" + timesList.get(i) + "]" + " " + "</b>"); //select nearest departure in "[]" ex: "[9:00]"
                    long naj = (calSet.getTimeInMillis() - calNow.getTimeInMillis())/60/1000;
                    widgetTimeToDeparture.setText(naj + " min");
                    end = true;
                }else {
                    times.append(timesList.get(i)+" ");
                }
            }
            LinearLayout infoLayout = (LinearLayout)  findViewById(R.id.info_layout);
            infoLayout.setVisibility(View.GONE);
            widgetHours.setText(Html.fromHtml(times.toString()));
            if(Repository.getInstance().getSmartWatchWidgetClass().getSelectedDate().equals(sdf.format(new Date()))){

            }else {
                widgetInfo.setText(R.string.smartwatch_widget_not_valid);
                infoLayout.setVisibility(View.VISIBLE);
            }
            widgetCurrentBusStop.setText(Repository.getInstance().getSmartWatchWidgetClass().getCurrentBusStop());
            widgetDirection.setText(Repository.getInstance().getSmartWatchWidgetClass().getLastBusStop());
        }else{
            widgetArrow.setText("");
            widgetTimeToDeparture.setText("");
            widgetLineName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.text_size_medium));
            widgetLineName.setTypeface(null, Typeface.NORMAL);
            LinearLayout infoLayout = (LinearLayout)  findViewById(R.id.info_layout);
            infoLayout.setVisibility(View.GONE);
            widgetLineName.setText(R.string.smartwatch_widget_not_set);
            widgetHours.setText("");
            widgetCurrentBusStop.setText("");
            widgetDirection.setText("");
        }
        if (Repository.getInstance().getAlarmClass() != null){
            alarmArrow.setText("→");
            alarmLineName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.text_size_large));
            alarmLineName.setTypeface(null, Typeface.BOLD);

            alarmLineName.setText(Repository.getInstance().getAlarmClass().getLine());
            alarmCurrentBusStop.setText(Repository.getInstance().getAlarmClass().getCurrentBusStop());
            alarmLastBusStop.setText(Repository.getInstance().getAlarmClass().getLastBusStop());
            Calendar cal = Repository.getInstance().getAlarmClass().getAlarm();
            cal = (Calendar) cal.clone();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formatted = sdf.format(cal.getTime());
            alarmData.setText(formatted.toString());
            alarmRememberBefore.setText("Przypomnij wcześniej: "+Repository.getInstance().getRememberBefore()+" min");
            turnOffNotification.setVisibility(View.VISIBLE);
        }else{
            alarmArrow.setText("");
            alarmLineName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.text_size_medium));
            alarmLineName.setTypeface(null, Typeface.NORMAL);
            alarmLineName.setText(R.string.alarm_widget_not_set);
            alarmCurrentBusStop.setText("");
            alarmLastBusStop.setText("");
            alarmData.setText("");
            alarmRememberBefore.setText("");
            turnOffNotification.setVisibility(View.GONE);
        }
    }

    public void loadData(){ //loads data from internet (Retrofit)
        startPanel = 0;
        LinearLayout mainPanel = (LinearLayout)  findViewById(R.id.main_panel);
        LinearLayout loading = (LinearLayout)  findViewById(R.id.loading);
        mainPanel.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Repository.getInstance().getApi())
                .build();
        final RetrofitService service = restAdapter.create(RetrofitService.class);


        service.aprzystanki(new Callback<BusStopsBaseClass>() { //download busstops
            @Override
            public void success(BusStopsBaseClass busStopsBaseClass, Response response) {
                Repository.getInstance().clearPrzystanki();
                List<BusStop> busStopData = busStopsBaseClass.busStops.przystanki;
                Collections.sort(busStopData, new Comparator<BusStop>() {
                            @Override
                            public int compare(BusStop lhs, BusStop rhs) {
                                Collator c = Collator.getInstance(new Locale("pl", "PL"));
                                return c.compare(lhs.nazwa_przystanku, rhs.nazwa_przystanku);
                            }
                });
                Repository.getInstance().setPrzystanki(busStopData);
                startPanel++;
                if (startPanel ==2){
                LinearLayout mainPanel = (LinearLayout)  findViewById(R.id.main_panel);
                LinearLayout loading = (LinearLayout)  findViewById(R.id.loading);
                loading.setVisibility(View.GONE);
                mainPanel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), R.string.connection_problem, Toast.LENGTH_LONG).show();
            }
        });

        service.alinie(new Callback<LinesBaseClass>() { //download lines
            @Override
            public void success(LinesBaseClass linesBaseClass, Response response) {
                Repository.getInstance().clearLines();
                List<Line> lineData = linesBaseClass.lines.lines;
                List<Integer> deleteList = new ArrayList<Integer>();
                int repeats;
                for (int i = 0; i < lineData.size(); i++) {
                    Line elem = lineData.get(i);
                    repeats = 0;
                    for (int j = 0; j < lineData.size(); j++) {
                        if (elem.nazwa_lini.equals(lineData.get(j).nazwa_lini) && elem.nazwa_pierwszy_przystanek.equals(lineData.get(j).nazwa_pierwszy_przystanek) && elem.wariant_trasy != "") {
                            repeats = repeats + 1;
                        }
                    }
                    if (repeats > 1) {
                        deleteList.add(i);
                    }
                }
                Collections.reverse(deleteList);

                for (int i = 0; i < deleteList.size(); i++) {
                    int delIndex = deleteList.get(i);
                    lineData.remove(delIndex);
                }
                // sort collection alphabetically
                Collections.sort(lineData, new Comparator<Line>() {
                    @Override
                    public int compare(Line lhs, Line rhs) {
                        Collator c = Collator.getInstance(new Locale("pl", "PL"));
                        return c.compare(lhs.nazwa_lini, rhs.nazwa_lini);
                    }
                });

                Repository.getInstance().setLines(lineData);

                startPanel++;
                if (startPanel == 2){
                    LinearLayout mainPanel = (LinearLayout)  findViewById(R.id.main_panel);
                    LinearLayout loading = (LinearLayout)  findViewById(R.id.loading);
                    loading.setVisibility(View.GONE);
                    mainPanel.setVisibility(View.VISIBLE);

                }
            }


            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), R.string.connection_problem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadDataToWidget(){ //load data to widget
        if(Repository.getInstance().getSmartWatchWidgetClass() != null ) {

            final RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Repository.getInstance().getApi())
                    .build();
            final RetrofitService service = restAdapter.create(RetrofitService.class);


            //download data from internet (to the widget)

            service.alinia_rozklad(Repository.getInstance().getSmartWatchWidgetClass().getFirstStopId(), Repository.getInstance().getSmartWatchWidgetClass().getLine(), Repository.getInstance().getSelectedDate().toString(), new Callback<TimetableBaseClass>() {
                @Override
                public void success(TimetableBaseClass timetableBaseClass, Response response) {
                    Repository.getInstance().clearTracks();
                    Repository.getInstance().clearTimes();
                    Repository.getInstance().setTrack(timetableBaseClass.line.track);

                    List<Delay> delays = new ArrayList<Delay>();
                    for (int i = 0; i < Repository.getInstance().getTracks().size(); i++) {
                        if (Repository.getInstance().getSmartWatchWidgetClass().getCurrentBusStop().equals(Repository.getInstance().getTracks().get(i).nazwa_przystanku)) {
                            Delay delay = new Delay();
                            delay.setDelay(Integer.parseInt(Repository.getInstance().getTracks().get(i).opoznienie));
                            delay.setVariant(Repository.getInstance().getTracks().get(i).wariant_trasy);
                            delays.add(delay);
                        }
                    }

                    List<Time> filteredTime = new ArrayList<Time>();
                    for (int i = 0; i < delays.size(); i++) {
                        for (int j = 0; j < timetableBaseClass.line.time.size(); j++)
                            if (timetableBaseClass.line.time.get(j).wariant_trasy.equals(delays.get(i).getVariant())) {
                                filteredTime.add(timetableBaseClass.line.time.get(j));
                            }
                    }
                    Repository.getInstance().setTime(filteredTime);

                    Repository.getInstance().clearDelays();
                    Repository.getInstance().setDelay(delays);
                    setWidget();
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getApplicationContext(), R.string.connection_problem, Toast.LENGTH_LONG).show();
                }
            });
        }


    }
    private void setWidget(){ //set widget
        boolean tryPassed = false;
        SmartWatchWidgetClass smartWatchWidgetClass = new SmartWatchWidgetClass();
        smartWatchWidgetClass.setLine(Repository.getInstance().getSmartWatchWidgetClass().getLine());
        smartWatchWidgetClass.setFirstStopId(Repository.getInstance().getSmartWatchWidgetClass().getFirstStopId());
        smartWatchWidgetClass.setCurrentBusStop(Repository.getInstance().getSmartWatchWidgetClass().getCurrentBusStop());
        smartWatchWidgetClass.setFirstBusStop(Repository.getInstance().getSmartWatchWidgetClass().getFirstBusStop());
        smartWatchWidgetClass.setLastBusStop(Repository.getInstance().getSmartWatchWidgetClass().getLastBusStop());
        smartWatchWidgetClass.setSelectedDate(Repository.getInstance().getSelectedDate());

        List<String> timesList = new ArrayList<>();
        for(int i=0;i<Repository.getInstance().getTimes().size();i++){
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Calendar cal = Calendar.getInstance();
                List<Time> times = Repository.getInstance().getTimes();
                cal.setTime(dateFormat.parse(times.get(i).godzina));
                for (int j = 0; j<Repository.getInstance().getDelays().size(); j++){
                    if (times.get(i).wariant_trasy.equals(Repository.getInstance().getDelays().get(j).getVariant())){
                        int delay = Repository.getInstance().getDelays().get(j).getDelay();
                        cal.add(Calendar.MINUTE, delay);
                        String formatted = dateFormat.format(cal.getTime());
                        timesList.add(formatted + "" + Repository.getInstance().getDelays().get(j).getVariant());
                    }
                }
                smartWatchWidgetClass.setTimesList(timesList);
                tryPassed = true;
            }catch (Exception e){}
        }
        if(tryPassed==true){
            Repository.getInstance().setSmartWatchWidgetClass(smartWatchWidgetClass);
            saveWidgetToPreferences(smartWatchWidgetClass);

            //send notification to SmartWatch
            Log.d("SEND2Watch", "send test notification to SmartWatch");
            Intent logIntent = new Intent();
            logIntent.putExtra("ID", "RozkladJazdyMainAPP");
            logIntent.putExtra("type", "widget");

            String objToStr = new Gson().toJson(Repository.getInstance().getSmartWatchWidgetClass());

            logIntent.putExtra("class", objToStr);
            logIntent.putExtra("from", "timeTable");
            logIntent.setAction("com.pomykol.adrian.rozkladjazdy.smartWatchWidget.DATA");
            sendBroadcast(logIntent);
            setWidgetText();
        }
    }
    private void saveWidgetToPreferences(SmartWatchWidgetClass smartWatchWidgetClass){
        Gson gson = new Gson();
        String json = gson.toJson(smartWatchWidgetClass);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("SmartWatchWidgetClass", json).commit();

    }

}