package com.test.js.chat;

/**
 * Created by lenovo on 2017-06-27.
 */

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ChatViewHolder extends RecyclerView.ViewHolder {


    public TextView Name, tv_me_image_readNotUserSize, User_chat, tv_me_chat_time, Mychat, tv_me_chat_readNotUserSize,
            tv_me_image_time,tv_you_chat_readNotUserSize,tv_you_chat_time,tv_you_image_readNotUserSize,tv_you_image_time;
    LinearLayout OpponentChat_layout,LL_me_chatlayout,LL_me_imagelayout,LL_you_chatlayout,LL_you_imagelayout;
    LinearLayout MyChat_layout;
    public ImageView IV_chatProfile;
    public  ImageButton reciveImage,sendImage;
    public ChatViewHolder(View itemView) {
        super(itemView);

        //나
        MyChat_layout = (LinearLayout) itemView.findViewById(R.id.LL_MyChatting);
        Mychat = (TextView) itemView.findViewById(R.id.Mychat);
        sendImage = (ImageButton)itemView.findViewById(R.id.sendImage);
        tv_me_chat_readNotUserSize = (TextView) itemView.findViewById(R.id.tv_me_chat_readNotUserSize);
        tv_me_chat_time = (TextView) itemView.findViewById(R.id.tv_me_chat_time);
        tv_me_image_readNotUserSize = (TextView) itemView.findViewById(R.id.tv_me_image_readNotUserSize);
        tv_me_image_time = (TextView) itemView.findViewById(R.id.tv_me_image_time);
        LL_me_chatlayout = (LinearLayout) itemView.findViewById(R.id.LL_me_chatlayout);
        LL_me_imagelayout = (LinearLayout) itemView.findViewById(R.id.LL_me_imagelayout);

        //상대방
        IV_chatProfile = (ImageView) itemView.findViewById(R.id.IV_chatProfile);
        OpponentChat_layout = (LinearLayout) itemView.findViewById(R.id.LL_ChattingOpponent);
        Name = (TextView) itemView.findViewById(R.id.name);
        User_chat = (TextView) itemView.findViewById(R.id.chat);
        reciveImage = (ImageButton)itemView.findViewById(R.id.reciveImage);
        tv_you_chat_readNotUserSize = (TextView) itemView.findViewById(R.id.tv_you_chat_readNotUserSize);
        tv_you_chat_time = (TextView) itemView.findViewById(R.id.tv_you_chat_time);
        tv_you_image_readNotUserSize = (TextView) itemView.findViewById(R.id.tv_you_image_readNotUserSize);
        tv_you_image_time = (TextView) itemView.findViewById(R.id.tv_you_image_time);
        LL_you_chatlayout = (LinearLayout) itemView.findViewById(R.id.LL_you_chatlayout);
        LL_you_imagelayout = (LinearLayout) itemView.findViewById(R.id.LL_you_imagelayout);

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


class chatAdapter extends RecyclerView.Adapter<ChatViewHolder> implements ChatViewHolder.OnListItemClickListener {
    Context context;

    public chatAdapter(Context context) {
        this.context = context;
    }


    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        ChatViewHolder holder = new ChatViewHolder(v);
        holder.setOnListItemClickListener(this);
        return holder;

    }

    //어댑터아이템 추가후 데이터 반영
    List<chat_item> items = new ArrayList<>(); //채팅정보 리스트객체 생성

    public void add(chat_item data) {
        items.add(data);
        notifyDataSetChanged();// 변경데이터 뷰에 반영
    }


    String ServerUri = "http://jpcsm9003.vps.phps.kr";
    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        //뷰홀더를 데이터와 바인딩 , 데이터를 뷰에 그려준다
        chat_item item = items.get(position);


        //사용자와 채팅내용을 리사이클뷰에 그려준다
//        if (item.getProfileImageURI() != null) { //프로필 이미지
//            Glide.with(context).load(item.getProfileImageURI())
//                    //캐시저장하지 않음
////                    .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
////                    .skipMemoryCache(true)// 메모리 캐시 저장 off
//                    .bitmapTransform(new BlurTransformation(context))
//                    .into(holder.IV_chatProfile);
//        }
        if (item.getIsME()) { //내가보낸메세지일 경우


            //문자일경우
            holder.LL_me_chatlayout.setVisibility(View.VISIBLE);
            holder.Mychat.setText(item.getUser_chat());
            holder.MyChat_layout.setVisibility(View.VISIBLE); // 대화내용 우측정렬
            holder.OpponentChat_layout.setVisibility(View.GONE); // 대화내용 우측정렬
            holder.tv_me_chat_readNotUserSize.setText(item.getreadNotUserSize());
            holder.tv_me_chat_time.setText(item.gettime());
            holder.LL_me_imagelayout.setVisibility(View.GONE);
            if(item.getType()==2){ //이미지전송 일경우
                holder.LL_me_imagelayout.setVisibility(View.VISIBLE);
                holder.sendImage.setVisibility(View.VISIBLE);
                //전송할 이미지 세팅
                Glide.with(context).load(ServerUri+"/uploads/"+item.getimageName()+".jpg")
                        .override(700,700)
                        //캐시저장하지 않음
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
//                    .skipMemoryCache(true)// 메모리 캐시 저장 off
                        //.placeholder(R.drawable.user3)//원본이미지를 보여주기 전에 잠깐 보여주는 이미지
//                        .error(R.drawable.user3)//이미지 로드에 실패했을 때 등, 예상하지 못한 상황으로 원본이미지를 로드할 수 없을 때 보여주는 이미지이다.
//                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(holder.sendImage);
                holder.tv_me_image_readNotUserSize.setText(item.getreadNotUserSize());
                holder.tv_me_image_time.setText(item.gettime());
                holder.LL_me_chatlayout.setVisibility(View.GONE);
                Log.e("item.getType() ",item.getType()+" / item.getimageName() : "+item.getimageName());
            }





        } else { //상대방이 보낸메세지일 경우

            //문자일경우
            holder.LL_you_chatlayout.setVisibility(View.VISIBLE);
            holder.Name.setText(item.getName()); //상대방 아이디
            holder.User_chat.setText(item.getUser_chat());
            holder.MyChat_layout.setVisibility(View.GONE); // 대화내용 좌측정렬
            holder.OpponentChat_layout.setVisibility(View.VISIBLE); // 대화내용 우측정렬
            holder.tv_you_chat_readNotUserSize.setText(item.getreadNotUserSize());
            holder.tv_you_chat_time.setText(item.gettime());
            holder.LL_you_imagelayout.setVisibility(View.GONE);
            //프로필이미지 세팅
            Glide.with(context).load(ServerUri+"/uploads/"+item.getName()+".jpg")
                    //캐시저장하지 않음
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
//                    .skipMemoryCache(true)// 메모리 캐시 저장 off
                    //.placeholder(R.drawable.user3)//원본이미지를 보여주기 전에 잠깐 보여주는 이미지
                    .error(R.drawable.user3)//이미지 로드에 실패했을 때 등, 예상하지 못한 상황으로 원본이미지를 로드할 수 없을 때 보여주는 이미지이다.
//                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.IV_chatProfile);




            if(item.getType()==2){ //이미지전송 일경우
                holder.LL_you_imagelayout.setVisibility(View.VISIBLE);
                holder.reciveImage.setVisibility(View.VISIBLE);
                //전송할 이미지 세팅
                Glide.with(context).load(ServerUri+"/uploads/"+item.getimageName()+".jpg")
                        .override(700,700)
                        //캐시저장하지 않음
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
//                    .skipMemoryCache(true)// 메모리 캐시 저장 off
                        //.placeholder(R.drawable.user3)//원본이미지를 보여주기 전에 잠깐 보여주는 이미지
//                        .error(R.drawable.user3)//이미지 로드에 실패했을 때 등, 예상하지 못한 상황으로 원본이미지를 로드할 수 없을 때 보여주는 이미지이다.
//                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(holder.reciveImage);
                holder.tv_you_image_readNotUserSize.setText(item.getreadNotUserSize());
                holder.tv_you_image_time.setText(item.gettime());
                holder.LL_you_chatlayout.setVisibility(View.GONE);
            }

        }
//        holder.GoodsPrice.setText(Comma_won(item.getPrice()+"")+" P");
//        holder.GoodsBrand.setText(item.getBrand());

        // Glide.with(context).load(Server.localhost+"/img/"+item.getImage()+".jpg").into(holder.GoodsImage);

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

    //리스트아이템 클릭이벤트
    @Override
    public void onListItemClick(int position) {

//        Toast.makeText(context,"상품명 : "+items.get(position).getName()+"     " +
//                "position : "+position,Toast.LENGTH_SHORT).show();
        Log.d("onListItemClick", "클릭" + items.get(position).getName());
        //상세페이지(구매)로 상품정보전달
//        Intent intent = new Intent(context,CouponDetailActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("name",items.get(position).getName());
//        intent.putExtra("price",items.get(position).getPrice());
//        //intent.putExtra("category",items.get(position).getName());
//        intent.putExtra("image",items.get(position).getImage());
//        intent.putExtra("brand",items.get(position).getBrand());
//        intent.putExtra("couponnum",items.get(position).getCouponnum());
//        intent.putExtra("validity",items.get(position).getValidity());
//        Log.d("인텐트생성","완료");
//        context.startActivity(intent);
    }
}

//class coupon_item {
//    private String name, brand,image, couponnum,validity;
//    private int price;
//
//    public String getBrand() {
//        return brand;
//    }
//    public String getName() {
//        return name;
//    }
//    public int getPrice() {
//        return price;
//    }
//    public String getImage() {
//        return image;
//    }
//    public String getCouponnum() {
//        return couponnum;
//    }
//    public String getValidity() { return validity; }
//
//    public void setBrand(String brand) {
//        this.brand = brand;
//    }
//    public void setName(String name) {
//        this.name = name;
//    }
//    public void setPrice(int price) {
//        this.price = price;
//    }
//    public void setImage(String image) {
//        this.image = image;
//    }
//    public void setCouponnum(String couponnum) {
//        this.couponnum = couponnum;
//    }
//    public void setValidity(String validity) { this.validity = validity; }
//}

class chat_item {
    private int CHAT_MODE = 1; //채팅모드
    private int SENDIMAGE_MODE = 2;
    private int SENDFILE_MODE = 3;

    //사용자 정보
    private String name; //사용자명
    private String user_chat; // 사용자 대화내용
    private boolean isME; // 내가 보낸메세지인지 상대방이 보낸메세지인지
    private int Type ; // 2 - 이미지
    private Uri ProfileImageURI;  // 프로필이미지
    private Uri imageUri; // 전송이미지
    private String time; //메제시 작성 시간
    private String readNotUserSize; //메세지 안읽은 사람수
    public String gettime() {
        return this.time;
    }
    public void settime(String time) {
        this.time = time;
    }
    public String getreadNotUserSize() {
        return this.readNotUserSize;
    }
    public void setreadNotUserSize(String readNotUserSize) {
        this.readNotUserSize = readNotUserSize;
    }

    public String getName() {
        return this.name;
    }

    public String getUser_chat() {
        return this.user_chat;
    }
    public int getType() {
        return this.Type;
    }
    public boolean getIsME() {
        return this.isME;
    }

    public void setUser_chat(String user_chat) {
        this.user_chat = user_chat;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setType(int Type) {
        this.Type = Type;
    }
    public void set_isMe(Boolean isME) {
        this.isME = isME;
    }

    public Uri getimageUri() {
        return this.imageUri;
    }

    public void setimageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getProfileImageURI() {
        return this.ProfileImageURI;
    }

    public void setProfileImageURI(Uri ProfileImageURI) {
        this.ProfileImageURI = ProfileImageURI;
    }


    private String imageName;  // 전송이미지 이름
    public String getimageName() {
        return this.imageName;
    }

    public void setimageName(String imageName) {
        this.imageName = imageName;
    }
}