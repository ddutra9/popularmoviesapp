package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.ddutra9.popularmoviesapp.interfaces.AsyncTaskDelegate;
import com.ddutra9.popularmoviesapp.model.ParcelableMovie;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by donato on 26/06/17.
 */

public class MoviesTask extends AsyncTask<String, Void, ParcelableMovie[]> {
    private final String THE_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    private final String TAG = MoviesTask.class.getSimpleName();

    private final Context context;
    private AsyncTaskDelegate delegate = null;

    public MoviesTask(Context context, AsyncTaskDelegate delegate) {
        this.context = context;
        this.delegate = delegate;
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
            final String QUERY_PARAM = params[0];
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
            return MoviesProcessor.process(moviesJsonString, context);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(ParcelableMovie[] movies) {
        super.onPostExecute(movies);
        if(delegate != null)
            delegate.processFinish(movies);
    }
}
