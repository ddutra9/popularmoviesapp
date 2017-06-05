package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.ddutra9.popularmoviesapp.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by donato on 05/06/17.
 */

public class MoviesTask extends AsyncTask<String, Void, Movie[]> {

    private static final String  URL = "https://api.themoviedb.org/3/movie/";
    private static final String TAG = MoviesTask.class.getSimpleName();

    final Context context;

    public MoviesTask(Context context){
        this.context = context;
    }

    @Override
    protected Movie[] doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonString = null;

        try {
            final String QUERY_PARAM = "popular";
            final String API_KEY = "api_key";
            final String LANGUAGE = "language";

            Uri builtUri = Uri.parse(URL).buildUpon()
                    .appendPath(QUERY_PARAM)
                    .appendQueryParameter(API_KEY, context.getString(R.string.API_MOVIE_KEY))
                    .appendQueryParameter(LANGUAGE, "pt-BR")
                    .build();

            Log.d(TAG, "url: " + builtUri.toString());
            URL url = new URL(builtUri.toString());


            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            moviesJsonString = buffer.toString();
            Log.d(TAG, moviesJsonString);

        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Erro ao fechar stream", e);
                }
            }
        }

        try {
            return getMoviesFromJson(moviesJsonString);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private Movie[] getMoviesFromJson(String moviesJsonString) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String VOTE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String IMAGE = "poster_path";

        JSONObject moviesJson = new JSONObject(moviesJsonString);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        Movie[] movies = new Movie[moviesArray.length()];
        for(int i = 0; i < moviesArray.length(); i++) {

            // Get the JSON object representing the movie
            JSONObject movieJson = moviesArray.getJSONObject(i);

            movies[i].setTitle(movieJson.getString(TITLE));
            movies[i].setOverview(movieJson.getString(OVERVIEW));
            movies[i].setVoteAverage(movieJson.getDouble(VOTE));
//            movies[i].setReleaseDate(movieJson.get(RELEASE_DATE));
            movies[i].setPosterPath(movieJson.getString(IMAGE));


        }

        return movies;
    }
}
