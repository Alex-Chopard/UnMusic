package com.sloubi.unmusic.acceleroPackage;

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
    static SeekBar runningSeekbar = null;
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

    public void start(SeekBar id, boolean estHorizontal) {
        runningSeekbar = id;
        this.estHorizontal = estHorizontal;
    }

    public boolean end(SeekBar id) {
        if (runningSeekbar.equals(id)) {
            runningSeekbar = null;
            return true;
        }
        return false;
    }

    private void seekBarController(int value) {
        player.seekTo(value);
    }

    private void playerController(int value) {
        if (estHorizontal) {

        } else {
            Float volume = value / 100.0f;
            player.setVolume(volume, volume);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int value = 0;
        int facteurProgression = 0;
        if (runningSeekbar != null) {
            value = runningSeekbar.getProgress();
            float prog = 0;
            if (estHorizontal)
                prog = event.values[0];
            else
                prog = event.values[1];
            facteurProgression = (int) Math.floor(prog * 5.0);

            value = (50 - facteurProgression);
            seekBarController(value);
            playerController(value);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
