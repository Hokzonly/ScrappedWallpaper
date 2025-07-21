package com.cinnamoroll.wallpaperlivewallpaperauth2.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.cinnamoroll.wallpaperlivewallpaperauth2.config.AppInterstitialListenerManager;
import com.cinnamoroll.wallpaperlivewallpaperauth2.config.AppManager;
import com.cinnamoroll.wallpaperlivewallpaperauth2.databinding.ItemHomeFeaturedBinding;
import com.cinnamoroll.wallpaperlivewallpaperauth2.databinding.NativeAdapterBinding;
import com.cinnamoroll.wallpaperlivewallpaperauth2.models.FeaturedModel;
import com.cinnamoroll.wallpaperlivewallpaperauth2.ui.WallpaperApplyActivity;

import java.util.List;

public class FeaturedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity context;
    List<FeaturedModel> featuredModels;
    public static int ITEM_VIEW = 0;
    public static int AD_VIEW = 1;
    public static int ITEM_FEED_COUNT = 15; // Increased from 7 to 15 to show fewer native ads
    public FeaturedAdapter(Activity context, List<FeaturedModel> featuredModels) {
        this.context = context;
        this.featuredModels = featuredModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_VIEW) {
            LayoutInflater inflater = LayoutInflater.from(context);
            ItemHomeFeaturedBinding binding = ItemHomeFeaturedBinding.inflate(inflater, parent, false);
            return new FeaturedViewHolder(binding.getRoot());
        } else if (viewType == AD_VIEW) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            NativeAdapterBinding binding2 = NativeAdapterBinding.inflate(inflater, parent, false);
            return new ViewHolderAds(binding2.getRoot());
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == ITEM_VIEW) {
            int pos2 = position - (position / ITEM_FEED_COUNT) - (position >= ITEM_FEED_COUNT ? 1 : 0);
            ((FeaturedViewHolder) holder).setData(featuredModels.get(pos2));
        } else if (holder.getItemViewType() == AD_VIEW) {
            AppManager.ShowAppNative(context, ((FeaturedAdapter.ViewHolderAds) holder).binding.nativeAdContainer);


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
        int wallpaperCount = featuredModels.size();
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


    public class FeaturedViewHolder extends RecyclerView.ViewHolder {
        ItemHomeFeaturedBinding binding;

        public FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemHomeFeaturedBinding.bind(itemView);
        }

        void setData(FeaturedModel model) {
            Glide.with(context).load(model.getWallpaperImage()).into(binding.featuredImage);
            binding.getRoot().setOnClickListener(v -> {
                AppManager.ShowInterstitial(context, new AppInterstitialListenerManager() {
                    @Override
                    public void onInterstitialClosed() {
                        context.startActivity(new Intent(context, WallpaperApplyActivity.class)
                                .putExtra("id", model.getWallpaperImage())
                                .putExtra("image", model.getWallpaperImage())
                                .putExtra("premium", model.isPremium())
                        );
                    }
                });
            });
        }
    }



}
