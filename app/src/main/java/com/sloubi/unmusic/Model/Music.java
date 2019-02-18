package com.sloubi.unmusic.Model;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sloubi.unmusic.CallApi;
import com.sloubi.unmusic.Interface.OnMusicGetListener;
import com.sloubi.unmusic.Interface.OnMusicListDownloadListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Music {
    private String id;
    private String musicId;
    private String url;
    private String artist;
    private String title;
    private String fullTitle;
    private String data;

    public Music(String id, String musicId, String url, String artist, String title, String fullTitle) {
        this.id = id;
        this.musicId = musicId;
        this.url = url;
        this.artist = artist;
        this.title = title;
        this.fullTitle = fullTitle;
    }

    public static void list(Context context, final OnMusicListDownloadListener listener) {
        // TODO: First load stored musics.
        CallApi.get(context, "/music", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONArray list = response.getJSONArray("list");
                    List<Music> musics = new ArrayList<>();

                    // Build all music.
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject row = list.getJSONObject(i);

                        Music music = new Music(
                                row.getString("id"),
                                row.getString("musicId"),
                                row.getString("url"),
                                row.getString("artist"),
                                row.getString("title"),
                                row.getString("fullTitle"));

                        musics.add(music);
                    }

                    // TODO: Save loaded music in DB.

                    listener.onDownloadComplete(musics);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Return the error message.
                listener.onDownloadError(errorResponse.toString());
            }
        });
    }

    public static void get (int id, Context context, final OnMusicGetListener listener) {
        CallApi.get(context, "/music/" + id, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    Music music = new Music(
                        response.getString("id"),
                        response.getString("musicId"),
                        response.getString("url"),
                        response.getString("artist"),
                        response.getString("title"),
                        response.getString("fullTitle"));
                    Log.i("5555555555555555", "lksjdfuh");
                    StringBuilder base64 = new StringBuilder();
                    JSONArray jsonArray = response.getJSONObject("data").getJSONArray("data");

                    for(int i = 0; i < jsonArray.length(); i++) {
                        int character = (int) jsonArray.get(i);
                        base64.append((char) character);
                    }

                    music.setData(base64.toString());
                    Log.i("0000000000000000000000", "" + music.getData().length());
                    // TODO: Save loaded music in DB.

                    listener.onDownloadComplete(music);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Return the error message.
                listener.onDownloadError(errorResponse.toString());
            }
        });
    }

    /*** ACCESSOR ***/

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
