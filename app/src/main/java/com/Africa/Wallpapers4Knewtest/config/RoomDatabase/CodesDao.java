package com.Africa.Wallpapers4Knewtest.config.RoomDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CodesDao {

    @Query("select * from mysavedcodes")
    List<MySavedCodes> getAllFavs();

    @Insert
    void insertUser(MySavedCodes mySavedCodes);

    @Query("SELECT EXISTS (SELECT 1 FROM mysavedcodes WHERE code=:id)")
    int isSaved(String id);




}
