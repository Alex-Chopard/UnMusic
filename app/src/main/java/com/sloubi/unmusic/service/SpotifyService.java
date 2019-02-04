package com.sloubi.unmusic.service;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class SpotifyService {
    private static final String CLIENT_ID = "";
    private static final String REDIRECT_URI = "http://com.sloubi.unmusic/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
}
