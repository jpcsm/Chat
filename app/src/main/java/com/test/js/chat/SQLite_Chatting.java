package com.test.js.chat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lenovo on 2017-07-06.
 */

public class SQLite_Chatting extends SQLiteOpenHelper { // 채팅내용 저장 SQLite

    //  생성자로 관리할 DB 이름과 버전 정보를 받음
    public SQLite_Chatting(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        //Log.e("SQLite_Chatting"," onOpen() ");
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 RoomList이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        RoomName 문자열 컬럼, RoomUser String[]컬럼으로 구성된 테이블을 생성. */
        String sql = "CREATE TABLE Chatting (idx INTEGER PRIMARY KEY AUTOINCREMENT, RoomID String, UserID TEXT, msg String, msgID String," +
                "  create_time String, type String, imageuri String, ReadNotUserSize  INTEGER , ReadUsers String);";
        db.execSQL(sql);

        Log.e("SQLite_Chatting", " onCreate() : " + sql);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

//    public void insert(String RoomID, String UserID, String msg, String msgID, String create_time) {
//        // 읽고 쓰기가 가능하게 DB 열기
//        SQLiteDatabase db = getWritableDatabase();
//        // DB에 입력한 값으로 행 추가
//        db.execSQL("INSERT INTO Chatting VALUES(null,'" + RoomID + "', '" + UserID + "', '" + msg + "', '" + msgID + "', '" + create_time + "',null,null,null);");
//        db.close();
//    }

    public void insert(String RoomID, String UserID, String msg, String msgID, String create_time,
                       String type, String imgeUri, int ReadNotUserSize, String ReadUsers) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO Chatting VALUES(null,'" + RoomID + "', '" + UserID + "', '" + msg + "', '" +
                msgID + "', '" + create_time + "','" + type + "','" + imgeUri + "','" + ReadNotUserSize +
                "','" + ReadUsers + "');");
        db.close();
    }


//    public void update(String RoomID, int ReadNotUserSize, String ReadUsers) {
//        // 읽고 쓰기가 가능하게 DB 열기
//        SQLiteDatabase db = getWritableDatabase();
//        // DB에 입력한 값으로 행 추가
//        db.execSQL("UPDATE Chatting SET ReadNotUserSize =" + ReadNotUserSize +
//                ", ReadUsers ='" + ReadUsers + "' WHERE RoomID='" + RoomID + "';");
//        db.close();
//    }

    public void update(String msgID, int ReadNotUserSize, String ReadUsers) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("UPDATE Chatting SET ReadNotUserSize =" + ReadNotUserSize +
                ", ReadUsers ='" + ReadUsers + "' WHERE msgID='" + msgID + "';");
        db.close();
    }
//    public void update(String RoomID, String RoomUser) {
//        SQLiteDatabase db = getWritableDatabase();
//        // 입력한 항목과 일치하는 행의 유저 정보 수정
//        db.execSQL("UPDATE RoomList SET RoomUser=" + RoomUser + " WHERE RoomID='" + RoomID + "';");
//        db.close();
//    }

    //    public void delete(String msgID) {
//        SQLiteDatabase db = getWritableDatabase();
//        // 입력한 항목과 일치하는 행 삭제
//        db.execSQL("DELETE FROM Chatting WHERE msgID='" + msgID + "';");
//        db.close();
//    }
//
    public String[] select_RoomID(int idx, String RoomID) {
        SQLiteDatabase db = getReadableDatabase();//읽기 가능하게 열기
        String[] result = new String[15];

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        //RoomID로 방정보 검색
        Cursor cursor = db.rawQuery("SELECT * FROM Chatting WHERE RoomID ='" + RoomID + "' AND idx = '" + idx + "'  ;", null);
        while (cursor.moveToNext()) {
            result[0] = cursor.getString(0);//idx
            result[1] = cursor.getString(1);//RoomID
            result[2] = cursor.getString(2);//UserID
            result[3] = cursor.getString(3);//msg
            result[4] = cursor.getString(4);//msgID
            result[5] = cursor.getString(5);//create_time
            result[6] = cursor.getString(6);//type
            result[7] = cursor.getString(7);//imageUri
            result[8] = cursor.getInt(8) + "";//ReadNotUserSize
            result[9] = cursor.getString(9);//ReadUsers

        }
        if (cursor != null) cursor.close();
        db.close();
        return result;
    }

    public String[] select_MsgID(String msgID) {
        SQLiteDatabase db = getReadableDatabase();//읽기 가능하게 열기
        String[] result = new String[15];

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        //RoomID로 방정보 검색
        Cursor cursor = db.rawQuery("SELECT * FROM Chatting WHERE msgID ='" + msgID + "  ;", null);
        while (cursor.moveToNext()) {
            result[0] = cursor.getString(0);//idx
            result[1] = cursor.getString(1);//RoomID
            result[2] = cursor.getString(2);//UserID
            result[3] = cursor.getString(3);//msg
            result[4] = cursor.getString(4);//msgID
            result[5] = cursor.getString(5);//create_time
            result[6] = cursor.getString(6);//type
            result[7] = cursor.getString(7);//imageUri
            result[8] = cursor.getInt(8) + "";//ReadNotUserSize
            result[9] = cursor.getString(9);//ReadUsers
        }
        db.close();
        if (cursor != null) cursor.close();
        return result;
    }

    //
//    public String[] select_RoomName(String RoomName){
//        SQLiteDatabase db = getReadableDatabase();//읽기 가능하게 열기
//        String[] result = new String[5];
//
//        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
//        //RoomID로 방정보 검색
//        Cursor cursor = db.rawQuery("SELECT * FROM Chatting WHERE RoomName ='" + RoomName + "';", null);
//        while (cursor.moveToNext()) {
//            result[0] =cursor.getString(0);
//            result[1] =cursor.getString(1);
//            result[2] =cursor.getString(2);
//            result[3] =cursor.getString(3);
//            result[4] =cursor.getString(4);
//        }
//        db.close();
//        return result;
//    }
//
    public String[] select(int idx) {
        SQLiteDatabase db = getReadableDatabase();//읽기 가능하게 열기
        String[] result = new String[15];

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        //idx로 방정보 검색
        Cursor cursor = db.rawQuery("SELECT * FROM Chatting WHERE idx='" + idx + "';", null);
        while (cursor.moveToNext()) {
            result[0] = cursor.getString(0);//idx
            result[1] = cursor.getString(1);//RoomID
            result[2] = cursor.getString(2);//UserID
            result[3] = cursor.getString(3);//msg
            result[4] = cursor.getString(4);//msgID
            result[5] = cursor.getString(5);//create_time
            result[6] = cursor.getString(6);//type
            result[7] = cursor.getString(7);//imageUri
            result[8] = cursor.getInt(8) + "";//ReadNotUserSize
            result[9] = cursor.getString(9);//ReadUsers
        }
        db.close();
        if (cursor != null) cursor.close();
        return result;
    }


//    public String[] getAlldata() { // 테이블 전체 데이터 가져오기
//        // 읽기가 가능하게 DB 열기
//        SQLiteDatabase db = getReadableDatabase();
//        String[] result = new String[7];
//       //idx INTEGER PRIMARY KEY AUTOINCREMENT, RoomID String, UserID TEXT, msg String, msgID String,  create_time String );";
//
//        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
//        Cursor cursor = db.rawQuery("SELECT * FROM Chatting", null);
//        while (cursor.moveToNext()) {
////            result += cursor.getString(0) //인덱스
////                    + " | "
////                    + cursor.getString(1) // RoomName
////                    + " | "
////                    + cursor.getInt(2) // RoomUesr
////                    + " | "
////                    + cursor.getString(3) // create_time
////                    + "\n";
//            result[0] =cursor.getString(0);//idx
//            result[1] =cursor.getString(1);//RoomID
//            result[2] =cursor.getString(2);//UserID
//            result[3] =cursor.getString(3);//msg
//            result[4] =cursor.getString(4);//msgID
//            result[5] =cursor.getString(5);//create_time
//        }
//        db.close();
//
//        return result;
//    }

    public int getChatCount() { //채팅 Row 개수 가져오기
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Chatting", null);
//        cursor.close();
//        db.close();
        // return count

        int result = cursor.getCount();
        if (cursor != null) cursor.close();
        return result;

    }


}


