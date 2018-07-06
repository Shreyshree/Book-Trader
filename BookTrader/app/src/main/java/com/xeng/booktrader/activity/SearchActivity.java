package com.xeng.booktrader.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.xeng.booktrader.R;
import com.xeng.booktrader.adapter.PostingsArrayAdapter;
import com.xeng.booktrader.model.Posting;
import com.xeng.booktrader.model.SearchFilter;
import com.xeng.booktrader.service.ParseAPIService;
import com.xeng.booktrader.service.ParseResponseHandler;
import com.xeng.booktrader.utility.Common;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    ListView lvPostings;
    TextView tvNoResultsFound;
    private ProgressBar spinner;
    LinearLayout linearLayoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        linearLayoutRoot = (LinearLayout) findViewById(R.id.linearLayoutRoot);
        searchView = (SearchView) findViewById(R.id.searchView);
        lvPostings = (ListView) findViewById(R.id.lvPostings);
        searchView.setActivated(true);
        searchView.setQueryHint("Which book are you looking for?");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();

        tvNoResultsFound = (TextView) findViewById(R.id.tvNoResultsFound);
        lvPostings = (ListView) findViewById(R.id.lvPostings);
        spinner= (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                spinner.setVisibility(View.VISIBLE);

                // Call search endpoint
                search();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(onFiltersChanged,
                new IntentFilter(getString(R.string.event_filters_changed)));
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "onFiltersChanged" is broadcasted.
    private BroadcastReceiver onFiltersChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Call search endpoint again
            search();
        }
    };

    // Calls search endpoint
    public void search() {
        ParseAPIService.search(searchView.getQuery().toString(), Common.getSharedInstance().getSearchFilters(), new ParseResponseHandler() {
            @Override
            public void onParseCallSuccess(Object results) {
                ArrayList<Posting> postings = (ArrayList<Posting>) results;
                if (postings.size()>0) {
                    tvNoResultsFound.setVisibility(View.GONE);
                    PostingsArrayAdapter postingsAdapter = new PostingsArrayAdapter(getApplicationContext(),
                            R.layout.postings_item, postings);
                    lvPostings.setAdapter(postingsAdapter);

                    // Define what happens when a posting is clicked
                    lvPostings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(SearchActivity.this, ViewPostActivity.class);
                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("postingId", ((Posting) lvPostings.getItemAtPosition(i)).getObjectId());
                            startActivity( intent );
                        }
                    });
                } else {
                    tvNoResultsFound.setVisibility(View.VISIBLE);
                    PostingsArrayAdapter adapter = (PostingsArrayAdapter) lvPostings.getAdapter();
                    if (adapter != null) {
                        adapter.clear();
                    }
                }

                searchView.clearFocus();
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onParseCallError(ParseException e) {
                Toast.makeText(SearchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                spinner.setVisibility(View.GONE);
            }
        });
    }

    public void filtersButtonClicked(View view) {
        startActivity(new Intent(SearchActivity.this, SearchFiltersActivity.class));
    }

    public void clearFilters(View view) {
        Common.getSharedInstance().clearSearchFilters();
        Toast.makeText(this, "Search filters reset.", Toast.LENGTH_SHORT).show();
        search();
    }
}
