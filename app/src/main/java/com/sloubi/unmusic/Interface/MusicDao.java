package com.sloubi.unmusic.Interface;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sloubi.unmusic.Model.Music;

import java.util.List;

@Dao
public interface MusicDao {
    @Insert
    void insert(Music music);

    @Query("DELETE FROM music")
    void deleteAll();

    @Query("SELECT * from music ORDER BY fullTitle ASC")
    List<Music> getAllMusic();
}
