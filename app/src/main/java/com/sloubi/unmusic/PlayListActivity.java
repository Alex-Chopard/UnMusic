package com.sloubi.unmusic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.sloubi.unmusic.Adapter.PlaylistAdapter;
import com.sloubi.unmusic.Interface.OnMusicListDownloadListener;
import com.sloubi.unmusic.Model.Music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayListActivity extends AppCompatActivity implements OnMusicListDownloadListener {

    private ListView playlist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlist = findViewById(R.id.lv_playlist);

        // Get all music.
        Music.list(this, this);
    }

    @Override
    public void onDownloadComplete(List<Music> data) {
        List<HashMap<String, String>> musics = new ArrayList<>();

        for (Music music: data) {
            HashMap<String, String> musicMap = new HashMap<>();
            musicMap.put("id", music.getId());
            musicMap.put("fullTitle", music.getFullTitle());

            musics.add(musicMap);
        }

        PlaylistAdapter adapter = new PlaylistAdapter(this.getBaseContext(), musics);
        playlist.setAdapter(adapter);
    }

    @Override
    public void onDownloadError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
