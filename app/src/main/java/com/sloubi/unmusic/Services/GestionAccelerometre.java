package com.sloubi.unmusic.Services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Display;
import android.widget.SeekBar;

import static android.content.Context.SENSOR_SERVICE;

public class GestionAccelerometre implements SensorEventListener {
    private static final String TAG = "[GestionAccelerometre]";

    private SeekBar mSeekBar;
    private boolean mIsVolume = false;
    private MediaPlayer mMediaPlayer;


    public GestionAccelerometre(Context context, MediaPlayer mediaPlayer) {
        this.mMediaPlayer = mediaPlayer;

        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void setSeekBar(SeekBar seekBar, boolean isVolume) {
        this.mSeekBar = seekBar;
        this.mIsVolume = isVolume;
    }

    public void unSetSeekBar() {
        this.mSeekBar = null;
    }

    private void timeController(int value) {
        Log.i(TAG,"[:timeController]" + value * this.mSeekBar.getMax() / 100);

        this.mMediaPlayer.seekTo(value * 1000);
        this.mSeekBar.setProgress(value * this.mSeekBar.getMax() / 100);
    }

    private void volumeController(int value) {
        Log.i(TAG, "[:volumeController]" + value);

        float volume = value / 1000.0f;

        this.mMediaPlayer.setVolume(volume, volume);
        this.mSeekBar.setProgress(value);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (this.mSeekBar != null) {
            int value = 0;
            int facteurProgression = 0;
            float progress = 0;

            if (this.mIsVolume) {
                progress = event.values[1];
            } else {
                progress = event.values[0];
            }

            facteurProgression = (int) Math.floor(progress * 5.0);

            value = (50 - facteurProgression);

            if (this.mIsVolume) {
                this.volumeController(value);
            } else {
                this.timeController(value);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
