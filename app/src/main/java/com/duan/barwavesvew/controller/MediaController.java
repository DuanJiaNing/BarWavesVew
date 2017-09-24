package com.duan.barwavesvew.controller;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DuanJiaNing on 2017/4/1.
 */

public class MediaController implements PlayControl {

    private Context mContext;

    private volatile int mCurrentSong;

    private volatile boolean mPlayState = false; //true为正在播放

    private static MediaController mediaController = null;

    private final ArrayList<SongInfo> songs = new ArrayList<>();

    private final MediaPlayer mPlayer;

    private MediaController(Context context) {

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

        mPlayer = new MediaPlayer();

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

    public static synchronized MediaController getMediaController(Context context) {
        if (mediaController == null)
            mediaController = new MediaController(context);
        return mediaController;
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
        if (mPlayer != null)
            mPlayer.release();
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

        if (mPlayState)
            mPlayer.stop();

        mPlayer.reset();
        mPlayer.setDataSource(songs.get(mCurrentSong).getSongPath());
        mPlayer.prepare();

        if (mPlayState)
            mPlayer.start();

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
