package com.duan.barwavesvew;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer player;
    private AssetManager assetManager = getAssets();
    private int current = 0;

    private final String[] songs = {
            "01 The Moment I Knew.mp3",
            "Mystery Skulls - Paralyzed.mp3",
            "Taylor Swift - Songs About You.mp3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = new MediaPlayer();
        assetManager = getAssets();

        init();

    }

    private void init() {

    }

    public void onPlay(View v) {

    }

    public void onSwitch(View v) {

        try {

            AssetFileDescriptor fd = assetManager.openFd(songs[0]);
            if (player.isPlaying()) {
                player.stop();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                player.setDataSource(fd);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
