package com.sloubi.unmusic.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sloubi.unmusic.AppDatabase;
import com.sloubi.unmusic.Async.PopulateMusicDbAsync;
import com.sloubi.unmusic.CallApi;
import com.sloubi.unmusic.Interface.MusicDao;
import com.sloubi.unmusic.Interface.OnMusicGetListener;
import com.sloubi.unmusic.Interface.OnMusicListDownloadListener;
import com.sloubi.unmusic.Repository.MusicRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

@Entity(
        tableName = "music",
        indices = { @Index(value = { "musicId" }, unique = true) }
)
public class Music {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "musicId")
    private String musicId;

    @NonNull
    @ColumnInfo(name = "url")
    private String url;

    @NonNull
    @ColumnInfo(name = "artist")
    private String artist;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "fullTitle")
    private String fullTitle;

    @ColumnInfo(name = "filePath")
    private String filePath;

    public Music(String id, String musicId, String url, String artist, String title, String fullTitle) {
        this.id = id;
        this.musicId = musicId;
        this.url = url;
        this.artist = artist;
        this.title = title;
        this.fullTitle = fullTitle;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Music){
            Music toCompare = (Music) o;
            return this.id.equals(toCompare.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public static void list(final Context context, final OnMusicListDownloadListener listener) {
        // AsyncTask for call the DB.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // Load stored music.
                MusicRepository repository = new MusicRepository(context);
                List<Music> musics = repository.getAllMusics();

                // Return stored music
                listener.onDownloadComplete(musics);
                return null;
            }
        }.execute();

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

                    listener.onDownloadComplete(musics);

                    // Insert music in DB.
                    new PopulateMusicDbAsync(AppDatabase.getInstance(context), musics).execute();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    // Return the error message.
                    listener.onDownloadError(errorResponse.toString());
                }
            }
        });
    }

    public static void get (final int id, final Context context, final OnMusicGetListener listener) {
        final MusicDao mMusicDao = AppDatabase.getInstance(context).musicDao();


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Music music = mMusicDao.findByMusicId(id);
                    if(music != null && music.getFilePath() != null && music.getFilePath().length() > 0) {
                        listener.onDownloadComplete(music);
                    } else {
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

                                    JSONArray jsonArray = response.getJSONObject("data").getJSONArray("data");
                                    int length = jsonArray.length();
                                    byte[] byteData = new byte[length];

                                    // Parse JSONArray to array of byte.
                                    for(int i = 0; i < length; i++) {
                                        byteData[i] = (byte) ((jsonArray.getInt(i)) & 0xFF);
                                    }

                                    String filename = music.getFullTitle() + ".mp3";

                                    FileOutputStream outputStream;

                                    outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                                    outputStream.write(byteData);
                                    outputStream.close();

                                    music.setFilePath(filename);

                                    // Save in db.
                                    new MusicRepository.updateAsyncTask(AppDatabase.getInstance(context).musicDao())
                                            .execute(music);

                                    listener.onDownloadComplete(music);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                if (errorResponse != null) {
                                    // Return the error message.
                                    listener.onDownloadError(errorResponse.toString());
                                }
                            }


                            @Override
                            public boolean getUseSynchronousMode() {
                                return false;
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
