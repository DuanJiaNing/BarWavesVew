package com.duan.barwavesvew.controller;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.media.audiofx.Visualizer.getMaxCaptureRate;

/**
 * Created by DuanJiaNing on 2017/4/1.
 */

public class MediaController implements PlayControl {

    private Context mContext;

    private volatile int mCurrentSong;

    private volatile boolean mPlayState = false; //true为正在播放

    private final ArrayList<SongInfo> songs = new ArrayList<>();

    private final MediaPlayer mPlayer;
    private Visualizer mVisualizer;
    private boolean mVisualizerEnable = false;

    public MediaController(Context context) {
        this.mContext = context.getApplicationContext();

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            SongInfo info = new SongInfo();
            info.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
            info.setSongPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            String albumID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            info.setArtPicPath(getAlbumArtPicPath(albumID));
            songs.add(info);
        }
        cursor.close();

        if (songs.size() < 0) {
            throw new IllegalStateException("没有歌曲");
        }

        mPlayer = new MediaPlayer();
        try {
            setCurrentSong(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface onFftDataCaptureListener {
        void onFftCapture(float[] fft);
    }

    /**
     * 设置频谱回调
     *
     * @param size 传回的数组大小
     * @param max  整体频率的大小，该值越小，传回数组的平均值越大，在 50 时效果较好。
     * @param l    回调
     */
    public void setupVisualizer(final int size, final int max, final onFftDataCaptureListener l) {
        // 频率分之一是时间  赫兹=1/秒
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]); //0为128；1为1024
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {

            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                //快速傅里叶变换有关的数据

            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                //波形数据

                byte[] model = new byte[fft.length / 2 + 1];
                model[0] = (byte) Math.abs(fft[1]);
                int j = 1;

                for (int i = 2; i < size * 2; ) {

                    model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
                    i += 2;
                    j++;
                }

                float[] data = new float[size];
                if (max != 0) {
                    for (int i = 0; i < size; i++) {
                        data[i] = (float) model[i] / max;
                        data[i] = data[i] < 0 ? 0 : data[i];
                    }
                } else {
                    Arrays.fill(data, 0);
                }

                l.onFftCapture(data);

            } // getMaxCaptureRate() -> 20000 最快
        }, getMaxCaptureRate() / 8, false, true);

        mVisualizer.setEnabled(false); //这个设置必须在参数设置之后，表示开始采样
    }

    public void setVisualizerEnable(boolean visualizerEnable) {
        this.mVisualizerEnable = visualizerEnable;
        if (mPlayer.isPlaying()) {
            mVisualizer.setEnabled(mVisualizerEnable);
        }
    }

    private String getAlbumArtPicPath(String albumId) {
        String[] projection = {MediaStore.Audio.Albums.ALBUM_ART};
        String imagePath = null;
        Cursor cur = mContext.getContentResolver().query(
                Uri.parse("content://media" + MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI.getPath() + "/" + albumId),
                projection,
                null,
                null,
                null);
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            imagePath = cur.getString(0);
        }
        cur.close();
        return imagePath;
    }

    public ArrayList<SongInfo> getSongsList() {
        return songs;
    }

    public int getCurrentSong() {
        return mCurrentSong;
    }

    public int setCurrentSong(int index) throws IOException {
        if (mCurrentSong != index) {
            this.mCurrentSong = index;
            changeSong();
        }
        return mCurrentSong;
    }

    public int setCurrentSong(SongInfo info) throws IOException {
        this.mCurrentSong = songs.indexOf(info);
        changeSong();
        return mCurrentSong;
    }

    public boolean getPlayState() {
        return mPlayState;
    }

    public void releaseMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.release();
        }

        if (mVisualizer != null) {
            mVisualizer.release();
        }
    }

    @Override
    public SongInfo preSong() throws IOException {

        if (mCurrentSong == 0) {
            mCurrentSong = songs.size() - 1;
        } else {
            mCurrentSong--;
        }

        changeSong();

        return songs.get(mCurrentSong);
    }

    private synchronized void changeSong() throws IOException {

        if (mPlayState) {
            mPlayer.stop();
        }

        mPlayer.reset();
        mPlayer.setDataSource(songs.get(mCurrentSong).getSongPath());
        mPlayer.prepare();

        if (mPlayState) {
            mPlayer.start();
        }

    }

    @Override
    public SongInfo nextSong() throws IOException {

        if (mCurrentSong == songs.size() - 1) {
            mCurrentSong = 0;
        } else {
            mCurrentSong++;
        }

        changeSong();

        return songs.get(mCurrentSong);
    }

    @Override
    public synchronized boolean play() {
        if (mPlayer.isPlaying())
            return false;
        else {
            mPlayer.start();
            if (mVisualizerEnable) {
                mVisualizer.setEnabled(true);
            }
            mPlayState = true;
            return true;
        }
    }

    public boolean playing() {
        return mPlayer.isPlaying();
    }

    @Override
    public synchronized boolean stop() {
        if (!mPlayer.isPlaying())
            return false;
        else {
            mPlayer.pause();
            if (mVisualizerEnable) {
                mVisualizer.setEnabled(false);
            }
            mPlayState = false;
            return true;
        }
    }

    @Override
    public void seekTo(int to) {
        mPlayer.seekTo(to);
    }

    @Override
    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mPlayer.getDuration();
    }


}
