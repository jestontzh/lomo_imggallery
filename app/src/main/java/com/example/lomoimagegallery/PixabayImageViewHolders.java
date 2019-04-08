package com.example.lomoimagegallery;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

public class PixabayImageViewHolders extends RecyclerView.ViewHolder {

    private ImageView imageView;

    public PixabayImageViewHolders(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
    }

    public ImageView getImageView() {
        return imageView;
    }

    // TODO: Set up onClick here
}
