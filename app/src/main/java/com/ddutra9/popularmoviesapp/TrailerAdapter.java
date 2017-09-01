package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddutra9.popularmoviesapp.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by donato on 24/08/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<Trailer> dataList;
    private Context mContext;

    public TrailerAdapter(Context context, List<Trailer> dataList){
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie, null);
        TrailerAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder holder, int position) {
        Trailer trailer = dataList.get(position);

        Picasso.with(mContext).load(PREFIX_IMAGE_URL + movie.getPosterPath()).into(holder.youtubeImage);
        holder.youtubeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return !dataList.isEmpty() ? dataList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView youtubeImage;

        public ViewHolder(View view) {
            super(view);
            this.youtubeImage = (ImageView) view.findViewById(R.id.trailer_image_view);
        }
    }
}
