package com.sloubi.unmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.sloubi.unmusic.service.LocationService;

import com.sloubi.unmusic.acceleroPackage.GestionAccelerometre;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.play = findViewById(R.id.iv_play);
        progress = findViewById(R.id.sb_avancement);
        volume = findViewById(R.id.sb_volume);
        btn_timming = findViewById(R.id.btn_timming);
        btn_volume = findViewById(R.id.btn_volume);


        play.setOnClickListener(this);
        progress.setOnSeekBarChangeListener(this);
        volume.setOnSeekBarChangeListener(this);
        btn_timming.setOnClickListener(this);
        btn_volume.setOnClickListener(this);


        mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.igorrr_viande);
        mediaPlayer.setVolume(0.4f, 0.4f);

        progress.setMax(mediaPlayer.getDuration() / 1000);
        volume.setMax(100);
        volume.setProgress(40);

        piloteAccelero = new GestionAccelerometre(this.getBaseContext(), mediaPlayer);
        //Make sure you update Seekbar on UI thread.
        MainActivity.this.runOnUiThread(new Runnable() {
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
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
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
}
