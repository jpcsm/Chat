package com.test.js.chat;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.test.js.chat.Network.Protocol;
import com.test.js.chat.Network.Upload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Locale;

import static com.test.js.chat.GotoChattingRoomActivity.isSeeRoomID;
import static com.test.js.chat.GotoChattingRoomActivity.mMainHandler;
import static com.test.js.chat.Home.HomeFragment.Live_Fragment.mLiveHandler;
import static com.test.js.chat.Project_Fragment.MessageHandler.MSG_PROJECT_FRAGMENT;
import static com.test.js.chat.Project_Fragment.mProjectHandler;
import static com.test.js.chat.RoomList_Fragment.mRoomListHandler;

//import static com.test.js.chat.GotoChattingRoomActivity.mMainHandler;
//import static com.test.js.chat.Home_Activity.MyID;
//import static com.test.js.chat.RoomList_Fragment.mRoomListHandler;

/**
 * Created by lenovo on 2017-07-04.
 */
public class SocketService extends Service implements TextToSpeech.OnInitListener {
    private Messenger mRemote;
    SpeechRecognizer apeechRecognizer;
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    SQLite_RoomList RoomList_DB;    //SQLite 객체생성
    chat_item item;
    chatAdapter mAdapter;
    static final int MSG_REGISTER_CLIENT = 44;
    static final int MSG_UNREGISTER_CLIENT = 56;
    public static final int MSG_SET_VALUE = 77;           // 서버로메시지 보내기
    public static final int FileSenderThread_START = 8;//파일전송스레드 실행
    int mValue = 0;
    IncomingHandler incomingHandler = new IncomingHandler();
    /**
     * Command to the service to display a message
     */
    static final int MSG_SAY_HELLO = 1;
    static final int TOAST_MSG = 587;
    static final int UNBIND_SERVICE = 2;
    public static final int MSG_LIVE_FRAGMENT = 3;

    //String ReadUser;
    /**
     * Handler of incoming messages from clients.
     */
    String absolutePath;
    String filename;



    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //서비스-액티비티 바인드 성공
                case MSG_REGISTER_CLIENT :
                    mClients.add(msg.replyTo);
                    //Toast.makeText(getApplicationContext(), "SocketService MSG_REGISTER_CLIENT", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_UNREGISTER_CLIENT :
                    mClients.remove(msg.replyTo);
                    // Toast.makeText(getApplicationContext(), "SocketService MSG_UNREGISTER_CLIENT", Toast.LENGTH_SHORT).show();



                case TOAST_MSG : //토스트메세지

                    // Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    //String[] msgarr = msg.obj.toString().split("[|]"); //메세지 내용 추출
                    String readUser="";
                    if(msg.obj.toString().split("[|]").length>2){
                        readUser = msg.obj.toString().split("[|]")[2];
                    }
                    Log.e("TOAST_MSG msg",msg.obj.toString()+"");
                    Log.e("TOAST_MSG readUser",readUser.toString());
                    Log.e("","TOAST_MSG readUser.trim().equals(MyID)"+readUser.toString().trim().equals(MyID)+"");
                    if(msg.obj.toString().trim().startsWith("NonReceiptMsg")&&!readUser.toString().trim().equals(MyID)){

                        String msgSize = msg.obj.toString().split("[|]")[1];
                        if(Integer.valueOf(msgSize)==1){
                            myTTS.speak("메세지가 도착했습니다", TextToSpeech.QUEUE_FLUSH, null); //큐에 모든 값을 없애고 초기화한후 값을 넣는 옵션
                        }else{
                            myTTS.speak(msgSize+"건의 메세지가 도착했습니다", TextToSpeech.QUEUE_FLUSH, null); //큐에 모든 값을 없애고 초기화한후 값을 넣는 옵션

                        }
                        Log.e("NonReceiptMsg",msg.obj.toString()+"");
                    }else {
                        Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("TOAST",msg.obj.toString()+"");
                    }






                    break;


                //액티비티에서 서비스로 온 메세지 수신 ( 클라 -> 서버로보낼 데이터)
                case MSG_SET_VALUE :
                    if (msg.obj.toString().equals("서비스종료")) {
                        Toast.makeText(getApplicationContext(), "MSG_SET_VALUE" + msg.obj.toString(), Toast.LENGTH_SHORT).show();

                        onDestroy();
                    } else {

                        msg.obj.toString();
                        //mValue = msg.arg1;
//                    for (int i=mClients.size()-1; i>=0; i--){ //홈 액티비티로 데이터 전송
//                        try{
//                            mClients.get(i).send( Message.obtain( null, MSG_SET_VALUE, mValue, 0, new String(msg.obj.toString())) );
//                        }
//                        catch( RemoteException e){
//                            mClients.remove( i );
//                        }
//                    }
                        //서버에 보낼 데이터
                        receiveThread.send_msg(msg.obj.toString());
                    }


                    break;
                case FileSenderThread_START: //파일전송스레드 실행
                    String mImageUri = msg.obj.toString().split(" ")[0]; // 파일 경로
                    filename = msg.obj.toString().split(" ")[1]; //파일이름
                    Log.e("mImageUri", mImageUri + " filename : " + filename);
                    //절대경로를 획득
                    Cursor c = getContentResolver().query(Uri.parse(mImageUri), null, null, null, null);
                    c.moveToNext();
                    absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                    //파일전송스레드 실행
                    //receiveThread.sendFile(absolutePath,filename);
                    //서버에 프로필이미지 전송
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Upload.File(absolutePath, filename);
                        }
                    }).start();
                    break;
                default:
                    super.handleMessage(msg);
            }


        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */

    static boolean chatmode = false;
    static int chatState = 0;
    Socket socket;
    //0 : 로그온 안된상태, 1 : 로그온된상태, 2: 방입장완료 (대화가능),
    //3 : 상대방이 1:1대화요청한 상태 ,
    //5 : req_fileSend (상대방이 현재사용자에게 파일전송을 수락요청한 상태)
    Context context;
    DataOutputStream out;
    DataInputStream in;
    String ClientID;
    ReceiveThread receiveThread;

    @Override
    public IBinder onBind(Intent intent) {
        // Toast.makeText(getApplicationContext(), "서비스 바인드", Toast.LENGTH_SHORT).show();
        Log.d("onBind", "onBind");


        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("onUnbind", "onUnbind");
        return super.onUnbind(intent);
    }

    String MyID;
    TextToSpeech myTTS;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("서비스", "실행");
        //Toast.makeText(getApplicationContext(), "서비스 실행", Toast.LENGTH_SHORT).show();

        registerRestartAlarm(true); /**알람 매니져에 서비스 등록*/

        //SQLite 객체생성
        ChattingDB = new SQLite_Chatting(getApplicationContext(), "chatting.db", null, 1);

        //SQLite 객체생성
        RoomList_DB = new SQLite_RoomList(getApplicationContext(), "chat.db", null, 1);

        //unregisterRestartAlarm(); //이미 등록된 알람이 있으면 제거

        SharedPreferences sf = getSharedPreferences("ChatUserID", Activity.MODE_PRIVATE);
        //SharedPreferences.Editor ed = sf.edit();
//        ed.putString("userid", MyID);
//        ed.commit();

        MyID = sf.getString("userid", "");
        // Toast.makeText(getApplicationContext(), " SocketService onCreate MyID : " + MyID, Toast.LENGTH_SHORT).show();

        //소켓생성 - 메인에서 생성시 에러발생
        receiveThread = new ReceiveThread(this, MyID); // context를 스레드로 넘겨준다
        receiveThread.start();

        //TextToSpeech객체생성
        myTTS = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.ERROR) {
            myTTS.setLanguage(Locale.KOREAN);
        }

//        String myText1 = "안녕하세요 안드로이드 블로그 녹두장군 입니다.";
//        String myText2 = "말하는 스피치 입니다.";
//        myTTS.speak(myText1, TextToSpeech.QUEUE_FLUSH, null); //큐에 모든 값을 없애고 초기화한후 값을 넣는 옵션
//        myTTS.speak(myText2, TextToSpeech.QUEUE_ADD, null); //현재 있는 큐값에 추가하는 옵션
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("Service", "onRebind");
        //   Toast.makeText(getApplicationContext(), "Service onRebind", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", "onStartCommand");
        // Toast.makeText(getApplicationContext(), "Service onStartCommand()", Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SocketService", "onDestroy");
        // Toast.makeText(getApplicationContext(), "서비스 종료", Toast.LENGTH_SHORT).show();
        if(myTTS !=null){
            myTTS.stop();
            myTTS.shutdown();
        }
        registerRestartAlarm(true); /**알람 매니져에 서비스 등록*/
    }

    private void registerRestartAlarm(Boolean bool) { /**알람 매니져에 서비스 등록*/
        if (bool) {
            Log.i("SocketService", "registerRestartAlarm");
            Intent intent = new Intent(SocketService.this, BootBroadcastReceiver.class);
            intent.setAction("ACTION.RESTART.SocketService");
            PendingIntent sender = PendingIntent.getBroadcast(SocketService.this, 0, intent, 0);

            long firstTime = SystemClock.elapsedRealtime();
            firstTime += 1 * 1000;

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            /**
             * 알람 등록
             */
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1 * 1000, sender);
            //첫번째 매개변수에는 기준 시간이 들어갑니다.
            // ElAPSED_REALTIME_WAKEUP은 현재 시작되는 시간을 0으로 하는 상대적인 시간입니다.바로 지금!
            // 이라고 하는거죠. ElAPSED_REALTIME 과 다른점은 시스템이 휴면상태에 들어가면 깨워라! 가 추가됩니다.
            // 절대시간을 사용하고 싶다면 RTC 또는 RTC_WAKEUP을 사용하면 됩니다.
            // 두번째 매개변수는 언제 처음 시작할건가 입니다. SystemClock.elapsedRealtime() + 1000 이라는 것은
            // 지금부터 1초(1000ms)후라는 의미입니다. 세번째 매개변수는 반복되는 시간간격입니다.
            // 10000ms마다 이므로 10초에 한번씩 입니다. 마지막 매개변수는 알람이 전해줄 PendingIntent로
            // 위에서 작성한 코드대로면 RestartReceiver에 ACTION_RESTART_SERVICE를 전달하겠습니다.

        }

    }


    public void unregisterRestartAlarm() {   /** 알람 매니져에 서비스 해제*/

        Log.i("000 SocketService", "unregisterRestartAlarm");

        Intent intent = new Intent(SocketService.this, BootBroadcastReceiver.class);
        intent.setAction("ACTION.RESTART.SocketService");
        PendingIntent sender = PendingIntent.getBroadcast(SocketService.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        /**
         * 알람 취소
         */
        alarmManager.cancel(sender);


    }
    //Socket socket;

    public class ReceiveThread extends Thread { //소켓생성스레드

        Socket socket;
        //0 : 로그온 안된상태, 1 : 로그온된상태, 2: 방입장완료 (대화가능),
        //3 : 상대방이 1:1대화요청한 상태 ,
        //5 : req_fileSend (상대방이 현재사용자에게 파일전송을 수락요청한 상태)
        Context context;
        DataOutputStream out;
        DataInputStream in;
        String ClientID;
        public static final int MSG_FROM_SERVER = 0;
        Boolean isSocketConected = false;

        public ReceiveThread(Context context, String ClientID) {
            this.context = context;
            this.ClientID = ClientID;
        }

        @Override
        public void run() {
            super.run();


            SocketCreat();

            while (socket != null) {
                // this.sleep(1000);
                //Log.d("ClientChat", "Thread run();");
                receiver(); //서버로부터 메세지 받음
            }

        }

        public void SocketCreat() {//소켓생성

            try {
                Log.e("SocketCreat", "시작");
                String ServerIP = "jpcsm9003.vps.phps.kr";
                socket = new Socket(ServerIP, 9999); //소켓 객체 생성
                Log.e("SocketCreat", "소켓생성");
                isSocketConected = true;
                //사용자로부터 얻은 문자열을 서버로 전송해주는 역할을 하는 쓰레드.
                //Thread sender = new Sender(socket);  //서버로 메세지를 보내는 클래스
                //Thread receiver = new Receiver(socket);    //서버로부터 메시지를 받는 클래스
                // handler.sendEmptyMessage(0);
                //sender.start(); //스레드 시작
                //receiver.start(); //스레드 시작
                in = new DataInputStream(this.socket.getInputStream());
                out = new DataOutputStream(this.socket.getOutputStream());
                out.writeUTF("req_logon|" + ClientID);
                Log.e("Socket", "연결성공");

                //서비스 토스트 메세지
                Message message2 = incomingHandler.obtainMessage();// 메시지 ID 설정
                message2.what = TOAST_MSG;// 메시지 정보 설정 (int 형식)
                message2.obj = new String("Socket : 연결성공");// 메시지 정보 설정3 (Object 형식)
                incomingHandler.sendMessage(message2);


            } catch (Exception e) {
                isSocketConected = false;
                Log.e("Socket", "객체생성실패" + e);
            }


        }

        //    public Handler handler = new Handler(){
//
//        @Override
//        public void handleMessage(Message msg) {
//
//            if(msg.what == 0){
//                Toast.makeText(context, "socket객체생성성공 ", Toast.LENGTH_SHORT).show();
//            }else if(msg.what == 1){
//
//            }else if(msg.what == 2){
//            }
//
//        }
//    };
        //서버로메세지 보내기
        String toServerMsg;
        Protocol protocol;

        public void send_msg(String msg) {
            toServerMsg = msg;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        out.writeUTF(toServerMsg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        //파일전송
        public void sendFile(final String fileuri, String filename) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileInputStream fin = new FileInputStream(fileuri); //FileInputStream 파일 받는 스트림
                        byte[] buffer = new byte[1024];        //바이트단위로 임시저장하는 버퍼를 생성합니다.
                        int len;                               //전송할 데이터의 길이를 측정하는 변수입니다.
                        int dataSize = 0;

                        while ((len = fin.read(buffer)) > 0) {     //FileInputStream을 통해 파일에서 입력받은 데이터를 버퍼에 임시저장하고 그 길이를 측정합니다.
                            dataSize++;                        //데이터의 양을 측정합니다.
                        }

                        int datas = dataSize;                      //아래 for문을 통해 data가 0이되기때문에 임시저장한다.


//                        fin = new FileInputStream(fileuri);   //FileInputStream이 만료되었으니 새롭게 개통합니다.
//                        out.writeInt(dataSize);                   //데이터 전송횟수를 서버에 전송하고,
//                        out.writeUTF(filename);               //파일의 이름을 서버에 전송합니다.

                        len = 0;
                        for (; dataSize > 0; dataSize--) {                   //데이터를 읽어올 횟수만큼 FileInputStream에서 파일의 내용을 읽어옵니다.
                            len = fin.read(buffer);        //FileInputStream을 통해 파일에서 입력받은 데이터를 버퍼에 임시저장하고 그 길이를 측정합니다.
                            protocol = new Protocol();
                            protocol.setPacket(Protocol.PT_FILE_SEND, dataSize, buffer);
                            out.write(protocol.getPacket());       //서버에게 파일의 정보(1kbyte만큼보내고, 그 길이를 보냅니다.
                        }

                        Log.e("", "약 " + datas + " kbyte");
                        fin.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


//                    try {
//                        out.write(toServerMsg);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }).start();

        }

        /**
         * 메시지 파서
         */
        public String[] getMsgParse(String msg) {
            //System.out.println("msgParse()=>msg?"+ msg);

            String[] tmpArr = msg.split("[|]");

            return tmpArr;
        }

        public void receiver() {


            while (in != null) { //입력스트림이 null이 아니면..반복
                try {

//                    byte[] buf = new byte[0];
//                    in.read(buf);
                    
                    String msg = in.readUTF(); //입력스트림을 통해 읽어온 문자열을 msg에 할당.



                    Log.e("서버로부터 받은 메세지", msg);

                    if(msg!=null){
                        //서버에 보낼 데이터
                        receiveThread.send_msg("pong|"+MyID);
                    }
                    if(msg.startsWith("NonReceiptMsg")){
                        //서비스 토스트 메세지
                        Message m1 = incomingHandler.obtainMessage();// 메시지 ID 설정
                        m1.what = TOAST_MSG;// 메시지 정보 설정 (int 형식)
                        m1.obj = new String(msg);// 메시지 정보 설정3 (Object 형식)
                        incomingHandler.sendMessage(m1);

                    }
                    if (msg.startsWith("AddNewRoom") || msg.startsWith("ExistRoom")) { //로그인 성공  (대화명)
                        //ClientChat.chatState = 1; //채팅 상태를 로그온 된 상태로 변경.
                        //logon#yes|그룹리스트
                        //System.out.println(msgArr[0]); //방목록
                        //System.out.println("방이름을 입력해 주세요:");
                        String RoomID = msg.split("[|]")[3];
                        Log.e("서비스 토스트 메세지", RoomID + " / " + isSeeRoomID);

//                        String sendUser = msg.split("[|]")[2];
//                        Log.e("sendUser",sendUser);
//                        Log.e("","sendUser.trim().startsWith(MyID) : "+sendUser.trim().equals(MyID));
//                        Log.e("MyID",MyID);
                        //해당 채팅방에 들어와있지 않으면 토스트 띄움
                        if (!RoomID.trim().equals(isSeeRoomID)) {

                                //서비스 토스트 메세지
                                Message message2 = incomingHandler.obtainMessage();// 메시지 ID 설정
                                message2.what = TOAST_MSG;// 메시지 정보 설정 (int 형식)
                                String[] msgarr = msg.split("[|]"); //메세지 내용 추출
                                message2.obj = new String(msgarr[2] + " : " + msgarr[1]);// 메시지 정보 설정3 (Object 형식)

                                incomingHandler.sendMessage(message2);


                        }


                        //String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|") + 1));
                        try {
                            AddNewRoom_OR_ExistRoom(msg);//새방인지 기존방인지 구별해서 로컬 DB에 메세지 저장
                        } catch (Exception e) {
                            Log.e("NewRoom_OR_ExistRoom오류", e + "");
                        }

                        // 방리스트 프래그먼트로 메세지 전달
                        try {
                            Message message3 = mRoomListHandler.obtainMessage();// 메시지 얻어오기
                            message3.what = MSG_FROM_SERVER; // 메시지 ID 설정
                            message3.obj = new String(msg); // 메시지 정보 설정3 (Object 형식)
                            mRoomListHandler.sendMessage(message3);
                        } catch (Exception e) {
                            Log.e("mRoomListHandler 오류", e + "");
                        }

                        // 채팅룸 액티비티로 메세지 전달
                        try {
                            Message message = mMainHandler.obtainMessage();// 메시지 얻어오기
                            message.what = MSG_FROM_SERVER; // 메시지 ID 설정
                            message.obj = new String(msg); // 메시지 정보 설정3 (Object 형식)
                            mMainHandler.sendMessage(message);
                            //   Log.e("서비스", "채팅룸 액티비티로 메세지 전달" + msg);
                        } catch (Exception e) {
                            Log.e("mMainHandler 오류", e + "");
                        }
                    }

                    //메세지 업데이트
                    //!msg.split("[|]")[2].equals(MyID)
                    else if (msg.startsWith("UpdateMsg")) {
                        Log.e("UpdateMsg수신", "");
                        String[] msgArr = msg.split("[|]");
                        String RoomID = msgArr[1];
                        String readuser = msgArr[2];
                        //상대방이 방입장  채팅읽음

                        a:
                        for (int i = 1; i <= ChattingDB.getChatCount(); ++i) {
                            //채팅내용  로컬DB에서 가져오기
                            String[] RoomArray = ChattingDB.select_RoomID(i, RoomID); // 해당 방ID로 로컬DB에서 채팅내용 가져오기
                            String idx = RoomArray[0];

                            if (idx != null) { //데이터가 있다면
                                //Log.e("채팅내용 로컬DB에서 가져오기" + i, RoomArray[0] + "|" + RoomArray[1] + "|" + RoomArray[2] + "|" + RoomArray[3] + "|" + RoomArray[4] + "|" + RoomArray[5]+ "|" + RoomArray[6]+ "|" + RoomArray[7]+ "|" + RoomArray[8]+"|" + RoomArray[9]);
                                RoomID = RoomArray[1];
                                String UserID = RoomArray[2];
                                msg = RoomArray[3];
                                String msgID = RoomArray[4];
                                String creat_date = RoomArray[5];
                                String msgtype = RoomArray[6];
                                String imageName = RoomArray[7];
                                int readNotUserSize = Integer.valueOf(RoomArray[8]);
                                String readUsers = RoomArray[9];

//                                readUsers에 있는지 확인
                                String[] userarr = readUsers.split(",");
                                boolean isRead = false;
                                for (int l = 0; l < userarr.length; ++l) {
                                   // Log.e("UpdateMsg수신", "readUsers에 있는지 확인");
                                    if (userarr[l].trim().equals(readuser)) { //읽은 유저중에 유저이름이 없으면 추가
                                        isRead = true;
                                        break;
                                    }
                                }


                                if (isRead) { //이미 읽은 메세지면 추가안함
                                    //Log.e("UpdateMsg수신", "읽은 유저중에 유저이름이 있음" + " / readUsers : " + readUsers + "\nreaduser : " + readuser);
                                    //break a ;
                                } else {
//                                    String[] Roominfo =  RoomList_DB.select_RoomID(RoomID);
//                                    int fullsize = Roominfo[3].length();
//                                    Log.e("fullsize",fullsize+""+"Roominfo[3] : "+Roominfo[3]);
//                                    readNotUserSize = fullsize - readUsers.split(",").length;
                                    readUsers = readUsers + "," + readuser; //읽은 사람 추가
                                    ChattingDB.update(msgID, readNotUserSize-1, readUsers); // 읽은 사람 추가
                                    Log.e("UpdateMsg수신", "읽은 유저중에 유저이름 추가" + " / readUsers : " + readUsers + "\nreaduser : " + readuser);
                                    // break a ;
                                }

                            }
                            //  Log.d ("Project_Fragment","onResume : "+RoomArray[2]);
                        }

                        // 채팅룸 액티비티로 메세지 전달
                        try {
                            Message message = mMainHandler.obtainMessage();// 메시지 얻어오기
                            message.what = MSG_FROM_SERVER; // 메시지 ID 설정
                            message.obj = new String(msg); // 메시지 정보 설정3 (Object 형식)
                            mMainHandler.sendMessage(message);
                            //Log.e("서비스", "채팅룸 액티비티로 메세지 전달" + msg);
                        } catch (Exception e) {
                            Log.e("mMainHandler 오류", e + "");
                        }
                    }

                    //프로젝트 생성
                    else if (msg.startsWith("AddProject")) {
                        //AddProject|ProjectName|ProjectUsers|admin|ProjectID|timeString
                        String[] msgArr = msg.split("[|]");
                        String ProjectName = msgArr[1];
                        String ProjectUsers = msgArr[2];
                        String admin = msgArr[3];
                        String ProjectID = msgArr[4];
                        String timeString = msgArr[5];


                        String[] Arr = RoomList_DB.select_project(ProjectID);
                        //idx |projectid| projectname | projectimage | projectusers |create_time | adminuser
                        if (Arr[1] == null) { //기존에 저장된 프로젝트가 아니면 저장한다
                            //DB에 저장( projectid ,projectname ,adminuser ,projectusers,create_time)
                            Log.e("프로젝트 SQLite에 저장", msgArr[0] + " | " + msgArr[1] + " | " + msgArr[2] + " | " + msgArr[3] + " | " + msgArr[4] + " | " + msgArr[5]);
                            RoomList_DB.insert_project(ProjectID, ProjectName, admin, ProjectUsers, timeString);
                        } else { //기존에 있는 프로젝트면 업데이트 한다
                            RoomList_DB.update_project(ProjectID, ProjectName, admin, ProjectUsers);
                        }


                        // 프로젝트 프래그먼트로 메세지 전달
                        try {
                            Message str = mProjectHandler.obtainMessage();// 메시지 얻어오기
                            str.what = MSG_PROJECT_FRAGMENT; // 메시지 ID 설정
                            str.obj = new String(msg); // 메시지 정보 설정3 (Object 형식)
                            mProjectHandler.sendMessage(str);
                        } catch (Exception e) {
                            Log.e("mRoomListHandler 오류", e + "");
                        }

                    }


                    //Live 방송추가
                    else if (msg.startsWith("AddLive")) {
                        //AddProject|ProjectName|ProjectUsers|admin|ProjectID|timeString
                        String[] msgArr = msg.split("[|]");
                        String LiveID = msgArr[1];
                        String livepublisher = msgArr[2];
                        String livetitle = msgArr[3];
                        String liveusersize = msgArr[4];

                        this.sleep(1000);//1초 딜레이 - 파일업로드 한후 썸네일 가져오기 위해
                        // Live프래그먼트에 방송목록 전달
                        try {
                            Message str = mLiveHandler.obtainMessage();// 메시지 얻어오기
                            str.what = MSG_LIVE_FRAGMENT; // 메시지 ID 설정
                            str.obj = new String(msg); // 메시지 정보 설정3 (Object 형식)
                            mLiveHandler.sendMessage(str);
                        } catch (Exception e) {
                            Log.e("AddLive Message 오류", e + "");
                        }

                    }


                    //프로젝트 초대되었을 때
//                    else if (msg.startsWith("InvitedProject")) {
//                        //AddProject|ProjectName|admin|ProjectUsers|ProjectID|timeString
//
//                        String[] msgArr = msg.split("[|]");
//                        RoomList_DB.insert_project(msgArr[4],msgArr[1],msgArr[3],msgArr[5],msgArr[2]);
//
//                        //서비스 토스트 메세지
//                        Message message2 = incomingHandler.obtainMessage();// 메시지 ID 설정
//                        message2.what = TOAST_MSG;// 메시지 정보 설정 (int 형식)
//                        message2.obj = new String("'"+msgArr[1]+"' 프로젝트에 초대되었습니다");// 메시지 정보 설정3 (Object 형식)
//                        incomingHandler.sendMessage(message2);
//                        // 프로젝트 프래그먼트로 메세지 전달
//                        try {
//                            Message str = mProjectHandler.obtainMessage();// 메시지 얻어오기
//                            str.what = MSG_PROJECT_FRAGMENT; // 메시지 ID 설정
//                            str.obj = new String(msg); // 메시지 정보 설정3 (Object 형식)
//                            mProjectHandler.sendMessage(str);
//                        } catch (Exception e) {
//                            Log.e("InvitedProject 오류", e + "");
//                        }
//
//                    }

                    //서버에서 받은 메세지를 홈 액티비티로 전달 한다

//                    try {
//                        mClients.get(0).send(Message.obtain(
//                                null, MSG_SET_VALUE, mValue, 0, new String(msg)
//                        ));
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }


                    //메세지 처리 ----------------------------------------------
                    if (msg.startsWith("logon#yes")) { //로그인 성공  (대화명)
                        //ClientChat.chatState = 1; //채팅 상태를 로그온 된 상태로 변경.
                        //logon#yes|그룹리스트
                        //System.out.println(msgArr[0]); //방목록
                        //System.out.println("방이름을 입력해 주세요:");


                    } else if (msg.startsWith("logon#no")) { //로그온 실패 (대화명)

                        //ClientChat.chatState = 0;
                        //System.out.println("[##] 중복된 아이디가 존재합니다. 다시 입력하세요.:");
                        //1. 이름이 중복될경우(서버전체 or 그룹) logon#no 패킷이 서버로부터 전달됨.
                        //2. 이름이 중복될경우 서버에서 자체적으로 name(1), name(2) 이런식으로 중복되지 않게 변경하는 방법.

                    } else if (msg.startsWith("enterRoom#yes")) { //그룹입장

                        //enterRoom#yes|지역
                        // System.out.println("[##] 채팅방 ("+msgArr[0]+") 에 입장하였습니다.");
                        //ClientChat.chatState = 2; //챗 상태 변경 ( 채팅방입장 완료로 대화가능상태)

                    } else if (msg.startsWith("enterRoom#no")) {
                        //enterRoom#no|지역
                        //System.out.println("[##] 입력하신 ["+msgArr[0]+ "]는 존재하지않는 지역입니다.");
                        // System.out.println("▶지역을 다시 입력해 주세요:");

                    } else if (msg.startsWith("show")) { //서버에서 받은 메시지

                        //show|메시지내용
                        //System.out.println(msgArr[0]);

                    } else if (msg.startsWith("say")) { //대화내용
                        // say|아이디|대화내용
                        // System.out.println("["+msgArr[0]+"] "+msgArr[1] );

                    } else if (msg.startsWith("whisper")) { //귓속말
                        //whisper|아이디|대화내용
                        //  System.out.println("[귓]["+msgArr[0]+"] "+msgArr[1] );

                    } else if (msg.startsWith("req_PvPchat")) { //해당 사용자에게 1:1대화 요청
                        //req_PvPchat|출력내용
                        // ClientChat.chatState = 3; //챗 상태 변경 (상대방이 1:1대화신청을 했을경우)
                        //MultiClient.chatmode=true; //Sender에게 1:1요청이 들어왔다는것을 알려주기 위함
                        //System.out.println(msgArr[0]); //메세지만 추출
                        // System.out.print("▶선택:");

                    } else if (msg.startsWith("req_fileSend")) { //상대방이 현재 사용자에게 파일전송 수락 요청
                        //req_fileSend|출력내용
                        //req_fileSend|[##] name 님께서 파일 전송을 시도합니다. 수락하시겠습니까?(Y/N)
                        // ClientChat.chatState = 5; //상태 변경 (상대방이 현재사용자에게 파일전송을 수락요청한 상태)
                        // System.out.println(msgArr[0]); //메세지만 추출
                        // System.out.print("▶선택:");
                        // sleep(100);
                    }
//                else if(msg.startsWith("fileSender")){ //파일을 보내기위해 파일서버 준비
//
//                    //fileSender|filepath;
//                    System.out.println("fileSender:"+InetAddress.getLocalHost().getHostAddress());
//                    System.out.println("fileSender:"+msgArr[0]);
//                    //String ip=InetAddress.getLocalHost().getHostAddress();
//
//                    try {
//                        new FileSender(msgArr[0]).start(); //쓰레드 실행.
//                    } catch (Exception e) {
//                        System.out.println("FileSender 쓰레드 오류:");
//                        e.printStackTrace();
//                    }
//
//                }
//                else if(msg.startsWith("fileReceiver")){ //파일받기
//                    //fileReceiver|ip|fileName;
//
//                    System.out.println("fileReceiver:"+InetAddress.getLocalHost().getHostAddress());
//                    System.out.println("fileReceiver:"+msgArr[0]+"/"+msgArr[1]);
//
//                    String ip = msgArr[0];  //서버의 아이피를 전달 받음
//                    String fileName = msgArr[1]; //서버에서 전송할 파일이름.
//
//                    try {
//                        new FileReceiver(ip,fileName).start(); //쓰레드 실행.
//                    } catch (Exception e) {
//                        System.out.println("FileSender 쓰레드 오류:");
//                        e.printStackTrace();
//                    }
//
//
//                }
                    else if (msg.startsWith("req_exit")) { //종료
                    }

                } catch (SocketException e) {
                    //System.out.println("예외:"+e);
                    // System.out.println("##접속중인 서버와 연결이 끊어졌습니다.");
                    return;

                } catch (Exception e) {
                    //서비스 토스트 메세지
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message2 = incomingHandler.obtainMessage();// 메시지 ID 설정
                            message2.what = TOAST_MSG;// 메시지 정보 설정 (int 형식)
                            message2.obj = new String("Socket : 서버와 연결이 끊어졌습니다.");// 메시지 정보 설정3 (Object 형식)
                            incomingHandler.sendMessage(message2);
                        }
                    }).start();


                    Log.e("receiver() ", "##접속중인 서버와 연결이 끊어졌습니다." + e);


                    isSocketConected = false;
//                    try {
//                        socket.close();
//                        in.close();
//                        out.close();
//                        socket = null;
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
                    while (!isSocketConected) {
                        try {
                            this.sleep(3000);//3초후 재연결
                            //if(!isSocketConected) SocketCreat(); //소켓 재생성
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isSocketConected) SocketCreat(); //소켓 재생성
                                }
                            }).start();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }


                    }


                    break;

                }//try catch
            }//while
        }//receiver

//    public void beforeFunction(final String toMainMsg ) {
//        Runnable rn = new Runnable()
//        {
//
//            @Override
//
//            public void run()
//
//            {
//            // 딜레이 걸리는 처리
//                //String str = toMainMsg ;
//
//                Bundle bd = new Bundle() ;      /// 번들 생성
//                bd.putString( "arg", toMainMsg ) ;    /// 번들에 값 넣기
//                Message msg = m_hd.obtainMessage( ) ;   /// 핸들에 전달할 메세지 구조체 받기
//                msg.setData( bd ) ;                     /// 메세지에 번들 넣기
//                m_hd.sendMessage( msg ) ;               /// 메세지를 전달
//            }
//
//        };
//        /// 여기서 왠만하면 프로스레스 띄우기
//        new Thread( rn ).start() ;
//    }


        //
//    Handler m_hd = new Handler( ) {
//        public void handleMessage(android.os.Message msg) {
//            Bundle bd = msg.getData( ) ;            /// 전달 받은 메세지에서 번들을 받음
//            String str = bd.getString( "arg" ) ;    /// 번들에 들어있는 값 꺼냄
//            ////// 이후 처리
//            afterFunction( str ) ;
//        }
//    } ;
//
//    void afterFunction( String str ){
//        /// 스래드 처리가 끝난 다음 할 일
//        /// 여기서 프로그레스 제거
//    }
        public void AddNewRoom_OR_ExistRoom(String msg) {
            Log.e("AddNewRoom_OR_ExistRoom", msg);

            //AddNewRoom_OR_ExistRoom
            Log.e("", "AddNewRoom_OR_ExistRoom수신");
            String[] msgArr = msg.split("[|]");
            // msgArr[1] : msg
            // msgArr[2] : SendUserID
            // msgArr[3] : RoomID
            // msgArr[4] : Date
            // msgArr[5] : RoomUser
            String chatMsg = msgArr[1];
            String SendID = msgArr[2];
            String RoomID = msgArr[3];
            String Date = msgArr[4];
            //String readNotUserSize = msgArr[8];
            String RoomUser;
            if (msgArr[5] != null) {
                RoomUser = msgArr[5];
            } else {
                RoomUser = "";
            }
            String type = msgArr[6];
            String ImageUri = msgArr[7];
            int ReadNotUserSize = Integer.valueOf(msgArr[8]);
            String readUser = msgArr[9];
            String msgID = msgArr[10];

            //해당 채팅방에 들어와 있으면 읽은 유저에 추가
            if (RoomID.trim().equals(isSeeRoomID)) {
//                readUser = readUser + "," + MyID;
//                ReadNotUserSize--;
                Log.e("isSeeRoomID", isSeeRoomID + "\n" + readUser + "\n" + ReadNotUserSize);
                // 서버로 메세지 전달
                toServerMsg = "EnterRoom|" + RoomID + "|" + MyID;
                receiveThread.send_msg(toServerMsg);

            }



            //현재시간 가져오기
//            String from = msgArr[4];
//            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date to = null;
//            try {
//                to = (Date) transFormat.parse(from);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            //고유식별ID 생성
            //String msgID = UUID.randomUUID().toString();

            if (msg.trim().startsWith("AddNewRoom")) {


                //새로운 방일경우 채팅방목록에 추가
                //RoomName : RoomUser에서 내ID를 뺀 나머지
                String[] UserArr = RoomUser.split(",");
                //Log.e("UserArr[0]", UserArr[0]);
                //Log.e("UserArr[1]", UserArr[1]);
                //                        Log.d("UserArr[2]",UserArr[2]);
                //                        Log.d("UserArr[3]",UserArr[3]);
                //                        Log.d("UserArr[4]",UserArr[4]);

                //사용자 아이디 가져옴
                SharedPreferences sf = getSharedPreferences("ChatUserID", Activity.MODE_PRIVATE);
                MyID = sf.getString("userid", "");
                //Log.e("MyID", MyID);
                String RoomName = "";
                for (int i = 0; i < UserArr.length; ++i) {
                    if (!UserArr[i].equals(MyID)) { //내ID가 아닐때
                        if (UserArr.length == 2) {// 채팅참여자가 나 포함 2명 일때
                            RoomName = UserArr[i];
                        } else {
                            if (RoomName.equals("")) {
                                RoomName = UserArr[i];
                            } else {
                                RoomName = RoomName + "," + UserArr[i];
                            }
                        }
                    }
                }
                //Log.e("SocketService", "채팅내용, 방정보 SQLite에 저장");

                if(readUser.trim().equals(MyID)){ //내가 보낸 메세지
                    RoomList_DB.insert(RoomID, RoomName, RoomUser, Date, chatMsg,"");//RoomID,RoomName, RoomUser, date
                }else{ //상대방이 보낸 메세지
                    RoomList_DB.insert(RoomID, RoomName, RoomUser, Date, chatMsg,1+"");//RoomID,RoomName, RoomUser, date
                }
                Log.e("채팅방저장완료", RoomID + " | " + RoomName + " | " + RoomUser + " | " + Date + "\n\n");

                //로컬DB에 채팅내용 저장
                if (Integer.valueOf(type) == 2) { //새로운방에서 이미지 전송시

                    //String[] roomUserArr = RoomUser.split(",");
                    //int ReadNotUserSize = roomUserArr.length-1; //전체구성원에서 나를 뺀수  = > 메시지 읽지 않은 사람 수


                    //SQLite에 채팅내용 저장 //새로운방에서 이미지 전송시
                    if(readUser.equals(MyID)){
                        ChattingDB.insert(RoomID, SendID, null, msgID, Date, type, ImageUri, ReadNotUserSize, readUser);//RoomID,RoomName, RoomUser, date
                    }else{
                        ChattingDB.insert(RoomID, SendID, null, msgID, Date, type, ImageUri, ReadNotUserSize-1, readUser+","+MyID);//RoomID,RoomName, RoomUser, date
                    }
                    // String RoomID, String UserID, String msg, String msgID, Date create_time, String type, String imgeUri, int ReadNotUserSize) {
                    Log.e("이미지 저장 완료", RoomID + " | " + SendID + " | " + msgID + " | " + type + " | " + ImageUri + " | " + ReadNotUserSize);

                } else {

                    // 메세지 저장
                    if(readUser.equals(MyID)){
                        ChattingDB.insert(RoomID, SendID, chatMsg, msgID, Date, 1 + "", null, ReadNotUserSize, readUser);
                    }else{
                        ChattingDB.insert(RoomID, SendID, chatMsg, msgID, Date, 1 + "", null, ReadNotUserSize-1, readUser+","+MyID);
                    }
                    Log.e("메세지 저장 완료", RoomID + " | " + SendID + " | " + chatMsg + " | " + msgID);
                }

                //Log.e("채팅내용저장완료", RoomID + " | " + SendID + " | " + chatMsg + " | " + msgID + " | " + Date + "\n\n");
            } else if (msg.trim().startsWith("ExistRoom")) {
                //로컬DB에 채팅내용 저장

                //String[] roomUserArr = RoomUser.split(",");
                //int ReadNotUserSize = roomUserArr.length-1; //전체구성원에서 나를 뺀수  = > 메시지 읽지 않은 사람 수

                if (Integer.valueOf(type) == 2) {

                    //기존방에서 이미지 전송시


                    //SQLite에 메세지정보 저장
                    if(readUser.equals(MyID)){
                        ChattingDB.insert(RoomID, SendID, null, msgID, Date, type, ImageUri, ReadNotUserSize, readUser);//RoomID,RoomName, RoomUser, date
                    }else{
                        ChattingDB.insert(RoomID, SendID, null, msgID, Date, type, ImageUri, ReadNotUserSize-1, readUser+","+MyID);//RoomID,RoomName, RoomUser, date
                    }
                    //Log.e("image" + "type", type + " | ImageUri  :" + ImageUri + " | ReadNotUserSize :" + ReadNotUserSize);
                    // String RoomID, String UserID, String msg, String msgID, Date create_time, String type, String imgeUri, int ReadNotUserSize) {
                } else {


                    // 메세지 저장
                    //Log.e("msg" + "type", type + " | ImageUri  :" + ImageUri + " | ReadNotUserSize :" + ReadNotUserSize);
                    if(readUser.equals(MyID)){
                        ChattingDB.insert(RoomID, SendID, chatMsg, msgID, Date, 1 + "", null, ReadNotUserSize, readUser);
                    }else{
                        ChattingDB.insert(RoomID, SendID, chatMsg, msgID, Date, 1 + "", null, ReadNotUserSize-1, readUser+","+MyID);
                    }
                }

                //Log.d("SocketService", "채팅내용, 방정보 SQLite에 저장");


               // Log.e("Date",Date);
                //내가보낸 메세지가 아니고 확인하지 않은 메세지 일때
                if(!RoomID.trim().equals(isSeeRoomID)&&!readUser.trim().equals(MyID)){
                    RoomList_DB.NotReadMsg(RoomID, chatMsg,1,Date);// 읽지 않은 (상대방)메세지
                } else{
                    RoomList_DB.MsgChange(RoomID, chatMsg,Date);// 메세지 내용만 업데이트
                }



//                if(readUser.trim().equals(MyID)){ //내가보낸메세지 일때
//
//                    ////내가보낸 메세지해당 채팅방을 보고 있을때
//                    if(RoomID.trim().equals(isSeeRoomID)){
//                        //SQLite에 방정보 저장
//                        RoomList_DB.update(RoomID, chatMsg,"",Date);//
//                    }else{ ////내가보낸 메세지 안보고 있을 때
//                        RoomList_DB.update(RoomID, chatMsg,"",Date);//
//                    }
//
//                }else { //상대방이 보낸 메세지 일때
//
//                    //상대방이 보낸 메세지 일때 해당 채팅방을 보고 있을때
//                    if(RoomID.trim().equals(isSeeRoomID)){
//                        //SQLite에 방정보 저장
//                        RoomList_DB.update(RoomID, chatMsg,"",Date);//
//                    }else{ //상대방이 보낸 메세지 일때 안보고 있을 때
//
//                    }
//
//
//                }

                Log.e("채팅방저장완료", RoomID + " | " + chatMsg +"\n\n");

            }

        }
    }//ClientChat extends Thread

    SQLite_Chatting ChattingDB;





}


