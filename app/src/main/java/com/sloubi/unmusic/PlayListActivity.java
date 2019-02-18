package com.sloubi.unmusic;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.sloubi.unmusic.Adapter.PlaylistAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayListActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        ListView playlist = findViewById(R.id.lv_playlist);
        List<HashMap<String, String>> musics = new ArrayList<>();

        HashMap<String, String> music = new HashMap<>();
        music.put("id", "0");
        music.put("fullTitle", "Nightcore - 7 rings  God is a woman (Switching Vocals)");

        musics.add(music);

        PlaylistAdapter adapter = new PlaylistAdapter(this.getBaseContext(), musics);

        playlist.setAdapter(adapter);
    }
}
