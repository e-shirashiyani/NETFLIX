package com.example.netflix.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.netflix.database.TVShowDatabase;
import com.example.netflix.models.TVShow;
import com.example.netflix.repositories.TVShowDetailsRepository;
import com.example.netflix.responses.TVShowDetailsResponse;

import io.reactivex.Completable;

public class TVShowDetailsViewModel extends AndroidViewModel {

    private TVShowDetailsRepository tvShowDetailsRepository;
    private TVShowDatabase tvShowDatabase;

    public TVShowDetailsViewModel(@NonNull Application application) {
        super(application);
        this.tvShowDetailsRepository = new TVShowDetailsRepository();
        tvShowDatabase=TVShowDatabase.getTvShowDatabase(application);
    }

    public LiveData<TVShowDetailsResponse> getTvShowDetails(String tvShowId ) {
        return tvShowDetailsRepository.getTVShowDetails(tvShowId);
    }

    public Completable addToWatchList(TVShow tvShow){
        return tvShowDatabase.tvShowDao().addToWatchlist(tvShow);
    }

}
