package com.ddutra9.popularmoviesapp.Processor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ddutra9.popularmoviesapp.data.MoviesContract;
import com.ddutra9.popularmoviesapp.model.Review;
import com.ddutra9.popularmoviesapp.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.ddutra9.popularmoviesapp.MovieAdapter.TAG;

/**
 * Created by donato on 26/06/17.
 */

public class ReviewsProcessor {

    private static final String[] REVIEWS_COLUMNS = {
            MoviesContract.ReviewEntry.COLUMN_MOVIE_ID,
            MoviesContract.ReviewEntry.COLUMN_AUTHOR,
            MoviesContract.ReviewEntry.COLUMN_CONTENT,
            MoviesContract.ReviewEntry.COLUMN_LANGUAGE
    };

    static final int COLUMN_MOVIE_ID = 0;
    static final int COLUMN_AUTHOR = 1;
    static final int COLUMN_CONTENT = 2;
    static final int COLUMN_LANGUAGE = 3;

    public static void process(String input, Long movieId, String language, Context context)throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";

        JSONObject moviesJson = new JSONObject(input);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

        for (int i = 0; i < moviesArray.length(); i++) {

            JSONObject movieJson = moviesArray.getJSONObject(i);

            Log.d(TAG, "moviesJson: " + movieJson.toString());

            ContentValues values = new ContentValues();

            values.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, movieJson.optString(AUTHOR));
            values.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, movieJson.optString(CONTENT));
            values.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
            values.put(MoviesContract.ReviewEntry.COLUMN_LANGUAGE, language);

            Cursor cursor = context.getContentResolver().query(
                    MoviesContract.ReviewEntry.CONTENT_URI,
                    new String[]{MoviesContract.ReviewEntry._ID},
                    MoviesContract.ReviewEntry.COLUMN_AUTHOR + " = ? AND " +
                    MoviesContract.ReviewEntry.COLUMN_LANGUAGE + " = ?",
                    new String[]{movieJson.optString(AUTHOR), language},
                    null);

            if(cursor.moveToNext()){
                int id = cursor.getInt(0);
                context.getContentResolver().update(MoviesContract.ReviewEntry.CONTENT_URI, values, MoviesContract.ReviewEntry._ID + " = ?",
                        new String[]{String.valueOf(id)});
            } else {
                cVVector.add(values);
            }

            cursor.close();
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] contentValues = new ContentValues[cVVector.size()];
            cVVector.toArray(contentValues);
            context.getContentResolver().bulkInsert(MoviesContract.ReviewEntry.CONTENT_URI, contentValues);
        }
    }

    public static Review[] getReviews(Context context, Long movieId, String language){
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.ReviewEntry.CONTENT_URI,
                REVIEWS_COLUMNS,
                MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? AND " +
                MoviesContract.ReviewEntry.COLUMN_LANGUAGE + " = ?",
                new String[]{String.valueOf(movieId), language},
                null);

        List<Review> reviews = new ArrayList<>();
        Review review = null;
        while (cursor.moveToNext()){
            review = new Review();

            review.setMoveId(cursor.getLong(COLUMN_MOVIE_ID));
            review.setAuthor(cursor.getString(COLUMN_AUTHOR));
            review.setContent(cursor.getString(COLUMN_CONTENT));
            review.setLanguage(cursor.getString(COLUMN_LANGUAGE));

            reviews.add(review);
        }

        cursor.close();

        return reviews.toArray(new Review[reviews.size()]);
    }
}
