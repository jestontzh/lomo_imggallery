package com.example.lomoimagegallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

//    private static final int ACCESS_INTERNET_PERMISSIONS = 1;
//    private static final int WRITE_EXTERNAL_STORAGE_PERMISSIONS = 2;
//    private static final int READ_EXTERNAL_STORAGE_PERMISSIONS = 3;
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private static final String API_KEY = "12114072-7845e9d84252d3611d51cff25";
    private static final int RESULTS_PER_PAGE = 30;


    private ProgressBar progressBar;
    private StaggeredGridLayoutManager sglManager;
    private RecyclerView recyclerView;
    private ImageFragment imageFragment;
    private int spanCount;
    private int pageNum = 1; // default to 1

    private String searchQuery;
    private List<PixalbayImages> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] appPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        for (String permission : appPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), PERMISSIONS_REQUEST_CODE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.indeterminate_bar);
        progressBar.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.image_recycler_view);
        recyclerView.setHasFixedSize(false);

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

        recyclerView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            void onSwipeLeft() {
                // Go next page
                pageNum++;
                Log.i(TAG, String.format("Current page no: %d", pageNum));
                Toast.makeText(getApplicationContext(), String.format("Page %d", pageNum), Toast.LENGTH_SHORT).show();
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().beginTransaction().remove(imageFragment).commit();
                }
                progressBar.setVisibility(View.VISIBLE);
                loadImages();
            }

            @Override
            void onSwipeRight() {
                // Go previous page
                if (pageNum == 1) {
                    Log.i(TAG, String.format("Already at page 1"));
                    Toast.makeText(getApplicationContext(), "Already at page 1", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    pageNum--;
                    Log.i(TAG, String.format("Current page no: %d", pageNum));
                    Toast.makeText(getApplicationContext(), String.format("Page %d", pageNum), Toast.LENGTH_SHORT).show();
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().beginTransaction().remove(imageFragment).commit();
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    loadImages();
                }
            }
        });
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
                pageNum = 1;
                progressBar.setVisibility(View.VISIBLE);

                loadImages();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    private void loadImages() {
        Call<HttpResponse> call = HttpRequestClient.getClient().create(HttpRequest.class).requestImages(API_KEY, convertQuery(searchQuery), pageNum, RESULTS_PER_PAGE);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                Log.i(TAG, String.format("%b %d", response.isSuccessful(), response.code()));
                progressBar.setVisibility(View.INVISIBLE);
                try {
                    mList = response.body().getHits();
                    PixabayImageRecyclerViewAdapter rcAdapter = new PixabayImageRecyclerViewAdapter(getApplicationContext(), mList, new RecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            // TODO: FRAGMENT SET UP HERE
//                            Log.i(TAG, mList.get(position).getLargeImageURL());
                            String largeImageUrl = mList.get(position).getLargeImageURL();
                            int imageHeight = mList.get(position).getImageHeight();
                            int imageWidth = mList.get(position).getImageWidth();
                            String userName = mList.get(position).getUser();

                            imageFragment = ImageFragment.newInstance(largeImageUrl, imageHeight, imageWidth, userName);
                            getSupportFragmentManager().beginTransaction().replace(R.id.image_card_view_frame, imageFragment).commit();
                        }
                    });
                    recyclerView.setAdapter(rcAdapter);
                } catch (NullPointerException e) {
                    if (pageNum == 1) {
                        Toast.makeText(getApplicationContext(), "No result for your query.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No results left for your query", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<HttpResponse> call, Throwable t) {
                Log.i(TAG, "Query Failure - Try again later");
                Log.i(TAG, t.getMessage());
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        return;
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
