package com.example.netflix.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.example.netflix.R;
import com.example.netflix.adapters.EpisodesAdapter;
import com.example.netflix.adapters.ImageSliderAdapter;
import com.example.netflix.databinding.ActivityTvshowDetailsBinding;
import com.example.netflix.databinding.LayoutEpisodesBottomSheetBinding;
import com.example.netflix.models.TVShow;
import com.example.netflix.viewmodels.TVShowDetailsViewModel;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTvshowDetailsBinding binding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog episodesBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_details);
        doInitialization();
    }

    private void doInitialization() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        binding.imageBack.setOnClickListener(v ->
                onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        getTVShowDetails();
    }

    private void getTVShowDetails() {
        binding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());
        tvShowDetailsViewModel.getTvShowDetails(tvShowId).observe(
                this, tvShowDetailsResponse -> {
                    binding.setIsLoading(false);
                    if (tvShowDetailsResponse.getTvShowDetails() != null) {
                        if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                            loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                        }
                        binding.setTvShowImageUrl(
                                tvShowDetailsResponse.getTvShowDetails().getImagePath()
                        );

                        binding.imageTvShow.setVisibility(View.VISIBLE);
                        binding.setDescription(
                                String.valueOf(
                                        HtmlCompat.fromHtml(
                                                tvShowDetailsResponse.getTvShowDetails().getDescription()
                                                , HtmlCompat.FROM_HTML_MODE_LEGACY
                                        )
                                )
                        );
                        binding.textDescription.setVisibility(View.VISIBLE);
                        binding.textReadMore.setVisibility(View.VISIBLE);
                        binding.textReadMore.setOnClickListener(v -> {
                            if (binding.textReadMore.getText().toString().equals("Read More")) {
                                binding.textDescription.setMaxLines(Integer.MAX_VALUE);
                                binding.textDescription.setEllipsize(null);
                                binding.textReadMore.setText("Read less");
                            } else {
                                binding.textDescription.setMaxLines(4);
                                binding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                                binding.textReadMore.setText(R.string.read_more);


                            }
                        });
                        binding.setRating(String.format(
                                Locale.getDefault(),
                                "%.2f",
                                Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())
                                )
                        );

                        if (tvShowDetailsResponse.getTvShowDetails().getGenres() != null) {
                            binding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                        } else {
                            binding.setGenre("N/A");
                        }
                        binding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime() + " Min");
                        binding.viewDivider1.setVisibility(View.VISIBLE);
                        binding.layoutMisc.setVisibility(View.VISIBLE);
                        binding.viewDivider2.setVisibility(View.VISIBLE);
                        binding.buttonWebsite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                                startActivity(intent);
                            }
                        });
                        binding.buttonWebsite.setVisibility(View.VISIBLE);
                        binding.buttonEpisodes.setVisibility(View.VISIBLE);
                        binding.buttonEpisodes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (episodesBottomSheetDialog == null) {
                                    episodesBottomSheetDialog = new BottomSheetDialog(
                                            TVShowDetailsActivity.this);
                                    layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                            LayoutInflater.from(TVShowDetailsActivity.this),
                                            R.layout.layout_episodes_bottom_sheet,
                                            findViewById(R.id.episodesContainer),
                                            false
                                    );
                                    episodesBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                                    layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(
                                            new EpisodesAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes()));

                                    layoutEpisodesBottomSheetBinding.textTitle.setText(
                                            String.format("Episode | %s", tvShow.getName())
                                    );
                                    layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            episodesBottomSheetDialog.dismiss();
                                        }
                                    });
                                }

                                FrameLayout frameLayout = episodesBottomSheetDialog.findViewById(
                                        com.google.android.material.R.id.design_bottom_sheet
                                );
                                if (frameLayout != null) {
                                    BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                                    bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                }

                                episodesBottomSheetDialog.show();
                            }
                        });

                        binding.imageWatchlist.setOnClickListener(view -> {
                            CompositeDisposable compositeDisposable = new CompositeDisposable();
                            if (isTVShowAvailableInWatchlist) {
                                compositeDisposable.add(tvShowDetailsViewModel.removeTVShowFromWatchlist(tvShow)
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            isTVShowAvailableInWatchlist = false;
                                            TempDataHolder.IS_WATCHLIST_UPDATE = true;
                                            binding.imageWatchlist.setImageResource(R.drawable.ic_round_remove_red_eye_24);
                                            Toast.makeText(getApplicationContext(), "Removed from watchlist", Toast.LENGTH_SHORT).show();
                                        }));
                            } else {
                                compositeDisposable.add(tvShowDetailsViewModel
                                        .addToWatchlist(tvShow)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            TempDataHolder.IS_WATCHLIST_UPDATE = true;
                                            binding.imageWatchlist.setImageResource(R.drawable.ic_added);
                                            Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        })
                                );
                            }
                        });
                        activityTVShowDetailsBinding.imageWatchlist.setVisibility(View.VISIBLE);
                        loadBasicTVShowDetails();
                    }

                }
        );

    }

    private void loadImageSlider(String[] sliderImages) {
        binding.slideViewPager.setOffscreenPageLimit(1);
        binding.slideViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        binding.slideViewPager.setVisibility(View.VISIBLE);
        binding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicator(sliderImages.length);
        binding.slideViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });

    }

    private void setupSliderIndicator(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive
            ));

            indicators[i].setLayoutParams(layoutParams);
            binding.layoutSliderIndicator.addView(indicators[i]);
        }

        binding.layoutSliderIndicator.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);


    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = binding.layoutSliderIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView ImageView = (ImageView) binding.layoutSliderIndicator.getChildAt(i);
            if (i == position) {
                ImageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),
                                R.drawable.background_slider_indicator_active)
                );
            } else {
                ImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.background_slider_indicator_inactive));
            }

        }
    }

    private void loadBasicTVShowDetails() {
        binding.setTvShowName(tvShow.getName());
        binding.setNetworkCountry(tvShow.getNetwork() + " {"
                + tvShow.getCountry() + " }"
        );

        binding.setStatus(tvShow.getStatus());
        binding.setStartedDate(tvShow.getStartDate());
        binding.textName.setVisibility(View.VISIBLE);
        binding.textNetworkCountry.setVisibility(View.VISIBLE);
        binding.textStatus.setVisibility(View.VISIBLE);
        binding.textStarted.setVisibility(View.VISIBLE);
    }
}