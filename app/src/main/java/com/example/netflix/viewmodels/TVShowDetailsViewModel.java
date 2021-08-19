package com.example.netflix.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.netflix.repositories.TVShowDetailsRepository;
import com.example.netflix.responses.TVShowDetailsResponse;

public class TVShowDetailsViewModel extends ViewModel {

    private TVShowDetailsRepository tvShowDetailsRepository;

    public TVShowDetailsViewModel() {
        this.tvShowDetailsRepository = new TVShowDetailsRepository();
    }

    public LiveData<TVShowDetailsResponse> getTvShowDetails(String tvShowId ) {
        return tvShowDetailsRepository.getTVShowDetails(tvShowId);
    }
}
