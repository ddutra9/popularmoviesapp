package com.ddutra9.popularmoviesapp;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Bundle arguments = new Bundle();
        arguments.putParcelable(MainActivityFragment.SELECTED_MOVIE, getIntent().getParcelableExtra(MainActivityFragment.SELECTED_MOVIE));

        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_container, new MovieDetailFragment())
                .commit();
    }
}
