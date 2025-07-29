package com.Africa.Wallpapers4Knewtest.ui;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.Africa.Wallpapers4Knewtest.adapters.SearchAdapter;
import com.Africa.Wallpapers4Knewtest.config.AppManager;
import com.Africa.Wallpapers4Knewtest.databinding.ActivityWallpapersBinding;
import com.Africa.Wallpapers4Knewtest.models.FeaturedModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WallpapersActivity extends AppCompatActivity {

    ActivityWallpapersBinding binding;
    List<FeaturedModel> featuredModels = new ArrayList<>();
    SearchAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWallpapersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String name = getIntent().getStringExtra("name");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.CategoryName.setText(Optional.ofNullable(name).orElse(""));
        }else {
            binding.CategoryName.setText(name);
        }
        try {
            AppManager.ShowAppBanner(this,binding.Banner);
            new getCategoriesListWallpapersData().execute();
        }catch (Exception ignored){
        }
        binding.Back.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAndRemoveTask();
            }
        });
    }
    public class getCategoriesListWallpapersData extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://wallpapercave.com"+getIntent().getStringExtra("url")).userAgent("opera").get();
                Elements elements = doc.select("div#albumwp").select("div.wallpaper");
                for (Element element : elements) {
                    String img = element.select("img.wimg").attr("src");
                    if (elements.indexOf(element)> 5) {
                        featuredModels.add(new FeaturedModel("https://wallpapercave.com"+img,true));
                    }else {
                        featuredModels.add(new FeaturedModel("https://wallpapercave.com"+img,false));
                    }
                }




            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (featuredModels.size()>0) {
                adapter = new SearchAdapter( WallpapersActivity.this,featuredModels);
                binding.CategoryList.setAdapter(adapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(WallpapersActivity.this, 3);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int viewType = Objects.requireNonNull(binding.CategoryList.getAdapter()).getItemViewType(position);
                        return viewType == SearchAdapter.AD_VIEW ? 3 : 1;                    }
                });
                binding.CategoryList.setLayoutManager(gridLayoutManager);
                binding.CategoryList.setHasFixedSize(true);
                binding.Shimmer.stopShimmer();
                binding.Shimmer.setVisibility(View.GONE);
            }

        }
    }

}