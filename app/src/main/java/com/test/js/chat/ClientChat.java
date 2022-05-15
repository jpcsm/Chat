//package com.test.js.chat;
//
///**
// * Created by lenovo on 2017-06-28.
// */
//
//import android.content.Context;
//import android.os.Message;
//import android.util.Log;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.net.SocketException;
//
//import static com.test.js.chat.GotoChattingRoomActivity.mMainHandler;
//import static com.test.js.chat.RoomList_Fragment.mRoomListHandler;
//
///* 멀티채팅 클라이언트 스레드*/
//public class ClientChat extends Thread{ //소켓생성스레드
//
//    static boolean chatmode = false;
//    static int chatState = 0;
//    Socket socket;
//    //0 : 로그온 안된상태, 1 : 로그온된상태, 2: 방입장완료 (대화가능),
//    //3 : 상대방이 1:1대화요청한 상태 ,
//    //5 : req_fileSend (상대방이 현재사용자에게 파일전송을 수락요청한 상태)
//    Context context;
//    DataOutputStream out;
//    DataInputStream in;
//    String ClientID;
//    static final int  MSG_FROM_SERVER=0;
//    public ClientChat(Context context,String ClientID){
//        this.context = context;
//        this.ClientID = ClientID;
//    }
//
//    @Override
//    public void run() {
//        super.run();
//
//
//        try{
//            String ServerIP = "jpcsm9003.vps.phps.kr";
//            socket = new Socket(ServerIP, 9999); //소켓 객체 생성
//            //사용자로부터 얻은 문자열을 서버로 전송해주는 역할을 하는 쓰레드.
//
//            //Thread sender = new Sender(socket);  //서버로 메세지를 보내는 클래스
//            //Thread receiver = new Receiver(socket);    //서버로부터 메시지를 받는 클래스
//            // handler.sendEmptyMessage(0);
//            //sender.start(); //스레드 시작
//            //receiver.start(); //스레드 시작
//            in = new DataInputStream(this.socket.getInputStream());
//            out = new DataOutputStream(this.socket.getOutputStream());
//            out.writeUTF("req_logon|"+ClientID);
//            while(socket!=null){
//               // this.sleep(1000);
//                Log.d("ClientChat","Thread run();");
//                receiver(); //서버로부터 메세지 받음
//            }
//        }catch(Exception e){
//            Log.d("socket객체생성실패",""+e);
//        }
//    }
//
////    public Handler handler = new Handler(){
////
////        @Override
////        public void handleMessage(Message msg) {
////
////            if(msg.what == 0){
////                Toast.makeText(context, "socket객체생성성공 ", Toast.LENGTH_SHORT).show();
////            }else if(msg.what == 1){
////
////            }else if(msg.what == 2){
////            }
////
////        }
////    };
//    //서버로메세지 보내기
//    public void send_msg(String msg){
//        try {
//            out.writeUTF(msg);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    /**메시지 파서*/
//    public String[] getMsgParse(String msg){
//        //System.out.println("msgParse()=>msg?"+ msg);
//
//        String[] tmpArr = msg.split("[|]");
//
//        return tmpArr;
//    }
//    public void receiver() {
//
//
//        while(in!=null){ //입력스트림이 null이 아니면..반복
//            try{
//
//
//                String msg = in.readUTF(); //입력스트림을 통해 읽어온 문자열을 msg에 할당.
//                //Toast.makeText(context,"서버로부터 받음"+msg,Toast.LENGTH_SHORT).show();
//                Log.d("서버로부터 받은 메세지",msg);
//
//                //메인액티비티로 핸들러를 통해 서버에서 온 메세지 전달
//                // 메시지 얻어오기
//                Message message = mMainHandler.obtainMessage();
//                // 메시지 ID 설정
//                message.what = MSG_FROM_SERVER;
////                // 메시지 정보 설정 (int 형식)
////                message.arg1 = Integer.valueOf(getNowDateTime());
//                // 메시지 정보 설정3 (Object 형식)
//                String hi = new String(msg);
//                message.obj = hi;
//                mMainHandler.sendMessage(message);
//
//                Message message2 = mRoomListHandler.obtainMessage();
//                // 메시지 ID 설정
//                message2.what = MSG_FROM_SERVER;
////                // 메시지 정보 설정 (int 형식)
////                message.arg1 = Integer.valueOf(getNowDateTime());
//                // 메시지 정보 설정3 (Object 형식)
//                message2.obj = hi;
//                mRoomListHandler.sendMessage(message2);
//                //beforeFunction(msg);
//
//
//
//
//                String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));
//
//                //메세지 처리 ----------------------------------------------
//                if(msg.startsWith("logon#yes")){ //로그온 시도 (대화명)
//                    ClientChat.chatState = 1; //채팅 상태를 로그온 된 상태로 변경.
//                    //logon#yes|그룹리스트
//                    //System.out.println(msgArr[0]); //방목록
//                    //System.out.println("방이름을 입력해 주세요:");
//
//                }else  if(msg.startsWith("logon#no")){ //로그온 실패 (대화명)
//
//                    ClientChat.chatState = 0;
//                    //System.out.println("[##] 중복된 아이디가 존재합니다. 다시 입력하세요.:");
//                    //1. 이름이 중복될경우(서버전체 or 그룹) logon#no 패킷이 서버로부터 전달됨.
//                    //2. 이름이 중복될경우 서버에서 자체적으로 name(1), name(2) 이런식으로 중복되지 않게 변경하는 방법.
//
//                }else if(msg.startsWith("enterRoom#yes")){ //그룹입장
//
//                    //enterRoom#yes|지역
//                   // System.out.println("[##] 채팅방 ("+msgArr[0]+") 에 입장하였습니다.");
//                    ClientChat.chatState = 2; //챗 상태 변경 ( 채팅방입장 완료로 대화가능상태)
//
//                }else if(msg.startsWith("enterRoom#no")){
//                    //enterRoom#no|지역
//                    //System.out.println("[##] 입력하신 ["+msgArr[0]+ "]는 존재하지않는 지역입니다.");
//                   // System.out.println("▶지역을 다시 입력해 주세요:");
//
//                }
//
//
//
//                else if(msg.startsWith("show")){ //서버에서 받은 메시지
//
//                    //show|메시지내용
//                    //System.out.println(msgArr[0]);
//
//                }
//                else if(msg.startsWith("say")){ //대화내용
//                    // say|아이디|대화내용
//                    // System.out.println("["+msgArr[0]+"] "+msgArr[1] );
//
//                }
//
//
//
//
//
//                else if(msg.startsWith("whisper")){ //귓속말
//                    //whisper|아이디|대화내용
//                  //  System.out.println("[귓]["+msgArr[0]+"] "+msgArr[1] );
//
//                }else if(msg.startsWith("req_PvPchat")){ //해당 사용자에게 1:1대화 요청
//                    //req_PvPchat|출력내용
//                    ClientChat.chatState = 3; //챗 상태 변경 (상대방이 1:1대화신청을 했을경우)
//                    //MultiClient.chatmode=true; //Sender에게 1:1요청이 들어왔다는것을 알려주기 위함
//                    //System.out.println(msgArr[0]); //메세지만 추출
//                   // System.out.print("▶선택:");
//
//                }else if(msg.startsWith("req_fileSend")){ //상대방이 현재 사용자에게 파일전송 수락 요청
//                    //req_fileSend|출력내용
//                    //req_fileSend|[##] name 님께서 파일 전송을 시도합니다. 수락하시겠습니까?(Y/N)
//                    ClientChat.chatState = 5; //상태 변경 (상대방이 현재사용자에게 파일전송을 수락요청한 상태)
//                   // System.out.println(msgArr[0]); //메세지만 추출
//                   // System.out.print("▶선택:");
//                   // sleep(100);
//                }
////                else if(msg.startsWith("fileSender")){ //파일을 보내기위해 파일서버 준비
////
////                    //fileSender|filepath;
////                    System.out.println("fileSender:"+InetAddress.getLocalHost().getHostAddress());
////                    System.out.println("fileSender:"+msgArr[0]);
////                    //String ip=InetAddress.getLocalHost().getHostAddress();
////
////                    try {
////                        new FileSender(msgArr[0]).start(); //쓰레드 실행.
////                    } catch (Exception e) {
////                        System.out.println("FileSender 쓰레드 오류:");
////                        e.printStackTrace();
////                    }
////
////                }
////                else if(msg.startsWith("fileReceiver")){ //파일받기
////                    //fileReceiver|ip|fileName;
////
////                    System.out.println("fileReceiver:"+InetAddress.getLocalHost().getHostAddress());
////                    System.out.println("fileReceiver:"+msgArr[0]+"/"+msgArr[1]);
////
////                    String ip = msgArr[0];  //서버의 아이피를 전달 받음
////                    String fileName = msgArr[1]; //서버에서 전송할 파일이름.
////
////                    try {
////                        new FileReceiver(ip,fileName).start(); //쓰레드 실행.
////                    } catch (Exception e) {
////                        System.out.println("FileSender 쓰레드 오류:");
////                        e.printStackTrace();
////                    }
////
////
////                }
//                else if(msg.startsWith("req_exit")){ //종료
//                    }
//
//            }catch(SocketException e){
//               //System.out.println("예외:"+e);
//               // System.out.println("##접속중인 서버와 연결이 끊어졌습니다.");
//                return;
//
//            } catch(Exception e){
//                Log.e("Receiver:run() 예외:",""+e);
//
//            }//try catch
//        }//while
//    }//receiver
//
////    public void beforeFunction(final String toMainMsg ) {
////        Runnable rn = new Runnable()
////        {
////
////            @Override
////
////            public void run()
////
////            {
////            // 딜레이 걸리는 처리
////                //String str = toMainMsg ;
////
////                Bundle bd = new Bundle() ;      /// 번들 생성
////                bd.putString( "arg", toMainMsg ) ;    /// 번들에 값 넣기
////                Message msg = m_hd.obtainMessage( ) ;   /// 핸들에 전달할 메세지 구조체 받기
////                msg.setData( bd ) ;                     /// 메세지에 번들 넣기
////                m_hd.sendMessage( msg ) ;               /// 메세지를 전달
////            }
////
////        };
////        /// 여기서 왠만하면 프로스레스 띄우기
////        new Thread( rn ).start() ;
////    }
//
//
////
////    Handler m_hd = new Handler( ) {
////        public void handleMessage(android.os.Message msg) {
////            Bundle bd = msg.getData( ) ;            /// 전달 받은 메세지에서 번들을 받음
////            String str = bd.getString( "arg" ) ;    /// 번들에 들어있는 값 꺼냄
////            ////// 이후 처리
////            afterFunction( str ) ;
////        }
////    } ;
////
////    void afterFunction( String str ){
////        /// 스래드 처리가 끝난 다음 할 일
////        /// 여기서 프로그레스 제거
////    }
//
//}//ClientChat extends Thread
//
//
//
//
//
///////////////////////////////////////////////////////////////////////
//
////서버로부터 메시지를 읽는 클래스
////class Receiver extends Thread{
////
////    Socket socket;
////    DataInputStream in;
////
////    //Socket을 매개변수로 받는 생성자.
////    public Receiver(Socket socket){
////        this.socket = socket;
////
////        try{
////            in = new DataInputStream(this.socket.getInputStream());
////        }catch(Exception e){
////            System.out.println("Receiver 생성자:"+e);
////        }
////    }//생성자 --------------------
////
////
////    /**메시지 파서*/
////    public String[] getMsgParse(String msg){
////        //System.out.println("msgParse()=>msg?"+ msg);
////
////        String[] tmpArr = msg.split("[|]");
////
////        return tmpArr;
////    }
////
////
////
////    @Override
////    public void run(){ //run()메소드 재정의
////
////        while(in!=null){ //입력스트림이 null이 아니면..반복
////
////        }//while----
////    }//run()------
////}//class Receiver -------
//
///////////////////////////////////////////////////////////////
//
////서버로 메시지를 전송하는 클래스
////class Sender extends Thread {
////    Socket socket;
////    DataOutputStream out;
////    String name;
////    String msg;
////
////    public void send_msg(String msg){
////        this.msg =msg;
////        try {
////            out.writeUTF(this.msg);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }
////
////
////    //생성자 ( 매개변수로 소켓과 사용자 이름 받습니다. )
////    public Sender(Socket socket){ //소켓과 채팅내용을 받는다.
////        this.socket = socket;
////
////        try{
////            out = new DataOutputStream(this.socket.getOutputStream());
////        }catch(Exception e){
////            Log.d(" Sender 생성자 :","Exception"+e);
////        }
////
////    }
////
////    @Override
////    public void run(){ //run()메소드 재정의
////
////        //while(out!=null){ //출력스트림이 null이 아니면..반복
////            try { //while문 안에 try-catch문을 사용한 이유는 while문 내부에서 예외가 발생하더라도
////                //계속 반복할수있게 하기위해서이다.
////
////
////                //메인스레드에게 텍스트내용을 전달 받는다
////                msg
////
////                if(msg==null||msg.trim().equals("")){
////
////                    msg=" ";
////                    //continue; //콘솔에선 공백으로 넘기는것이 좀더 효과적임.
////                    //System.out.println("공백");
////                }
////
////                if(ClientChat.chatState == 0){
////                    //추후 대화명 관련 처리시 사용.
////
////                    if(!msg.trim().equals("")){
////                        name=msg;
////                        out.writeUTF("req_logon|"+msg);
////
////                    }else{
////                        System.out.println("[##] 공백을 입력할수없습니다.\r\n" +
////                                "아이디를 다시 입력해 주세요:");
////                    }
////
////                }else if(ClientChat.chatState == 1) {//로그온된 상태이며 그룹방을 입력받기위한 상태
////                    //req_enterRoom|대화명|지역명
////
////                    if(!msg.trim().equals("")){
////                        out.writeUTF("req_enterRoom|"+name+"|"+msg);
////                    }else{
////                        System.out.println("[##] 공백을 입력할수없습니다.\r\n" +
////                                "방이름을 다시 입력해 주세요:");
////                    }
////
////
////                }else if(msg.trim().startsWith("/")){
////                    //명령어 기능 추가. ( /접속자 , /귓속말 상대방아이디 전달할메시지... 등 )
////                    //클라이언트단에서 체크
////
////                    //확장성을 위해 위 코드를 수정.
////
////                    if(msg.equals("/초대")||msg.equalsIgnoreCase("/invite")){
////                        out.writeUTF("req_invite|"+name+"|"+msg); // 채팅방에 친구초대하기
////
////                    }
////
////                    else{
////                        out.writeUTF("req_cmdMsg|"+name+"|"+msg);
////                        //req_cmdMsg|대화명|/접속자
////                    }
////
////                }
////                else{
////                    //req_say|아이디|대화내용
////                    out.writeUTF("req_say|"+name+"|"+msg);
////                }
//////                /*else if(msg.startsWith("/귓속말")){
//////
//////                    out.writeUTF("req_whisper|"+name+"|"+msg);
//////
//////                }*/
//////                else if(MultiClient.chatState==3){ //3 : 상대방이 1:1대화요청한 상태 ,
//////                    //PvPchat|result)
//////                    msg = msg.trim(); //메시지 공백제거
//////
//////                    if(msg.equalsIgnoreCase("y")){
//////                        out.writeUTF("PvPchat|yes");
//////                    }else if(msg.equalsIgnoreCase("n")){
//////                        out.writeUTF("PvPchat|no");
//////                    }else{
//////                        System.out.println("입력한 값이 올바르지 않습니다.");
//////                        out.writeUTF("PvPchat|no");
//////                    }
//////                    MultiClient.chatState=2; //1:1대화 요청에 응답완료 상태
//////
//////                }
//////                else if(MultiClient.chatState == 5) { //5 : 상대방이 파일전송을 시도하여 사용자의 수락요청을 기다림.
//////                    //fileSend|result)
//////                    if(msg.trim().equalsIgnoreCase("y")){
//////                        out.writeUTF("fileSend|yes");
//////                    }else if(msg.trim().equalsIgnoreCase("n")){
//////                        out.writeUTF("fileSend|no");
//////                    }else{
//////                        System.out.println("입력한 값이 올바르지 않습니다.");
//////                        out.writeUTF("fileSend|no");
//////                    }
//////
//////                    MultiClient.chatState=2; //파일전송수락요청에대한 응답완료 상태
//////
//////                }
////
////
////            }catch(SocketException e){
////                System.out.println("Sender:run() SocketException:"+e);
////                System.out.println("##접속중인 서버와 연결이 끊어졌습니다.");
////                return;
////            } catch (IOException e) {
////                System.out.println("Sender:run() IOException:"+e);
////            }
////        //}//while------
////
////    }//run()------
////}//class Sender-------
//
