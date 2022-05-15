//package com.test.js.chat.facedetect;
//
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import com.test.js.chat.R;
//
//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.CameraBridgeViewBase;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.android.Utils;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//import org.opencv.core.Rect;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class FdActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
//
//    private static final String    TAG                 = "OCVSample::Activity";
//    //FACE_RECT_COLOR
//    private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
//    public static final int        JAVA_DETECTOR       = 0;
//    public static final int        NATIVE_DETECTOR     = 1;
//
//    private MenuItem mItemFace50;
//    private MenuItem               mItemFace40;
//    private MenuItem               mItemFace30;
//    private MenuItem               mItemFace20;
//    private MenuItem               mItemType;
//
//    private Mat mRgba;
//    private Mat                    mGray;
//    private File mCascadeFile;
//    private CascadeClassifier mJavaDetector;
//    private DetectionBasedTracker  mNativeDetector;
//
//    private int                    mDetectorType       = NATIVE_DETECTOR;
//    private String[]               mDetectorName;
//
//    private float                  mRelativeFaceSize   = 0.2f;   // 상대적인 얼굴 크기 (초기값은 0.2f가 들어온다)
//    private int                    mAbsoluteFaceSize   = 0;  // 절대적인 얼굴 크기 (초기값이 0?)
//
//    //private CameraBridgeViewBase mOpenCvCameraView;
//    private Tutorial3View mOpenCvCameraView;
//
//    //
//    int viewMode = VIEW_MODE_RGBA;
//    private static final int       VIEW_MODE_RGBA     = 0;
//    private static final int       VIEW_MODE_GRAY     = 1;
//    private static final int       VIEW_MODE_CANNY    = 2;
//    private static final int       VIEW_MODE_FEATURES = 5;
//    CameraBridgeViewBase.CvCameraViewFrame inputFrame;
//    int Face_lenth;
//
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS:
//                {
//                    Log.i(TAG, "OpenCV loaded successfully");
//
//                    // Load native library after(!) OpenCV initialization
//                    System.loadLibrary("detection_based_tracker");
//
//                    try {
//                        // load cascade file from application resources
//                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
//                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
//                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
//                        FileOutputStream os = new FileOutputStream(mCascadeFile);
//
//                        byte[] buffer = new byte[4096];
//                        int bytesRead;
//                        while ((bytesRead = is.read(buffer)) != -1) {
//                            os.write(buffer, 0, bytesRead);
//                        }
//                        is.close();
//                        os.close();
//
//                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
//                        if (mJavaDetector.empty()) {
//                            Log.e(TAG, "Failed to load cascade classifier");
//                            mJavaDetector = null;
//                        } else
//                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
//
//                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(),0);
//
//                        cascadeDir.delete();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
//                    }
//
//                    mOpenCvCameraView.enableView();
//                } break;
//                default:
//                {
//                    super.onManagerConnected(status);
//                } break;
//            }
//        }
//    };
//
//    public FdActivity() {
//        mDetectorName = new String[2];
//        mDetectorName[JAVA_DETECTOR] = "Java";
//        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";
//
//        Log.i(TAG, "Instantiated new " + this.getClass());
//    }
//    Button Btn_cameraoption;
//    ImageButton Btncapture;
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Log.i(TAG, "called onCreate");
//        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        setContentView(R.layout.face_detect_surface_view);
//
////        ActionBar ab = getActionBar();
////        ab.show();///액션바 보이기
//
//        //mOpenCvCameraView.setCameraIndex(0); // 후면 카메라 front-camera(1),  back-camera(0)
//        //mOpenCvCameraView.setCameraIndex(1); //  전면 카메라 front-camera(1),  back-camera(0)
//
//        //자바카메라 <=> 네이티브 카메라
//        Btn_cameraoption = (Button)findViewById(R.id.Btn_cameraoption);
//        Btn_cameraoption.setText(mDetectorName[mDetectorType]); //카메라 옵션명
//        Btn_cameraoption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mDetectorType==0){
//                    mDetectorType=1;
//                }else{
//                    mDetectorType=0;
//                }
//                Btn_cameraoption.setText(mDetectorName[mDetectorType]); //카메라 옵션명
//            }
//        });
//
//
//        //카메라캡쳐 버튼
//        Btncapture = (ImageButton)findViewById(R.id.Btncapture);
//        Btncapture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG,"카메라캡쳐버튼 onClick");
//                // 사각형이 그려진 mRgba의 넓이, 높이만을 가지고 Bitmap객체 생성 (직접 변환은 다음줄에서 이루어짐)
//                Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
//
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//                String currentDateandTime = sdf.format(new Date());
//                String fileName = Environment.getExternalStorageDirectory().getPath() +
//                        "/FaceDetection_" + currentDateandTime + ".jpg";
//
//                Bitmap bitmap = Utils.matToBitmap(mRgba,bmp);
//                byte[] data = bitmapToByteArray(bitmap);
//
//                // Write the image in a file (in jpeg format)
//                try {
//                    FileOutputStream fos = new FileOutputStream(fileName);
//
//                    fos.write(data);
//                    fos.close();
//
//                } catch (java.io.IOException e) {
//                    Log.e(TAG, "Exception in photoCallback", e);
//                    Toast.makeText(FdActivity.this, "파일저장오류", Toast.LENGTH_SHORT).show();
//                }
//                Toast.makeText(FdActivity.this, "파일저장완료\n"+"Face_lenth : "+Face_lenth, Toast.LENGTH_SHORT).show();
//
//                // 사각형이 그려진 mRgba의 넓이, 높이만을 가지고 Bitmap객체 생성 (직접 변환은 다음줄에서 이루어짐)
////                Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
////
////                try
////                {
////                    // 유틸 클래스를 이용하여 bmp클래스로 변환
////                    Utils.matToBitmap(mRgba, bmp);
////                }
////                catch(Exception e)
////                {
////                    // 에러가 난다면 리사이클 (아마도 풀 관리로 이루어진 듯 하다)
////                    Log.e(TAG, "Utils.matToBitmap() throws an exception: " + e.getMessage());
////                    bmp.recycle();
////                    bmp = null;
////                }
//
//
////                mOpenCvCameraView.takePicture(fileName,inputFrame,viewMode);
////
////                Toast.makeText(getApplication(), "mAbsoluteFaceSize : " + mAbsoluteFaceSize, Toast.LENGTH_SHORT).show();
//
//
//
////                SharedPreferences sp = getSharedPreferences("LockScreenBackgroundImage",MODE_PRIVATE);
////                SharedPreferences.Editor ed = sp.edit();
//                //해당이미지uri 쉐어드에 저장
////                Uri uri = Uri.fromFile(new File(fileName));
//////                        Uri uri = Uri.fromFile(getFileStreamPath(fileName));
////                ed.putString("pos"+ pos,""+uri.toString());
////                ed.commit();
//
//                // 미디어 스캐너를 통해 스크린샷이미지를 갱신시킨다.
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                        Uri.parse("file://"+Environment.getExternalStorageDirectory()+"/"+fileName+".jpg")));
//
////                new Thread(new Runnable() {
////                    @Override
////                    public void run() {
////                        try {
////                            Thread.sleep(1000);
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                finish();
////                            }
////                        });
////                    }
////                }).start();
//
//
//
//            }
//        });
//        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.fd_activity_surface_view);
//        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
//        mOpenCvCameraView.setCvCameraViewListener(this);
//    }
//
//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        if (mOpenCvCameraView != null)
//            mOpenCvCameraView.disableView();
//    }
//
//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
//        } else {
//            Log.d(TAG, "OpenCV library found inside package. Using it!");
//            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }
//    }
//
//    public void onDestroy() {
//        super.onDestroy();
//        mOpenCvCameraView.disableView();
//    }
//
//    public void onCameraViewStarted(int width, int height) {
//        mGray = new Mat();
//        mRgba = new Mat();
//    }
//
//    public void onCameraViewStopped() {
//        mGray.release();
//        mRgba.release();
//    }
//    //
//
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        this.inputFrame = inputFrame;
//
//        mRgba = inputFrame.rgba();
//        mGray = inputFrame.gray();
//
//        if (mAbsoluteFaceSize == 0) { //얼굴이 0개 일때
//            int height = mGray.rows();
//            if (Math.round(height * mRelativeFaceSize) > 0) {
//                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
//            }
//            // 최소 얼굴 크기를 설정하는 함수
//            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
//        }
//        //Core.flip(matInput, matInput, 1);
//
//        //얼굴인식시 초록색네모테두리
//        MatOfRect faces = new MatOfRect();
//
//        if (mDetectorType == JAVA_DETECTOR) {
//            if (mJavaDetector != null)
//                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
//                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
//        }
//        else if (mDetectorType == NATIVE_DETECTOR) {
//            if (mNativeDetector != null)
//                mNativeDetector.detect(mGray, faces);
//        }
//        else {
//            Log.e(TAG, "Detection method is not selected!");
//        }
//
//        Rect[] facesArray = faces.toArray();
//        //  인식한얼굴 개수
//        Face_lenth = facesArray.length;
//        for (int i = 0; i < Face_lenth; i++){
//            // 인식한 얼굴 네모 테두리를 초록색으로 그려준다
//            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
//
//        }
//
////
////        bitmap = Utils.matToBitmap(mat,bitmap);
////        data = bitmapToByteArray(bitmap);
////
////        // Write the image in a file (in jpeg format)
////        try {
////            FileOutputStream fos = new FileOutputStream(mPictureFileName);
////
////            fos.write(data);
////            fos.close();
////
////        } catch (java.io.IOException e) {
////            Log.e("PictureDemo", "Exception in photoCallback", e);
////        }
//        return mRgba;
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        Log.i(TAG, "called onCreateOptionsMenu");
//        mItemFace50 = menu.add("Face size 50%");
//        mItemFace40 = menu.add("Face size 40%");
//        mItemFace30 = menu.add("Face size 30%");
//        mItemFace20 = menu.add("Face size 20%");
//        mItemType   = menu.add(mDetectorName[mDetectorType]);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
//        // 최소 얼굴 크기 설정
//        if (item == mItemFace50)
//            setMinFaceSize(0.5f);
//        else if (item == mItemFace40)
//            setMinFaceSize(0.4f);
//        else if (item == mItemFace30)
//            setMinFaceSize(0.3f);
//        else if (item == mItemFace20)
//            setMinFaceSize(0.2f);
//        else if (item == mItemType) {
//            int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
//            item.setTitle(mDetectorName[tmpDetectorType]);
//            setDetectorType(tmpDetectorType);
//        }
//        return true;
//    }
//
//    // 최소 얼굴 크기 설정
//    private void setMinFaceSize(float faceSize) {
//        mRelativeFaceSize = faceSize;
//        mAbsoluteFaceSize = 0;
//    }
//
//    private void setDetectorType(int type) {
//        if (mDetectorType != type) {
//            mDetectorType = type;
//
//            if (type == NATIVE_DETECTOR) {
//                Log.i(TAG, "Detection Based Tracker enabled");
//                mNativeDetector.start();
//            } else {
//                Log.i(TAG, "Cascade detector enabled");
//                mNativeDetector.stop();
//            }
//        }
//    }
//
//
//    public byte[] bitmapToByteArray( Bitmap $bitmap ) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
//        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
//        byte[] byteArray = stream.toByteArray() ;
//        return byteArray ;
//    }
//}
//
//
//
//// Activity 클래스를 상속받은 FdActivity
////public class FdActivity extends Activity
////{
////    // 이 엑티비티의 이름
////    private static final String TAG         = "Sample::Activity";
////
////    // 얼굴 크기를 설정하는 메뉴 맴버변수 (ex 얼굴이 화면의 50%일때 검출 가능)
////    private MenuItem            mItemFace50;
////    private MenuItem            mItemFace40;
////    private MenuItem            mItemFace30;
////    private MenuItem            mItemFace20;
////
////    // Java 모드와 Native 모드를 설정하는 메뉴버튼 (근데 자바일때가 더 빠르다? 현재 작동방식을 나타내는걸까나...)
////    private MenuItem            mItemType;
////
////    // 뷰!
////    private FdView    mView;
////
////    // 자바에서는 콜백함수(가 아니고 클래스)를 이렇게도 만들 수 있구나...
////    // 로더콜백 에 대해서 필요할 경우 조사해 볼 것
////    private BaseLoaderCallback  mOpenCVCallBack = new BaseLoaderCallback(this)
////    {
////        @Override
////        public void onManagerConnected(int status)
////        {
////            // 메니저에 접근하여 상태 변수값을 받아온 뒤 switch 문으로 처리
////            switch (status)
////            {
////                // 로드 콜백 인터페이스 성공
////                case LoaderCallbackInterface.SUCCESS:
////                {
////                    // 로그에 기록 (자바 로그 클래스 인가?)
////                    Log.i(TAG, "OpenCV loaded successfully");
////
////                    // Load native libs after OpenCV initialization
////                    // OpenCV 를 초기화 한 뒤 라이브러리를 로드한다. (로드 코드는 BetectionBasedTracker.java 에도 있는데?)
////                    System.loadLibrary("detection_based_tracker");
////
////                    // Create and set View
////                    // 뷰를 생성하고 세팅한다
////                    mView = new FdView(mAppContext);    // 콘텍스트에 뷰 세팅하여 생성
////                    mView.setDetectorType(mDetectorType);  // 그냥 정수형 맴버변수
////                    mView.setMinFaceSize(0.2f);        // 초기 최소 얼굴 크기는 0.2f
////                    setContentView(mView);          // 셋 콘텍스트
////
////                    // Check native OpenCV camera
////                    if( !mView.openCamera() )
////                    {
////                        // 뷰에서 오픈 카메라가 실패할 경우
////                        // 실패 다이얼로그를 띄우고 프로그램을 종료한
////                        AlertDialog ad = new AlertDialog.Builder(mAppContext).create();
////                        ad.setCancelable(false); // This blocks the 'BACK' button
////                        ad.setMessage("Fatal error: can't open camera!");
////                        ad.setButton("OK", new DialogInterface.OnClickListener()
////                        {
////                            public void onClick(DialogInterface dialog, int which)
////                            {
////                                dialog.dismiss();
////                                finish();
////                            }
////                        });
////                        ad.show();
////                    }
////                }
////                break;
////
////                // 현재는 스위치 케이스 문
////                default:
////                {
////                    super.onManagerConnected(status);
////                }
////                break;
////            }
////        }
////    };
////
////
////    private int                 mDetectorType = 0;    // 디텍터 타입
////    private String[]            mDetectorName;       // 디텍터 이름의 배열 (매뉴바에 이름을 띄우기 위한 용도일 뿐)
////
////
////    // 생성자
////    public FdActivity()
////    {
////        // 로그에 기록
////        Log.i(TAG, "Instantiated new " + this.getClass());
////
////        // 디텍터 이름은 2개
////        mDetectorName = new String[2];
////
////        // 각각 자바 디텍터, 네이티브 디텍터 라는 이름을 가진다.
////        mDetectorName[FdView.JAVA_DETECTOR] = "자바";
////        mDetectorName[FdView.NATIVE_DETECTOR] = "네이티브";
////    }
////
////    @Override
////    protected void onPause()
////    {
////        // 포즈 걸리면 카메라 릴리즈
////        Log.i(TAG, "onPause");
////        super.onPause();
////        if (mView != null)
////            mView.releaseCamera();
////    }
////
////    @Override
////    protected void onResume()
////    {
////        // 살아나면
////        // 로그에 기록하고 슈퍼에 리섬을 호출
////        Log.i(TAG, "onResume");
////        super.onResume();
////
////        // 만약 뷰가 살아있으면서 오픈카메라가 실패하면
////        if( mView != null && !mView.openCamera() )
////        {
////            // 다이얼로그 생성하고 에러메시지 띄우고 종료
////            AlertDialog ad = new AlertDialog.Builder(this).create();
////            ad.setCancelable(false); // This blocks the 'BACK' button
////            ad.setMessage("Fatal error: can't open camera!");
////            ad.setButton("OK", new DialogInterface.OnClickListener() {
////                public void onClick(DialogInterface dialog, int which) {
////                    dialog.dismiss();
////                    finish();
////                }
////            });
////            ad.show();
////        }
////    }
////
////    // 이 액티비티가 처음으로 생성될때 불리는 함수
////    // 아마 생성자라고 생각해도 될 것 같다
////    /** Called when the activity is first created. */
////    @Override
////    public void onCreate(Bundle savedInstanceState)
////    {
////        Log.i(TAG, "onCreate");
////        super.onCreate(savedInstanceState);
////
////        // 타이틀바를 없엔 화면 출력 (시계창까지 없애는 건 아님)
////        requestWindowFeature(Window.FEATURE_NO_TITLE);
////
////        Log.i(TAG, "Trying to load OpenCV library");
////
////        // OpenCV로더 클래스에 OpenCV의 초기화를 요청함 (3번째 인자로 콜백함수를 받음)
////        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack))
////        {
////            Log.e(TAG, "Cannot connect to OpenCV Manager");
////        }
////    }
////
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu)
////    {
////        // 메뉴바 생성
////        Log.i(TAG, "onCreateOptionsMenu");
////        mItemFace50 = menu.add("얼굴크기 50%");
////        mItemFace40 = menu.add("얼굴크기 40%");
////        mItemFace30 = menu.add("얼굴크기 30%");
////        mItemFace20 = menu.add("얼굴크기 20%");
////        mItemType   = menu.add(mDetectorName[mDetectorType]);
////
////        return true;
////    }
////
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item)
////    {
////        // 선택된 메뉴에 따라 얼굴 인지 크기가 바뀜
////        Log.i(TAG, "Menu Item selected " + item);
////        if (item == mItemFace50)
////            mView.setMinFaceSize(0.5f);
////        else if (item == mItemFace40)
////            mView.setMinFaceSize(0.4f);
////        else if (item == mItemFace30)
////            mView.setMinFaceSize(0.3f);
////        else if (item == mItemFace20)
////            mView.setMinFaceSize(0.2f);
////
////        else if (item == mItemType)
////        {
////            // mDetectorType을 1을 더하고 2로 나눈 나머지를 취한다 (결국 1과 0, 결국 boolean 변수처럼 동작시키겠다는 의미)
////            mDetectorType = (mDetectorType + 1) % mDetectorName.length;
////
////            // 이름 변경
////            item.setTitle(mDetectorName[mDetectorType]);
////
////            // 실질적인 동작방식 전환은 View 클래스에 의해 이루어진다
////            mView.setDetectorType(mDetectorType);
////        }
////        return true;
////    }
////
////
////}
