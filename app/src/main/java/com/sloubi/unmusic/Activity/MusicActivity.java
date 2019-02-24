package com.sloubi.unmusic.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sloubi.unmusic.Interface.OnMusicGetListener;
import com.sloubi.unmusic.Model.Music;
import com.sloubi.unmusic.R;
import com.sloubi.unmusic.Services.GestionAccelerometre;
import com.sloubi.unmusic.Services.LocationService;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnMusicGetListener {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler mHandler = new Handler();
    private SeekBar progress;
    private SeekBar volume;
    private boolean accelroIsActivate = false;
    private GestionAccelerometre piloteAccelero;
    private Button btn_timming;
    private Button btn_volume;
    private ImageView play;
    private AVLoadingIndicatorView loader;
    private TextView title;

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
        this.btn_timming = findViewById(R.id.btn_timming);
        this.btn_volume = findViewById(R.id.btn_volume);
        this.loader = findViewById(R.id.liv_loader);
        this.title = findViewById(R.id.tv_title);

        // If the music is already loaded.
        if (fileUrl != null && fileUrl.length() > 0) {
            this.initMediaPlayer(fileUrl);
        } else {
            // Start to load music data.
            Music.get(id, this, this);
        }

        // Set listeners.
        this.play.setOnClickListener(this);
        this.progress.setOnSeekBarChangeListener(this);
        this.volume.setOnSeekBarChangeListener(this);
        this.btn_timming.setOnClickListener(this);
        this.btn_volume.setOnClickListener(this);

        this.loader.setVisibility(View.VISIBLE);
        this.play.setVisibility(View.GONE);

        this.title.setText(fullTitle);

        // Enabled user directly change SeekBar progress.
        this.progress.setEnabled(false);
        this.volume.setEnabled(false);

        updateSeekBarProgress();

        // piloteAccelero = new GestionAccelerometre(this.getBaseContext(), mediaPlayer);

        // Start listening the broadcaster.
        // TODO: replace it by an EventBus
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(LocationService.LOCATION_SERVICE_BRODCAST_NAME));

        // Start location service.
        startService(new Intent(this, LocationService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
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
                gestionAccelero(String.valueOf(R.id.sb_avancement), true);
                break;
            case R.id.btn_volume:
                gestionAccelero(String.valueOf(R.id.sb_volume), false);
                break;
        }
    }

    private void gestionAccelero(String id, boolean estHorizontal) {
        if (!accelroIsActivate) {
            piloteAccelero.setSeekBar((SeekBar) findViewById(Integer.valueOf(id)), estHorizontal);
            accelroIsActivate = true;
        } else {
            piloteAccelero.unssetSeekBar();
            accelroIsActivate = false;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            double latitude = intent.getDoubleExtra("latitude", 0);
            double longiture = intent.getDoubleExtra("longiture", 0);
            double altitude = intent.getDoubleExtra("altitude", 0);

            int r = createColorFromNumber(latitude * longiture);
            int g = createColorFromNumber(latitude * altitude);
            int b = createColorFromNumber(altitude * longiture);

            int color = Color.rgb(r, g, b);

            play.setColorFilter(color);
        }
    };

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
}
