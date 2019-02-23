package com.sloubi.unmusic.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.sloubi.unmusic.Interface.OnMusicGetListener;
import com.sloubi.unmusic.Model.Music;
import com.sloubi.unmusic.R;
import com.sloubi.unmusic.Services.GestionAccelerometre;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnMusicGetListener {

    private int id;
    private String fullTitle;
    private String fileUrl;

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
        this.id = getIntent().getIntExtra("id", -1);
        this.fullTitle = getIntent().getStringExtra("fullTitle");
        this.fileUrl = getIntent().getStringExtra("fileUrl");

        if (this.fileUrl.length() > 0) {
            this.initMediaPlayer(this.fileUrl);
        } else {
            // Start to load music data.
            Music.get(this.id, this, this);
        }

        this.play = findViewById(R.id.iv_play);
        this.progress = findViewById(R.id.sb_avancement);
        this.volume = findViewById(R.id.sb_volume);
        this.btn_timming = findViewById(R.id.btn_timming);
        this.btn_volume = findViewById(R.id.btn_volume);
        this.loader = findViewById(R.id.liv_loader);
        this.title = findViewById(R.id.tv_title);


        this.play.setOnClickListener(this);
        this.progress.setOnSeekBarChangeListener(this);
        this.volume.setOnSeekBarChangeListener(this);
        this.btn_timming.setOnClickListener(this);
        this.btn_volume.setOnClickListener(this);

        this.loader.setVisibility(View.VISIBLE);
        this.play.setVisibility(View.GONE);

        this.title.setText(this.fullTitle);

        /* piloteAccelero = new GestionAccelerometre(this.getBaseContext(), mediaPlayer);
        //Make sure you update Seekbar on UI thread.
        MusicActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    progress.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });


        // Start listening the broadcaster.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(LocationService.LOCATION_SERVICE_BRODCAST_NAME));

        // Start location service.
        startService(new Intent(this, LocationService.class)); */
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
        /*LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();*/
    }

    @Override
    public void onClick(View v) {

        int vId = v.getId();
        switch (vId) {
            case R.id.iv_play:
                ImageView play = findViewById(R.id.iv_play);

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    play.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mediaPlayer.start();
                    play.setImageResource(android.R.drawable.ic_media_pause);
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

    public void initMediaPlayer (String urlFile) {
        try {
            File file = new File(this.getFilesDir(), urlFile);

            this.mediaPlayer.setDataSource(this, Uri.fromFile(file));
            this.mediaPlayer.prepare();

            this.mediaPlayer.setVolume(0.5f, 0.5f);
            this.progress.setMax(mediaPlayer.getDuration() / 1000);
            this.progress.setProgress(0);

            this.volume.setMax(100);
            this.volume.setProgress(50);

            this.loader.setVisibility(View.GONE);
            this.play.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Log.i("error", e.getMessage());
        }
    }
}
