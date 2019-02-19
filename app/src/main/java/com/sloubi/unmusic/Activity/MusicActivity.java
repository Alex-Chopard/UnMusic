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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.sloubi.unmusic.Interface.OnMusicGetListener;
import com.sloubi.unmusic.Model.Music;
import com.sloubi.unmusic.R;
import com.sloubi.unmusic.Services.GestionAccelerometre;
import com.sloubi.unmusic.Services.LocationService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnMusicGetListener {

    private int id;

    private MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();
    private SeekBar progress;
    private SeekBar volume;
    private boolean accelroIsActivate = false;
    private GestionAccelerometre piloteAccelero;
    private Button btn_timming;
    private Button btn_volume;
    private ImageView play;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.id = getIntent().getIntExtra("id", -1);

        Music.get(this.id, this, this);

        this.play = findViewById(R.id.iv_play);
        progress = findViewById(R.id.sb_avancement);
        volume = findViewById(R.id.sb_volume);
        btn_timming = findViewById(R.id.btn_timming);
        btn_volume = findViewById(R.id.btn_volume);
        Button btn_playlist = findViewById(R.id.btn_playlist);


        play.setOnClickListener(this);
        progress.setOnSeekBarChangeListener(this);
        volume.setOnSeekBarChangeListener(this);
        btn_timming.setOnClickListener(this);
        btn_volume.setOnClickListener(this);
        btn_playlist.setOnClickListener(this);

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
        /*mediaPlayer.stop();
        mediaPlayer.release();*/
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
            case R.id.btn_playlist:
                Intent intent = new Intent(this, PlayListActivity.class);
                startActivity(intent);
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        try {
            byte[] byteMusic = music.getData();
            File fileMusic = File.createTempFile("music", null, this.getCacheDir());
            FileOutputStream fileOutputStream = new FileOutputStream(fileMusic);

            BufferedOutputStream buffer = new BufferedOutputStream(fileOutputStream);

            buffer.write(byteMusic);
            buffer.close();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, Uri.fromFile(fileMusic));

            mediaPlayer.prepare();

            mediaPlayer.setVolume(0.4f, 0.4f);
            progress.setMax(mediaPlayer.getDuration() / 1000);

            volume.setMax(100);
            volume.setProgress(40);
        } catch (IOException e) {
            Log.i("error", e.getMessage());
        }
    }

    @Override
    public void onDownloadError(String error) {

    }
}