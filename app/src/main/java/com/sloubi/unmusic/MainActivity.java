package com.sloubi.unmusic;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  private int i;
  private Context context;
  private MediaPlayer mediaPlayer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    i = 0;
    context = getBaseContext();

    /* Button button = (Button) findViewById(R.id.button);
    button.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View v) {
        i ++;
        Toast.makeText(context, "Sloubi " + i, Toast.LENGTH_SHORT).show();
      }
    }); */


    mediaPlayer = MediaPlayer.create(context, R.raw.igorrr_viande);

    mediaPlayer.start();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mediaPlayer.stop();
    mediaPlayer.release();
  }
}
