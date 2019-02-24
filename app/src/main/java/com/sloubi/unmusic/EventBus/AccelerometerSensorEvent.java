package com.sloubi.unmusic.EventBus;

public class AccelerometerSensorEvent {
    public int mTimeProgress;
    public int mVolumeProgress;

    public AccelerometerSensorEvent(int timeProgress, int volumeProgress) {
        this.mTimeProgress = timeProgress;
        this.mVolumeProgress = volumeProgress;
    }
}
