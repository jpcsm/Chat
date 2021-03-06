package com.test.js.chat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.test.js.chat.facedetect.DetectionBasedTracker;
//import com.test.js.chat.facedetect.FdActivity;
//
//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.android.Utils;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//import org.opencv.core.Rect;
//import org.opencv.core.Size;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.OkHttpClient;

//import static com.test.js.chat.facedetect.DetectionBasedTracker.loadImage;

//
public class MyProfileSettingActivity extends AppCompatActivity {

    private String imgPath = "";
    //?????? ?????? ??????
    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
    private static final int MULTIPLE_PERMISSIONS = 101; //?????? ?????? ?????? ?????? ??? CallBack ????????? ?????? ??????

    final static String TAG = "OCV:MyProfileSetting ";
    static int TAKE_PICTURE = 12343;
    static int PICK_FROM_ALBUM = 242;
    ImageButton imBtnMyPorofile, MyChat;

    SharedPreferences sp;//??????????????????  MyProfileImageURI
    SharedPreferences.Editor ed;
    Uri mImageUri;

    Context context;


    String absolutePath;
//    private DetectionBasedTracker mNativeDetector;
//    Mat mRgb;

//    private Uri getFileUri() {
//        File dir = new File(getExternalFilesDir(null), "img" );
//        Log.e("getFileUri", "getFilesDir()="+getExternalFilesDir(null));
//        if ( !dir.exists() ) {
//            dir.mkdirs();
//        }
//        File file = new File( dir, System.currentTimeMillis() + ".png" );
//        imgPath = file.getAbsolutePath();
//        Log.e("getFileUri", "file path="+imgPath);
////        getApplicationContext().getPackageName() + ".fileprovider"
//        return FileProvider.getUriForFile( this, "com.test.js.chat.fileprovider", file );
//    }

    //    private Uri getCaptureimageUri() {
//        String fileName = Environment.getExternalStorageDirectory().getPath() +
//                "/Captureimage_" + currentDateandTime + ".jpg";
//        Uri uri = Uri.fromFile(new File(fileName));
//
//
//    }
    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) { //???????????? ?????? ????????? ????????? ?????? ?????? ?????? ???????????? ?????? ????????? ??????
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) { //????????? ?????????????????? ?????? ???????????? empty??? ???????????? request ??? ????????? ???????????????.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    Boolean AgreePermission;

    //?????? ????????? ????????? ?????? ????????? ?????? ?????? Toast ???????????? ????????? ?????? Activity??? ??????????????????.
    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "?????? ????????? ?????? ???????????? ?????? ???????????????. ???????????? ?????? ?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
        //finish();
    }

    Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_setting);

        context = this;
        imBtnMyPorofile = (ImageButton) findViewById(R.id.Btn_ProfileImageGhange);

        MyChat = (ImageButton) findViewById(R.id.imBtn_Mychat);
        checkPermissions(); //????????? ????????????


        //???????????? ??????
//        Glide.with(this).load("/img/ad_"+(pos+1)+".jpg")
//                .bitmapTransform(new CropCircleTransformation(getContext()))
//                .into(smallImage);

        //????????? ???????????? ?????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            MyChat.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                MyChat.setClipToOutline(true);
            }
        }

        //?????????????????? ?????????????????? ???????????? ??????
        imBtnMyPorofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items;
                items = new CharSequence[]{"?????? ??????(opencv)", "???????????? ?????? ??????", "?????? ???????????? ??????", "????????????"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // ????????????
                //alertDialogBuilder.setTitle("?????????");
                alertDialogBuilder.setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                //Toast.makeText(getContext(),"position : "+getArguments().getInt("H_num"), Toast.LENGTH_SHORT).show();
                                switch (id) {
                                    case 0:

                                        // ?????? ??????
//                                        sp = getSharedPreferences("chat", Activity.MODE_PRIVATE);//??????????????????  MyProfileImageURI
//                                        ed = sp.edit();
//                                        ed.remove("MyProfileImageURI");
//                                        ed.commit();//????????? ????????? ??????
//
//                                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                            File dir = new File(Environment.getExternalStorageDirectory().getPath(), "/img");
//                                            if (!dir.exists()) {
//                                                dir.mkdirs();
//                                            }
//
//                                            File file = new File(dir, "Captureimage_" + System.currentTimeMillis() + ".jpg");
//
//                                            photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.test.js.chat.fileprovider", file);
//
//                                            intent1.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                                        } else {
//                                            // intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPath)));
//                                        }
//                                        startActivityForResult(intent1, TAKE_PICTURE);

                                        //opencv
//                                        Intent intent2 = new Intent(getApplicationContext(),OpencvCameraViewActivity.class);
//                                        startActivity(intent2);
                                        break;
                                    case 1:
                                        // ?????? ??????
                                        Intent intent = new Intent(Intent.ACTION_PICK);
                                        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

                                        //intent.putExtra("pos",position);
                                        startActivityForResult(intent, PICK_FROM_ALBUM);
                                        break;
                                    case 2: //?????? ???????????? ??????
                                        sp = getSharedPreferences("chat", Activity.MODE_PRIVATE);//??????????????????  MyProfileImageURI
                                        ed = sp.edit();
                                        ed.remove("MyProfileImageURI");
                                        ed.commit();//????????? ????????? ??????
                                        mImageUri = null;
                                        imBtnMyPorofile.setImageResource(R.drawable.userimagechange);// ?????????????????? ??????
//                                        Intent intent2 = new Intent(getContext(), Tutorial3Activity.class);
//                                        startActivity(intent2);
                                        break;
                                    case 3 :
                                        //opencv
//                                        Intent intent3 = new Intent(getApplicationContext(),FdActivity.class);
//                                        startActivity(intent3);
                                        break;
                                }
                                // ??????????????? ??????
                                dialog.dismiss();
                            }
                        });
                // ??????????????? ??????
                AlertDialog alertDialog = alertDialogBuilder.create();

                Animation anim = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
                );
                anim.setDuration(1000);

                // ??????????????? ????????????
                alertDialog.show();
            }
        });

    }

    String ServerUri = "http://jpcsm9003.vps.phps.kr";

    @Override
    protected void onResume() {
        super.onResume();

//        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
////            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
//        } else {
//            Log.d(TAG, "OpenCV library found inside package. Using it!");
////            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }


        SharedPreferences sf = getSharedPreferences("ChatUserID", Activity.MODE_PRIVATE);
        sp = getSharedPreferences("chat", Activity.MODE_PRIVATE);//??????????????????  MyProfileImageURI
        ed = sp.edit();
        String str_uri = sp.getString("MyProfileImageURI", null);
        if (str_uri != null) {
            mImageUri = Uri.parse(str_uri); //????????? ????????????????????? ???????????? ????????????
            imBtnMyPorofile.setImageURI(mImageUri);

            Glide.with(this).load(mImageUri)
//                    .bitmapTransform(new CropCircleTransformation(MyProfileSettingActivity.this))
                    .into(imBtnMyPorofile);
        } else {//????????? ???????????? ?????? ?????? ?????? ???????????? ????????????
            Glide.with(context).load(ServerUri + "/uploads/" + sf.getString("userid",null) + ".jpg")
                    //?????????????????? ??????
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)// ????????? ?????? ?????? off
//                    .skipMemoryCache(true)// ????????? ?????? ?????? off
                    .error(R.drawable.user3) //????????? ????????? ???????????? ??? ???, ???????????? ?????? ???????????? ?????????????????? ????????? ??? ?????? ??? ???????????? ???????????????.
//                    .bitmapTransform(new CropCircleTransformation(MyProfileSettingActivity.this))
                    .into(imBtnMyPorofile);

            imBtnMyPorofile.setImageResource(R.drawable.userimagechange);
        }


    }


    private File mCascadeFile;
//    private CascadeClassifier mJavaDetector;
    
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS:
//                {
//                    Log.e(TAG, "OpenCV loaded successfully");
//                    //Toast.makeText(MyProfileSettingActivity.this, "OpenCV loaded successfully", Toast.LENGTH_SHORT).show();
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
//                            Log.e(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
//
//                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
//
//                        cascadeDir.delete();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
//                    }
//
//                    //mOpenCvCameraView.enableView();
//                } break;
//                default:
//                {
//                    super.onManagerConnected(status);
//                } break;
//            }
//        }
//    };
    Uri Cropuri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sp = getSharedPreferences("chat", Activity.MODE_PRIVATE);//??????????????????  MyProfileImageURI
        ed = sp.edit();

        if (requestCode == PICK_FROM_ALBUM) { //???????????? ????????? ????????????, ????????????
            if (data != null) {
              //  Log.e(TAG, "data.getData() : " + data.getData() + "\ndata.getExtras() : " + data.getExtras());
                //???????????????uri ???????????? ??????
                mImageUri = data.getData();

                if (mImageUri != null) {
                   // Log.e(TAG, "data.getData().toString() : " + data.getData().toString());
//                    ed.putString("MyProfileImageURI", "" + mImageUri.toString());
//                    ed.commit();
                    if (data.getExtras() != null) {
                       // Log.d(TAG, "" + data.getExtras().toString());
                        //Toast.makeText(this, "data.getData().toString() : " + data.getData().toString(), Toast.LENGTH_SHORT).show();
                    }


                    //??????????????? ??????
                    absolutePath = Uri_To_AbsolutePath(mImageUri);

//                    mRgb = new Mat();
                    //Bitmap photo = data.getExtras().getParcelable("data");
                    Bitmap photo = DecodeBitmapFile(absolutePath);
//                    Mat p =  Utils.bitmapToMat(photo,mRgb);

//                    imBtnMyPorofile.setImageBitmap(bmp);
//                    mRgb = new Mat();
//                    mRgb = Utils.bitmapToMat(bmp,mRgb);
                    String filename =  Environment.getExternalStorageDirectory().getPath() +"/"+
                            getImageNameToUri(data.getData());
                    //Log.e(TAG,"filename : "+filename);
                    //mRgb = read_image_file(filename);

                    //???????????????
                    //IMREAD_UNCHANGED ??????????????? ???????????? ???????????? ?????? ????????? ???????????????.

//                    mRgb = Imgcodecs.imread(absolutePath,Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);

                    //??????????????? mat?????? ??????
                    //Mat mGray = Imgcodecs.imread(absolutePath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    //??????????????? ????????????????????????
//                    MatOfRect faces = new MatOfRect();
//                    if (mNativeDetector != null)
//                        mNativeDetector.detect(mGray, faces);
//                    if (mJavaDetector != null)
//                        mJavaDetector.detectMultiScale(mRgb, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
//                                new Size(0, 0), new Size());
//                    Log.e(TAG,"faces.toArray().length : "+faces.toArray().length);
//                    ab:if(faces.toArray().length==1){ //????????? ????????? 1??? ???



                        //????????? ?????? ??????????????? ??????
//                        Rect[] facesArray = faces.toArray();
//                        Rect rectCrop =new Rect(facesArray[0].x,facesArray[0].y,facesArray[0].width,facesArray[0].height);
//                        if(rectCrop==null) {
//                            Log.e(TAG,"rectCrop==null");
//                            break ab;
//                        }
//                        Mat CropMat = new Mat(p ,rectCrop);
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss"); // ????????????
//                        String currentDateandTime = sdf.format(new Date());
//                        String pathname = Environment.getExternalStorageDirectory()+"/rectCrop_"+currentDateandTime+".jpg";
//                        //Imgcodecs.imwrite(fileName,CropMat); //??????
//                        //??????: http://huky3324.tistory.com/entry/java-opencv-??????????????? [?]
//
//
//                        //??????????????????->?????????->byte[]->FileOutputStream
//                        // ???????????? ????????? mRgba??? ??????, ???????????? ????????? Bitmap?????? ?????? (?????? ????????? ??????????????? ????????????)
//                        Bitmap bmp = Bitmap.createBitmap(CropMat.cols(), CropMat.rows(), Bitmap.Config.ARGB_8888);
//                        Bitmap bitmap = Utils.matToBitmap(CropMat,bmp);
//                        byte[] arr = bitmapToByteArray(bitmap);
//
//                        // Write the image in a file (in jpeg format)
//                        try {
//                            FileOutputStream fos = new FileOutputStream(pathname);
//
//                            fos.write(arr);
//                            fos.close();
//
//                        } catch (java.io.IOException e) {
//                            Log.e(TAG, "??????????????????", e);
//                        }
//

                        // ????????? ???????????? ?????? ???????????????????????? ???????????????.KitKat(4.4) ???????????? ?????? ??????.
                        // CPU ???????????? ?????? ????????????, ????????? ?????? ??????.
//                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                                Uri.parse("file://"+Environment.getExternalStorageDirectory()+"/"+pathname)));

//                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));
//
//                        Cropuri = Uri.parse("file://"+pathname);// file Uri
//                        Log.e(TAG, "???????????? ??????\nCropuri.getPath() : "+Cropuri.getPath()+
//                        "\npathname : "+pathname);
//
//
//                        //????????? ?????????????????? ??????
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                String CropPath = Cropuri.getPath();
//                                uploadFile(CropPath);
//                            }
//                        }).start();
//
//
//                        ed.putString("MyProfileImageURI",Cropuri.toString());
//                        ed.commit();
//
//
//                    }else if(faces.toArray().length==0){
//                        Toast.makeText(context, "????????? ????????? ????????? ???????????? ?????????????????????", Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        Toast.makeText(context, "??? ?????? ????????? ???????????? ????????? ??? ????????????", Toast.LENGTH_SHORT).show();
//                    }
//                    Log.e(TAG,"faces.toArray().length : "+faces.toArray().length+"\nmRgb.empty() : "+mRgb.empty()+"\nabsolutePath : "+absolutePath);
//
//



                    //Log.d("getPathtoString", mImageUri.getPath().toString());

//
//                Glide.with(this).load(mImageUri)
//                        .bitmapTransform(new CropCircleTransformation(MyProfileSettingActivity.this))
//                        .into(imBtnMyPorofile);
                } else {
                    Log.e("mImageURi", "" + mImageUri);
                }




            }
        } else if (requestCode == TAKE_PICTURE) {
            //data.getData();
//            File dir = new File(Environment.getExternalStorageDirectory().getPath(), "/img");
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//
//            File file = new File(dir, "Captureimage_.jpg");
//
//            photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.test.js.chat.fileprovider", file);


            Log.e("photoURI", "data == null \n" + photoURI.toString());
            ed.putString("MyProfileImageURI", "" + photoURI.toString());
            ed.commit();
        }
    }

//    public void loadImage(String path){
//        Mat originalImage= Imgcodecs.imread(path);
//        Mat rgbImage=new    Mat();
//        Imgproc.cvtColor(originalImage, rgbImage,   Imgproc.COLOR_BGR2RGB);
//
////to reshape the image.
//        Display display =   getWindowManager().getDefaultDisplay();
//        Point   size    =   new Point();
//        display.getSize(size);
//        int width=size.x;
//        int height=size.y;
//        sampledImage=new Mat();
//        double  downSampleRatio=calculateSubSampleSize(rgbImage,width,height);
//        Imgproc.resize(rgbImage,sampledImage,new Size(),downSampleRatio,downSampleRatio,Imgproc.INTER_AREA);
//
////to rotate the image,seems not a problem in this case.
//        try {
//            ExifInterface exif    =   new ExifInterface(selectedImagePath);
//            int orientation =   exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//            switch (orientation){
//                case    ExifInterface.ORIENTATION_ROTATE_90:
//                    sampledImage=sampledImage.t();
//                    Core.flip(sampledImage, sampledImage,   1);
//                    break;
//                case    ExifInterface.ORIENTATION_ROTATE_270:
//                    sampledImage=sampledImage.t();
//                    Core.flip(sampledImage, sampledImage,   0);
//                    break;}
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }
    public String Uri_To_AbsolutePath(Uri ImageUri){ // contentUri??? ?????? FileUri??????
        //??????????????? ??????
        Log.e(TAG,"ImageUri.toString() : "+ImageUri.toString());
        Cursor c = getContentResolver().query(Uri.parse(ImageUri.toString()), null, null, null, null);
        c.moveToNext();
        absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
        return absolutePath ;
    }
public byte[] bitmapToByteArray( Bitmap $bitmap ) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
    $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
    byte[] byteArray = stream.toByteArray() ;
    return byteArray ;
}

    //Uri ?????? ???????????? ???????????? ??????
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }

    // ????????? ?????? ????????? ????????? ????????????
    private Bitmap DecodeBitmapFile(String strFilePath) {
        final int IMAGE_MAX_SIZE = 1024;
        File file = new File(strFilePath);
        if (file.exists() == false) {return null;}
        BitmapFactory.Options bfo = new BitmapFactory.Options();

        bfo.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(strFilePath, bfo);
        if (bfo.outHeight * bfo.outWidth >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE) {
            bfo.inSampleSize = (int) Math.pow(2,
                    (int) Math.round(Math.log(IMAGE_MAX_SIZE
                            / (double) Math.max(bfo.outHeight, bfo.outWidth))
                            / Math.log(0.5)));
        }
        bfo.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(strFilePath, bfo);
        return bitmap;
    }
//    Mat img_input;
//    Mat img_output;
//    private Mat read_image_file(String filename) {
//
//        img_input = new Mat();
//        img_output = new Mat();
//
//        loadImage(filename, img_input.getNativeObjAddr());
//
//        return img_output;
//    }

    int serverResponseCode = 0;
    // ProgressDialog dialog = null;

    String upLoadServerUri = "http://jpcsm9003.vps.phps.kr/UploadImage.php";

    /**********  File Path *************/
    final String uploadFilePath = "storage/emulated/0/";//????????? ???????????????, ????????? ?????????????????? ?????? ??????->?????? ??????
    final String uploadFileName = "testimage.jpg"; //????????????????????? ?????? ??????

    ProgressDialog dialog = null;

    public int uploadFile(String sourceFileUri) {

//        dialog = new ProgressDialog(this); // ?????????????????????
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        dialog.setMessage("????????? ??????????????????...");

        //????????? ????????? ?????????
        SharedPreferences sf = getSharedPreferences("ChatUserID", Activity.MODE_PRIVATE);
        String MyID = sf.getString("userid", "");

        String fileName = MyID + ".jpg";//????????????
        Log.e("fileName", fileName);

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;


        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
//
            Log.e("uploadFile ?????? ", "sourceFileUri :" + sourceFileUri + "\n" + sourceFile);
            //dialog.dismiss();

            //+uploadFilePath + "" + uploadFileName);
//
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    messageText.setText("Source File not exist :"
//                            +uploadFilePath + "" + uploadFileName);
//                }
//            });
            return 0;

        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());
//                DataInputStream dis = new DataInputStream(conn.getInputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                //?????? ???????????????
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
//                        +"\ndis : "+dis.readUTF());

//                if(serverResponseCode == 200){
//
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//
//                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
//                                    +uploadFileName;
//
//                            messageText.setText(msg);
//                            Toast.makeText(MainActivity.this, "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                //dialog.dismiss();
                ex.printStackTrace();

//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        messageText.setText("MalformedURLException Exception : check script url.");
//                        Toast.makeText(MainActivity.this, "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                //dialog.dismiss();
                e.printStackTrace();

//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        messageText.setText("Got Exception : see logcat ");
//                        Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
                Log.e("Upload file", "Exception : " + e.getMessage(), e);
            }
            // dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

}


class okhttp3 extends Thread {
    URL url;
    String data;
    Boolean isReturn;

    public void okhttp3(Context context, String imageuri, Boolean isReturn) {
        this.isReturn = isReturn;
        try {
            this.url = new URL("jpcsm9003.vpsd.phps.kr:22/var/www/html/" + imageuri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    final OkHttpClient client = new OkHttpClient();
//    @Override
//    public void run() {
//        super.run();
//
//
//        RequestBody body = new FormBody.Builder()
//                .add("data", data)
//                .build();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//               // .post(RequestBody.create(okhttp3.MediaType.parse("application/json"), json))
//                .build();
//
//
//        Response response = null;
//
//        if(isReturn){// ????????? Id??? ?????? ????????? php??? json ????????? ??????????????? ????????? ????????????
//            // callback??? ??????????????????. execute??? ???????????? ????????? ??????.
//            client.newCall(request).enqueue(callback);
//        } else {//????????? ?????????????????? .execute()??? ??????
//            try {
//                response = client.newCall(request).execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        Log.d("request : ",request.toString());
//        Log.d("Response : ",response.toString());
//
//
//    }

//    String myJSON;
//    private Callback callback = new Callback() {
//        @Override
//        public void onFailure(Call call, IOException e) {
//
//        }
//
//        @Override
//        public void onResponse(Call call, Response response) throws IOException {
//            myJSON = response.body().string();
//            //showList();
//        }
//    };
//    public  String uploadPhoto(File file) {
//        try {
////            RequestBody requestBody = new okhttp3.MultipartBody().Builder()
////                    .setType(okhttp3.MultipartBody.FORM)
////                    .addFormDataPart("photo", "photo.png", RequestBody.create(okhttp3.MediaType.parse("image/png"), file))
////                    .build();
//            //RequestBody requestBody = new okhttp3.MultipartBody.Builder();
//
//
////            Multipart m = new okhttp3.Multipart.Builder()
////                    .type(okhttp3.Multipart.Type.FORM)
////                    .addPart(new okhttp3.MultipartBody.Part.Builder()
////                            .body("value")
////                            .contentDisposition("form-data; name=\"non_file_field\"")
////                            .build())
////                    .addPart(new okhttp3.MultipartBody.Part.Builder()
////                            .contentType("text/csv")
////                            .body(aFile)
////                            .contentDisposition("form-data; name=\"file_field\"; filename=\"file1\"")
////                            .build())
////                    .build();
////            RequestBody body = new FormBody.Builder()
////                    .add("data", data)
////                    .build();
//            String url = "http://...";
//            Request request = new Request.Builder()
//                    .url(url)
//                    //.post(requestBody)
//                    .build();
//
//            OkHttpClient client = new OkHttpClient();
//            Response response = client.newCall(request).execute();
//            return response.body().string();
//        } catch (UnknownHostException | UnsupportedEncodingException e) {
//
//        } catch (Exception e) {
//
//        }
//        return null;
//    }

}
