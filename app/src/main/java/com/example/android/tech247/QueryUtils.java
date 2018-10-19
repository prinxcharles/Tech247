package com.example.android.tech247;
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
 * Helper methods related to requesting and receiving News feed from The Guardian.
 */
public final class QueryUtils {

    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Query the Guardian dataset and return an {@link TechNews} object to represent a single Tech News.
     * @param urls
     */
    public static List<TechNews> fetchTechNewsData(String urls) {
        // Create URL object
        URL url = createUrl(urls);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<TechNews> techNews = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event} object as the result fo the {@link TechNewsAsyncTask}
        return techNews;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     * @param url
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // Check if the url is null, return early
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            //check if the url request was succesful (i.e 200)
            //then input the stream and parse the info into the jsonResponse
            if (urlConnection.getResponseCode() ==200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG, "Error respomse code" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem receiving the earthquake JSON results", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
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
     * Return an {@link TechNews} object by parsing out information
     * about the first news from the input techNewsJSON string.
     */
    private static List<TechNews> extractFeatureFromJson(String techNewsJSON) {

        // Create an empty ArrayList that we can start adding news feeds to
        List<TechNews> techNews = new ArrayList<>();
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(techNewsJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(techNewsJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            //for each tech news in the techNewsArray, create an {@link TechNews} object
            for (int i = 0; i<results.length(); i++) {
                JSONObject currentTechNews = results.getJSONObject(i);
                JSONObject fields = currentTechNews.getJSONObject("fields");
                JSONArray tags = currentTechNews.getJSONArray("tags");
                JSONObject currentTag;
                if (tags.length()==0) {
                    currentTag = null;
                } else {
                    currentTag = tags.getJSONObject(0);
                }

                String author;
                if (currentTag!= null) {
                    //get the strings for the author of the news
                    author = currentTag.getString("webTitle");
                } else {
                    //set the strings for the author to anonymous
                    author = "anonymous";
                }
                //get the strings for the news headline
                String headline = fields.getString("headline");
                //get the strings for the section name
                String sectionName = currentTechNews.getString("sectionName");
                //get the strings for the thumbnail for the associated image
                String thumbnail = fields.getString("thumbnail");
                //get the strings for the publication date
                String webPublicationDate = currentTechNews.getString("webPublicationDate");
                //get the strings for the url link to the news
                String url = currentTechNews.getString("webUrl");

                // Creates new @link TechNews object
                TechNews techNewsData = new TechNews(thumbnail, headline, author, sectionName, webPublicationDate, url);
                // Add the created TechNews object to the to the techNews array
                techNews.add(techNewsData);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the tech news JSON results", e);
        }
        // Return the list of techNews
        return techNews;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

}
