package com.Africa.Wallpapers4Knewtest.config.RoomDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.Africa.Wallpapers4Knewtest.R;
@Database(entities = {MySavedCodes.class},version = 1, exportSchema = false)
public abstract class MySavedCodesDataBse extends RoomDatabase {
    public abstract CodesDao favDao();
    private static MySavedCodesDataBse Instance;
    public static MySavedCodesDataBse getInstance(Context context){
        if (Instance==null){
           Instance = Room.databaseBuilder(context.getApplicationContext(), MySavedCodesDataBse.class,"codes"+context.getString(R.string.app_name).replace(" ","")).allowMainThreadQueries().build();
        }
        return Instance;
    }
}
