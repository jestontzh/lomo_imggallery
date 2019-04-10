package com.example.lomoimagegallery;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HttpRequest {

    @POST(".")
    Call<HttpResponse> requestImages(@Query("key") String apiKey,
                                     @Query("q") String query,
                                     @Query("page") int page,
                                     @Query("per_page") int resultsPerPage);
}
