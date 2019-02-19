package com.sloubi.unmusic.Repository;

import android.content.Context;
import android.os.AsyncTask;

import com.sloubi.unmusic.AppDatabase;
import com.sloubi.unmusic.Interface.MusicDao;
import com.sloubi.unmusic.Model.Music;

import java.util.List;

public class MusicRepository {
    private MusicDao mMusicDao;
    private List<Music> mAllMusic;

    public MusicRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.mMusicDao = db.musicDao();
        this.mAllMusic = mMusicDao.getAllMusic();
    }

    public List<Music> getAllMusics() {
        return this.mAllMusic;
    }

    public void insert (Music music) {
        new insertAsyncTask(this.mMusicDao).execute(music);
    }

    private static class insertAsyncTask extends AsyncTask<Music, Void, Void> {

        private MusicDao mAsyncTaskDao;

        insertAsyncTask(MusicDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Music... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
