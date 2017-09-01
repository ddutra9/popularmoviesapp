package com.ddutra9.popularmoviesapp.task;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.ddutra9.popularmoviesapp.Processor.MoviesProcessor;
import com.ddutra9.popularmoviesapp.Processor.ReviewsProcessor;
import com.ddutra9.popularmoviesapp.Processor.TrailersProcessor;
import com.ddutra9.popularmoviesapp.R;
import com.ddutra9.popularmoviesapp.interfaces.AsyncTaskDelegate;
import com.ddutra9.popularmoviesapp.model.Movie;
import com.ddutra9.popularmoviesapp.model.Review;

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

public class ReviewsTask extends AsyncTask<String, Void, Review[]> {
    private final String THE_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    private final String REVIEW_URI = "reviews";
    private final String TAG = ReviewsTask.class.getSimpleName();

    private final Context context;
    private AsyncTaskDelegate delegate = null;

    public ReviewsTask(Context context, AsyncTaskDelegate delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected Review[] doInBackground(String... params) {

        if (params.length == 0 || params[0].isEmpty()) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewsJsonString = null;
        final String MOVIE_ID = params[0];

        try {
            final String API_KEY = "api_key";
            final String LANGUAGE = "language";

            Uri builtUri = Uri.parse(THE_MOVIE_URL).buildUpon()
                    .appendPath(MOVIE_ID)
                    .appendPath(REVIEW_URI)
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

            reviewsJsonString = buffer.toString();
            Log.d(TAG, reviewsJsonString);

        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            return ReviewsProcessor.getReviews(context, Long.parseLong(MOVIE_ID), "pt-BR");
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
            ReviewsProcessor.process(reviewsJsonString, Long.parseLong(MOVIE_ID), "pt-BR", context);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return ReviewsProcessor.getReviews(context, Long.parseLong(MOVIE_ID), "pt-BR");
    }


    @Override
    protected void onPostExecute(Review[] reviews) {
        super.onPostExecute(reviews);
        if(delegate != null)
            delegate.processFinish(reviews);
    }
}
