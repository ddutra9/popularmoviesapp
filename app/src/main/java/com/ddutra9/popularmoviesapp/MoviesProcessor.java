package com.ddutra9.popularmoviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ddutra9.popularmoviesapp.data.MoviesContract;
import com.ddutra9.popularmoviesapp.model.ParcelableMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import static com.ddutra9.popularmoviesapp.MovieAdapter.TAG;

/**
 * Created by donato on 26/06/17.
 */

public class MoviesProcessor {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static ParcelableMovie[] process(String input, Context context)throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String VOTE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String IMAGE = "poster_path";

        JSONObject moviesJson = new JSONObject(input);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        ParcelableMovie[] movies = new ParcelableMovie[moviesArray.length()];
        ParcelableMovie movie = null;
        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

        for (int i = 0; i < moviesArray.length(); i++) {

            JSONObject movieJson = moviesArray.getJSONObject(i);

            Log.d(TAG, "moviesJson: " + movieJson.toString());

            movie = new ParcelableMovie();

            movie.setTitle(movieJson.optString(TITLE));
            movie.setOverview(movieJson.optString(OVERVIEW));
            movie.setVoteAverage(movieJson.optDouble(VOTE));
            movie.setReleaseDate(parseDateFromAPI(movieJson.optString(RELEASE_DATE)));
            movie.setPosterPath(movieJson.optString(IMAGE));

            movies[i] = movie;

            ContentValues values = new ContentValues();

            values.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            values.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

            Cursor cursor = context.getContentResolver().query(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    new String[]{MoviesContract.MovieEntry._ID},
                    MoviesContract.MovieEntry.COLUMN_TITLE + " = ?",
                    new String[]{movie.getTitle()},
                    null);

            if(cursor.moveToNext()){
                int id = cursor.getInt(0);
                context.getContentResolver().update(MoviesContract.MovieEntry.CONTENT_URI, values, MoviesContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(id)});
            } else {
                cVVector.add(values);
            }
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] contentValues = new ContentValues[cVVector.size()];
            cVVector.toArray(contentValues);
            context.getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);
        }

        return movies;
    }

    private static Long parseDateFromAPI(String dtStr) {

        try {
            Date dt = SIMPLE_DATE_FORMAT.parse(dtStr);
            return dt.getTime();
        } catch (ParseException e) {
            Log.e(TAG, "erro ao fazer parse data", e);
        }

        return null;
    }

}
