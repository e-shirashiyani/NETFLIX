package com.example.netflix.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.netflix.models.TVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import retrofit2.http.DELETE;

@Dao
public interface TVShowDao {

    @Query("SELECT * FROM tvShows")
    Flowable<List<TVShow>> getWatchlist();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addToWatchlist(TVShow tvShow);

    @DELETE
    Completable removeFromWatchlist(TVShow tvShow);

    @Query("SELECT * FROM tvshows WHERE id =:tvShowId")
    Flowable<TVShow> getTVShowFromWatchlist(String tvShowId);
}
