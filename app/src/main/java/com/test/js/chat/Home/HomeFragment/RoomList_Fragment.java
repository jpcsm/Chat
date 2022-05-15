//package com.test.js.chat.Home.HomeFragment;
//
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.test.js.chat.R;
//import com.test.js.chat.SQLite_RoomList;
//
//import static com.test.js.chat.SocketService.ReceiveThread.MSG_FROM_SERVER;
//
///**
// * Created by lenovo on 2017-08-18.
// */
//@SuppressLint("ValidFragment")
//public class RoomList_Fragment extends Fragment {
//    // 채팅방목록 탭
//
//    private OnFragmentInteractionListener mListener;
//
//    public RoomList_Fragment() {
//        // Required empty public constructor
//    }
//
//    public static RoomList_Fragment newInstance() {
//        RoomList_Fragment fragment = new RoomList_Fragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
//    }
//
//    ChattingRoom_Adapter chattingRoom_Adapter;
//    RecyclerView mRecyclerView;
//
//    chattingroom_item item;
//    RecyclerView.LayoutManager mLayoutManger;
//    SQLite_RoomList RoomList_DB;
//    static RoomListHandler mRoomListHandler = null;//  핸들러
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chattingroom, container, false);
//        Log.d("onCreateView", "onCreateView");
//        //SQLite 객체생성
//        RoomList_DB = new SQLite_RoomList(getActivity().getApplicationContext(), "chat.db", null, 1);
//
//        // RoomList_DB.onOpen(db);
//        //리사이클러뷰 생성
//        chattingRoom_Adapter = new ChattingRoom_Adapter(getActivity());
//        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.Rv_RoomList);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//        mLayoutManger = new LinearLayoutManager(getContext());
//        mRecyclerView.setLayoutManager(mLayoutManger);
//        mRecyclerView.setAdapter(chattingRoom_Adapter);
//
//        //핸들러 객체생성
//        mRoomListHandler = new RoomListHandler();// resiver스레드에서 서버에서 받은 메세지 가져옴
//
//
//        return rootView;
//    }
//
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d("RoomList_Fragment", "onResume");
////        Toast.makeText(getContext(),"onResume() 방리스트 리사이클러뷰 초기화",Toast.LENGTH_SHORT).show();
//        //리사이클러뷰 초기화
//        chattingRoom_Adapter.clear();
//        //채팅방정보 SQLite에서 가져와서 리사이클뷰에 뿌려준다
//        mRecyclerView.clearOnChildAttachStateChangeListeners();
//        for (int i = RoomList_DB.getRoomCount(); i >= 1; --i) {
//            String[] RoomArray = RoomList_DB.select(i);
//
//            //RoomArray[] 0 : idx , 1 : RoomID, 2 : RoomName, 3 : RoomUser , 4 : Date
//            item = new chattingroom_item();
//            item.setRoom(RoomArray[2]);
//            //item.setRoom_size(false);
//            item.setUser(RoomArray[3]);
//            item.setRoomID(RoomArray[1]);
//            chattingRoom_Adapter.add(item);
//            //  Log.d ("Project_Fragment","onResume : "+RoomArray[2]);
//        }
//
//    }
//
//
//    // Handler 클래스
//    class RoomListHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            switch (msg.what) { //서버에서 메세지 오면
//                case MSG_FROM_SERVER:
//                    Log.d("RoomListHandler", "방리스트 리사이클러뷰 초기화");
////                    String msgArr = msg.obj.toString(); //msgArr  msg|MyID|RoomID|Date|RoomUser
////                    String[] data = msgArr.split("[|]");
//
////                    Log.d("data[0]",data[0]);
////                    Log.d("data[1]",data[1]);
////                    Log.d("data[2]",data[2]);
////                    Log.d("data[3]",data[3]);
////
//                    onResume(); //프래그먼트 초기화
////                    Log.d("data[4]",data[4]);
//
////                    if(data[4]!=null){// RoomUesr를 보내줄경우 새로운 방생성
////                        //RoomName : RoomUser에서 내ID를 뺀 나머지
////                        String[] UserArr = data[4].split(",");
////                        Log.d("UserArr[0]",UserArr[0]);
////                        Log.d("UserArr[1]",UserArr[1]);
//////                        Log.d("UserArr[2]",UserArr[2]);
//////                        Log.d("UserArr[3]",UserArr[3]);
//////                        Log.d("UserArr[4]",UserArr[4]);
////                        Log.d("MyID",MyID);
////                        RoomName = "";
////                        for(int i =0;i<UserArr.length;++i){
////                            if(!UserArr[i].equals(MyID)){ //내ID가 아닐때
////                                if(UserArr.length==2){// 채팅참여자가 나 포함 2명 일때
////                                    RoomName = UserArr[i];
////                                }else{
////                                    if(RoomName.equals("")){
////                                        RoomName = UserArr[i];
////                                    }else{
////                                        RoomName = RoomName+","+UserArr[i];
////                                    }
////                                }
////                            }
////                        }
////                        Log.d("RoomName",RoomName);
////                        //SQLite에 방정보 저장
////                        RoomList_DB.insert(data[2],RoomName, data[4], date);//RoomID,RoomName, RoomUser, date
////                    }else if(data[4]==null){ //RoomUesr가 없을 경우 기존 방
////
////                    }
//                    ;
//                    //채팅내용 리사이클러뷰에 추가
////                    item = new chat_item();
////                    item.setName(data[1]);// 채팅발송자 아이디
////                    item.setUser_chat(data[0]);
////                    mAdapter.add(item);
//
//                    break;
//
//                case 1:
////                    mCountThread.stopThread();
////                    tv_Count.setText("Count Thread가 중지 되었습니다.");
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        Log.d("onAttach", "onAttach");
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        Log.d("onDetach", "onDetach");
//        mListener = null;
//    }
//
//    public interface OnFragmentInteractionListener {
//        void onFragmentInteraction(Uri uri);
//
//    }
//}
//
