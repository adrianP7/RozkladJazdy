package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.adapters.BusStopAdapter;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopsList.BusStop;

import android.support.v7.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

//This class is responsible for displaying information about all stops
public class BusStopActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private BusStopAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.bus_stops_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<BusStop> busStopData = Repository.getInstance().getPrzystanki();
        List<BusStop> dataToAdapter = cloneList(busStopData);
        mAdapter = new BusStopAdapter(dataToAdapter);
        mRecyclerView.setAdapter(mAdapter);



    }

    public static List<BusStop> cloneList(List<BusStop> busStopList) {
        List<BusStop> clonedList = new ArrayList<BusStop>(busStopList.size());
        for (BusStop busStop : busStopList) {
            clonedList.add(new BusStop(busStop));
        }
        return clonedList;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bus_stop, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) BusStopActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(BusStopActivity.this.getComponentName()));
        }
        //to search the list
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                final List<BusStop> filteredModelList = filter(Repository.getInstance().getPrzystanki(), query);
                mAdapter.animateTo(filteredModelList);
                mRecyclerView.scrollToPosition(0);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Repository.getInstance().clearCurrentlySelectedTime();
        finish();
    }

    private List<BusStop> filter(List<BusStop> models, String query) {
        query = query.toLowerCase();

        final List<BusStop> filteredModelList = new ArrayList<>();
        for (BusStop model : models) {
            final String text = model.nazwa_przystanku.toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
