package com.sloubi.unmusic.EventBus;

import android.location.Location;

public class NewLocationEvent {
    public Location mLocation;

    public NewLocationEvent(Location location) {
        this.mLocation = location;
    }
}
