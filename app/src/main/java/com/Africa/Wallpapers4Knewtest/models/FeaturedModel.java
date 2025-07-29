package com.Africa.Wallpapers4Knewtest.models;

public class FeaturedModel {
    String WallpaperImage;
    boolean isPremium;

    public FeaturedModel(String wallpaperImage, boolean isPremium) {
        WallpaperImage = wallpaperImage;
        this.isPremium = isPremium;
    }

    public String getWallpaperImage() {
        return WallpaperImage;
    }

    public boolean isPremium() {
        return isPremium;
    }
}
