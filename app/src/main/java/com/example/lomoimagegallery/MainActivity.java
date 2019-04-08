package com.example.lomoimagegallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.POST;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
    private static final int RESULTS_PER_PAGE = 30;

    private StaggeredGridLayoutManager sglManager;
    private RecyclerView recyclerView;
    private int spanCount;
    private int pageNum = 1; // default to 1

    private String searchQuery;
    private List<PixalbayImages> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.image_recycler_view);
        recyclerView.setHasFixedSize(true);

        switch(getScreenWidthFactor()) {
            case 2:
                spanCount = 2;
                break;
            case 3:
                spanCount = 3;
                break;
            case 4:
                spanCount = 4;
                break;
        }
        sglManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglManager);

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

                Call<HttpResponse> call = HttpRequestClient.getClient().create(HttpRequest.class).requestImages(API_KEY, convertQuery(searchQuery), pageNum, RESULTS_PER_PAGE);
                call.enqueue(new Callback<HttpResponse>() {
                    @Override
                    public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                        Log.i(TAG, String.format("%b %d", response.isSuccessful(), response.code()));
                        mList = response.body().getHits();
                        PixabayImageRecyclerViewAdapter rcAdapter = new PixabayImageRecyclerViewAdapter(MainActivity.this, mList);
                        recyclerView.setAdapter(rcAdapter);
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

    private int getScreenWidthFactor() {
        int currentWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        Log.i(TAG, String.format("%d", currentWidth));
        if (currentWidth <= 1280) {
            return 2;
        } else if (currentWidth <= 1600) {
            return 3;
        } else {
            return 4;
        }
    }
}
