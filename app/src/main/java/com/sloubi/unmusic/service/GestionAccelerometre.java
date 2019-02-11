package com.sloubi.unmusic.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.SeekBar;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public class GestionAccelerometre implements SensorEventListener {
    private Display mDisplay;
    private SeekBar runningSeekbar = null;
    private boolean estHorizontal = false;
    SensorManager sensorManager;
    Sensor accelerometer;
    private Context context;
    private MediaPlayer player;


    public GestionAccelerometre(Context context, MediaPlayer player) {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        this.context = context;
        this.player = player;
    }

    public void setSeekBar(SeekBar runningSeekbar, boolean estHorizontal) {
        this.runningSeekbar = runningSeekbar;
        this.estHorizontal = estHorizontal;
    }

    public void unssetSeekBar() {
        this.runningSeekbar = null;
    }

    private void timeController(int value) {
        player.seekTo(value * 1000);
        Log.i("logN2",value*runningSeekbar.getMax()/100+"");
        runningSeekbar.setProgress(value*runningSeekbar.getMax()/100);
    }

    private void volumeController(int value) {
        Float volume = value / 1000.0f;
        player.setVolume(volume, volume);
        Log.i("logN1", value + "");
        runningSeekbar.setProgress(value);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (runningSeekbar != null) {
            int value = 0;
            int facteurProgression = 0;
            float prog = 0;
            if (estHorizontal)
                prog = event.values[0];
            else
                prog = event.values[1];
            facteurProgression = (int) Math.floor(prog * 5.0);

            value = (50 - facteurProgression);
            if (estHorizontal) {
                timeController(value);
            } else {
                volumeController(value);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
