package com.test.js.chat;

//public class MainActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        new NDK().run_ffmpeg();
//    }
//}

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    //private FaceDetector faceDetector;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            // 이 권한을 필요한 이유를 설명
            // It can see the file list in external storage.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다
            }
        }
        else {
            new NDK().run_ffmpeg();
        }

//        RtspPlayView playView = new RtspPlayView(getApplicationContext(), "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov");
//        setContentView(playView);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                    new NDK().run_ffmpeg();
                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                }
                return;
        }
    }
}

//class RtspPlayView extends SurfaceView implements SurfaceHolder.Callback{
//
//    private static final String TAG = "RtspPlayView";
//
//    private SurfaceHolder mHolder;
//    private NDK mPlayerNdkAdapter;
//
//    public RtspPlayView(Context context, String uri) {
//        super(context);
//        mHolder = getHolder();
//        mHolder.addCallback(this);
//
//        // JNI에 있는 라이브러리를 통해서 작업한다.
//        mPlayerNdkAdapter = new NDK();
//        mPlayerNdkAdapter.setDataSource(uri);
//    }
//
//
//    /** -----------------------------
//     * SurfaceHolder.Callback Implementation
//     */
//    @Override
//    public void surfaceCreated(SurfaceHolder surfaceHolder) {
//        // 비디오 플레이는 시간이 많이 걸리는 작업이여서 작업도중 메인 UI 쓰레드를 차단하지 않기 위해서, 별도의 쓰레드를 통해서 재생한다.
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mPlayerNdkAdapter.play(mHolder.getSurface());
//            }
//        }).start();
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//
//    }
//}