package com.sloubi.unmusic.Interface;

import com.sloubi.unmusic.Model.Music;

import java.io.IOException;
import java.util.List;

public interface OnMusicGetListener {
    void onDownloadComplete(Music music) throws IOException;
    void onDownloadError(String error);
}
