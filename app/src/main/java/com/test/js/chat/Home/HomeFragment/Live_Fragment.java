package com.test.js.chat.Home.HomeFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.test.js.chat.Live_Streaming.VLCstrreaming;
import com.test.js.chat.R;
import com.test.js.chat.SQLite_RoomList;

import java.util.ArrayList;
import java.util.List;

import static com.test.js.chat.SocketService.MSG_LIVE_FRAGMENT;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Project_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Project_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Live_Fragment extends Fragment {
    // 홈(프로젝트 생성 탭)
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Live_Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Live_Fragment newInstance() {
        Live_Fragment fragment = new Live_Fragment();
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


    Live_Adapter Live_adapter;
    RecyclerView mRecyclerView;

    Live_item item;
    RecyclerView.LayoutManager mLayoutManger;
    SQLite_RoomList RoomList_DB;
    public static MessageHandler mLiveHandler = null;//  핸들러

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_live, container, false);
        Log.d("onCreateView", "onCreateView");
        //SQLite 객체생성
        RoomList_DB = new SQLite_RoomList(getActivity().getApplicationContext(), "chat.db", null, 1);

        //레이아웃매니저 추가
        GridLayoutManager gm = new GridLayoutManager(getContext(), 2);
        //리사이클러뷰 생성
        Live_adapter = new Live_Adapter(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.RV_project);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gm);
        //구분선 추가
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mLayoutManger = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.setAdapter(Live_adapter);

        //핸들러 객체생성
        mLiveHandler = new MessageHandler();// resiver스레드에서 서버에서 받은 메세지 가져옴


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //        //리사이클러뷰 초기화
        Live_adapter.clear();
        //채팅방정보 SQLite에서 가져와서 리사이클뷰에 뿌려준다
        mRecyclerView.clearOnChildAttachStateChangeListeners();
        Log.d("onResume","");
//
//        for (int i = RoomList_DB.getProjectCount(); i >= 1; --i) {
//            String[] ProjectArray = RoomList_DB.select_project(i);
//
//            //idx |projectid| projectname | projectimage | projectusers |create_time | adminuser
//            item = new Live_item();
//            item.setProject_Name(ProjectArray[2]);
//            //item.setRoom_size(false);
//            item.setProject_Users(ProjectArray[4]);
//            item.setProjectID(ProjectArray[1]);
//            item.setadmin(ProjectArray[6]);
//            Live_adapter.add(item);
//            Log.e("Project_Fragment", "onResume : " + ProjectArray[0] + " | " + ProjectArray[1] + " | " + ProjectArray[2] + " | " + ProjectArray[3] + " | " + ProjectArray[4] + " | " + ProjectArray[5] + " | " + ProjectArray[6]);
//        }



    }

    String result;
    String str;
    String LiveID;
    String livepublisher;
    String livetitle;
    String liveusersize;

    // Handler 클래스
    public class MessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) { //서버에서 메세지 오면
                case MSG_LIVE_FRAGMENT:
                    Log.d("13", "MSG_LIVE_FRAGMENT: 프로젝트 프래그먼트 ");

                    String arr = (String) msg.obj;
                    String[] msgArr = arr.split("[|]");
                    LiveID = msgArr[1];
                    livepublisher = msgArr[2];
                    livetitle = msgArr[3];
                    liveusersize = msgArr[4];
                    onResume(); //프래그먼트 초기화

                    item = new Live_item();
                    item.setLive_ID(LiveID);
                    //item.setRoom_size(false);
                    item.setLive_publisher(livepublisher);
                    item.setLive_Title(livetitle);
                    //item.setLive_Usersize("liveusersize");
                    Live_adapter.add(item);

                    break;
                case 2:
                    Live_adapter.add(item);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

class Live_ViewHolder extends RecyclerView.ViewHolder {


    public TextView tv_live_title, tv_live_publisher, tv_live_user_size;
    public ImageView iv_live_preview;

    public Live_ViewHolder(View itemView) {
        super(itemView);

        tv_live_title = (TextView) itemView.findViewById(R.id.tv_live_title);
        tv_live_publisher = (TextView) itemView.findViewById(R.id.tv_live_publisher);
        iv_live_preview = (ImageView) itemView.findViewById(R.id.iv_live_preview);
        tv_live_user_size = (TextView) itemView.findViewById(R.id.tv_live_user_size);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onListItemClick(getAdapterPosition());
            }
        });
    }

    public interface OnListItemClickListener {
        public void onListItemClick(int position);

    }

    OnListItemClickListener mListener;

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        mListener = onListItemClickListener;
    }
}

class Live_Adapter extends RecyclerView.Adapter<Live_ViewHolder> implements Live_ViewHolder.OnListItemClickListener {
    Context context;

    public Live_Adapter(Context context) {
        this.context = context;
    }


    @Override
    public Live_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_cardview, parent, false);
        Live_ViewHolder holder = new Live_ViewHolder(v);
        holder.setOnListItemClickListener(this);
        return holder;

    }

    //어댑터아이템 추가후 데이터 반영
    List<Live_item> items = new ArrayList<>(); //채팅정보 리스트객체 생성

    public void add(Live_item data) {
        items.add(data);
        notifyDataSetChanged();// 변경데이터 뷰에 반영
    }

    String ServerUri = "http://jpcsm9003.vps.phps.kr";
//    public class RotateTransformation extends BitmapTransformation {
//
//        private float rotateRotationAngle = 0f;
//
//        public RotateTransformation(Context context, float rotateRotationAngle) {
//            super( context );
//
//            this.rotateRotationAngle = rotateRotationAngle;
//        }
//
//        @Override
//        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
//            Matrix matrix = new Matrix();
//
//            matrix.postRotate(rotateRotationAngle);
//
//            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
//        }
//
//        @Override
//        public String getId() {
//            return "rotate" + rotateRotationAngle;
//        }
//    }

    @Override
    public void onBindViewHolder(Live_ViewHolder holder, int position) {
        //뷰홀더를 데이터와 바인딩 , 데이터를 뷰에 그려준다
        Live_item item = items.get(position);

        Log.e("onBindViewHolder",ServerUri + "/uploads/LiveThumbnail_" + item.getLive_ID() + ".jpg");
        //사용자와 채팅내용을 리사이클뷰에 그려준다
        holder.tv_live_title.setText(item.getLive_Title());
        //holder.tv_live_user_size.setText(item.getLive_Usersize());
        holder.tv_live_publisher.setText(item.getLive_publisher());

        // 썸네일이미지 세팅
//        Glide.with(context).load(ServerUri + "/uploads/LiveThumbnail_" + item.getLive_ID() + ".jpg")
//                .transform( new RotateTransformation( context, 90f ))
//                //캐시저장하지 않음
////                        .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
////                        .skipMemoryCache(true)// 메모리 캐시 저장 off
////                .error(R.drawable.user3) //이미지 로드에 실패했을 때 등, 예상하지 못한 상황으로 원본이미지를 로드할 수 없을 때 보여주는 이미지이다.
////                .bitmapTransform(new CropCircleTransformation(context))
//                .into(holder.iv_live_preview);
//        String[] strarr = item.getProject_Users().split("[|]");
//        for(int i =0;i<strarr.length;++i){
//            Log.e("strarr "+i,strarr[i]+"\n");
//        }

//        String user1 = strarr[0]; // 첫번째 유저
//        //프로필이미지 세팅
//        Glide.with(context).load(ServerUri+"/uploads/"+user1+".jpg")
//                //캐시저장하지 않음
//                .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
//                .skipMemoryCache(true)// 메모리 캐시 저장 off
//                .placeholder(R.drawable.user3)//원본이미지를 보여주기 전에 잠깐 보여주는 이미지
//                //이미지 로드에 실패했을 때 등, 예상하지 못한 상황으로 원본이미지를 로드할 수 없을 때 보여주는 이미지이다.
//                .error(R.drawable.user3)
//                .bitmapTransform(new CropCircleTransformation(context))
//                .into(holder.iv_projectimage);


        //holder.GoodsImage.setImageURI(item.getName());


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.removeAll(items);
        notifyDataSetChanged();
    }
    //  home home;

    //프로젝트 클릭이벤트
    @Override
    public void onListItemClick(int position) {


//        Toast.makeText(context,"상품명 : "+items.get(position).getName()+"     " +
//                "position : "+position,Toast.LENGTH_SHORT).show();
        Log.d("onListItemClick", "클릭" + items.get(position).getLive_Title());
        //VLCstrreaming 로 데이터 전달


        Intent intent = new Intent(context, VLCstrreaming.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//다음 액티비티를 스택에 쌓지 않는다
        intent.putExtra("livetitle", items.get(position).getLive_Title());
        intent.putExtra("liveid", items.get(position).getLive_ID());
        intent.putExtra("livepublisher", items.get(position).getLive_publisher());
        intent.putExtra("liveusersize", items.get(position).getLive_Usersize());
        context.startActivity(intent);
    }

    String str, result;
    Live_item item;
    SQLite_RoomList RoomList_DB;
//    public void http() { //http로 Json데이터전송
//        //  Log.v(TAG, value);
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.accumulate("userid", MyID);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        str = jsonObject.toString();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                POST post = new POST();
//                result = post.POST("http://jpcsm9003.vps.phps.kr/db/SELECT_project.php", str);
//                Log.e("result", result +"");
//                //Toast.makeText(getContext(),"result : "+result,Toast.LENGTH_SHORT).show();
//
//
//                if (result != null) {
//                    //상품정보 어댑터에 추가
//                    try {
//                        JSONArray jsonArray = new JSONArray(result);
//
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            item = new Live_item();
//
//                            JSONObject jObject = jsonArray.getJSONObject(i);  // JSONObject 추출
//                            String projectid = jObject.getString("projectid");
//                            String projectname = jObject.getString("projectname");
//                            String projectimage = jObject.getString("projectimage");
//                            String projectusers = jObject.getString("projectusers");
//                            String create_time = jObject.getString("create_time");
//                            String adminuser = jObject.getString("adminuser");
//                            //Log.d("Name",Name);
//                            item.setLive_Title(projectname);
//                            item.setLive_publisher(projectusers);
//                            item.setLive_ID(projectid);
//
//                            //DB에 저장( projectid ,projectname ,adminuser ,projectusers,String create_time)
//                            RoomList_DB.insert_project(projectid, projectname, adminuser, projectusers, create_time);
//                            Log.e("Project_Fragment", "onResume : " + projectid + " | " + projectname + " | " +
//                                    adminuser + " | " + projectusers + " | " + create_time);
////                            getActivity().runOnUiThread(new Runnable() { public void run() {
////                                /// / 메시지 큐에 저장될 메시지의 내용
////                                project_adapter.add(item);
////                            } });
//
//
//                            //Log.d("상품목록스레드결과",result);
//                        }
////                        Message msg = new Message();
////                        msg.what = 2;
////                        mProjectHandler.handleMessage(msg);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    //서버연결실패시 예외처리
//                    //Toast.makeText(getApplicationContext(), "서버연결에 실패하였습니다"+result.toString(), Toast.LENGTH_SHORT).show();
//                    Log.d("서버연결실패", result);
//                }
//            }
//        }).start();
//    }
}

class Live_item {

    private String Live_Title; //
    private int Live_Usersize;//
    private String Live_Users;//
    private String Live_publisher;//
    private String Live_ID; //

    public String getLive_Title() {
        return this.Live_Title;
    }

    public int getLive_Usersize() {
        return this.Live_Usersize;
    }

    public String getLive_Users() {
        return this.Live_Users;
    }

    public String getLive_publisher() {
        return this.Live_publisher;
    }

    public String getLive_ID() {
        return this.Live_ID;
    }

    public void setLive_Title(String Live_Title) {
        this.Live_Title = Live_Title;
    }

    public void setLive_Usersize(int Live_Usersize) {
        this.Live_Usersize = Live_Usersize;
    }

    public void setLive_Users(String Live_Users) {
        this.Live_Users = Live_Users;
    }

    public void setLive_publisher(String Live_publisher) {
        this.Live_publisher = Live_publisher;
    }

    public void setLive_ID(String Live_ID) {
        this.Live_ID = Live_ID;
    }

}


