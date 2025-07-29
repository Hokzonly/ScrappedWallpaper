package com.Africa.Wallpapers4Knewtest.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import com.Africa.Wallpapers4Knewtest.config.AppInterstitialListenerManager;
import com.Africa.Wallpapers4Knewtest.config.AppManager;
import com.Africa.Wallpapers4Knewtest.databinding.ItemCategoryListBinding;
import com.Africa.Wallpapers4Knewtest.databinding.NativeAdapterBinding;
import com.Africa.Wallpapers4Knewtest.models.CategoryListModel;
import com.Africa.Wallpapers4Knewtest.ui.WallpapersActivity;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Activity context;
    List<CategoryListModel> categoryModels;
    public static int ITEM_VIEW = 0;
    public static int AD_VIEW = 1;
    public static int ITEM_FEED_COUNT = 15; // Increased from 7 to 15 to show fewer native ads
    public CategoryListAdapter(Activity context, List<CategoryListModel> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }

    public  class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryListBinding binding;
        public CategoryViewHolder(View view) {
            super(view);
            binding = ItemCategoryListBinding.bind(view);
        }

        void bind(CategoryListModel categoryModel) {
            binding.CategoryName.setText(categoryModel.getName());
            binding.CategoryDescription.setText(categoryModel.getDescription());
            Glide.with(context).load(categoryModel.getImage())
                    .circleCrop()
                    .into(binding.CategoryImage);
            binding.getRoot().setOnClickListener(v -> {
                AppManager.ShowInterstitial(context, new AppInterstitialListenerManager() {
                    @Override
                    public void onInterstitialClosed() {
                        context.startActivity(new Intent(context, WallpapersActivity.class)
                                .putExtra("url", categoryModel.getUrl())
                                .putExtra("name", categoryModel.getName()));
                    }
                });
            });

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_VIEW) {
            ItemCategoryListBinding binding = ItemCategoryListBinding.inflate(layoutInflater, parent, false);
            return new CategoryViewHolder(binding.getRoot());
        } else if (viewType == AD_VIEW) {
            NativeAdapterBinding binding2 = NativeAdapterBinding.inflate(layoutInflater, parent, false);
            return new ViewHolderAds(binding2.getRoot());
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == ITEM_VIEW) {
            int pos2 = position - (position / ITEM_FEED_COUNT) - (position >= ITEM_FEED_COUNT ? 1 : 0);
            ((CategoryViewHolder) holder).bind(categoryModels.get(pos2));

        } else if (holder.getItemViewType() == AD_VIEW) {
            AppManager.ShowAppNative(context, ((CategoryListAdapter.ViewHolderAds) holder).binding.nativeAdContainer);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if ((position + 1) % ITEM_FEED_COUNT == 0) {
            return AD_VIEW;
        }
        return ITEM_VIEW;
    }

    @Override
    public int getItemCount() {
        int wallpaperCount = categoryModels.size();
        int adCount = wallpaperCount / ITEM_FEED_COUNT; // Calculate number of ads
        int remainingItems = wallpaperCount % ITEM_FEED_COUNT; // Calculate remaining items after ads

        // If there are remaining items and it's not the last set of items, add one more ad
        if (remainingItems > 0 && wallpaperCount > ITEM_FEED_COUNT) {
            adCount++;
        }

        // Total count includes both items and ads
        return wallpaperCount + adCount;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }
    public class ViewHolderAds extends RecyclerView.ViewHolder {
        NativeAdapterBinding binding;

        public ViewHolderAds(@NonNull View itemView) {
            super(itemView);
            binding = NativeAdapterBinding.bind(itemView);
        }


    }
    public void clear() {
        categoryModels.clear();
        notifyDataSetChanged();
    }



}
