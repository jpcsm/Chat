package com.test.js.chat.Home.HomeFragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.test.js.chat.GotoChattingRoomActivity;
import com.test.js.chat.Project_Timeline_Activity;
import com.test.js.chat.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.test.js.chat.Home_Activity.MyID;

public class Friend_invite_Activity extends AppCompatActivity {
    FriendList_Adapter friendList_Adapter;
    RecyclerView friendRecyclerView;
    Button btn_cancel;
    Button btn_confirm;
    FriendList_item item;
    RecyclerView.LayoutManager mLayoutManger;

    public final static int PROJECT_TIMELINE = 1;
    final static int CREAT_CHATTINGROOM = 2;
    int Prev_Activity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invite);



        Intent intent = getIntent();
        Prev_Activity = intent.getIntExtra("Prev",2); //이전 액티비티가 무엇인지?
        if(Prev_Activity==PROJECT_TIMELINE){
            setTitle("프로젝트 초대");
        }else if(Prev_Activity==CREAT_CHATTINGROOM){
            setTitle("대화상대 초대");
        }
         Log.e("Prev_Activity : ",Prev_Activity+"");
        //리사이클러뷰 생성
        friendList_Adapter = new FriendList_Adapter(getApplicationContext());
        friendRecyclerView = (RecyclerView) findViewById(R.id.Rv_FriendinviteList);
        mLayoutManger = new LinearLayoutManager(this);
        friendRecyclerView.setLayoutManager(mLayoutManger);
        friendRecyclerView.setAdapter(friendList_Adapter);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject jsonObject = new JSONObject();

                //String[] a = new String[friendList_Adapter.items.size()];

                //친구목록어댑터에서 선택한 친구목록 가져오기
                for (int i = 0; i < friendList_Adapter.items.size(); i++) {
                    if (friendList_Adapter.items.get(i).getCheck()) {
                        try {
                            jsonObject.accumulate("UserID", friendList_Adapter.items.get(i).getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

//                chattingroom_item item = new chattingroom_item();
//                item.setRoom("방이름");
//                item.setRoom_size(1);
//                chattingRoom_Adapter.add(item);

                //채팅방에 참여한 유저 정보
//                String[] userarray = new String[2];
//                userarray[0]="user1";
//                userarray[1]="user1";

                Log.d("jsonObject.toString()", jsonObject.toString());
                Log.d("getCheckCount", "" + friendList_Adapter.getCheckCount());
                //선택한 친구수가 한 명 이상일 때 채팅액티비티로 데이터 넘겨준다
                if (friendList_Adapter.getCheckCount() > 0) {

                    if(Prev_Activity==PROJECT_TIMELINE){//프로젝트 친구추가일때
                        Intent intent = new Intent(getApplicationContext(), Project_Timeline_Activity.class);
                        intent.putExtra("userarray", jsonObject.toString());
                        intent.putExtra("usercount", friendList_Adapter.getCheckCount());
                        //intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        setResult(PROJECT_TIMELINE,intent);
                    }else if(Prev_Activity==CREAT_CHATTINGROOM){// 채팅방 생성시 친구추가일 때
                        Intent intent = new Intent(getApplicationContext(), GotoChattingRoomActivity.class);
                        intent.putExtra("userarray", jsonObject.toString());
                        intent.putExtra("newroom", true);
                        intent.putExtra("roomstate", "AddNewRoom");
                        intent.putExtra("usercount", friendList_Adapter.getCheckCount());
                        startActivity(intent);
                    }

                } else { // 미선택시 버튼 비활성화

                }


                finish();
            }
        });

        friendRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Friend_invite_Activity", "friendRecyclerView.onClick()");
                //friendList_Adapter.getCheckCount(); //선택한친구 수
                if (friendList_Adapter.getCheckCount() == 0) {
                    //선택한 친구가 없을때 버튼 비활성화
                    btn_confirm.setEnabled(false);// 확인버튼 비활성화
                } else {
                    //선택한 친구가 있을 때 확인 버튼활성화
                    btn_confirm.setEnabled(true);//
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences("chat", Activity.MODE_PRIVATE);//프로필이미지  MyProfileImageURI
        if (!MyID.equals("user1")) {
            item = new FriendList_item();
            item.setName("user1");
            item.setCheck(false);
            friendList_Adapter.add(item);
        }

        if (!MyID.equals("user2")) {
            item = new FriendList_item();
            item.setName("user2");
            item.setCheck(false);
            friendList_Adapter.add(item);
        }
        if (!MyID.equals("user3")) {
            item = new FriendList_item();
            item.setName("user3");
            item.setCheck(false);
            friendList_Adapter.add(item);
        } if (!MyID.equals("user4")) {
            item = new FriendList_item();
            item.setName("user4");
            item.setCheck(false);
            friendList_Adapter.add(item);
        }
        if (!MyID.equals("user5")) {
            item = new FriendList_item();
            item.setName("user5");
            item.setCheck(false);
            friendList_Adapter.add(item);
        }
        if (!MyID.equals("user6")) {
            item = new FriendList_item();
            item.setName("user6");
            item.setCheck(false);
            friendList_Adapter.add(item);
        }
        if (!MyID.equals("user7")) {
            item = new FriendList_item();
            item.setName("user7");
            item.setCheck(false);
            friendList_Adapter.add(item);
        }
        if (!MyID.equals("user8")) {
            item = new FriendList_item();
            item.setName("user8");
            item.setCheck(false);
            friendList_Adapter.add(item);
        }
        if (!MyID.equals("user9")) {
            item = new FriendList_item();
            item.setName("user9");
            item.setCheck(false);
            friendList_Adapter.add(item);
        }
        if (!MyID.equals("user10")) {
            item = new FriendList_item();
            item.setName("user10");
            item.setCheck(false);
            friendList_Adapter.add(item);
        }

    }
}
