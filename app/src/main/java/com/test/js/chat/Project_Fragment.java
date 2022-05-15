package com.test.js.chat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import static com.test.js.chat.Home_Activity.MyID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Project_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Project_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Project_Fragment extends Fragment {
    // 홈(프로젝트 생성 탭)
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Project_Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Project_Fragment newInstance() {
        Project_Fragment fragment = new Project_Fragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    Project_Adapter project_adapter;
    RecyclerView mRecyclerView;

    project_item item;
    RecyclerView.LayoutManager mLayoutManger;
    SQLite_RoomList RoomList_DB;
    static MessageHandler mProjectHandler = null;//  핸들러


    POST post;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_project_, container, false);
        Log.d("onCreateView", "onCreateView");
        //SQLite 객체생성
        RoomList_DB = new SQLite_RoomList(getActivity().getApplicationContext(), "chat.db", null, 1);

        //레이아웃매니저 추가
        GridLayoutManager gm = new GridLayoutManager(getContext(), 2);
        //리사이클러뷰 생성
        project_adapter = new Project_Adapter(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.RV_project);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gm);
        //구분선 추가
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mLayoutManger = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.setAdapter(project_adapter);

        //핸들러 객체생성
        mProjectHandler = new MessageHandler();// resiver스레드에서 서버에서 받은 메세지 가져옴


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
   //        //리사이클러뷰 초기화
        project_adapter.clear();
        //채팅방정보 SQLite에서 가져와서 리사이클뷰에 뿌려준다
        mRecyclerView.clearOnChildAttachStateChangeListeners();

        for (int i = RoomList_DB.getProjectCount(); i >= 1; --i) {
            String[] ProjectArray = RoomList_DB.select_project(i);

            //idx |projectid| projectname | projectimage | projectusers |create_time | adminuser
            item = new project_item();
            item.setProject_Name(ProjectArray[2]);
            //item.setRoom_size(false);
            item.setProject_Users(ProjectArray[4]);
            item.setProjectID(ProjectArray[1]);
            item.setadmin(ProjectArray[6]);
            project_adapter.add(item);
            Log.e ("Project_Fragment","onResume : "+ProjectArray[0]+" | "+ProjectArray[1]+" | "+ProjectArray[2]+" | "+ProjectArray[3]+" | "+ProjectArray[4]+" | "+ProjectArray[5]+" | "+ProjectArray[6]);
        }

    }
    String result;String str;
    public void http() { //http로 Json데이터전송

        //  Log.v(TAG, value);
//Toast.makeText(getApplicationContext(),"프로젝트 생성 : ",Toast.LENGTH_SHORT).show();
        String PROJECT_ID = UUID.randomUUID().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("userid",MyID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        str = jsonObject.toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                POST post = new POST();
                result = post.POST("http://jpcsm9003.vps.phps.kr/db/SELECT_project.php", str);
                Log.e("result",result+"");
                //Toast.makeText(getContext(),"result : "+result,Toast.LENGTH_SHORT).show();



                if (result != null) {
                    //상품정보 어댑터에 추가
                    try {
                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            item = new project_item();

                            JSONObject jObject = jsonArray.getJSONObject(i);  // JSONObject 추출
                            String projectid = jObject.getString("projectid");
                            String projectname = jObject.getString("projectname");
                            String projectimage = jObject.getString("projectimage");
                            String projectusers = jObject.getString("projectusers");
                            String create_time = jObject.getString("create_time");
                            String adminuser = jObject.getString("adminuser");
                            //Log.d("Name",Name);
                            item.setProject_Name(projectname);
                            item.setProject_Users(projectusers);
                            item.setProjectID(projectid);

                            //DB에 저장( projectid ,projectname ,adminuser ,projectusers,String create_time)
                            RoomList_DB.insert_project(projectid,projectname,adminuser,projectusers,create_time);
                            Log.e ("Project_Fragment","onResume : "+projectid+" | "+projectname+" | "+
                                    adminuser+" | "+projectusers+" | "+create_time);
//                            getActivity().runOnUiThread(new Runnable() { public void run() {
//                                /// / 메시지 큐에 저장될 메시지의 내용
//                                project_adapter.add(item);
//                            } });


                            //Log.d("상품목록스레드결과",result);
                        }
//                        Message msg = new Message();
//                        msg.what = 2;
//                        mProjectHandler.handleMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    //서버연결실패시 예외처리
                    //Toast.makeText(getApplicationContext(), "서버연결에 실패하였습니다"+result.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("서버연결실패", result);
                }
            }
        }).start();
    }
    // Handler 클래스
    class MessageHandler extends Handler {


        final static  int MSG_PROJECT_FRAGMENT = 13;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) { //서버에서 메세지 오면
                case MSG_PROJECT_FRAGMENT:
                    Log.d("13", "MSG_PROJECT_FRAGMENT: 프로젝트 프래그먼트 ");
                    onResume(); //프래그먼트 초기화

                    break;
                case 2:
                    project_adapter.add(item);
                    break;

                default:
                    break;
            }
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}


