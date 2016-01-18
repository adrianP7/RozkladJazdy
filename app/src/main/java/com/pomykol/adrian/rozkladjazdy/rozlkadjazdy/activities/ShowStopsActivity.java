package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.adapters.ShowStopsAdapter;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;

//This class is responsible for displaying the line stops
public class ShowStopsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stops);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.show_stops_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ShowStopsAdapter(Repository.getInstance().getTracks());
        mRecyclerView.setAdapter(mAdapter);


    }
    @Override
    public void onBackPressed() {
        Repository.getInstance().clearCurrentlySelectedTime();
        finish();
    }

    public void end(){
        finish();
    }

}
