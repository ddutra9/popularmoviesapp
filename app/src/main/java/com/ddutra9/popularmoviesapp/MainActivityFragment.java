package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.ddutra9.popularmoviesapp.interfaces.AsyncTaskDelegate;
import com.ddutra9.popularmoviesapp.model.ParcelableMovie;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements AsyncTaskDelegate {

    private static String TAG = MainActivityFragment.class.getSimpleName();

    private ArrayAdapter<ParcelableMovie> adapter;
    private ArrayList<ParcelableMovie> movieList;

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
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void updateMovies(){
        Log.d(TAG, "isOnline: " + isOnline());
        if(isOnline()){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String order = prefs.getString(getString(R.string.pref_order_key),
                    getString(R.string.pref_order_popular));

            new MoviesTask(getContext(), this).execute(new String[]{order});
        } else {
            //Se não há conexão disponível, exibe a mensagem
            View view = getActivity().findViewById(R.id.activity_main);
            Snackbar snackbar = Snackbar.make(view, getString(R.string.no_internet_connected), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMovies();
                }
            });
            snackbar.show();
        }
    }

    @Override
    public void processFinish(Object output) {
        if(output != null){
            ParcelableMovie[] movies = (ParcelableMovie[]) output;

            adapter.clear();
            adapter.addAll(movies);
        }else{
            Toast.makeText(getActivity(), R.string.connection_error, Toast.LENGTH_LONG).show();
        }
    }
}
