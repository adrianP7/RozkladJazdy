package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.adapters;

/**
 * Created by adrian on 11.11.15.
 */

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities.ShowStopsActivity;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.timetable.Track;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by adrian on 08.11.15.
 * This adapter is used in the activity ShowStopsActivity
 */
public class ShowStopsAdapter extends RecyclerView.Adapter<ShowStopsAdapter.ViewHolder> {
    private List<Track> mDataset;
    static int position;
    String actualBusStop;
    private WeakReference<ShowStopsActivity> mActivity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {        // each data item is just a string in this case
        //public TextView mTextView;
        public TextView textViewBusStop;
        public TextView textViewDelay;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewBusStop = (TextView) itemView.findViewById(R.id.textViewBusStop);
            this.textViewDelay = (TextView) itemView.findViewById(R.id.textViewDelay);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
            actualBusStop = Repository.getInstance().getActualBusStop();
            itemView.setOnClickListener((View.OnClickListener) this);

        }

        @Override
        public void onClick(View v) {
            if (Repository.getInstance().getBusStopsType().equals("lines") && mDataset.get(getAdapterPosition()).id_lini != null && Repository.getInstance().getCurrentlySelectedTime().length() < 1){
                Repository.getInstance().setCurrentBusStop(mDataset.get(getAdapterPosition()).nazwa_przystanku);
                mActivity = new WeakReference<ShowStopsActivity>((ShowStopsActivity) itemView.getContext());
                ShowStopsActivity target = mActivity.get();
                if (target != null) target.end();
            }
        }

        public int getPos() {
            return getAdapterPosition();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ShowStopsAdapter(List<Track> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ShowStopsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_show_stops, parent, false);
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
        //This method display bus delays

        TextView textViewBusStop = holder.textViewBusStop;
        TextView textViewDelay = holder.textViewDelay;
        CardView cardView = holder.cardView;
        textViewDelay.setText(R.string.stop_for_another_variant);

        if (mDataset.get(position).nazwa_lini.equals("This_is_label_0")){
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            textViewBusStop.setTextAppearance(cardView.getContext(), android.R.style.TextAppearance_Medium);
            textViewDelay.setTextAppearance(cardView.getContext(), android.R.style.TextAppearance_Medium);
            textViewDelay.setText("Opóźnienie");
        }else if (mDataset.get(position).nazwa_lini.equals("This_is_label_1")) {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            textViewBusStop.setTextAppearance(cardView.getContext(), android.R.style.TextAppearance_Medium);
            textViewDelay.setTextAppearance(cardView.getContext(), android.R.style.TextAppearance_Medium);
            textViewDelay.setText("");
        }else{
            int color = cardView.getContext().getResources().getColor(R.color.cardsColour);
            holder.cardView.setCardBackgroundColor(color);
            textViewBusStop.setTextAppearance(cardView.getContext(), android.R.style.TextAppearance_Small);
            textViewDelay.setTextAppearance(cardView.getContext(), android.R.style.TextAppearance_Small);
        }

        try{
            textViewBusStop.setText(mDataset.get(position).nazwa_przystanku);

        }catch (Exception e){}

        try {
            textViewBusStop.setTypeface(null, Typeface.NORMAL);
            textViewDelay.setTypeface(null, Typeface.NORMAL);

            if (Repository.getInstance().getCurrentlySelectedTime().length() < 1) { //when button click
                for (int i = 0; i < Repository.getInstance().getDelays().size(); i++) {
                    if (Repository.getInstance().getDelays().get(i).getVariant().equals(mDataset.get(position).wariant_trasy)) {
                        int stdDelay = Integer.parseInt(mDataset.get(position).opoznienie);
                        stdDelay -= Repository.getInstance().getDelays().get(i).getDelay();
                        if (stdDelay == 0) { //if it is selected busstop
                            textViewBusStop.setTextAppearance(cardView.getContext(), android.R.style.TextAppearance_Medium);
                            textViewDelay.setTextAppearance(cardView.getContext(), android.R.style.TextAppearance_Medium);
                            textViewBusStop.setTypeface(null, Typeface.BOLD);
                            textViewDelay.setTypeface(null, Typeface.BOLD);
                        }
                        textViewDelay.setText(stdDelay + "");
                    }
                }
            }else { //when time click
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(dateFormat.parse(Repository.getInstance().getCurrentlySelectedTime()));
                } catch (ParseException e) {
                }
                for (int i = 0; i < Repository.getInstance().getDelays().size(); i++) {
                    if (Repository.getInstance().getDelays().get(i).getVariant().equals(mDataset.get(position).wariant_trasy)) {
                        if(mDataset.get(position).wariant_trasy.equals(Repository.getInstance().getVariant())) {
                            int stdDelay = Integer.parseInt(mDataset.get(position).opoznienie);
                            cal.add(Calendar.MINUTE, stdDelay);
                            String formatted = dateFormat.format(cal.getTime());
                            textViewDelay.setText(formatted + "");
                            if (stdDelay - Repository.getInstance().getDelays().get(i).getDelay() == 0) {
                                textViewBusStop.setTextAppearance(cardView.getContext(), android.R.style.TextAppearance_Medium);
                                textViewDelay.setTextAppearance(cardView.getContext(), android.R.style.TextAppearance_Medium);
                                textViewBusStop.setTypeface(null, Typeface.BOLD);
                                textViewDelay.setTypeface(null, Typeface.BOLD);
                            }
                        }
                    }


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
