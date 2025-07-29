package com.Africa.Wallpapers4Knewtest.config.RoomDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyFavs {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Id")
    public String id;

    @ColumnInfo(name = "Wallpaper")
    public String Wallpaper;

    @ColumnInfo(name = "isPremium")
    public boolean isPremium;

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getWallpaper() {
        return Wallpaper;
    }


    public void setWallpaper(String wallpaper) {
        Wallpaper = wallpaper;
    }

}
