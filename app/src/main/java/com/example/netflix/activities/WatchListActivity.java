package com.example.netflix.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.netflix.R;
import com.example.netflix.adapters.WatchListAdapter;
import com.example.netflix.databinding.ActivityWatchListBinding;
import com.example.netflix.listeners.WatchListListener;
import com.example.netflix.models.TVShow;
import com.example.netflix.utilities.TempDataHolder;
import com.example.netflix.viewmodels.WatchListViewModel;

import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchListActivity extends AppCompatActivity implements WatchListListener {

    private ActivityWatchListBinding binding;
    private WatchListViewModel viewModel;
    private WatchListAdapter watchListAdapter;
    private List<TVShow> watchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch_list);
        doInitialization();
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(WatchListViewModel.class);
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        watchList=new ArrayList<>();
        loadWatchList();
    }

    private void loadWatchList() {
        binding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.loadWatchList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShows -> {
                    binding.setIsLoading(false);
                    if (watchList.size()>0){
                        watchList.clear();
                    }
                    watchList.addAll(tvShows);
                    watchListAdapter=new WatchListAdapter(watchList,this);
                    binding.watchListRecyclerView.setAdapter(watchListAdapter);
                    binding.watchListRecyclerView.setVisibility(View.VISIBLE);
                    compositeDisposable.dispose();

                }));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TempDataHolder.IS_WATCHLIST_UPDATED){
            loadWatchList();
            TempDataHolder.IS_WATCHLIST_UPDATED=false;

        }

    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent=new Intent(getApplicationContext(),TVShowDetailsActivity.class);
        intent.putExtra("tvShow",tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTVShowFromWatchList(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposableForDelete=new CompositeDisposable();
        compositeDisposableForDelete.add(viewModel.removeTVShowFromWatchList(tvShow)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(()->{
            watchList.remove(position);
            watchListAdapter.notifyItemRemoved(position);
            watchListAdapter.notifyItemRangeChanged(position,watchListAdapter.getItemCount());
            compositeDisposableForDelete.dispose();
        }));


    }
}