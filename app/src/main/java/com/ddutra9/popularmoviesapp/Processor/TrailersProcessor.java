package com.ddutra9.popularmoviesapp.Processor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ddutra9.popularmoviesapp.data.MoviesContract;
import com.ddutra9.popularmoviesapp.model.Movie;
import com.ddutra9.popularmoviesapp.model.Trailer;

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

public class TrailersProcessor {

    private static final String[] TRAILERS_COLUMNS = {
            MoviesContract.TrailerEntry.COLUMN_MOVIE_ID,
            MoviesContract.TrailerEntry.COLUMN_SOURCE,
            MoviesContract.TrailerEntry.COLUMN_NAME,
            MoviesContract.TrailerEntry.COLUMN_LANGUAGE
    };

    static final int COLUMN_MOVIE_ID = 0;
    static final int COLUMN_SOURCE = 1;
    static final int COLUMN_NAME = 2;
    static final int COLUMN_LANGUAGE = 3;

    public static void process(String input, Long movieId, String language, Context context)throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "youtube";
        final String NAME = "name";
        final String SOURCE = "source";

        JSONObject moviesJson = new JSONObject(input);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

        for (int i = 0; i < moviesArray.length(); i++) {

            JSONObject movieJson = moviesArray.getJSONObject(i);

            Log.d(TAG, "moviesJson: " + movieJson.toString());

            ContentValues values = new ContentValues();

            values.put(MoviesContract.TrailerEntry.COLUMN_NAME, movieJson.optString(NAME));
            values.put(MoviesContract.TrailerEntry.COLUMN_SOURCE, movieJson.optString(SOURCE));
            values.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
            values.put(MoviesContract.TrailerEntry.COLUMN_LANGUAGE, language);

            Cursor cursor = context.getContentResolver().query(
                    MoviesContract.TrailerEntry.CONTENT_URI,
                    new String[]{MoviesContract.TrailerEntry.TABLE_NAME + "." + MoviesContract.TrailerEntry._ID},
                    MoviesContract.TrailerEntry.COLUMN_NAME + " = ? AND " +
                    MoviesContract.TrailerEntry.COLUMN_LANGUAGE + " = ?",
                    new String[]{movieJson.optString(NAME), language},
                    null);

            if(cursor.moveToNext()){
                int id = cursor.getInt(0);
                context.getContentResolver().update(MoviesContract.TrailerEntry.CONTENT_URI, values, MoviesContract.TrailerEntry._ID + " = ?",
                        new String[]{String.valueOf(id)});
            } else {
                cVVector.add(values);
            }

            cursor.close();
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] contentValues = new ContentValues[cVVector.size()];
            cVVector.toArray(contentValues);
            context.getContentResolver().bulkInsert(MoviesContract.TrailerEntry.CONTENT_URI, contentValues);
        }
    }

    public static Trailer[] getTrailers(Context context, Long movieId){
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.TrailerEntry.buildTrailerUri(movieId),
                TRAILERS_COLUMNS,
                null,
                new String[]{String.valueOf(movieId)},
                null);

        List<Trailer> trailers = new ArrayList<>();
        Trailer trailer = null;
        while (cursor.moveToNext()){
            trailer = new Trailer();

            trailer.setMoveId(cursor.getLong(COLUMN_MOVIE_ID));
            trailer.setSource(cursor.getString(COLUMN_SOURCE));
            trailer.setName(cursor.getString(COLUMN_NAME));
            trailer.setLanguage(cursor.getString(COLUMN_LANGUAGE));

            trailers.add(trailer);
        }

        cursor.close();

        return trailers.toArray(new Trailer[trailers.size()]);
    }
}
