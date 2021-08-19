package com.example.netflix.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.example.netflix.R;
import com.example.netflix.adapters.ImageSliderAdapter;
import com.example.netflix.databinding.ActivityTvshowDetailsBinding;
import com.example.netflix.viewmodels.TVShowDetailsViewModel;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTvshowDetailsBinding binding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_details);
        doInitialization();
    }

    private void doInitialization() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        getTVShowDetails();
    }

    private void getTVShowDetails() {
        binding.setIsLoading(true);
        String tvShowId = String.valueOf(getIntent().getIntExtra("id", -1));
        tvShowDetailsViewModel.getTvShowDetails(tvShowId).observe(
                this, tvShowDetailsResponse -> {
                    binding.setIsLoading(false);
                    if (tvShowDetailsResponse.getTvShowDetails() != null) {
                        if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                            loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());


                        }
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
}