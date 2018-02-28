package com.example.devsmar.earthquakereport.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.devsmar.earthquakereport.activity.Earthquake;
import com.example.devsmar.earthquakereport.helper.JsonParserClass;

import java.util.List;


public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {


    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    private String mUrl;


    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public List<Earthquake> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<Earthquake> earthquakesList = JsonParserClass.getResultAsArrayList(mUrl);
        return earthquakesList;
    }
}
