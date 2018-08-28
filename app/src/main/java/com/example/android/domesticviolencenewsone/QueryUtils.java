package com.example.android.domesticviolencenewsone;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving domestic violence article data from the Guardian API.
 */


public final class QueryUtils {
    Context myContext;
    public QueryUtils (Context context) {
        myContext = context;
    }

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /*
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
            }

    /**
     * Query the Guardian API dataset and return a list of {@link DVArticles} objects.
     */
    public static List<DVArticles> fetchDVArticlesData(String requestUrl) {
        Log.i(LOG_TAG, "TEST: QueryUtils.fetchDVArticlesData' called");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link DVArticles}
        List<DVArticles> dvArticles = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link DVArticles}s
        return dvArticles;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the DVarticles JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link DVArticles} objects that have been built up from
     * parsing the given JSON response.
     */
    private static List<DVArticles> extractFeatureFromJson(String dvArticlesJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(dvArticlesJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<DVArticles> dvArticleList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(dvArticlesJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of articles (or dv articles).
            JSONArray dvArticlesArray = baseJsonResponse.getJSONObject("response").getJSONArray("results");
            JSONObject dvArticlesResponse = baseJsonResponse.getJSONObject("response");

            String pageSize = dvArticlesResponse.getString("pageSize");
            // For each dv article in the dvArticlesArray, create an {@link DVArticles} object
            for (int i = 0; i < dvArticlesArray.length(); i++) {

                // Get a single article at position i within the list of articles
                JSONObject currentDVArticle = dvArticlesArray.getJSONObject(i);

                // For a given article, extract the JSONObject associated with the
                // JSONarray called "results", which represents a list of all results
                // for that article.

                // Extract the values for the keys
                String section = currentDVArticle.getString("sectionName");
                String sectionId = currentDVArticle.getString("sectionId");
                String url = currentDVArticle.getString("webUrl");
                String date = currentDVArticle.getString("webPublicationDate");

                if (currentDVArticle.has("pageSize")) {
                    pageSize = currentDVArticle.getString("pageSize");
                }

                //Extract the tags array
                JSONArray dvArticlesTagsArray = currentDVArticle.getJSONArray("tags");
                String byline;

                if (dvArticlesTagsArray.length() > 0) {
                    JSONObject tagsObject = dvArticlesTagsArray.getJSONObject(0);
                    //Extract the name of the author of the news report:
                    if (tagsObject != null) {
                        byline = tagsObject.getString("webTitle");
                    } else {
                        byline = Context.getString(R.string.unknown);
                    }
                    //Extract the fields object
                    JSONObject fields = currentDVArticle.getJSONObject("fields");
                    if (fields != null) {
                        // From the fields JSONobject, extract the value for the keys called "headline" and "body"
                        String headline = fields.getString("headline");

                        String body = fields.getString("bodyText");

                        int wordcount = fields.getInt("wordcount");


                        // Create a new {@link DVArticles} object
                        DVArticles dvArticles = new DVArticles(section, sectionId, url, headline, byline, date, body, pageSize, wordcount);

                        // Add the new {@link DVArticles} to the list of dvarticles.
                        dvArticleList.add(dvArticles);
                    }
                }
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        // Return the list of dvarticles
        return dvArticleList;
    }
}
