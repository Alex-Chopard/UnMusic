package com.sloubi.unmusic;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.sloubi.unmusic.Interface.MusicDao;
import com.sloubi.unmusic.Model.Music;

@Database(entities = {
        Music.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MusicDao musicDao();

    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "unMusic")
                            .build();
                }
            }
        }
        return instance;
    }
}