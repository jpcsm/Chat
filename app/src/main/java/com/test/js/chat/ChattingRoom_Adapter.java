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
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by lenovo on 2017-06-28.
 */
class ChattingRoom_ViewHolder extends RecyclerView.ViewHolder {



    public TextView tv_room, tv_userSize,tv_date,tv_NewMsgSize,tv_lastmsg ;
    public ImageView IV_RoomImage;
    public ChattingRoom_ViewHolder(View itemView) {
        super(itemView);

        tv_room = (TextView)itemView.findViewById(R.id.tv_room);
        tv_userSize = (TextView)itemView.findViewById(R.id.tv_userSize);
        tv_lastmsg = (TextView)itemView.findViewById(R.id.tv_lastmsg);
        tv_date = (TextView)itemView.findViewById(R.id.tv_date);
        tv_NewMsgSize = (TextView)itemView.findViewById(R.id.tv_NewMsgSize);
        IV_RoomImage = (ImageView) itemView.findViewById(R.id.IV_RoomImage);

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


public class ChattingRoom_Adapter extends RecyclerView.Adapter<ChattingRoom_ViewHolder> implements ChattingRoom_ViewHolder.OnListItemClickListener{
    Context context;
    public ChattingRoom_Adapter(Context context) {
        this.context = context;
    }


    @Override
    public ChattingRoom_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chattingroom_row,parent,false);
        ChattingRoom_ViewHolder holder = new ChattingRoom_ViewHolder(v);
        holder.setOnListItemClickListener(this);
        return holder;

    }

    //어댑터아이템 추가후 데이터 반영
    List<chattingroom_item> items = new ArrayList<>(); //채팅정보 리스트객체 생성
    public void add(chattingroom_item data){
        items.add(data);
        notifyDataSetChanged();// 변경데이터 뷰에 반영
    }

    String ServerUri = "http://jpcsm9003.vps.phps.kr";
    @Override
    public void onBindViewHolder(ChattingRoom_ViewHolder holder, int position) {
        //뷰홀더를 데이터와 바인딩 , 데이터를 뷰에 그려준다
        chattingroom_item item = items.get(position);

        //사용자와 채팅내용을 리사이클뷰에 그려준다
        holder.tv_room.setText(item.getRoom());
        String[] strarr = item.getRoom().split("[|]");


        holder.tv_userSize.setText(item.getRoom_size());
        holder.tv_date.setText(item.getDate());
        holder.tv_lastmsg.setText(item.getlastMsg());
        holder.tv_NewMsgSize.setText(item.getNewMsgSize());




//        for(int i =0;i<strarr.length;++i){
//            Log.e("strarr "+i,strarr[i]+"\n");
//        }

        String user1 = strarr[0].split(",")[0]; // 첫번째 유저
        Log.e("채팅방프로필",""+user1);
        //프로필이미지 세팅
        Glide.with(context).load(ServerUri+"/uploads/"+user1+".jpg")
                //캐시저장하지 않음
                .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
                .skipMemoryCache(true)// 메모리 캐시 저장 off
                .placeholder(R.drawable.user3)//원본이미지를 보여주기 전에 잠깐 보여주는 이미지
                //이미지 로드에 실패했을 때 등, 예상하지 못한 상황으로 원본이미지를 로드할 수 없을 때 보여주는 이미지이다.
                .error(R.drawable.user3)
//                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.IV_RoomImage);
//        holder.tv_roomsize.setText(item.getRoom_size());
//        holder.GoodsPrice.setText(Comma_won(item.getPrice()+"")+" P");
//        holder.GoodsBrand.setText(item.getBrand());


        //holder.GoodsImage.setImageURI(item.getName());


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
    //  home home;

    //채팅방 클릭이벤트
    @Override
    public void onListItemClick(int position) {

//        Toast.makeText(context,"상품명 : "+items.get(position).getName()+"     " +
//                "position : "+position,Toast.LENGTH_SHORT).show();
        Log.d("onListItemClick","클릭"+items.get(position).getRoom());
        //채팅방으로 방정보전달

        Intent intent = new Intent(context,GotoChattingRoomActivity.class); //채팅액티비티로 이동
        intent.putExtra("roomname",items.get(position).getRoom());
        intent.putExtra("roomstate","Exist");
        intent.putExtra("roomid",items.get(position).getRoomID()); //방ID
        context.startActivity(intent);
    }
}



class chattingroom_item {
    private int CHAT_MODE=1; //채팅모드
    private int SENDIMAGE_MODE=2;
    private int SENDFILE_MODE=3;

    private String room; //채팅방이름
    private String room_size;//채팅방 참여인원
    private String user;// 채팅방 참여유저 아이디
    private String roomID; //채팅방ID
    private String lastMsg; // 마지막 채팅내용
    private String NewMsgSize; // 읽지않은 메세지 수
    private String Date; //날짜
    public String getDate() {
        return this.Date;
    }
    public String getRoom() {
        return this.room;
    }
    public String getlastMsg() {
        return this.lastMsg;
    }

    public String getNewMsgSize() {
        return this.NewMsgSize;
    }

    public  String getRoom_size() {
        return this.room_size;
    }
    public String getUser(){
        return this.user;
    }
    public String getRoomID() {
        return this.roomID;
    }

    public void setDate(String Date){
        this.Date = Date;
    }
    public void setRoom(String room){
        this.room = room;
    }
    public void setRoom_size(String room_size){
        this.room_size = room_size;
    }
    public void setUser(String user){
        this.user = user;
    }
    public void setRoomID(String roomID){
        this.roomID = roomID;
    }
    public void setlastMsg(String lastMsg){
        this.lastMsg = lastMsg;
    }
    public void setNewMsgSize(String NewMsgSize){
        this.NewMsgSize = NewMsgSize;
    }
}