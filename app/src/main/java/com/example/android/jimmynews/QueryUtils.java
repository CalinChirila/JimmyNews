package com.example.android.jimmynews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * Created by utilizator12 on 31/08/2017.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    /**
     * The constructor
     */
    private QueryUtils(){}

    /**
     * Create a URL object from a provided url string
     */
    private static URL createUrl(String urlString){

        // If the provided url string is empty or null, exit early
        if(urlString == null || urlString.isEmpty()){
            return null;
        }

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "The provided url is malformed");
        }
        return url;
    }

    /**
     * Make a http request based on the provided URL object
     */
    private static String makeHTTPRequest (URL url){

        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {

            // Establish a connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Check the response code
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, urlConnection.getResponseCode() + ": " + urlConnection.getResponseMessage());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Encountered a problem while establishing the http network connection");
        } finally {

            // If the url connection is active, disconnect from it
            if(urlConnection != null) {
                urlConnection.disconnect();
            }

            // If the input stream is open, close it
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e){
                    Log.e(LOG_TAG, "Couldn't close the input stream");
                }
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{

        StringBuilder stringBuilder = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    private static List<News> extractFieldsFromJson (String jsonResponse){

        // If the provided json response is empty, exit early
        if(TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        // Create a new list of News objects that will be populated later
        List<News> newsList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");

            // Iterate through the results array
            for(int i = 0; i < resultsArray.length(); i++){
                JSONObject resultObject = resultsArray.getJSONObject(i);

                // Extract the TITLE, URL, PUBLICATION DATE and CATEGORY
                String title = "";
                if(resultObject.getString("webTitle") != null){
                    title = resultObject.getString("webTitle");
                }

                String url = "";
                if(resultObject.getString("webUrl") != null){
                    url = resultObject.getString("webUrl");
                }
                String publicationDate = "";
                if(resultObject.getString("webPublicationDate") != null) {
                    publicationDate = resultObject.getString("webPublicationDate");
                }
                String category = "";
                if(resultObject.getString("sectionName") != null){
                    category = resultObject.getString("sectionName");
                }

                // Extract the thumbnail IMAGE
                JSONObject fieldsObject = resultObject.getJSONObject("fields");
                String imageString = fieldsObject.getString("thumbnail");
                URL imageUrl = createUrl(imageString);
                Bitmap image = null;
                try {
                    image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                } catch(IOException e){
                    Log.e(LOG_TAG, "Problem retrieving the article image");
                }

                // Extract the first author's NAME
                JSONArray tagsArray = resultObject.getJSONArray("tags");
                String author = "";
                if (tagsArray.length() != 0){
                    JSONObject authorObject = tagsArray.getJSONObject(0);
                    author = authorObject.getString("webTitle");
                }

                newsList.add(new News(title, author, publicationDate, category, image, url));
            }

        } catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing the json response");
        }
        return newsList;
    }

    /**
     * This method combines all the methods from above
     * @param requestString - takes in a web address
     * @return - the list of news
     */
    public static List<News> extractNews (String requestString){
        URL url = createUrl(requestString);
        String jsonResponse = makeHTTPRequest(url);
        return extractFieldsFromJson(jsonResponse);
    }
}
