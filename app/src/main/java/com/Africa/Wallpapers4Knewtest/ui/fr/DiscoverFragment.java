package com.Africa.Wallpapers4Knewtest.ui.fr;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
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
import com.Africa.Wallpapers4Knewtest.adapters.FeaturedAdapter;
import com.Africa.Wallpapers4Knewtest.databinding.FragmentDiscoverBinding;
import com.Africa.Wallpapers4Knewtest.models.CategoryModel;
import com.Africa.Wallpapers4Knewtest.models.FeaturedModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.Africa.Wallpapers4Knewtest.MyUtils;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {
    FragmentDiscoverBinding binding;
    List<CategoryModel> allCategories = new ArrayList<>();
    List<CategoryModel> filteredCategories = new ArrayList<>();
    List<FeaturedModel> carouselWallpapers = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    FeaturedAdapter carouselAdapter;

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
                // Don't search on every character change to avoid too many requests
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
                }
                return true;
            }
            return false;
        });
    }

    private void searchWallpapers(String query) {
        // Show loading
        binding.categoriesRecycler.setVisibility(View.GONE);
        binding.categoriesShimmer.setVisibility(View.VISIBLE);
        binding.categoriesShimmer.startShimmer();

        // Clear previous results
        filteredCategories.clear();
        if (categoryAdapter != null) {
            categoryAdapter.updateCategories(filteredCategories);
        }

        // Search wallpapers like SearchActivity does
        new SearchWallpapersTask().execute(query);
    }

    private class SearchWallpapersTask extends AsyncTask<String, Void, List<CategoryModel>> {
        @Override
        protected List<CategoryModel> doInBackground(String... queries) {
            List<CategoryModel> searchResults = new ArrayList<>();
            String query = queries[0];
            
            try {
                Document document = Jsoup.connect("https://wallpapercave.com/search?q=" + query.replace(" ", "+"))
                        .userAgent("chrome")
                        .followRedirects(true)
                        .get();

                Elements elements = document.select("div#content").select("div#popular").select("a.albumthumbnail");
                for (Element element : elements) {
                    String categoryName = element.select("div.psc").select("p.title").text();
                    String categoryUrl = element.attr("href");
                    searchResults.add(new CategoryModel(categoryName, categoryUrl));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return searchResults;
        }

        @Override
        protected void onPostExecute(List<CategoryModel> results) {
            if (isAdded()) {
                binding.categoriesShimmer.stopShimmer();
                binding.categoriesShimmer.setVisibility(View.GONE);
                
                if (results.isEmpty()) {
                    // Show no results message
                    binding.categoriesRecycler.setVisibility(View.GONE);
                    // You can add a TextView to show "No results found"
                } else {
                    // Show search results
                    filteredCategories.clear();
                    filteredCategories.addAll(results);
                    binding.categoriesRecycler.setVisibility(View.VISIBLE);
                    if (categoryAdapter != null) {
                        categoryAdapter.updateCategories(filteredCategories);
                    }
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
        // Add some sample wallpapers first to show immediately
        carouselWallpapers.add(new FeaturedModel("https://wallpapercave.com/wp/wp1234567.jpg", false));
        carouselWallpapers.add(new FeaturedModel("https://wallpapercave.com/wp/wp1234568.jpg", false));
        carouselWallpapers.add(new FeaturedModel("https://wallpapercave.com/wp/wp1234569.jpg", false));
        
        // Set up the adapter immediately
        carouselAdapter = new FeaturedAdapter(getActivity(), carouselWallpapers);
        binding.carouselRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.carouselRecycler.setAdapter(carouselAdapter);
        
        // Load real wallpapers from the same source as main wallpapers
        new LoadCarouselWallpapers().execute();
    }

    public class LoadCarouselWallpapers extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(MyUtils.NicheLink).userAgent("firefox").followRedirects(false).get();
                Elements elements = doc.select("div#albumwp").select("div.wallpaper");

                for (Element element : elements) {
                    String img = element.select("img.wimg").attr("src");
                    if (carouselWallpapers.size() < 10) { // Limit to 10 wallpapers for carousel
                        carouselWallpapers.add(new FeaturedModel("https://wallpapercave.com" + img, false));
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
                // Update the existing adapter with new data
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
                Document doc = Jsoup.connect("https://wallpapercave.com/categories").userAgent("opera").get();
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