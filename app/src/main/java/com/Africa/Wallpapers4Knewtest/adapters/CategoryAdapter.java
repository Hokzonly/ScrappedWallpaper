package com.Africa.Wallpapers4Knewtest.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Africa.Wallpapers4Knewtest.config.AppManager;
import com.Africa.Wallpapers4Knewtest.databinding.ItemCategoryBinding;
import com.Africa.Wallpapers4Knewtest.databinding.NativeAdapterBinding;
import com.Africa.Wallpapers4Knewtest.models.CategoryModel;
import com.Africa.Wallpapers4Knewtest.ui.CategoriesActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Activity context;
    List<CategoryModel> categoryModels;
    public static int ITEM_VIEW = 0;
    public static int AD_VIEW = 1;
    public static int ITEM_FEED_COUNT = 15; // Increased from 7 to 15 to show fewer native ads
    public CategoryAdapter(Activity context, List<CategoryModel> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }

    public void updateCategories(List<CategoryModel> newCategories) {
        this.categoryModels.clear();
        this.categoryModels.addAll(newCategories);
        notifyDataSetChanged();
    }

    public  class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;
        public CategoryViewHolder(View view) {
            super(view);
            binding = ItemCategoryBinding.bind(view);
        }

        void bind(CategoryModel categoryModel) {
            binding.categoryName.setText(categoryModel.getName());
            // Set category count (you can modify this based on your data)
            binding.categoryCount.setText("50+ wallpapers");
            
            binding.getRoot().setOnClickListener(v -> {
                // Direct navigation without ads
                context.startActivity(new Intent(context, CategoriesActivity.class)
                        .putExtra("url", categoryModel.getUrl())
                        .putExtra("name", categoryModel.getName()));
            });
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_VIEW) {
            ItemCategoryBinding binding = ItemCategoryBinding.inflate(layoutInflater, parent, false);
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
           AppManager.ShowAppNative(context, ((ViewHolderAds) holder).binding.nativeAdContainer);
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


}
