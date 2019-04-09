package com.example.lomoimagegallery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import androidx.fragment.app.Fragment;

public class ImageFragment extends Fragment {

    private Context context;

    private String largeImageUrl;
    private int imageHeight;
    private int imageWidth;
    private String userName;

    private ImageView imageView;
    private TextView dimensionsText;
    private TextView usernameText;

    public static ImageFragment newInstance(String largeImageUrl, int imageHeight, int imageWidth, String userName) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("largeImageUrl", largeImageUrl);
        args.putInt("imageHeight", imageHeight);
        args.putInt("ImageWidth", imageWidth);
        args.putString("userName", userName);
        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = getContext();

        this.largeImageUrl = getArguments().getString("largeImageUrl");
        this.imageHeight = getArguments().getInt("imageHeight");
        this.imageWidth = getArguments().getInt("imageWidth");
        this.userName = getArguments().getString("userName");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imageView = (ImageView) view.findViewById(R.id.image_view);
        dimensionsText = (TextView) view.findViewById(R.id.dimensions_text);
        usernameText = (TextView) view.findViewById(R.id.username_text);

        Picasso.with(context).load(this.largeImageUrl).into(imageView);
        String dim = Integer.toString(this.imageHeight) + " x " + Integer.toString(this.imageWidth);
        dimensionsText.setText(dim);
        usernameText.setText(this.userName);
    }
}
