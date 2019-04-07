package com.example.lomoimagegallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.POST;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ACCESS_INTERNET_PERMISSIONS = 1;
    private static final String TAG = "MainActivity";
    private static final String API_KEY = "12114072-7845e9d84252d3611d51cff25";
    private static final String PIXABAY_URL = "https://pixabay.com/api/";

    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, ACCESS_INTERNET_PERMISSIONS);
        } else {
            Log.i(TAG, "INTERNET permission already granted");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, String.format("String captured: %s", query));
                searchQuery = query;

                Call<HttpResponse> call = HttpRequestClient.getClient().create(HttpRequest.class).requestImages(API_KEY, convertQuery(searchQuery));
                call.enqueue(new Callback<HttpResponse>() {
                    @Override
                    public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                        Log.i(TAG, String.format("%b %d", response.isSuccessful(), response.code()));
                        List<PixalbayImages> mList = response.body().getHits();
                        Log.i(TAG, String.format("%d", mList.size()));
                    }

                    @Override
                    public void onFailure(Call<HttpResponse> call, Throwable t) {
                        Log.i(TAG, "Something went wrong. What?!");
                        Log.i(TAG, t.getMessage());
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case ACCESS_INTERNET_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "INTERNET Permission granted");
                } else {
                    Log.i(TAG, "INTERNET Permission not granted. Unable to continue");
                }
                return;
            }
        }
    }

    private String convertQuery(String query) {
        return query.replace(" ", "+");
    }
}
