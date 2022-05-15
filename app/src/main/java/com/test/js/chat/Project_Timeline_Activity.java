package com.test.js.chat;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.test.js.chat.Home.HomeFragment.Friend_invite_Activity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.test.js.chat.Home.HomeFragment.Friend_invite_Activity.PROJECT_TIMELINE;
import static com.test.js.chat.Home_Activity.MyID;

public class Project_Timeline_Activity extends AppCompatActivity {
    String projectid, projectname, projectusers = null;
    FloatingActionButton btn_invite_project;

    Timeline_Adapter timeline_adapter;
    RecyclerView mRecyclerView;

    Timeline_item item;
    RecyclerView.LayoutManager mLayoutManger;
    SQLite_RoomList RoomList_DB;
    //static MessageHandler mProjectHandler = null;//  핸들러
    Button btn_writer_timeline;

    static int WRITING_TASK = 3;
    static int PLAY_TO_VIDEO = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //SQLite 객체생성
        RoomList_DB = new SQLite_RoomList(getApplicationContext(), "chat.db", null, 1);

        //레이아웃매니저 추가
        GridLayoutManager gm = new GridLayoutManager(getApplicationContext(), 2);
        //리사이클러뷰 생성
        timeline_adapter = new Timeline_Adapter(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_projec_timeline);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gm);
        //구분선 추가
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mLayoutManger = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.setAdapter(timeline_adapter);

        //핸들러 객체생성
        //mProjectHandler = new MessageHandler();// resiver스레드에서 서버에서 받은 메세지 가져옴



        Intent intent = getIntent();
        projectid = intent.getStringExtra("projectid");
        projectname = intent.getStringExtra("projectname");
        projectusers = intent.getStringExtra("projectusers");
        Log.e("projectid",projectid);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(projectname);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //친구초대 버튼클릭리스너
        btn_invite_project = (FloatingActionButton) findViewById(R.id.btn_invite_project);
        btn_invite_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Friend_invite_Activity.class);
                intent.putExtra("Prev", 1);
                intent.addFlags(intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityForResult(intent, PROJECT_TIMELINE);
            }


        });

        //게시글 작성 버튼
        btn_writer_timeline = (Button)findViewById(R.id.btn_writer_timeline);
        btn_writer_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WritingTaskActivity.class);
                intent.putExtra("Prev", 1);
                intent.addFlags(intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityForResult(intent, WRITING_TASK);
            }
        });
    }

    public void Btn_Click_VideoPlay(View v) {
        Intent intent = new Intent(getApplicationContext(), VideoPlayActivity.class);
        intent.putExtra("Prev", 1);
        intent.addFlags(intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, PLAY_TO_VIDEO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //리사이클러뷰 초기화
        timeline_adapter.clear();
        //채팅방정보 SQLite에서 가져와서 리사이클뷰에 뿌려준다
        mRecyclerView.clearOnChildAttachStateChangeListeners();

//        for (int i = RoomList_DB.getProjectCount(); i >= 1; --i) {
//            String[] ProjectArray = RoomList_DB.select_project(i);
//
//            //idx |projectid| projectname | projectimage | projectusers |create_time | adminuser
//            item = new project_item();
//            item.setProject_Name(ProjectArray[2]);
//            //item.setRoom_size(false);
//            item.setProject_Users(ProjectArray[4]);
//            item.setProjectID(ProjectArray[1]);
//            item.setadmin(ProjectArray[6]);
//            project_adapter.add(item);
//            Log.e ("Project_Fragment","onResume : "+ProjectArray[0]+" | "+ProjectArray[1]+" | "+ProjectArray[2]+" | "+ProjectArray[3]+" | "+ProjectArray[4]+" | "+ProjectArray[5]+" | "+ProjectArray[6]);
//        }
        item = new Timeline_item();
        item.setcontentText("응용2단계 작품 진행사항");
        //item.setRoom_size(false);
        item.setwriter("user1");
        item.setwriteTime("2017-08-06");
        timeline_adapter.add(item);

        item = new Timeline_item();
        item.setcontentText("개발 5주차 발표 내용");
        //item.setRoom_size(false);
        item.setwriter("user2");
        item.setwriteTime("2017-08-06");
        timeline_adapter.add(item);

        item = new Timeline_item();
        item.setcontentText("디자인 진행사항");
        //item.setRoom_size(false);
        item.setwriter("user1");
        item.setwriteTime("2017-08-06");
        timeline_adapter.add(item);

        item = new Timeline_item();
        item.setcontentText("공지사항");
        //item.setRoom_size(false);
        item.setwriter("user2");
        item.setwriteTime("2017-08-06");
        timeline_adapter.add(item);

        item = new Timeline_item();
        item.setcontentText("응용2단계 작품 진행사항");
        //item.setRoom_size(false);
        item.setwriter("user1");
        item.setwriteTime("2017-08-06");
        timeline_adapter.add(item);

        item = new Timeline_item();
        item.setcontentText("개발 5주차 발표 내용");
        //item.setRoom_size(false);
        item.setwriter("user2");
        item.setwriteTime("2017-08-06");
        timeline_adapter.add(item);

        item = new Timeline_item();
        item.setcontentText("디자인 진행사항");
        //item.setRoom_size(false);
        item.setwriter("user1");
        item.setwriteTime("2017-08-06");
        timeline_adapter.add(item);

        item = new Timeline_item();
        item.setcontentText("공지사항");
        //item.setRoom_size(false);
        item.setwriter("user2");
        item.setwriteTime("2017-08-06");
        timeline_adapter.add(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }
    // Handler 클래스
//    class MessageHandler extends Handler {
//
//
//        final static  int MSG_PROJECT_FRAGMENT = 13;
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) { //서버에서 메세지 오면
//                case MSG_PROJECT_FRAGMENT:
//                    Log.d("13", "MSG_PROJECT_FRAGMENT: 프로젝트 프래그먼트 ");
//                    onResume(); //프래그먼트 초기화
//
//                    break;
//                case 2:
//                    timeline_adapter.add(item);
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    String Friend_ID;

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        projectusers = null;

        if (data != null) {
            switch (requestCode) {
                case PROJECT_TIMELINE://친구선택 완료후 선택한 친구목록 리턴
                    //선택한 친구목록을 가져온다 - 초대할 유저목록
                    try {
                        int usercount = data.getIntExtra("usercount", 0); //유저수
                        String userarray = data.getStringExtra("userarray");

                        Log.e("usercount", usercount + "\nuserarray : " + userarray);
                        JSONObject json = new JSONObject(userarray);//{"UserID":["user2","user3"]}
                        if (usercount == 1) { //1명 {"UserID":"user8"}
                            Friend_ID = json.get("UserID").toString();//user8
                            projectusers = Friend_ID;
                            Log.d("Friend_ID", Friend_ID);
                        } else if (usercount > 1) { //2명 이상  {"UserID":["user2","user3"]}

                            String users = json.get("UserID").toString();//["user2","user3"]
                            Friend_ID = users.substring(1, users.length() - 1);//"user2","user3"
                            Friend_ID = Friend_ID.substring(1, Friend_ID.length() - 1);//  user2","user3
                            Friend_ID = Friend_ID.replace("\",\"", ",");//  user2,user3
                            String[] Friend_ID_arry = Friend_ID.split("[|]");
                            String friends = null;
                            for (int i = 0; i < Friend_ID_arry.length; i++) {
                                if (i == 0) {
                                    friends = Friend_ID_arry[i];
                                } else {
                                    friends = friends + "," + Friend_ID_arry[i];
                                }

                            }
                            projectusers = friends;
                            Log.d("2명 이상 json Friend_ID", json.getString("UserID") + " / " + json.get("UserID"));
                        }

                        //sender 스레드로 EditText값 전달
                        String toServerMsg = "ProjectAddUser" + "|" + projectid + "|" + projectusers+"|" +MyID+"|" +projectname;
                        // 방id와 참여자아이디, 메세지내용을 서버에 전송
                        //clientChat.send_msg(toServerMsg); //서버에메세지 전송

                        //서비스에 전송메세지 전달
                        Home_Activity.sendToService(SocketService.MSG_SET_VALUE,toServerMsg);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
                default:
            }
        }

    }
}
