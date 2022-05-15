package com.test.js.chat;

/**
 * Created by lenovo on 2017-08-09.
 */

public class NDK {
    static {
        System.loadLibrary("ffmpeg");
    }

    public native int run_ffmpeg();

//
//    static {
//        System.loadLibrary("VideoPlayer");
//    }
//    public static native void setDataSource(String uri);
//    public static native int play(Object surface);
//    public NDK() { }

}

