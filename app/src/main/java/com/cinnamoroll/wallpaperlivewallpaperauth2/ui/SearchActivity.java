package com.cinnamoroll.wallpaperlivewallpaperauth2.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cinnamoroll.wallpaperlivewallpaperauth2.adapters.CategoryListAdapter;
import com.cinnamoroll.wallpaperlivewallpaperauth2.adapters.FeaturedAdapter;
import com.cinnamoroll.wallpaperlivewallpaperauth2.databinding.FragmentSearchBinding;
import com.cinnamoroll.wallpaperlivewallpaperauth2.models.CategoryListModel;

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

public class SearchActivity extends AppCompatActivity {

    FragmentSearchBinding binding;
    CategoryListAdapter adapter;
    List<CategoryListModel> categoryListModels = new ArrayList<>();
    boolean isCrashed = false;
    boolean isSuccessful = false;
    String s = "cock, deepthroat, dick, cumshot, tasty, baby, wet, fuck, sperm, jerk off, naked, ass, tits, fingering, masturbate, bitch, blowjob, prostitute, shit, bullshit, dumbass, dickhead, pussy, piss, asshole, boobs, butt, booty, dildo, erection, foreskin, gag, handjob, licking, nude, penis, porn, vibrator, viagra, virgin, vagina, vulva, wet dream, threesome, orgy, bdsm, hickey, condom, sexting, squirt, testicles, anal, bareback, bukkake, creampie, stripper, strap-on, missionary, make out, clitoris, cock ring, sugar daddy, cowgirl, reach-around, doggy style, fleshlight, contraceptive, makeup sex, lingerie, butt plug, moan, milf, wank, oral, sucking, kiss, dirty talk, straddle, blindfold, bondage, orgasm, french kiss, scissoring, hard, deeper, don't stop, slut, cumming, tasty, dirty, ode, men's milk, pound, jerk, prick, cunt, bastard, faggot, anal, anus,lingerie,bikini,pussy,anal,ass,skirt,sex,sexy,sexing,sixing,sexting,sexe";
    String[] array = s.split(",");
    List<String> list = new ArrayList<>(Arrays.asList(array));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.Back.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        try {
            binding.SearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (list.contains(query)) {
                        binding.Loading.setVisibility(View.GONE);
                        binding.RecyclerView.setVisibility(View.GONE);
                        binding.NoResults.setVisibility(View.VISIBLE);
                    } else {
                        if (adapter != null) {
                            adapter.clear();
                        }
                        binding.Loading.setVisibility(View.VISIBLE);
                        binding.RecyclerView.setVisibility(View.GONE);
                        binding.NoResults.setVisibility(View.GONE);
                        binding.SearchView.clearFocus();
                        String processedQuery = query;
                        if (!processedQuery.isEmpty() && processedQuery.charAt(processedQuery.length() - 1) == ' ') {
                            processedQuery = processedQuery.substring(0, processedQuery.length() - 1);
                        }
                        getCategoriesListData(SearchActivity.this, processedQuery);
                    }

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCategoriesListData(Activity activity, String query) {
        List<CategoryListModel> newData = new ArrayList<>();
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (activity == null) {
                return;
            }
            try {

                Document document = Jsoup.connect("https://wallpapercave.com/search?q=" + query.replace(" ", "+"))
                        .userAgent("chrome")
                        .followRedirects(true)
                        .get();

                Elements elements = document.select("div#content").select("div#popular").select("a.albumthumbnail");
                for (Element element : elements) {
                    String categoryName = element.select("div.psc").select("p.title").text();
                    String categoryDescription = element.select("div.psc").select("p.number").text();
                    String categoryImage = element.select("div.albumphoto").select("img.thumbnail").attr("src");
                    String categoryUrl = element.attr("href");
                    newData.add(new CategoryListModel(categoryName, categoryDescription, categoryImage, categoryUrl));
                }
                isSuccessful = true;


                activity.runOnUiThread(() -> {
                    // Update UI components on the main thread
                    if (isSuccessful) {
                        if (isCrashed) {
                            binding.RecyclerView.setVisibility(View.GONE);
                            binding.Loading.setVisibility(View.GONE);
                            binding.NoResults.setVisibility(View.VISIBLE);
                        } else {
                            if (newData.size()==0){
                                binding.RecyclerView.setVisibility(View.GONE);
                                binding.Loading.setVisibility(View.GONE);
                                binding.NoResults.setVisibility(View.VISIBLE);
                            }else {
                                binding.RecyclerView.setVisibility(View.VISIBLE);
                                binding.Loading.setVisibility(View.GONE);
                                binding.NoResults.setVisibility(View.GONE);
                                categoryListModels.addAll(newData);
                                adapter = new CategoryListAdapter(SearchActivity.this, categoryListModels);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, 2);
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        int viewType = Objects.requireNonNull(binding.RecyclerView.getAdapter()).getItemViewType(position);
                                        return viewType == FeaturedAdapter.AD_VIEW ? 2 : 1;
                                    }
                                });
                                binding.RecyclerView.setLayoutManager(gridLayoutManager);
                                binding.RecyclerView.setAdapter(adapter);
                            }

                        }



                    } else {
                        isCrashed = true;
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                isCrashed = true;
                isSuccessful = false;
            }
        });
    }
}