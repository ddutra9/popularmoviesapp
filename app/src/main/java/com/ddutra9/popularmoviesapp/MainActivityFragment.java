package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.ddutra9.popularmoviesapp.data.MoviesContract;
import com.ddutra9.popularmoviesapp.model.ParcelableMovie;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private ArrayAdapter<ParcelableMovie> adapter;
    private ArrayList<ParcelableMovie> movieList;

    private static final String[] MOVIE_COLUMNS = {
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

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            movieList = new ArrayList<ParcelableMovie>();
        }
        else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
        }

        adapter = new MovieAdapter(getActivity(), movieList);

        GridView gridMovies = (GridView)view.findViewById(R.id.grid_view_movies);
        gridMovies.setAdapter(adapter);

        gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Toast.makeText(getContext(), adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieList);
        super.onSaveInstanceState(outState);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void updateMovies(){
        if(isOnline()){
            new MoviesTask(getContext()).execute(new String[]{});
        } else {
            Toast.makeText(getActivity(), "Favor verificar sua coneccao com a internet!", Toast.LENGTH_LONG);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " ASC";
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(getActivity(), uri, MOVIE_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        adapter.swa(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
