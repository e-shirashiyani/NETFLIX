package com.example.netflix.repositories;

import android.support.annotation.NonNull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.netflix.network.ApiClient;
import com.example.netflix.network.ApiService;
import com.example.netflix.responses.TVShowDetailsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVShowDetailsRepository {

    private ApiService apiService;

    public TVShowDetailsRepository() {
        apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<TVShowDetailsResponse> getTVShowDetails(String tvShowId) {
        MutableLiveData<TVShowDetailsResponse> data = new MutableLiveData<>();
        apiService.getTVShoeDetails(tvShowId).enqueue(new Callback<TVShowDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowDetailsResponse> call,@NonNull Response<TVShowDetailsResponse> response) {
                data.setValue(response.body());

            }

            @Override
            public void onFailure(@NonNull Call<TVShowDetailsResponse> call,@NonNull Throwable t) {
                data.setValue(null);


            }
        });
        return data;

    }
}
