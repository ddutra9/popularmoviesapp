package com.ddutra9.popularmoviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddutra9.popularmoviesapp.data.MoviesContract;
import com.ddutra9.popularmoviesapp.interfaces.AsyncTaskDelegate;
import com.ddutra9.popularmoviesapp.model.Movie;
import com.ddutra9.popularmoviesapp.model.Review;
import com.ddutra9.popularmoviesapp.model.Trailer;
import com.ddutra9.popularmoviesapp.task.ReviewsTask;
import com.ddutra9.popularmoviesapp.task.TrailersTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MovieDetailFragment extends Fragment implements AsyncTaskDelegate, View.OnClickListener{

    private RecyclerView youtubeTrailersRV, reviewsRV;
    private ImageButton markFavoriteIB;
    private Movie movie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            movie = new Movie();
        }
        else {
            movie = savedInstanceState.getParcelable("movie");
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(MainActivityFragment.SELECTED_MOVIE);
        }

        youtubeTrailersRV = (RecyclerView) view.findViewById(R.id.youtube_trailers_rv);
        reviewsRV = (RecyclerView) view.findViewById(R.id.reviews_rv);
        ImageView movieIcon = (ImageView) view.findViewById(R.id.movie_icon);
        TextView voteAverageTV = (TextView) view.findViewById(R.id.vote_average);
        TextView descMovieTV = (TextView) view.findViewById(R.id.text_desc_movie);
        TextView releaseYearTV = (TextView) view.findViewById(R.id.release_year);
        TextView titleMovieTV = (TextView) view.findViewById(R.id.title_movie_tv);

        markFavoriteIB = (ImageButton) view.findViewById(R.id.mark_favorite_ib);
        markFavoriteIB.setImageResource(R.drawable.ic_favorite_border_black_24dp);

        String[] paramsTask = new String[]{String.valueOf(movie.getMovieAPIId()),
                String.valueOf(movie.getId())};
        new TrailersTask(getContext(), this).execute(paramsTask);
        new ReviewsTask(getContext(), this).execute(paramsTask);

        youtubeTrailersRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        reviewsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        Picasso.with(getContext()).load(MovieAdapter.PREFIX_IMAGE_URL + movie.getPosterPath()).into(movieIcon);

        voteAverageTV.setText(movie.getVoteAverage() + "/10");
        descMovieTV.setText(movie.getOverview());
        titleMovieTV.setText(movie.getTitle());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        releaseYearTV.setText(sdf.format(new Date(movie.getReleaseDate())));
        markFavoriteIB.setOnClickListener(this);
        markFavoriteIsCheck();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void processFinish(Object output) {
        if(output != null && output instanceof Trailer[]){
            Trailer[] trailers = (Trailer[]) output;

            TrailerAdapter trailerAdapter = new TrailerAdapter(getActivity(), Arrays.asList(trailers));
            youtubeTrailersRV.setAdapter(trailerAdapter);
        }

        if(output != null && output instanceof Review[]){
            Review[] reviews = (Review[]) output;

            ReviewAdapter reviewAdapter = new ReviewAdapter(getActivity(), Arrays.asList(reviews));
            reviewsRV.setAdapter(reviewAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.mark_favorite_ib){
            movie.setIsFavorite(isChecked(movie.getIsFavorite()) ? 0 : 1);
            markFavoriteIsCheck();

            ContentValues values = new ContentValues();
            values.put(MoviesContract.MovieEntry.COLUMN_IS_FAVORITE, movie.getIsFavorite());

            getActivity().getContentResolver().update(MoviesContract.MovieEntry.CONTENT_URI, values,
                    MoviesContract.MovieEntry._ID + " = ?",
                    new String[]{String.valueOf(movie.getId())});

        }
    }

    private void markFavoriteIsCheck(){
        if(isChecked(movie.getIsFavorite())){
            markFavoriteIB.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            markFavoriteIB.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    private boolean isChecked(Integer i){
        if(i == null || i.equals(0)){
            return false;
        }

        return true;
    }
}
