package com.duan.barwavesvew;

import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.duan.barwavesvew.controller.MediaController;
import com.duan.barwavesvew.controller.SongInfo;
import com.duan.library.BarWavesView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageSwitcher switcher;
    private TextView name;
    private MediaController controller;
    private Switch switcz;

    private BarWavesView barWavesView;
    private BarWavesView barWavesView_1;
    private BarWavesView barWavesView_2;
    private BarWavesView barWavesView_2_;
    private BarWavesView barWavesView_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
//        testSetColor();
    }

    private void testSetColor() {
        barWavesView.setBarColor(ColorUtils.getRandomColor());
//        barWavesView.setWaveColor(ColorUtils.getRandomColor());

        int[][] cs = new int[barWavesView.getWaveNumber()][2];
        for (int i = 0; i < cs.length; i++) {
            cs[i][0] = ColorUtils.getRandomColor();
            cs[i][1] = ColorUtils.getRandomColor();
        }
        barWavesView.setWaveColor(cs);

    }

    private void init() {

        barWavesView = (BarWavesView) findViewById(R.id.BarWavesView);
        barWavesView_1 = (BarWavesView) findViewById(R.id.BarWavesView_1);
        barWavesView_2 = (BarWavesView) findViewById(R.id.BarWavesView_2);
        barWavesView_2_ = (BarWavesView) findViewById(R.id.BarWavesView_2_);
        barWavesView_3 = (BarWavesView) findViewById(R.id.BarWavesView_3);


        switcher = (ImageSwitcher) findViewById(R.id.image_switch);
        name = (TextView) findViewById(R.id.name);
        switcz = (Switch) findViewById(R.id.visuall);

        switcz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                controller.setVisualizerEnable(isChecked);
            }
        });

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

        controller = new MediaController(this);
        controller.setupVisualizer(barWavesView.getWaveNumber(), 50, new MediaController.onFftDataCaptureListener() {
            @Override
            public void onFftCapture(float[] fft) {
                barWavesView.setWaveHeight(fft);
                barWavesView_1.setWaveHeight(fft);
                barWavesView_2.setWaveHeight(fft);
                barWavesView_2_.setWaveHeight(fft);
                barWavesView_3.setWaveHeight(fft);

//                testSetColor();
            }
        });
        controller.setVisualizerEnable(true);

        update(controller.getSongsList().get(0));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
