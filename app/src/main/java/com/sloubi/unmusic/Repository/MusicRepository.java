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

    public void update (Music music) {
        new updateAsyncTask (this.mMusicDao).execute(music);
    }

    public static class updateAsyncTask extends AsyncTask<Music, Void, Void> {

        private MusicDao mAsyncTaskDao;

        public updateAsyncTask(MusicDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Music... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }
}
