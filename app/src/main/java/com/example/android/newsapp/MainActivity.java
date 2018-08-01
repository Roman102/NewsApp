package com.example.android.newsapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NULL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<GuardianNewsItem>> {

    private VerticalGridView guardianNewsView;
    private EditText guardianAPIKeyEditText;

    private Loader<ArrayList<GuardianNewsItem>> guardianNewsLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        guardianNewsView = findViewById(R.id.guardian_news_view);
        guardianAPIKeyEditText = findViewById(R.id.guardian_api_key);

        // A VerticalGridView fills itself with views provided by a layout manager.
        // Each view is represented by a view holder object.
        guardianNewsView.setLayoutManager(new LinearLayoutManager(this));

        // Show the error/usage message at the start of the app.
        fetchGuardianNews(null);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        fetchGuardianNews(null);
    }

    /**
     * Enables (true) or disables (false) the input field for the Guardian API key.
     *
     * @param state_
     */
    private void switchGuardianAPIKeyEditTextOnOrOff(boolean state_) {
        guardianAPIKeyEditText.setEnabled(state_);

        guardianAPIKeyEditText.setInputType(state_ ? TYPE_CLASS_TEXT : TYPE_NULL);
    }

    public void fetchGuardianNews(View v) {
        switchGuardianAPIKeyEditTextOnOrOff(false);

        Bundle guardianNewsLoaderArgs = new Bundle();

        guardianNewsLoaderArgs.putString("guardian_api_entry_point", getResources().getString(R.string.guardian_api_entry_point));
        guardianNewsLoaderArgs.putString("guardian_api_key", guardianAPIKeyEditText.getText().toString().trim());

        if (guardianNewsLoader == null) {
            guardianNewsLoader = getSupportLoaderManager().initLoader(0, guardianNewsLoaderArgs, this);
        } else {
            guardianNewsLoader = getSupportLoaderManager().restartLoader(0, guardianNewsLoaderArgs, this);
        }

        guardianNewsLoader.forceLoad();
    }

    @NonNull
    @Override
    public Loader<ArrayList<GuardianNewsItem>> onCreateLoader(int id, @Nullable Bundle args) {
        return new GuardianNewsLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<GuardianNewsItem>> loader, ArrayList<GuardianNewsItem> data) {
        switchGuardianAPIKeyEditTextOnOrOff(true);

        // The view holder objects are managed by a custom adapter by extending
        // RecyclerView.Adapter. The adapter creates view holders as needed. The adapter also
        // binds the view holders to their data.
        guardianNewsView.setAdapter(new GuardianNewsViewAdapter(data));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<GuardianNewsItem>> loader) {

    }

    private static class GuardianNewsLoader extends AsyncTaskLoader<ArrayList<GuardianNewsItem>> {

        private Bundle mainActivityArgs;

        GuardianNewsLoader(Context context, Bundle args) {
            super(context);

            this.mainActivityArgs = args;
        }

        @Nullable
        @Override
        public ArrayList<GuardianNewsItem> loadInBackground() {
            HttpURLConnection urlConnection = null;
            JsonReader jsonReader = null;

            try {
                URL url = new URL(mainActivityArgs.getString("guardian_api_entry_point") + mainActivityArgs.getString("guardian_api_key"));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    Log.e("MainActivity", "The InputStream doesn't exist!");

                    return null;
                }

                jsonReader = new JsonReader(new InputStreamReader(inputStream));

                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (jsonReader.nextName().equals("response")) {
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            if (jsonReader.nextName().equals("results")) {
                                ArrayList<GuardianNewsItem> results = new ArrayList<>();

                                jsonReader.beginArray();
                                while (jsonReader.hasNext()) {
                                    GuardianNewsItem guardianNewsItem = new GuardianNewsItem();

                                    jsonReader.beginObject();
                                    while (jsonReader.hasNext()) {
                                        switch (jsonReader.nextName()) {
                                            case "webTitle":
                                                guardianNewsItem.title = jsonReader.nextString();
                                                break;
                                            case "webUrl":
                                                guardianNewsItem.url = jsonReader.nextString();
                                                break;
                                            case "sectionName":
                                                guardianNewsItem.sectionName = jsonReader.nextString();
                                                break;
                                            case "webPublicationDate":
                                                guardianNewsItem.articleDate = jsonReader.nextString();
                                                break;
                                            case "tags":
                                                int authorCount = 0;
                                                StringBuilder authors = new StringBuilder();

                                                jsonReader.beginArray();
                                                while (jsonReader.hasNext()) {
                                                    jsonReader.beginObject();
                                                    while (jsonReader.hasNext()) {
                                                        if (jsonReader.nextName().equals("webTitle")) {
                                                            if (authorCount == 0) {
                                                                authors.append(jsonReader.nextString());
                                                            } else {
                                                                authors.append(", ").append(jsonReader.nextString());
                                                            }

                                                            authorCount++;
                                                        } else {
                                                            jsonReader.skipValue();
                                                        }
                                                    }
                                                    jsonReader.endObject();
                                                }
                                                jsonReader.endArray();

                                                guardianNewsItem.authors = authors.toString();
                                                break;
                                            default:
                                                jsonReader.skipValue();
                                        }
                                    }
                                    jsonReader.endObject();

                                    results.add(guardianNewsItem);
                                }
                                jsonReader.endArray(); // END - iterating over "results"-array

                                jsonReader.close();

                                return results;
                            } else { // ELSE - if (jsonReader.nextName().equals("results")) {
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.endObject(); // END - looking for "results" - field within "response"
                    } else { // ELSE - if (jsonReader.nextName().equals("response")) {
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject(); // END - looking for "response" field
            } catch (IOException e) {
                Log.e("MainActivity", "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (jsonReader != null) {
                    try {
                        jsonReader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }

            return null;
        }

    }

}
