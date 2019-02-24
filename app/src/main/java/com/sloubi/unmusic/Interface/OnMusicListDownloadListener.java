package com.sloubi.unmusic.Interface;

import com.sloubi.unmusic.Model.Music;

import java.util.List;

public interface OnMusicListDownloadListener {
    void onDownloadComplete(List<Music> musics);
    void onDownloadError(String error);
}
