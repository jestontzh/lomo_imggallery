package com.example.lomoimagegallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PixabayImageRecyclerViewAdapter extends RecyclerView.Adapter<PixabayImageViewHolders> {

    private Context context;
    private List<PixalbayImages> imageList;
    private RecyclerViewItemClickListener itemClickListener;

    public PixabayImageRecyclerViewAdapter(Context context, List<PixalbayImages> imageList, RecyclerViewItemClickListener itemClickListener) {
        this.context = context;
        this.imageList = imageList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public PixabayImageViewHolders onCreateViewHolder(ViewGroup parent, int position) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, null);
        final PixabayImageViewHolders holder = new PixabayImageViewHolders(layoutView);
        layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v, holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(PixabayImageViewHolders holder, int position) {
        Picasso.with(context).load(imageList.get(position).getPreviewURL()).into(holder.getImageView());
    }

    @Override
    public int getItemCount() {
        return this.imageList.size();
    }
}
