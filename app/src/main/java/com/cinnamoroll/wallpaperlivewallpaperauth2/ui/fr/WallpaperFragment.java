package com.cinnamoroll.wallpaperlivewallpaperauth2.ui.fr;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cinnamoroll.wallpaperlivewallpaperauth2.MyUtils;
import com.cinnamoroll.wallpaperlivewallpaperauth2.adapters.FeaturedAdapter;
import com.cinnamoroll.wallpaperlivewallpaperauth2.databinding.FragmentHomeBinding;
import com.cinnamoroll.wallpaperlivewallpaperauth2.models.FeaturedModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class WallpaperFragment extends Fragment {

    FragmentHomeBinding binding;
    List<FeaturedModel> featuredModels = new ArrayList<>();
    FeaturedAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            if (MyUtils.appControl.isShowMockupData()){
                binding.load1.setVisibility(View.VISIBLE);
                binding.load2.setVisibility(View.VISIBLE);
                binding.featuredRecycler.setVisibility(View.GONE);
                binding.WallpapersShimmer.stopShimmer();
                binding.WallpapersShimmer.setVisibility(View.GONE);
                binding.SampleWallpapers.setVisibility(View.VISIBLE);
                List<FeaturedModel> featuredModels1 = new ArrayList<>();
                featuredModels1.add(new FeaturedModel("https://epicappquest.com/samplewallpapers/wall1.jpeg", false));
                featuredModels1.add(new FeaturedModel("https://epicappquest.com/samplewallpapers/wall2.jpg", false));
                featuredModels1.add(new FeaturedModel("https://epicappquest.com/samplewallpapers/wall3.jpeg", false));
                featuredModels1.add(new FeaturedModel("https://epicappquest.com/samplewallpapers/wall4.jpeg", false));
                featuredModels1.add(new FeaturedModel("https://epicappquest.com/samplewallpapers/wall5.jpeg", false));
                featuredModels1.add(new FeaturedModel("https://epicappquest.com/samplewallpapers/wall6.jpeg", false));
                binding.SampleWallpapers.setLayoutManager(new GridLayoutManager(getContext(), 3));
                FeaturedAdapter adapter1 = new FeaturedAdapter(getActivity(), featuredModels1);
                binding.SampleWallpapers.setAdapter(adapter1);

            }else {
                    GetWallpaperCaveData getWallpaperCaveData = new GetWallpaperCaveData();
                    getWallpaperCaveData.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class GetWallpaperCaveData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Focus on the main URL first to get all wallpapers from that specific page
                String mainUrl = "https://wallpapercave.com/cinnamoroll-iphone-wallpapers";
                
                Document doc = Jsoup.connect(mainUrl).userAgent("firefox").followRedirects(false).get();
                Elements elements = doc.select("div#albumwp").select("div.wallpaper");

                Log.d("WallpaperFragment", "Found " + elements.size() + " wallpapers from main URL");

                for (Element element : elements) {
                    String img = element.select("img.wimg").attr("src");
                    if (!img.isEmpty()) {
                        String fullImageUrl = "https://wallpapercave.com" + img;
                        Log.d("WallpaperFragment", "Adding wallpaper: " + fullImageUrl);
                        
                        // All wallpapers from the main URL are premium (first 6) or regular
                        if (elements.indexOf(element) > 5) {
                            featuredModels.add(new FeaturedModel(fullImageUrl, true));
                        } else {
                            featuredModels.add(new FeaturedModel(fullImageUrl, false));
                        }
                    }
                }

                // If we need more wallpapers, add from additional sources
                if (featuredModels.size() < 20) {
                    String[] additionalSources = {
                        "https://wallpapercave.com/cinnamoroll-wallpapers",
                        "https://wallpapercave.com/cinnamoroll-desktop-wallpapers",
                        "https://wallpapercave.com/cinnamoroll-mobile-wallpapers"
                    };

                    for (String source : additionalSources) {
                        try {
                            Document additionalDoc = Jsoup.connect(source).userAgent("firefox").followRedirects(false).get();
                            Elements additionalElements = additionalDoc.select("div#albumwp").select("div.wallpaper");

                            for (Element element : additionalElements) {
                                String img = element.select("img.wimg").attr("src");
                                if (!img.isEmpty()) {
                                    String fullImageUrl = "https://wallpapercave.com" + img;
                                    featuredModels.add(new FeaturedModel(fullImageUrl, true));
                                }
                            }
                        } catch (Exception e) {
                            Log.e("WallpaperFragment", "Error scraping from " + source + ": " + e.getMessage());
                        }
                    }
                }

                Log.d("WallpaperFragment", "Total wallpapers loaded: " + featuredModels.size());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (isAdded()) {
                setupCarousel();
                binding.WallpapersShimmer.stopShimmer();
                binding.WallpapersShimmer.setVisibility(View.GONE);
            } else {
                Log.e("TAG", "onPostExecute: " + "Fragment not attached");
            }
        }
    }

    private void setupCarousel() {
        adapter = new FeaturedAdapter(getActivity(), featuredModels);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = Objects.requireNonNull(binding.featuredRecycler.getAdapter()).getItemViewType(position);
                return viewType == FeaturedAdapter.AD_VIEW ? 3 : 1;
            }
        });
        binding.featuredRecycler.setLayoutManager(gridLayoutManager);
        binding.featuredRecycler.setAdapter(adapter);
        binding.featuredRecycler.setHasFixedSize(true);
        
        // Setup navigation buttons for scrolling through all wallpapers
        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        // Left navigation button - scroll up
        binding.btnPrevious.setOnClickListener(v -> {
            // Scroll to previous section
            GridLayoutManager layoutManager = (GridLayoutManager) binding.featuredRecycler.getLayoutManager();
            if (layoutManager != null) {
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                int targetPosition = Math.max(0, firstVisible - 9); // Move up by 3 rows
                binding.featuredRecycler.smoothScrollToPosition(targetPosition);
            }
        });

        // Right navigation button - scroll down
        binding.btnNext.setOnClickListener(v -> {
            // Scroll to next section
            GridLayoutManager layoutManager = (GridLayoutManager) binding.featuredRecycler.getLayoutManager();
            if (layoutManager != null) {
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                int targetPosition = Math.min(featuredModels.size() - 1, lastVisible + 9); // Move down by 3 rows
                binding.featuredRecycler.smoothScrollToPosition(targetPosition);
            }
        });

        // Update button visibility
        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        GridLayoutManager layoutManager = (GridLayoutManager) binding.featuredRecycler.getLayoutManager();
        if (layoutManager != null) {
            int firstVisible = layoutManager.findFirstVisibleItemPosition();
            int lastVisible = layoutManager.findLastVisibleItemPosition();
            
            // Show/hide previous button
            binding.btnPrevious.setVisibility(firstVisible > 0 ? View.VISIBLE : View.GONE);
            
            // Show/hide next button
            binding.btnNext.setVisibility(lastVisible < featuredModels.size() - 1 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}