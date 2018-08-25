package com.example.android.domesticviolencenewsone;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<DVArticles>> {

    private static final int DVARTICLES_LOADER_ID = 1;

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";

    private TextView emptyState;

    private ProgressBar indeterminate_bar;

    /**
     * Adapter for the list of earthquakes
     */
    private DVArticlesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyState = (TextView) findViewById(R.id.empty);
        indeterminate_bar = (ProgressBar) findViewById(R.id.progress);

        // Find a reference to the {@link ListView} in the layout
        ListView dvArticlesListView = (ListView) findViewById(R.id.list);


        // Create a new adapter that takes an empty list of articles as input
        mAdapter = new DVArticlesAdapter(this, new ArrayList<DVArticles>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        dvArticlesListView.setAdapter(mAdapter);

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
            Log.i(LOG_TAG, "TEST: initLoader called");

            loaderManager.initLoader(DVARTICLES_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.progress);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            emptyState.setText(R.string.no_internet);
        }

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected article.

        dvArticlesListView.setEmptyView(emptyState);

        dvArticlesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current DVArticle that was clicked on
                DVArticles currentDVArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri dvArticlesUri = Uri.parse(currentDVArticle.getUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, dvArticlesUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }

        });
    }

    @Override
    // onCreateLoader instantiates and returns a new Loader for the given ID
    public android.content.Loader<List<DVArticles>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "TEST: onCreateLoader called");
        // Create a new loader for the given URL
        Log.i(LOG_TAG, "Guardian request: " + GUARDIAN_REQUEST_URL);


        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String pageSize = sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));
        ;

        String sectionId = sharedPrefs.getString(
                getString(R.string.settings_section_key),
                getString(R.string.settings_section_default)
        );

        // Append query parameter and its value. For example, the `format=geojson`

        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("pageSize", String.valueOf(pageSize));
        uriBuilder.appendQueryParameter("sectionId", sectionId);
        uriBuilder.appendQueryParameter("show-fields", "wordcount,headline,bodyText");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("q", "domestic violence");

        uriBuilder.appendQueryParameter("api-key", "3588df55-9efc-4677-96bc-fecca45a6851");


        // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=time
        return new DVArticleLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(android.content.Loader<List<DVArticles>> loader, List<DVArticles> dvArticles) {
        indeterminate_bar.setVisibility(View.GONE);
        emptyState.setText(R.string.no_articles);
        // Clear the adapter of previous DV article data
        Log.i(LOG_TAG, "TEST: onLoadFinished clears mAdapter");
        mAdapter.clear();

        // If there is a valid list of {@link DVarticles}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (dvArticles != null && !dvArticles.isEmpty()) {
            mAdapter.addAll(dvArticles);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<DVArticles>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, "TEST: onLoaderReset");
        mAdapter.clear();
    }

    @Override
    // This method initializes the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
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

