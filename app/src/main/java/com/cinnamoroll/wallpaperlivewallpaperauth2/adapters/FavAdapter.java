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
import com.cinnamoroll.wallpaperlivewallpaperauth2.config.RoomDatabase.MyFavs;
import com.cinnamoroll.wallpaperlivewallpaperauth2.databinding.ItemHomeFeaturedBinding;
import com.cinnamoroll.wallpaperlivewallpaperauth2.ui.WallpaperApplyActivity;

import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FeaturedViewHolder> {

    Activity context;
    public List<MyFavs> featuredModels;

    public FavAdapter(Activity context, List<MyFavs> featuredModels) {
        this.context = context;
        this.featuredModels = featuredModels;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemHomeFeaturedBinding binding = ItemHomeFeaturedBinding.inflate(inflater, parent, false);
        return new FeaturedViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        holder.setData(featuredModels.get(position));
    }

    @Override
    public int getItemCount() {
        return featuredModels.size();
    }

    public class FeaturedViewHolder extends RecyclerView.ViewHolder {
        ItemHomeFeaturedBinding binding;

        public FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemHomeFeaturedBinding.bind(itemView);
        }

        void setData(MyFavs model) {
            Glide.with(context).load(model.getWallpaper().replace("/pwp/", "/wp/").replace("/fwp/", "/wp/").replace("/fuwp/", "/uwp/").replace("/dwp2x/", "/wp/")).into(binding.featuredImage);
            binding.getRoot().setOnClickListener(v -> {
                // Direct navigation without ads
                context.startActivity(new Intent(context, WallpaperApplyActivity.class)
                        .putExtra("id", model.getId())
                        .putExtra("image", model.getWallpaper())
                        .putExtra("premium", model.isPremium())
                );
            });
        }
    }

}
