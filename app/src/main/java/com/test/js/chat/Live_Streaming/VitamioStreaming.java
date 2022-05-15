//package com.test.js.chat.Live_Streaming;
//
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//import android.widget.Toast;
//
//import com.test.js.chat.R;
//
//import io.vov.vitamio.LibsChecker;
//import io.vov.vitamio.MediaPlayer;
//import io.vov.vitamio.widget.MediaController;
//import io.vov.vitamio.widget.VideoView;
//
//
//public class VitamioStreaming extends AppCompatActivity {
//    private static final String TAG = "MainActivity";
//    String path;
//    //private HashMap<String, String> options;
//    VideoView mVideoView;
//    String live_ID;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (!LibsChecker.checkVitamioLibs(this))
//            return;
//        setContentView(R.layout.activity_vitamio_streaming);
//        live_ID = getIntent().getStringExtra("liveid");
//        mVideoView = (VideoView) findViewById(R.id.vitamio_videoView);
//        //path = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
//        //
//        //path = "rtsp://gstreamer_rtsp_ip:8554/test";
//        path = "rtmp://jpcsm9003.vps.phps.kr/hls/"+live_ID;
//        Toast.makeText(this, "path : "+path, Toast.LENGTH_SHORT).show();
//        mVideoView.setVideoPath(path);
//        mVideoView.setMediaController(new MediaController(this));
//        mVideoView.requestFocus();
//
//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mediaPlayer.setPlaybackSpeed(1.0f);
//            }
//        });
//
//    }
//}