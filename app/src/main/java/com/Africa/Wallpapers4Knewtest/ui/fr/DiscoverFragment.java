package com.Africa.Wallpapers4Knewtest.ui.fr;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Africa.Wallpapers4Knewtest.adapters.CategoryAdapter;
import com.Africa.Wallpapers4Knewtest.adapters.CategoryListAdapter;
import com.Africa.Wallpapers4Knewtest.adapters.FeaturedAdapter;
import com.Africa.Wallpapers4Knewtest.databinding.FragmentDiscoverBinding;
import com.Africa.Wallpapers4Knewtest.models.CategoryModel;
import com.Africa.Wallpapers4Knewtest.models.CategoryListModel;
import com.Africa.Wallpapers4Knewtest.models.FeaturedModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.Africa.Wallpapers4Knewtest.MyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DiscoverFragment extends Fragment {
    FragmentDiscoverBinding binding;
    List<CategoryModel> allCategories = new ArrayList<>();
    List<CategoryModel> filteredCategories = new ArrayList<>();
    List<CategoryListModel> searchResults = new ArrayList<>();
    List<FeaturedModel> carouselWallpapers = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    CategoryListAdapter searchAdapter;
    FeaturedAdapter carouselAdapter;
    boolean isSearchMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupSearchFunctionality();
        loadCarouselData();
        loadCategoriesData();
    }

    private void setupSearchFunctionality() {
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if search is cleared
                if (s.toString().trim().isEmpty()) {
                    // Restore original categories
                    restoreOriginalCategories();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add search button functionality
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchWallpapers(query);
                } else {
                    // If search is empty, restore original categories
                    restoreOriginalCategories();
                }
                // Hide the keyboard
                hideKeyboard();
                return true;
            }
            return false;
        });
    }

    private void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(binding.searchInput.getWindowToken(), 0);
            }
        }
    }

    private void restoreOriginalCategories() {
        // Clear search results and show original categories
        isSearchMode = false;
        searchResults.clear();
        
        if (searchAdapter != null) {
            searchAdapter.clear();
        }
        
        // Show the categories recycler and hide shimmer
        binding.categoriesRecycler.setVisibility(View.VISIBLE);
        binding.categoriesShimmer.setVisibility(View.GONE);
        binding.categoriesShimmer.stopShimmer();
        
        // Update with original categories
        if (categoryAdapter != null) {
            categoryAdapter.updateCategories(filteredCategories);
        }
    }

    private void searchWallpapers(String query) {
        // Show loading
        binding.categoriesRecycler.setVisibility(View.GONE);
        binding.categoriesShimmer.setVisibility(View.VISIBLE);
        binding.categoriesShimmer.startShimmer();

        // Clear previous results
        searchResults.clear();
        isSearchMode = true;

        // Search wallpapers using the same logic as SearchActivity
        new SearchWallpapersTask().execute(query);
    }

    private class SearchWallpapersTask extends AsyncTask<String, Void, List<CategoryListModel>> {
        @Override
        protected List<CategoryListModel> doInBackground(String... queries) {
            List<CategoryListModel> results = new ArrayList<>();
            String query = queries[0];
            
            try {
                Document document = Jsoup.connect("https://wallpapercave.com/search?q=" + query.replace(" ", "+"))
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .followRedirects(true)
                        .timeout(15000)
                        .get();

                // Look for wallpaper collections (albums) - same as SearchActivity
                Elements albumElements = document.select("div#content").select("div#popular").select("a.albumthumbnail");
                
                // Add wallpaper collections
                for (Element element : albumElements) {
                    String categoryName = element.select("div.psc").select("p.title").text();
                    String categoryDescription = element.select("div.psc").select("p.number").text();
                    String categoryImage = element.select("div.albumphoto").select("img.thumbnail").attr("src");
                    String categoryUrl = element.attr("href");
                    
                    if (!categoryName.isEmpty()) {
                        // Make sure the image URL is complete
                        if (!categoryImage.isEmpty() && !categoryImage.startsWith("http")) {
                            categoryImage = "https://wallpapercave.com" + categoryImage;
                        }
                        results.add(new CategoryListModel(categoryName, categoryDescription, categoryImage, categoryUrl));
                    }
                }
                
                // If no results from popular section, try other sections
                if (results.isEmpty()) {
                    Elements allAlbumElements = document.select("div#content").select("a.albumthumbnail");
                    for (Element element : allAlbumElements) {
                        String categoryName = element.select("div.psc").select("p.title").text();
                        String categoryDescription = element.select("div.psc").select("p.number").text();
                        String categoryImage = element.select("div.albumphoto").select("img.thumbnail").attr("src");
                        String categoryUrl = element.attr("href");
                        
                        if (!categoryName.isEmpty()) {
                            // Make sure the image URL is complete
                            if (!categoryImage.isEmpty() && !categoryImage.startsWith("http")) {
                                categoryImage = "https://wallpapercave.com" + categoryImage;
                            }
                            results.add(new CategoryListModel(categoryName, categoryDescription, categoryImage, categoryUrl));
                        }
                    }
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return results;
        }

        @Override
        protected void onPostExecute(List<CategoryListModel> results) {
            if (isAdded()) {
                binding.categoriesShimmer.stopShimmer();
                binding.categoriesShimmer.setVisibility(View.GONE);
                
                if (results.isEmpty()) {
                    // Show no results message
                    binding.categoriesRecycler.setVisibility(View.GONE);
                    // You can add a TextView to show "No results found"
                } else {
                    // Show search results using CategoryListAdapter
                    searchResults.clear();
                    searchResults.addAll(results);
                    binding.categoriesRecycler.setVisibility(View.VISIBLE);
                    
                    // Use CategoryListAdapter for search results (same as SearchActivity)
                    searchAdapter = new CategoryListAdapter(requireActivity(), searchResults);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            int viewType = Objects.requireNonNull(binding.categoriesRecycler.getAdapter()).getItemViewType(position);
                            return viewType == FeaturedAdapter.AD_VIEW ? 2 : 1;
                        }
                    });
                    binding.categoriesRecycler.setLayoutManager(gridLayoutManager);
                    binding.categoriesRecycler.setAdapter(searchAdapter);
                }
            }
        }
    }

    private void filterCategories(String query) {
        if (query.isEmpty()) {
            filteredCategories.clear();
            filteredCategories.addAll(allCategories);
        } else {
            filteredCategories.clear();
            for (CategoryModel category : allCategories) {
                if (category.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredCategories.add(category);
                }
            }
        }
        
        if (categoryAdapter != null) {
            categoryAdapter.updateCategories(filteredCategories);
        }
    }

    private void loadCarouselData() {
        // Set up the adapter with empty list first
        carouselAdapter = new FeaturedAdapter(getActivity(), carouselWallpapers);
        binding.carouselRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.carouselRecycler.setAdapter(carouselAdapter);
        
        // Load real wallpapers from wallpapercave
        new LoadCarouselWallpapers().execute();
    }

    public class LoadCarouselWallpapers extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Use the NicheLink from MyUtils to get wallpapers
                Document doc = Jsoup.connect(MyUtils.NicheLink)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .followRedirects(true)
                        .timeout(15000)
                        .get();
                
                // Clear existing data first
                carouselWallpapers.clear();
                
                // Look for wallpapers in the album
                Elements wallpaperElements = doc.select("div#albumwp").select("div.wallpaper");
                
                // If no wallpapers found in albumwp, try other selectors
                if (wallpaperElements.isEmpty()) {
                    wallpaperElements = doc.select("div.wallpaper");
                }
                
                // If still no wallpapers, try looking for any image links
                if (wallpaperElements.isEmpty()) {
                    wallpaperElements = doc.select("a[href*=/wp/]");
                }
                
                for (Element element : wallpaperElements) {
                    String imgSrc = "";
                    
                    // Try different ways to get the image source
                    if (element.select("img.wimg").size() > 0) {
                        imgSrc = element.select("img.wimg").attr("src");
                    } else if (element.select("img").size() > 0) {
                        imgSrc = element.select("img").attr("src");
                    }
                    
                    if (!imgSrc.isEmpty()) {
                        // Make sure the URL is complete
                        if (!imgSrc.startsWith("http")) {
                            imgSrc = "https://wallpapercave.com" + imgSrc;
                        }
                        
                        // Add the wallpaper to the list
                        carouselWallpapers.add(new FeaturedModel(imgSrc, false));
                        
                        // Limit to 20 wallpapers for carousel to avoid too many items
                        if (carouselWallpapers.size() >= 20) {
                            break;
                        }
                    }
                }
                
                // If we still don't have enough wallpapers, try a fallback source
                if (carouselWallpapers.size() < 5) {
                    Document doc2 = Jsoup.connect("https://wallpapercave.com/popular-wallpapers")
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                            .followRedirects(true)
                            .timeout(15000)
                            .get();
                    
                    Elements elements2 = doc2.select("div.wallpaper");
                    
                    for (Element element : elements2) {
                        String imgSrc = element.select("img.wimg").attr("src");
                        if (!imgSrc.isEmpty()) {
                            if (!imgSrc.startsWith("http")) {
                                imgSrc = "https://wallpapercave.com" + imgSrc;
                            }
                            carouselWallpapers.add(new FeaturedModel(imgSrc, false));
                            
                            if (carouselWallpapers.size() >= 20) {
                                break;
                            }
                        }
                    }
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                // Add some fallback wallpapers if scraping fails
                carouselWallpapers.add(new FeaturedModel("https://wallpapercave.com/wp/wp1234567.jpg", false));
                carouselWallpapers.add(new FeaturedModel("https://wallpapercave.com/wp/wp1234568.jpg", false));
                carouselWallpapers.add(new FeaturedModel("https://wallpapercave.com/wp/wp1234569.jpg", false));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (isAdded()) {
                // Update the adapter with new data
                if (carouselAdapter != null) {
                    carouselAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void loadCategoriesData() {
        new getCategoriesData().execute();
    }

    public class getCategoriesData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://wallpapercave.com/categories").userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36").get();
                Elements elements = doc.select("div#content").select("ul#catsinbox").select("li");

                for (Element element : elements) {
                    String categoryName = element.select("a").text();
                    String categoryUrl = element.select("a").attr("href");
                    if (!categoryName.contains("Religion") && !categoryName.contains("Fortnite")) {
                        allCategories.add(new CategoryModel(categoryName, categoryUrl));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (isAdded()) {
                filteredCategories.addAll(allCategories);
                categoryAdapter = new CategoryAdapter(requireActivity(), filteredCategories);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                binding.categoriesRecycler.setLayoutManager(gridLayoutManager);
                binding.categoriesRecycler.setAdapter(categoryAdapter);
                binding.categoriesRecycler.setVisibility(View.VISIBLE);
                binding.categoriesShimmer.setVisibility(View.GONE);
                binding.categoriesShimmer.stopShimmer();
            } else {
                Log.e("TAG", "onPostExecute: " + "Fragment not attached");
            }
        }
    }
} 