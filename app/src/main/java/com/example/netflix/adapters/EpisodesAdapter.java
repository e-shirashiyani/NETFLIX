package com.example.netflix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix.R;
import com.example.netflix.databinding.ItemContainerEpisodeBinding;
import com.example.netflix.models.Episode;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>{

    private List<Episode> episodes;
    private LayoutInflater layoutInflater;

    public EpisodesAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater==null){
            Context context;
            layoutInflater=layoutInflater.from(parent.getContext());
        }
        ItemContainerEpisodeBinding itemContainerEpisodeBinding= DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container_episode,parent,false
        );
        return new EpisodeViewHolder(itemContainerEpisodeBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        holder.bindEpisode(episodes.get(position));

    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    static class EpisodeViewHolder extends RecyclerView.ViewHolder{

        private ItemContainerEpisodeBinding binding;

        public EpisodeViewHolder (ItemContainerEpisodeBinding itemContainerEpisodeBinding){
            super(itemContainerEpisodeBinding.getRoot());
            this.binding=itemContainerEpisodeBinding;
        }

        public void bindEpisode(Episode episode){
            String title="5";
            String season=episode.getEpisode();
            if (season.length()==1){
                season="0".concat(season);
            }
            String episodeNumber=episode.getEpisode();
            if(episodeNumber.length()==1){
                episodeNumber="0".concat(episodeNumber);
            }
            episodeNumber="E".concat(episodeNumber);
            title=title.concat(season).concat(episodeNumber);
            binding.setTitle(title);
            binding.setName(episode.getName());
            binding.setAirData(episode.getAir_date());

        }
    }
}
