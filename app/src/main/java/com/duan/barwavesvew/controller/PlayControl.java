package com.duan.barwavesvew.controller;

import java.io.IOException;

/**
 * Created by DuanJiaNing on 2017/4/1.
 */

public interface PlayControl {

    SongInfo nextSong() throws IOException;

    SongInfo preSong() throws IOException;

    boolean play();

    boolean stop();

    void seekTo(int to);

    int getCurrentPosition();

}
