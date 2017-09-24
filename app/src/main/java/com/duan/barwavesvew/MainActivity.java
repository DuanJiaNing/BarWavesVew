package com.duan.barwavesvew;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.duan.barwavesvew.controller.MediaController;
import com.duan.barwavesvew.controller.SongInfo;
import com.duan.library.BarWavesView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageSwitcher switcher;
    private BarWavesView barWavesView;
    private TextView name;
    private MediaController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        testSetColor();

    }

    private void testSetColor() {
//        barWavesView.setBarColor(ColorUtils.getRandomColor());
//        barWavesView.setWaveColor(ColorUtils.getRandomColor());

        int[][] cs = new int[barWavesView.getWaveNumber()][2];
        for (int i = 0; i < cs.length; i++) {
            cs[i][0] = ColorUtils.getRandomColor();
            cs[i][1] = ColorUtils.getRandomColor();
        }
        barWavesView.setWaveColor(cs);

    }

    private void init() {

        switcher = (ImageSwitcher) findViewById(R.id.image_switch);
        name = (TextView) findViewById(R.id.name);
        barWavesView = (BarWavesView) findViewById(R.id.BarWavesView);

        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView view = new ImageView(MainActivity.this);
                view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                view.setAlpha(0.4f);
                return view;
            }
        });

        controller = MediaController.getMediaController(this);
        try {
            controller.setCurrentSong(0);
            update(controller.getSongsList().get(0));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.releaseMediaPlayer();
    }

    public void onPlay(View v) {
        if (controller.playing()) {
            controller.stop();
            ((Button) v).setText("播放");
        } else {
            controller.play();
            ((Button) v).setText("暂停");
        }
    }

    public void onNext(View v) {
        try {
            update(controller.nextSong());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void update(SongInfo song) {
        name.setText(song.getName());
        BitmapDrawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(song.getArtPicPath()));
        switcher.setImageDrawable(d);
    }

    public void onPre(View v) {
        try {
            update(controller.preSong());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
