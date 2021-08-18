package com.example.netflix.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.example.netflix.R;
import com.example.netflix.adapters.TVShowsAdapter;
import com.example.netflix.databinding.ActivityMainBinding;
import com.example.netflix.models.TVShow;
import com.example.netflix.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private List<TVShow> tvShows= new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;

    private MostPopularTVShowsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        doInitialization();


    }

    private void doInitialization(){
        binding.tvShowRecyclerView.setHasFixedSize(true);
        viewModel=new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        tvShowsAdapter= new TVShowsAdapter(tvShows);
        binding.tvShowRecyclerView.setAdapter(tvShowsAdapter);
        getMostPopularTVShows();
    }

    private void getMostPopularTVShows(){
        binding.setIsLoading(true);

        viewModel.getMostPopularTVShows(0).observe(this,mostPopularTVShowsResponse->{
                binding.setIsLoading(false);
        if(mostPopularTVShowsResponse!=null) {
            if(mostPopularTVShowsResponse.getTvShows()!=null){
                tvShows.addAll(mostPopularTVShowsResponse.getTvShows());
                tvShowsAdapter.notifyDataSetChanged();
            }
        }
        }
        );
    }
}