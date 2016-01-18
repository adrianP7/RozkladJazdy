package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.adapters;

/**
 * Created by adrian on 11.11.15.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities.ShowStopsActivity;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities.TimetableActivity;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.AlarmClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by adrian on 08.11.15.
 * This adapter is used in the activity TimeTableActivity
 */
public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {
    private List<Time> mDataset;
    static int position;
    boolean end;
    private int size;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {        // each data item is just a string in this case
        public TextView textViewTime;
        public TextView textViewRemainingTime;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            this.textViewRemainingTime = (TextView)itemView.findViewById(R.id.textViewRemainingTime);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);

            itemView.setOnClickListener((View.OnClickListener) this);

        }

        @Override
        public void onClick(View v) {

            if(Repository.getInstance().timeTableActivityAlarmMode==false) { //if alarm mode is disabled
                Intent intent = new Intent(v.getContext(), ShowStopsActivity.class);
                Repository.getInstance().setCurrentlySelectedTime(mDataset.get(getAdapterPosition()).godzina);
                Repository.getInstance().setVariant(mDataset.get(getAdapterPosition()).wariant_trasy);
                if (textViewTime.getText() != "xx:xx")
                    v.getContext().startActivity(intent);
                TimetableAdapter.position = getAdapterPosition();
            }else{ //if alarm mode is enabled

                String selTime = textViewTime.getText().toString().substring(0,5);
                String selectedTime = Repository.getInstance().getSelectedDate()+" "+selTime;

                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    calSet.setTime(sdf.parse(selectedTime));
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d("Parse err", e.toString());
                }
                String formatted = sdf.format(calSet.getTime());

                if(calSet.getTimeInMillis()>calNow.getTimeInMillis()){
                    AlarmClass alarmClass = new AlarmClass();
                    alarmClass.setAlarm(calSet);
                    alarmClass.setLine(Repository.getInstance().getLineName());
                    alarmClass.setCurrentBusStop(Repository.getInstance().getCurrentBusStop());
                    alarmClass.setLastBusStop(Repository.getInstance().getLastStopName());
                    alarmClass.setFirstBusStop(Repository.getInstance().getFirstStopName());
                    Repository.getInstance().setAlarmClass(alarmClass);
                    saveAlarmToPreferences(alarmClass, v.getContext());

                    if(v.getContext() instanceof TimetableActivity){
                        ((TimetableActivity)v.getContext()).setAlarm();
                        Repository.getInstance().timeTableActivityAlarmMode=false;
                    }
                    Toast.makeText(v.getContext(), R.string.set_alarm, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(v.getContext(), R.string.given_date_has_already_been, Toast.LENGTH_LONG).show();

                }
            }

        }

        public int getPos() {
            return getAdapterPosition();
        }
    }
    private void saveAlarmToPreferences(AlarmClass alarmClass, Context context){
        Gson gson = new Gson();
        String json = gson.toJson(alarmClass);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("AlarmClass", json).commit();
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TimetableAdapter(List<Time> myDataset) {
        mDataset = myDataset;
        size = Repository.getInstance().getDelays().size();


    }

    // Create new views (invoked by the layout manager)
    @Override
    public TimetableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        end = false;
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_timetable, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        position = vh.getPos();


        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //method displays the departures of buses

        TextView textViewTime = holder.textViewTime;
        TextView textViewRemainingTime = holder.textViewRemainingTime;

        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Calendar cal = Calendar.getInstance();
            Calendar previousDeparture = (Calendar) cal.clone();
            Calendar calNow = Calendar.getInstance();
            cal.setTime(dateFormat.parse(mDataset.get(position).godzina));
            String formatted = dateFormat.format(cal.getTime());
            int hour = Integer.parseInt(formatted.substring(0, 2));
            int min = Integer.parseInt(formatted.substring(3, 5));

            cal = (Calendar) calNow.clone();
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
            if(position>0){
                previousDeparture.setTime(dateFormat.parse(mDataset.get(position-1).godzina));
            }

            textViewTime.setText("xx:xx");
            textViewRemainingTime.setText("");
            textViewTime.setTypeface(null, Typeface.NORMAL);

            for (int i = 0; i<size; i++){
                if (mDataset.get(position).wariant_trasy.equals(Repository.getInstance().getDelays().get(i).getVariant())){
                    int delay = Repository.getInstance().getDelays().get(i).getDelay();
                    cal.add(Calendar.MINUTE, delay);
                    formatted = dateFormat.format(cal.getTime());
                    textViewTime.setText(formatted + "" + Repository.getInstance().getDelays().get(i).getVariant());
                }

                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
                String today = sdfDate.format(calNow.getTime());

                if (calNow.getTimeInMillis()<cal.getTimeInMillis() && calNow.getTimeInMillis()>= previousDeparture.getTimeInMillis() && today.equals(Repository.getInstance().getSelectedDate())){
                    long remainingTime = (cal.getTimeInMillis()-calNow.getTimeInMillis())/1000/60;
                    String time;
                    if(remainingTime>60){
                        long hours = remainingTime/60;
                        long minutes = remainingTime - (hours*60);
                        if(minutes<10){
                            time=hours+" h "+"0"+minutes+" min";
                        }else {
                            time=hours+" h "+minutes+" min";
                        }
                    }else{
                        time = remainingTime+" min";
                    }
                    textViewTime.setTypeface(null, Typeface.BOLD);
                    textViewRemainingTime.setText(time);
                }

            }

        }catch (Exception e){}


    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        try{
            return mDataset.size();
        }catch(Exception e){
            return -1;
        }
    }

}
