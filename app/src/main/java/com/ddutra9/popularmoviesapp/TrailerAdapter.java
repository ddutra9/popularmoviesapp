package com.ddutra9.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddutra9.popularmoviesapp.model.Trailer;

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

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle;

        protected RecyclerView recycler_view_list;

        protected Button btnMore;



        public ItemRowHolder(View view) {
            super(view);

            this.itemTitle = (TextView) view.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);
            this.btnMore= (Button) view.findViewById(R.id.btnMore);


        }
    }
}
