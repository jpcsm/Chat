package com.test.js.chat.Network;

/**
 * Created by lenovo on 2017-08-22.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

//http://itpsolver.com/protocol-%EC%84%A4%EA%B3%84%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%9D%B8%EC%A6%9D%EC%98%88%EC%A0%9C-%EC%86%8C%EC%BC%93-%ED%94%84%EB%A1%9C%ED%86%A0%EC%BD%9C-%EB%B0%94/
public class Protocol implements Serializable {


    //프로토콜 타입에 관한 변수
    public static final int PT_UNDEFINED = -1;   //프로토콜이 지정되어 있지 않을 경우에
    public static final int PT_EXIT = 0;
    public static final int PT_REQ_LOGIN = 1;   //로그인요청
    public static final int PT_RES_LOGIN = 2;   //인증요청
    public static final int PT_LOGIN_RESULT = 3;  //인증결과
    public static final int PT_FILE_SEND = 4;    //최대 데이타 길이


    //타입 크기 변수
//    public static final int LEN_LOGIN_ID = 20;   //ID길이
//    public static final int LEN_LOGIN_PASSWORD = 20; //PW길이
//    public static final int LEN_LOGIN_RESULT = 2;  //로그인인증값 길이
    public static final int LEN_PROTOCOL_TYPE = 1;  //프로토콜타입 길이
    public static final int LEN_MAX = 1000;    //최대 데이타 길이


    protected int protocolType;
    protected int dataSize;                       //전송횟수, 용량을 측정하는 변수
    String data;                                  // 전송할 데이터

    private byte[] packet = new byte[1026];                     //프로토콜과 데이터의 저장공간이 되는 바이트배열


    //생성자
    public Protocol(){

    }

    //생성자
    public Protocol(int protocolType, int dataSize, String data) {

        this.protocolType = protocolType;

//어떤 상수를 생성자에 넣어 Protocol 클래스를 생성하느냐에 따라서 바이트배열 packet 의 length 가 결정된다.
        getPacket(protocolType, dataSize, data);
    }


    public byte[] setPacket(int Type, int dataSize, byte[] data) {

        this.protocolType = Type;

        packet[0] = (byte) protocolType;   //packet 바이트배열의 첫번째 방에 프로토콜타입 상수를 셋팅해 놓는다.
        packet[1] = (byte) dataSize;       //
        System.arraycopy(data, 0, packet, 2, data.length);  // 배열 두번째 부터 데이터를 저장
//어떤 상수를 생성자에 넣어 Protocol 클래스를 생성하느냐에 따라서 바이트배열 packet 의 length 가 결정된다.
       // getPacket(protocolType, dataSize, data);
        return packet;
    }
    public byte[] getPacket(int protocolType, int dataSize, String data) {

//        if(packet == null){
//
//            switch(protocolType){
//                //paket 바이트배열 크기 정의
//                case PT_REQ_LOGIN : packet = new byte[LEN_PROTOCOL_TYPE]; break;
//                case PT_RES_LOGIN : packet = new byte[LEN_PROTOCOL_TYPE + LEN_LOGIN_ID + LEN_LOGIN_PASSWORD]; break;
//                case PT_UNDEFINED : packet = new byte[LEN_MAX]; break;
//                case PT_LOGIN_RESULT : packet = new byte[LEN_PROTOCOL_TYPE + LEN_LOGIN_RESULT]; break;
//                case PT_EXIT : packet = new byte[LEN_PROTOCOL_TYPE]; break;
//            }
//        }


        packet[0] = (byte) protocolType;   //packet 바이트배열의 첫번째 방에 프로토콜타입 상수를 셋팅해 놓는다.
        packet[1] = (byte) dataSize;
        //packet[2] = data.trim().getBytes();
        return packet;
    }


    //로그인후 성공/실패의 결과값을 프로토콜로 부터 추출하여 문자열로 리턴
//    public String getLoginResult() {
//        //String의 다음 생성자를 사용 : String(byte[] bytes, int offset, int length)
//        return new String(packet, LEN_PROTOCOL_TYPE, LEN_LOGIN_RESULT).trim();
//    }


    //    //File를 byte[] 로 만들어서 packet의 프로토콜 타입 바로 뒤에 추가한다.
    FileInputStream fin;

    public void setFile(File file) {
        String filename = "";
        int len;
        byte[] buffer = new byte[1024]; //바이트단위로 임시저장하는 버퍼를 생성합니다. 1kbyte만큼


        try {
            fin = new FileInputStream(file); //FileInputStream - 파일에서 입력받는 스트림을 개통합니다.

            while ((len = fin.read(buffer)) > 0) {     //FileInputStream을 통해 파일에서 입력받은 데이터를 버퍼에 임시저장하고 그 길이를 측정합니다.
                dataSize++;                        //데이터의 양을 측정합니다. 배열개수
            }


            for (; dataSize > 0; dataSize--) {                   //데이터를 읽어올 횟수만큼 FileInputStream에서 파일의 내용을 읽어옵니다.
                len = fin.read(buffer);        //FileInputStream을 통해 파일에서 입력받은 데이터를 버퍼에 임시저장하고 그 길이를 측정합니다.
                // out.write(buffer, 0, len);       //서버에게 파일의 정보(1kbyte만큼보내고, 그 길이를 보냅니다.
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.arraycopy(ok.trim().getBytes(), 0, packet, LEN_PROTOCOL_TYPE, ok.trim().getBytes().length);
    }

    //String ok를 byte[] 로 만들어서 packet의 프로토콜 타입 바로 뒤에 추가한다.
    public void setDate(String data) {


        //arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
//        Object src : 복사하려는 대상-원본
//        int srcPos : 복사할 시작위치
//        Object dest : 붙여넣기 할 대상 - 복사본
//        int destPos : 붙여넣기 할 시작 위치
//        int length : 원본에서 복사본까지 얼마큼 읽어 올지
        System.arraycopy(data.trim().getBytes(), 0, packet, 2, data.trim().getBytes().length);  // 배열 두번째 부터 데이터를 저장
    }


    public void setProtocolType(int protocolType) {
        this.protocolType = protocolType;
    }


    public int getProtocolType() {
        return protocolType;
    }


    public byte[] getPacket() {
        return packet;
    }


    //Default 생성자로 생성한 후 Protocol 클래스의 packet 데이타를 바꾸기 위한 메서드
//    public void setPacket(int pt, byte[] buf) {
//        packet = null;
//        packet = getPacket(pt);
//        protocolType = pt;
//        System.arraycopy(buf, 0, packet, 0, packet.length);                  // 배열에서 원하는 부분만 복사한다는 의미
//    }


//    public String getId() {
//        //String(byte[] bytes, int offset, int length)
//        return new String(packet, LEN_PROTOCOL_TYPE, LEN_LOGIN_ID).trim();
//    }


    //byte[] packet 에 String ID를 byte[]로 만들어 프로토콜 타입 바로 뒷부분에 추가한다.
    public void setId(String id) {
        System.arraycopy(id.trim().getBytes(), 0, packet, LEN_PROTOCOL_TYPE, id.trim().getBytes().length);
    }


//    public String getPassword() {
//        //구성으로 보아 패스워드는 byte[] 에서 로그인 아이디 바로 뒷부분에 들어가는 듯 하다.
//        return new String(packet, LEN_PROTOCOL_TYPE + LEN_LOGIN_ID, LEN_LOGIN_PASSWORD).trim();
//    }
//
//
//    public void setPassword(String password) {
//        System.arraycopy(password.trim().getBytes(), 0, packet, LEN_PROTOCOL_TYPE + LEN_LOGIN_ID, password.trim().getBytes().length);
//        packet[LEN_PROTOCOL_TYPE + LEN_LOGIN_ID + password.trim().getBytes().length] = '\0';
//    }

}
