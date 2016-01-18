package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.adapters.TimetableAdapter;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.RetrofitService;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.gcm.TicketControl;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.Delay;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.SetAlarm;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.SmartWatchWidgetClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable.Time;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable.TimetableBaseClass;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

//class displays the bus timetable
public class TimetableActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Menu mMenu;

    Button showStopsButton;
    TextView lineName;
    TextView begin;
    TextView end;
    TextView actualStop;
    String sLineName;
    String sActualStop;
    String sBegin;
    String sEnd;
    String beginID;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    List<Time> times;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();

        showStopsButton = (Button) findViewById(R.id.showStopsButton);
        showStopsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Repository.getInstance().setActualBusStop(actualStop.toString());
                Intent intent = new Intent(getApplicationContext(), ShowStopsActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.timetable_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        lineName = (TextView) findViewById(R.id.lineNameTextView);
        begin = (TextView) findViewById(R.id.beginTextView);
        end = (TextView) findViewById(R.id.endTextView);
        actualStop = (TextView)findViewById(R.id.actualStopTextView);


        try{
            sLineName = extras.getString("lineName");
            lineName.setText(sLineName);
            sBegin = extras.getString("firstStopName");
            begin.setText(sBegin);
            sEnd = extras.getString("lastStopName");
            end.setText(sEnd);
            sActualStop = Repository.getInstance().getCurrentBusStop();

            Repository.getInstance().setLineName(sLineName);
            Repository.getInstance().setFirstStopName (sBegin);
            Repository.getInstance().setLastStopName (sEnd);

            actualStop.setText(sActualStop);
            beginID = extras.getString("firstStopID");
            Repository.getInstance().setFirstStopID(beginID);
            Bundle bundle = new Bundle();
            bundle.putString("lineName", sLineName);
            bundle.putString("actualStop", sActualStop);
        }catch(Exception e){}
        if (Repository.getInstance().getBusStopsType().equals("lines")){
            showStopsButton.setText("Zmień przystanek");
        }

        loadData();

    }

    private void loadData(){

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Repository.getInstance().getApi())
                .build();
        final RetrofitService service = restAdapter.create(RetrofitService.class);

        showWheel();

        //load data from internet

        service.alinia_rozklad(beginID, sLineName, Repository.getInstance().getSelectedDate().toString(), new Callback<TimetableBaseClass>() {
            @Override
            public void success(TimetableBaseClass timetableBaseClass, Response response) {
                Repository.getInstance().clearTracks();
                Repository.getInstance().clearTimes();
                Repository.getInstance().setTrack(timetableBaseClass.line.track);

                List<Delay> delays = new ArrayList<Delay>();
                for (int i = 0; i < Repository.getInstance().getTracks().size(); i++) {
                    if (sActualStop.equals(Repository.getInstance().getTracks().get(i).nazwa_przystanku)) {
                        Delay delay = new Delay();
                        delay.setDelay(Integer.parseInt(Repository.getInstance().getTracks().get(i).opoznienie));
                        delay.setVariant(Repository.getInstance().getTracks().get(i).wariant_trasy);
                        delays.add(delay);
                    }
                }

                List<Time> filteredTime = new ArrayList<Time>();
                for (int i = 0; i<delays.size(); i++){
                    for(int j=0;j<timetableBaseClass.line.time.size();j++)
                        if (timetableBaseClass.line.time.get(j).wariant_trasy.equals(delays.get(i).getVariant())){
                            filteredTime.add(timetableBaseClass.line.time.get(j));
                        }
                }
                Repository.getInstance().setTime(filteredTime);

                times = Repository.getInstance().getTimes();
                Repository.getInstance().clearDelays();
                Repository.getInstance().setDelay(delays);

                mAdapter = new TimetableAdapter(times);
                mRecyclerView.setAdapter(mAdapter);
                cancelShowWheel();
                MenuItem setWidgetButton = mMenu.findItem(R.id.set_widget);
                MenuItem setAddAlarmButton = mMenu.findItem(R.id.add_alarm);
                setWidgetButton.setEnabled(true);
                setAddAlarmButton.setEnabled(true);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(),R.string.connection_problem, Toast.LENGTH_LONG).show();
                cancelShowWheel();
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        sActualStop = Repository.getInstance().getCurrentBusStop();

        try{
            if (!actualStop.getText().toString().equals(Repository.getInstance().getCurrentBusStop().toString())){
                loadData();
                mAdapter.notifyDataSetChanged();
                actualStop.setText(sActualStop);
            }
        }catch(NullPointerException e){}

    }
    @Override
    public void onBackPressed() { //in alarm mode, back to normal mode
        if(Repository.getInstance().timeTableActivityAlarmMode==true){
            Repository.getInstance().timeTableActivityAlarmMode=false;
            LinearLayout informationPanel = (LinearLayout)  findViewById(R.id.informationPanel);
            LinearLayout recycleViewPanel = (LinearLayout)  findViewById(R.id.recycleViewPanel);
            informationPanel.setVisibility(View.VISIBLE);
            recycleViewPanel.setVisibility(View.VISIBLE);
        }else{ //finish activity
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Repository.getInstance().setSelectedDate(sdf.format(new Date()));
            finish();
        }
    }




    //change date
    public static class DatePickerFragment extends DialogFragment
                            implements DatePickerDialog.OnDateSetListener {
        private WeakReference<TimetableActivity> mActivity;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        mActivity = new WeakReference<TimetableActivity>((TimetableActivity) getActivity());
        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return datePickerDialog;
    }
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //date chosen by the user
        month++;
        String strMonth = month+"";
        String strDay = day+"";
        if (strMonth.length()<2){
            strMonth = "0"+strMonth;
        }
        if(strDay.length()<2){
            strDay = "0"+strDay;
        }
        String date = year+"-"+strMonth+"-"+strDay;
        Repository.getInstance().setSelectedDate(date);
        TimetableActivity target = mActivity.get();
        if (target != null) target.loadData();
        if (target != null) target.showWheel();
    }
        public void onCancel(DialogInterface dialog){
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timetable, menu);

        // Save the menu reference
        mMenu = menu;
        MenuItem setWidgetButton = mMenu.findItem(R.id.set_widget);
        MenuItem setAddAlarmButton = mMenu.findItem(R.id.add_alarm);
        setWidgetButton.setEnabled(false);
        setAddAlarmButton.setEnabled(false);
        if(Repository.getInstance().getSmartWatchWidgetClass() != null){
            if(Repository.getInstance().getSmartWatchWidgetClass().getCurrentBusStop().equals(sActualStop) && Repository.getInstance().getSmartWatchWidgetClass().getLine().equals(sLineName) && Repository.getInstance().getSmartWatchWidgetClass().getFirstBusStop().equals(sBegin) && Repository.getInstance().getSmartWatchWidgetClass().getSelectedDate().equals(sdf.format(new Date()))){
                setWidgetButton.setIcon(R.drawable.ic_watch_white_star);
            }else if (Repository.getInstance().getSmartWatchWidgetClass().getCurrentBusStop().equals(sActualStop) && Repository.getInstance().getSmartWatchWidgetClass().getLine().equals(sLineName) && Repository.getInstance().getSmartWatchWidgetClass().getFirstBusStop().equals(sBegin)){
                setWidgetButton.setIcon(R.drawable.ic_watch_white_sync_problem);
            }else{
                setWidgetButton.setIcon(R.drawable.ic_watch_white_48px);
            }
        }
        if(Repository.getInstance().getAlarmClass() != null){
            setAddAlarmButton.setIcon(R.drawable.ic_notifications_active_white_48dp);
        }else{
            setAddAlarmButton.setIcon(R.drawable.ic_add_alert_white_48dp);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.refresh_api) {
            loadData();
            return true;
        }
        if (id == R.id.set_widget) {
            MenuItem setWidgetButton = mMenu.findItem(R.id.set_widget);
            if(Repository.getInstance().getSmartWatchWidgetClass() == null){
                setWidget();
                if (Repository.getInstance().getSmartWatchWidgetClass() != null){
                    if(Repository.getInstance().getSelectedDate().equals(sdf.format(new Date()))){ //sdf.format(new Date())
                        setWidgetButton.setIcon(R.drawable.ic_watch_white_star);
                    }else{
                        setWidgetButton.setIcon(R.drawable.ic_watch_white_sync_problem);
                    }
                }
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.watch_dialog_message).setPositiveButton(R.string.yes, watchDialogClickListener)
                        .setNegativeButton(R.string.no, watchDialogClickListener).show();
            }


            return true;
        }
        if (id == R.id.add_alarm) {

            if(Repository.getInstance().getAlarmClass()== null){
                LinearLayout informationPanel = (LinearLayout)  findViewById(R.id.informationPanel);
                LinearLayout recycleViewPanel = (LinearLayout)  findViewById(R.id.recycleViewPanel);
                informationPanel.setVisibility(View.GONE);
                recycleViewPanel.setVisibility(View.VISIBLE);
                Repository.getInstance().timeTableActivityAlarmMode = true;

            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage(R.string.alarm_dialog_message).setPositiveButton(R.string.yes, alarmDialogClickListener)
                        .setNegativeButton(R.string.no, alarmDialogClickListener).show();

            }
            return true;
        }
        if (id == R.id.ticket_control) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if(preferences.getBoolean("gcmEnable", false)==true) {
                if (Repository.getInstance().getLastTicketContlorAlert() != null) {
                    Calendar calNow = Calendar.getInstance();
                    Calendar calLastAlert = (Calendar) Repository.getInstance().getLastTicketContlorAlert().clone();
                                calLastAlert.add(Calendar.MINUTE, 5);
                    if (calNow.getTimeInMillis() > calLastAlert.getTimeInMillis()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(R.string.send_message_for_all).setPositiveButton(R.string.yes, messageDialogClickListener)
                                .setNegativeButton(R.string.no, messageDialogClickListener).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.notify_ticket_inspection_error, Toast.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.send_message_for_all).setPositiveButton(R.string.yes, messageDialogClickListener)
                            .setNegativeButton(R.string.no, messageDialogClickListener).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),R.string.register_to_gcm,Toast.LENGTH_LONG).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }
    DialogInterface.OnClickListener watchDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    MenuItem setWidgetButton = mMenu.findItem(R.id.set_widget);
                    if(Repository.getInstance().getSmartWatchWidgetClass().getCurrentBusStop().equals(sActualStop) && Repository.getInstance().getSmartWatchWidgetClass().getLine().equals(sLineName) && Repository.getInstance().getSmartWatchWidgetClass().getFirstBusStop().equals(sBegin) && Repository.getInstance().getSmartWatchWidgetClass().getSelectedDate().equals(sdf.format(new Date()))){
                        Repository.getInstance().clearSmartWatchWidgetClass();
                        saveWidgetToPreferences(null);
                        setWidgetButton.setIcon(R.drawable.ic_watch_white_48px);
                    }else{
                        Repository.getInstance().clearSmartWatchWidgetClass();
                        setWidget();
                        if (Repository.getInstance().getSmartWatchWidgetClass() != null){
                            if(Repository.getInstance().getSelectedDate().equals(sdf.format(new Date()))){
                                setWidgetButton.setIcon(R.drawable.ic_watch_white_star);
                            }else{
                                if (Repository.getInstance().getSmartWatchWidgetClass().getSelectedDate().equals(Repository.getInstance().getSelectedDate())){
                                    Repository.getInstance().clearSmartWatchWidgetClass();
                                    saveWidgetToPreferences(null);
                                    setWidgetButton.setIcon(R.drawable.ic_watch_white_48px);
                                }else{
                                    setWidgetButton.setIcon(R.drawable.ic_watch_white_sync_problem);
                                }
                            }
                        }
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    Log.d("haa", "nope");
                    break;
            }
        }
    };

    DialogInterface.OnClickListener alarmDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    MenuItem setAddAlarmButton = mMenu.findItem(R.id.add_alarm);
//                    cancelAlarm();
                    SetAlarm setAlarm = new SetAlarm();
                    setAlarm.cancelAlarm(getApplicationContext(),true);

                    setAddAlarmButton.setIcon(R.drawable.ic_add_alert_white_48dp);
                    LinearLayout informationPanel = (LinearLayout)  findViewById(R.id.informationPanel);
                    LinearLayout recycleViewPanel = (LinearLayout)  findViewById(R.id.recycleViewPanel);
                    informationPanel.setVisibility(View.GONE);
                    recycleViewPanel.setVisibility(View.VISIBLE);
                    Repository.getInstance().timeTableActivityAlarmMode = true;
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    Log.d("haa", "nope");
                    break;
            }
        }
    };

    DialogInterface.OnClickListener messageDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    TicketControl tc = new TicketControl();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    String info = sLineName+" → "+sEnd+" "+currentDateandTime;
                    tc.postData(info);
                    Log.d("kontrol", info+"");
                    Repository.getInstance().setLastTicketContlorAlert(Calendar.getInstance());
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    Log.d("haa", "nope");
                    break;
            }
        }
    };

    private void saveWidgetToPreferences(SmartWatchWidgetClass smartWatchWidgetClass){
        Gson gson = new Gson();
        String json = gson.toJson(smartWatchWidgetClass);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("SmartWatchWidgetClass", json).commit();

    }


    private void showWheel(){
        final LinearLayout mainRelativeLayout = (LinearLayout)  findViewById(R.id.main_linear_layout);
        final LinearLayout wheelRelativeLayout = (LinearLayout) findViewById(R.id.wheel_linear_layout);
        mainRelativeLayout.setVisibility(View.GONE);
        wheelRelativeLayout.setVisibility(View.VISIBLE);
    }
    private void cancelShowWheel(){
        final LinearLayout mainRelativeLayout = (LinearLayout)  findViewById(R.id.main_linear_layout);
        final LinearLayout wheelRelativeLayout = (LinearLayout) findViewById(R.id.wheel_linear_layout);
        wheelRelativeLayout.setVisibility(View.GONE);
        mainRelativeLayout.setVisibility(View.VISIBLE);
    }
// set widget to smartwatch
    private void setWidget(){
        boolean tryPassed = false;
        SmartWatchWidgetClass smartWatchWidgetClass = new SmartWatchWidgetClass();
        smartWatchWidgetClass.setLine(sLineName);
        smartWatchWidgetClass.setFirstStopId(beginID);
        smartWatchWidgetClass.setCurrentBusStop(sActualStop);
        smartWatchWidgetClass.setFirstBusStop(sBegin);
        smartWatchWidgetClass.setLastBusStop(sEnd);
        smartWatchWidgetClass.setSelectedDate(Repository.getInstance().getSelectedDate());

        List<String> timesList = new ArrayList<>();
        for(int i=0;i<times.size();i++){
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Calendar cal = Calendar.getInstance();
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

            Log.d("test", smartWatchWidgetClass.getTimesList().toString());
        }
    }
    public void setAlarm(){
        LinearLayout informationPanel = (LinearLayout)  findViewById(R.id.informationPanel);
        LinearLayout recycleViewPanel = (LinearLayout)  findViewById(R.id.recycleViewPanel);
        informationPanel.setVisibility(View.VISIBLE);
        recycleViewPanel.setVisibility(View.VISIBLE);
        MenuItem setAddAlarmButton = mMenu.findItem(R.id.add_alarm);
        setAddAlarmButton.setIcon(R.drawable.ic_notifications_active_white_48dp);
        SetAlarm setAlarm = new SetAlarm();
        setAlarm.setAlarm(getApplicationContext());

    }
}
