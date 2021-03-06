package com.sloubi.unmusic.Async;

import android.os.AsyncTask;

import com.sloubi.unmusic.AppDatabase;
import com.sloubi.unmusic.Interface.MusicDao;
import com.sloubi.unmusic.Model.Music;

import java.util.List;

public class PopulateMusicDbAsync extends AsyncTask<Void, Void, Void> {
    private final MusicDao mDao;
    private final List<Music> mMusics;

    public PopulateMusicDbAsync(AppDatabase db, List<Music> musics) {
        mDao = db.musicDao();
        mMusics = musics;
    }

    @Override
    protected Void doInBackground(final Void... params) {
        List<Music> existingMusic = mDao.getAllMusic();

        for (Music music: mMusics) {
            if (!existingMusic.contains(music)) {
                mDao.insert(music);
            }
        }

        return null;
    }
}
