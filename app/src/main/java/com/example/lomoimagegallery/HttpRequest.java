package com.example.lomoimagegallery;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HttpRequest {

    @POST(".")
    Call<HttpResponse> requestImages(@Query("key") String apiKey, @Query("q") String query);
}
