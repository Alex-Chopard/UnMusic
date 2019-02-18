package com.sloubi.unmusic.Adapter;

import android.app.Service;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sloubi.unmusic.R;

import java.util.HashMap;
import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<HashMap<String, String>> {

    private List<HashMap<String, String>> musics;
    private Context context;

    public PlaylistAdapter(Context context, List<HashMap<String, String>> musics){
        super(context, R.layout.activity_playlist, musics);

        this.context = context;
        this.musics = musics;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_playlist, parent, false);

        HashMap<String, String> music = this.musics.get(position);

        rowView.setId(Integer.parseInt(music.get("id")));

        TextView musicTitle = rowView.findViewById(R.id.tv_music_title);
        musicTitle.setText(music.get("fullTitle"));

        return rowView;
    }
}
