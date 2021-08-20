package com.example.netflix.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.Locale;

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
        binding.imageBack.setOnClickListener(v ->
                onBackPressed());
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

                        if(tvShowDetailsResponse.getTvShowDetails().getGenres()!=null){
                            binding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                        }else{
                            binding.setGenre("N/A");
                        }
                        binding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime()+" Min");
                        binding.viewDivider1.setVisibility(View.VISIBLE);
                        binding.layoutMisc.setVisibility(View.VISIBLE);
                        binding.viewDivider2.setVisibility(View.VISIBLE);
                        binding.buttonWebsite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                                startActivity(intent);
                            }
                        });
                        binding.buttonWebsite.setVisibility(View.VISIBLE);
                        binding.buttonEpisodes.setVisibility(View.VISIBLE);
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
        binding.setTvShowName(getIntent().getStringExtra("name"));
        binding.setNetworkCountry(getIntent().getStringExtra("network") + " {"
                + getIntent().getStringExtra("country") + " }"
        );

        binding.setStatus(getIntent().getStringExtra("status"));
        binding.setStartedDate(getIntent().getStringExtra("startDate"));
        binding.textName.setVisibility(View.VISIBLE);
        binding.textNetworkCountry.setVisibility(View.VISIBLE);
        binding.textStatus.setVisibility(View.VISIBLE);
        binding.textStarted.setVisibility(View.VISIBLE);
    }
}