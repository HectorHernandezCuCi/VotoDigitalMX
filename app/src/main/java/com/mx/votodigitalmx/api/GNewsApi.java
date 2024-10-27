package com.mx.votodigitalmx.api;

import com.mx.votodigitalmx.model.GNewsResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GNewsApi {
    String BASE_URL = "https://gnews.io/api/v4/";

    @GET("top-headlines")
    Call<GNewsResponse> getTopHeadlines(@Query("country") String country,
                                        @Query("lang") String language,
                                        @Query("token") String apiKey);

    static GNewsApi create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GNewsApi.class);
    }
}
