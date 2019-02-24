package com.sloubi.unmusic.Interface;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.sloubi.unmusic.Model.Music;

import java.util.List;

@Dao
public interface MusicDao {
    @Query("SELECT * FROM music WHERE id LIKE :mId LIMIT 1")
    Music findByMusicId(int mId);

    @Insert
    void insert(Music music);

    @Update
    int update(Music music);

    @Query("DELETE FROM music")
    void deleteAll();

    @Query("SELECT id, musicId, url, artist, title, fullTitle from music")
    List<Music> getAllMusic();
}
