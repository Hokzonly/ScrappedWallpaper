package com.Africa.Wallpapers4Knewtest.config.RoomDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavDao {

    @Query("select * from myfavs")
    List<MyFavs> getAllFavs();

    @Insert
    void insertUser(MyFavs myFavs);

    @Query("SELECT EXISTS (SELECT 1 FROM myfavs WHERE id=:id)")
    int isFavorite(String id);

    @Delete
    void deleteUser(MyFavs  myFavs);


}
