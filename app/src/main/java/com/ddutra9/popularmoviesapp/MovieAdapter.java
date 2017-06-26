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

import com.ddutra9.popularmoviesapp.model.ParcelableMovie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by donato on 12/06/17.
 */

public class MovieAdapter extends ArrayAdapter<ParcelableMovie> {

    private static final String PREFIX_IMAGE_URL = "http://image.tmdb.org/t/p/w342//";

    public static final String TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(@NonNull Context context, List<ParcelableMovie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ParcelableMovie movie = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(getContext()).load(PREFIX_IMAGE_URL + movie.getPosterPath()).into(viewHolder.iconView);
        Log.d(TAG, "imagePath: " + PREFIX_IMAGE_URL + movie.getPosterPath());

        return convertView;
    }


    public static class ViewHolder {

        public final ImageView iconView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.movie_image);
        }
    }
}
