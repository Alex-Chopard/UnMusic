package com.sloubi.unmusic.Activity;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sloubi.unmusic.EventBus.AccelerometerSensorEvent;
import com.sloubi.unmusic.EventBus.NewLocationEvent;
import com.sloubi.unmusic.Interface.OnMusicGetListener;
import com.sloubi.unmusic.Model.Music;
import com.sloubi.unmusic.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, OnMusicGetListener {
    private final int ACCELEROMETER_NONE = 0;
    private final int ACCELEROMETER_TIME = 1;
    private final int ACCELEROMETER_VOLUME = 2;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler mHandler = new Handler();
    private SeekBar progress;
    private SeekBar volume;
    private ImageView play;
    private AVLoadingIndicatorView loader;
    private TextView title;
    private int accelerometerType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // Get the music id.
        int id = getIntent().getIntExtra("id", -1);
        String fullTitle = getIntent().getStringExtra("fullTitle");
        String fileUrl = getIntent().getStringExtra("fileUrl");

        // Get Views
        this.play = findViewById(R.id.iv_play);
        this.progress = findViewById(R.id.sb_avancement);
        this.volume = findViewById(R.id.sb_volume);
        this.loader = findViewById(R.id.liv_loader);
        this.title = findViewById(R.id.tv_title);
        Button btn_timming = findViewById(R.id.btn_timming);
        Button btn_volume = findViewById(R.id.btn_volume);

        // If the music is already loaded.
        if (fileUrl != null && fileUrl.length() > 0) {
            this.initMediaPlayer(fileUrl);
        } else {
            // Start to load music data.
            Music.get(id, this, this);
        }

        // Set listeners.
        this.play.setOnClickListener(this);
        btn_timming.setOnClickListener(this);
        btn_volume.setOnClickListener(this);

        this.loader.setVisibility(View.VISIBLE);
        this.play.setVisibility(View.GONE);

        this.title.setText(fullTitle);

        // Enabled user directly change SeekBar progress.
        this.progress.setEnabled(false);
        this.volume.setEnabled(false);

        updateSeekBarProgress();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();

        switch (vId) {
            case R.id.iv_play:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.ic_pause_black_24dp);
                }

                break;
            case R.id.btn_timming:
                manageAccelerometer(ACCELEROMETER_TIME);
                break;
            case R.id.btn_volume:
                manageAccelerometer(ACCELEROMETER_VOLUME);
                break;
        }
    }

    private void manageAccelerometer(int type) {
        if (this.accelerometerType == ACCELEROMETER_NONE) {
            this.accelerometerType = type;
        } else {
            if (this.accelerometerType == type) {
                this.accelerometerType = ACCELEROMETER_NONE;
            }
        }
    }

    /*** EVENT BUS ***/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewLocationEvent event) {
        Location location = event.mLocation;

        int r = createColorFromNumber(location.getLatitude() * location.getLongitude());
        int g = createColorFromNumber(location.getLatitude() * location.getAltitude());
        int b = createColorFromNumber(location.getAltitude() * location.getLongitude());

        int color = Color.rgb(r, g, b);

        play.setColorFilter(color);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AccelerometerSensorEvent event) {
        switch (this.accelerometerType) {
            case ACCELEROMETER_TIME:
                this.manageTime(event.mTimeProgress);
                break;
            case ACCELEROMETER_VOLUME:
                this.manageVolume(event.mVolumeProgress);
                break;
        }
    }

    /*** END EVENT BUS ***/

    public int createColorFromNumber (double number) {
        return (int) ((number * 10000) % 255);
    }

    @Override
    public void onDownloadComplete(Music music) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        this.title.setText(music.getFullTitle());

        this.initMediaPlayer(music.getFilePath());
    }

    @Override
    public void onDownloadError(String error) {

    }

    public void initMediaPlayer (final String urlFile) {
        final Context context = this;

        // Be sure to run on UI Thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(getFilesDir(), urlFile);

                    mediaPlayer.setDataSource(context, Uri.fromFile(file));
                    mediaPlayer.prepare();

                    mediaPlayer.setVolume(0.5f, 0.5f);
                    progress.setMax(mediaPlayer.getDuration() / 1000);
                    progress.setProgress(0);

                    volume.setMax(100);
                    volume.setProgress(50);

                    loader.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    Log.i("error", e.getMessage());
                }
            }
        });
    }

    public void updateSeekBarProgress () {
        //Make sure you update Seekbar on UI thread.
        MusicActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mHandler != null) {
                        if (mediaPlayer != null) {
                            int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                            progress.setProgress(mCurrentPosition);
                        }

                        mHandler.postDelayed(this, 1000);
                    }
                } catch (Exception ex) {
                    Log.i("ERROR", "error - " + ex.getLocalizedMessage());
                }
            }
        });
    }

    private void manageTime(int value) {
        Log.i("e","[:timeController]" + value * this.progress.getMax() / 100);

        try {
            int newProgress = this.mediaPlayer.getCurrentPosition() + value * 1000;

            this.mediaPlayer.seekTo(newProgress);
            this.progress.setProgress(newProgress / 1000);
        } catch (Exception ex) {
            Log.i("ERROR", "error - " + ex.getLocalizedMessage());
        }
    }

    private void manageVolume(int value) {
        Log.i("e", "[:volumeController]" + value);
        try {
            int newVolume = this.volume.getProgress() + value;

            this.mediaPlayer.setVolume(newVolume / 100f, newVolume / 100f);
            this.volume.setProgress(newVolume);
        } catch (Exception ex) {
            Log.i("ERROR", "error - " + ex.getLocalizedMessage());
        }
    }
}
