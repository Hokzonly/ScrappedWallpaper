package com.Africa.Wallpapers4Knewtest.ui.fr;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.Africa.Wallpapers4Knewtest.adapters.CategoryAdapter;
import com.Africa.Wallpapers4Knewtest.adapters.CategoryListAdapter;
import com.Africa.Wallpapers4Knewtest.adapters.FeaturedAdapter;
import com.Africa.Wallpapers4Knewtest.databinding.FragmentCategoryBinding;
import com.Africa.Wallpapers4Knewtest.models.CategoryModel;
import com.Africa.Wallpapers4Knewtest.models.CategoryListModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CategoriesFragment extends Fragment {
    FragmentCategoryBinding binding;
    List<CategoryModel> categoryModels = new ArrayList<>();
    List<CategoryListModel> searchResults = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    CategoryListAdapter searchAdapter;
    boolean isSearchMode = false;
    
    // Filtered words (same as SearchActivity)
    String s = "cock, deepthroat, dick, cumshot, tasty, baby, wet, fuck, sperm, jerk off, naked, ass, tits, fingering, masturbate, bitch, blowjob, prostitute, shit, bullshit, dumbass, dickhead, pussy, piss, asshole, boobs, butt, booty, dildo, erection, foreskin, gag, handjob, licking, nude, penis, porn, vibrator, viagra, virgin, vagina, vulva, wet dream, threesome, orgy, bdsm, hickey, condom, sexting, squirt, testicles, anal, bareback, bukkake, creampie, stripper, strap-on, missionary, make out, clitoris, cock ring, sugar daddy, cowgirl, reach-around, doggy style, fleshlight, contraceptive, makeup sex, lingerie, butt plug, moan, milf, wank, oral, sucking, kiss, dirty talk, straddle, blindfold, bondage, orgasm, french kiss, scissoring, hard, deeper, don't stop, slut, cumming, tasty, dirty, ode, men's milk, pound, jerk, prick, cunt, bastard, faggot, anal, anus,lingerie,bikini,pussy,anal,ass,skirt,sex,sexy,sexing,sixing,sexting,sexe";
    String[] array = s.split(",");
    List<String> filteredWords = new ArrayList<>(Arrays.asList(array));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            // Start shimmer loading immediately
            binding.WallpapersShimmer.setVisibility(View.VISIBLE);
            binding.WallpapersShimmer.startShimmer();
            binding.categoryRecycler.setVisibility(View.GONE);
            
            setupSearchView();
            new getCategoriesData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearchView() {
        binding.SearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (filteredWords.contains(query.toLowerCase())) {
                    showNoResults();
                } else {
                    if (searchAdapter != null) {
                        searchResults.clear();
                        searchAdapter.notifyDataSetChanged();
                    }
                    binding.Loading.setVisibility(View.VISIBLE);
                    binding.categoryRecycler.setVisibility(View.GONE);
                    binding.searchResultsRecycler.setVisibility(View.GONE);
                    binding.NoResults.setVisibility(View.GONE);
                    binding.SearchView.clearFocus();
                    
                    String processedQuery = query;
                    if (!processedQuery.isEmpty() && processedQuery.charAt(processedQuery.length() - 1) == ' ') {
                        processedQuery = processedQuery.substring(0, processedQuery.length() - 1);
                    }
                    
                    searchWallpapers(processedQuery);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Show categories when search is cleared
                    showCategories();
                } else {
                    // Hide categories when user starts typing
                    binding.categoryRecycler.setVisibility(View.GONE);
                    binding.searchResultsRecycler.setVisibility(View.GONE);
                    binding.NoResults.setVisibility(View.GONE);
                    
                    // Clear previous search results
                    if (searchAdapter != null) {
                        searchResults.clear();
                        searchAdapter.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });
    }

    private void searchWallpapers(String query) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (getActivity() == null) {
                return;
            }
            
            try {
                Document document = Jsoup.connect("https://wallpapercave.com/search?q=" + query.replace(" ", "+"))
                        .userAgent("chrome")
                        .followRedirects(true)
                        .get();

                Elements elements = document.select("div#content").select("div#popular").select("a.albumthumbnail");
                searchResults.clear();
                
                for (Element element : elements) {
                    String categoryName = element.select("div.psc").select("p.title").text();
                    String categoryDescription = element.select("div.psc").select("p.number").text();
                    String categoryImage = element.select("div.albumphoto").select("img.thumbnail").attr("src");
                    String categoryUrl = element.attr("href");
                    searchResults.add(new CategoryListModel(categoryName, categoryDescription, categoryImage, categoryUrl));
                }

                getActivity().runOnUiThread(() -> {
                    if (searchResults.size() == 0) {
                        showNoResults();
                    } else {
                        showSearchResults();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    showNoResults();
                });
            }
        });
    }

    private void showSearchResults() {
        binding.Loading.setVisibility(View.GONE);
        binding.categoryRecycler.setVisibility(View.GONE);
        binding.searchResultsRecycler.setVisibility(View.VISIBLE);
        binding.NoResults.setVisibility(View.GONE);
        
        // Ensure shimmer is hidden
        if (binding.WallpapersShimmer != null) {
            binding.WallpapersShimmer.setVisibility(View.GONE);
            binding.WallpapersShimmer.stopShimmer();
        }
        
        searchAdapter = new CategoryListAdapter(requireActivity(), searchResults);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = Objects.requireNonNull(binding.searchResultsRecycler.getAdapter()).getItemViewType(position);
                return viewType == FeaturedAdapter.AD_VIEW ? 2 : 1;
            }
        });
        binding.searchResultsRecycler.setLayoutManager(gridLayoutManager);
        binding.searchResultsRecycler.setAdapter(searchAdapter);
    }

    private void showCategories() {
        binding.Loading.setVisibility(View.GONE);
        binding.categoryRecycler.setVisibility(View.VISIBLE);
        binding.searchResultsRecycler.setVisibility(View.GONE);
        binding.NoResults.setVisibility(View.GONE);
        
        // Ensure shimmer is hidden when showing categories
        if (binding.WallpapersShimmer != null) {
            binding.WallpapersShimmer.setVisibility(View.GONE);
            binding.WallpapersShimmer.stopShimmer();
        }
    }

    private void showNoResults() {
        binding.Loading.setVisibility(View.GONE);
        binding.categoryRecycler.setVisibility(View.GONE);
        binding.searchResultsRecycler.setVisibility(View.GONE);
        binding.NoResults.setVisibility(View.VISIBLE);
        
        // Ensure shimmer is hidden
        if (binding.WallpapersShimmer != null) {
            binding.WallpapersShimmer.setVisibility(View.GONE);
            binding.WallpapersShimmer.stopShimmer();
        }
    }

    public class getCategoriesData extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://wallpapercave.com/categories").userAgent("opera").get();
                Elements elements = doc.select("div#content").select("ul#catsinbox").select("li");

                for (Element element : elements) {
                    String categoryName = element.select("a").text();
                    String categoryUrl = element.select("a").attr("href");
                    if (!categoryName.contains("Religion") && !categoryName.contains("Fortnite")) {
                        categoryModels.add(new CategoryModel(categoryName, categoryUrl));
                    }
                }
                
                Log.d("CategoriesFragment", "Loaded " + categoryModels.size() + " categories");

            } catch (Exception e) {
                Log.e("CategoriesFragment", "Error loading categories: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (isAdded()) {
                if (categoryModels.size() > 0) {
                    categoryAdapter = new CategoryAdapter(requireActivity(), categoryModels);
                    // Use 2 columns for better visual appeal
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                    binding.categoryRecycler.setLayoutManager(gridLayoutManager);
                    binding.categoryRecycler.setAdapter(categoryAdapter);
                    binding.categoryRecycler.setVisibility(View.VISIBLE);
                    binding.WallpapersShimmer.setVisibility(View.GONE);
                    binding.WallpapersShimmer.stopShimmer();
                } else {
                    // If no categories loaded, show no results
                    binding.WallpapersShimmer.setVisibility(View.GONE);
                    binding.WallpapersShimmer.stopShimmer();
                    binding.NoResults.setVisibility(View.VISIBLE);
                }
            } else {
                Log.e("TAG", "onPostExecute: " + "Fragment not attached");
            }
        }
    }
}