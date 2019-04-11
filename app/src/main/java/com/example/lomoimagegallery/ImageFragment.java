package com.example.lomoimagegallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import androidx.fragment.app.DialogFragment;

public class ImageFragment extends DialogFragment {

    private final static String TAG = "ImageFragment";

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

    private AsyncTask dTask;

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
                Log.i(TAG, String.format("Downloading URL: %s", largeImageUrl));
                try {
                    URL targetUrl = new URL(largeImageUrl);
                    dTask = new DownloadTask().execute(targetUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class DownloadTask extends AsyncTask<URL, Void, Void> {

        private String urlString;

        @Override
        protected Void doInBackground(URL... urls) {
            URL url = urls[0];
            urlString = url.toString();

            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.connect();
                int fileSize = connection.getContentLength();
                Log.i(TAG, String.format("File size: %d", fileSize));

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), generateFileName(urlString));
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                InputStream inputStream = connection.getInputStream();

                byte[] buffer = new byte[4096];
                int len = 0;

                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                inputStream.close();

                // TODO: Show download progress in Notifications
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }

        protected void onPostExecute(Void v) {
            Toast.makeText(getContext(), "Download Compelete", Toast.LENGTH_SHORT).show();
        }

        protected String generateFileName(String url) {
            String[] stringArr = url.split("/");
            String result = stringArr[stringArr.length-1];
            return result;
        }
    }
}
