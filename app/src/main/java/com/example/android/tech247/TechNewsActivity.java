package com.example.android.tech247;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TechNewsActivity extends AppCompatActivity  implements LoaderCallbacks<List<TechNews>>  {

/** URL for api feed from the Guardian website */
private static final String GUARDIAN_REQUEST_URL ="https://content.guardianapis.com/search";

    /**
     * Constant value for the tech news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int TECHNEWS_LOADER_ID = 1;

    /** Adapter for the list of tech news */
    private TechNewAdapter mAdapter;

    public static final String LOG_TAG = TechNewsActivity.class.getName();

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView techNewsListView = (ListView) findViewById(R.id.list);

        mAdapter = new TechNewAdapter(this, new ArrayList<TechNews>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        techNewsListView.setAdapter(mAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        techNewsListView.setEmptyView(mEmptyStateTextView);

        techNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current tech news that was clicked on
                TechNews currentTechNews = (TechNews) mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri technewsUri = Uri.parse(currentTechNews.getUrl());

                // Create a new intent to view the tech news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, technewsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(TECHNEWS_LOADER_ID, null, this);

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }


    @Override
    public Loader<List<TechNews>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        String orderDate  = sharedPrefs.getString(
                getString(R.string.settings_order_date_key),
                getString(R.string.settings_order_date_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=json`
        uriBuilder.appendQueryParameter("api-key", "test");
        uriBuilder.appendQueryParameter("q", "science/technology");
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("from-date", "2014-01-01");
        uriBuilder.appendQueryParameter("show-tags", "contributor,author");
        uriBuilder.appendQueryParameter("show-fields", "headline,thumbnail");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("order-date", orderDate);

        // Return the completed uri "https://content.guardianapis.com/search?q=science/technology&format=json&from-date=2014-01-01" +
        //        "&show-tags=contributor,author&show-fields=headline,thumbnail&order-by=relevance&order-date=last-modified&api-key=test"
        return new TechNewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<TechNews>> loader, List<TechNews> techNews) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous techNews data
        if (mAdapter != null) {
            mAdapter.clear();
        }
        // If there is a valid list of {@link TechNews}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (techNews != null && !techNews.isEmpty()) {
            mAdapter.addAll(techNews);
        }

        // Get a reference to the ConnectivityManager to check state of network connectivity
        // This code is present here so that if the home button is pressed. and user return back to the app.
        // A network check would be done
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            mEmptyStateTextView.setText(R.string.no_news_feed);

            //state that there is no internet connection
        } else if (networkInfo == null) {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<TechNews>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
