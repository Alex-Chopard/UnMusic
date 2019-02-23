package com.sloubi.unmusic.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sloubi.unmusic.Model.Music;
import com.sloubi.unmusic.R;

import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Music> {

    private List<Music> mMusics;
    private Context mContext;

    public PlaylistAdapter(Context context, List<Music> musics){
        super(context, R.layout.activity_playlist, musics);

        this.mContext = context;
        this.mMusics = musics;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_playlist, parent, false);
        TextView musicTitle = rowView.findViewById(R.id.tv_music_title);

        Music music = this.mMusics.get(position);

        rowView.setId(Integer.parseInt(music.getId()));
        musicTitle.setText(music.getFullTitle());

        return rowView;
    }
}
