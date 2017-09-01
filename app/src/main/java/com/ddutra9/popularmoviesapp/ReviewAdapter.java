package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddutra9.popularmoviesapp.model.Review;
import com.ddutra9.popularmoviesapp.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by donato on 24/08/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> dataList;
    private Context mContext;

    public ReviewAdapter(Context context, List<Review> dataList){
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review, null);
        ReviewAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        final Review review = dataList.get(position);

        holder.authorTV.setText(review.getAuthor());
        holder.reviewsTV.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return !dataList.isEmpty() ? dataList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView reviewsTV, authorTV;

        public ViewHolder(View view) {
            super(view);
            this.reviewsTV = (TextView) view.findViewById(R.id.reviews_tv);
            this.authorTV = (TextView) view.findViewById(R.id.author_tv);
        }
    }
}
