package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.ddutra9.popularmoviesapp.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by donato on 12/06/17.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String PREFIX_IMAGE_URL = "http://image.tmdb.org/t/p/w342//";

    public static final String TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(@NonNull Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.list_item_icon);
        iconView.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.with(getContext()).load(PREFIX_IMAGE_URL + movie.getPosterPath()).into(iconView);
        Log.d(TAG, "imagePath: " + PREFIX_IMAGE_URL + movie.getPosterPath());

        return convertView;
    }
}
