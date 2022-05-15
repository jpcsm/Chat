package com.test.js.chat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lenovo on 2017-06-29.
 */

public class SQLite_RoomList extends SQLiteOpenHelper {

    //  생성자로 관리할 DB 이름과 버전 정보를 받음
    public SQLite_RoomList(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        Log.d("SQLite_RoomList"," onOpen() ");
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 RoomList이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        RoomName 문자열 컬럼, RoomUser String[]컬럼으로 구성된 테이블을 생성. */
        String sql = "CREATE TABLE RoomList (idx INTEGER PRIMARY KEY AUTOINCREMENT, RoomID String, RoomName TEXT, RoomUser String, Date TEXT, LastMessaga TEXT, NewMsgSize TEXT);";
        db.execSQL(sql);
        Log.d("SQLite_RoomList"," onCreate() : "+sql);
        String sql1 = "CREATE TABLE project (idx INTEGER  PRIMARY KEY AUTOINCREMENT, ProjectID TEXT, ProjectName TEXT, ProjectImage TEXT, ProjectUsers TEXT, Date TEXT, adminUser TEXT );";
        db.execSQL(sql1);
        Log.d("Creat_project",sql1);

    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String RoomID, String RoomName, String RoomUser, String Date , String LastMessaga, String NewMsgSize) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO RoomList VALUES(null,'" + RoomID + "', '" + RoomName + "'," +
                " '" + RoomUser + "', '" + Date + "', '" + LastMessaga + "', '" + NewMsgSize + "');");
        db.close();
    }

    public void insert_project( String projectid ,String projectname ,String adminuser ,
                                String projectusers,String Date) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        String sql =   "INSERT INTO project (projectid, projectname ,adminuser, projectusers," +
                " Date) VALUES( '"+projectid+"' , '"+projectname+"'  , '"+adminuser+"' ," +
                " '"+projectusers+"' ,'"+Date+"');";
        db.execSQL(sql);
        db.close();
    }
    public void update_project( String projectid ,String projectname ,String adminuser ,
                                String projectusers) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        String sql =   "UPDATE project SET projectname = '" + projectname + "', adminuser = '"
                + adminuser + "' , " + "projectusers = '" + projectusers + "' WHERE projectid = '" + projectid + "';";
        db.execSQL(sql);
        db.close();
    }
    public void update(String RoomID, String LastMessaga, String NewMsgSize , String Date) {
            SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 유저 정보 수정
        db.execSQL("UPDATE RoomList SET LastMessaga='" + LastMessaga + "', NewMsgSize ='" + NewMsgSize + "', Date ='" + Date + "'  WHERE RoomID='" + RoomID + "';");
        db.close();
    }
    public void EnterRoom(String RoomID, String NewMsgSize) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE RoomList SET  NewMsgSize ='" + NewMsgSize  + "'  WHERE RoomID='" + RoomID + "';");
        db.close();
    }
    public void MsgChange(String RoomID, String LastMessaga, String Date) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 유저 정보 수정
        db.execSQL("UPDATE RoomList SET LastMessaga='" + LastMessaga + "', Date ='" + Date + "'  WHERE RoomID='" + RoomID + "';");
        db.close();
    }
    public void NotReadMsg(String RoomID, String LastMessaga ,int NewMsgSize, String Date) {
        //읽지않은 메세지
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 유저 정보 수정
        db.execSQL("UPDATE RoomList SET LastMessaga='" + LastMessaga + "', NewMsgSize = NewMsgSize+"+NewMsgSize+" ,Date ='" + Date + "' WHERE RoomID='" + RoomID + "';");
        db.close();
    }
    public void update(String RoomID, String RoomUser) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 유저 정보 수정omID='" + Room
        db.execSQL("UPDATE RoomList SET RoomUser=" + RoomUser + " WHERE RoomID = '"+RoomID+"';");
        db.close();
    }

    public void delete(String RoomID) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM RoomList WHERE RoomID='" + RoomID + "';");
        db.close();
    }

    public String[] select_RoomID(String RoomID){
        SQLiteDatabase db = getReadableDatabase();//읽기 가능하게 열기
        String[] result = new String[10];

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        //RoomID로 방정보 검색
        Cursor cursor = db.rawQuery("SELECT * FROM RoomList WHERE RoomID ='" + RoomID + "';", null);
        while (cursor.moveToNext()) {
            result[0] =cursor.getString(0);
            result[1] =cursor.getString(1);
            result[2] =cursor.getString(2);
            result[3] =cursor.getString(3);
            result[4] =cursor.getString(4);
            result[5] =cursor.getString(5);// lastMsg
            result[6] =cursor.getString(6);// NewMsgSize
        }
        db.close();
        return result;
    }

    public String[] select_RoomName(String RoomName){
        SQLiteDatabase db = getReadableDatabase();//읽기 가능하게 열기
        String[] result = new String[10];

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        //RoomID로 방정보 검색
        Cursor cursor = db.rawQuery("SELECT * FROM RoomList WHERE RoomName ='" + RoomName + "';", null);
        while (cursor.moveToNext()) {
            result[0] =cursor.getString(0);//idx
            result[1] =cursor.getString(1);//RoomID
            result[2] =cursor.getString(2);//Roomname
            result[3] =cursor.getString(3);//RoomUser
            result[4] =cursor.getString(4);//creat_time
            result[5] =cursor.getString(5);// lastMsg
            result[6] =cursor.getString(6);// NewMsgSize
        }
        db.close();
        return result;
    }

    public String[] select(int idx){
            SQLiteDatabase db = getReadableDatabase();//읽기 가능하게 열기
            String[] result = new String[10];

            // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
            //idx로 방정보 검색
            Cursor cursor = db.rawQuery("SELECT * FROM RoomList WHERE idx='" + idx + "';", null);
            while (cursor.moveToNext()) {
            result[0] =cursor.getString(0);// idx
            result[1] =cursor.getString(1);// RoomID
            result[2] =cursor.getString(2);// RoomName
            result[3] =cursor.getString(3);// RoomUser
            result[4] =cursor.getString(4);// Date
            result[5] =cursor.getString(5);// lastMsg
            result[6] =cursor.getString(6);// NewMsgSize
        }
        db.close();
        return result;
    }
    public String[] select_project(int idx){
        SQLiteDatabase db = getReadableDatabase();//읽기 가능하게 열기
        String[] result = new String[10];

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        //idx로 방정보 검색
        Cursor cursor = db.rawQuery("SELECT * FROM project WHERE idx='" + idx + "';", null);
        while (cursor.moveToNext()) {
            result[0] =cursor.getString(0);// idx
            result[1] =cursor.getString(1);// ProjectID
            result[2] =cursor.getString(2);// ProjectName
            result[3] =cursor.getString(3);// ProjectImage
            result[4] =cursor.getString(4);// ProjectUsers
            result[5] =cursor.getString(5);// Date
            result[6] =cursor.getString(6);// adminUser
        }
        db.close();
        return result;
    }

    public String[] select_project(String id){
        SQLiteDatabase db = getReadableDatabase();//읽기 가능하게 열기
        String[] result = new String[10];

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        //idx로 방정보 검색
        Cursor cursor = db.rawQuery("SELECT * FROM project WHERE ProjectID='" + id + "';", null);
        while (cursor.moveToNext()) {
            result[0] =cursor.getString(0);// idx
            result[1] =cursor.getString(1);// ProjectID
            result[2] =cursor.getString(2);// ProjectName
            result[3] =cursor.getString(3);// ProjectImage
            result[4] =cursor.getString(4);// ProjectUsers
            result[5] =cursor.getString(5);// Date
            result[6] =cursor.getString(6);// adminUser
        }
        db.close();
        return result;
    }

    public String[] getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String[] result = new String[10];

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM RoomList", null);
        while (cursor.moveToNext()) {
//            result += cursor.getString(0) //인덱스
//                    + " | "
//                    + cursor.getString(1) // RoomName
//                    + " | "
//                    + cursor.getInt(2) // RoomUesr
//                    + " | "
//                    + cursor.getString(3) // Date
//                    + "\n";
            result[0] =cursor.getString(0);//
            result[1] =cursor.getString(1);
            result[2] =cursor.getString(2);
            result[3] =cursor.getString(3);
            result[4] =cursor.getString(4);
            result[5] =cursor.getString(5);// lastMsg
            result[6] =cursor.getString(6);// NewMsgSize
        }
        db.close();

        return result;
    }

    public int getRoomCount() { //방개수 가져오기
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM RoomList", null);
//        cursor.close();
//        db.close();
        // return count
        return cursor.getCount();
    }

    public int getProjectCount() { //방개수 가져오기
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM project", null);
//        cursor.close();
//        db.close();
        // return count
        return cursor.getCount();
    }

}


