package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities.TimetableActivity;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.lines.Line;

import java.util.List;

/**
 * Created by adrian on 08.11.15.
 * This adapter is used in the activity LinesActivity
 */
public class LinesAdapter extends RecyclerView.Adapter<LinesAdapter.ViewHolder> {
    private List<Line> mDataset;
    static int position;
    Bundle bundle = new Bundle();

    //Methods used for component SearchView
    public Line removeItem(int position) {
        final Line model = mDataset.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Line model) {
        mDataset.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Line model = mDataset.remove(fromPosition);
        mDataset.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<Line> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Line> newModels) {
        for (int i = mDataset.size() - 1; i >= 0; i--) {
            final Line model = mDataset.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Line> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Line model = newModels.get(i);
            if (!mDataset.contains(model)) {
                addItem(i, model);
            }
        }
    }
    private void applyAndAnimateMovedItems(List<Line> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Line model = newModels.get(toPosition);
            final int fromPosition = mDataset.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

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
            bundle.putString("type", "Lines");
            bundle.putString("lineID", mDataset.get(getAdapterPosition()).id_lini.toString());
            bundle.putString("lineName", mDataset.get(getAdapterPosition()).nazwa_lini.toString());
            Repository.getInstance().setCurrentBusStop(mDataset.get(getAdapterPosition()).nazwa_pierwszy_przystanek.toString());
            bundle.putString("firstStopID", mDataset.get(getAdapterPosition()).pierwszy_przystanek.toString());
            bundle.putString("firstStopName", mDataset.get(getAdapterPosition()).nazwa_pierwszy_przystanek.toString());
            if(mDataset.get(getAdapterPosition()).ostatni_przystanek != null){
                bundle.putString("lastStopName", mDataset.get(getAdapterPosition()).ostatni_przystanek.toString());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
                LinesAdapter.position = getAdapterPosition();
            }else{
                Toast.makeText(v.getContext(), R.string.last_busstop_not_set, Toast.LENGTH_SHORT).show();
            }
        }
        public int getPos() {
            return getAdapterPosition();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public LinesAdapter(List<Line> myDataset) {

        mDataset = myDataset;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public LinesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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