package com.ddutra9.popularmoviesapp.Processor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ddutra9.popularmoviesapp.data.MoviesContract;
import com.ddutra9.popularmoviesapp.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static com.ddutra9.popularmoviesapp.MovieAdapter.TAG;

/**
 * Created by donato on 26/06/17.
 */

public class MoviesProcessor {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String[] MOVIES_COLUMNS = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH
    };

    static final int COLUMN_ID = 0;
    static final int COLUMN_TITLE = 1;
    static final int COLUMN_OVERVIEW = 2;
    static final int COLUMN_VOTE_AVERAGE = 3;
    static final int COLUMN_RELEASE_DATE = 4;
    static final int COLUMN_POSTER_PATH = 5;

    public static void process(String input, String orderBy, Context context)throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String VOTE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String IMAGE = "poster_path";

        JSONObject moviesJson = new JSONObject(input);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

        for (int i = 0; i < moviesArray.length(); i++) {

            JSONObject movieJson = moviesArray.getJSONObject(i);

            Log.d(TAG, "moviesJson: " + movieJson.toString());

            Movie movie = new Movie();

            movie.setTitle(movieJson.optString(TITLE));
            movie.setOverview(movieJson.optString(OVERVIEW));
            movie.setVoteAverage(movieJson.optDouble(VOTE));
            movie.setReleaseDate(parseDateFromAPI(movieJson.optString(RELEASE_DATE)));
            movie.setPosterPath(movieJson.optString(IMAGE));

            ContentValues values = new ContentValues();

            values.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            values.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            values.put(MoviesContract.MovieEntry.COLUMN_ORDER_BY, orderBy);

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

            cursor.close();
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] contentValues = new ContentValues[cVVector.size()];
            cVVector.toArray(contentValues);
            context.getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);
        }
    }

    public static Movie[] getMovies(Context context, String orderBy){
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIES_COLUMNS,
                MoviesContract.MovieEntry.COLUMN_ORDER_BY + " = ?",
                new String[]{orderBy},
                null);

        List<Movie> movies = new ArrayList<>();
        Movie movie = null;
        while (cursor.moveToNext()){
            movie = new Movie();

            movie.setId(cursor.getLong(COLUMN_ID));
            movie.setTitle(cursor.getString(COLUMN_TITLE));
            movie.setVoteAverage(cursor.getDouble(COLUMN_VOTE_AVERAGE));
            movie.setOverview(cursor.getString(COLUMN_OVERVIEW));
            movie.setReleaseDate(cursor.getLong(COLUMN_RELEASE_DATE));
            movie.setPosterPath(cursor.getString(COLUMN_POSTER_PATH));

            movies.add(movie);
        }

        cursor.close();

        return movies.toArray(new Movie[movies.size()]);
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
