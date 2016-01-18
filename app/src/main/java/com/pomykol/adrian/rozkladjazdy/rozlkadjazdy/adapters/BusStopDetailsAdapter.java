package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities.TimetableActivity;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopDetails.BusStopDetailsLine;

import java.util.List;

/**
 * Created by adrian on 08.11.15.
 * This adapter is used in the activity BusStopDetailsActivity
 */
public class BusStopDetailsAdapter extends RecyclerView.Adapter<BusStopDetailsAdapter.ViewHolder> {
    private List<BusStopDetailsLine> mDataset;
    static int position;
    Bundle bundle = new Bundle();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {        // each data item is just a string in this case
        public TextView textViewName;
        public TextView textViewBegin;
        public TextView textViewEnd;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewBegin = (TextView) itemView.findViewById(R.id.textViewBegin);
            this.textViewEnd = (TextView) itemView.findViewById(R.id.textViewEnd);

            itemView.setOnClickListener((View.OnClickListener) this);
        }

        @Override
        public void onClick(View v) {
            //when click open new activity (TimetableActivity)
            Intent intent = new Intent(v.getContext(), TimetableActivity.class);
            //set data
            bundle.putString("type", "BusStopDetails");
            bundle.putString("lineID", mDataset.get(getAdapterPosition()).id_lini.toString());
            bundle.putString("lineName", mDataset.get(getAdapterPosition()).nazwa_lini.toString());
            Repository.getInstance().setCurrentBusStop(mDataset.get(getAdapterPosition()).nazwa_przystanku.toString());
            bundle.putString("firstStopID", mDataset.get(getAdapterPosition()).pierwszy_przystanek.toString());
            bundle.putString("firstStopName", mDataset.get(getAdapterPosition()).nazwa_pierwszy_przystanek.toString());
            bundle.putString("lastStopName", mDataset.get(getAdapterPosition()).ostatni_przystanek.toString());

            intent.putExtras(bundle);
            v.getContext().startActivity(intent);
            BusStopAdapter.position = getAdapterPosition();
        }

        public int getPos() {
            return getAdapterPosition();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BusStopDetailsAdapter(List<BusStopDetailsLine> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BusStopDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_bus_stop_details, parent, false);
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
        TextView textViewName = holder.textViewName;
        TextView textViewBegin = holder.textViewBegin;
        TextView textViewEnd = holder.textViewEnd;
        try{
            textViewName.setText(mDataset.get(position).nazwa_lini);

        }catch (Exception e){}
        try{
            textViewBegin.setText(mDataset.get(position).nazwa_pierwszy_przystanek);

        }catch (Exception e){}
        try{
                textViewEnd.setText(mDataset.get(position).ostatni_przystanek);

        }catch (Exception e){}
    }
    public int getPos() {
        return position;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        try{
        return mDataset.size();}catch(Exception e){
            return -1;
        }
    }
}