package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.ddutra9.popularmoviesapp.Processor.MoviesProcessor;
import com.ddutra9.popularmoviesapp.interfaces.AsyncTaskDelegate;
import com.ddutra9.popularmoviesapp.model.Movie;
import com.ddutra9.popularmoviesapp.task.MoviesTask;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements AsyncTaskDelegate {

    private static final String TAG = MainActivityFragment.class.getSimpleName();
    public static final String SELECTED_MOVIE = "SELECTED_MOVIE";

    private ArrayAdapter<Movie> adapter;
    private ArrayList<Movie> movieList;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            movieList = new ArrayList<Movie>();
        }
        else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
        }

        adapter = new MovieAdapter(getActivity(), movieList);

        GridView gridMovies = (GridView)view.findViewById(R.id.grid_view_movies);
        gridMovies.setAdapter(adapter);

        gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.selectedMovie(movieList.get(position));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter.isEmpty()){
            sortByPopularOrTopRated(getString(R.string.pref_order_popular_key));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_favorite:{
                Movie[] movies = MoviesProcessor.getFavorites(getContext());

                adapter.clear();
                adapter.addAll(movies);
                item.setChecked(true);
                return true;
            } case R.id.menu_sort_by_top_rated:{
                sortByPopularOrTopRated(getString(R.string.pref_order_top_rated_key));
                item.setChecked(true);
                return true;
            } case R.id.menu_sort_by_popular:{
                sortByPopularOrTopRated(getString(R.string.pref_order_popular_key));
                item.setChecked(true);
                return true;
            } case R.id.menu_toggle_language:{
                item.setChecked(!item.isChecked());
                if(item.isChecked()){
                    item.setTitle(getString(R.string.en_language));
                } else {
                    item.setTitle(getString(R.string.pt_language));
                }

                return true;
            } default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void sortByPopularOrTopRated(final String order){
        new MoviesTask(getContext(), this).execute(new String[]{order});

        if (!isOnline()) {
            //Se não há conexão disponível, exibe a mensagem
            View view = getActivity().findViewById(R.id.activity_main);
            Snackbar snackbar = Snackbar.make(view, getString(R.string.no_internet_connected), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sortByPopularOrTopRated(order);
                }
            });
            snackbar.show();
        }
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

    @Override
    public void processFinish(Object output) {
        if(output != null){
            Movie[] movies = (Movie[]) output;

            adapter.clear();
            adapter.addAll(movies);
        }else{
            Toast.makeText(getActivity(), R.string.connection_error, Toast.LENGTH_LONG).show();
        }
    }
}
