package com.sloubi.unmusic;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getBaseContext();


        ImageView play = findViewById(R.id.iv_play);
        play.setOnClickListener(this);

        mediaPlayer = MediaPlayer.create(context, R.raw.igorrr_viande);
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
}
