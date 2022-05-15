//package com.test.js.chat.facedetect;
//
///**
// * Created by lenovo on 2017-09-07.
// */
//
//
///*
// * 1) 일단 비디오 캡쳐 이미지로 Gray이미지와 RGBA이미지를 생성
// * 2) Gray이미지로 얼굴 판독하여 얼굴영역 리스트를 받아옴
// * 3) 얼굴 영역 리스트들을 순회하며 RGBA이미지에 사각형을 직접 그림
// * 4) RGBA이미지를 Bitmap 객체로 변환하여 리턴
// */
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.util.Log;
//import android.view.SurfaceHolder;
//
//import com.test.js.chat.R;
//
//import org.opencv.android.Utils;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//import org.opencv.core.Rect;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.highgui.Highgui;
//import org.opencv.highgui.VideoCapture;
//import org.opencv.objdetect.CascadeClassifier;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//
//// 기본적인 View 클래스가 아니라 SampleCvViewBase 를 상속
//// SampleCvViewBase는 SurfaceView 를 상속받고 있고,
//// 그 밖에 SurfaceHolder.Callback, Runnable의 인터페이스를 구현하고 있다.
//// public abstract class SampleCvViewBase extends SurfaceView implements SurfaceHolder.Callback, Runnable
//class FdView extends SampleCvViewBase
//{
//    private static final String   TAG = "Sample::FdView";    // 테그
//    private Mat                   mRgba;            // mRgba 행렬
//    private Mat                   mGray;            // Gray 행렬
//    private File                  mCascadeFile;          // 케스케이드 파일
//    private CascadeClassifier     mJavaDetector;        // 케스케이드 분류기 자바디텍터
//    private DetectionBasedTracker mNativeDetector;        // 네이티브 디텍터는 C++라이브러리를 레핑한 DetectionBasedTracker
//
//    private static final Scalar   FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);    // 얼굴 검출 영역을 표시할 색인듯.. (녹색인 걸로 봐서)
//
//    public static final int       JAVA_DETECTOR     = 0;    // 상태값을 나타내는데 쓰이는 변수 (자바 디텍터 0)
//    public static final int       NATIVE_DETECTOR   = 1;    // (네이티브 디텍터 1)
//
//    private int                   mDetectorType     = NATIVE_DETECTOR;  // 디텍터 타입의 초기값은 0
//
//    private float                 mRelativeFaceSize = 0;    // 상대적인 얼굴 크기 (초기값은 0.5f가 들어온다)
//    private int            mAbsoluteFaceSize = 0;    // 절대적인 얼굴 크기 (초기값이 0?)
//
//    // 최소 얼굴 크기 설정
//    public void setMinFaceSize(float faceSize)
//    {
//        mRelativeFaceSize = faceSize;
//        mAbsoluteFaceSize = 0;
//    }
//
//    // 검출기 타입 설정
//    public void setDetectorType(int type)
//    {
//        // 같지 않을때만 변경하는건 당연한 기법
//        if (mDetectorType != type)
//        {
//            mDetectorType = type;
//
//            if (type == NATIVE_DETECTOR)
//            {
//                // 로그에 기록한 뒤 시작
//                Log.i(TAG, "Detection Based Tracker enabled");
//                mNativeDetector.start();
//            }
//            else
//            {
//                // 자바 디텍터로 바꾸는 코드는 따로 없이 네이티브를 종료하는것으로 해결
//                // (좀 신기한데?)
//                Log.i(TAG, "Cascade detector enabled");
//                mNativeDetector.stop();
//            }
//        }
//    }
//
//    // 생성자 - 파일을 복사해 오는 코드가 왜 들어가 있는지 이해가 잘 안됨
//    public FdView(Context context)
//    {
//        // SampleCvViewBase 에 super을 호출하여 콘텍스트를 넘김
//        super(context);
//
//        // 익셉션 컨트롤이 적용된 코드
//        try
//        {
//            // 인풋스트림 is에 로우 리소스를 받는다. (의미불명)
//            InputStream is = context.getResources().openRawResource(R.raw.lbpcascade_frontalface);
//
//            // cascadeDir에 정면 얼굴 정보를 담고있을 것으로 추정되는 xml파일을 오픈시킨다.
//            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
//            mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");  // 바로 대입시키지 않은건 스트롱 익셉션을 고려한 코드
//            FileOutputStream os = new FileOutputStream(mCascadeFile);      // 열고 난 뒤 해당 파일로 아웃풋 스트림을 생성
//
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//            while ((bytesRead = is.read(buffer)) != -1)
//            {
//                // 파일을 버퍼 크기만큼 읽어들여 EOF가 아닐 때까지 os에 기록한다 (그냥 복사?)
//                os.write(buffer, 0, bytesRead);
//            }
//            is.close();
//            os.close();
//
//            // 자바 디텍터는 폭포분류기로 생성된다. (파일의 절대경로를 인자로 받는 생성자)
//            mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
//            if (mJavaDetector.empty())
//            {
//                // 자바 디텍터 생성에 실패했다면 로그를 기록하고 null을 대입
//                Log.e(TAG, "Failed to load cascade classifier");
//                mJavaDetector = null;
//            }
//            else
//            {
//                // 자바 디텍터 생성 성공 시 로그를 기록
//                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
//            }
//
//            // 네이티브 디텍터도 마찬가지로 생성자에 전체경로를 받는다. (두번째 인자는 최소 얼굴 크기)
//            mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
//
//            // 임시로 생성했던 파일은 삭제한다.
//            cascadeDir.delete();
//
//        }
//        catch (IOException e)
//        {
//            // IO익셉션 발생 시 현재 스택의 상태를 기록하고 로그를 남긴다.
//            e.printStackTrace();
//            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
//        }
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder)
//    {
//        // 표면이 생성될때 싱크를 맞춰서 (아마도 멀티 스레드) 각 행렬들을 초기화 (생성)
//        synchronized (this)
//        {
//            // initialize Mats before usage
//            mGray = new Mat();
//            mRgba = new Mat();
//        }
//
//        super.surfaceCreated(holder);
//    }
//
//    @Override
//    // Bitmap을 반환하는 processFrame 함수 (인자로 비디오 캡쳐를 받는다)
//    protected Bitmap processFrame(VideoCapture capture)
//    {
//        // 캡쳐에 검색 메서드를 호출해서 Rgba와 Gray 레이어를 받아와 저장한다 (매트릭스 형태로 넘어옴)
//        capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
//        capture.retrieve(mGray, Highgui.CV_CAP_ANDROID_GREY_FRAME);
//
//        // 절대 얼굴 크기가 0이라면
//        if (mAbsoluteFaceSize == 0)
//        {
//            int height = mGray.rows();
//            if (Math.round(height * mRelativeFaceSize) > 0);
//            {
//                // 높이와 상대크기를 곱한 값을 반올림 한 것이 0보다 클 경우
//                // 절대 크기는 높이와 상대크기를 곱하고 반올림한 값
//                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
//            }
//
//            // 0보다 작을경우 네이티브 디텍터의 최소 얼굴 크기는 그냥 0
//            // 은근히 코드가 알아보기 난해하다
//            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
//        }
//
//        // 얼굴 영역을 표시할 사각형 생성
//        MatOfRect faces = new MatOfRect();
//
//        // 자바 디텍터를 사용할 경우
//        if (mDetectorType == JAVA_DETECTOR)
//        {
//            // 모든 크기의 얼굴을 검출할 수 있는 모양? (근데 막상 해보니 역시 스케일(mRelativeFaceSize)의 영향을 받는데??)
//            if (mJavaDetector != null)
//                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2 // TODO: objdetect.CV_HAAR_SCALE_IMAGE
//                        , new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
//
//            // detectMultiScale(Mat image, MatOfRect objects, double scaleFactor, int minNeighbors, int flags, Size minSize, Size maxSize)
//            // 이미지 행렬, 사각형, 스케일팩터(?), 최소 이웃들, 플레그(2), 최소크기, 최대크기
//            // Size의 생성자의 두 인자값은 넓이, 높이 이다.
//            // Size의 생성자에 아무것도 집어넣지 않으면 0, 0 으로 생성된다.
//        }
//        // 네이티브 디텍터를 사용할 경우
//        else if (mDetectorType == NATIVE_DETECTOR)
//        {
//            // 훨씬 사용하기 편해보인다
//            // 몇개까지 검출할 수 있는지에 대한 명세는 없는걸까...
//            if (mNativeDetector != null)
//                mNativeDetector.detect(mGray, faces);
//        }
//        else
//        {
//            // 어느 디텍터도 사용하지 않을 경우에 대한 코드... 이렇게까지 작성해야 하나?
//            Log.e(TAG, "Detection method is not selected!");
//        }
//
//        // 사각형의 배열 생성 (아마도 MatOfRect 는 얼굴 영역들의 리스트인것 같다)
//        Rect[] facesArray = faces.toArray();
//        for (int i = 0; i < facesArray.length; i++)
//            Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
//        // rectangle(Mat img, Point pt1, Point pt2, Scalar color, int thickness)
//        // Rgba이미지에, 2점과, 주어진 컬러로 사각형을 그린다. 두깨는 3
//
//        // 사각형이 그려진 mRgba의 넓이, 높이만을 가지고 Bitmap객체 생성 (직접 변환은 다음줄에서 이루어짐)
//        Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
//
//        try
//        {
//            // 유틸 클래스를 이용하여 bmp클래스로 변환
//            Utils.matToBitmap(mRgba, bmp);
//        }
//        catch(Exception e)
//        {
//            // 에러가 난다면 리사이클 (아마도 풀 관리로 이루어진 듯 하다)
//            Log.e(TAG, "Utils.matToBitmap() throws an exception: " + e.getMessage());
//            bmp.recycle();
//            bmp = null;
//        }
//
//        // 비트맵 리턴
//        return bmp;
//    }
//
//    @Override
//    public void run()
//    {
//        super.run();
//
//        // 이건 소멸자의 느낌인데?
//        synchronized (this)
//        {
//            // Explicitly deallocate Mats
//            // 명시적으로 메트릭스 할당을 해제 (아무리 자바라도 메모리 관리 차원에서 해주는 건가?
//            if (mRgba != null)
//                mRgba.release();
//            if (mGray != null)
//                mGray.release();
//            if (mCascadeFile != null)
//                mCascadeFile.delete();
//            if (mNativeDetector != null)
//                mNativeDetector.release();
//
//            mRgba = null;
//            mGray = null;
//            mCascadeFile = null;
//        }
//    }
//}
