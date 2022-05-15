//package com.test.js.chat.facedetect;
//
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//
////
//
///*
// * jni 프레임워크의cpp로 작성된 라이브러리를 로드하고
// * 그 라이브러리에서 사용하는 함수들이 레핑된 클래스
// */
//
//
//public class DetectionBasedTracker
//{
//    // DetectionBasedTracker의 생성자 (케스케이드 이름, 최소 얼굴 사이즈)
//    public DetectionBasedTracker(String cascadeName, int minFaceSize)
//    {
//        // 네이티브 함수로 오브젝트를 생성하여 헨들을 받아온다
//        mNativeObj = nativeCreateObject(cascadeName, minFaceSize);
//    }
//
//    // 시작
//    public void start()
//    {
//        // 해당 핸들(아마도 디바이스)로 검출을 시작한다
//        nativeStart(mNativeObj);
//    }
//
//    // 멈춤
//    public void stop()
//    {
//        // 해당 핸들로 검출을 멈춘다
//        nativeStop(mNativeObj);
//    }
//
//    // 최소 얼굴 크기를 설정하는 함수
//    public void setMinFaceSize(int size)
//    {
//        // 단지 네이티브 함수의 렙핑함수일 뿐이다
//        nativeSetFaceSize(mNativeObj, size);
//    }
//
//    // 검출 (회색조 이미지의 행렬과 얼굴 범위를 인자값으로 가진다)
//    public void detect(Mat imageGray, MatOfRect faces)
//    {
//        // 네이티브 레핑
//        nativeDetect(mNativeObj, imageGray.getNativeObjAddr(), faces.getNativeObjAddr());
//    }
//
//    // 해제
//    public void release()
//    {
//        // 네이티브 오브젝트를 해재한다
//        nativeDestroyObject(mNativeObj);
//        mNativeObj = 0;
//    }
//
//    // 맴버변수 mNativeObj;
//    private long mNativeObj = 0;
//
//    // 각종 네이티브 메서드 들
//    private static native long nativeCreateObject(String cascadeName, int minFaceSize);
//    private static native void nativeDestroyObject(long thiz);
//    private static native void nativeStart(long thiz);
//    private static native void nativeStop(long thiz);
//    private static native void nativeSetFaceSize(long thiz, int size);
//    private static native void nativeDetect(long thiz, long inputImage, long faces);
//    public static native void loadImage(String imageFileName, long img);
//
//    // JNI 라이브러리를 로드한다
//    static
//    {
//        System.loadLibrary("detection_based_tracker");
//    }
//}
//
//
////public class DetectionBasedTracker
////{
////    public DetectionBasedTracker(String cascadeName, int minFaceSize) {
////        mNativeObj = nativeCreateObject(cascadeName, minFaceSize);
////    }
////
////    public void start() {
////        nativeStart(mNativeObj);
////    }
////
////    public void stop() {
////        nativeStop(mNativeObj);
////    }
////
////    // 최소 얼굴 크기를 설정하는 함수
////    public void setMinFaceSize(int size) {
////        nativeSetFaceSize(mNativeObj, size);
////    }
////
////    // 검출 (회색조 이미지의 행렬과 얼굴 범위를 인자값으로 가진다)
////    public void detect(Mat imageGray, MatOfRect faces) {
////        // 네이티브 레핑
////        nativeDetect(mNativeObj, imageGray.getNativeObjAddr(), faces.getNativeObjAddr());
////    }
////
////    public void release() {
////        nativeDestroyObject(mNativeObj);
////        mNativeObj = 0;
////    }
////
////    private long mNativeObj = 0;
////
////    private static native long nativeCreateObject(String cascadeName, int minFaceSize);
////    private static native void nativeDestroyObject(long thiz);
////    private static native void nativeStart(long thiz);
////    private static native void nativeStop(long thiz);
////    private static native void nativeSetFaceSize(long thiz, int size);
////    private static native void nativeDetect(long thiz, long inputImage, long faces);
////}