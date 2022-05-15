package com.test.js.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import static com.test.js.chat.MyProfileSettingActivity.PICK_FROM_ALBUM;
import static com.test.js.chat.MyProfileSettingActivity.TAKE_PICTURE;
import static com.test.js.chat.SocketService.ReceiveThread.MSG_FROM_SERVER;

public class GotoChattingRoomActivity extends AppCompatActivity {

    EditText et_chat;//채팅입력창
    Button bt_send;//전송버튼
    chat_item item;
    chatAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManger;
    Boolean NewRoom = false;
    String ServerIP = "jpcsm9003.vps.phps.kr";
    Socket socket;
    //ClientChat clientChat;
    String RoomName;
    String RoomUser;
    Date date; //생성날짜
    String RoomID, MyID;
    SQLite_RoomList RoomList_DB;
    String msg;
    String MsgID;
    private static final int SEND_THREAD_INFOMATION = 0;
    public static final int UPDATE_MSG = 1;

    static SendMassgeHandler mMainHandler = null;// 메인 핸들러
    //private CountThread mCountThread = null;
    String RoomState;
    SQLite_Chatting ChattingDB;
    static String NowRoomID;
    static String isSeeRoomID;
    LinearLayout Li_chat;
    // int readNotUser;   // 전체인원에서 나를 뺀 나머지 - 읽지않은 사람 수
    String readUsers;
    int MsgType;
    //boolean READ_CHATTING = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Log.e("GotoChattingRoom", "onCreate");


        Li_chat = (LinearLayout) findViewById(R.id.Li_chat); // 채팅어댑터 부모레이아웃

        //해당 방ID 가져오기
        NowRoomID = getIntent().getStringExtra("roomid");

        //SQLite 객체생성
        ChattingDB = new SQLite_Chatting(getApplicationContext(), "chatting.db", null, 1);

        //메인핸들러 객체생성
        mMainHandler = new SendMassgeHandler();// resiver스레드에서 서버에서 받은 메세지 가져옴

        //사용자 아이디 가져옴
        SharedPreferences sf = getSharedPreferences("ChatUserID", Activity.MODE_PRIVATE);
        MyID = sf.getString("userid", "");
        readUsers = MyID; // 내가 보낸 메세지 읽음 표시
        //SQLite 객체생성
        RoomList_DB = new SQLite_RoomList(getApplicationContext(), "chat.db", null, 1);

        et_chat = (EditText) findViewById(R.id.Et_chat);
        bt_send = (Button) findViewById(R.id.bt_send);

        //리사이클러뷰 생성
        mAdapter = new chatAdapter(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.Rv_chat);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mLayoutManger = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.setAdapter(mAdapter);

        //기존방인지 새로 생성된 방인지?
        NewRoom = getIntent().getBooleanExtra("newroom", false);
        RoomState = getIntent().getStringExtra("roomstate");
        Log.e("GotoChattingRoom", "RoomState : " + RoomState + "\nRoomState.equals(1:1) : " + RoomState.equals("1:1"));

        if (RoomState.equals("1:1")) {//친구목록 프래그머트에서 선택시 - 1대1채팅
            RoomName = getIntent().getStringExtra("roomname");// 방이름, 채팅상대 아이디 가져오기
            Log.d("RoomName", RoomName + "");
            //기존에 채팅내용이 있을경우 가져오기 위해서 방이름으로 방ID를 찾는다
            String[] RoomArr = RoomList_DB.select_RoomName(RoomName);
            Log.d("RoomArr", RoomArr[0] + " / " + RoomArr[1] + " / " + RoomArr[2]);
            if (RoomArr[1] != null) {
                NowRoomID = RoomArr[1];
                RoomID = RoomArr[1];
                Log.d("NowRoomID", NowRoomID + "");
            } else {
                NewRoom = true;
            }
        }

        //대화내용 입력후 전송버튼 클릭시
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!et_chat.getText().toString().trim().equals("")){ // 텍스트 내용이 있을 때 서버로 전송
                    msg = et_chat.getText().toString();
                    MsgType = 1; // 문자 전송
                    Calendar calendar = Calendar.getInstance(); // 현재시간
                    long now = calendar.getTimeInMillis();
                    date = new Date(now);
                    MsgID = UUID.randomUUID().toString();
                    if (RoomState.equals("Exist")) { // 기존방목록에서 클릭시 입장
                        String RoomName = getIntent().getStringExtra("roomname");
                        String[] RoomArr = RoomList_DB.select_RoomName(RoomName);
                        RoomUser = RoomArr[3];
                        int readNotUserSize = RoomUser.split(",").length - 1;
//                    RoomArr[1] // RoomID
//                    RoomArr[2] // RoomName
//                    RoomArr[3] // RoomUser
//                    RoomArr[4] // Date
                        //          RoomArr[0] idx//  RoomArr[1] RoomID// RoomArr[2] RoomName// RoomArr[3] : RoomUser RoomArr[4] Date
                        toServerMsg = "ExistRoom" + "|" + RoomArr[1] + "|" + msg + "|" + date + "|" + MyID + "|" + RoomUser + "|" + MsgType + "|" + null + "|" + readNotUserSize + "|" + readUsers + "|" + MsgID;


                        //고유식별ID 생성 - 방고유id
                        String msgID = UUID.randomUUID().toString();
                        //로컬DB에 채팅내용 저장
                        //ChattingDB.insert(RoomID,MyID,msg,msgID,date);
                        //서비스에 전송메세지 전달
                        Home_Activity.sendToService(SocketService.MSG_SET_VALUE, toServerMsg);

                        // clientChat.send_msg(toServerMsg); //서버에메세지 전송
                    } else if (RoomState.equals("1:1")) {//1대1 채팅 - 친구목록 프래그먼트에서 선택
                        RoomUser = MyID + "," + RoomName;
                        int readNotUserSize = RoomUser.split(",").length - 1;
                        if (NewRoom) {//새로운 방에서 채팅

                            RoomID = UUID.randomUUID().toString();
                            NowRoomID = RoomID;
                            isSeeRoomID = RoomID;
                            toServerMsg = "AddNewRoom" + "|" + RoomID + "|" + RoomName + "|" + RoomUser + "|" + msg + "|" + date + "|" + MyID + "|" + MsgType + "|" + null + "|" + readNotUserSize + "|" + readUsers + "|" + MsgID;
                            Home_Activity.sendToService(SocketService.MSG_SET_VALUE, toServerMsg);   //서비스에 전송메세지 전달
                            Log.d("NewRoom  1:1", NewRoom + "\n" + toServerMsg);
                            NewRoom = false;
                        } else { //기존방에서의 채팅일경우 - 방생성하지 않음

                            toServerMsg = "ExistRoom" + "|" + RoomID + "|" + msg + "|" + date + "|" + MyID + "|" + RoomUser + "|" + MsgType + "|" + null + "|" + readNotUserSize + "|" + readUsers + "|" + MsgID;
                            Home_Activity.sendToService(SocketService.MSG_SET_VALUE, toServerMsg);   //서비스에 전송메세지 전달
                            Log.d("NewRoom 1:1", NewRoom + "\n" + toServerMsg);
                        }

                    } else { // 친구추가후 채팅방 입장
                        if (NewRoom) {//새로운 방에서 채팅
                            isAddNewRoom(true);
                            NewRoom = false;
                        } else {
                            //기존방에서의 채팅일경우 - 방생성하지 않음
                            isAddNewRoom(false);
                        }
                    }

                    et_chat.setText("");//EditText초기화


                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);  // 리사이클러뷰  맨 아래로 내리기

                }

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        isSeeRoomID="";
        Log.e("onStop isSeeRoomID",isSeeRoomID);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSeeRoomID="";
        Log.e("onDestroy isSeeRoomID",isSeeRoomID);
    }

    public void ib_addfile(View v) {           // 파일첨부버튼 클릭리스너
        // 서비스에 filesender스레드 시작 요청
        final CharSequence[] items;
        items = new CharSequence[]{"사진 촬영", "앨범에서 사진 선택"};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // 제목셋팅
        //alertDialogBuilder.setTitle("프로필");
        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {

                        //Toast.makeText(getContext(),"position : "+getArguments().getInt("H_num"), Toast.LENGTH_SHORT).show();
                        switch (id) {
                            case 0:// 사진 촬영
//                                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//사진촬영
//                                        String url = "tmp_imsi.jpg";
//                                        //mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
////                                        intent1.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, getFileUri());
//                                        startActivityForResult(intent1, TAKE_PICTURE);
//                                sp = getSharedPreferences("chat", Activity.MODE_PRIVATE);//프로필이미지  MyProfileImageURI
//                                ed = sp.edit();
//                                ed.remove("MyProfileImageURI");
//                                ed.commit();//저장된 이미지 삭제

                                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    File dir = new File(Environment.getExternalStorageDirectory().getPath(), "/img");
                                    if (!dir.exists()) {
                                        dir.mkdirs();
                                    }

                                    File file = new File(dir, "Captureimage_" + System.currentTimeMillis() + ".jpg");

                                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.test.js.chat.fileprovider", file);

                                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                } else {
                                    // intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPath)));
                                }
                                startActivityForResult(intent1, TAKE_PICTURE);
                                break;
                            case 1:
                                // 앨범 호출
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

                                //intent.putExtra("pos",position);
                                startActivityForResult(intent, PICK_FROM_ALBUM);
                                break;

                        }
                        // 다이얼로그 종료
                        dialog.dismiss();
                    }
                });
        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        Animation anim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        anim.setDuration(1000);

        // 다이얼로그 보여주기
        alertDialog.show();


    }

    String ImageName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_ALBUM) { //앨범에서 이미지 가져오기, 사진촬영
            if (data != null) { // 사진선택시
                //해당이미지uri 쉐어드에 저장
                Uri mImageUri = data.getData();
                MsgType = 2;


                //서버로 메세지

//                toServerMsg = "ExistRoom" + "|" + RoomID + "|" + msg + "|" + date + "|" + MyID;
//                Home_Activity.sendToService(SocketService.MSG_SET_VALUE, toServerMsg);   //서비스에 전송메세지 전달

                // msgArr[1] : msg
                // msgArr[2] : SendUserID
                // msgArr[3] : RoomID
                // msgArr[4] : Date
                // msgArr[5] : RoomUser
//                String ImageUri = msgArr[6];
//                String timeString = msgArr[7];
//                String type = msgArr[8];
//
                //msg = et_chat.getText().toString();
                Calendar calendar = Calendar.getInstance(); // 현재시간
                long now = calendar.getTimeInMillis();
                date = new Date(now);
                msg = "사진";

                int type = 2;
                String ImageID = UUID.randomUUID().toString();
                ImageName = "IM_" + ImageID + "_DT_" + date.toString();

                Home_Activity.sendToService(SocketService.FileSenderThread_START, mImageUri.toString() + " " + ImageName);//파일전송스레드 실행

                if (RoomState.equals("Exist")) { // 기존방목록에서 클릭시 입장
                    String RoomName = getIntent().getStringExtra("roomname");
                    String[] RoomArr = RoomList_DB.select_RoomName(RoomName);
                    RoomUser = RoomArr[3];
                    MsgID = UUID.randomUUID().toString();
                    int readNotUserSize = RoomUser.split(",").length - 1;
//                    RoomArr[1] // RoomID
//                    RoomArr[2] // RoomName
//                    RoomArr[3] // RoomUser
//                    RoomArr[4] // Date
                    //          RoomArr[0] idx//  RoomArr[1] RoomID// RoomArr[2] RoomName// RoomArr[3] : RoomUser RoomArr[4] Date
                    toServerMsg = "ExistRoom" + "|" + RoomArr[1] + "|" + msg + "|" + date + "|" + MyID + "|" + RoomUser + "|" + MsgType + "|" + ImageName + "|" + readNotUserSize + "|" + readUsers + "|" + MsgID;

                    //고유식별ID 생성 - 방고유id
                    String msgID = UUID.randomUUID().toString();
                    //로컬DB에 채팅내용 저장
                    //ChattingDB.insert(RoomID,MyID,msg,msgID,date);
                    //서비스에 전송메세지 전달
                    Home_Activity.sendToService(SocketService.MSG_SET_VALUE, toServerMsg);

                    // clientChat.send_msg(toServerMsg); //서버에메세지 전송
                } else if (RoomState.equals("1:1")) {//1대1 채팅 - 친구목록 프래그먼트에서 선택
                    RoomUser = MyID + "," + RoomName;
                    int readNotUserSize = RoomUser.split(",").length - 1;
                    if (NewRoom) {//새로운 방에서 채팅

                        RoomID = UUID.randomUUID().toString();
                        NowRoomID = RoomID;
                        MsgID = UUID.randomUUID().toString();
                        toServerMsg = "AddNewRoom" + "|" + RoomID + "|" + RoomName + "|" + RoomUser + "|" + msg + "|" + date + "|" + MyID + "|" + MsgType + "|" + ImageName + "|" + readNotUserSize + "|" + readUsers + "|" + MsgID;
                        Home_Activity.sendToService(SocketService.MSG_SET_VALUE, toServerMsg);   //서비스에 전송메세지 전달
                        Log.d("NewRoom", NewRoom + "\n" + toServerMsg);
                        NewRoom = false;
                    } else { //기존방에서의 채팅일경우 - 방생성하지 않음
                        MsgID = UUID.randomUUID().toString();
                        toServerMsg = "ExistRoom" + "|" + RoomID + "|" + msg + "|" + date + "|" + MyID + "|" + RoomUser + "|" + MsgType + "|" + ImageName + "|" + readNotUserSize + "|" + readUsers + "|" + MsgID;
                        Home_Activity.sendToService(SocketService.MSG_SET_VALUE, toServerMsg);   //서비스에 전송메세지 전달
                        Log.d("NewRoom", NewRoom + "\n" + toServerMsg);
                    }

                } else { // 친구추가후 채팅방 입장

                    if (NewRoom) {//새로운 방에서 채팅
                        isAddNewRoom(true);
                        NewRoom = false;
                    } else {
                        //기존방에서의 채팅일경우 - 방생성하지 않음
                        isAddNewRoom(false);
                    }
                }

                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);  // 리사이클러뷰  맨 아래로 내리기

            }
        } else if (requestCode == TAKE_PICTURE) {


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GotoChattingRoom", "onResume");
        // Toast.makeText(getApplicationContext()," onResume " , Toast.LENGTH_SHORT).show();
        isSeeRoomID = NowRoomID;

        RoomList_DB.EnterRoom(NowRoomID,"");//방 정보 않읽은 메세지 수 0으로 수정 : 메세지 모두 읽음

        //방입장시 서버에 메세지 전송
        mMainHandler.sendEmptyMessage(UPDATE_MSG);

        //리사이클러뷰에 채팅내용을 넣는다
        mRecyclerView_SET_DATA();

    }

    public void mRecyclerView_SET_DATA() {

        //리사이클러뷰 초기화
        mAdapter.clear();
        //채팅방정보 SQLite에서 가져와서 리사이클뷰에 뿌려준다
        mRecyclerView.clearOnChildAttachStateChangeListeners();
        for (int i = 1; i <= ChattingDB.getChatCount(); ++i) {
            //채팅내용  로컬DB에서 가져오기
            String[] RoomArray = ChattingDB.select_RoomID(i, NowRoomID); // 해당 방ID로 로컬DB에서 채팅내용 가져오기
            //Log.e("채팅내용 로컬DB에서 가져오기" + i, NowRoomID + "\n" + RoomArray[0] + "|" + RoomArray[1] + "|" + RoomArray[2] + "|" + RoomArray[3] + "|" + RoomArray[4] + "|" + RoomArray[5]+ "|" + RoomArray[6]+ "|" + RoomArray[7]+ "|" + RoomArray[8]);
            String idx = RoomArray[0];
            String RoomID = RoomArray[1];
            String UserID = RoomArray[2];
            String msg = RoomArray[3];
            String msgID = RoomArray[4];
            String creat_date = RoomArray[5];
            String type = RoomArray[6];
            String imageName = RoomArray[7];
            String readNotUserSize = RoomArray[8];
            String readusers = RoomArray[9];
            if (idx != null) { // 데이터 있으면
                //Log.e ("RoomArray","0 : "+RoomArray[0]+"\n"+"1 : "+RoomArray[1]+"\n"+"2 : "+RoomArray[2]+"\n"+"3 : "+RoomArray[3]+"\n"+"4 : "+RoomArray[4]+"\n"+"5 : "+RoomArray[5]+"\n"+"6 : "+RoomArray[6]+"\n"+"7 : "+RoomArray[7]+"\n"+"8 : "+RoomArray[8]+"\n"+"9 : "+RoomArray[9]+"\n");


                item = new chat_item();
                item.setName(UserID);

                if(!readNotUserSize.trim().equals("0")){
                    item.setreadNotUserSize(readNotUserSize);
                }else{
                    item.setreadNotUserSize("");
                }

                //서버에서 작성시간 가져오기
                String time = creat_date.split(" ")[3];
                String AM_PM = creat_date.split(" ")[4];
               // Log.e("시간",time);
                if (time.trim().startsWith("0")) {
                    time.replaceFirst("0", "");
                   // Log.e("시간 ","time.trim().startsWith()"+time);
                }
                String creatTime = AM_PM + " " + time; // 오전 00:00

                item.settime(creatTime);
                //item.setRoom_size(false);

                if (UserID.equals(MyID)) { //내가 보낸 메세지일경우 우측에
                    item.set_isMe(true);
                } else { //상대방이 보낸 메세지일 경우 좌측에 배치
                    item.set_isMe(false);
                }

               // Log.e(this + "", "type : " + type);
                if (Integer.valueOf(type) == 2) { //이미지전송일 경우
                    item.setType(2);// 이미지 전송
                    item.setimageName(imageName);// 보낼 이미지이름
                } else {//문자 전송
                    item.setUser_chat(msg);
                }
                mAdapter.add(item);


            }

        }

        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);  // 리사이클러뷰  맨 아래로 내리기

    }


    String toServerMsg; //서버에게 보낼 메세지

    public void isAddNewRoom(Boolean NewRoom) {
        // 현재시간 가져오기
        Log.e("NewRoom 생성", NewRoom.toString());


        //채팅내용 리사이클러뷰에 추가
//        item = new chat_item();
//        item.setName(MyID);// 클라이언트 아이디
//        item.setUser_chat(et_chat.getText().toString());
//
//        //어댑터 업데이트
//        Log.d("전송클릭 item ",item.getName()+"  채팅내용 : "+item.getUser_chat());
//        mAdapter.add(item);

        if (NewRoom) {
            //새로운 방이면

            //선택한 친구목록을 가져온다 - 초대할 유저목록
            try {
                int usercount = getIntent().getIntExtra("usercount", 0);
                String test = getIntent().getStringExtra("userarray");
                JSONObject json = new JSONObject(test);//{"UserID":["user2","user3"]}
                if (usercount == 1) { //1명 {"UserID":"user8"}
                    RoomName = json.get("UserID").toString();//user8
                } else if (usercount > 1) { //2명 이상  {"UserID":["user2","user3"]}

                    // json.getString("UserID");//["user2","user3"]
                    //  json.get("UserID");//["user2","user3"]

                    String users = json.get("UserID").toString();
                    //방이름 생성
                    RoomName = users.substring(1, users.length() - 1);//"user2","user3"
                    RoomName = RoomName.substring(1, RoomName.length() - 1);//  user2","user3

                    RoomName = RoomName.replace("\",\"", ",");//  user2,user3
                    Log.d("2명 이상 json UserID", json.getString("UserID") + " / " + json.get("UserID"));
                }
                Log.e("RoomName", RoomName);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //고유식별ID 생성 - 방고유id
            RoomID = UUID.randomUUID().toString();
            NowRoomID = RoomID; // 현재방ID null -> RoomID데이터 넣어준다
            isSeeRoomID = RoomID;
            //Log.d("NewRoom",  NewRoom.toString() );
            // 전송버튼 클릭시 방생성 - SQLite에 저장
            RoomUser = MyID + "," + RoomName; //채팅방에 참여한 유저아이디  MyID,user2,user3
            int readNotUserSize = RoomUser.split(",").length - 1;
//            //SQLite에 방정보 저장 - 방이름, 참여자아이디
//            RoomList_DB.insert(RoomID, RoomName, RoomUser, date);

            //SQLite TEST로그
//            String[] RoomArray = RoomList_DB.select(2);
//            String[] a = RoomList_DB.select_RoomID(RoomID); //RoomID로 방정보 검색
//            Toast.makeText(getApplicationContext(), a[0].toString() + " / " + a[1].toString() + " / " + a[2].toString() + " / " + a[3].toString(), Toast.LENGTH_SHORT).show();
//            Log.d("RoomList_DB", a[0].toString() + " / " + RoomID + " = " + a[1].toString() + " / " + a[2].toString() + " / " + a[3].toString() + " / " + a[4].toString());
//            // Log.d("RoomArray", RoomArray[0]+ " / " + RoomArray[1] + " / " + RoomArray[2]+ " / " +RoomArray[3]);

            //sender 스레드로 EditText값 전달
            MsgID = UUID.randomUUID().toString();
            toServerMsg = "AddNewRoom" + "|" + RoomID + "|" + RoomName + "|" + RoomUser + "|" + msg + "|" + date + "|" + MyID + "|" + MsgType + "|" + ImageName + "|" + readNotUserSize + "|" + readUsers + "|" + MsgID;
            // 방id와 참여자아이디, 메세지내용을 서버에 전송
            //clientChat.send_msg(toServerMsg); //서버에메세지 전송

            //서비스에 전송메세지 전달
            Home_Activity.sendToService(SocketService.MSG_SET_VALUE, toServerMsg);

        } else if (!NewRoom && msg.trim() != "") {
            //기존방 대화내용입력 후 전송클릭시
            //RoomName = getIntent().getStringExtra("roomname");
            String[] RoomArr = RoomList_DB.select_RoomName(RoomName);
            Log.e("전송버튼클릭", "RoomName : " + RoomName);
            RoomID = RoomArr[1];
            RoomUser = RoomArr[3];
            int readNotUserSize = RoomUser.split(",").length - 1;
//          RoomArr[0] idx//  RoomArr[1] RoomID// RoomArr[2] RoomName// RoomArr[3] : RoomUser RoomArr[4] Date
            MsgID = UUID.randomUUID().toString();
            toServerMsg = "ExistRoom" + "|" + RoomID + "|" + msg + "|" + date + "|" + MyID + "|" + RoomUser + "|" + MsgType + "|" + ImageName + "|" + readNotUserSize + "|" + readUsers + "|" + MsgID;
            //"ExistRoom" + "|" + RoomID + "|" + msg + "|" + date + "|" + MyID + "|" + RoomUser+ "|" + MsgType+ "|" + ImageName+ "|" + 1;

            //clientChat.send_msg(toServerMsg); //서버에메세지 전송
            //서비스에 전송메세지 전달
            Home_Activity.sendToService(SocketService.MSG_SET_VALUE, toServerMsg);

        }
        Log.d("서버에게 보낸메세지", toServerMsg);

    }

    boolean enterRoom = false;

    // Handler 클래스
    class SendMassgeHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) { //서버에서 메세지 오면
                case MSG_FROM_SERVER:

                    Log.e("채팅룸 MSG_FROM_SERVER", "서비스에서 메세지 받음" + msg.obj.toString());
                    // String msgArr = msg.obj.toString();
                    //msgArr  ExistRoom or AddNewRoom|msg|MyID|RoomID|Date|RoomUser
//                    String[] data = msgArr.split("[|]");
//                    //Toast.makeText(getApplicationContext(),"서버로부터온메세지\n"+msgArr,Toast.LENGTH_SHORT).show();
//                    Log.d("data[0]", data[0]); //ExistRoom or AddNewRoom
//                    Log.d("data[1]", data[1]); //msg
//                    Log.d("data[2]", data[2]); //MyID
//                    Log.d("data[3]", data[3]); //RoomID
//                    Log.d("data[4]", data[4]); //Date
////                            Log.d("data[5]",data[5]); //RoomUser   AddNewRoom일 경우
                    // if (msgArr.startsWith("ExistRoom")) { //이미 존재하는 방일경우 방생성 안함

                    // ExistRoom|msg|MyID|RoomID|Date

                    //  } else if (msgArr.startsWith("AddNewRoom")) { //새로운 방일경우 채팅방목록에 추가
//                        // AddNewRoom|msg|MyID|RoomID|Date|RoomUser
//                        //RoomName : RoomUser에서 내ID를 뺀 나머지
//                        String[] UserArr = data[5].split(",");
//                        Log.d("UserArr[0]", UserArr[0]);
//                        Log.d("UserArr[1]", UserArr[1]);
//                        //                        Log.d("UserArr[2]",UserArr[2]);
//                        //                        Log.d("UserArr[3]",UserArr[3]);
//                        //                        Log.d("UserArr[4]",UserArr[4]);
//                        Log.d("MyID", MyID);
//                        RoomName = "";
//                        for (int i = 0; i < UserArr.length; ++i) {
//                            if (!UserArr[i].equals(MyID)) { //내ID가 아닐때
//                                if (UserArr.length == 2) {// 채팅참여자가 나 포함 2명 일때
//                                    RoomName = UserArr[i];
//                                } else {
//                                    if (RoomName.equals("")) {
//                                        RoomName = UserArr[i];
//                                    } else {
//                                        RoomName = RoomName + "," + UserArr[i];
//                                    }
//                                }
//                            }
//                        }
//                        Log.d("RoomName", RoomName);
//                        //SQLite에 방정보 저장
//                        //RoomList_DB.insert(data[3], RoomName, data[5], date);//RoomID,RoomName, RoomUser, date
                    //  }

//                    //채팅내용 리사이클러뷰에 추가
//                    item = new chat_item();
//                    item.setName(data[2]);// 채팅발송자 아이디
//                    item.setUser_chat(data[1]);
//                    mAdapter.add(item);

                    //채팅방 목록 추가
                    //rf.onResume();// 프래그먼트 동기화
                    //onResume(); // 채팅내용 동기화
                    mRecyclerView_SET_DATA();
                    break;

                case UPDATE_MSG:


                    //

                    // 클라이언트에 방에 입장했다는 정보 저장
                    for (int i = 1; i <= ChattingDB.getChatCount(); ++i) {
                        //채팅내용  로컬DB에서 가져오기
                        String[] RoomArray = ChattingDB.select_RoomID(i, RoomID); // 해당 방ID로 로컬DB에서 채팅내용 가져오기
                        String idx = RoomArray[0];


                        if (idx != null) { //데이터가 있다면
                            //Log.e("채팅내용 로컬DB에서 가져오기" + i, RoomArray[0] + "|" + RoomArray[1] + "|" + RoomArray[2] + "|" + RoomArray[3] + "|" + RoomArray[4] + "|" + RoomArray[5]+ "|" + RoomArray[6]+ "|" + RoomArray[7]+ "|" + RoomArray[8]+"|" + RoomArray[9]);
                            RoomID = RoomArray[1];String UserID = RoomArray[2];
                            String msgID = RoomArray[4];String creat_date = RoomArray[5];String msgtype = RoomArray[6];String imageName = RoomArray[7];
                            int readNotUserSize = Integer.valueOf(RoomArray[8]);
                            String readUsers = RoomArray[9];

                            //readUsers에 있는지 확인
                            String[] userarr = readUsers.split(",");
                            boolean isRead = false;
                            for (int l = 0; l < userarr.length; ++l) {
                               // Log.e("UpdateMsg수신", "readUsers에 있는지 확인");
                                if (userarr[l].trim().equals(MyID)) { //읽은 유저중에 유저이름이 없으면 추가
                                    isRead = true;
                                    break;
                                }
                            }
                            if (isRead) { //이미 읽은 메세지면 추가안함
                                Log.e("클라이언트에 방에 입장했다는 정보 저장", "읽은 유저중에 유저이름이 있음" + " / readUsers : " + readUsers +
                                        "\n readuser : " + MyID);
                            }else{
//                                String[] Roominfo =  RoomList_DB.select_RoomID(RoomID);
//                                int fullsize = Roominfo[3].length();

                                readUsers = readUsers + "," + MyID; //읽은 사람 추가
//                                Log.e("readUsers", readUsers);
//                                readNotUserSize = fullsize - readUsers.split(",").length;
                                ChattingDB.update(msgID, readNotUserSize-1, readUsers); // 읽은 사람 추가
                                Log.e("클라이언트에 방에 입장했다는 정보 저장", "읽은 유저중에 유저이름 추가" + " / readUsers : " + readUsers +
                                        "\n readuser : " + MyID);
                            }


                        }
                    }

                    //서버에 입장했다는 메세지 전송
                    Log.e("SendMassgeHandler", "UPDATE_MSG");
                    if (NowRoomID != null) {
                        Log.e("SendMassgeHandler", "NowRoomID : " + NowRoomID);
                        //방에 들어오면 서버에 알림
                        toServerMsg = "EnterRoom|" + NowRoomID + "|" + MyID;
                        Home_Activity.sendToService(SocketService.MSG_SET_VALUE, toServerMsg);

                        mRecyclerView_SET_DATA();
                        //NowRoomID=null;
                        enterRoom = false;
                    }

                    break;

                default : //내가 입장한거를 먼저 db에 저장 후 레이아웃에 보여준다
                    break;
            }
        }
    }
    SpeechRecognizer mRecognizer;
    public void FBtn_record_onClick(View v){ //음성으로 문자입력하기 버튼
        //*음성인식 시작하기
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //음성인식 intent생성
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());    //데이터 설정
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");                            //음성인식 언어 설정

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);                //음성인식 객체
        mRecognizer.setRecognitionListener(listener);                                        //음성인식 리스너 등록
        mRecognizer.startListening(i);
    }

    //음성인식 리스너
    private RecognitionListener listener = new RecognitionListener() {
        //입력 소리 변경 시
        @Override public void onRmsChanged(float rmsdB) {}

        //음성 인식 결과 받음
        @Override public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            et_chat.setText(""+rs[0]);
        }

        //음성 인식 준비가 되었으면
        @Override public void onReadyForSpeech(Bundle params) {
            Toast.makeText(GotoChattingRoomActivity.this, "음성인식준비", Toast.LENGTH_SHORT).show();
        }

        //음성 입력이 끝났으면
        @Override public void onEndOfSpeech() {

        }

        //에러가 발생하면
        @Override public void onError(int error) {
            Log.e("음성인식에러 ", error+"");
        }

        @Override public void onBeginningOfSpeech() {}                            //입력이 시작되면
        @Override public void onPartialResults(Bundle partialResults) {}       //인식 결과의 일부가 유효할 때

        //미래의 이벤트를 추가하기 위해 미리 예약되어진 함수
        @Override public void onEvent(int eventType, Bundle params) {}
        @Override public void onBufferReceived(byte[] buffer) {}                //더 많은 소리를 받을 때
    };
}

