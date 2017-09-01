package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddutra9.popularmoviesapp.interfaces.AsyncTaskDelegate;
import com.ddutra9.popularmoviesapp.model.Movie;
import com.ddutra9.popularmoviesapp.model.Review;
import com.ddutra9.popularmoviesapp.model.Trailer;
import com.ddutra9.popularmoviesapp.task.TrailersTask;

import java.util.Arrays;

public class MovieDetailFragment extends Fragment implements AsyncTaskDelegate {

    RecyclerView youtubeTrailersRV;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

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

        new TrailersTask(getContext(), this).execute(new String[]{""});
        LinearLayoutManager horizontalLM = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        youtubeTrailersRV = (RecyclerView) view.findViewById(R.id.youtube_trailers_rv);
        youtubeTrailersRV.setLayoutManager(horizontalLM);
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

//            adapter.clear();
//            adapter.addAll(movies);
        }
    }
}
