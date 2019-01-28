package com.sloubi.unmusic;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();

    private SeekBar progress;
    private SeekBar volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView play = findViewById(R.id.iv_play);
        progress = findViewById(R.id.sb_avancement);
        volume = findViewById(R.id.sb_volume);

        play.setOnClickListener(this);
        progress.setOnSeekBarChangeListener(this);
        volume.setOnSeekBarChangeListener(this);

        mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.igorrr_viande);
        mediaPlayer.setVolume(0.4f, 0.4f);

        progress.setMax(mediaPlayer.getDuration() / 1000);
        volume.setMax(100);
        volume.setProgress(40);

        //Make sure you update Seekbar on UI thread.
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    progress.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();
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
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int sId = seekBar.getId();

        if(mediaPlayer != null && fromUser) {
            switch (sId) {
                case R.id.sb_avancement:
                    mediaPlayer.seekTo(progress * 1000);
                    break;
                case R.id.sb_volume:
                    Float volume = progress / 100.0f;
                    mediaPlayer.setVolume(volume, volume);
                    break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
