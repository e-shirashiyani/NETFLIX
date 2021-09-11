package com.example.netflix.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.netflix.R;
import com.example.netflix.adapters.TVShowsAdapter;
import com.example.netflix.databinding.ActivityMainBinding;
import com.example.netflix.listeners.TVShowsListener;
import com.example.netflix.models.TVShow;
import com.example.netflix.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TVShowsListener {

    private ActivityMainBinding binding;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private MostPopularTVShowsViewModel viewModel;
    private int currentPage = 1;
    private int totalAvailablePages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        doInitialization();


    }

    private void doInitialization() {
        binding.tvShowRecyclerView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShows, this);
        binding.tvShowRecyclerView.setAdapter(tvShowsAdapter);
        binding.tvShowRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.tvShowRecyclerView.canScrollVertically(1)) {
                    if (currentPage <= totalAvailablePages) {
                        currentPage += 1;
                        getMostPopularTVShows();

                    }
                }
            }
        });
        getMostPopularTVShows();

    }

    private void getMostPopularTVShows() {
        toggleLoading();
        viewModel.getMostPopularTVShows(currentPage).observe(this, mostPopularTVShowsResponse -> {
                    toggleLoading();
                    if (mostPopularTVShowsResponse != null) {
                        totalAvailablePages = mostPopularTVShowsResponse.getTotalPages();
                        if (mostPopularTVShowsResponse.getTvShows() != null) {
                            int oldCount = tvShows.size();
                            tvShows.addAll(mostPopularTVShowsResponse.getTvShows());
                            tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShows.size());
                        }
                    }
                }
        );
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            if (binding.getIsLoading() != null && binding.getIsLoading()) {
                binding.setIsLoading(false);
            } else {
                binding.setIsLoading(true);
            }
        } else {

            if (binding.getIsLoadingMore() != null && binding.getIsLoadingMore()) {
                binding.setIsLoadingMore(false);
            } else {
                binding.setIsLoadingMore(true);
            }

        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }
}