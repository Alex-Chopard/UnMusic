package com.sloubi.unmusic.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.sloubi.unmusic.EventBus.AccelerometerSensorEvent;
import com.sloubi.unmusic.EventBus.NewLocationEvent;

import org.greenrobot.eventbus.EventBus;

// https://stackoverflow.com/a/8830135
public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = "[LocationService]";
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0;

    private LocationManager mLocationManager;

    private LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        LocationListener(String provider) {
            Log.i(TAG, "[:constructor] " + provider);
            this.mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "[:onLocationChanged] " + location);
            mLastLocation.set(location);

            // Publish the new Location.
            EventBus.getDefault().post(new NewLocationEvent(mLastLocation));
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "[:onProviderDisabled]" + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "[:onProviderEnabled] " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "[:onStatusChanged] " + provider);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "[:onStartCommand]");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "[:onCreate]");

        Context context = getApplicationContext();

        /** Location **/

        // Init LocationManager
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        /** End Location **/


        /** Accelerometer **/

        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        /** End Accelerometer **/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "[:onDestroy]");

        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    /*** SENSOR EVENTS ***/

    @Override
    public void onSensorChanged(SensorEvent event) {
        float progressX = event.values[0];
        float progressY = event.values[1];

        int valueForTime = (50 - (int) Math.floor(progressX * 5.0));
        int valueForVolume = (50 - (int) Math.floor(progressY * 5.0));

        // Post the event
        EventBus.getDefault().post(new AccelerometerSensorEvent(valueForTime, valueForVolume));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    /*** END SENSOR EVENTS ***/
}