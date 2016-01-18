package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.adapters.BusStopDetailsAdapter;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.RetrofitService;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopDetails.BusStopDetailsBaseClass;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.busStopDetails.BusStopDetailsLine;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
//This class is responsible for displaying the available lines per stop
public class BusStopDetailsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int stopId =  -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.bus_stop_details_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle extras = getIntent().getExtras();

        try{
            stopId = Integer.parseInt(extras.getString("stopId"));

        }catch(Exception e){
            stopId = -1;
        }
        loadData(stopId);
        Repository.getInstance().setBusStopsType("stops");
    }
    private void loadData(int stopId) {

        final LinearLayout mainRelativeLayout = (LinearLayout)  findViewById(R.id.main_linear_layout);
        final LinearLayout wheelRelativeLayout = (LinearLayout) findViewById(R.id.wheel_linear_layout);
        mainRelativeLayout.setVisibility(View.GONE);
        wheelRelativeLayout.setVisibility(View.VISIBLE);

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Repository.getInstance().getApi())
                .build();
        final RetrofitService service = restAdapter.create(RetrofitService.class);

        service.aprzystanek(stopId, new Callback<BusStopDetailsBaseClass>() {
            @Override
            public void success(BusStopDetailsBaseClass busStopDetailsBaseClass, Response response) {
                Repository.getInstance().clearBusStopDetailsLines();
                List<BusStopDetailsLine> linesOnBusStop = busStopDetailsBaseClass.busStopDetails.busStops;
                List<Integer> deleteList = new ArrayList<Integer>();
                int repeats;
                for (int i = 0; i < linesOnBusStop.size(); i++) {
                    BusStopDetailsLine elem = linesOnBusStop.get(i);
                    repeats = 0;
                    for (int j = 0; j < linesOnBusStop.size(); j++) {
                        if (elem.nazwa_lini.equals(linesOnBusStop.get(j).nazwa_lini) && elem.nazwa_pierwszy_przystanek.equals(linesOnBusStop.get(j).nazwa_pierwszy_przystanek) && elem.wariant_trasy != "") {
                            repeats = repeats + 1;
                        }
                    }
                    if (repeats > 1) {
                        deleteList.add(i);
                    }
                }
                //if the line is a variant
                int deleteListSize = deleteList.size();
                while (deleteListSize>0) {
                    BusStopDetailsLine elem = linesOnBusStop.get(deleteList.get(deleteListSize-1));
                    for (int i = 0; i < linesOnBusStop.size(); i++) {
                        if (elem.nazwa_lini.equals(linesOnBusStop.get(i).nazwa_lini) && elem.nazwa_pierwszy_przystanek.equals(linesOnBusStop.get(i).nazwa_pierwszy_przystanek) && linesOnBusStop.get(i).wariant_trasy == "") {
                            int removeIndex = deleteList.get(deleteListSize - 1);
                            linesOnBusStop.remove(removeIndex);
                        }
                    }
                    deleteListSize--;
                }
                //sorting
                Collections.sort(linesOnBusStop, new Comparator<BusStopDetailsLine>() {
                    @Override
                    public int compare(BusStopDetailsLine lhs, BusStopDetailsLine rhs) {
                        Collator c = Collator.getInstance(new Locale("pl", "PL"));
                        return c.compare(lhs.nazwa_lini, rhs.nazwa_lini);
                    }
                });

                Repository.getInstance().setBusStopDetailsLines(linesOnBusStop);
                // specify an adapter (see also next example)
                mAdapter = new BusStopDetailsAdapter(Repository.getInstance().getBusStopDetailsLines());
                mRecyclerView.setAdapter(mAdapter);
                wheelRelativeLayout.setVisibility(View.GONE);
                mainRelativeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), R.string.connection_problem, Toast.LENGTH_LONG).show();
                wheelRelativeLayout.setVisibility(View.GONE);
                mainRelativeLayout.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bus_stop_details, menu);
        MenuItem searchItem = menu.findItem(R.id.refresh_api);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.refresh_api) {
            loadData(stopId);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


