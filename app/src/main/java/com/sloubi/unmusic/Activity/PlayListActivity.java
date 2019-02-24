package com.sloubi.unmusic.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sloubi.unmusic.Adapter.PlaylistAdapter;
import com.sloubi.unmusic.AppDatabase;
import com.sloubi.unmusic.Interface.OnMusicListDownloadListener;
import com.sloubi.unmusic.Model.Music;
import com.sloubi.unmusic.R;
import com.sloubi.unmusic.Services.SensorService;

import java.util.List;

public class PlayListActivity extends AppCompatActivity implements OnMusicListDownloadListener, AdapterView.OnItemClickListener {

    private ListView mPlaylist;
    private LinearLayout mLoader;
    private List<Music> mMusics;
    private Intent sensorService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        mPlaylist = findViewById(R.id.lv_playlist);
        mLoader = findViewById(R.id.ll_loader);

        mPlaylist.setOnItemClickListener(this);
        // Display loader.
        mLoader.setVisibility(View.VISIBLE);

        //Init RoomDatabase
        AppDatabase.getInstance(this);

        // Get all music.
        Music.list(this, this);

        // Start sensor service for manage Accelerometer and Location.
        this.sensorService = new Intent(this, SensorService.class);
        startService(this.sensorService);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop service
        stopService(this.sensorService);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int vId = view.getId();
        Music music = null;

        for (Music m: mMusics) {
            if (m.getId() == String.valueOf(vId)) {
                music = m;
            }
        }

        if (music != null) {
            // Start intent for play an music.
            Intent intent = new Intent(this, MusicActivity.class);
            intent.putExtra("id", Integer.valueOf(music.getId()));
            intent.putExtra("fullTitle", music.getFullTitle());
            intent.putExtra("fileUrl", music.getFilePath());

            startActivity(intent);
        }
    }

    @Override
    public void onDownloadComplete(List<Music> data) {
        // Do nothing if the list is empty
        if (!data.isEmpty()) {
            mMusics = data;

            final PlaylistAdapter adapter = new PlaylistAdapter(this, this.mMusics);

            // Use this Runnable for be able to modify the UI.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoader.setVisibility(View.GONE);
                    mPlaylist.setAdapter(adapter);
                }
            });
        }
    }

    @Override
    public void onDownloadError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
