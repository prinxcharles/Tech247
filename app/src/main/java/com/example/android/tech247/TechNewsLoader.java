package com.example.android.tech247;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.AsyncTaskLoader;

import java.util.List;

public class TechNewsLoader extends AsyncTaskLoader {

    /** Tag for log messages */
    private static final String LOG_TAG = TechNewsLoader.class.getName();
    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link TechNewsLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public TechNewsLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Object loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the HTTP request for news data and process the response.
        List<TechNews> techNews = QueryUtils.fetchTechNewsData(mUrl);
        return techNews;
    }
}
