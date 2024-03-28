package com.example.webrtc;

import android.app.Application;

import com.farsunset.webrtc.WebrtcMeetingSdk;

public class App extends Application {

    public void onCreate() {
        super.onCreate();
        WebrtcMeetingSdk.install(this);
    }
}
