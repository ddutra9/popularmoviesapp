package com.ddutra9.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.ddutra9.popularmoviesapp.model.Movie;

import static com.ddutra9.popularmoviesapp.MainActivityFragment.SELECTED_MOVIE;

public class MainActivity extends AppCompatActivity {

    public boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
    }

    public void selectedMovie(Movie movie){
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MainActivityFragment.SELECTED_MOVIE, movie);

            Fragment f = new MovieDetailFragment();
            f.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, f).commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(SELECTED_MOVIE, movie);
            startActivity(intent);
        }
    }
}
