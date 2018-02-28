package com.example.devsmar.earthquakereport.activity;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devsmar.earthquakereport.R;
import com.example.devsmar.earthquakereport.adapters.RecyclerviewAdapter;
import com.example.devsmar.earthquakereport.helper.JsonParserClass;
import com.example.devsmar.earthquakereport.loaders.EarthquakeLoader;



import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements RecyclerviewAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<List<Earthquake>> {

    RecyclerView recyclerView;
    RecyclerviewAdapter recyclerviewAdapter;
    public ArrayList<Earthquake> earthquakeList;

    private static final int EARTHQUAKE_LOADER_ID = 1;

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";


    private TextView mEmptyStateText;

    View loadingIndicator, mEmptyStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initialize();

        recyclerView.setAdapter(recyclerviewAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        dummyOnCreate();
    }


    private void initialize() {
        earthquakeList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        recyclerviewAdapter = new RecyclerviewAdapter(earthquakeList, this);

        mEmptyStateText =  findViewById(R.id.empty_view);
        mEmptyStateView =  findViewById(R.id.empty_view_drawable);
        mEmptyStateView.setVisibility(View.INVISIBLE);
    }

    public void dummyOnCreate(){
        mEmptyStateView.setVisibility(View.INVISIBLE);
        mEmptyStateText.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);

            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
            } else {
                loadingIndicator.setVisibility(View.INVISIBLE);
                mEmptyStateView.setVisibility(View.VISIBLE);
                mEmptyStateText.setVisibility(View.VISIBLE);
                mEmptyStateText.setText(R.string.no_internet_connection);
            }


    }

    @Override
    public void onCustomClickListener(int position) {
        Earthquake currentEarthquake = recyclerviewAdapter.getItem(position);

        Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

        startActivity(websiteIntent);
    }

    @Override
    public void onCustomLongClickListener(int position) {

    }



    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        String maxMagnitude = sharedPrefs.getString(
                getString(R.string.settings_max_magnitude_key),
                getString(R.string.settings_max_magnitude_default));

        String limit = sharedPrefs.getString(
                getString(R.string.settings_limit_key),
                getString(R.string.settings_limit_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", limit);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("maxmag", maxMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);


        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {


        if (data != null && !data.isEmpty()) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            mEmptyStateText.setVisibility(View.INVISIBLE);
            mEmptyStateView.setVisibility(View.INVISIBLE);
            earthquakeList.addAll(data);
            recyclerviewAdapter.notifyDataSetChanged();
        }
        else  {
            recyclerView.setVisibility(View.INVISIBLE);
            loadingIndicator.setVisibility(View.INVISIBLE);
            mEmptyStateText.setVisibility(View.VISIBLE);
            mEmptyStateView.setBackgroundResource(R.drawable.ic_stat_announcement);
            mEmptyStateView.setVisibility(View.VISIBLE);
            mEmptyStateText.setText(getString(R.string.empty_data));
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        earthquakeList.clear();
        recyclerviewAdapter.notifyItemRangeRemoved(0, earthquakeList.size());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
            Intent settingsIntent = new Intent(this, SettingActivity.class);
            startActivity(settingsIntent);
            return true;

            case R.id.action_aboutDev:
                String url = "https://github.com/Smarpit-Singh";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        getLoaderManager().destroyLoader(EARTHQUAKE_LOADER_ID);
       dummyOnCreate();
    }

}

