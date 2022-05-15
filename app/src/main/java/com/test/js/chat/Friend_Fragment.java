//package com.test.js.chat;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
///**
// * Created by lenovo on 2017-08-18.
// */
//public class Friend_Fragment extends Fragment { //친구목록 프래그먼트
//    FriendList_Adapter friendList_adapter;
//    RecyclerView mRecyclerView;
//    FloatingActionButton btn_Addroom;
//    FriendList_item item;
//    RecyclerView.LayoutManager mLayoutManger;
//    SQLite_RoomList RoomList_DB;
//    SharedPreferences sp;
//    SharedPreferences.Editor ed;
//
//    private OnFragmentInteractionListener mListener;
//
//    public Friend_Fragment() {
//    }
//
//    // TODO: Rename and change types and number of parameters
//    public static Friend_Fragment newInstance() {
//        Friend_Fragment fragment = new Friend_Fragment();
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
////            mParam1 = getArguments().getString(ARG_PARAM1);
////            mParam2 = getArguments().getString(ARG_PARAM2);
//            sp = getActivity().getSharedPreferences("chat", Activity.MODE_PRIVATE);//프로필이미지  MyProfileImageURI
//            ed = sp.edit();
//        }
//    }
//
//    String MyID;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_friend_, container, false);
//        Log.d("onCreateView", "onCreateView");
//
//        //사용자 아이디 가져옴
//        SharedPreferences sf = getActivity().getSharedPreferences("ChatUserID", Activity.MODE_PRIVATE);
//        MyID = sf.getString("userid", "");
//        Log.d("MyID", MyID);
//
//        //SQLite 객체생성
//        RoomList_DB = new SQLite_RoomList(getActivity().getApplicationContext(), "chat.db", null, 1);
//
//        // RoomList_DB.onOpen(db);
//        //리사이클러뷰 생성
//        friendList_adapter = new FriendList_Adapter(getContext());
//        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.Rv_FriendList);
//        //리사이클러뷰 리스트구분선 추가
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//        mLayoutManger = new LinearLayoutManager(getContext());
//        mRecyclerView.setLayoutManager(mLayoutManger);
//        mRecyclerView.setAdapter(friendList_adapter);
//
//
//        return rootView;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        sp = getActivity().getSharedPreferences("chat", Activity.MODE_PRIVATE);//프로필이미지  MyProfileImageURI
//
//        Log.d("RoomList_Fragment", "onResume");
////        Toast.makeText(getContext(),"onResume() 방리스트 리사이클러뷰 초기화",Toast.LENGTH_SHORT).show();
//        //리사이클러뷰 초기화
//        friendList_adapter.clear();
//        //채팅방정보 SQLite에서 가져와서 리사이클뷰에 뿌려준다
//        mRecyclerView.clearOnChildAttachStateChangeListeners();
////        for(int i=RoomList_DB.getRoomCount();i>=1;--i){
////            String[] RoomArray = RoomList_DB.select(i);
////
////            //RoomArray[] 0 : idx , 1 : RoomID, 2 : RoomName, 3 : RoomUser , 4 : Date
////            item = new FriendList_item();
////            item.setName(친구이름);
////            friendList_adapter.add(item);
////            //  Log.d ("Project_Fragment","onResume : "+RoomArray[2]);
////        }
//        item = new FriendList_item();
//        item.setMyName(MyID);
//        item.setCheck(false);
//        if (sp.getString("MyProfileImageURI", null) != null) {
//            item.setProfileImageURI(Uri.parse(sp.getString("MyProfileImageURI", null)));//프로필이미지 uri
//        }
//        item.setCheckBox_GONE(true);
//        friendList_adapter.add(item);
//
//        if (!MyID.equals("user1")) {
//            item = new FriendList_item();
//            item.setName("user1");
//            item.setCheck(false);
//            item.setCheckBox_GONE(true);
//            friendList_adapter.add(item);
//        }
//
//        if (!MyID.equals("user2")) {
//            item = new FriendList_item();
//            item.setName("user2");
//            item.setCheck(false);
//            item.setCheckBox_GONE(true);
//            friendList_adapter.add(item);
//        }
//
//
//        if (!MyID.equals("user3")) {
//            item = new FriendList_item();
//            item.setName("user3");
//            item.setCheck(false);
//            item.setCheckBox_GONE(true);
//            friendList_adapter.add(item);
//        }
//
//        if (!MyID.equals("user4")) {
//            item = new FriendList_item();
//            item.setName("user4");
//            item.setCheck(false);
//            item.setCheckBox_GONE(true);
//            friendList_adapter.add(item);
//        }
//
//        if (!MyID.equals("user5")) {
//            item = new FriendList_item();
//            item.setName("user5");
//            item.setCheck(false);
//            item.setCheckBox_GONE(true);
//            friendList_adapter.add(item);
//        }
//
//        if (!MyID.equals("user6")) {
//            item = new FriendList_item();
//            item.setName("user7");
//            item.setCheck(false);
//            item.setCheckBox_GONE(true);
//            friendList_adapter.add(item);
//        }
//
//        if (!MyID.equals("user7")) {
//            item = new FriendList_item();
//            item.setName("user7");
//            item.setCheck(false);
//            item.setCheckBox_GONE(true);
//            friendList_adapter.add(item);
//        }
//
//        if (!MyID.equals("user8")) {
//            item = new FriendList_item();
//            item.setName("user8");
//            item.setCheck(false);
//            item.setCheckBox_GONE(true);
//            friendList_adapter.add(item);
//        }
//
//        if (!MyID.equals("user9")) {
//            item = new FriendList_item();
//            item.setName("user9");
//            item.setCheck(false);
//            item.setCheckBox_GONE(true);
//            friendList_adapter.add(item);
//        }
//
//        if (!MyID.equals("user10")) {
//            item = new FriendList_item();
//            item.setName("user10");
//            item.setCheck(false);
//            item.setCheckBox_GONE(true);
//            friendList_adapter.add(item);
//        }
//
//
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
////        if (context instanceof OnFragmentInteractionListener) {
////            mListener = (OnFragmentInteractionListener) context;
////        } else {
////            throw new RuntimeException(context.toString()
////                    + " must implement OnFragmentInteractionListener");
////        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
//}
