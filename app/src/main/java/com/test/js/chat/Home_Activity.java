package com.test.js.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.test.js.chat.Home.HomeFragment.Friend_invite_Activity;
//import com.test.js.chat.Live_Streaming.VideoRecordingActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 메인 페이지
public class Home_Activity extends AppCompatActivity {
    ViewPager tab_viewPager;
    TabPagerAdapter tabPagerAdapter;
    SQLite_RoomList RoomList_DB;
    static String email;
    public static String MyID;

    //권한 설정 변수
    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS  //  주소록권한
    };

    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) { //권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
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
                        }else if (permissions[i].equals(this.permissions[3])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }else if (permissions[i].equals(this.permissions[4])) {
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

    //권한 획득에 동의를 하지 않았을 경우 아래 Toast 메세지를 띄우며 해당 Activity를 종료시킵니다.
    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        //finish();
    }

    static Messenger mService = null;

    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean mBound;


    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    private static final String TAG = "ServiceTestActivityWithMessenger";
    private static final int MSG_REGISTER_CLIENT = 44;
    private static final int MSG_SET_VALUE = 56;
    private static final int MSG_UNREGISTER_CLIENT = 77;
    private static boolean mIsBound = false;


    class IncomingHandler extends Handler {
        @SuppressLint("LongLogTag")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SocketService.MSG_SET_VALUE:
                    Log.d(TAG, msg.arg1 + "");
                    msg.obj.toString();
                    // Toast.makeText(getApplicationContext(), "Home_Activity : " + msg.obj.toString(), Toast.LENGTH_SHORT).show();

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBound = true;
            //Toast.makeText(getApplicationContext(), "onServiceConnected", Toast.LENGTH_SHORT).show();
            mService = new Messenger(service);
            Log.d("onServiceConnected", "onServiceConnected()");

            try {
                Message msg = Message.obtain(null, MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
                // sendToService("data");
            } catch (RemoteException e) {
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            Log.d("onServiceDisconnected", "onServiceDisconnected()");
            mService = null;
            mBound = false;
        }
    };

    public static void sendToService(int type, String str) { //서비스로 데이터를 보내는 메소드
        //if (!mBound) return;
        Log.e("서버로 메세지 전송 ",str);
        //Toast.makeText(getApplicationContext(), " sayHello()", Toast.LENGTH_SHORT).show();

        //Message객체에 String오브젝트를 넣어서 서비스로 전송한다
        Message msg = Message.obtain(null, type, 0, 0, new String(str));
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    String result= "";
    String json= "";
    FloatingActionButton FBtn_Add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);

        checkPermissions(); // 권한체크
        //유저정보 가져옴
        MyID = getIntent().getStringExtra("userid");//유저아이디
        email = getIntent().getStringExtra("email");//유저이메일


        // //소켓생성 - 메인에서 생성시 에러발생
//        clientChat = new ClientChat(this,MyID); // context를 스레드로 넘겨준다
//        clientChat.start();
        //ClientSocketService.socket = clientChat.socket;


        SharedPreferences sf = getSharedPreferences("ChatUserID", Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sf.edit();

        ed.putString("userid", MyID);
        ed.commit();

        // SQLite 객체생성
        RoomList_DB = new SQLite_RoomList(getApplicationContext(), "chat.db", null, 1);

        //탭뷰페이저 생성
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        tab_viewPager = (ViewPager) findViewById(R.id.home_viewpager);
        tab_viewPager.setAdapter(tabPagerAdapter);
        TabLayout mTab = (TabLayout) findViewById(R.id.tabs);
        mTab.setupWithViewPager(tab_viewPager);
        // tab_viewPager.setOnPageChangeListener();
        // rf.onResume();
//        tab_viewPager.
//        GotoChattingRoomActivity RoomAct = (GotoChattingRoomActivity) getFragmentManager().findFragmentById(R.id.f);
//        RoomAct.favoritesButton();
                //default 홈일 때 버튼리스너 - 프로젝트생성

                onPageProject(); //디폴트 : 프로젝트 프래그먼트일 때
                //탭이 바뀔때 버튼도 바뀜
                tab_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                switch (position){
                    case 0 : onPageProject(); //프로젝트 프래그먼트일 때
                        break;
                    case 1 : FBtn_Add = (FloatingActionButton)findViewById(R.id.Fbtn_Add);
                        FBtn_Add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(),Friend_invite_Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//다음 액티비티를 스택에 쌓지 않는다
                                startActivity(intent);
                            }
                        });FBtn_Add.setImageResource(R.drawable.addtalk);
                        break;
                    case 2 :FBtn_Add = (FloatingActionButton)findViewById(R.id.Fbtn_Add);
                        FBtn_Add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });FBtn_Add.setImageResource(R.drawable.new_user);
                        break;
                    case 3 :
//                        FBtn_Add = (FloatingActionButton)findViewById(R.id.Fbtn_Add);
//                        FBtn_Add.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(getApplicationContext(),VideoRecordingActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//다음 액티비티를 스택에 쌓지 않는다
//                                startActivity(intent);
//                            }
//                        });FBtn_Add.setImageResource(R.drawable.video_camera);
                        break;
                    default:
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });//기존 setOnPageChangeListener의 문제점은 listener를 하나만 사용할 수 있다는 것이다. 그래서 이번에 Deprecated되면서 여러개의 listener를 사용할 수 있도록 변경되었다.



        // Toast.makeText(getApplicationContext(), "onCreate", Toast.LENGTH_SHORT).show();

    }
    // 액티비티가 시작되면 서비스에 연결
    @Override
    protected void onStart(){
        super.onStart();
        //소켓서비스 바인드
        Intent intent = new Intent(this, SocketService.class);
        intent.putExtra("myid", MyID);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void onPageProject(){
        FBtn_Add = (FloatingActionButton)findViewById(R.id.Fbtn_Add);
        FBtn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProjectDialog(); //다이얼 로그 뛰우기
            }
        });FBtn_Add.setImageResource(R.drawable.file);

    }
    EditText et;
    public void AddProjectDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setTitle("새 프로젝트");       // 제목 설정
        // ad.setMessage("Message");   // 내용 설정

        // EditText 삽입하기
        et = new EditText(this);
        et.setHint("프로젝트명 입력");
        et.setText("프로젝트1");
        ad.setView(et);

        // 확인 버튼 설정
        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Log.v(TAG, "Yes Btn Click");

                // Text 값 받아서 로그 남기기
                String PROJECT_NAME = et.getText().toString();
                String PROJECT_ID = UUID.randomUUID().toString();  //고유식별ID 생성 - PROJECT_ID

                //TCP로 서버에 데이터 전송
                String toServerMsg = "AddProject" + "|" + PROJECT_NAME + "|" + MyID + "|" + PROJECT_ID + "|";// AddProject|ProjectName|admin|ProjectID

                //로컬DB에 채팅내용 저장
                //ChattingDB.insert(RoomID,MyID,msg,msgID,date);
                //서비스에 전송메세지 전달
                Home_Activity.sendToService(SocketService.MSG_SET_VALUE,toServerMsg);



                dialog.dismiss();     //닫기
                // Event
            }
        });

// 중립 버튼 설정
//        ad.setNeutralButton("What?", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //Log.v(TAG,"Neutral Btn Click");
//                dialog.dismiss();     //닫기
//                // Event
//            }
//        });

// 취소 버튼 설정
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Log.v(TAG,"No Btn Click");
                dialog.dismiss();     //닫기
                // Event
            }
        });

// 창 띄우기
        ad.show();
    }
    // 액티비티가 종료되면 서비스 연결을 해제
//    @Override
//    protected void onStop(){
//        super.onStop();
//        if(mBound){
//            unbindService(mConnection);
//            mBound = false;
//        }
//    }



}
