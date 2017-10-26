package com.example.android.jimmynews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private NewsAdapter mAdapter;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_state_text_view)
    TextView emptyState;
    @BindView(R.id.list)
    ListView newsListView;

    private static final String baseQueryString = "http://content.guardianapis.com/search?";
    private static final String extraFieldsString = "&show-tags=contributor&show-fields=thumbnail&page-size=50&api-key=test";
    private static final String DEFAULT = "N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        ButterKnife.bind(this);

        /**
         * Create a boolean variable that will check the status of the internet connection
         */
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        /**
         * Set the empty view to the news list view
         */
        newsListView.setEmptyView(emptyState);

        // Check the internet connectivity
        // Get an instance of the loader manager
        if (isConnected) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(0, null, this);

            // If there are news in the list, hide the empty view
            emptyState.setVisibility(View.GONE);
        } else {
            // Display a message letting the user know there is no active network connection
            // Hide the progress bar
            emptyState.setText(R.string.no_internet_connection);
            progressBar.setVisibility(View.GONE);
        }

        // Create a new NewsAdapter and set it to the newsListView
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(mAdapter);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle bundle) {

        Uri baseUri = Uri.parse(baseQueryString);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String selectedCategory = sharedPref.getString("category_key", DEFAULT);

        /**
         * When the user runs the app for the first time, he should receive some news articles
         * even if he didn't chose any categories or key words
         */
        String defaultQueryString = "http://content.guardianapis.com/search?q=a&api-key=test&show-tags=contributor&show-fields=thumbnail&order-by=newest";
        if(sharedPref.getString("category_key", "").isEmpty()){
            return new NewsLoader(this, defaultQueryString);
        }

        /**
         * Return a special query if the user entered specific keywords
         */
        String specificKeyWords = sharedPref.getString(getString(R.string.specific_words_key), "");
        String queryWithKeyWords;
        if(!specificKeyWords.isEmpty()){
            specificKeyWords.replace(" ", ",");
            queryWithKeyWords = baseQueryString + "&q=" + specificKeyWords + extraFieldsString;
            return new NewsLoader(this, queryWithKeyWords);
        }

        /**
         * Build a query based on the user's choice of category
         */
        uriBuilder.appendQueryParameter("section", selectedCategory);
        String queryString = uriBuilder.toString() + extraFieldsString;

        if(selectedCategory.equals("all")){
            queryString = baseQueryString + "q=a&order-by=newest" + extraFieldsString;
        }

        return new NewsLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        mAdapter.clear();
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        } else {
            // Display a message in the empty view saying that there were no news found
            emptyState.setText(R.string.no_news_found);
        }
        // Hide the progress bar
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu so that it will appear in the toolbar, once the app is opened
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Create an itemID variable and assign it the selected menu item ID
        int itemID = item.getItemId();

        // Compare the selected item ID with the ID's of different menu items
        // and act accordingly to each one
        switch (itemID) {
            case R.id.settings:
                Intent intent = new Intent(NewsList.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        return true;
    }
}
