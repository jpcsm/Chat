package com.test.js.chat;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by lenovo on 2017-08-01.
 */

class Project_ViewHolder extends RecyclerView.ViewHolder {



    public TextView tv_projectName, tv_ProjectUsers, tv_admin;
    public ImageView iv_projectimage;
    public Project_ViewHolder(View itemView) {
        super(itemView);

        tv_projectName = (TextView)itemView.findViewById(R.id.tv_projectName);
        tv_ProjectUsers = (TextView)itemView.findViewById(R.id.tv_projectUsers);
        iv_projectimage = (ImageView) itemView.findViewById(R.id.iv_projectImage);
        tv_admin = (TextView)itemView.findViewById(R.id.tv_admin);
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
public class Project_Adapter extends RecyclerView.Adapter<Project_ViewHolder> implements Project_ViewHolder.OnListItemClickListener{
    Context context;
    public Project_Adapter(Context context) {
        this.context = context;
    }


    @Override
    public Project_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_cardview,parent,false);
        Project_ViewHolder holder = new Project_ViewHolder(v);
        holder.setOnListItemClickListener(this);
        return holder;

    }

    //어댑터아이템 추가후 데이터 반영
    List<project_item> items = new ArrayList<>(); //채팅정보 리스트객체 생성
    public void add(project_item data){
        items.add(data);
        notifyDataSetChanged();// 변경데이터 뷰에 반영
    }

    String ServerUri = "http://jpcsm9003.vps.phps.kr";
    @Override
    public void onBindViewHolder(Project_ViewHolder holder, int position) {
        //뷰홀더를 데이터와 바인딩 , 데이터를 뷰에 그려준다
        project_item item = items.get(position);

        //사용자와 채팅내용을 리사이클뷰에 그려준다
        holder.tv_projectName.setText(item.getProject_Name());
        holder.tv_ProjectUsers.setText(item.getProject_Users());
        holder.tv_admin.setText(item.getadmin());
        // 프로젝트 관리자 이미지세팅
        Glide.with(context).load(ServerUri + "/uploads/" + item.getadmin() + ".jpg")
                //캐시저장하지 않음
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
//                        .skipMemoryCache(true)// 메모리 캐시 저장 off
                .error(R.drawable.user3) //이미지 로드에 실패했을 때 등, 예상하지 못한 상황으로 원본이미지를 로드할 수 없을 때 보여주는 이미지이다.
//                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.iv_projectimage);

//        String[] strarr = item.getProject_Users().split("[|]");
////        for(int i =0;i<strarr.length;++i){
////            Log.e("strarr "+i,strarr[i]+"\n");
////        }
//
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
//        holder.tv_roomsize.setText(item.getRoom_size());
//        holder.GoodsPrice.setText(Comma_won(item.getPrice()+"")+" P");
//        holder.GoodsBrand.setText(item.getBrand());


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
        Log.d("onListItemClick","클릭"+items.get(position).getProject_Name());
        //프로젝트로 정보전달
        Intent intent = new Intent(context,Project_Timeline_Activity.class); //채팅액티비티로 이동
        intent.putExtra("projectname",items.get(position).getProject_Name());
        intent.putExtra("projectid",items.get(position).getProjectID());
        intent.putExtra("projectusers",items.get(position).getProject_Users());
        intent.putExtra("projectusersize",items.get(position).getProject_Usersize());
        context.startActivity(intent);
    }
}


