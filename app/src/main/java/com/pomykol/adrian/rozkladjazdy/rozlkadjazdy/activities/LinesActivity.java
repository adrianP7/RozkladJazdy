package com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pomykol.adrian.rozkladjazdy.R;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.adapters.LinesAdapter;
import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.Repository;

import com.pomykol.adrian.rozkladjazdy.rozlkadjazdy.models.lines.Line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//This class is responsible for displaying all the lines in the database
public class LinesActivity extends AppCompatActivity implements Cloneable {

    private RecyclerView mRecyclerView;
    private LinesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lines);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.lines_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        List<Line> filteredLines = Repository.getInstance().getLines();
        List<Integer> deleteList = new ArrayList<>();
        for (int i = 0; i<filteredLines.size();i++){
            if (filteredLines.get(i).ostatni_przystanek== null){
                deleteList.add(i);
            }
        }
        Collections.reverse(deleteList);
        for (int i = 0; i<deleteList.size();i++){
            int deleteIndex = deleteList.get(i);
            filteredLines.remove(deleteIndex);
        }

        List<Line> dataToAdapter = cloneList(Repository.getInstance().getLines());

        mAdapter = new LinesAdapter(dataToAdapter);
        mRecyclerView.setAdapter(mAdapter);

        Repository.getInstance().setBusStopsType("lines");
    }
    public static List<Line> cloneList(List<Line> lineList) {
        List<Line> clonedList = new ArrayList<Line>(lineList.size());
        for (Line line : lineList) {
            clonedList.add(new Line(line));
        }
        return clonedList;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_lines, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) LinesActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(LinesActivity.this.getComponentName()));
        }
        //to search the list
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                final List<Line> filteredModelList = filter(Repository.getInstance().getLines(), query);
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

    private List<Line> filter(List<Line> models, String query) {
        query = query.toLowerCase();

        final List<Line> filteredModelList = new ArrayList<>();
        for (Line model : models) {
            final String text = model.nazwa_lini.toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

}
