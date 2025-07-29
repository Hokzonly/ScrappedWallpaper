package com.Africa.Wallpapers4Knewtest.config.RoomDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MySavedCodes {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Code")
    public String code;

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
    }
}
