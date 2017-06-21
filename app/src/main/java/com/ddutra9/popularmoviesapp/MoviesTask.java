package com.ddutra9.popularmoviesapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.ddutra9.popularmoviesapp.data.MoviesContract;
import com.ddutra9.popularmoviesapp.model.ParcelableMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by donato on 21/06/17.
 */

public class MoviesTask extends AsyncTask<String, Void, ParcelableMovie[]> {
    private final String  THE_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    private final String TAG = MoviesTask.class.getSimpleName();

    final Context context;

    public MoviesTask(Context context){
        this.context = context;
    }

    @Override
    protected ParcelableMovie[] doInBackground(String... params) {

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

            Uri builtUri = Uri.parse(THE_MOVIE_URL).buildUpon()
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

    private ParcelableMovie[] getMoviesFromJson(String moviesJsonString) throws JSONException {

        final String RESULTS = "results";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String VOTE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String IMAGE = "poster_path";

        JSONObject moviesJson = new JSONObject(moviesJsonString);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());
        ParcelableMovie[] movies = new ParcelableMovie[moviesArray.length()];
        ParcelableMovie movie = null;
        for(int i = 0; i < moviesArray.length(); i++) {

            JSONObject movieJson = moviesArray.getJSONObject(i);

            Log.d(TAG, "moviesJson: " + movieJson.toString());

            movie = new ParcelableMovie();

            movie.setTitle(movieJson.getString(TITLE));
            movie.setOverview(movieJson.getString(OVERVIEW));
            movie.setVoteAverage(movieJson.getDouble(VOTE));
            movie.setReleaseDate(movieJson.getString(RELEASE_DATE));
            movie.setPosterPath(movieJson.getString(IMAGE));

            movies[i] = movie;
            addMovie(movie);
        }

        return movies;
    }

    long addMovie(ParcelableMovie movie) {
        long movieId = 0;
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                new String[]{MoviesContract.MovieEntry._ID},
                MoviesContract.MovieEntry.COLUMN_TITLE + " = ?",
                new String[]{movie.getTitle()},
                null);

        if (cursor.moveToFirst()) {
            int movieIdIndex = cursor.getColumnIndex(MoviesContract.MovieEntry._ID);
            movieId = cursor.getLong(movieIdIndex);
        } else {
            ContentValues values = new ContentValues();

            values.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            values.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

            Uri uri = context.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI,
                    values);

            movieId = ContentUris.parseId(uri);
        }

        cursor.close();

        return  movieId;
    }

    @Override
    protected void onPostExecute(ParcelableMovie[] movies) {
//        if(movies != null){
//            adapter.clear();
//            adapter.addAll(Arrays.asList(movies));
//        }
    }
}
