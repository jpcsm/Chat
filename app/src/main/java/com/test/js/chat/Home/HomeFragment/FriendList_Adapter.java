package com.test.js.chat.Home.HomeFragment;

/**
 * Created by lenovo on 2017-06-29.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.js.chat.GotoChattingRoomActivity;
import com.test.js.chat.MyProfileSettingActivity;
import com.test.js.chat.R;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by lenovo on 2017-06-28.
 */
class FriendList_ViewHolder extends RecyclerView.ViewHolder {


    public TextView tv_friendname, tv_Myname;
    public CheckBox Cb_addfriend;
    public LinearLayout LL_myprofile, LL_freindprofile;
    public ImageView IV_friendprofile, IV_Myprofile;

    public FriendList_ViewHolder(View itemView) {
        super(itemView);

        tv_friendname = (TextView) itemView.findViewById(R.id.tv_friendname);
        tv_Myname = (TextView) itemView.findViewById(R.id.tv_Myname);
        Cb_addfriend = (CheckBox) itemView.findViewById(R.id.Cb_addfriend);
        LL_myprofile = (LinearLayout) itemView.findViewById(R.id.LL_myprofile);
        LL_freindprofile = (LinearLayout) itemView.findViewById(R.id.LL_freindprofile);
        IV_friendprofile = (ImageView) itemView.findViewById(R.id.IV_friendprofile);
        IV_Myprofile = (ImageView) itemView.findViewById(R.id.IV_MyProflie);
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


public class FriendList_Adapter extends RecyclerView.Adapter<FriendList_ViewHolder> implements FriendList_ViewHolder.OnListItemClickListener {
    Context context;

    public FriendList_Adapter(Context context) {
        this.context = context;
    }


    @Override
    public FriendList_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendlist_item, parent, false);
        FriendList_ViewHolder holder = new FriendList_ViewHolder(v);
        holder.setOnListItemClickListener(this);
        return holder;

    }

    //어댑터아이템 추가후 데이터 반영
    List<FriendList_item> items = new ArrayList<>(); //채팅정보 리스트객체 생성

    public void add(FriendList_item data) {
        items.add(data);
        notifyDataSetChanged();// 변경데이터 뷰에 반영
    }

    String ServerUri = "http://jpcsm9003.vps.phps.kr";

    @Override
    public void onBindViewHolder(FriendList_ViewHolder holder, int position) {


        //뷰홀더를 데이터와 바인딩 , 데이터를 뷰에 그려준다
        FriendList_item item = items.get(position);

        //사용자와 채팅내용을 리사이클뷰에 그려준다
        holder.tv_friendname.setText(item.getName());
        holder.Cb_addfriend.setChecked(item.getCheck());
        if (item.getCheckBox_GONE()) { // 체크박스보이지않기가 true이면 체크박스 gone
            if (item.getMyName() != null) { //내프로필
                //첫번째에만 내 프로필 보이기
                holder.LL_myprofile.setVisibility(View.VISIBLE);
                holder.LL_freindprofile.setVisibility(View.GONE);
                // 프로필이미지 세팅
                Glide.with(context).load(ServerUri + "/uploads/" + item.getMyName() + ".jpg")
                        //캐시저장하지 않음
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
//                        .skipMemoryCache(true)// 메모리 캐시 저장 off
                        .error(R.drawable.user3) //이미지 로드에 실패했을 때 등, 예상하지 못한 상황으로 원본이미지를 로드할 수 없을 때 보여주는 이미지이다.
//                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(holder.IV_Myprofile);
                holder.tv_Myname.setText(item.getMyName());

            } else { //두번째 부터는 친구프로필
                holder.LL_myprofile.setVisibility(View.GONE);//내 프로필 보이지 않기
                holder.LL_freindprofile.setVisibility(View.VISIBLE);
//                if(item.getProfileImageURI()!=null){
                //holder.IV_friendprofile.setImageURI(item.getProfileImageURI());
                //프로필이미지 세팅
                Glide.with(context).load(ServerUri + "/uploads/" + item.getName() + ".jpg")
                        //캐시저장하지 않음
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
//                        .skipMemoryCache(true)// 메모리 캐시 저장 off
                        //.placeholder(R.drawable.user3)//원본이미지를 보여주기 전에 잠깐 보여주는 이미지
                        //이미지 로드에 실패했을 때 등, 예상하지 못한 상황으로 원본이미지를 로드할 수 없을 때 보여주는 이미지이다.
                        .error(R.drawable.user3)
//                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(holder.IV_friendprofile);
//                }
            }
            holder.Cb_addfriend.setVisibility(View.GONE);
        }else{//대화상대 초대 액티비티
            holder.LL_myprofile.setVisibility(View.GONE);//내 프로필 보이지 않기
            holder.LL_freindprofile.setVisibility(View.VISIBLE);
//                if(item.getProfileImageURI()!=null){
            //holder.IV_friendprofile.setImageURI(item.getProfileImageURI());
            //프로필이미지 세팅
            Glide.with(context).load(ServerUri + "/uploads/" + item.getName() + ".jpg")
                    //캐시저장하지 않음
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
//                    .skipMemoryCache(true)// 메모리 캐시 저장 off
                    .placeholder(R.drawable.user3)//원본이미지를 보여주기 전에 잠깐 보여주는 이미지
                    //이미지 로드에 실패했을 때 등, 예상하지 못한 상황으로 원본이미지를 로드할 수 없을 때 보여주는 이미지이다.
                    .error(R.drawable.user3)
//                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.IV_friendprofile);
        }


    }

    //숫자 세자리마다 콤마
//    public static String Comma_won(String junsu) {
//        int inValues = Integer.parseInt(junsu);
//        DecimalFormat Commas = new DecimalFormat("#,###");
//        String result_int = (String)Commas.format(inValues);
//        return result_int;
//    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.removeAll(items);
        notifyDataSetChanged();
    }

    int UserCount = 0;

    //리스트아이템 클릭이벤트
    @Override
    public void onListItemClick(int position) {


        if (items.get(position).getCheckBox_GONE()) { //친구목록 프래그먼트 - 체크박스보이지 않음 true
            if (position == 0) { //내프로필일 때
                // 내 프로필관리 액티비티로 이동
                Intent intent = new Intent(context, MyProfileSettingActivity.class); //채팅액티비티로 이동
//            intent.putExtra("roomname",items.get(position).getName());
//            intent.putExtra("roomstate","1:1");
                context.startActivity(intent);
            }else { // 친구프로필 일 때
                //1대1 채팅액티비티로 이동한다
                Intent intent = new Intent(context, GotoChattingRoomActivity.class); //채팅액티비티로 이동
                intent.putExtra("roomname", items.get(position).getName());
                intent.putExtra("roomstate", "1:1");
                context.startActivity(intent);
                Log.d("onListItemClick", "클릭" + items.get(position).getName());
            }


        } else {// 친구초대액티비티 - 체크박스 보이기

            if (items.get(position).getCheck()) { //친구추가 체크시 누르면 선택해제
                items.get(position).setCheck(false);
                UserCount--;

            } else {//선택해제 상태에서 클릭시 체크
                items.get(position).setCheck(true);
                UserCount++;
            }
            Log.d("onListItemClick", "클릭" + items.get(position).getName() + "/ UserCount : " + UserCount);

        }


        notifyDataSetChanged();
    }

    public int getCheckCount() {
        return this.UserCount;
    }
}


class FriendList_item {
    private int CHAT_MODE = 1; //채팅모드
    private int SENDIMAGE_MODE = 2;
    private int SENDFILE_MODE = 3;

    private String MyName; //내이름
    private String name; //친구이름
    private Boolean check; //체크여부 true, false
    private Uri ProfileImageURI;
    private Boolean CheckBox_GONE = false; //체크박스 보이지 않기 true, false    기본값(false) : 보이기

    public String getName() {
        return this.name;
    }

    public Uri getProfileImageURI() {
        return this.ProfileImageURI;
    }

    public String getMyName() {
        return this.MyName;
    }

    public Boolean getCheck() {
        return this.check;
    }

    public Boolean getCheckBox_GONE() {
        return this.CheckBox_GONE;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMyName(String MyName) {
        this.MyName = MyName;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public void setProfileImageURI(Uri ProfileImageURI) {
        this.ProfileImageURI = ProfileImageURI;
    }

    public void setCheckBox_GONE(boolean CheckBox_GONE) {
        this.CheckBox_GONE = CheckBox_GONE;
    }
}
