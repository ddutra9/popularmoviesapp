package com.ddutra9.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

/**
 * Created by donato on 22/08/17.
 */

public class MoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    public static final int MOVIE = 100;
    public static final int REVIEW = 200;
    public static final int REVIEW_WITH_MOVIE = 201;
    public static final int TRAILER = 300;
    public static final int TRAILER_WITH_MOVIE = 301;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;

    static{
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(MoviesContract.MovieEntry.TABLE_NAME);
    }

    private Cursor getMovie(String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private static final SQLiteQueryBuilder sReviewByMovieQueryBuilder;

    private static final String sReviewMovieSelection =
            MoviesContract.MovieEntry.TABLE_NAME+
                    "." + MoviesContract.MovieEntry._ID + " = ? ";

    static{
        sReviewByMovieQueryBuilder = new SQLiteQueryBuilder();
        sReviewByMovieQueryBuilder.setTables(
                MoviesContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.ReviewEntry.TABLE_NAME +
                        " ON " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry._ID +
                        " = " + MoviesContract.ReviewEntry.TABLE_NAME +
                        "." + MoviesContract.ReviewEntry.COLUMN_MOVIE_ID);
    }

    private Cursor getReviewByMovie(Uri uri, String[] projection, String sortOrder) {
        String movieId = MoviesContract.ReviewEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[]{movieId};
        String selection = sReviewMovieSelection;

        return sReviewByMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private static final SQLiteQueryBuilder sTrailerByMovieQueryBuilder;

    private static final String sTrailerMovieSelection =
            MoviesContract.MovieEntry.TABLE_NAME+
                    "." + MoviesContract.MovieEntry._ID + " = ? ";

    static{
        sTrailerByMovieQueryBuilder = new SQLiteQueryBuilder();
        sTrailerByMovieQueryBuilder.setTables(
                MoviesContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.ReviewEntry.TABLE_NAME +
                        " ON " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry._ID +
                        " = " + MoviesContract.TrailerEntry.TABLE_NAME +
                        "." + MoviesContract.TrailerEntry.COLUMN_MOVIE_ID);
    }

    private Cursor getTrailerByMovie(Uri uri, String[] projection, String sortOrder) {
        String movieId = MoviesContract.TrailerEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[]{movieId};
        String selection = sTrailerMovieSelection;

        return sTrailerByMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIE, MOVIE);

        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_REVIEW, REVIEW);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_REVIEW + "/*", REVIEW_WITH_MOVIE);

        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_TRAILER, TRAILER);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_TRAILER + "/*", TRAILER_WITH_MOVIE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                retCursor = getMovie(projection, selection, selectionArgs, sortOrder);
                break;
            }
            case REVIEW_WITH_MOVIE: {
                retCursor = getReviewByMovie(uri, projection, sortOrder);
                break;
            }
            case TRAILER_WITH_MOVIE: {
                retCursor = getTrailerByMovie(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case TRAILER:
                return MoviesContract.TrailerEntry.CONTENT_TYPE;
            case TRAILER_WITH_MOVIE:
                return MoviesContract.TrailerEntry.CONTENT_ITEM_TYPE;
            case REVIEW:
                return MoviesContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW_WITH_MOVIE:
                return MoviesContract.ReviewEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER: {
                long _id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsDeleted = 0;
        switch (match) {
            case MOVIE: {
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TRAILER: {
                rowsDeleted = db.delete(MoviesContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowsDeleted = db.delete(MoviesContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0 || selection == null){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsUpdated = 0;
        switch (match) {
            case MOVIE: {
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowsUpdated = db.update(MoviesContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case TRAILER: {
                rowsUpdated = db.update(MoviesContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsUpdated != 0 || selection == null){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();

        return rowsUpdated;
    }
}
