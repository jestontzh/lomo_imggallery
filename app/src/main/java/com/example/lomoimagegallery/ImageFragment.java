package com.example.lomoimagegallery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class ImageFragment extends DialogFragment {

    private Context context;

    private String largeImageUrl;
    private int imageHeight;
    private int imageWidth;
    private String userName;

    private View mainView;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView dimensionsText;
    private TextView usernameText;
    private Button downloadButton;

    public static ImageFragment newInstance(String largeImageUrl, int imageHeight, int imageWidth, String userName) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("largeImageUrl", largeImageUrl);
        args.putInt("imageHeight", imageHeight);
        args.putInt("imageWidth", imageWidth);
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
        mainView = inflater.inflate(R.layout.image_fragment, parent, false);
//        mainView.setVisibility(View.GONE);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        dimensionsText = (TextView) view.findViewById(R.id.dimensions_text);
        usernameText = (TextView) view.findViewById(R.id.username_text);
        downloadButton = (Button) view.findViewById(R.id.download_button);
        downloadButton.setVisibility(View.GONE);

        Picasso.with(context).load(this.largeImageUrl).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                showView();
            }

            @Override
            public void onError() {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void showView() {
//        mainView.setVisibility(View.VISIBLE);
        String dim = Integer.toString(this.imageHeight) + " x " + Integer.toString(this.imageWidth);
        dimensionsText.setText(dim);
        String user = "Submitted by: " + this.userName;
        usernameText.setText(user);
        downloadButton.setVisibility(View.VISIBLE);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: SET UP DOWNLOAD HERE
            }
        });
    }
}