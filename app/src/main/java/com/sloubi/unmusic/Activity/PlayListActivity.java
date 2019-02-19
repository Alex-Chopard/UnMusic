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
import com.sloubi.unmusic.Async.PopulateMusicDbAsync;
import com.sloubi.unmusic.Interface.MusicDao;
import com.sloubi.unmusic.Interface.OnMusicListDownloadListener;
import com.sloubi.unmusic.Model.Music;
import com.sloubi.unmusic.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayListActivity extends AppCompatActivity implements OnMusicListDownloadListener, AdapterView.OnItemClickListener {

    private ListView playlist;
    private LinearLayout loader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlist = findViewById(R.id.lv_playlist);
        loader = findViewById(R.id.ll_loader);

        playlist.setOnItemClickListener(this);


        //Init RoomDatabase
        AppDatabase.getInstance(this);

        // Display loader.
        this.loader.setVisibility(View.VISIBLE);
        // Get all music.
        Music.list(this, this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int vId = view.getId();

        // Start intent for play an music.
        Intent intent = new Intent(this, MusicActivity.class);
        intent.putExtra("id", vId);
        startActivity(intent);
    }

    @Override
    public void onDownloadComplete(List<Music> data) {
        // Do nothing if the list is empty
        if (!data.isEmpty()) {
            List<HashMap<String, String>> musics = new ArrayList<>();

            for (Music music: data) {
                HashMap<String, String> musicMap = new HashMap<>();
                musicMap.put("id", music.getId());
                musicMap.put("fullTitle", music.getFullTitle());

                musics.add(musicMap);
            }

            PlaylistAdapter adapter = new PlaylistAdapter(this.getBaseContext(), musics);
            this.loader.setVisibility(View.GONE);
            this.playlist.setAdapter(adapter);
        }
    }

    @Override
    public void onDownloadError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
