package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities.BusStopDetailsActivity;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList.BusStop;


import java.util.List;

/**
 * Created by adrian on 08.11.15.
 * This adapter is used in the activity BusStopActivity
 */
public class BusStopAdapter extends RecyclerView.Adapter<BusStopAdapter.ViewHolder> {
    private List<BusStop> mDataset;
    static int position;
    Bundle bundle = new Bundle();

    //Methods used for component SearchView
    public BusStop removeItem(int position) {
        final BusStop model = mDataset.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, BusStop model) {
        mDataset.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final BusStop model = mDataset.remove(fromPosition);
        mDataset.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<BusStop> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<BusStop> newModels) {
        for (int i = mDataset.size() - 1; i >= 0; i--) {
            final BusStop model = mDataset.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<BusStop> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final BusStop model = newModels.get(i);
            if (!mDataset.contains(model)) {
                addItem(i, model);
            }
        }
    }
    private void applyAndAnimateMovedItems(List<BusStop> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final BusStop model = newModels.get(toPosition);
            final int fromPosition = mDataset.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {        // each data item is just a string in this case
        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewLines;



        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
            this.textViewLines = (TextView) itemView.findViewById(R.id.textViewLines);

            itemView.setOnClickListener((View.OnClickListener) this);
        }

        @Override
        public void onClick(View v) {
            //when click open new activity (BusStopDetailsActivity)
            Intent intent = new Intent(v.getContext(), BusStopDetailsActivity.class);
                bundle.putString("stopId", mDataset.get(getAdapterPosition()).id_przystanku.toString());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
                BusStopAdapter.position = getAdapterPosition();
        }

        public int getPos() {
            return getAdapterPosition();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BusStopAdapter(List<BusStop> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BusStopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_bus_stops, parent, false);
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
        TextView textViewDescription = holder.textViewDescription;
        TextView textViewLine = holder.textViewLines;
        try{
            textViewName.setText(mDataset.get(position).nazwa_przystanku);

        }catch (Exception e){}
        try{
            textViewDescription.setText(mDataset.get(position).opis_przystanku);

        }catch (Exception e){}
        try{
            if (mDataset.get(position).przystanki == null){
                textViewLine.setText("");
            }else {
                textViewLine.setText(mDataset.get(position).przystanki);
            }
        }catch (Exception e){}

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