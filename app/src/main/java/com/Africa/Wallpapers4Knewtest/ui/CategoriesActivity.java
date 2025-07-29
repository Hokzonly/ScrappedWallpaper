package com.Africa.Wallpapers4Knewtest.ui;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.Africa.Wallpapers4Knewtest.adapters.CategoryListAdapter;
import com.Africa.Wallpapers4Knewtest.adapters.FeaturedAdapter;
import com.Africa.Wallpapers4Knewtest.config.AppManager;
import com.Africa.Wallpapers4Knewtest.databinding.ActivityCategoriesBinding;
import com.Africa.Wallpapers4Knewtest.models.CategoryListModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CategoriesActivity extends AppCompatActivity {

    ActivityCategoriesBinding binding;
    List<CategoryListModel> categoryModels = new ArrayList<>();
    CategoryListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String name = getIntent().getStringExtra("name");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.CategoryName.setText(Optional.ofNullable(name).orElse(""));
        }else {
            binding.CategoryName.setText(name);
        }
        try {
            AppManager.ShowAppBanner(this,binding.Banner);
            new getCategoriesListData().execute();

        }catch (Exception ignored){
        }

        binding.Back.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

    }

    public class getCategoriesListData extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://wallpapercave.com"+getIntent().getStringExtra("url")).userAgent("opera").get();
                Elements elements = doc.select("div#content").select("div#popular").select("a.albumthumbnail");

                for (Element element : elements) {
                    String categoryName = element.select("div.psc").select("p.title").text();
                    String categoryDescription = element.select("div.psc").select("p.number").text();
                    String categoryImage = element.select("div.albumphoto").select("img.thumbnail").attr("src");
                    String categoryUrl = element.attr("href");
                    categoryModels.add(new CategoryListModel(categoryName,categoryDescription,categoryImage, categoryUrl));
                }




            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (categoryModels.size()>0) {
                adapter = new CategoryListAdapter(CategoriesActivity.this, categoryModels);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(CategoriesActivity.this, 2);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int viewType = Objects.requireNonNull(binding.CategoryList.getAdapter()).getItemViewType(position);
                        return viewType == FeaturedAdapter.AD_VIEW ? 2 : 1;
                    }
                });
                binding.CategoryList.setLayoutManager(gridLayoutManager);
                binding.CategoryList.setAdapter(adapter);
                binding.Shimmer.stopShimmer();
                binding.Shimmer.setVisibility(View.GONE);
            }

        }
    }

}